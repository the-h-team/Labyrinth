package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.TypeFlag;
import java.lang.reflect.Field;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

@Note("This class requires a no argument method with ordinal level 0")
public abstract class Task extends TimerTask implements Applicable {

	public static final TypeFlag<Task> FLAG = () -> Task.class;

	public static final int SINGULAR = 0;
	public static final int REPEATABLE = 1;
	private static final long serialVersionUID = 5781615452181138418L;
	private static final Plugin host = LabyrinthProvider.getInstance().getPluginInstance();

	protected TaskChain parent;
	private final LabyrinthCollection<TaskPredicate<Task>> predicates = new LabyrinthList<>();
	private final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
	private final OrdinalProcedure<Task> ordinal;
	private final String key;
	private Runnable runnable;
	private final int type;
	private boolean async;
	private boolean cancelled;

	public Task(String key, @MagicConstant(valuesFromClass = TaskService.class) int runtime) {
		this.type = SINGULAR;
		this.key = key;
		this.parent = LabyrinthProvider.getInstance().getScheduler(runtime);
		this.ordinal = OrdinalProcedure.of(this);
	}

	public Task(String key, @MagicConstant(valuesFromClass = TaskService.class) int runtime, @NotNull Runnable runnable) {
		this.type = SINGULAR;
		this.key = key;
		this.runnable = runnable;
		this.parent = LabyrinthProvider.getInstance().getScheduler(runtime);
		this.ordinal = OrdinalProcedure.of(this);
	}

	public Task(String key, @MagicConstant(intValues = {SINGULAR, REPEATABLE}) int type, @MagicConstant(valuesFromClass = TaskService.class) int runtime) {
		this.type = type;
		this.key = key;
		this.parent = LabyrinthProvider.getInstance().getScheduler(runtime);
		this.ordinal = OrdinalProcedure.of(this);
	}

	public Task(String key, @MagicConstant(intValues = {SINGULAR, REPEATABLE}) int type, final TaskChain parent) {
		this.parent = parent;
		this.type = type;
		this.key = key;
		this.ordinal = OrdinalProcedure.of(this);
	}

	public final String getKey() {
		return this.key;
	}

	public final <T> T cast(TypeFlag<T> flag) {
		return flag.cast(this);
	}

	public final boolean isCancelled() {
		return cancelled;
	}

	public final boolean isAsync() {
		return async;
	}

	public final Task setAsync(boolean async) {
		this.async = async;
		return this;
	}

	public final void listen(TaskPredicate<?>... predicate) {
		for (TaskPredicate<?> p : predicate) {
			predicates.add((TaskPredicate<Task>) p);
		}
	}

	@Override
	public final void run() {
		if (host.isEnabled()) {
			final Runnable r = runnable != null ? runnable : () -> ordinal.run(0);
			if (isAsync()) {
				if (!predicates.isEmpty() && predicates.stream().anyMatch(p -> !p.accept(this))) return;
				r.run();
			} else {
				bukkitScheduler.runTask(host, () -> {
					if (!predicates.isEmpty() && predicates.stream().anyMatch(p -> !p.accept(this))) return;
					r.run();
				});
			}
			if (type == Task.SINGULAR && parent != null) {
				parent.map.remove(getKey());
				try {
					Field field = TimerTask.class.getDeclaredField("state");
					field.setAccessible(true);
					Field virginField = TimerTask.class.getDeclaredField("VIRGIN");
					virginField.setAccessible(true);
					int virgin = (int) virginField.get(null);
					field.set(this, virgin);
				} catch (Exception e) {
					LabyrinthProvider.getInstance().getLogger().finest("Unable to reset task timer for task #" + getKey() + "-" + hashCode());
				}
			}
		} else cancel();
	}

	/**
	 * @inheritDocs
	 */
	@Override
	public final boolean cancel() {
		if (!cancelled) {
			if (parent != null) parent.map.remove(this.key);
			return this.cancelled = super.cancel();
		}
		return false;
	}
}
