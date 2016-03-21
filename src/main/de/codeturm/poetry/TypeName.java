package de.codeturm.poetry;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Simple type name.
 * 
 * Used by annotations usages, import declarations and other non-annotatable constructs.
 * 
 * http://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-TypeName
 * 
 * <pre>
 * TypeName:
 *   Identifier 
 *   PackageOrTypeName . Identifier
 *   
 *  PackageOrTypeName:
 *    Identifier 
 *    PackageOrTypeName . Identifier
 * </pre>
 */
public class TypeName {

  /* @formatter:off  
  public static final TypeName BOOLEAN = new TypeName("boolean");
  public static final TypeName BYTE = new TypeName("byte");
  public static final TypeName CHAR = new TypeName("char");
  public static final TypeName DOUBLE = new TypeName("double");
  public static final TypeName FLOAT = new TypeName("float");
  public static final TypeName INT = new TypeName("int");
  public static final TypeName LONG = new TypeName("long");
  public static final TypeName OBJECT = new TypeName("java.lang", "Object");
  public static final TypeName SHORT = new TypeName("short");
  public static final TypeName VOID = new TypeName("void");
  @formatter:on */

  private final List<String> names;
  private final String packageName;

  public TypeName(String packageName, List<String> names) {
    this.packageName = requireNonNull(packageName, "packageName");
    this.names = Collections.unmodifiableList(requireNonNull(names, "names"));
    if (names.isEmpty()) {
      throw new IllegalArgumentException("names must not be empty");
    }
  }

  public TypeName(String packageName, String name) {
    this(packageName, Arrays.asList(name));
  }

  public TypeName(String packageName, String outer, String inner) {
    this(packageName, Arrays.asList(outer, inner));
  }

  public List<String> getNames() {
    return names;
  }

  public String getPackageName() {
    return packageName;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (!packageName.isEmpty()) {
      builder.append(packageName).append('.');
    }
    builder.append(String.join(".", names));
    return builder.toString();
  }

}
