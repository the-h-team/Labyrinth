package com.github.sanctum.labyrinth.data.container;

/**
 * Represents a {@link LabyrinthCollection} that cannot be modified only read from.
 *
 * @param <K> The type of collection this is.
 */
public abstract class ImmutableLabyrinthCollection<K> extends LabyrinthCollectionBase<K> {

	ImmutableLabyrinthCollection(Iterable<K> assortment) {
		super.addAll(assortment);
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

}
