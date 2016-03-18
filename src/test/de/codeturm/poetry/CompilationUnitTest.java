package de.codeturm.poetry;

import java.util.Arrays;

import org.junit.Test;
import org.junit.Assert;

public class CompilationUnitTest {

  @Test
  public void simple() {
    CompilationUnit unit = new CompilationUnit("de.codeturm.poetry");
    unit.annotations.add(new Annotation("java.lang.annotation.Documented"));
    unit.annotations.add(new Annotation("de.codeturm.poetry.PackageModfierAnnotation"));
    unit.declarations.add(new ClassDeclaration("SimpleClass").setModifiers("public"));
    unit.declarations.add(new EnumDeclaration("SimpleEnum"));
    unit.declarations.add(new InterfaceDeclaration("SimpleInterface"));
    unit.declarations.add(new AnnotationDeclaration("SimpleAnnotation"));
    JavaPrinter printer = new JavaPrinter();
    unit.toString(printer);
    Assert.assertArrayEquals(Arrays.asList(
        "@java.lang.annotation.Documented",
        "@de.codeturm.poetry.PackageModfierAnnotation",
        "package de.codeturm.poetry;",
        "",
        "public class SimpleClass {",
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
