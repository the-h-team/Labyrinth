package com.github.sanctum.labyrinth.unity.construct;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class MenuOptional<T extends Menu> {

	private T value = null;

	private final Class<T> tClass;

	private MenuOptional() {
		this.tClass = (Class<T>) Menu.class;
	}

	protected MenuOptional(Class<T> path, T value) {
		this.tClass = path;
		if (value != null) {
			this.value = value;
		}
	}

	public static <T extends Menu> MenuOptional<T> empty() {
		return new MenuOptional<>();
	}

	public static <T extends Menu> MenuOptional<T> ofNullable(Class<T> path, T value) {
		if (path == null && value != null) path = (Class<T>) value.getClass();
		return value == null ? empty() : new MenuOptional<>(path, value);
	}

	public boolean isPresent() {
		return this.value != null;
	}

	public <R extends Menu> R or(R other) {
		return other;
	}

	public <R extends Menu> R or(Supplier<R> other) {
		return other.get();
	}

	public T orElseMake(Consumer<Menu.Builder<T>> builder) {
		if (this.value != null) return this.value;
		Menu.Builder<T> b = new Menu.Builder<>(this.tClass);
		builder.accept(b);
		return b.initialize();
	}

	public T get() {
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	public boolean ifPresent(Consumer<? super T> consumer) {
		if (value != null) {
			consumer.accept(value);
			return true;
		}
		return false;
	}

	public MenuOptional<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);
		if (!isPresent())
			return this;
		else
			return predicate.test(value) ? this : empty();
	}

	public <U extends Menu> MenuOptional<U> map(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		if (!isPresent())
			return empty();
		else {
			return ofNullable(null, mapper.apply(value));
		}
	}

	public <U extends Menu> MenuOptional<U> flatMap(Function<? super T, MenuOptional<U>> mapper) {
		Objects.requireNonNull(mapper);
		if (!isPresent())
			return empty();
		else {
			return Objects.requireNonNull(mapper.apply(value));
		}
	}

	public T orElse(T other) {
		return value != null ? value : other;
	}

	public T orElseGet(Supplier<? extends T> other) {
		return value != null ? value : other.get();
	}

	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (value != null) {
			return value;
		} else {
			throw exceptionSupplier.get();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof MenuOptional)) {
			return false;
		}

		MenuOptional<?> other = (MenuOptional<?>) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	public String toString() {
		return value != null
				? String.format("Optional[%s]", value)
				: "Optional.empty";
	}


}
