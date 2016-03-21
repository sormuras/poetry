package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

public class ArrayType implements ReferenceType {

  public static class Dim extends AnnotatedIdentifier {

    public Dim() {
      super("[]");
    }

  }

  public ClassType componentType;
  public List<Dim> dims = new ArrayList<>();

  public ArrayType(ClassType componentType, int dim) {
    this.componentType = componentType;
    for (int i = 0; i < dim; i++) {
      dims.add(new Dim());
    }
  }
  
  /**
   * Convenient for {@code getDims().get(index).addAnnotation(annotation)}
   */
  public ArrayType addAnnotation(int index, Annotation annotation) {
    dims.get(index).addAnnotation(annotation);
    return this;
  }

  public JavaPrinter print(JavaPrinter printer) {
    componentType.print(printer);
    for (Dim dim : dims) {
      dim.print(printer, ElementType.TYPE_USE);
    }
    return printer;
  }

  @Override
  public String toString() {
    return print(new JavaPrinter()).toString();
  }

}
