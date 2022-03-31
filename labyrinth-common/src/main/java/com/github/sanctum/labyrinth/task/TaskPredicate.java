package com.github.sanctum.labyrinth.task;

import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * An interface responsible for deciding whether a task can either continue to execute or get cancelled.
 *
 * Returning false within a task predicate will stop the initial task from executing again but to fully cancel it make sure to run {@link Task#cancel()}
 *
 * @param <T> The type of task.
 */
@FunctionalInterface
public interface TaskPredicate<T extends Task> {

	boolean accept(T task);


	static <T extends Task> TaskPredicate<T> cancelEmpty() {
		return task -> {
			if (Bukkit.getOnlinePlayers().size() == 0) {
				task.cancel();
				return false;
			}
			return true;
		};
	}

	static <T extends Task> TaskPredicate<T> cancelAfter(int count) {
		return new TaskPredicate<T>() {
			private int i = count;

			@Override
			public boolean accept(Task task) {
				if (i == 0) {
					task.cancel();
					return false;
				}
				i--;
				return true;
			}
		};
	}

	static <T extends Task> TaskPredicate<T> cancelAfter(Player target) {
		return task -> {
			if (target == null || !target.isOnline()) {
				task.cancel();
				return false;
			}
			return true;
		};
	}

	static <T extends Task> TaskPredicate<T> cancelAfter(Function<T, Boolean> consumer) {
		return consumer::apply;
	}

	static <T extends Task> TaskPredicate<T> reduceEmpty() {
		return new TaskPredicate<T>() {
			private final Object LOCK_OBJ = new Object();

			@Override
			public boolean accept(Task task) {
				synchronized (LOCK_OBJ) {
					if (Bukkit.getOnlinePlayers().size() == 0) {
						return false;
					}
				}
				return true;
			}
		};
	}

}
