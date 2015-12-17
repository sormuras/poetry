package com.github.sormuras.poetry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.junit.Assert;
import org.junit.Test;

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
    Source source = new Source("Test", ""
        + "@" + T + "\n"
        + "public interface Test extends Runnable, java.nio.channels.ByteChannel {\n"
        + "  void test();\n"
        + "}\n");
    TagProcessor processor = new TagProcessor();
    source.getCompilerProcessors().add(processor);
    source.compile();
    Assert.assertEquals(""
        + "@com.github.sormuras.poetry.ModelPoetryTest.Tag\n"
        + "public interface Test extends java.lang.Runnable, java.nio.channels.ByteChannel {\n"
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
        processor.builder.build().toString());
  }

  @SupportedAnnotationTypes("com.github.sormuras.poetry.ModelPoetryTest.Tag")
  private class TagProcessor extends AbstractProcessor {
    TypeSpec.Builder builder;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      for (Element element : roundEnv.getElementsAnnotatedWith(Tag.class)) {
        builder = new ModelPoetry(processingEnv).buildInterface((TypeElement) element);
      }
      return true;
    }
  }

}
