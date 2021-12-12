package com.github.sanctum.labyrinth.formatting.pagination;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.library.Deployable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * A page of elements retaining the order they were added in.
 *
 * @param <T> The type of elements this page contains.
 */
public interface Page<T> extends Iterable<T> {

	/**
	 * Check if this page is modifiable.
	 *
	 * @return true if this page can have elements be removed/added
	 */
	boolean isModifiable();

	/**
	 * Get this page as its number.
	 *
	 * @return the number representation for this page.
	 */
	int getNumber();

	/**
	 * Add an element to this page if it's modifiable.
	 *
	 * @param t The element to add.
	 * @return true if the element was added.
	 */
	boolean add(T t);

	/**
	 * Remove an element from this page if it's modifiable.
	 *
	 * @param t The element to remove
	 * @return true if the element was removed.
	 */
	boolean remove(T t);

	/**
	 * Check if this page contains a specific element.
	 *
	 * @param t The element to check existence for.
	 * @return true if this page contains the specified element.
	 */
	boolean contains(T t);

	/**
	 * Get all the elements within this page.
	 *
	 * @return All the elements within this page.
	 */
	@Deprecated
	@Note("Generic array creation not allowed!")
	T[] getContents();

	Collection<T> getContent();

	/**
	 * Reorder this specific page by its parents' filtration sequences.
	 *
	 * <p>The use of this will allow you to delve further and apply ordering on specific pages but
	 * at the same time making it mess up the <strong>over-all</strong> ordering, if that's the case
	 * this can easily be fixed by re-instating the method {@link AbstractPaginatedCollection#reorder()} on the parent of this page.</p>
	 *
	 * @return The same page reordered.
	 */
	@Note("This is an optional method, you should nether need to use it but instead call the main AbstractPaginatedCollecction#reorder() method.")
	Deployable<Page<T>> reorder();

	@NotNull
	@Override
	default Iterator<T> iterator() {
		return Arrays.asList(getContents()).iterator();
	}

	default boolean isEmpty() {
		return getContents().length == 0;
	}

	class Impl<T> implements Page<T> {

		private final AbstractPaginatedCollection<T> parent;
		private Collection<T> collection;
		private final int page;

		public Impl(AbstractPaginatedCollection<T> parent, int number) {
			this.parent = parent;
			this.collection = Collections.synchronizedList(new LinkedList<>());
			this.page = number;
		}

		@Override
		@Note("Labyrinth impl is modifiable by default. (elements can be added)")
		public boolean isModifiable() {
			return true;
		}

		@Override
		public int getNumber() {
			return this.page;
		}

		@Override
		public boolean add(T t) {
			if (!isModifiable()) return false;
			return collection.add(t);
		}

		@Override
		public boolean remove(T t) {
			if (!isModifiable()) return false;
			return collection.remove(t);
		}

		@Override
		public boolean contains(T t) {
			return collection.contains(t);
		}

		@Override
		public T[] getContents() {
			Class<?> cl = null;
			for (T t : this) {
				cl = t.getClass();
				break;
			}
			Object[] ar = (Object[]) Array.newInstance(cl, collection.size());
			int i = 0;
			for (T t : this) {
				ar[i] = t;
				i++;
			}
			return (T[]) ar;
		}

		@Override
		public Collection<T> getContent() {
			return Collections.unmodifiableCollection(this.collection);
		}

		@Override
		public @NotNull Iterator<T> iterator() {
			return collection.iterator();
		}

		@Override
		public Deployable<Page<T>> reorder() {
			return Deployable.of(this, ts -> {
				Collection<T> copy = collection;
				if (parent.predicate != null) {
					copy = collection.stream().filter(parent.predicate).collect(Collectors.toList());
				}
				if (parent.comparator != null) {
					copy = copy.stream().sorted(parent.comparator).collect(Collectors.toCollection(LinkedHashSet::new));
				}
				collection.clear();
				collection = copy;
			});
		}

	}

}
