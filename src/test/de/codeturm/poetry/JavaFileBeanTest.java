package de.codeturm.poetry;

import java.util.Arrays;

import org.junit.Test;
import org.junit.Assert;

import de.codeturm.poetry.TypeDeclaration.Kind;

public class JavaFileBeanTest {

  @Test
  public void simple() {
    CompilationUnit unit = new CompilationUnit("de.codeturm.poetry");
    unit.annotations.add(new Annotation("java.lang.annotation.Documented"));
    unit.annotations.add(new Annotation("de.codeturm.poetry.PackageModfierAnnotation"));
    unit.declarations.add(new TypeDeclaration("SimpleClass", Kind.CLASS));
    unit.declarations.add(new TypeDeclaration("SimpleEnum", Kind.ENUM));
    unit.declarations.add(new TypeDeclaration("SimpleInterface", Kind.INTERFACE));
    unit.declarations.add(new TypeDeclaration("SimpleAnnotation", Kind.ANNOTATION));
    JavaPrinter printer = new JavaPrinter();
    unit.toString(printer);
    Assert.assertArrayEquals(Arrays.asList(
        "@java.lang.annotation.Documented",
        "@de.codeturm.poetry.PackageModfierAnnotation",
        "package de.codeturm.poetry;",
        "",
        "class SimpleClass {",
        "}",
        "",
        "enum SimpleEnum {",
        "}",
        "",
        "interface SimpleInterface {",
        "}",
        "",
        "@interface SimpleAnnotation {",
        "}",
        "").toArray(), printer.lines.toArray());
  }

}
