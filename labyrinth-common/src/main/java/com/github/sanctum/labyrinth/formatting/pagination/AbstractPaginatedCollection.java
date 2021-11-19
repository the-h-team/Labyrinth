package com.github.sanctum.labyrinth.formatting.pagination;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.library.Deployable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * This acts as a 'collection' of sorted and or filtered elements that have been categorized into {@link Page}'s
 * for ease of use with pagination.
 *
 * <p>You'll receive a collection of pages containing the prior adjusted elements in the <strong>exact</strong>
 * order they were received in. It is heavily implied that this collection follows synchronized linked ordering.</p>
 *
 * @see Collection for a more precise descriptive on the backing of this module click here.
 * @param <T> The type of object this collection represents.
 */
public abstract class AbstractPaginatedCollection<T> implements Collection<Page<T>> {

	protected final Set<Page<T>> set = Collections.synchronizedSet(new LinkedHashSet<>());
	protected Collection<T> collection;
	protected int initialElementsPer = 8;
	protected boolean isChanged;
	protected Comparator<? super T> comparator;
	protected Predicate<? super T> predicate;

	protected AbstractPaginatedCollection(Collection<T> collection) {
		this.collection = collection;
	}

	@SafeVarargs
	public AbstractPaginatedCollection(T... t) {
		this.collection = Arrays.asList(t);
	}

	@Note("It is noted that this collection is modifiable and empty and implied to the type casting")
	public static <T> AbstractPaginatedCollection<T> emptyCollection() {
		return new LabyrinthPagination<>();
	}

	@SafeVarargs
	@Note("It is noted that this collection is modifiable and contains the following elements")
	public static <T> AbstractPaginatedCollection<T> of(T... t) {
		return new LabyrinthPagination<>(t);
	}

	@Note("It is noted that this collection is modifiable and contains the following elements")
	public static <T> AbstractPaginatedCollection<T> of(Collection<T> collection) {
		return new LabyrinthPagination<>(collection);
	}

	/**
	 * Set the local comparator this pagination will use for calculation.
	 * This option will determine the loading order in which elements are met in sequence.
	 *
	 * @param comparator The comparator to use for the elements.
	 * @return The same abstract pagination.
	 */
	public AbstractPaginatedCollection<T> sort(Comparator<? super T> comparator) {
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
	public AbstractPaginatedCollection<T> filter(Predicate<? super T> predicate) {
		this.predicate = predicate;
		return this;
	}

	/**
	 * Set the local limit for (non-enforced w/ modifiable pages) elements allowed per page.
	 *
	 * @param elementsPer The elements per page to allow.
	 * @return The same abstract pagination.
	 */
	public AbstractPaginatedCollection<T> limit(int elementsPer) {
		this.initialElementsPer = elementsPer;
		return this;
	}

	/**
	 * Completely reset and re-order the backing page collection while un-affecting the provided <strong>element</strong> collection.
	 *
	 * @return A deployable reordering operation.
	 */
	public Deployable<AbstractPaginatedCollection<T>> reorder() {
		return Deployable.of(this, collection1 -> {
			collection1.isChanged = true;
			Set<Page<T>> toAdd = new HashSet<>();
			List<T> toSort = new ArrayList<>();

			if (collection1.predicate != null) {
				toSort = collection1.collection.stream().filter(collection1.predicate).collect(Collectors.toList());
			}
			if (collection1.comparator != null) {
				if (toSort.isEmpty()) {
					toSort = collection1.collection.stream().sorted(collection1.comparator).collect(Collectors.toList());
				} else {
					toSort = toSort.stream().sorted(collection1.comparator).collect(Collectors.toList());
				}
			}
			collection1.collection = toSort;

			int totalPageCount = collection1.size();
			for (int slot = 1; slot < totalPageCount + 1; slot++) {
				int page = slot;
				Page<T> newPage = new Page.Impl<>(collection1, slot);
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

	public @NotNull Set<Page<T>> getPages() {
		return Collections.unmodifiableSet(set);
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
		return isChanged;
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
	public boolean contains(Object o) {
		if (o instanceof Integer) {
			return set.stream().filter(ts -> ts.getNumber() == ((int)o)).findFirst().orElse(null) != null;
		}
		if (!(o instanceof Page)) return false;
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
	@Override
	public Object[] toArray() {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.stream().sorted(Comparator.comparingInt(Page::getNumber)).toArray(Object[]::new);
	}


	/**
	 * @inheritDoc
	 */
	@Override
	public <R> @NotNull R[] toArray(R[] a) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.toArray(a);
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
			Page<T> newOne = new Page.Impl<>(this, index);
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
	 * @see AbstractPaginatedCollection#add(Page) 
	 */
	@Note("Add a custom page to this pagination collection")
	public boolean add(Consumer<Page<T>> consumer, int page) {
		Page<T> newIn = new Page.Impl<>(this, page);
		consumer.accept(newIn);
		add(newIn);
		return true;
	}

	/**
	 * Add an element to this container on a specific page.
	 *
	 * @param t The element to add
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
		Page<T> p = new Page.Impl<>(this, page);
		p.add(t);
		add(p);
		return true;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Note("Add remove a page from this pagination collection")
	public boolean remove(Object o) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.remove(o);
	}

	/**
	 * Remove an element from this container by its identity and page.
	 *
	 * @param t The element to add
	 * @param page the page to remove the element from.
	 * @return true if this element was removed.
	 */
	public boolean remove(T t, int page) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		Page<T> test = get(page);
		if (test != null) {
			return test.remove(t);
		}
		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.containsAll(c);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean addAll(@NotNull Collection<? extends Page<T>> c) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.addAll(c);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.removeAll(c);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		if (set.isEmpty() && !collection.isEmpty()) {
			reorder().deploy().submit().join();
		}
		return set.retainAll(c);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void clear() {
		set.clear();
	}
}
