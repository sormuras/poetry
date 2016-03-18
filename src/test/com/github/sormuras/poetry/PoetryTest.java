package com.github.sormuras.poetry;

import static org.junit.Assert.assertEquals;
import static com.github.sormuras.poetry.Poetry.binary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.junit.Assert;
import org.junit.Test;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
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

    assertEquals(toString(taco), ""
        + "package com.squareup.tacos;\n"
        + "\n"
        + "import " + getClass().getCanonicalName() + ";\n"
        + "\n"
        + "@" + getClass().getSimpleName() + ".AnnotationC(\"test\")\n"
        + "class Taco {\n"
        + "}\n");
  }

  @Test
  public void compile() throws Exception {
    JavaFile tacoFile = JavaFile.builder("com.squareup.tacos",
        TypeSpec.classBuilder("Taco")
            .addModifiers(Modifier.PUBLIC)
            .addField(ClassName.get("com.squareup.tacos", "Taco", "Inner"), "inner")
            .addType(TypeSpec.classBuilder("Inner")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build())
            .build())
        .build();
    Class<?> tacoClass = Poetry.compile(tacoFile);
    Object taco = tacoClass.newInstance();
    assertEquals("Taco", taco.getClass().getSimpleName());
    assertEquals("com.squareup.tacos.Taco", taco.getClass().getCanonicalName());
    Object a = tacoClass.getDeclaredClasses()[0].newInstance();
    assertEquals("Inner", a.getClass().getSimpleName());
    assertEquals("com.squareup.tacos.Taco.Inner", a.getClass().getCanonicalName());
  }

  @Test
  public void compileAndCall() throws Exception {
    JavaFile tacoFile = JavaFile.builder("com.squareup.tacos",
        TypeSpec.classBuilder("Taco")
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(ParameterizedTypeName.get(Callable.class, String.class))
            .addField(String.class, "text", Modifier.FINAL)
            .addField(Number.class, "number", Modifier.FINAL)
            .addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "text")
                .addParameter(Number.class, "number")
                .addStatement("this.text = text")
                .addStatement("this.number = number")
                .build())
            .addMethod(MethodSpec.methodBuilder("call")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return text + '-' + number")
                .build())
            .build())
        .build();
    @SuppressWarnings("unchecked")
    Callable<String> taco = Poetry.compile(tacoFile, Callable.class, "NCC", (short) 1701);
    assertEquals("NCC-1701", taco.call());
  }

  @Test
  public void binaryString() {
    assertEquals("void", binary(TypeName.VOID));
    assertEquals("int", binary(TypeName.INT));
    assertEquals("java.lang.Object", binary(TypeName.OBJECT));
    assertEquals("java.lang.Thread$State", binary(TypeName.get(Thread.State.class)));
    assertEquals("[java.util.Map$Entry;", binary(TypeName.get(Map.Entry[].class)));
    assertEquals("[[[Z", binary(TypeName.get(boolean[][][].class)));
    assertEquals("java.util.List", binary(ParameterizedTypeName.get(List.class, String.class)));
    assertEquals("java.util.Set", binary(ParameterizedTypeName.get(Set.class, UUID.class)));
  }

  private String toString(TypeSpec typeSpec) {
    return JavaFile.builder("com.squareup.tacos", typeSpec).build().toString();
  }
}
