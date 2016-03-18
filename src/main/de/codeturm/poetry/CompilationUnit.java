package de.codeturm.poetry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompilationUnit {

  public List<Annotation> annotations = new ArrayList<>();
  public List<TypeDeclaration> declarations = new ArrayList<>();
  public List<String> packages = new ArrayList<>();

  public CompilationUnit(String packageName) {
    this.packages = Arrays.asList(packageName.split("\\."));
  }

  /**
   * {@linkplain http://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html} 
   */
  public void print(JavaPrinter printer) {
    // [PackageDeclaration]
    String packageName = String.join(".", packages);
    if (!packageName.isEmpty()) {
      if (!annotations.isEmpty()) {
        for (Annotation annotation : annotations) {
          annotation.print(printer, Annotation.Context.PackageModifier);
        }
      }
      printer.add("package %s;", packageName);
      printer.add("");
    }
    // {ImportDeclaration}
    // {TypeDeclaration}
    for (TypeDeclaration javaType : declarations) {
      javaType.print(printer);
      printer.add("");
    }
  }

  @Override
  public String toString() {
    return toString(new JavaPrinter());
  }

  public String toString(JavaPrinter printer) {
    print(printer);
    return String.join(printer.linesDelimiter, printer.lines);
  }

}
