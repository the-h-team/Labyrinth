package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.annotation.See;
import com.github.sanctum.labyrinth.data.service.Check;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

@See({LabyrinthCollection.class, LabyrinthMap.class})
@Note("To be used in tandem with stream collecting.")
public final class LabyrinthCollectors {

	/**
	 * Get a collector for a new labyrinth list.
	 *
	 * @param <T> The type of list to collect.
	 * @return a fresh labyrinth list collector.
	 */
	public static <T> Collector<T, ?, LabyrinthList<T>> toList() {
		return Collector.of(LabyrinthList::new, LabyrinthCollection::add,
				(left, right) -> {
					left.addAll(right);
					return left;
				});
	}

	/**
	 * Get a collector for a new labyrinth set.
	 *
	 * @param <T> The type of set to collect.
	 * @return a fresh labyrinth set collector.
	 */
	public static <T> Collector<T, ?, LabyrinthSet<T>> toSet() {
		return Collector.of(LabyrinthSet::new, LabyrinthCollection::add,
				(left, right) -> {
					left.addAll(right);
					return left;
				});
	}

	/**
	 * Get a collector for a new immutable labyrinth list.
	 *
	 * @param <T> The type of immutable list to collect.
	 * @return a fresh immutable labyrinth list collector.
	 */
	public static <T> Collector<T, ?, LabyrinthCollection<T>> toImmutableList() {
		return Collector.of(LabyrinthList::new, LabyrinthCollection::add,
				(left, right) -> {
					left.addAll(right);
					return ImmutableLabyrinthCollection.of(left);
				});
	}

	/**
	 * Get a collector for a new immutable labyrinth set.
	 *
	 * @param <T> The type of immutable set to collect.
	 * @return a fresh immutable labyrinth set collector.
	 */
	public static <T> Collector<T, ?, LabyrinthCollection<T>> toImmutableSet() {
		return Collector.of(LabyrinthSet::new, LabyrinthCollection::add,
				(left, right) -> {
					left.addAll(right);
					return ImmutableLabyrinthCollection.of(left);
				});
	}

	/**
	 * Process mapping functions to convert a stream query into a valid labyrinth map.
	 *
	 * @param keyMapper The key mapper.
	 * @param valueMapper The value mapper.
	 * @param <T> The key type
	 * @param <K> The value type
	 * @param <U> The new value type
	 * @return a fresh labyrinth map collector.
	 */
	public static <T, K, U> Collector<T, ?, LabyrinthMap<K, U>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
		return Collector.of(LabyrinthEntryMap::new,
				defaultJavaEntryAccumulation(keyMapper, valueMapper),
				defaultJavaEntryMerger());
	}

	/**
	 * Process mapping functions to convert a stream query into a valid immutable labyrinth map.
	 *
	 * @param keyMapper The key mapper.
	 * @param valueMapper The value mapper.
	 * @param <T> The key type
	 * @param <K> The value type
	 * @param <U> The new value type
	 * @return a fresh immutable labyrinth map collector.
	 */
	public static <T, K, U> Collector<T, ?, LabyrinthMap<K, U>> toImmutableMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
		return Collector.of(LabyrinthEntryMap::new,
				defaultJavaEntryAccumulation(keyMapper, valueMapper),
				immutableJavaEntryMerger());
	}

	private static <T, K, V> BiConsumer<LabyrinthMap<K, V>, T> defaultJavaEntryAccumulation(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
		return (map, element) -> {
			K k = keyMapper.apply(element);
			V v = Check.forNull(valueMapper.apply(element), "Cannot copy null map entry value for key " + k);
			map.computeIfAbsent(k, v);
		};
	}

	private static <K, V, M extends LabyrinthMap<K, V>> BinaryOperator<M> defaultJavaEntryMerger() {
		return (m1, m2) -> {
			for (Map.Entry<K, V> e : m2.entries()) {
				K k = e.getKey();
				V v = Check.forNull(e.getValue(), "Cannot merge null map entry value for key " + k);
				m1.computeIfAbsent(k, v);
			}
			return m1;
		};
	}

	private static <K, V> BinaryOperator<LabyrinthMap<K, V>> immutableJavaEntryMerger() {
		return (m1, m2) -> {
			for (Map.Entry<K, V> e : m2.entries()) {
				K k = e.getKey();
				V v = Check.forNull(e.getValue(), "Cannot merge null immutable map entry value for key " + k);
				m1.computeIfAbsent(k, v);
			}
			return ImmutableLabyrinthMap.of(m1);
		};
	}

}
