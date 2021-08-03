package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.library.MathUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;

/**
 * Encapsulate a list of objects to be sorted and paginated.
 *
 * @param <T> the object type representative of this pagination operation
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
		this.typeList = new LinkedList<>(new LinkedHashSet<>(list));
	}

	public PaginatedList(Set<T> set) {
		this.typeList = new LinkedList<>(set);
	}

	/**
	 * Format/trim a given amount to a specific length format.
	 *
	 * @param amount    the amount to format
	 * @param precision the math precision to stop the decimal placing at
	 * @return the newly formatted double
	 */
	public double format(Number amount, int precision) {
		return MathUtils.use(amount).format(precision);
	}

	/**
	 * Setup a comparator for this list's sorting procedure.
	 *
	 * @param comparable the comparing operation to run for the given object type
	 * @return this paginated list procedure
	 */
	public PaginatedList<T> compare(Comparator<? super T> comparable) {
		this.comparable = comparable;
		return this;
	}

	/**
	 * Provided the page number, total page count, object in queue and
	 * current paginated list instance; decorate any possible actions
	 * to take while iterating through the entries.
	 *
	 * @param decoration the primary execution to be ran for every entry given
	 * @return this paginated list procedure
	 */
	public PaginatedList<T> decorate(ComponentDecoration<T> decoration) {
		this.decoration = decoration;
		return this;
	}

	/**
	 * Specify an amount of entries to be display per page.
	 * <p>
	 * Lower entry counts will result in larger page counts.
	 *
	 * @param linesPerPage the amount of entries per page to display
	 * @return this paginated list procedure
	 */
	public PaginatedList<T> limit(int linesPerPage) {
		this.linesPerPage = linesPerPage;
		return this;
	}

	/**
	 * Provided the page number and total page count, provide a starting
	 * sequence for the pagination.
	 *
	 * @param compliment the starting execution to be run once
	 * @return this paginated list procedure
	 */
	public PaginatedList<T> start(StartingCompliment<T> compliment) {
		this.start = compliment;
		return this;
	}

	/**
	 * Provided the page number and total page count,
	 * describe a finishing sequence for the pagination.
	 *
	 * @param compliment the finishing execution to be run once
	 * @return this paginated list procedure
	 */
	public PaginatedList<T> finish(FinishingCompliment<T> compliment) {
		this.finish = compliment;
		return this;
	}

	/**
	 * Provided the page number and total page count, describe a finishing
	 * sequence for the pagination (so the desired player can browse pages).
	 *
	 * @param builderConsumer the finishing execution to be run once
	 * @return this paginated list procedure
	 */
	public PaginatedList<T> finish(Consumer<PrintedPaginationBuilder> builderConsumer) {
		this.finish = (list, page, max) -> {

			PrintedPaginationBuilder builder = new PrintedPaginationBuilder(max).setPage(page);

			builderConsumer.accept(builder);

			builder.build(list);

		};
		return this;
	}

	/**
	 * Separate unwanted elements from the list.
	 *
	 * @param predicate test that must succeed to include an element
	 * @return this paginated list procedure
	 */
	public PaginatedList<T> filter(Predicate<? super T> predicate) {
		this.typeList = typeList.stream().filter(predicate).collect(Collectors.toList());
		return this;
	}

	public int getTotalPageCount() {
		int totalPageCount = 1;
		if (this.comparable != null) {
			typeList.sort(this.comparable);
		}
		if ((this.typeList.size() % linesPerPage) == 0) {
			if (this.typeList.size() > 0) {
				totalPageCount = this.typeList.size() / linesPerPage;
			}
		} else {
			totalPageCount = (this.typeList.size() / linesPerPage) + 1;
		}
		return totalPageCount;
	}

	/**
	 * Run all prior sorting arrangements and sequence operations for a specified page.
	 *
	 * @param pageNum the page to to collect
	 * @return a list of collected objects from the sorting procedure
	 */
	public List<T> get(int pageNum) {
		LinkedList<T> list = new LinkedList<>();
		int page = pageNum;

		int o = linesPerPage;

		int totalPageCount = getTotalPageCount();

		if (page <= totalPageCount) {

			if (this.start != null) {
				this.start.apply(this, pageNum, totalPageCount);
			}

			if (!typeList.isEmpty()) {
				int i1 = 0, k = 0;
				page--;
				LinkedList<T> sorted_list = new LinkedList<>(this.typeList);

				for (T value : sorted_list) {

					k++;
					if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
						i1++;
						if (decoration != null) {
							decoration.apply(this, value, pageNum, totalPageCount, k);
						}
						list.add(value);
					}
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
