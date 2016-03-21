package de.codeturm.poetry;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
      printer.add("<");
      ListIterator<TypeParameter> iterator = typeParameters.listIterator();
      while (iterator.hasNext()) {
        TypeParameter typeParameter = iterator.next();
        if (iterator.previousIndex() > 0) {
          printer.add(", ");
        }
        typeParameter.print(printer);        
      }
      printer.add(">");
    }
    // [Superclass]
    if (superClass != ClassType.OBJECT) {
      printer.add(" extends ");
      superClass.print(printer);
    }
    // [Superinterfaces]
  }
  
  public ClassDeclaration addTypeParameter(TypeParameter typeParameter) {
    typeParameters.add(typeParameter);
    return this;
  }

  public ClassDeclaration setSuperClass(ClassType superClass) {
    this.superClass = superClass;
    return this;
  }
}
