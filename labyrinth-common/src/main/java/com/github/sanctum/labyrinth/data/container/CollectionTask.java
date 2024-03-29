package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.panther.annotation.Ordinal;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.util.ProgressBar;
import com.github.sanctum.panther.util.Task;
import com.github.sanctum.panther.util.TaskChain;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.intellij.lang.annotations.MagicConstant;

public abstract class CollectionTask<T> extends Task implements Iterator<T> {

	private static final long serialVersionUID = -3836578226377446193L;
	boolean paused;

	public CollectionTask(String key) {
		super(key, REPEATABLE, TaskChain.getSynchronous());
	}

	public CollectionTask(String key, int runtime) {
		super(key, REPEATABLE, TaskChain.getChain(runtime));
	}

	protected final String correctDecimal(String s) {
		String[] split = s.split("\\.");
		if (split.length == 1) {
			return split[0] + ".00";
		} else {
			if (split[1].length() == 1) {
				return split[0] + ".0" + split[1];
			}
		}
		return s;
	}

	public abstract double getCompletion();

	public abstract long getTimeStarted();

	public abstract long getRecentExecution();

	public abstract T current();

	public abstract boolean hasNext();

	public abstract boolean hasNext(int bounds);

	@Ordinal
	public abstract T next();

	@Ordinal(1)
	public abstract T next(int bounds);

