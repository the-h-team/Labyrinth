package com.github.sanctum.labyrinth.task;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * A construct using the observer pattern that provides the functionality to join and to leave running repeatable tasks.
 *
 * @param <T> The type of object the task shall run for
 * @author Rigobert0
 */
public abstract class JoinableRepeatingTask<T> {
	/**
	 * Container for all joined objects
	 */
	private final Set<T> observers = new HashSet<>();
	/**
	 * Delay between task executions
	 */
	private final int runInterval;
	/**
	 * The plugin the task timer will be associated with
	 */
	private final Plugin host;
	/**
	 * The task timer that is registered to the bukkit scheduler
	 */
	private BukkitTask task;

	/**
	 * Constructor
	 *
	 * @param runInterval the time interval that will pass between every execution of the task
	 * @param host        the plugin that is running the task
	 */
	protected JoinableRepeatingTask(final int runInterval, final Plugin host) {
		this.runInterval = runInterval;
		this.host = host;
	}

	/**
	 * Adds an object for task execution. The execution will run every {@link #runInterval} ticks
	 *
	 * @param type the object the task will be performed on
	 */
	public synchronized void joinTask(T type) {
		observers.add(type);
		if (task == null) {
			start();
		}
	}

	/**
	 * Removes an object from the task execution.
	 *
	 * @param type the object the task will no longer be performed on
	 */
	public synchronized void leaveTask(T type) {
		observers.remove(type);
		if (observers.isEmpty()) {
			stop();
		}
	}

	/**
	 * Starts the task timer after {@link #runInterval} delay.
	 * The task will be repeated after the same time while there are objects to perform this task on.
	 */
	private void start() {
		task = Bukkit.getScheduler().runTaskTimer(host, this::run, runInterval, runInterval);
	}

	/**
	 * Stops the task timer
	 */
	public void stop() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	/**
	 * Run the task for each joined object.
	 */
	private synchronized void run() {
		observers.forEach(this::executeTask);
	}

	/**
	 * The task to be run for each joined object
	 *
	 * @param type a joined object
	 */
	protected abstract void executeTask(T type);

	/**
	 * Static initializer for usage without subclassing.
	 *
	 * @param runInterval {@link #runInterval}
	 * @param host {@link #host}
	 * @param task {@link #executeTask(Object)}
	 * @param <T> The type of object the tasks will run on
	 * @return An anonymous JoinableRepeatingTask subclass that will run your provided task
	 */
	public static <T> JoinableRepeatingTask<T> create(int runInterval, Plugin host, Consumer<T> task) {
		return new JoinableRepeatingTask<T>(runInterval, host) {
			@Override
			protected void executeTask(final T type) {
				task.accept(type);
			}
		};
	}
}
