package de.codeturm.poetry.type;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.codeturm.poetry.Annotation;
import de.codeturm.poetry.JavaPrinter;

public class ClassType extends ReferenceType {

  public static final ClassType OBJECT = new ClassType("java.lang", "Object");

  public String packageName = "";
  public List<AnnotatedIdentifier> parts = new ArrayList<>();

  // java.lang.@T Thread t = null;
  // java.lang.Thread.@T State s = null;
  // java.util.Map.@T Entry<Byte, Byte> e = null;
  // de.codeturm.poetry.type.@T ClassType.@T @U Inner i = null;

  public ClassType(String packageName, String... simpleNames) {
    this.packageName = packageName;
    Arrays.asList(simpleNames).forEach(n -> parts.add(new AnnotatedIdentifier(n)));
  }

  public ClassType addAnnotation(Annotation annotation) {
    return addAnnotation(parts.size() - 1, annotation);
  }

  public ClassType addAnnotation(int index, Annotation annotation) {
    parts.get(index).addAnnotation(annotation);
    return this;
  }

  public ClassType print(JavaPrinter printer) {
    if (!packageName.isEmpty()) {
      printer.inline("%s.", packageName);
    }
    for (AnnotatedIdentifier part : parts) {
      part.print(printer, ElementType.TYPE_USE);
    }
    return this;
  }

}
