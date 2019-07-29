package com.liu.test.compile;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * In-memory compile Java source code as String.
 * 
 * @author lxl
 */
public class JavaStringCompiler {

	JavaCompiler compiler;
	StandardJavaFileManager stdManager;

	public JavaStringCompiler() {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.stdManager = compiler.getStandardFileManager(null, null, null);
	}

	/**
	 * Compile a Java source file in memory.
	 * 
	 * @param fileName
	 *            Java file name, e.g. "Test.java"
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Map that contains class name as key,
	 *         class binary as value.
	 * @throws IOException
	 *             If compile error.
	 */
	public Map<String, byte[]> compile(String fileName, String source) throws IOException {
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager, Thread.currentThread().getContextClassLoader())) {
			System.out.println("cd");
			JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
			List<String> options = new ArrayList<>();
			options.addAll(Arrays.asList("-classpath",System.getProperty("java.class.path")));
//			Iterable options = Arrays.asList("-classpath", "/Users/hyukaliu/Desktop/Git/java/error/target/error-1.0-SNAPSHOT.jar!/BOOT-INF/*");
//			StringBuilder cp = new StringBuilder();
//			URLClassLoader urlClassLoader = (URLClassLoader) SpringUtil.getBean("service").getClass().getClassLoader();
//
//			for (URL url : urlClassLoader.getURLs()) {
//				cp.append(url.getFile()).append(File.pathSeparator);
//			}
//			System.out.println(cp);
//			System.out.println("cp" + System.getProperty("java.class.path"));
//			List<String> options = new ArrayList<String>();
//			options.add("-classpath");
//			options.add(cp.toString());
			CompilationTask task = compiler.getTask(null, manager, null, options, null, Arrays.asList(javaFileObject));
			Boolean result = task.call();
			if (result == null || !result.booleanValue()) {
				throw new RuntimeException("Compilation failed.");
			}
			return manager.getClassBytes();
		}
	}

	private String buildClassPath() {
		return null;
	}

	/**
	 * Load class from compiled classes.
	 * 
	 * @param name
	 *            Full class name.
	 * @param classBytes
	 *            Compiled results as a Map.
	 * @return The Class instance.
	 * @throws ClassNotFoundException
	 *             If class not found.
	 * @throws IOException
	 *             If load error.
	 */
	public Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {
		try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
			return classLoader.loadClass(name);
		}
	}

	private class MemoryClassLoader extends URLClassLoader {

		// class name to class bytes:
		private Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

		private URLClassLoader parent;


		private MemoryClassLoader(Map<String, byte[]> classBytes) {
			super(new URL[0], MemoryClassLoader.class.getClassLoader());
			this.classBytes.putAll(classBytes);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			byte[] buf = classBytes.get(name);
			if (buf == null) {
				return super.findClass(name);
			}
			classBytes.remove(name);
			return defineClass(name, buf, 0, buf.length);
		}



	}
}
