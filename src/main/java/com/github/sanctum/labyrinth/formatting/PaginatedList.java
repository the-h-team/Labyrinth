package com.github.sanctum.labyrinth.formatting;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PaginatedList<T> {

	private Map<T, Double> doubleMap;
	private Map<T, Long> longMap;
	private Type type;
	private Comparator<? super T> comparable;
	private ComponentCompliment start;
	private ComponentCompliment finish;
	private ComponentDecoration<T> decoration;
	private int linesPerPage;


	protected double format(double amount) {
		BigDecimal b1 = new BigDecimal(amount);
		MathContext m = new MathContext(2);
		BigDecimal b2 = b1.round(m);
		return b2.doubleValue();
	}

	public PaginatedList<T> forDouble(Map<T, Double> mapToSort) {
		this.doubleMap = mapToSort;
		this.type = Type.DOUBLE;
		return this;
	}

	public PaginatedList<T> forLong(Map<T, Long> mapToSort) {
		this.longMap = mapToSort;
		this.type = Type.LONG;
		return this;
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

		if (this.type == Type.DOUBLE) {
			// Double procedure
			HashMap<T, Double> tempMap = new HashMap<>(doubleMap);

			int totalPageCount = 1;
			if ((tempMap.size() % o) == 0) {
				if (tempMap.size() > 0) {
					totalPageCount = tempMap.size() / o;
				}
			} else {
				totalPageCount = (tempMap.size() / o) + 1;
			}
			T nextTop = null;
			double nextTopAmount = 0.0;

			if (page <= totalPageCount) {
				// begin line


				if (this.start != null) {
					this.start.apply(pageNum, totalPageCount);
				}

				if (!tempMap.isEmpty()) {
					int i1 = 0, k = 0;
					page--;
					TreeMap<T, Double> sorted_map = new TreeMap<>(this.comparable);
					sorted_map.putAll(tempMap);

					for (Map.Entry<T, Double> map : sorted_map.entrySet()) {
						if (map.getValue() > nextTopAmount) {
							nextTop = map.getKey();
							nextTopAmount = map.getValue();
						}

						k++;
						if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
							i1++;

							if (decoration != null) {
								decoration.apply(map.getKey(), pageNum, totalPageCount, k, format(map.getValue()), 0L);
								list.add(nextTop);
							}
						}
						tempMap.remove(nextTop);

					}
					if (this.finish != null) {
						this.finish.apply(pageNum, totalPageCount);
					}
				}
				// end line
			}
		} else {
			HashMap<T, Long> tempMap = new HashMap<>(longMap);

			int totalPageCount = 1;
			if ((tempMap.size() % o) == 0) {
				if (tempMap.size() > 0) {
					totalPageCount = tempMap.size() / o;
				}
			} else {
				totalPageCount = (tempMap.size() / o) + 1;
			}
			T nextTop = null;
			long nextTopAmount = 0L;

			if (page <= totalPageCount) {

				if (this.start != null) {
					this.start.apply(pageNum, totalPageCount);
				}

				if (!tempMap.isEmpty()) {
					int i1 = 0, k = 0;
					page--;
					TreeMap<T, Long> sorted_map = new TreeMap<>(this.comparable);
					sorted_map.putAll(tempMap);

					for (Map.Entry<T, Long> map : sorted_map.entrySet()) {
						if (map.getValue() > nextTopAmount) {
							nextTop = map.getKey();
							nextTopAmount = map.getValue();
						}

						k++;
						if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
							i1++;

							if (decoration != null) {
								decoration.apply(map.getKey(), pageNum, totalPageCount, k, 0.0, map.getValue());
								list.add(nextTop);
							}
						}
						tempMap.remove(nextTop);

					}
					if (this.finish != null) {
						this.finish.apply(pageNum, totalPageCount);
					}
				}
				// end line
			}
		}
		return list;
	}

	public enum Type {
		DOUBLE, LONG
	}

}
