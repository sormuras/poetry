package de.codeturm.poetry;

public class Annotation {

  public enum Context {
    PackageModifier
  }

  public String name;

  public Annotation(String name) {
    this.name = name;
  }

  public void print(JavaPrinter printer, Context context) {
    printer.line("@%s", name);
  }

}
