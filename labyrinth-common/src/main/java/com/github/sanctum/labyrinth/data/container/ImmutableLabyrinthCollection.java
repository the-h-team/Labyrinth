package com.github.sanctum.labyrinth.data.container;

/**
 * Represents a {@link LabyrinthCollection} that cannot be modified only read from.
 *
 * @param <K> The type of collection this is.
 */
public abstract class ImmutableLabyrinthCollection<K> extends LabyrinthCollectionBase<K> {

	ImmutableLabyrinthCollection(Iterable<K> assortment) {
		for (K k : assortment) {
			super.add(k);
		}
	}

	@Override
	public boolean add(K k) {
		throw warning();
	}

	@Override
	public boolean addAll(Iterable<K> iterable) {
		throw warning();
	}

	@Override
	public boolean remove(K k) {
		throw warning();
	}

	@Override
	public boolean removeAll(Iterable<K> iterable) {
		throw warning();
	}

	@Override
	public void clear() {
		throw warning();
	}

	RuntimeException warning() {
		return new ImmutableStorageException("Element modifications cannot be made to immutable collections!");
	}

	public static <K> ImmutableLabyrinthCollection<K> of(Iterable<K> assortment) {
		return new ImmutableLabyrinthCollection<K>(assortment) {
		};
	}

	public static <K> ImmutableLabyrinthCollection.Builder<K> builder() {
		return new ImmutableLabyrinthCollection.Builder<>();
	}

	public static final class Builder<K> {

		private final LabyrinthCollection<K> internal;

		Builder() {
			internal = new LabyrinthList<>();
		}

		public Builder<K> add(K key) {
			internal.add(key);
			return this;
		}

		public ImmutableLabyrinthCollection<K> build() {
			return of(internal);
		}

	}

}
