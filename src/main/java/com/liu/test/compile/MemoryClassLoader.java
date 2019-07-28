package com.liu.test.compile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Load class from byte[] which is compiled in memory.
 * 
 * @author michael
 */
class MemoryClassLoader extends URLClassLoader {

	// class name to class bytes:
	Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

	URLClassLoader parent;


	public MemoryClassLoader(Map<String, byte[]> classBytes, ClassLoader parent) {
		super(new URL[0], parent);
//		LaunchedURLClassLoader launchedURLClassLoader = SpringUtils.getBean("mileageService")
//		EngineClassLoader engineClassLoader = new EngineClassLoader(launchedURLClassLoader);
		this.classBytes.putAll(classBytes);
//		this.parent = (URLClassLoader)parent;
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

//	@Override
//	public URL findResource(String name) {
//		return parent.findResource(name);
//	}
//
//	@Override
//	public Enumeration<URL> findResources(String name) throws IOException {
//		return parent.getResources(name);
//	}

//	@Override
//	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//		try {
//			System.out.println(parent.toString());
//			Method loadClass = parent.getClass().getDeclaredMethod("loadClass", String.class, boolean.class);
//			loadClass.setAccessible(true);
//			//System.out.println(loadClass.toString());
//			return (Class<?>) loadClass.invoke(parent, name, resolve);
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}


}
