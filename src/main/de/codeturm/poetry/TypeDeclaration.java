package de.codeturm.poetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TypeDeclaration {

  public List<Annotation> annotations = new ArrayList<>();
  public final String keyword;
  public List<String> modifiers = new ArrayList<>();
  public String name;

  protected TypeDeclaration(String keyword, String name) {
    this.keyword = keyword;
    this.name = name;
  }

  public void print(JavaPrinter printer) {
    printDeclarationHead(printer);
    printer.append("{");
    printer.inc();
    // type body...
    printer.dec();
    printer.line("}");
  }

  public void printDeclarationHead(JavaPrinter printer) {
    if (!modifiers.isEmpty()) {
      printer.append(String.join(" ", modifiers));
    }
    printer.append("%s %s", keyword, name);
  }

  public TypeDeclaration setModifiers(String... modifiers) {
    Collections.addAll(this.modifiers, modifiers);
    return this;
  }

}
