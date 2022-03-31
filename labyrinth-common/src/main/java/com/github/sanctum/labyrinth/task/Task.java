package com.github.sanctum.labyrinth.task;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.TypeFlag;
import java.util.TimerTask;
import org.intellij.lang.annotations.MagicConstant;

@Note("This class requires a void method with ordinal level 0")
public abstract class Task extends TimerTask implements Applicable {

	public static final TypeFlag<Task> FLAG = () -> Task.class;

	public static final int SINGULAR = 0;
	public static final int REPEATABLE = 1;
	private static final long serialVersionUID = 5781615452181138418L;

	protected TaskChain parent;
	private final String key;
	private final int type;
	private boolean cancelled;

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

	public final <T> T cast(TypeFlag<T> flag) {
		return flag.cast(this);
	}

	public final boolean isCancelled() {
		return cancelled;
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
		if (!cancelled) {
			if (parent != null) {
				parent.map.remove(this.key);
			}
			cancelled = true;
			return super.cancel();
		}
		return false;
	}
}
