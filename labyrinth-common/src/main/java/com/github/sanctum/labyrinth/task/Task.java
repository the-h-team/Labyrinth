package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import java.util.TimerTask;
import org.intellij.lang.annotations.MagicConstant;

@Note("This class requires one method with the ordinal level 0")
public abstract class Task extends TimerTask {

	public static final int SINGULAR = 0;
	public static final int REPEATABLE = 1;

	protected TaskChain parent;
	private final String key;
	private final int type;

	public Task(String key) {
		this.type = SINGULAR;
		this.key = key;
	}

	public Task(String key, @MagicConstant(intValues = {SINGULAR, REPEATABLE}) int type) {
		this.type = type;
		this.key = key;
	}

	public Task(String key, @MagicConstant(intValues = {SINGULAR, REPEATABLE}) int type, final TaskChain parent) {
		this.parent = parent;
		this.type = type;
		this.key = key;
	}

	public final String getKey() {
		return this.key;
	}

	@Override
	public final void run() {
		OrdinalProcedure.process(this, 0);
		if (type == 0) {
			if (parent != null) {
				parent.map.remove(getKey());
			}
		}
	}

	@Override
	public final boolean cancel() {
		if (parent != null) {
			parent.map.remove(this.key);
		}
		return super.cancel();
	}
}
