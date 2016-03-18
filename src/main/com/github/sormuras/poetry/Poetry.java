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

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.Processor;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

/**
 * Java reflection, Javax model and JavaPoet related utilities.
 * 
 * @author Christian Stein
 */
public interface Poetry {

  /**
   * Create annotated type name instance from reflected annotated type.
   * 
   * @since 1.8
   * @param type
   *          annotated type to inspect
   * @return annotated type name
   */
  static TypeName annotated(AnnotatedType type) {
    AnnotatedType annotated = type;
    while (annotated instanceof AnnotatedArrayType) {
      annotated = ((AnnotatedArrayType) annotated).getAnnotatedGenericComponentType();
    }
    return TypeName.get(type.getType()).annotated(annotations(annotated.getAnnotations()));
  }

  /**
   * Create annotated type name instance from type mirror.
   * 
   * @since 1.8
   * @param mirror
   *          annotated type mirror to inspect
   * @return annotated type name
   */
  static TypeName annotated(TypeMirror mirror) {
    assert mirror instanceof AnnotatedConstruct;
    TypeMirror annotated = mirror;
    while (annotated.getKind() == TypeKind.ARRAY) {
      annotated = ((ArrayType) annotated).getComponentType();
    }
    return TypeName.get(mirror).annotated(annotations(annotated.getAnnotationMirrors()));
  }

  static AnnotationSpec annotation(Class<? extends Annotation> type, Object... values) {
    return annotation(ClassName.get(type), values);
  }

  static AnnotationSpec annotation(ClassName type, Object... values) {
    AnnotationSpec.Builder builder = AnnotationSpec.builder(type);
    for (Object value : values) {
      value(builder, "value", value);
    }
    return builder.build();
  }

  /**
   * Convert annotations to annotation spec list.
   *
   * @param annotations
   *          source collection
   * @return a list of annotation specs
   */
  static List<AnnotationSpec> annotations(Annotation... annotations) {
    return asList(annotations).stream().map(AnnotationSpec::get).collect(Collectors.toList());
  }

  /**
   * Convert annotation mirrors to annotation spec list.
   *
   * @param mirrors
   *          source collection
   * @return a list of annotation specs
   */
  static List<AnnotationSpec> annotations(List<? extends AnnotationMirror> mirrors) {
    return mirrors.stream().map(AnnotationSpec::get).collect(Collectors.toList());
  }

  static String binary(TypeName type) {
    return binary(type, new StringBuilder()).toString();
  }

  static StringBuilder binary(TypeName type, StringBuilder builder) {
    if (type instanceof TypeVariableName || type instanceof WildcardTypeName) {
      throw new UnsupportedOperationException(type.getClass() + " hasn't got a binary name!");
    }
    if (type instanceof ClassName) {
      ClassName className = (ClassName) type;
      builder.append(className.topLevelClassName());
      className.simpleNames().subList(1, className.simpleNames().size()).forEach(
          name -> builder.append('$').append(name));
      return builder;
    }
    if (type instanceof ArrayTypeName) {
      TypeName component = ((ArrayTypeName) type).componentType;
      builder.append('[');
      if (component.isPrimitive()) {
        // @formatter:off
        if (component.equals(TypeName.BOOLEAN)) return builder.append('Z');
        if (component.equals(TypeName.BYTE)) return builder.append('B');
        if (component.equals(TypeName.CHAR)) return builder.append('C');
        if (component.equals(TypeName.DOUBLE)) return builder.append('D');
        if (component.equals(TypeName.FLOAT)) return builder.append('F');
        if (component.equals(TypeName.INT)) return builder.append('I');
        if (component.equals(TypeName.LONG)) return builder.append('J');
        if (component.equals(TypeName.SHORT)) return builder.append('S');
        // @formatter:on
        throw new AssertionError(component + " is primitive and not handled?!");
      }
      builder.append(binary(component));
      if (component instanceof ClassName) {
        builder.append(';');
      }
      return builder;
    }
    if (type instanceof ParameterizedTypeName) {
      return builder.append(((ParameterizedTypeName) type).rawType);
    }
    return builder.append(type);
  }

  /**
   * Build method call statement string using its own parameter names.
   * <p>
   * Same as: {@code call(method, p -> p.name)}
   * 
   * @param method
   *          method to call
   * @return method call statement
   */
  static String call(MethodSpec method) {
    return call(method, p -> p.name);
  }

  /**
   * Build method call statement string using its own parameter names.
   * <p>
   * If parameter names are provided {@link String#indexOf(String, int)} yields: {@code "indexOf(str, fromIndex)"}.
   * 
   * @param method
   *          method to call
   * @param parameterName
   *          calculates name of the parameter
   * @return method call statement
   */
  static String call(MethodSpec method, Function<ParameterSpec, String> parameterName) {
    if (method.parameters.isEmpty())
      return method.name + "()";
    StringBuilder builder = new StringBuilder();
    builder.append(method.name);
    builder.append('(');
    for (ParameterSpec parameter : method.parameters) {
      if (builder.length() > method.name.length() + 1)
        builder.append(", ");
      builder.append(parameterName.apply(parameter));
    }
    builder.append(')');
    return builder.toString();
  }

