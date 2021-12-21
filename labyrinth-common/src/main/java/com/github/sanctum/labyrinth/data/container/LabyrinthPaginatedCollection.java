package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.formatting.pagination.Page;
import com.github.sanctum.labyrinth.library.Deployable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

/**
 * This acts as a 'collection' of sorted and or filtered elements that have been categorized into {@link LabyrinthPage}'s
 * for ease of use with pagination.
 *
 * <p>You'll receive a collection of pages containing the prior adjusted elements in the <strong>exact</strong>
 * order they were received in. It is heavily implied that this collection follows synchronized linked ordering.</p>
 *
 * @param <T> The type of object this collection represents.
 * @see Collection for a more precise descriptive on the backing of this module click here.
 */
public abstract class LabyrinthPaginatedCollection<T> implements LabyrinthCollection<Page<T>> {

	protected final LabyrinthCollection<Page<T>> set = new LabyrinthSet<>();
	protected LabyrinthCollection<T> collection;
	protected int initialElementsPer = 8;
	protected boolean sorted;
	protected Comparator<? super T> comparator;
	protected Predicate<? super T> predicate;

	public LabyrinthPaginatedCollection(LabyrinthCollection<T> collection) {
		this.collection = collection;
	}

	@SafeVarargs
	public LabyrinthPaginatedCollection(T... t) {
		this.collection = new LabyrinthList<>(Arrays.asList(t));
	}

	@Note("It is noted that this collection is modifiable and empty and implied to the type casting")
	public static <T> LabyrinthPaginatedCollection<T> emptyCollection() {
		return new LabyrinthPaginatedCollection<T>() {
		};
	}

	@SafeVarargs
	@Note("It is noted that this collection is modifiable and contains the following elements")
	public static <T> LabyrinthPaginatedCollection<T> of(T... t) {
		return new LabyrinthPaginatedCollection<T>(t) {
		};
	}

	@Note("It is noted that this collection is modifiable and contains the following elements")
	public static <T> LabyrinthPaginatedCollection<T> of(LabyrinthCollection<T> collection) {
		return new LabyrinthPaginatedCollection<T>(collection) {
		};
	}

	/**
	 * Set the local comparator this pagination will use for calculation.
	 * This option will determine the loading order in which elements are met in sequence.
	 *
	 * @param comparator The comparator to use for the elements.
	 * @return The same abstract pagination.
	 */
	public LabyrinthPaginatedCollection<T> sort(Comparator<? super T> comparator) {
		this.comparator = comparator;
		return this;
	}

	/**
	 * Set the local filter this pagination will use for calculation.
	 * This option will determine the which objects to remove before final calculation.
	 *
	 * @param predicate the predicate to user for provided elements.
	 * @return The same abstract pagination.
	 */
	public LabyrinthPaginatedCollection<T> filter(Predicate<? super T> predicate) {
		this.predicate = predicate;
		return this;
	}

	/**
	 * Set the local limit for (non-enforced w/ modifiable pages) elements allowed per page.
	 *
	 * @param elementsPer The elements per page to allow.
	 * @return The same abstract pagination.
	 */
	public LabyrinthPaginatedCollection<T> limit(int elementsPer) {
		this.initialElementsPer = elementsPer;
		return this;
	}

	/**
	 * Completely reset and re-order the backing page collection while un-affecting the provided <strong>element</strong> collection.
	 *
	 * @return A deployable reordering operation.
	 */
	public Deployable<LabyrinthPaginatedCollection<T>> reorder() {
		return Deployable.of(this, collection1 -> {
			collection1.sorted = true;
			LabyrinthCollection<Page<T>> toAdd = new LabyrinthSet<>();
			LabyrinthCollection<T> toSort = new LabyrinthList<>();

			if (collection1.predicate != null) {
				toSort = collection1.collection.stream().filter(collection1.predicate).collect(LabyrinthCollectors.toList());
			}
			if (collection1.comparator != null) {
				if (toSort.isEmpty()) {
					toSort = collection1.collection.stream().sorted(collection1.comparator).collect(LabyrinthCollectors.toList());
				} else {
					toSort = toSort.stream().sorted(collection1.comparator).collect(LabyrinthCollectors.toList());
				}
			}
			if (toSort.isEmpty()) {
				toSort = new LabyrinthList<>(collection1.collection);
			}
			collection1.collection = toSort;

			int totalPageCount = collection1.size();
			for (int slot = 1; slot < totalPageCount + 1; slot++) {
				int page = slot;
				LabyrinthPage<T> newPage = new LabyrinthPage<>(collection1, slot);
				if (page <= totalPageCount) {

					if (!toSort.isEmpty()) {
						int placeholder = 0, index = 0;
						page--;
						for (T value : toSort) {

							index++;
							if ((((page * collection1.initialElementsPer) + placeholder + 1) == index) && (index != ((page * collection1.initialElementsPer) + collection1.initialElementsPer + 1))) {
								placeholder++;
								newPage.add(value);
							}
						}
					}
					// end line
				}
				toAdd.add(newPage);
			}
			collection1.set.addAll(toAdd);
		});
	}

