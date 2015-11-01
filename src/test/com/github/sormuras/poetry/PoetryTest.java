package com.github.sormuras.poetry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedType;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.junit.Assert;
import org.junit.Test;

import com.github.sormuras.poetry.Poetry;
import com.squareup.javapoet.TypeName;

@SuppressWarnings("javadoc")
public class PoetryTest {

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER, ElementType.TYPE_USE})
  public @interface Tag {
  }

  public static final String T = Tag.class.getCanonicalName();

  public static int n;
  public static @Tag int t;
  public static @Tag int[] a;
  public static @Tag int[][] a2;
  public static @Tag int[][][][][][][][][] a9;

  @Test
  public void testN() throws Exception {
    AnnotatedType annotatedType = PoetryTest.class.getField("n").getAnnotatedType();
    TypeName typeName = Poetry.annotated(annotatedType);
    Assert.assertEquals("int", typeName.toString());
  }

  @Test
  public void testT() throws Exception {
    AnnotatedType annotatedType = PoetryTest.class.getField("t").getAnnotatedType();
    TypeName typeName = Poetry.annotated(annotatedType);
    Assert.assertEquals("@" + T + " int", typeName.toString());
  }

  @Test
  public void testA() throws Exception {
    AnnotatedType annotatedType = PoetryTest.class.getField("a").getAnnotatedType();
    TypeName typeName = Poetry.annotated(annotatedType);
    Assert.assertEquals("@" + T + " int[]", typeName.toString());
  }

  @Test
  public void testA2() throws Exception {
    AnnotatedType annotatedType = PoetryTest.class.getField("a2").getAnnotatedType();
    TypeName typeName = Poetry.annotated(annotatedType);
    Assert.assertEquals("@" + T + " int[][]", typeName.toString());
  }

  @Test
  public void testA9() throws Exception {
    AnnotatedType annotatedType = PoetryTest.class.getField("a9").getAnnotatedType();
    TypeName typeName = Poetry.annotated(annotatedType);
    Assert.assertEquals("@" + T + " int[][][][][][][][][]", typeName.toString());
  }

  @Test
  public void testA3() throws Exception {
    Source source = new Source("A3", "interface A3 { void a3(@" + T + " int[][][] a3); }");
    source.getCompilerProcessors().add(new A3());
    source.compile();
  }

  @SupportedAnnotationTypes("com.github.sormuras.poetry.PoetryTest.Tag")
  private class A3 extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      for (Element element : roundEnv.getElementsAnnotatedWith(Tag.class)) {
        TypeName typeName = Poetry.annotated(element.asType());
        Assert.assertEquals("@" + T + " int[][][]", typeName.toString());
      }
      return true;
    }
  }

}
