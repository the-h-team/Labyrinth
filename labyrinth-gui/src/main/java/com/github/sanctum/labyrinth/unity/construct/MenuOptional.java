package com.github.sanctum.labyrinth.unity.construct;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MenuOptional<T extends Menu> {

	private T value = null;

	private Class<T> tClass;

	protected MenuOptional(T value) {
		if (value != null) {
			this.value = value;
		}
	}

	public T get() {
		return value;
	}

	public MenuOptional<T> supplyClass(Class<T> tClass) {
		this.tClass = tClass;
		return this;
	}

	public T orElse(T other) {
		return other;
	}

	public T orElseGet(Supplier<T> supplier) {
		return supplier.get();
	}

	public T orElseMake(Consumer<Menu.Builder<T>> builder) {
		if (this.value != null) return this.value;
		Menu.Builder<T> b = new Menu.Builder<>(this.tClass);
		builder.accept(b);
		return b.initialize();
	}

	public static <T extends Menu> MenuOptional<T> ofNullable(T value) {
		return new MenuOptional<>(value);
	}

}
