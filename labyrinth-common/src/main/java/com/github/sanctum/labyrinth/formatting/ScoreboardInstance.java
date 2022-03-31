package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.RenderedTask;
import com.github.sanctum.labyrinth.task.TaskPredicate;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScoreboardInstance {

	void update(@NotNull Scoreboard scoreboard);

	void update(@NotNull ScoreboardGroup group);

	void update(@NotNull ScoreboardGroup group, long interval);

	default void flip(@NotNull ScoreboardGroup group, @NotNull ScoreboardGroup group2, long interval, long flip) {
		update(group, interval); // initialize return state.
		update(group2); // update current status
		TaskScheduler.of(this::revert).scheduleLater(flip); // revert after flip interval
	}

	void add(@NotNull ScoreboardGroup group);

	void remove(@NotNull ScoreboardGroup group);

	void revert();

	void resume();

	void stop();

	@NotNull Player getHolder();

	@Nullable ScoreboardGroup getGroup(@NotNull String key);

	@Nullable ScoreboardGroup getPrevious();

	@Nullable ScoreboardGroup getCurrent();

	static ScoreboardInstance get(@NotNull Player p) {
		return ScoreboardBuilder.instances.stream().filter(i -> i.getHolder().equals(p)).findFirst().orElse(new ScoreboardInstance() {
			final LabyrinthMap<String, ScoreboardGroup> groups = new LabyrinthEntryMap<>();
			final Player player = p;
			long lastRecordedInt = 1;
			Scoreboard last;
			RenderedTask task;
			ScoreboardGroup previous;
			ScoreboardGroup now;

			{
				this.last = player.getScoreboard();
			}

			public void update(@NotNull Scoreboard scoreboard) {
				this.last = player.getScoreboard();
				Objective t = scoreboard.getObjective("Labyrinth-Board");
				if (t != null) {
					String title = t.getDisplayName();
					LabyrinthMap<String, Integer> map = new LabyrinthEntryMap<>();
					scoreboard.getEntries().forEach(e -> {
						scoreboard.getScores(e).forEach(s -> {
							map.put(StringUtils.use(s.getEntry()).translate(player), s.getScore());
						});
					});
					t.unregister();
					Objective n = scoreboard.registerNewObjective("Labyrinth-Board", "dummy", StringUtils.use(title).translate(player));
					map.forEach(e -> n.getScore(e.getKey()).setScore(e.getValue()));
					n.setDisplaySlot(DisplaySlot.SIDEBAR);
				}
				player.setScoreboard(scoreboard);
			}

			public void update(@NotNull ScoreboardGroup group) {
				update(group, lastRecordedInt);
			}

			public void update(@NotNull ScoreboardGroup group, long interval) {
				if (now != null) {
					now.setActive(false);
				}
				this.previous = now;
				this.now = group;
				now.setActive(true);
				this.lastRecordedInt = interval;
				if (task != null) {
					task.getTask().cancel();
				}
				task = TaskScheduler.of(() -> {
					now.next();
					ScoreboardBuilder builder = now.getBuilder(now.getIndex());

					update(builder.toScoreboard());
				}).scheduleTimer(player.getName() + "-scoreboard", interval, interval, TaskPredicate.cancelAfter(player));
			}

			@Override
			public void add(@NotNull ScoreboardGroup group) {
				groups.put(group.getKey(), group);
			}

			@Override
			public void remove(@NotNull ScoreboardGroup group) {
				groups.remove(group.getKey());
			}

			public void revert() {
				if (previous == null) {
					final Scoreboard now = player.getScoreboard();
					player.setScoreboard(last);
					last = now;
				} else {
					update(previous, lastRecordedInt);
				}
			}

			@Override
			public void resume() {
				if (!getCurrent().isActive()) {
					update(getCurrent());
				}
			}

			@Override
			public void stop() {
				if (getCurrent().isActive()) {
					task.getTask().cancel();
					getCurrent().setActive(false);
				}
			}

			@Override
			public @NotNull Player getHolder() {
				return player;
			}

			@Override
			public ScoreboardGroup getGroup(@NotNull String key) {
				return groups.get(key);
			}

			public ScoreboardGroup getPrevious() {
				return previous;
			}

			public ScoreboardGroup getCurrent() {
				return now;
			}
		});
	}


}
