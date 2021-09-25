package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.api.TaskService;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public final class DeployableMapping<R> implements Deployable<R> {

	private final Function<? super Object, ? extends R> function;
	private final Object parent;
	private R value;

	DeployableMapping(Object o, Function<? super Object, ? extends R> function) {
		this.function = function;
		this.parent = o;
	}

	@Override
	public DeployableMapping<R> deploy() {
		if (this.value == null) {
			this.value = function.apply(this.parent);
		}
		return this;
	}

	@Override
	public DeployableMapping<R> deploy(Consumer<? super R> consumer) {
		if (this.value == null) {
			this.value = function.apply(this.parent);
		}
		consumer.accept(this.value);
		return this;
	}

	@Override
	public DeployableMapping<R> queue() {
		deploy();
		return this;
	}

	@Override
	public DeployableMapping<R> queue(long wait) {
		LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(this::queue, HUID.randomID().toString(), wait);
		return this;
	}

	@Override
	public DeployableMapping<R> queue(Date date) {
		LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(this::queue, HUID.randomID().toString(), date);
		return this;
	}

	@Override
	public DeployableMapping<R> queue(Consumer<? super R> consumer, long wait) {
		LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
			queue();
			consumer.accept(this.value);
		}, HUID.randomID().toString(), wait);
		return this;
	}

	@Override
	public DeployableMapping<R> queue(Consumer<? super R> consumer, Date date) {
		LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
			queue();
			consumer.accept(this.value);
		}, HUID.randomID().toString(), date);
		return this;
	}

	@Override
	public <O> DeployableMapping<O> map(Function<? super R, ? extends O> mapper) {
		return new DeployableMapping<>(submit().join(), (Function<? super Object, ? extends O>) mapper);
	}

	@Override
	public CompletableFuture<R> submit() {
		return CompletableFuture.supplyAsync(() -> {
			deploy();
			return this.value;
		});
	}
}
