package de.codeturm.poetry;

public class ClassDeclaration extends TypeDeclaration {

  public ClassDeclaration(String name) {
    super("class", name);
  }

  @Override
  public void printDeclarationHead(JavaPrinter printer) {
    super.printDeclarationHead(printer);
    // [TypeParameters]
    // [Superclass]
    // [Superinterfaces]
  }
}
