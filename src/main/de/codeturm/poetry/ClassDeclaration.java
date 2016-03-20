package de.codeturm.poetry;

import de.codeturm.poetry.type.ClassType;

public class ClassDeclaration extends TypeDeclaration<ClassDeclaration> {
  
  public ClassType superClass = ClassType.OBJECT;

  public ClassDeclaration(String name) {
    super("class", name);
  }

  @Override
  public void printDeclarationHead(JavaPrinter printer) {
    super.printDeclarationHead(printer);
    // [TypeParameters]
    if (superClass != ClassType.OBJECT) {
      printer.inline("extends");
      superClass.print(printer); 
    }
    // [Superinterfaces]
  }

  public ClassDeclaration setSuperClass(ClassType superClass) {
    this.superClass = superClass;
    return this;
  }
}
