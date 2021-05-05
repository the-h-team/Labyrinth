package com.github.sanctum.labyrinth.formatting;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PaginatedList<T> {
	private final List<T> typeList;
	private Comparator<? super T> comparable;
	private ComponentCompliment start;
	private ComponentCompliment finish;
	private ComponentDecoration<T> decoration;
	private int linesPerPage;

	public PaginatedList(List<T> list) {
		this.typeList = list;
	}

	protected double format(double amount) {
		BigDecimal b1 = new BigDecimal(amount);
		MathContext m = new MathContext(2);
		BigDecimal b2 = b1.round(m);
		return b2.doubleValue();
	}

	public PaginatedList<T> compare(Comparator<? super T> comparable) {
		this.comparable = comparable;
		return this;
	}

	public PaginatedList<T> decorate(ComponentDecoration<T> decoration) {
		this.decoration = decoration;
		return this;
	}

	public PaginatedList<T> limit(int linesPerPage) {
		this.linesPerPage = linesPerPage;
		return this;
	}

	public PaginatedList<T> start(StartingCompliment<T> compliment) {
		this.start = compliment;
		return this;
	}

	public PaginatedList<T> finish(FinishingCompliment<T> compliment) {
		this.finish = compliment;
		return this;
	}

	public List<T> get(int pageNum) {
		LinkedList<T> list = new LinkedList<>();
		int page = pageNum;

		int o = linesPerPage;

		List<T> tempList = new LinkedList<>(this.typeList);

		int totalPageCount = 1;
		if ((tempList.size() % o) == 0) {
			if (tempList.size() > 0) {
				totalPageCount = tempList.size() / o;
			}
		} else {
			totalPageCount = (tempList.size() / o) + 1;
		}

		if (page <= totalPageCount) {
			// begin line


			if (this.start != null) {
				this.start.apply(pageNum, totalPageCount);
			}

			if (!tempList.isEmpty()) {
				int i1 = 0, k = 0;
				page--;
				tempList.sort(this.comparable);
				LinkedList<T> sorted_list = new LinkedList<>(tempList);

				for (T value : sorted_list) {

					k++;
					if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
						i1++;

						if (decoration != null) {
							decoration.apply(value, pageNum, totalPageCount, k);
							list.add(value);
						}
					}
					tempList.remove(value);

				}
				if (this.finish != null) {
					this.finish.apply(pageNum, totalPageCount);
				}
			}
			// end line
		}
		return list;
	}

}
