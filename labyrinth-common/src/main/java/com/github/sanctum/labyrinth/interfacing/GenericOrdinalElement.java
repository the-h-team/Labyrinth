package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.library.TypeFlag;
import org.intellij.lang.annotations.MagicConstant;

public final class GenericOrdinalElement implements OrdinalElement<Object> {

	private final Object o;

	GenericOrdinalElement(Object o) {
		this.o = o;
	}

	public <R> R cast(@MagicConstant(valuesFromClass = TypeFlag.class) TypeFlag<R> flag) {
		return flag.getType().cast(getElement());
	}

	@Override
	public Object getElement() {
		return o;
	}

	@Override
	public GenericOrdinalElement select(int ordinal) {
		return OrdinalProcedure.select(getElement(), ordinal);
	}

	@Override
	public GenericOrdinalElement select(int ordinal, Object... args) {
		return OrdinalProcedure.select(getElement(), ordinal, args);
	}

	@Override
	public String toString() {
		return getElement().toString();
	}
}
