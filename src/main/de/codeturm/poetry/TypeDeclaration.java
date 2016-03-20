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
    printer.context.typeDeclaration = this;
    printDeclarationHead(printer);
    printer.inline(" {");
    printer.inc();
    // type body...
    printer.dec();
    printer.newline("}");
  }

  public void printDeclarationHead(JavaPrinter printer) {
    for (Annotation annotation : annotations) {
      annotation.print(printer, ElementType.TYPE);
    }
    for (Modifier modifier : modifiers) {
      printer.inline("%s", modifier);
      printer.inline(" ");
    }
    printer.inline("%s", keyword);
    printer.inline(" ");
    printer.inline("%s", name);
  }

  @SuppressWarnings("unchecked")
  public T setModifiers(String... modifiers) {
    Arrays.asList(modifiers).forEach(n -> this.modifiers.add(Modifier.valueOf(n.toUpperCase())));
    return (T) this;
  }

}
