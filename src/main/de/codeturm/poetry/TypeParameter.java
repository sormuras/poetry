package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class TypeParameter implements Annotatable<TypeParameter> {

  public List<Annotation> annotations = new ArrayList<>();
  public List<ClassType> bounds = new ArrayList<>();
  public TypeVariable boundTypeVariable = null;
  public String identifier = "";

  public TypeParameter(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Add (additional) bound to list of bounds and clears bound type variable.
   */
  public TypeParameter setBounds(ClassType... bounds) {
    this.boundTypeVariable = null;
    Collections.addAll(this.bounds, bounds);
    return this;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

  /**
   * {TypeParameterModifier} Identifier [TypeBound]
   */
  public void print(JavaPrinter printer) {
    // {TypeParameterModifier}
    printAnnotations(printer, ElementType.TYPE_PARAMETER);
    // Identifier
    printer.add(identifier);
    // [TypeBound]
    if (!(boundTypeVariable == null && bounds.isEmpty())) {
      printer.add(" extends ");
    }
    if (boundTypeVariable != null) {
      boundTypeVariable.print(printer, ElementType.TYPE_PARAMETER);
    } else {
      ListIterator<ClassType> iterator = bounds.listIterator();
      while (iterator.hasNext()) {
        ClassType bound = iterator.next();
        if (iterator.previousIndex() > 0) {
          printer.add(" & ");
        }
        bound.print(printer);
      }
    }
  }

  /**
   * Set single type variable as bound and clears all other bounds.
   */
  public TypeParameter setBoundTypeVariable(TypeVariable boundTypeVariable) {
    this.boundTypeVariable = boundTypeVariable;
    this.bounds.clear();
    return this;
  }

}
