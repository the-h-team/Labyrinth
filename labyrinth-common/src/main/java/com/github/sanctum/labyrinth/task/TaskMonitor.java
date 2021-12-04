package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.TypeFlag;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskMonitor {

	private static TaskMonitor instance;
	private final TypeFlag<TaskPredicate<Task>[]> flagClass;
	private final Map<Integer, TaskChain> map = new HashMap<>();

	TaskMonitor() {
		this.flagClass = TypeFlag.get();
		map.put(0, LabyrinthProvider.getInstance().getScheduler(TaskService.SYNCHRONOUS));
		map.put(1, LabyrinthProvider.getInstance().getScheduler(TaskService.ASYNCHRONOUS));
	}

	public boolean shutdown() {
		return map.get(0).shutdown() && map.get(1).shutdown();
	}

	public @Nullable Task get(@NotNull String key) {
		return map.get(0).get(key) != null ? map.get(0).get(key) : map.get(1).get(key);
	}

	public @NotNull RenderedTask schedule(Applicable data) {
		RenderedTask execution = RenderedTask.of(data, TaskService.SYNCHRONOUS);
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(0).run(task);
		return execution;
	}

	public @NotNull RenderedTask scheduleAsync(Applicable data) {
		RenderedTask execution = RenderedTask.of(data, TaskService.ASYNCHRONOUS);
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(1).run(task);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Applicable data, long delay) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.SYNCHRONOUS, delay * 50, -1);
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(0).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Applicable data, String key, long delay) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, -1);
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(0).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Applicable data, long delay) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.ASYNCHRONOUS, delay * 50, -1);
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(1).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Applicable data, String key, long delay) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, -1);
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(1).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimer(Applicable data, String key, long delay, long period) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, period * 50);
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(0).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimerAsync(Applicable data, String key, long delay, long period) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, period * 50);
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(1).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Applicable data, long delay, TaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.SYNCHRONOUS, delay * 50, -1);
		for (TaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(0).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Applicable data, String key, long delay, TaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, -1);
		for (TaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(0).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Applicable data, long delay, TaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.ASYNCHRONOUS, delay * 50, -1);
		for (TaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(1).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Applicable data, String key, long delay, TaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, -1);
		for (TaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(1).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimer(Applicable data, String key, long delay, long period, TaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, period * 50);
		for (TaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(0).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimerAsync(Applicable data, String key, long delay, long period, TaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, period * 50);
		for (TaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		map.get(1).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask schedule(RenderedTask execution) {
		int runtime = execution.getRuntime();
		Task task = OrdinalProcedure.select(execution, 0).cast(() -> Task.class);
		switch (execution.getType()) {
			case SINGULAR:
				LabyrinthProvider.getInstance().getScheduler(runtime).run(task);
				break;
			case DELAYED:
				LabyrinthProvider.getInstance().getScheduler(runtime).wait(task, execution.getDelay());
				break;
			case REPEATABLE:
				LabyrinthProvider.getInstance().getScheduler(runtime).repeat(task, execution.getDelay(), execution.getPeriod());
				break;
		}
		return execution;
	}


	public static TaskMonitor getLocalInstance() {
		return instance != null ? instance : (instance = new TaskMonitor());
	}

}
