package de.codeturm.poetry;

import org.junit.Assert;
import org.junit.Test;

import de.codeturm.poetry.type.ClassType;

public class CompilationUnitTest {

  @Test
  public void simple() {
    CompilationUnit unit = new CompilationUnit("de.codeturm.poetry");
    unit.annotations.add(new Annotation("java.lang.annotation.Documented"));
    unit.annotations.add(new Annotation("de.codeturm.poetry.PackageModifierAnnotation"));
    unit.declarations.add(new ClassDeclaration("SimpleClass")
        .setModifiers("public")
        .setSuperClass(new ClassType("java.lang", "Thread").addAnnotation(new Annotation("Tag"))));
    unit.declarations.add(new EnumDeclaration("SimpleEnum").addAnnotation("java.lang.Deprecated"));
    unit.declarations.add(new InterfaceDeclaration("SimpleInterface"));
    unit.declarations.add(new AnnotationDeclaration("SimpleAnnotation"));
    JavaPrinter printer = new JavaPrinter();
    String actual = unit.toString(printer);
    Assert.assertEquals(String.join("\n",
        "@java.lang.annotation.Documented",
        "@de.codeturm.poetry.PackageModifierAnnotation",
        "package de.codeturm.poetry;",
        "",
        "public class SimpleClass extends java.lang.@Tag Thread {",
        "}",
        "",
        "@java.lang.Deprecated",
        "enum SimpleEnum {",
        "}",
        "",
        "interface SimpleInterface {",
        "}",
        "",
        "@interface SimpleAnnotation {",
        "}",
        ""), actual);
  }

}
