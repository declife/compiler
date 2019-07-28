package com.liu.test.compile;

import com.liu.test.controller.SpringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.io.*;
import javax.tools.JavaFileObject;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.Modifier;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

/**
 * In-memory java file manager.
 *
 * @author michael
 */
public class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    // compiled classes in bytes:
    final Map<String, byte[]> classBytes = new HashMap<String, byte[]>();
//    private StandardJavaFileManager stdManager;
    private PackageInternalsFinder finder;
    private final ClassLoader classLoader;

    MemoryJavaFileManager(StandardJavaFileManager fileManager, ClassLoader loader) {
        super(fileManager);
        this.classLoader = loader;
//        this.stdManager = fileManager;
        this.finder = new PackageInternalsFinder(loader);
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof CustomJavaFileObject) {
            return ((CustomJavaFileObject) file).binaryName();
        } else { // if it's not CustomJavaFileObject, then it's coming from standard file manager - let it handle the file
            return fileManager.inferBinaryName(location, file);
        }
    }

    public Map<String, byte[]> getClassBytes() {
        return new HashMap<String, byte[]>(this.classBytes);
    }

    @Override
    public void flush() throws IOException {
    }


    @Override
    public void close() throws IOException {
        classBytes.clear();
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classLoader;
        //return super.getClassLoader(location);
        //	return SpringUtil.getBean("service").getClass().getClassLoader();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
                                               FileObject sibling) throws IOException {
//		if (kind == Kind.CLASS) {
//			return new MemoryOutputJavaFileObject(className);
//		} else {
//			return super.getJavaFileForOutput(location, className, kind, sibling);
//		}
        throw new UnsupportedOperationException();
    }

    JavaFileObject makeStringSource(String name, String code) {
        return new MemoryInputJavaFileObject(name, code);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
        if (location == StandardLocation.PLATFORM_CLASS_PATH) { // let standard manager hanfle
            return fileManager.list(location, packageName, kinds, recurse);
        } else if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
            if (packageName.startsWith("java")) { // a hack to let standard manager handle locations like "java.lang" or "java.util". Prob would make sense to join results of standard manager with those of my finder here
                return fileManager.list(location, packageName, kinds, recurse);
            } else { // app specific classes are here
                return finder.find(packageName);
            }
        }
        return Collections.emptyList();

    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    static class MemoryInputJavaFileObject extends SimpleJavaFileObject {

        final String code;

        MemoryInputJavaFileObject(String name, String code) {
            super(URI.create("string:///" + name), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(code);
        }
    }


}

class CustomJavaFileObject implements JavaFileObject {
    private final String binaryName;
    private final URI uri;
    private final String name;

    public CustomJavaFileObject(String binaryName, URI uri) {
        this.uri = uri;
        this.binaryName = binaryName;
        name = uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath(); // for FS based URI the path is not null, for JAR URI the scheme specific part is not null
    }

    @Override
    public URI toUri() {
        return uri;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return uri.toURL().openStream(); // easy way to handle any URI!
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Kind getKind() {
        return Kind.CLASS;
    }

    @Override // copied from SImpleJavaFileManager
    public boolean isNameCompatible(String simpleName, Kind kind) {
        String baseName = simpleName + kind.extension;
        return kind.equals(getKind())
                && (baseName.equals(getName())
                || getName().endsWith("/" + baseName));
    }

    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }

    public String binaryName() {
        return binaryName;
    }


    @Override
    public String toString() {
        return "CustomJavaFileObject{" +
                "uri=" + uri +
                '}';
    }
}

//	class MemoryOutputJavaFileObject extends SimpleJavaFileObject {
//		final String name;
//
//		MemoryOutputJavaFileObject(String name) {
//			super(URI.create("string:///" + name), Kind.CLASS);
//			this.name = name;
//		}
//
//		@Override
//		public OutputStream openOutputStream() {
//			return new FilterOutputStream(new ByteArrayOutputStream()) {
//				@Override
//				public void close() throws IOException {
//					out.close();
//					ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
//					classBytes.put(name, bos.toByteArray());
//				}
//			};
//		}
//
//	}