	public abstract void reset();

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}

	public static <T> CollectionTask<T> process(PantherCollection<T> collection, String table, int interval, Consumer<T> action) {
		return new CollectionTask<T>(table, TaskService.ASYNCHRONOUS) {

			private static final long serialVersionUID = 8714205665644616539L;
			final PantherCollection<T> collector;
			int index = 0;
			long started = 0, lastRan = 0;
			T current;

			{
				this.collector = collection;
			}

			public double getCompletion() {
				return Math.min(100.00, new ProgressBar().setProgress(index + 1).setGoal(collector.size()).getPercentage());
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
				return index < collector.size();
			}

			@Override
			public boolean hasNext(int bounds) {
				return index + bounds < collector.size();
			}

			@Ordinal
			public T next() {
				return next(interval);
			}

			@Ordinal(1)
			public T next(int bounds) {
				int processed = 0;
				if (isPaused()) return current;
				if (index < collector.size()) {
					if (started == 0) started = System.currentTimeMillis();
					lastRan = System.currentTimeMillis();
					for (int i = index; i < collector.size(); i++) {
						if (processed <= bounds) {
							T o = collector.get(i);
							current = o;
							action.accept(o);
							index++;
							processed++;
						}
					}
					LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- Collection task " + table + ";RUN @ &e" + correctDecimal(NumberFormat.getNumberInstance().format(getCompletion())) + "&f%").translate());
					return current;
				}
				TimeWatch.Recording recording = TimeWatch.Recording.subtract(started);
				LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- &aTask " + table + " completed after " + recording.getHours() + " hours " + recording.getMinutes() + " minutes & " + recording.getSeconds() + " seconds.").translate());
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
		};
	}

	public static <T> CollectionTask<T> processSilent(PantherCollection<T> collection, String table, int interval, Consumer<T> action) {
		return new CollectionTask<T>(table, TaskService.ASYNCHRONOUS) {

			private static final long serialVersionUID = -3595452685342615045L;
			final PantherCollection<T> collector = collection;
			int index = 0;
			long started = 0, lastRan = 0;
			T current;

			public double getCompletion() {
				return Math.min(100.00, new ProgressBar().setProgress(index + 1).setGoal(collector.size()).getPercentage());
			}

			public T current() {
				return current;
			}

			@Override
			public long getTimeStarted() {
				return started;
			}

			@Override
			public long getRecentExecution() {
				return lastRan;
			}

			@Override
			public boolean hasNext() {
				return index < collector.size();
			}

			@Override
			public boolean hasNext(int bounds) {
				return index + bounds < collector.size();
			}

			@Ordinal
			public T next() {
				return next(interval);
			}

			@Ordinal(1)
			public T next(int bounds) {
				int processed = 0;
				if (isPaused()) return current;
				if (index < collector.size()) {
					if (started == 0) started = System.currentTimeMillis();
					lastRan = System.currentTimeMillis();
					for (int i = index; i < collector.size(); i++) {
						if (processed <= bounds) {
							T o = collector.get(i);
							current = o;
							action.accept(o);
							index++;
							processed++;
						}
					}
					return current;
				}
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
		};
	}

	public static <T> CollectionTask<T> process(List<T> collection, String table, int interval, Consumer<T> action) {
		return new CollectionTask<T>(table, TaskService.ASYNCHRONOUS) {

			private static final long serialVersionUID = 5908258963151067910L;
			final List<T> collector;
			int index = 0;
			long started = 0, lastRan = 0;
			T current;

			{
				this.collector = collection;
			}

			public double getCompletion() {
				return Math.min(100.00, new ProgressBar().setProgress(index + 1).setGoal(collector.size()).getPercentage());
			}

			public T current() {
				return current;
			}

			@Override
			public long getTimeStarted() {
				return started;
			}

			@Override
			public long getRecentExecution() {
				return lastRan;
			}

			@Override
			public boolean hasNext() {
				return index < collector.size();
			}

			@Override
			public boolean hasNext(int bounds) {
				return index + bounds < collector.size();
			}

			@Ordinal
			public T next() {
				return next(interval);
			}

			@Ordinal(1)
			public T next(int bounds) {
				int processed = 0;
				if (isPaused()) return current;
				if (index < collector.size()) {
					if (started == 0) started = System.currentTimeMillis();
					lastRan = System.currentTimeMillis();
					for (int i = index; i < collector.size(); i++) {
						if (processed <= bounds) {
							T o = collector.get(i);
							current = o;
							action.accept(o);
							index++;
							processed++;
						}
					}
					LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- Collection task " + table + ";RUN @ &e" + correctDecimal(NumberFormat.getNumberInstance().format(getCompletion())) + "&f%").translate());
					return current;
				}
				TimeWatch.Recording recording = TimeWatch.Recording.subtract(started);
				LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- &aTask " + table + " completed after " + recording.getHours() + " hours " + recording.getMinutes() + " minutes & " + recording.getSeconds() + " seconds.").translate());
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
		};
	}

	public static <T> CollectionTask<T> processSilent(List<T> collection, String table, int interval, Consumer<T> action) {
		return new CollectionTask<T>(table, TaskService.ASYNCHRONOUS) {

			private static final long serialVersionUID = -3595452685342615045L;
			final List<T> collector = collection;
			int index = 0;
			long started = 0, lastRan = 0;
			T current;

			public double getCompletion() {
				return Math.min(100.00, new ProgressBar().setProgress(index + 1).setGoal(collector.size()).getPercentage());
			}

			public T current() {
				return current;
			}

			@Override
			public long getTimeStarted() {
				return started;
			}

			@Override
			public long getRecentExecution() {
				return lastRan;
			}

			@Override
			public boolean hasNext() {
				return index < collector.size();
			}

			@Override
			public boolean hasNext(int bounds) {
				return index + bounds < collector.size();
			}

			@Ordinal
			public T next() {
				return next(interval);
			}

			@Ordinal(1)
			public T next(int bounds) {
				int processed = 0;
				if (isPaused()) return current;
				if (index < collector.size()) {
					if (started == 0) started = System.currentTimeMillis();
					lastRan = System.currentTimeMillis();
					for (int i = index; i < collector.size(); i++) {
						if (processed <= bounds) {
							T o = collector.get(i);
							current = o;
							action.accept(o);
							index++;
							processed++;
						}
					}
					return current;
				}
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
		};
	}

	public static <T> CollectionTask<T> process(T[] elements, String table, int interval, Consumer<T> action) {
		return new CollectionTask<T>(table, TaskService.ASYNCHRONOUS) {

			private static final long serialVersionUID = -3595452685342615045L;
			final T[] collector = elements;
			int index = 0;
			long started = 0, lastRan = 0;
			T current;

			public double getCompletion() {
				return Math.min(100.00, new ProgressBar().setProgress(index + 1).setGoal(collector.length).getPercentage());
			}

			public T current() {
				return current;
			}

			@Override
			public long getTimeStarted() {
				return started;
			}

			@Override
			public long getRecentExecution() {
				return lastRan;
			}

			@Override
			public boolean hasNext() {
				return index < collector.length;
			}

			@Override
			public boolean hasNext(int bounds) {
				return index + bounds < collector.length;
			}

			@Ordinal
			public T next() {
				return next(interval);
			}

			@Ordinal(1)
			public T next(int bounds) {
				int processed = 0;
				if (isPaused()) return current;
				if (index < collector.length) {
					if (started == 0) started = System.currentTimeMillis();
					lastRan = System.currentTimeMillis();
					for (int i = index; i < collector.length; i++) {
						if (processed <= bounds) {
							T o = collector[i];
							current = o;
							action.accept(o);
							index++;
							processed++;
						}
					}
					LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- Collection task " + table + ";RUN @ &e" + correctDecimal(NumberFormat.getNumberInstance().format(getCompletion())) + "&f%").translate());
					return current;
				}
				TimeWatch.Recording recording = TimeWatch.Recording.subtract(started);
				LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- &aTask " + table + " completed after " + recording.getHours() + " hours " + recording.getMinutes() + " minutes & " + recording.getSeconds() + " seconds.").translate());
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
		};
	}

	public static <T> CollectionTask<T> process(T[] elements, String table, int interval, @MagicConstant(valuesFromClass = TaskService.class) int runtime, Consumer<T> action) {
		return new CollectionTask<T>(table, runtime) {

			private static final long serialVersionUID = -3595452685342615045L;
			final T[] collector = elements;
			int index = 0;
			long started = 0, lastRan = 0;
			T current;

			public double getCompletion() {
				return Math.min(100.00, new ProgressBar().setProgress(index + 1).setGoal(collector.length).getPercentage());
			}

			public T current() {
				return current;
			}

			@Override
			public long getTimeStarted() {
				return started;
			}

			@Override
			public long getRecentExecution() {
				return lastRan;
			}

			@Override
			public boolean hasNext() {
				return index < collector.length;
			}

			@Override
			public boolean hasNext(int bounds) {
				return index + bounds < collector.length;
			}

			@Ordinal
			public T next() {
				return next(interval);
			}

			@Ordinal(1)
			public T next(int bounds) {
				int processed = 0;
				if (isPaused()) return current;
				if (index < collector.length) {
					if (started == 0) started = System.currentTimeMillis();
					lastRan = System.currentTimeMillis();
					for (int i = index; i < collector.length; i++) {
						if (processed <= bounds) {
							T o = collector[i];
							current = o;
							action.accept(o);
							index++;
							processed++;
						}
					}
					LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- Collection task " + table + ";RUN @ &e" + correctDecimal(NumberFormat.getNumberInstance().format(getCompletion())) + "&f%").translate());
					return current;
				}
				TimeWatch.Recording recording = TimeWatch.Recording.subtract(started);
				LabyrinthProvider.getInstance().getLogger().info(StringUtils.use("- &aTask " + table + " completed after " + recording.getHours() + " hours " + recording.getMinutes() + " minutes & " + recording.getSeconds() + " seconds.").translate());
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
		};
	}

	public static <T> CollectionTask<T> processSilent(T[] elements, String table, int interval, Consumer<T> action) {
		return new CollectionTask<T>(table, TaskService.ASYNCHRONOUS) {

			private static final long serialVersionUID = -3595452685342615045L;
			final T[] collector = elements;
			int index = 0;
			long started = 0, lastRan = 0;
			T current;

			public double getCompletion() {
				return Math.min(100.00, new ProgressBar().setProgress(index + 1).setGoal(collector.length).getPercentage());
			}

			public T current() {
				return current;
			}

			@Override
			public long getTimeStarted() {
				return started;
			}

			@Override
			public long getRecentExecution() {
				return lastRan;
			}

			@Override
			public boolean hasNext() {
				return index < collector.length;
			}

			@Override
			public boolean hasNext(int bounds) {
				return index + bounds < collector.length;
			}

			@Ordinal
			public T next() {
				return next(interval);
			}

			@Ordinal(1)
			public T next(int bounds) {
				int processed = 0;
				if (isPaused()) return current;
				if (index < collector.length) {
					if (started == 0) started = System.currentTimeMillis();
					lastRan = System.currentTimeMillis();
					for (int i = index; i < collector.length; i++) {
						if (processed <= bounds) {
							T o = collector[i];
							current = o;
							action.accept(o);
							index++;
							processed++;
						}
					}
					return current;
				}
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
		};
	}

	public static <T> CollectionTask<T> merge(PantherCollection<T> target, PantherCollection<T> additive, String table, int interval) {
		return new PantherCollectionMergeProcess<>(target, additive, interval, table);
	}

	public static <T> CollectionTask<T> merge(List<T> target, List<T> additive, String table, int interval) {
		return new ListMergeProcess<>(target, additive, interval, table);
	}

}
