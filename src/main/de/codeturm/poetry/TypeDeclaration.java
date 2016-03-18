package de.codeturm.poetry;

public class TypeDeclaration {

  public enum Kind {

    ANNOTATION, CLASS, ENUM, INTERFACE;

    public String literal() {
      if (this == ANNOTATION) {
        return "@interface";
      }
      return name().toLowerCase();
    }

  }

  public Kind kind;
  public String name;

  public TypeDeclaration(String name, Kind kind) {
    this.name = name;
    this.kind = kind;
  }

  public void print(JavaPrinter printer) {
    printer.add("%s %s {", kind.literal(), name);
    printer.inc();
    // members...
    printer.dec();
    printer.add("}");
  }

}
