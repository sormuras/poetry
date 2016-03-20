package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

public abstract class Annotatable<T> {

  public List<Annotation> annotations = new ArrayList<>();

  public T addAnnotation(String name) {
    return addAnnotation(new Annotation(name));
  }

  @SuppressWarnings("unchecked")
  public T addAnnotation(Annotation annotation) {
    annotations.add(annotation);
    return (T) this;
  }

  public void printAnnotations(JavaPrinter printer, ElementType elementType) {
    for (Annotation annotation : annotations) {
      annotation.print(printer, elementType);
    }
  }

}
