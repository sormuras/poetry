package de.codeturm.poetry;

import org.junit.Assert;
import org.junit.Test;

public class ToStringTest {

  @Test
  public void arrayType() {
    Assert.assertEquals("java.lang.Object[]", new ArrayType(ClassType.OBJECT, 1).toString());
    Assert.assertEquals("java.lang.Object[][][]", new ArrayType(ClassType.OBJECT, 3).toString());
    ArrayType actual = new ArrayType(ClassType.OBJECT, 3);
    actual.addAnnotation(0, new Annotation("", "T"));
    actual.addAnnotation(1, new Annotation("", "S"));
    actual.addAnnotation(1, new Annotation("", "T"));
    actual.addAnnotation(2, new Annotation("", "T"));
    Assert.assertEquals("java.lang.Object@T []@S @T []@T []", actual.toString());
  }

}
