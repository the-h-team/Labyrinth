package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.library.Deployable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

final class ParsableAccessibleConstants extends AccessibleConstants<Object> {

	private final Class<?> t;
	private final List<Constant<?>> list = new ArrayList<>();

	ParsableAccessibleConstants(Type type) {
		t = TypeToken.get(type).getRawType();
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
		});
	}

	@Override
	public Deployable<Map<Class<?>, List<Object>>> resolve() {
		final Map<Class<?>, List<Object>> objects = new HashMap<>();
		return Deployable.of(objects, objects1 -> {
			objects1.clear();
			this.list.clear();
			for (Field f : t.getFields()) {
				int modifiers = f.getModifiers();
				if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
					try {
						Object o = f.get(null);
						if (o != null) {
							this.list.add(new Constant<Object>() {
								@Override
								public String getName() {
									return f.getName();
								}

								@Override
								public Class<Object> getType() {
									return (Class<Object>) o.getClass();
								}

								@Override
								public Class<?> getParent() {
									return t;
								}

								@Override
								public Object getValue() {
									return o;
								}
							});
							if (objects.get(o.getClass()) != null) {
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
		});
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
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public Constant<?> get(String name) {
		if (list.isEmpty()) {
			resolve().deploy();
		}
		return list.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
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

	@NotNull
	@Override
	public Iterator<Constant<Object>> iterator() {
		Deployable<Map<Class<?>, List<Object>>> deployable = resolve();
		if (list.isEmpty()) {
			deployable.deploy();
		}
		return this.list.stream().map(constant -> (Constant<Object>)constant).collect(Collectors.toList()).iterator();
	}

	@Override
	public void forEach(Consumer<? super Constant<Object>> action) {
		Deployable<Map<Class<?>, List<Object>>> deployable = resolve();
		if (list.isEmpty()) {
			deployable.deploy();
		}
		this.list.stream().map(constant -> (Constant<Object>)constant).collect(Collectors.toList()).forEach(action);
	}

	@Override
	public Spliterator<Constant<Object>> spliterator() {
		Deployable<Map<Class<?>, List<Object>>> deployable = resolve();
		if (list.isEmpty()) {
			deployable.deploy();
		}
		return this.list.stream().map(constant -> (Constant<Object>)constant).collect(Collectors.toList()).spliterator();
	}
}
