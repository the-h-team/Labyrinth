package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.api.TaskService;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <h3>Goodbye void... Hello deployable!</h3>
 * A modular extension to modifying sourced material or an overall replacement to simply making a method return void.
 *
 * <p>The included functionality this interface provides insinuates that stacked operations within specific time intervals need processing.</p>
 *
 * <p>Using one or more objects finalize data results, <strong>this object can be used as a staging process to object building/handling.</strong></p>
 *
 * @param <T> The data type this deployable references
 */
public interface Deployable<T> {

	/**
	 * Instantly run the relative meta data attached to this deployable skipping queue.
	 *
	 * <p>The usage of this method suggests that the information being passed doesn't need any scheduling and can be provided right now</p>
	 *
	 * @see Deployable#deploy(Consumer)
	 */
	Deployable<T> deploy();

	/**
	 * Instantly run the relative meta data attached to this deployable skipping queue then apply an additional operation
	 * using the sourced material.
	 *
	 * @param consumer The operation to run.
	 * @see Deployable#deploy()
	 */
	Deployable<T> deploy(Consumer<? super T> consumer);

	/**
	 * Queue the relative meta data attached to this deployable to run on the next micro tick.
	 *
	 * @apiNote The calculation of "micro ticks" is simply milliseconds.
	 * @see Deployable#queue(long)
	 * @see Deployable#queue(Date)
	 */
	Deployable<T> queue();

	/**
	 * Queue the relative meta data attached to this deployable to run after a specified interval.
	 *
	 * @param wait The amount of milliseconds to wait.
	 * @see Deployable#queue(Date)
	 */
	Deployable<T> queue(long wait);

	/**
	 * Queue the relative meta data attached to this deployable to run @ a specific date.
	 *
	 * @param date The date to run the data.
	 * @see Deployable#queue(long)
	 */
	Deployable<T> queue(Date date);

	/**
	 * Queue the relative meta data attached to this deployable to run after a specified interval then apply
	 * an additional operation using the sourced material.
	 *
	 * @param consumer The operation to run after the interval is reached.
	 * @param wait     The amount of milliseconds to wait.
	 * @see Deployable#queue()
	 * @see Deployable#queue(long)
	 * @see Deployable#queue(Date)
	 */
	Deployable<T> queue(Consumer<? super T> consumer, long wait);

	/**
	 * Queue the relative meta data attached to this deployable to run after a specified interval then apply
	 * an additional operation using the sourced material.
	 *
	 * @param consumer The operation to run after the interval is reached.
	 * @param date     The date to run the data.
	 * @see Deployable#queue()
	 * @see Deployable#queue(long)
	 * @see Deployable#queue(Date)
	 * @see Deployable#queue(Consumer, long)
	 */
	Deployable<T> queue(Consumer<? super T> consumer, Date date);

	/**
	 * Map the provided source material into an object processor.
	 *
	 * @param mapper The operation to run.
	 * @param <O>    The resulting type.
	 * @return The desired return type.
	 */
	<O> DeployableMapping<O> map(Function<? super T, ? extends O> mapper);

	/**
	 * Run a completable future with the attached source material.
	 *
	 * @return A new completable future.
	 */
	CompletableFuture<T> submit();

	/**
	 * Complete the deployable information processing and get the object reference.
	 *
	 * @return The object reference from this deployable.
	 */
	default T complete() {
		deploy();
		return submit().join();
	}

	/**
	 * Get the object reference.
	 *
	 * @return The object reference from this deployable.
	 */
	default T get() {
		return complete();
	}

	/**
	 * Create a default deployable instance
	 *
	 * @param t      The source material.
	 * @param action The operation to deploy.
	 * @param <T>    The material data reference.
	 * @return A fresh deployable instance.
	 */
	static <T> Deployable<T> of(@Nullable T t, final @NotNull Consumer<T> action) {
		return new Deployable<T>() {

			@Override
			public Deployable<T> deploy() {
				action.accept(t);
				return this;
			}

			@Override
			public Deployable<T> deploy(Consumer<? super T> consumer) {
				deploy();
				consumer.accept(t);
				return this;
			}

			@Override
			public Deployable<T> queue() {
				submit().thenAccept(action).join();
				return this;
			}

			@Override
			public Deployable<T> queue(Consumer<? super T> consumer, long timeout) {
				LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
					queue();
					consumer.accept(t);
				}, HUID.randomID().toString(), timeout);
				return this;
			}

			@Override
			public Deployable<T> queue(Consumer<? super T> consumer, Date date) {
				LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
					queue();
					consumer.accept(t);
				}, HUID.randomID().toString(), date);
				return this;
			}

			@Override
			public <O> DeployableMapping<O> map(Function<? super T, ? extends O> mapper) {
				return new DeployableMapping<>(() -> submit().join(), (Function<? super Object, ? extends O>) mapper);
			}

			@Override
			public Deployable<T> queue(long timeout) {
				LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(this::queue, HUID.randomID().toString(), timeout);
				return this;
			}

			@Override
			public Deployable<T> queue(Date date) {
				LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(this::queue, HUID.randomID().toString(), date);
				return this;
			}

			@Override
			public CompletableFuture<T> submit() {
				return CompletableFuture.supplyAsync(() -> t);
			}
		};
	}

}
