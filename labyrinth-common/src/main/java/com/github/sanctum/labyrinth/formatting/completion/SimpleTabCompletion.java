package com.github.sanctum.labyrinth.formatting.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

public interface SimpleTabCompletion {

	SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, Collection<String> completions);

	SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, String predicate, @MagicConstant(valuesFromClass = TabCompletionIndex.class) int predicateIndex, Collection<String> completions);

	default SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, String... strings) {
		return then(index, new ArrayList<>(Arrays.asList(strings)));
	}

	default SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, Supplier<Collection<String>> supplier) {
		return then(index, supplier.get());
	}

	default SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, Supplier<String> predicate, @MagicConstant(valuesFromClass = TabCompletionIndex.class) int predicateIndex, Collection<String> completions) {
		return then(index, predicate.get(), predicateIndex, completions);
	}

	default SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, String predicate, @MagicConstant(valuesFromClass = TabCompletionIndex.class) int predicateIndex, String... strings) {
		return then(index, predicate, predicateIndex, new ArrayList<>(Arrays.asList(strings)));
	}

	default SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, String predicate, @MagicConstant(valuesFromClass = TabCompletionIndex.class) int predicateIndex, Supplier<Collection<String>> supplier) {
		return then(index, predicate, predicateIndex, supplier.get());
	}

	default SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, Supplier<String> predicate, @MagicConstant(valuesFromClass = TabCompletionIndex.class) int predicateIndex, String... strings) {
		return then(index, predicate.get(), predicateIndex, new ArrayList<>(Arrays.asList(strings)));
	}

	default SimpleTabCompletion then(@MagicConstant(valuesFromClass = TabCompletionIndex.class) int index, Supplier<String> predicate, @MagicConstant(valuesFromClass = TabCompletionIndex.class) int predicateIndex, Supplier<Collection<String>> supplier) {
		return then(index, predicate.get(), predicateIndex, supplier.get());
	}

	SimpleTabCompletion fillArgs(@NotNull String[] args);

	@NotNull List<String> get();


	static SimpleTabCompletion of(@NotNull String[] arguments) {
		return new SimpleTabCompletion() {

			String[] args;
			final Map<Integer, Collection<String>> non_predicates = new HashMap<>();
			final Map<Integer, List<TabCompletionPredicate>> predicates = new HashMap<>();

			{
				this.args = arguments;
			}

			@Override
			public SimpleTabCompletion then(int index, Collection<String> completions) {
				if (non_predicates.containsKey(index)) {
					non_predicates.get(index).addAll(completions);
				} else {
					non_predicates.put(index, completions);
				}
				return this;
			}

			@Override
			public SimpleTabCompletion then(int index, String predicate, int predicateIndex, Collection<String> completions) {
				if (predicates.containsKey(index)) {
					predicates.get(index).add(new TabCompletionPredicate(predicateIndex, predicate, new ArrayList<>(completions)));
				} else {
					List<TabCompletionPredicate> list = new ArrayList<>();
					list.add(new TabCompletionPredicate(predicateIndex, predicate, new ArrayList<>(completions)));
					predicates.put(index, list);
				}
				return this;
			}

			@Override
			public SimpleTabCompletion fillArgs(@NotNull String[] args) {
				this.args = args;
				return this;
			}

			@Override
			public @NotNull List<String> get() {
				int index = args.length - 1;
				List<String> list = new ArrayList<>();
				List<TabCompletionPredicate> predicate = predicates.get(index);
				if (predicate != null) {
					predicate.forEach(entry -> {
						if (entry.predicateIndex < index) {
							String arg = args[entry.predicateIndex];
							if (arg.equalsIgnoreCase(entry.predicate)) {
								for (String completion : entry.completions) {
									if (completion.toLowerCase().startsWith(args[index].toLowerCase())) {
										list.add(completion);
									}
								}
							}
						}
					});
				} else {
					Collection<String> completions = non_predicates.get(index);
					if (completions != null) {
						for (String completion : completions) {
							if (completion.toLowerCase().startsWith(args[index].toLowerCase())) {
								list.add(completion);
							}
						}
					}
				}
				return list;
			}
		};
	}

	static SimpleTabCompletion empty() {
		return new SimpleTabCompletion() {

			String[] args;
			final Map<Integer, Collection<String>> map = new HashMap<>();
			final Map<Integer, Map.Entry<Integer, String>> predicates = new HashMap<>();

			@Override
			public SimpleTabCompletion then(int index, Collection<String> completions) {
				if (map.containsKey(index)) {
					if (!map.get(index).containsAll(completions)) {
						map.get(index).addAll(completions);
					}
				} else {
					map.put(index, completions);
				}
				return this;
			}

			@Override
			public SimpleTabCompletion then(int index, String predicate, int predicateIndex, Collection<String> completions) {
				predicates.put(index, new Map.Entry<Integer, String>() {

					@Override
					public Integer getKey() {
						return predicateIndex;
					}

					@Override
					public String getValue() {
						return predicate;
					}

					@Override
					public String setValue(String value) {
						return getValue();
					}
				});
				if (map.containsKey(index)) {
					if (!map.get(index).containsAll(completions)) {
						map.get(index).addAll(completions);
					}
				} else {
					map.put(index, completions);
				}
				return this;
			}

			@Override
			public SimpleTabCompletion fillArgs(@NotNull String[] args) {
				this.args = args;
				return this;
			}

			@Override
			public @NotNull List<String> get() {
				List<String> list = new ArrayList<>();
				if (args == null) return list;
				int index = args.length - 1;
				Map.Entry<Integer, String> predicate = predicates.get(index);
				Collection<String> completions = map.get(index);
				if (predicate != null) {
					if (predicate.getKey() < index) {
						String arg = args[predicate.getKey()];
						if (arg.equalsIgnoreCase(predicate.getValue())) {
							for (String completion : completions) {
								if (completion.toLowerCase().startsWith(args[index].toLowerCase())) {
									list.add(completion);
								}
							}
						}
					}
				} else {
					if (completions != null) {
						for (String completion : completions) {
							if (completion.toLowerCase().startsWith(args[index].toLowerCase())) {
								list.add(completion);
							}
						}
					}
				}
				return list;
			}
		};
	}

}
