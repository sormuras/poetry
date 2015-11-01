package com.github.sormuras.poetry;

import static java.util.Collections.singleton;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Java source file object implementation.
 * 
 * @author Christian Stein
 */
@SuppressWarnings("javadoc")
public class Source extends SimpleJavaFileObject {

  private List<String> compilerOptions;
  private List<Processor> compilerProcessors;
  private List<String> lines;

  public Source(String canonical, String... lines) {
    this(canonical, Arrays.asList(lines));
  }

  public Source(String canonical, List<String> lines) {
    super(URI.create("source:///" + canonical.replace('.', '/') + Kind.SOURCE.extension),
        Kind.SOURCE);
    this.lines = lines;
    this.compilerOptions = new ArrayList<>();
    this.compilerProcessors = new ArrayList<>();
  }

  public ClassLoader compile() {
    return compile(getClass().getClassLoader());
  }

  public ClassLoader compile(ClassLoader parent) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      throw new IllegalStateException("No system java compiler available. JDK is required!");
    }
    StandardJavaFileManager sjfm = compiler.getStandardFileManager(null, null, null);
    // List<File> classPathFiles = new ArrayList<>();
    // for(String name :
    // System.getProperty("java.class.path").split(System.getProperty("path.separator")))
    // classPathFiles.add(new File(name));
    // try {
    // sjfm.setLocation(StandardLocation.CLASS_PATH, classPathFiles);
    // System.out.println(classPathFiles);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    Manager manager = new Manager(sjfm, parent);
    DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
    CompilationTask task = compiler.getTask(null, manager, diagnosticCollector, compilerOptions,
        null, singleton(this));

    if (!compilerProcessors.isEmpty())
      task.setProcessors(compilerProcessors);
    boolean success = task.call();
    if (!success)
      throw new RuntimeException(diagnosticCollector.getDiagnostics().toString());
    return manager.getClassLoader(null);
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    StringBuilder builder = new StringBuilder(lines.size() * 80);
    lines.forEach(line -> builder.append(line).append(System.lineSeparator()));
    return builder.toString();
  }

  public Charset getCharset() {
    return Charset.forName("UTF-8");
  }

  /**
   * <code><pre>
   *   .add("-parameters");
   *   .add("-Xlint:all");
   *   .add("-XprintRounds");
   *   .add("-Aorg.prevayler.contrib.compayler.Processor.debug=true"),
   * </pre></code>
   * 
   * @return
   */
  public List<String> getCompilerOptions() {
    return compilerOptions;
  }

  public List<Processor> getCompilerProcessors() {
    return compilerProcessors;
  }

  public List<String> getLines() {
    return lines;
  }

  public Path save() throws Exception {
    return save(System.getProperty("user.dir"));
  }

  public Path save(String targetPath) throws Exception {
    File base = new File(targetPath);
    File file = new File(base, toUri().getPath());
    file.getParentFile().mkdirs();
    return Files.write(file.toPath(), getLines(), getCharset());
  }

}
