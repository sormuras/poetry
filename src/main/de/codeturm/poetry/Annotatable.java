package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.EnumSet;
import java.util.List;

public interface Annotatable<T> {

  final EnumSet<ElementType> newLineSet = EnumSet.of(ElementType.PACKAGE, ElementType.TYPE);

  @SuppressWarnings("unchecked")
  default T addAnnotation(Annotation annotation) {
    getAnnotations().add(annotation);
    return (T) this;
  }

  default T addAnnotation(String packageName, String... simpleNames) {
    return addAnnotation(new Annotation(packageName, simpleNames));
  }

  List<Annotation> getAnnotations();

  default void printAnnotations(JavaPrinter printer, ElementType elementType) {
    for (Annotation annotation : getAnnotations()) {
      annotation.print(printer, elementType);
      if (newLineSet.contains(elementType)) {
        printer.newline();
      }
    }
  }

}
