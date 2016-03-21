package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code /Annotation/ Identifier}
 */
public class AnnotatedIdentifier implements Annotatable<AnnotatedIdentifier> {

  public List<Annotation> annotations = new ArrayList<>();
  public String name = "";

  public AnnotatedIdentifier(String name) {
    this.name = name;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void print(JavaPrinter printer, ElementType elementType) {
    printAnnotations(printer, elementType);
    printer.add("%s", name);
  }

}
