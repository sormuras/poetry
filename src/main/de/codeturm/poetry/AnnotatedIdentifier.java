package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code /Annotation/ Identifier}
 */
public abstract class AnnotatedIdentifier implements Annotatable<AnnotatedIdentifier> {

  public List<Annotation> annotations = new ArrayList<>();
  public String name = "";

  public AnnotatedIdentifier(String name) {
    this.name = name;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void print(JavaPrinter printer, ElementType annotationTarget) {
    printAnnotations(printer, annotationTarget);
    printer.add("%s", name);
  }

}
