package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Annotation {

  public static final EnumSet<ElementType> addSpaceSet = EnumSet.of(ElementType.TYPE_USE);

  public ClassType type;
  public List<ElementType> targets = new ArrayList<>();

  public Annotation(String packageName, String... simpleNames) {
    this(new ClassType(packageName, simpleNames));
  }

  public Annotation(ClassType type) {
    this.type = type;
  }

  public void print(JavaPrinter printer, ElementType target) {
    if (!targets.isEmpty()) {
      if (!targets.contains(target)) {
        throw new AssertionError("Annotation not allowed here!");
      }
    }
    printer.add("@");
    type.print(printer);
    if (addSpaceSet.contains(target)) {
      printer.add(" ");
    }
  }

}
