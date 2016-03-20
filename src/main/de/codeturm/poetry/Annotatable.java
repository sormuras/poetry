package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.List;

public interface Annotatable<T> {

  @SuppressWarnings("unchecked")
  default T addAnnotation(Annotation annotation) {
    getAnnotations().add(annotation);
    return (T) this;
  }

  default T addAnnotation(String name) {
    return addAnnotation(new Annotation(name));
  }

  List<Annotation> getAnnotations();

  default void printAnnotations(JavaPrinter printer, ElementType elementType) {
    for (Annotation annotation : getAnnotations()) {
      annotation.print(printer, elementType);
    }
  }

}
