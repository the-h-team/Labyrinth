package com.github.sanctum.labyrinth.data;

import org.jetbrains.annotations.NotNull;

public interface SimpleKeyedValue<K, V> {

	K getKey();

	V getValue();

	static <K, V> @NotNull SimpleKeyedValue<K, V> of(K k, V v) {
		return new SimpleKeyedValue<K, V>() {
			@Override
			public K getKey() {
				return k;
			}

			@Override
			public V getValue() {
				return v;
			}
		};
	}

}
