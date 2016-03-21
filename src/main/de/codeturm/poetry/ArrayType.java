package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

public class ArrayType implements ReferenceType {

  public ClassType componentType;
  public List<ArrayDimension> dimensions = new ArrayList<>();

  public ArrayType(ClassType componentType, int dim) {
    this.componentType = componentType;
    for (int i = 0; i < dim; i++) {
      dimensions.add(new ArrayDimension());
    }
  }
  
  /**
   * Convenient for {@code getDims().get(index).addAnnotation(annotation)}
   */
  public ArrayType addAnnotation(int index, Annotation annotation) {
    dimensions.get(index).addAnnotation(annotation);
    return this;
  }

  public JavaPrinter print(JavaPrinter printer) {
    componentType.print(printer);
    for (ArrayDimension dim : dimensions) {
      dim.print(printer, ElementType.TYPE_USE);
    }
    return printer;
  }

  @Override
  public String toString() {
    return print(new JavaPrinter()).toString();
  }

}
