package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class Annotation {

  public static final EnumSet<ElementType> addSpaceSet = EnumSet.of(ElementType.TYPE_USE);

  public TypeName typeName;
  public List<ElementType> targets = new ArrayList<>();

  public Annotation(String packageName, String... names) {
    this(new TypeName(packageName, Arrays.asList(names)));
  }

  public Annotation(TypeName typeName) {
    this.typeName = typeName;
  }

  public void print(JavaPrinter printer, ElementType annotationTarget) {
    if (!targets.isEmpty()) {
      if (!targets.contains(annotationTarget)) {
        throw new AssertionError("Annotation not allowed here!");
      }
    }
    printer.add("@");
    printer.add(typeName.toString());
    if (addSpaceSet.contains(annotationTarget)) {
      printer.add(" ");
    }
  }

}
