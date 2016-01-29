/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.sormuras.poetry;

import static java.lang.Thread.State.NEW;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.lang.model.element.Modifier;

import org.junit.Test;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class MemberRefTest {
  @Test
  public void readmeExampleWithThreadStateNew() {
    CodeBlock block = CodeBlock.builder().add("$L", MemberRef.get(NEW).toCodeBlock()).build();
    assertEquals("java.lang.Thread.State.NEW", block.toString());
  }

  @Test
  public void readmeExampleWithMethodChaining() throws Exception {
    CodeBlock builder = MemberRef.get(CodeBlock.class.getMethod("builder")).toCodeBlock();
    CodeBlock add = MemberRef
        .get(CodeBlock.Builder.class.getMethod("add", String.class, Object[].class)).toCodeBlock();
    CodeBlock build = MemberRef.get(CodeBlock.Builder.class.getMethod("build")).toCodeBlock();
    CodeBlock block = CodeBlock.builder()
        .add("$L().$L(\"$$L\", $L).$L()", builder, add, MemberRef.get(NEW).toCodeBlock(), build)
        .build();
    assertEquals("" + "com.squareup.javapoet.CodeBlock.builder()"
        + ".add(\"$L\", java.lang.Thread.State.NEW)" + ".build()", block.toString());
  }

  // @Test public void equals() throws Exception {
  // MemberRef expected = MemberRef.get(NEW);
  // assertThat(expected.kind).isEqualTo(Kind.ENUM);
  // String name = "NEW";
  // assertThat(expected).isEqualTo(MemberRef.get(NEW));
  // assertThat(expected).isEqualTo(MemberRef.get(
  // Kind.ENUM,
  // ClassName.get(Thread.State.class),
  // name,
  // true));
  // name = "serialVersionUID";
  // expected = MemberRef.get(String.class.getDeclaredField(name));
  // assertThat(expected.kind).isEqualTo(Kind.FIELD);
  // assertThat(expected).isEqualTo(MemberRef.get(String.class.getDeclaredField(name)));
  // assertThat(expected).isEqualTo(MemberRef.get(
  // Kind.FIELD,
  // ClassName.get(String.class),
  // name,
  // true));
  // name = "valueOf";
  // expected = MemberRef.get(String.class.getMethod(name, int.class));
  // assertThat(expected.kind).isEqualTo(Kind.METHOD);
  // assertThat(expected).isEqualTo(MemberRef.get(String.class.getMethod(name, int.class)));
  // assertThat(expected).isEqualTo(MemberRef.get(
  // Kind.METHOD,
  // ClassName.get(String.class),
  // name,
  // true));
  // }

  @Test
  public void importStaticNone() throws Exception {
    assertEquals(
        "" + "package readme;\n" + "\n" + "import java.lang.System;\n"
            + "import java.util.concurrent.TimeUnit;\n" + "\n" + "class Util {\n"
            + "  public static long minutesToSeconds(long minutes) {\n" + "    System.gc();\n"
            + "    return TimeUnit.SECONDS.convert(minutes, TimeUnit.MINUTES);\n" + "  }\n" + "}\n",
        JavaFile.builder("readme", typeSpec("Util")).build().toString());
  }

  @Test
  public void importStaticAll() throws Exception {
    assertEquals(
        "" + "package readme;\n" + "\n" + "import static java.lang.System.gc;\n"
            + "import static java.util.concurrent.TimeUnit.MINUTES;\n"
            + "import static java.util.concurrent.TimeUnit.SECONDS;\n" + "\n" + "class Util {\n"
            + "  public static long minutesToSeconds(long minutes) {\n" + "    gc();\n"
            + "    return SECONDS.convert(minutes, MINUTES);\n" + "  }\n" + "}\n",
        JavaFile.builder("readme", typeSpec("Util"))
            .addStaticImport(TimeUnit.class, "SECONDS", "MINUTES")
            .addStaticImport(System.class, "gc").build().toString());

  }

  TypeSpec typeSpec(String name) {
    try {
      Method convert = TimeUnit.class.getMethod("convert", long.class, TimeUnit.class);
      MethodSpec method = MethodSpec.methodBuilder("minutesToSeconds")
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(long.class)
          .addParameter(long.class, "minutes")
          .addStatement("$L()", MemberRef.get(System.class.getMethod("gc")).toCodeBlock())
          .addStatement("return $L.$L(minutes, $L)", MemberRef.get(TimeUnit.SECONDS).toCodeBlock(),
              MemberRef.get(convert).toCodeBlock(), MemberRef.get(TimeUnit.MINUTES).toCodeBlock())
          .build();
      return TypeSpec.classBuilder(name).addMethod(method).build();
    } catch (Exception e) {
      throw new RuntimeException("should not happen!", e);
    }
  }
}
