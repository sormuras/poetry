package de.codeturm.poetry;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

public class JavaPrinter {

  private int appendPointer = Integer.MIN_VALUE;
  private StringBuilder builder = new StringBuilder();
  public String indentation = "  ";
  private int indentationDepth = 0;
  public List<String> lines = new ArrayList<>();
  public String linesDelimiter = "\n";

  public JavaPrinter append(String format, Object... args) {
    if (appendPointer == Integer.MIN_VALUE) {
      line(format, args);
      appendPointer = lines.size() - 1;
      return this;
    }
    builder.setLength(0);
    builder.append(lines.get(appendPointer));
    builder.append(" ");
    builder.append(args.length == 0 ? format : format(format, args));
    lines.set(appendPointer, builder.toString());
    return this;
  }

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

  public JavaPrinter line(String format, Object... args) {
    appendPointer = Integer.MIN_VALUE;
    if (format.isEmpty()) {
      lines.add("");
      return this;
    }
    builder.setLength(0);
    for (int i = 0; i < indentationDepth; i++) {
      builder.append(indentation);
    }
    builder.append(args.length == 0 ? format : format(format, args));
    lines.add(builder.toString());
    return this;
  }
}
