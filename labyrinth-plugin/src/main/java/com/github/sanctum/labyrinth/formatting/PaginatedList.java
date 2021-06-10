package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.library.MathUtils;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Encapsulate a list of objects to be sorted and paginated.
 *
 * @param <T> The object type representative of this pagination operation.
 * @author Hempfest
 */
public class PaginatedList<T> {
	private List<T> typeList;
	private Comparator<? super T> comparable;
	private ComponentCompliment<T> start;
	private ComponentCompliment<T> finish;
	private ComponentDecoration<T> decoration;
	private int linesPerPage;

	public PaginatedList(List<T> list) {
		this.typeList = list;
	}

	/**
	 * Format/trim a given amount to a specific length format.
	 *
	 * @param amount    The amount to format.
	 * @param precision The math precision to stop the decimal placing at.
	 * @return The newly formatted double.
	 */
	public double format(Number amount, int precision) {
		return MathUtils.use(amount).format(precision);
	}

	/**
	 * Setup a comparator for this list's sorting procedure.
	 *
	 * @param comparable The comparing operation to run for the given object type.
	 * @return The same paginated list procedure.
	 */
	public PaginatedList<T> compare(Comparator<? super T> comparable) {
		this.comparable = comparable;
		return this;
	}

	/**
	 * Provided the page number, total page count, object in queue & current paginated list instance,
	 * decorate any possible actions to take while iterating through the entries.
	 *
	 * @param decoration The primary execution to be ran for every entry given.
	 * @return The same paginated list procedure.
	 */
	public PaginatedList<T> decorate(ComponentDecoration<T> decoration) {
		this.decoration = decoration;
		return this;
	}

	/**
	 * Specify an amount of entries to be display per page.
	 * Lower entry counts will result in larger page counts.
	 *
	 * @param linesPerPage The amount of entries per page to display.
	 * @return The same paginated list procedure.
	 */
	public PaginatedList<T> limit(int linesPerPage) {
		this.linesPerPage = linesPerPage;
		return this;
	}

	/**
	 * Provided the page number and total page count, provide a starting
	 * sequence for the pagination.
	 *
	 * @param compliment The starting execution to be ran one time.
	 * @return The same paginated list procedure.
	 */
	public PaginatedList<T> start(StartingCompliment<T> compliment) {
		this.start = compliment;
		return this;
	}

	/**
	 * Provided the page number and total page count, provide a finishing
	 * sequence for the pagination.
	 *
	 * @param compliment The finishing execution to be ran one time.
	 * @return The same paginated list procedure.
	 */
	public PaginatedList<T> finish(FinishingCompliment<T> compliment) {
		this.finish = compliment;
		return this;
	}

	/**
	 * Separate un-wanted elements from the list.
	 *
	 * @param predicate The check to succeed before adding each element.
	 * @return The same paginated list procedure.
	 */
	public PaginatedList<T> filter(Predicate<? super T> predicate) {
		this.typeList = typeList.stream().filter(predicate).collect(Collectors.toList());
		return this;
	}

	/**
	 * Run all prior sorting arrangements and sequence operations for a specified page.
	 *
	 * @param pageNum The page to to collect.
	 * @return A list of collected objects from the sorting procedure.
	 */
	public List<T> get(int pageNum) {
		LinkedList<T> list = new LinkedList<>();
		int page = pageNum;

		int o = linesPerPage;

		List<T> tempList = new LinkedList<>(new LinkedHashSet<>(this.typeList));
		int totalPageCount = 1;
		if ((tempList.size() % o) == 0) {
			if (tempList.size() > 0) {
				totalPageCount = tempList.size() / o;
			}
		} else {
			totalPageCount = (tempList.size() / o) + 1;
		}

		if (page <= totalPageCount) {

			if (this.start != null) {
				this.start.apply(this, pageNum, totalPageCount);
			}

			if (!tempList.isEmpty()) {
				int i1 = 0, k = 0;
				page--;
				if (this.comparable != null) {
					tempList.sort(this.comparable);
				}
				LinkedList<T> sorted_list = new LinkedList<>(tempList);

				for (T value : sorted_list) {

					k++;
					if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
						i1++;
						if (decoration != null) {
							decoration.apply(this, value, pageNum, totalPageCount, k);
							list.add(value);
						}
					}
					tempList.remove(value);
				}
				if (this.finish != null) {
					this.finish.apply(this, pageNum, totalPageCount);
				}
			}
			// end line
		}
		return list;
	}

}
