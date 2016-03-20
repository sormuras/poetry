package de.codeturm.poetry;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.codeturm.poetry.type.ClassType;

public class ClassDeclaration extends TypeDeclaration<ClassDeclaration> {

  public ClassType superClass = ClassType.OBJECT;
  public List<TypeParameter> typeParameters = new ArrayList<>();

  public ClassDeclaration(String name) {
    super("class", name);
  }

  @Override
  public void printDeclarationHead(JavaPrinter printer) {
    super.printDeclarationHead(printer);
    // [TypeParameters]
    if (!typeParameters.isEmpty()) {
      printer.inline("<");
      ListIterator<TypeParameter> iterator = typeParameters.listIterator();
      while (iterator.hasNext()) {
        TypeParameter typeParameter = iterator.next();
        if (iterator.previousIndex() > 0) {
          printer.inline(", ");
        }
        typeParameter.print(printer);        
      }
      printer.inline(">");
    }
    // [Superclass]
    if (superClass != ClassType.OBJECT) {
      printer.inline(" extends ");
      superClass.print(printer);
    }
    // [Superinterfaces]
  }

  public ClassDeclaration setSuperClass(ClassType superClass) {
    this.superClass = superClass;
    return this;
  }
}
