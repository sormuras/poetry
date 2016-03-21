package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassType implements ReferenceType {

  public static final ClassType BOOLEAN = new ClassType("boolean");
  public static final ClassType BYTE = new ClassType("byte");
  public static final ClassType CHAR = new ClassType("char");
  public static final ClassType DOUBLE = new ClassType("double");
  public static final ClassType FLOAT = new ClassType("float");
  public static final ClassType INT = new ClassType("int");
  public static final ClassType LONG = new ClassType("long");
  public static final ClassType OBJECT = new ClassType("java.lang", new Name("Object"));
  public static final ClassType SHORT = new ClassType("short");
  public static final ClassType VOID = new ClassType("void");


  public List<Name> names = new ArrayList<>();
  public String packageName = "";

  private ClassType(String keyword) {
    this("", new Name(keyword));
  }

  public ClassType(String packageName, String... names) {
    this.packageName = packageName;
    this.names = Arrays.asList(names).stream().map(n -> new Name(n)).collect(Collectors.toList());
  }

  public ClassType(String packageName, Name... names) {
    this.packageName = packageName;
    this.names = Arrays.asList(names);
  }

  public ClassType addAnnotation(Annotation annotation) {
    return addAnnotation(names.size() - 1, annotation);
  }

  public ClassType addAnnotation(int index, Annotation annotation) {
    names.get(index).addAnnotation(annotation);
    return this;
  }

  public ClassType print(JavaPrinter printer) {
    if (!packageName.isEmpty()) {
      printer.add("%s.", packageName);
    }
    for (Name part : names) {
      part.print(printer, ElementType.TYPE_USE);
    }
    return this;
  }

}
