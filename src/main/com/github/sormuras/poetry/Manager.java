package com.github.sormuras.poetry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * In-memory file manager.
 *
 * @author Christian Stein
 */
class Manager extends ForwardingJavaFileManager<StandardJavaFileManager> {

  final class ByteArrayFileObject extends SimpleJavaFileObject {
    ByteArrayOutputStream stream;

    ByteArrayFileObject(String canonical, Kind kind) {
      super(URI.create("javapoet:///" + canonical.replace('.', '/') + kind.extension), kind);
    }

    byte[] getBytes() {
      return stream.toByteArray();
    }

    @Override public CharSequence getCharContent(boolean ignoreErrors) throws IOException {
      return new String(getBytes(), StandardCharsets.UTF_8.name());
    }

    @Override public OutputStream openOutputStream() throws IOException {
      this.stream = new ByteArrayOutputStream(2000);
      return stream;
    }
  }

  final class SecureLoader extends SecureClassLoader {
    SecureLoader(ClassLoader parent) {
      super(parent);
    }

    @Override protected Class<?> findClass(String className) throws ClassNotFoundException {
      ByteArrayFileObject object = map.get(className);
      if (object == null) {
        throw new ClassNotFoundException(className);
      }
      byte[] b = object.getBytes();
      return super.defineClass(className, b, 0, b.length);
    }
  }

  private final Map<String, ByteArrayFileObject> map = new HashMap<>();
  private final ClassLoader parent;

  Manager(StandardJavaFileManager standardManager, ClassLoader parent) {
    super(standardManager);
    this.parent = parent != null ? parent : getClass().getClassLoader();
  }

  @Override public ClassLoader getClassLoader(Location location) {
    return new SecureLoader(parent);
  }

  @Override public JavaFileObject getJavaFileForOutput(Location location, String className,
      Kind kind, FileObject sibling) throws IOException {
    ByteArrayFileObject object = new ByteArrayFileObject(className, kind);
    map.put(className, object);
    return object;
  }

  @Override public boolean isSameFile(FileObject a, FileObject b) {
    return a.toUri().equals(b.toUri());
  }
}