	public @NotNull LabyrinthCollection<Page<T>> getPages() {
		return ImmutableLabyrinthCollection.of(set);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int size() {
		int totalPageCount = 1;
		if ((this.collection.size() % initialElementsPer) == 0) {
			if (this.collection.size() > 0) {
				totalPageCount = this.collection.size() / initialElementsPer;
			}
		} else {
			totalPageCount = (this.collection.size() / initialElementsPer) + 1;
		}
		return totalPageCount;
	}

	/**
	 * Check if this pagination has been sorted yet.
	 *
	 * @return true if this pagination has already been sorted.
	 */
	public boolean isSorted() {
		return sorted;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean isEmpty() {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.isEmpty();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Note("Use this method to also check if this collection contains a specific page!")
	public boolean contains(Page<T> o) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.contains(o);
	}

	/**
	 * @inheritDoc
	 */
	@NotNull
	@Override
	public Iterator<Page<T>> iterator() {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.stream().sorted(Comparator.comparingInt(Page::getNumber)).iterator();
	}

	/**
	 * @inheritDoc
	 */
	public Object[] toArray() {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.stream().sorted(Comparator.comparingInt(Page::getNumber)).toArray(Object[]::new);
	}


	/**
	 * @inheritDoc
	 */
	public <R> @NotNull R[] toArray(R[] a) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return ((LabyrinthCollectionBase<T>) set).toArray(a);
	}

	/**
	 * Get a page from this collection.
	 *
	 * @param index The page to get.
	 * @return An existing page or a new one skipping nullity.
	 */
	@Note("This works differently than the normal get index method! Make sure you call AbstractPaginatedCollection#contains(Object) first on the index or 'page' you want.")
	public @NotNull Page<T> get(int index) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.stream().sorted(Comparator.comparingInt(Page::getNumber)).filter(p -> p.getNumber() == index).findFirst().orElseGet(() -> {
			Page<T> newOne = new LabyrinthPage<>(this, index);
			add(newOne);
			return newOne;
		});
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Note("Add a custom page to this pagination collection")
	public boolean add(Page<T> tPage) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.add(tPage);
	}

	/**
	 * @see LabyrinthPaginatedCollection#add(Page)
	 */
	@Note("Add a custom page to this pagination collection")
	public boolean add(Consumer<Page<T>> consumer, int page) {
		Page<T> newIn = new LabyrinthPage<>(this, page);
		consumer.accept(newIn);
		add(newIn);
		return true;
	}

	/**
	 * Add an element to this container on a specific page.
	 *
	 * @param t    The element to add
	 * @param page The page to add the element to
	 * @return true if the element was added.
	 */
	public boolean add(T t, int page) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		Page<T> test = get(page);
		if (test != null) {
			return test.add(t);
		}
		LabyrinthPage<T> p = new LabyrinthPage<>(this, page);
		p.add(t);
		add(p);
		return true;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Note("Add remove a page from this pagination collection")
	public boolean remove(Page<T> o) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.remove(o);
	}

	/**
	 * Remove an element from this container by its identity and page.
	 *
	 * @param t    The element to add
	 * @param page the page to remove the element from.
	 * @return true if this element was removed.
	 */
	public boolean remove(T t, int page) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		Page<T> test = get(page);
		return test.remove(t);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean containsAll(@NotNull Iterable<Page<T>> c) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.containsAll(c);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean addAll(@NotNull Iterable<Page<T>> c) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.addAll(c);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean removeAll(@NotNull Iterable<Page<T>> c) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.removeAll(c);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void clear() {
		set.clear();
	}

	static final class LabyrinthPage<T> implements Page<T> {

		private final LabyrinthPaginatedCollection<T> parent;
		private LabyrinthCollection<T> collection;
		private final int page;

		LabyrinthPage(LabyrinthPaginatedCollection<T> parent, int number) {
			this.parent = parent;
			this.collection = new LabyrinthList<>();
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
		public int size() {
			return collection.size();
		}

		@Override
		public T get(int index) {
			return collection.get(index);
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
		public @NotNull Iterator<T> iterator() {
			return collection.iterator();
		}

		@Override
		public Deployable<Page<T>> reorder() {
			return Deployable.of(this, ts -> {
				LabyrinthCollection<T> copy = collection;
				if (parent.predicate != null) {
					copy = collection.stream().filter(parent.predicate).collect(LabyrinthCollectors.toList());
				}
				if (parent.comparator != null) {
					copy = copy.stream().sorted(parent.comparator).collect(LabyrinthCollectors.toSet());
				}
				collection.clear();
				collection = copy;
			});
		}

	}
}
