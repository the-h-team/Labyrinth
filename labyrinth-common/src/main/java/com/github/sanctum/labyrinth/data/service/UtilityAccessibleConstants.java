package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.panther.util.Deployable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

final class UtilityAccessibleConstants<T> extends AccessibleConstants<T> {

	private final Class<T> t;
	private final List<Constant<T>> list = new ArrayList<>();

	UtilityAccessibleConstants(Class<T> type) {
		t = type;
	}

	@Override
	public Deployable<Field[]> debug() {
		return Deployable.of(t.getFields(), fields -> {
			for (Field f : fields) {
				int modifiers = f.getModifiers();
				if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers)) {

					try {
						Object o = f.get(null);
						Bukkit.getLogger().info("- got field " + f.getName() + " type:" + o.getClass().getSimpleName());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0);
	}

	@Override
	public Deployable<Map<Class<?>, List<Object>>> resolve() {
		final Map<Class<?>, List<Object>> objects = new HashMap<>();
		return Deployable.of(objects, objects1 -> {
			objects1.clear();
			for (Field f : t.getFields()) {
				int modifiers = f.getModifiers();
				if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
					try {
						Object o = f.get(null);
						if (o != null) {
							if (objects1.get(o.getClass()) != null) {
								objects1.get(o.getClass()).add(o);
							} else {
								objects1.put(o.getClass(), new ArrayList<>(Collections.singletonList(o)));
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}, 0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <O> Deployable<List<O>> resolve(Class<O> c) {
		final List<O> objects = new ArrayList<>();
		return Deployable.of(objects, objects1 -> {
			objects1.clear();
			this.list.clear();
			for (Field f : t.getFields()) {
				int modifiers = f.getModifiers();
				if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
					try {
						Object o = f.get(null);
						if (o != null) {
							if (c.isAssignableFrom(o.getClass())) {
								objects1.add((O) o);
								this.list.add(new Constant<T>() {
									@Override
									public String getName() {
										return f.getName();
									}

									@Override
									public Class<T> getType() {
										return (Class<T>) o.getClass();
									}

									@Override
									public Class<?> getParent() {
										return t;
									}

									@Override
									public T getValue() {
										return (T) o;
									}
								});
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}, 0);
	}

	@Override
	public <O> Deployable<List<Constant<O>>> get(Class<O> c) {
		final List<Constant<O>> objects = new ArrayList<>();
		return Deployable.of(objects, objects1 -> {
			objects1.clear();
			for (Field f : t.getFields()) {
				int modifiers = f.getModifiers();
				if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
					try {
						Object o = f.get(null);
						if (o != null) {
							if (c.isAssignableFrom(o.getClass())) {
								objects1.add(new Constant<O>() {
									@Override
									public String getName() {
										return f.getName();
									}

									@Override
									public Class<O> getType() {
										return c;
									}

									@Override
									public Class<?> getParent() {
										return t;
									}

									@Override
									public O getValue() {
										return (O) o;
									}
								});
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}, 0);
	}

	@Override
	public int count() {
		return (int) Arrays.stream(t.getFields()).filter(f -> {
			int modifiers = f.getModifiers();
			return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
		}).map(field -> {
			try {
				return field.get(null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(o -> {
			if (o == null) return false;
			return t.isAssignableFrom(o.getClass());
		}).count();
	}

	@Override
	public Constant<T> get(String name) {
		if (list.isEmpty()) {
			resolve().deploy();
		}
		return list.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
	}

	@NotNull
	@Override
	public Iterator<Constant<T>> iterator() {
		Deployable<List<T>> deployable = resolve(t);
		if (list.isEmpty()) {
			deployable.deploy();
		}
		return this.list.iterator();
	}

	@Override
	public void forEach(Consumer<? super Constant<T>> action) {
		Deployable<List<T>> deployable = resolve(t);
		if (list.isEmpty()) {
			deployable.deploy();
		}
		this.list.forEach(action);
	}

	@Override
	public Spliterator<Constant<T>> spliterator() {
		Deployable<List<T>> deployable = resolve(t);
		if (list.isEmpty()) {
			deployable.deploy();
		}
		return this.list.spliterator();
	}
}
