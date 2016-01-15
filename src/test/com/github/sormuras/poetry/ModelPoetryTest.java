package com.github.sormuras.poetry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.channels.ByteChannel;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.junit.Assert;
import org.junit.Test;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@SuppressWarnings("javadoc")
public class ModelPoetryTest {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Tag {
  }

  public static final String T = Tag.class.getCanonicalName();

  @Test
  public void testFromInterface() throws Exception {
    TypeName testTypeName = ClassName.get("", "Test");
    //Source source = new Source("Test", ""
    //    + "@" + T + "\n"
    //    + "interface Test extends Runnable, java.nio.channels.ByteChannel {\n"
    //    + "  void test();\n"
    //    + "}\n");
    JavaFile source = JavaFile.builder("", TypeSpec.interfaceBuilder("Test")
        .addAnnotation(Poetry.annotation(Tag.class))
        .addSuperinterface(Runnable.class)
        .addSuperinterface(ByteChannel.class)
        .addMethod(MethodSpec.methodBuilder("test")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .build())
        .build())
        .build();
    TagProcessor processor = new TagProcessor();
    Poetry.compile(source, processor);
    Assert.assertEquals(""
        + "@com.github.sormuras.poetry.ModelPoetryTest.Tag\n"
        + "interface Test extends java.lang.Runnable, java.nio.channels.ByteChannel {\n"
        + "  void run();\n"
        + "\n"
        + "  boolean isOpen();\n"
        + "\n"
        + "  void close() throws java.io.IOException;\n"
        + "\n"
        + "  int write(java.nio.ByteBuffer arg0) throws java.io.IOException;\n"
        + "\n"
        + "  int read(java.nio.ByteBuffer arg0) throws java.io.IOException;\n"
        + "\n"
        + "  void test();\n"
        + "}\n",
        processor.interfaceBuilder.build().toString());
    TypeSpec implement = Poetry.implement(processor.interfaceBuilder.build(), "", "TestDecorator",
        m -> CodeBlock.builder()
          .addStatement((m.returnType.equals(TypeName.VOID) ? "" : "return ") + "component."
              + Poetry.call(m))
          .build())
        .addField(testTypeName, "component", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(testTypeName, "component")
            .addStatement("this.component = component")
            .build())
        .build();
    Assert.assertEquals(""
        + "public class TestDecorator implements Test {\n"
        + "  private static final Test component;\n"
        + "\n"
        + "  public TestDecorator(Test component) {\n"
        + "    this.component = component;\n"
        + "  }\n"
        + "\n"
        + "  public void run() {\n"
        + "    component.run();\n"
        + "  }\n"
        + "\n"
        + "  public boolean isOpen() {\n"
        + "    return component.isOpen();\n"
        + "  }\n"
        + "\n"
        + "  public void close() throws java.io.IOException {\n"
        + "    component.close();\n"
        + "  }\n"
        + "\n"
        + "  public int write(java.nio.ByteBuffer arg0) throws java.io.IOException {\n"
        + "    return component.write(arg0);\n"
        + "  }\n"
        + "\n"
        + "  public int read(java.nio.ByteBuffer arg0) throws java.io.IOException {\n"
        + "    return component.read(arg0);\n"
        + "  }\n"
        + "\n"
        + "  public void test() {\n"
        + "    component.test();\n"
        + "  }\n"
        + "}\n", 
        implement.toString());
  }

  @SupportedAnnotationTypes("com.github.sormuras.poetry.ModelPoetryTest.Tag")
  private class TagProcessor extends AbstractProcessor {
    TypeSpec.Builder interfaceBuilder;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      for (Element element : roundEnv.getElementsAnnotatedWith(Tag.class)) {
        interfaceBuilder = new ModelPoetry(processingEnv).buildInterface((TypeElement) element);
      }
      return true;
    }
  }

}
