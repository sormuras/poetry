package de.codeturm.poetry.type;

import java.lang.annotation.ElementType;

import de.codeturm.poetry.Annotatable;
import de.codeturm.poetry.JavaPrinter;

public class AnnotatedIdentifier extends Annotatable<AnnotatedIdentifier> {

  public String name = "";

  public AnnotatedIdentifier(String name) {
    this.name = name;
  }

  public void print(JavaPrinter printer, ElementType elementType) {
    printAnnotations(printer, elementType);
    printer.inline("%s", name);
  }

}
