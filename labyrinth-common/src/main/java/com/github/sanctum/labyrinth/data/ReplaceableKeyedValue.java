package com.github.sanctum.labyrinth.data;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface ReplaceableKeyedValue<K, V> extends SimpleKeyedValue<K, V>, Map.Entry<K, V> {

	@Override
	V setValue(V value);

	static <K,V> @NotNull ReplaceableKeyedValue<K, V> of(K key, V value) {
		return new ReplaceableKeyedValue<K, V>() {

			private final K k;
			private V v;

			{
				this.k = key;
				this.v = value;
			}

			@Override
			public V setValue(V value) {
				return (this.v = value);
			}

			@Override
			public K getKey() {
				return this.k;
			}

			@Override
			public V getValue() {
				return this.v;
			}
		};
	}

}
