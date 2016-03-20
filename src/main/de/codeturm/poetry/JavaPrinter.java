package de.codeturm.poetry;

import java.util.ArrayList;
import java.util.List;

public class JavaPrinter {

  public class Context {
    public CompilationUnit compilationUnit;
    public TypeDeclaration<?> typeDeclaration;
  }

  private int appendPointer = Integer.MIN_VALUE;
  private StringBuilder builder = new StringBuilder();
  public Context context = new Context();
  public String indentation = "  ";
  private int indentationDepth = 0;
  public List<String> lines = new ArrayList<>();
  public String linesDelimiter = "\n";

  public JavaPrinter dec() {
    indentationDepth--;
    if (indentationDepth < 0)
      indentationDepth = 0;
    return this;
  }

  public JavaPrinter inc() {
    indentationDepth++;
    return this;
  }

  public JavaPrinter inline(String format, Object... args) {
    if (format.isEmpty()) {
      return this;
    }
    if (appendPointer == Integer.MIN_VALUE) {
      newline(format, args);
      appendPointer = lines.size() - 1;
      return this;
    }
    builder.setLength(0);
    builder.append(lines.get(appendPointer));
    builder.append(args.length == 0 ? format : String.format(format, args));
    lines.set(appendPointer, builder.toString());
    return this;
  }

  public JavaPrinter newline(String format, Object... args) {
    appendPointer = Integer.MIN_VALUE;
    if (format.isEmpty()) {
      lines.add("");
      return this;
    }
    builder.setLength(0);
    for (int i = 0; i < indentationDepth; i++) {
      builder.append(indentation);
    }
    builder.append(args.length == 0 ? format : String.format(format, args));
    lines.add(builder.toString());
    return this;
  }
}
