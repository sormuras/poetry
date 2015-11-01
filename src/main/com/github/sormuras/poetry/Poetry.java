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
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeName;

/**
 * Java reflection, Javax model and JavaPoet related utilities.
 * 
 * @author Christian Stein
 */
public interface Poetry {

  /**
   * Create annotated type name instance from reflected annotated type.
   * 
   * @param type annotated type to inspect
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
   * @param mirror annotated type mirror to inspect
   * @return annotated type name
   */
  static TypeName annotated(TypeMirror mirror) {
    TypeMirror annotated = mirror;
    while (annotated.getKind() == TypeKind.ARRAY) {
      annotated = ((ArrayType) annotated).getComponentType();
    }
    return TypeName.get(mirror).annotated(annotations(annotated.getAnnotationMirrors()));
  }

  /**
   * Convert annotations to annotation spec list.
   *
   * @param annotations source collection
   * @return a list of annotation specs
   */
  static List<AnnotationSpec> annotations(Annotation... annotations) {
    return asList(annotations).stream().map(AnnotationSpec::get).collect(Collectors.toList());
  }

  /**
   * Convert annotation mirrors to annotation spec list.
   *
   * @param mirrors source collection
   * @return a list of annotation specs
   */
  static List<AnnotationSpec> annotations(List<? extends AnnotationMirror> mirrors) {
    return mirrors.stream().map(AnnotationSpec::get).collect(Collectors.toList());
  }

}
