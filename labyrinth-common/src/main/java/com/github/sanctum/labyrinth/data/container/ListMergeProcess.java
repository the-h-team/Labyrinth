package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.labyrinth.formatting.string.ProgressBar;
import com.github.sanctum.labyrinth.formatting.string.RandomID;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import java.text.NumberFormat;
import java.util.List;

final class ListMergeProcess<T> extends CollectionTask<T> {

	List<T> collector;
	List<T> child;
	int index = 0, limit;
	long started = 0, lastRan = 0;
	String table;
	T current;

	ListMergeProcess(List<T> collector, List<T> child, int processLimit, String table) {
		super(new RandomID().generate());
		this.collector = collector;
		this.child = child;
		this.limit = processLimit;
		this.table = table;
	}

	public double getCompletion() {
		return Math.min(100.00, new ProgressBar().setProgress(index + 1).setGoal(child.size()).getPercentage());
	}

	@Override
	public long getTimeStarted() {
		return started;
	}

	@Override
	public long getRecentExecution() {
		return lastRan;
	}

	public T current() {
		return current;
	}

	@Override
	public boolean hasNext() {
		return index < child.size();
	}

	@Override
	public boolean hasNext(int bounds) {
		return index + bounds < child.size();
	}

	@Ordinal
	public T next() {
		return next(limit);
	}

	@Ordinal(1)
	public T next(int bounds) {
		int processed = 0;
		if (isPaused()) return current;
		if (index < child.size()) {
			if (started == 0) started = System.currentTimeMillis();
			lastRan = System.currentTimeMillis();
			for (int i = index; i < child.size(); i++) {
				if (processed <= bounds) {
					T o = child.get(i);
					current = o;
					collector.add(o);
					index++;
					processed++;
				}
			}
			LabyrinthProvider.getInstance().getLogger().info("- Queable collection process " + table + ";MERGE @ " + correctDecimal(NumberFormat.getNumberInstance().format(getCompletion())) + "% ");
			return current;
		}
		TimeWatch.Recording recording = TimeWatch.Recording.subtract(started);
		LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- &aTask " + table + " completed after " + recording.getHours() + " hours " + recording.getMinutes() + " minutes & " + recording.getSeconds() + " seconds.").translate());
		LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- &aSize b match of a (" + child.size() + " vs " + collector.size() + ")").translate());
		cancel();
		return current;
	}

	@Override
	public void reset() {
		index = 0;
		started = 0;
		lastRan = 0;
		current = null;
	}

}
