package com.github.sanctum.labyrinth.task;

import java.util.TimerTask;
import org.intellij.lang.annotations.MagicConstant;

public abstract class Task extends TimerTask {

	public static final int SINGULAR = 0;
	public static final int REPEATABLE = 1;

	protected TaskChain parent;
	private final String key;
	private final int type;

	public Task(String key, @MagicConstant(intValues = {SINGULAR, REPEATABLE}) int type, final TaskChain parent) {
		this.parent = parent;
		this.type = type;
		this.key = key;
	}

	public Task(String key, @MagicConstant(intValues = {SINGULAR, REPEATABLE}) int type) {
		this.type = type;
		this.key = key;
	}

	public Task(String key) {
		this.type = SINGULAR;
		this.key = key;
	}

	public abstract void execute();

	@Override
	public final void run() {
		execute();
		if (type == 0) {
			parent.map.remove(getKey());
		}
	}

	public String getKey() {
		return this.key;
	}

	@Override
	public boolean cancel() {
		parent.map.remove(this.key);
		return super.cancel();
	}
}
