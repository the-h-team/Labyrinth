package com.github.sanctum.labyrinth.data.container;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class IsolatedClassLoader extends URLClassLoader {

	/**
	 * This constructor is used to set the parent ClassLoader
	 */
	public IsolatedClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}


	public void addURL(URL url) {
		super.addURL(url);
	}

	/**
	 * Loads the class from the file system. The class file should be located in
	 * the file system. The name should be relative to get the file location
	 *
	 * @param name Fully Classified name of the class, for example, com.journaldev.Foo
	 */
	private Class<?> getClass(String name) {
		String file = name.replace('.', File.separatorChar) + ".class";
		byte[] b = null;
		try {
			// This loads the byte code data from the file
			b = loadClassFileData(file);
			// defineClass is inherited from the ClassLoader class
			// that converts byte array into a Class. defineClass is Final
			// so we cannot override it
			Class<?> c = defineClass(name, b, 0, b.length);
			resolveClass(c);
			return c;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Every request for a class passes through this method. If the class is in
	 * com.journaldev package, we will use this classloader or else delegate the
	 * request to parent classloader.
	 *
	 * @param name Full class name
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		System.out.println("Loading Class '" + name + "'");
		if (name.startsWith("com.github.sanctum")) {
			System.out.println("Loading Class using CCLoader");
			return getClass(name);
		}
		return super.loadClass(name);
	}

	/**
	 * Reads the file (.class) into a byte array. The file should be
	 * accessible as a resource and make sure that it's not in Classpath to avoid
	 * any confusion.
	 *
	 * @param name Filename
	 * @return Byte array read from the file
	 * @throws IOException if an exception comes in reading the file
	 */
	private byte[] loadClassFileData(String name) throws IOException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream(
				name);
		int size = stream.available();
		byte[] buff = new byte[size];
		DataInputStream in = new DataInputStream(stream);
		in.readFully(buff);
		in.close();
		return buff;
	}
}