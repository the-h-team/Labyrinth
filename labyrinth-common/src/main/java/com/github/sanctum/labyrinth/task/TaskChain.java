package com.github.sanctum.labyrinth.task;

import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherMap;
import java.util.Date;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public abstract class TaskChain {

	protected final PantherMap<String, Task> map = new PantherEntryMap<>();

	public abstract TaskChain run(final @NotNull Task task);

	public abstract TaskChain run(final @NotNull Runnable data);

	public abstract TaskChain wait(final @NotNull Task task, long delay);

	public abstract TaskChain wait(final @NotNull Runnable data, String key, long milli);

	public abstract TaskChain wait(final @NotNull Task task, @NotNull Date start);

	public abstract TaskChain wait(final @NotNull Runnable data, String key, @NotNull Date start);

	public abstract TaskChain repeat(final @NotNull Task task, long delay, long period);

	public abstract TaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, long delay, long period);

	public abstract TaskChain repeat(final @NotNull Task task, @NotNull Date start, long period);

	public abstract TaskChain repeat(final @NotNull Consumer<Task> consumer, @NotNull String key, @NotNull Date start, long period);

	public abstract int purge();

	public abstract boolean shutdown();

	public abstract Task get(String key);


}
