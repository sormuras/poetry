package com.github.sormuras.poetry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

@SuppressWarnings("javadoc")
public class Manager extends ForwardingJavaFileManager<StandardJavaFileManager> {

  private class ByteArrayFileObject extends SimpleJavaFileObject {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2000);

    public ByteArrayFileObject(String canonical, Kind kind) {
      super(URI.create("bytes:///" + canonical.replace('.', '/') + kind.extension), kind);
    }

    public byte[] getBytes() {
      return outputStream.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
      return outputStream;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
      return new String(getBytes());
    }
  }

  private class SecureLoader extends SecureClassLoader {

    protected SecureLoader(ClassLoader parent) {
      super(parent);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
      ByteArrayFileObject object = map.get(className);
      if (object == null)
        throw new ClassNotFoundException(className);
      byte[] b = object.getBytes();
      return super.defineClass(className, b, 0, b.length);
    }
  }

  private final Map<String, ByteArrayFileObject> map = new HashMap<>();
  private final ClassLoader parent;

  public Manager(StandardJavaFileManager standardManager, ClassLoader parent) {
    super(standardManager);
    this.parent = parent != null ? parent : getClass().getClassLoader();
  }

  @Override
  public ClassLoader getClassLoader(Location location) {
    return new SecureLoader(parent);
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
      FileObject sibling) throws IOException {
    ByteArrayFileObject object = new ByteArrayFileObject(className, kind);
    map.put(className, object);
    return object;
  }
}