  static Class<?> compile(JavaFile java) {
    ClassLoader parent = java.getClass().getClassLoader();
    List<String> options = Collections.emptyList();
    List<Processor> processors = Collections.emptyList();
    ClassLoader loader = compile(java, parent, options, processors);
    try {
      String name = java.packageName;
      name = name.isEmpty() ? java.typeSpec.name : name + "." + java.typeSpec.name;
      return loader.loadClass(name);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("loading class failed after successful compilation?!", e);
    }
  }

  static <T> T compile(JavaFile java, Class<T> clazz, Object... args) {
    try {
      Class<? extends T> subClass = compile(java).asSubclass(clazz);
      assert subClass.getDeclaredConstructors().length == 1 : "expected single constructor";
      Class<?>[] types = subClass.getDeclaredConstructors()[0].getParameterTypes();
      return (T) subClass.getConstructor(types).newInstance(args);
    } catch (Throwable t) {
      throw new IllegalStateException("compiling or instantiating failed", t);
    }
  }

  static <T> T compile(JavaFile java, Class<T> clazz, Callable<Class<?>[]> typesProvider,
      Object... args) {
    try {
      Class<? extends T> subClass = compile(java).asSubclass(clazz);
      return (T) subClass.getConstructor(typesProvider.call()).newInstance(args);
    } catch (Throwable t) {
      throw new IllegalStateException("compiling or instantiating failed", t);
    }
  }

  static ClassLoader compile(JavaFile java, Processor... processors) {
    return compile(java, java.getClass().getClassLoader(), Collections.emptyList(),
        Arrays.asList(processors));
  }

  static ClassLoader compile(JavaFile java, ClassLoader parent, List<String> options,
      List<Processor> processors) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    assert compiler != null : "no system java compiler available - JDK is required!";
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    StandardJavaFileManager sjfm = compiler.getStandardFileManager(
        diagnostics,
        Locale.getDefault(),
        StandardCharsets.UTF_8);
    Manager manager = new Manager(sjfm, parent);
    CompilationTask task = compiler.getTask(
        null, // a writer for additional output from the compiler; use System.err if null
        manager,
        diagnostics,
        options,
        null, // names of classes to be processed by annotation processing, null means no classes
        Collections.singleton(java.toJavaFileObject()));
    if (!processors.isEmpty())
      task.setProcessors(processors);
    boolean success = task.call();
    if (!success)
      throw new RuntimeException("compilation failed" + diagnostics.getDiagnostics());
    return manager.getClassLoader(null);
  }

  /**
   * Create type spec builder for the interface spec provided.
   *
   * @param interfaceSpec
   *          the interface to implement
   * @param name
   *          the class name
   * @return the class spec implementing all methods found in interfaceSpec
   */
  static TypeSpec.Builder implement(TypeSpec interfaceSpec, String interfacePackage, String name,
      Function<MethodSpec, CodeBlock> coder) {
    TypeSpec.Builder builder = TypeSpec.classBuilder(name)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ClassName.get(interfacePackage, interfaceSpec.name));
    interfaceSpec.methodSpecs.forEach(m -> builder.addMethod(override(m, coder).build()));
    return builder;
  }

  /**
   * Create method spec builder for the given method spec.
   *
   * @param method
   *          the method to override
   * @param coder
   *          the code block supplier
   * @return the method builder overriding the other method
   */
  static MethodSpec.Builder override(MethodSpec method, Function<MethodSpec, CodeBlock> coder) {
    if (method.isConstructor())
      throw new IllegalArgumentException("constructor not supported");
    MethodSpec.Builder builder = MethodSpec.methodBuilder(method.name)
        .addJavadoc(method.javadoc.toString())
        .addAnnotations(method.annotations)
        .addModifiers(Modifier.PUBLIC)
        .returns(method.returnType)
        .addTypeVariables(method.typeVariables)
        .addParameters(method.parameters)
        .addExceptions(method.exceptions);
    builder.addCode(coder.apply(method));
    return builder;
  }

  /**
   * Delegates to {@link builder#addMember(String, String, Object...)}, with parameter {@code format} depending on the given {@code value}
   * object.
   * 
   * Falls back to {@code "$L"} literal format if the class of the given {@code value} object is not supported.
   * 
   * @param builder
   *          add value to that builder
   * @param member
   *          member name, like {@code "value"}
   * @param value
   *          object to assign to member
   * @return the {@code builder} passed as first argument
   */
  static AnnotationSpec.Builder value(AnnotationSpec.Builder builder, String member, Object value) {
    if (value instanceof Class<?>) {
      return builder.addMember(member, "$T.class", value);
    }
    if (value instanceof Enum) {
      return builder.addMember(member, "$T.$L", value.getClass(), ((Enum<?>) value).name());
    }
    if (value instanceof String) {
      return builder.addMember(member, "$S", value);
    }
    if (value instanceof Float) {
      return builder.addMember(member, "$Lf", value);
    }
    return builder.addMember(member, "$L", value);
  }

}
