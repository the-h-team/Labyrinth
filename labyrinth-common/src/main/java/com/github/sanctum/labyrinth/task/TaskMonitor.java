package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.panther.util.OrdinalProcedure;
import com.github.sanctum.panther.util.Task;
import com.github.sanctum.panther.util.TaskChain;
import com.github.sanctum.panther.util.TypeAdapter;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaskMonitor {

	private static TaskMonitor instance;
	protected final TypeAdapter<BukkitTaskPredicate<Task>[]> flagClass;
	protected final Map<Integer, TaskChain> map = new HashMap<>();

	TaskMonitor() {
		this.flagClass = TypeAdapter.get();
		LabyrinthAPI api = LabyrinthProvider.getInstance();
		map.put(0, api.getScheduler(TaskService.SYNCHRONOUS));
		map.put(1, api.getScheduler(TaskService.ASYNCHRONOUS));
	}

	public boolean shutdown() {
		return map.get(TaskService.SYNCHRONOUS).shutdown() && map.get(TaskService.ASYNCHRONOUS).shutdown();
	}

	public @Nullable Task get(@NotNull String key) {
		return map.get(TaskService.SYNCHRONOUS).get(key) != null ? map.get(TaskService.SYNCHRONOUS).get(key) : map.get(TaskService.ASYNCHRONOUS).get(key);
	}

	public @NotNull RenderedTask schedule(Runnable data) {
		RenderedTask execution = RenderedTask.of(data, TaskService.SYNCHRONOUS);
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).run(task);
		return execution;
	}

	public @NotNull RenderedTask scheduleAsync(Runnable data) {
		RenderedTask execution = RenderedTask.of(data, TaskService.ASYNCHRONOUS);
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).run(task);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Runnable data, long delay) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.SYNCHRONOUS, delay * 50, -1);
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Runnable data, String key, long delay) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, -1);
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Runnable data, long delay) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.ASYNCHRONOUS, delay * 50, -1);
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Runnable data, String key, long delay) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, -1);
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimer(Runnable data, String key, long delay, long period) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, period * 50);
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimerAsync(Runnable data, String key, long delay, long period) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, period * 50);
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Runnable data, long delay, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.SYNCHRONOUS, delay * 50, -1);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Runnable data, String key, long delay, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, -1);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Runnable data, long delay, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.ASYNCHRONOUS, delay * 50, -1);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Runnable data, String key, long delay, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, -1);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimer(Runnable data, String key, long delay, long period, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, period * 50);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimerAsync(Runnable data, String key, long delay, long period, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, period * 50);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask schedule(Task data) {
		RenderedTask execution = RenderedTask.of(data, TaskService.SYNCHRONOUS);
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).run(task);
		return execution;
	}

	public @NotNull RenderedTask scheduleAsync(Task data) {
		RenderedTask execution = RenderedTask.of(data, TaskService.ASYNCHRONOUS);
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).run(task);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Task data, long delay) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.SYNCHRONOUS, delay * 50, -1);
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Task data, String key, long delay) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, -1);
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Task data, long delay) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.ASYNCHRONOUS, delay * 50, -1);
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Task data, String key, long delay) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, -1);
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimer(Task data, String key, long delay, long period) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, period * 50);
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimerAsync(Task data, String key, long delay, long period) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, period * 50);
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Task data, long delay, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.SYNCHRONOUS, delay * 50, -1);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLater(Task data, String key, long delay, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, -1);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Task data, long delay, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, null, TaskService.ASYNCHRONOUS, delay * 50, -1);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleLaterAsync(Task data, String key, long delay, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, -1);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).wait(task, delay * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimer(Task data, String key, long delay, long period, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.SYNCHRONOUS, delay * 50, period * 50);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.SYNCHRONOUS).repeat(task, delay * 50, period * 50);
		return execution;
	}

	public @NotNull RenderedTask scheduleTimerAsync(Task data, String key, long delay, long period, BukkitTaskPredicate<?>... flags) {
		RenderedTask execution = RenderedTask.of(data, key, TaskService.ASYNCHRONOUS, delay * 50, period * 50);
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		execution.dependOn(BukkitTaskPredicate.reduceEmpty());
		for (BukkitTaskPredicate<Task> flag : flagClass.cast(flags)) {
			execution.dependOn(flag);
		}
		Task task = execution.getTask();
		map.get(TaskService.ASYNCHRONOUS).repeat(task, delay * 50, period * 50);
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

	public static void setInstance(@NotNull TaskMonitor monitor) {
		instance = monitor;
	}

}
