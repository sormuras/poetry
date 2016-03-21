package de.codeturm.poetry;

import org.junit.Assert;
import org.junit.Test;

public class ToStringTest {

  @Test
  public void arrayType() {
    Assert.assertEquals("java.lang.Object[]", new ArrayType(ClassType.OBJECT, 1).toString());
    Assert.assertEquals("java.lang.Object[][][]", new ArrayType(ClassType.OBJECT, 3).toString());
    ArrayType actual = new ArrayType(ClassType.OBJECT, 3);
    actual.addAnnotation(0, new Annotation(new TypeName("test", "T")));
    actual.addAnnotation(1, new Annotation(new TypeName("test", "S")));
    actual.addAnnotation(1, new Annotation(new TypeName("test", "T")));
    actual.addAnnotation(2, new Annotation(new TypeName("test", "T")));
    Assert.assertEquals("java.lang.Object@test.T []@test.S @test.T []@test.T []",
        actual.toString());
  }

}
