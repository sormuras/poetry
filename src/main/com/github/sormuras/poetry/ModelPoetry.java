/*
 * Copyright (C) 2015 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.sormuras.poetry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

/**
 * Provides utilities based on parsing elements and types from javax.lang.model instances.
 *
 * @author Christian Stein
 */
public class ModelPoetry {

  protected final Map<Class<?>, TypeElement> elemap;
  protected final Elements elements;
  protected final Types types;

  public ModelPoetry(ProcessingEnvironment processingEnvironment) {
    Objects.requireNonNull(processingEnvironment, "processingEnvironment == null");
    this.elements = processingEnvironment.getElementUtils();
    this.types = processingEnvironment.getTypeUtils();

    this.elemap = new HashMap<>();
    elemap.put(AutoCloseable.class, element(AutoCloseable.class));
    elemap.put(Object.class, element(Object.class));
    elemap.put(Override.class, element(Override.class));
  }

  /**
   * Create a type spec builder which copies from {@code element}.
   */
  public TypeSpec.Builder buildInterface(TypeElement element) {
    if (!element.getKind().isInterface()) {
      throw new IllegalArgumentException("only interfaces are supported, got " + element.getKind());
    }
    TypeSpec.Builder builder = TypeSpec.interfaceBuilder(element.getSimpleName().toString());
    builder.addAnnotations(Poetry.annotations(element.getAnnotationMirrors()));
    for (Modifier modifier : element.getModifiers()) {
      if (modifier == Modifier.ABSTRACT)
        continue;
      builder.addModifiers(modifier);
    }
    element.getInterfaces().forEach(t -> builder.addSuperinterface(TypeName.get(t)));
    element.getTypeParameters()
        .forEach(e -> builder.addTypeVariable(TypeVariableName.get((TypeVariable) e.asType())));
    List<ExecutableElement> methods = ElementFilter.methodsIn(elements.getAllMembers(element));
    methods.removeIf(m -> m.getEnclosingElement().equals(elemap.get(Object.class)));
    for (ExecutableElement method : methods) {
      builder.addMethod(buildMethod(method, (DeclaredType) element.asType()).build());
    }
    return builder;
  }

  /**
   * Create a method spec builder which copies {@code method} that is viewed as being a member of
   * the specified {@code containing} class or interface.
   *
   * This will copy its visibility modifiers, type parameters, return type, name, parameters, and
   * throws declarations.
   */
  public MethodSpec.Builder buildMethod(ExecutableElement method, DeclaredType containing) {
    Objects.requireNonNull(method, "method == null");
    Objects.requireNonNull(containing, "containing == null");
    ExecutableType executableType = (ExecutableType) types.asMemberOf(containing, method);
    String methodName = method.getSimpleName().toString();
    MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName);
    builder.addAnnotations(Poetry.annotations(method.getAnnotationMirrors()));
    builder.addModifiers(method.getModifiers());
    builder.returns(Poetry.annotated(executableType.getReturnType()));
    for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
      TypeVariable var = (TypeVariable) typeParameterElement.asType();
      builder.addTypeVariable(TypeVariableName.get(var));
    }
    List<? extends VariableElement> parameters = method.getParameters();
    List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
    for (int index = 0; index < parameters.size(); index++) {
      VariableElement parameter = parameters.get(index);
      TypeName type = TypeName.get(parameterTypes.get(index)); // annotations are grabbed later
      String name = parameter.getSimpleName().toString();
      Modifier[] paramods = new Modifier[parameter.getModifiers().size()];
      parameter.getModifiers().toArray(paramods);
      ParameterSpec.Builder psb = ParameterSpec.builder(type, name, paramods);
      for (AnnotationMirror mirror : parameter.getAnnotationMirrors()) {
        psb.addAnnotation(AnnotationSpec.get(mirror));
      }
      builder.addParameter(psb.build());
    }
    builder.varargs(method.isVarArgs());
    for (TypeMirror thrownType : method.getThrownTypes()) {
      builder.addException(TypeName.get(thrownType));
    }
    return builder;
  }

  protected TypeElement element(Class<?> type) {
    return elements.getTypeElement(type.getCanonicalName());
  }

}
