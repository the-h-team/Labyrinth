package com.github.sanctum.labyrinth.task;

import com.github.sanctum.panther.util.Deployable;
import com.github.sanctum.panther.util.TypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface Procedure<T> {

	Procedure<T> next(Consumer<T> action);

	Deployable<Void> run(T source);

	long getLastExecuted();

	TypeAdapter<T> getType();

	static <T> Procedure<T> request(TypeAdapter<T> flag) {
		return new Procedure<T>() {

			private final List<Consumer<T>> actions = new ArrayList<>();
			private long used = 0L;

			@Override
			public TypeAdapter<T> getType() {
				return flag;
			}

			@Override
			public Procedure<T> next(Consumer<T> action) {
				actions.add(action);
				return this;
			}

			@Override
			public Deployable<Void> run(T source) {
				return Deployable.of(() -> {
					actions.forEach(a -> a.accept(source));
					this.used = System.currentTimeMillis();
				}, 0);
			}

			@Override
			public long getLastExecuted() {
				return this.used;
			}
		};
	}

}
