package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

public class Annotation {

  public String name;
  public List<ElementType> targets = new ArrayList<>();

  public Annotation(String name) {
    this.name = name;
  }

  public void print(JavaPrinter printer, ElementType target) {
    if (!targets.isEmpty()) {
      if (!targets.contains(target)) {
        throw new AssertionError("Annotation not allowed here!");
      }
    }
    switch (target) {
    case TYPE_USE:
      printer.inline("@%s ", name);
      return;
    default:
      printer.newline("@%s", name);
    }
  }

}
