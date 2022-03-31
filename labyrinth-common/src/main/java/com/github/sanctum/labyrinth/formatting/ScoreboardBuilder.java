package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.library.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

public class ScoreboardBuilder {

	final static LabyrinthCollection<ScoreboardInstance> instances = new LabyrinthList<>();
	final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	Objective objective;

	public ScoreboardBuilder title(String name) {
		Objective test = scoreboard.getObjective("Labyrinth-Board");
		if (test != null) {
			test.unregister();
		}
		Objective o = scoreboard.registerNewObjective("Labyrinth-Board", "dummy", name);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective = o;
		return this;
	}

	public ScoreboardBuilder then(String text) {
		then(text, 1);
		return this;
	}

	public ScoreboardBuilder then(String text, int score) {
		objective.getScore(text).setScore(score);
		return this;
	}

	public Scoreboard toScoreboard() {
		return scoreboard;
	}

	public Objective toObjective() {
		return objective;
	}

}
