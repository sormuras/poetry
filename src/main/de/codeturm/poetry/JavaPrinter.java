package de.codeturm.poetry;

import java.util.ArrayList;
import java.util.List;

public class JavaPrinter {

  private StringBuilder currentLine = new StringBuilder();
  public String indentation = "  ";
  private int indentationDepth = 0;
  public List<String> lines = new ArrayList<>();
  public String linesDelimiter = "\n";

  public JavaPrinter add(String format, Object... args) {
    if (format.isEmpty()) {
      return this;
    }
    currentLine.append(args.length == 0 ? format : String.format(format, args));
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

  public JavaPrinter newline() {
    String newline = "";
    if (currentLine.length() > 0) {
      int capacity = indentationDepth * indentation.length() + currentLine.length();
      StringBuilder indented = new StringBuilder(capacity);
      for (int i = 0; i < indentationDepth; i++) {
        indented.append(indentation);
      }
      indented.append(currentLine.toString().trim());
      newline = indented.toString();
      currentLine.setLength(0);
    }
    lines.add(newline);
    return this;
  }

  @Override
  public String toString() {
    return String.join(linesDelimiter, lines) + currentLine;
  }
}
