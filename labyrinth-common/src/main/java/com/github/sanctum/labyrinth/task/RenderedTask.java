package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.FieldsFrom;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.formatting.string.RandomID;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.TimeWatch;
import java.util.Objects;
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

	default @NotNull Task getTask() {
		return Objects.requireNonNull(TaskMonitor.getLocalInstance().get(Objects.requireNonNull(getId())));
	}

	@NotNull TimeWatch getLastRendered();

	@NotNull RenderedTask dependOn(@NotNull TaskPredicate<? super Task> intent);

	@Note("Check if this task is either scheduled on delay or concurrence.")
	default boolean isConcurrent() {
		return getId() != null;
	}

	default @NotNull TaskScheduler next(@NotNull Runnable data) {
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

	static @NotNull <T extends Task> RenderedTask of(T data, @MagicConstant(valuesFromClass = TaskService.class) int runtime) {
		return new RenderedTask() {

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
				return data != null && !data.isCancelled();
			}

			@Ordinal
			public @NotNull Task getTask() {
				return data;
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
				data.listen(intent);
				return this;
			}
		};
	}

	static @NotNull <T extends Task> RenderedTask of(T data, @Nullable String key, @MagicConstant(valuesFromClass = TaskService.class) int runtime, long delay, long period) {
		return new RenderedTask() {

			@Override
			public int getRuntime() {
				return runtime;
			}

			@Override
			public boolean isRunning() {
				return data != null && !data.isCancelled();
			}

			@Ordinal
			public @NotNull Task getTask() {
				return data;
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
						TaskScheduler.of(() -> data.listen(intent)).schedule();
						break;
					case 1:
						TaskScheduler.of(() -> data.listen(intent)).scheduleAsync();
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

	static @NotNull RenderedTask of(Runnable data, @MagicConstant(valuesFromClass = TaskService.class) int runtime) {
		return new RenderedTask() {
			private final Task task = new Task(new RandomID().generate(), runtime, data) {
				private static final long serialVersionUID = 87916092504686934L;
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

	static @NotNull RenderedTask of(Runnable data, @Nullable String key, @MagicConstant(valuesFromClass = TaskService.class) int runtime, long delay, long period) {
		return new RenderedTask() {
			private final Task task = new Task(key != null ? key : new RandomID().generate(), runtime, data) {
				private static final long serialVersionUID = 2948251326991055359L;
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
						TaskScheduler.of(() -> task.listen(intent)).schedule();
						break;
					case 1:
						TaskScheduler.of(() -> task.listen(intent)).scheduleAsync();
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
