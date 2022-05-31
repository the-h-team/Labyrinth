package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.library.TypeFlag;
import java.util.List;
import org.intellij.lang.annotations.MagicConstant;

public interface OrdinalResult<E> {

	default <R> R cast(@MagicConstant(valuesFromClass = TypeFlag.class) TypeFlag<R> flag) {
		return flag.getType().cast(get().getElement());
	}

	OrdinalElement<E> get();

	List<OrdinalElement<E>> getAll();

	static <E> OrdinalResult<E> of(OrdinalElement<E> e) {
		return new OrdinalResult<E>() {
			@Override
			public OrdinalElement<E> get() {
				return e;
			}

			@Override
			public List<OrdinalElement<E>> getAll() {
				return null;
			}

		};
	}

	static <E, T extends OrdinalElement<E>> OrdinalResult<E> of(List<T> es) {
		return new OrdinalResult<E>() {
			@Override
			public OrdinalElement<E> get() {
				return null;
			}

			@Override
			public List<OrdinalElement<E>> getAll() {
				return (List<OrdinalElement<E>>) es;
			}
		};
	}

}
