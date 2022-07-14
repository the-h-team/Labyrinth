package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardBuilder {

	final static PantherCollection<ScoreboardInstance> instances = new PantherList<>();
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
