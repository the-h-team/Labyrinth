package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.FieldsFrom;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.formatting.string.RandomID;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.TimeWatch;
import java.util.HashSet;
import java.util.Set;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A scheduled task object retaining the originally supplied runnable & runtime settings.
 */
public interface RenderedTask {

	@MagicConstant(valuesFromClass = TaskService.class) @FieldsFrom(TaskService.class)
	int getRuntime();

	long getDelay();

	long getPeriod();

	boolean isRunning();

	@Nullable String getId();

	@NotNull Type getType();

	@NotNull TimeWatch getLastRendered();

	@NotNull RenderedTask dependOn(@NotNull TaskPredicate<? super Task> intent);

	@Note("Check if this task is either scheduled on delay or concurrence.")
	default boolean isConcurrent() {
		return getId() != null;
	}

	default @NotNull TaskScheduler next(@NotNull Applicable data) {
		return TaskScheduler.of(data);
	}

	default @NotNull RenderedTask runAgain() {
		if (isRunning()) return this;
		return TaskMonitor.getLocalInstance().schedule(this);
	}

	default @Nullable RenderedTask runAgain(int count, boolean progressively) throws IllegalStateException {
		if (isConcurrent()) throw new IllegalStateException("Concurrent operations cannot be stacked!");
		if (isRunning()) return this;
		for (int i = 0; i < count; i++) {
			if (progressively) {
				next(() -> TaskMonitor.getLocalInstance().schedule(this)).scheduleLater(i);
			} else {
				TaskMonitor.getLocalInstance().schedule(this);
			}
		}
		return this;
	}

	enum Type {

		SINGULAR,
		DELAYED,
		REPEATABLE

	}

	static @NotNull RenderedTask of(Applicable data, @MagicConstant(valuesFromClass = TaskService.class) int runtime) {
		return new RenderedTask() {
			private final Task task = new Task(new RandomID().generate()) {

				@Ordinal
				void execute() {
					data.run();
				}

			};

			@Override
			public int getRuntime() {
				return runtime;
			}

			@Override
			public long getDelay() {
				return 0;
			}

			@Override
			public long getPeriod() {
				return 0;
			}

			@Override
			public boolean isRunning() {
				return LabyrinthProvider.getInstance().getScheduler(getRuntime()).get(getTask().getKey()) != null;
			}

			@Ordinal
			public @NotNull Task getTask() {
				return task;
			}

			@Override
			public @Nullable String getId() {
				return null;
			}

			@Override
			public @NotNull Type getType() {
				return Type.SINGULAR;
			}

			@Override
			public @NotNull TimeWatch getLastRendered() {
				return TimeWatch.start(getTask().scheduledExecutionTime());
			}

			@Override
			public @NotNull RenderedTask dependOn(@NotNull TaskPredicate<? super Task> intent) {
				return this;
			}
		};
	}

	static @NotNull RenderedTask of(Applicable data, @Nullable String key, @MagicConstant(valuesFromClass = TaskService.class) int runtime, long delay, long period) {
		return new RenderedTask() {
			private final Set<TaskPredicate<? super Task>> flags = new HashSet<>();
			private final Task task = new Task(new RandomID().generate()) {

				@Ordinal
				void execute() {
					boolean stop = false;
					for (TaskPredicate<? super Task> flag : flags) {
						if (!flag.accept(this)) {
							stop = true;
							break;
						}
					}
					if (!stop) {
						data.run();
					}
				}

			};

			@Override
			public int getRuntime() {
				return runtime;
			}

			@Override
			public boolean isRunning() {
				return LabyrinthProvider.getInstance().getScheduler(getRuntime()).get(getTask().getKey()) != null;
			}

			@Ordinal
			public @NotNull Task getTask() {
				return task;
			}

			@Override
			public @Nullable String getId() {
				return key;
			}

			@Override
			public @NotNull Type getType() {
				return isConcurrent() && getDelay() != 0 && getPeriod() != 0 ? Type.REPEATABLE : Type.DELAYED;
			}

			@Override
			public @NotNull TimeWatch getLastRendered() {
				return TimeWatch.start(getTask().scheduledExecutionTime());
			}

			@Override
			public @NotNull RenderedTask dependOn(@NotNull TaskPredicate<? super Task> intent) {
				switch (getRuntime()) {
					case 0:
						TaskScheduler.of(() -> flags.add(intent)).schedule();
						break;
					case 1:
						TaskScheduler.of(() -> flags.add(intent)).scheduleAsync();
						break;
				}
				return this;
			}

			@Override
			public long getDelay() {
				if (delay != -1) {
					return delay;
				}
				return 0;
			}

			@Override
			public long getPeriod() {
				if (period != -1) {
					return period;
				}
				return 0;
			}
		};
	}

}
