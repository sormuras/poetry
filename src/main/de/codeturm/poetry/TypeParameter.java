package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

public class TypeParameter implements Annotatable<TypeParameter> {

  public List<Annotation> annotations = new ArrayList<>();
  public String identifier = "";

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void print(JavaPrinter printer) {
    // {TypeParameterModifier}
    printAnnotations(printer, ElementType.TYPE_PARAMETER);
    // Identifier
    // [TypeBound]
  }

}
