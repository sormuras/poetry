package com.github.sormuras.poetry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.junit.Assert;
import org.junit.Test;

import com.github.sormuras.poetry.Poetry;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@SuppressWarnings("javadoc")
public class PoetryTest {

  @Retention(RetentionPolicy.RUNTIME)
  public @interface AnnotationC {
    String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface AnnotationD {
    String[] value() default "default";
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.PARAMETER, ElementType.TYPE_USE })
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
    // "interface A3 { void a3(@" + T + " int[][][] a3); }"
    JavaFile source = JavaFile.builder("", TypeSpec.interfaceBuilder("A3")
        .addMethod(MethodSpec.methodBuilder("a3")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(ParameterSpec.builder(int[][][].class, "a3")
                .addAnnotation(Poetry.annotation(Tag.class))
                .build())
            .build())
        .build())
        .build();
    A3 a3 = new A3();
    Poetry.compile(source, a3);
    Assert.assertEquals(1, a3.names.size());
    Assert.assertEquals("@" + T + " int[][][]", a3.names.get(0).toString());
  }

  @SupportedAnnotationTypes("com.github.sormuras.poetry.PoetryTest.Tag")
  private class A3 extends AbstractProcessor {
    List<TypeName> names = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      for (Element element : roundEnv.getElementsAnnotatedWith(Tag.class)) {
        names.add(Poetry.annotated(element.asType()));
      }
      return true;
    }
  }

  @Test
  public void createSimpleAnnotation() {
    String className = getClass().getCanonicalName();
    AnnotationSpec d = Poetry.annotation(AnnotationD.class);
    Assert.assertEquals(d.toString(), "@" + className + ".AnnotationD");
    d = Poetry.annotation(AnnotationD.class, "abc");
    Assert.assertEquals(d.toString(), "@" + className + ".AnnotationD(\"abc\")");
    d = Poetry.annotation(AnnotationD.class, "a", "b", "c");
    Assert.assertEquals(d.toString(), "@" + className + ".AnnotationD({\"a\", \"b\", \"c\"})");
  }

  @Test
  public void createSimpleSuppressWarnings() {
    AnnotationSpec a = Poetry.annotation(SuppressWarnings.class, "javadoc", "null");
    AnnotationSpec b = AnnotationSpec.builder(SuppressWarnings.class)
        .addMember("value", "$S", "javadoc")
        .addMember("value", "$S", "null")
        .build();
    AnnotationSpec.Builder c = AnnotationSpec.builder(SuppressWarnings.class);
    Poetry.value(c, "value", "javadoc");
    Poetry.value(c, "value", "null");
    Assert.assertEquals(a, b);
    Assert.assertEquals(a, c.build());
  }

  @Test
  public void createSimpleAnnotationWithTypeSpec() {
    AnnotationSpec spec = Poetry.annotation(AnnotationC.class, "test");
    TypeSpec taco = TypeSpec.classBuilder("Taco")
        .addAnnotation(spec)
        .build();

    Assert.assertEquals(toString(taco), ""
        + "package com.squareup.tacos;\n"
        + "\n"
        + "import " + getClass().getCanonicalName() + ";\n"
        + "\n"
        + "@" + getClass().getSimpleName() + ".AnnotationC(\"test\")\n"
        + "class Taco {\n"
        + "}\n");
  }

  private String toString(TypeSpec typeSpec) {
    return JavaFile.builder("com.squareup.tacos", typeSpec).build().toString();
  }
}
