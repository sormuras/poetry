package de.codeturm.poetry;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

public abstract class TypeDeclaration<T> implements Annotatable<T> {

  public List<Annotation> annotations = new ArrayList<>();
  public final String keyword;
  public Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
  public String name;

  protected TypeDeclaration(String keyword, String name) {
    this.keyword = keyword;
    this.name = name;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void print(JavaPrinter printer) {
    printDeclarationHead(printer);
    printer.add(" {");
    printer.newline();
    printer.inc();
    // type body...
    printer.dec();
    printer.add("}");
    printer.newline();
  }

  /**
   * Print modifiers, keyword (class) and name inline.
   */
  public void printDeclarationHead(JavaPrinter printer) {
    printAnnotations(printer, ElementType.TYPE);
    for (Modifier modifier : modifiers) {
      printer.add("%s", modifier);
      printer.add(" ");
    }
    printer.add("%s", keyword);
    printer.add(" ");
    printer.add("%s", name);
  }

  @SuppressWarnings("unchecked")
  public T setModifiers(String... modifiers) {
    Arrays.asList(modifiers).forEach(n -> this.modifiers.add(Modifier.valueOf(n.toUpperCase())));
    return (T) this;
  }

}
