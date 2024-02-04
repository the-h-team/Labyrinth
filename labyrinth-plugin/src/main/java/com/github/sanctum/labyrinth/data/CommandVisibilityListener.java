package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.library.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

public class CommandVisibilityListener implements Listener {

	@EventHandler
	public void onCommandHide(PlayerCommandSendEvent e) {
		CommandUtils.getVisibilityCalculations().forEach(calculation -> {
			String test = calculation.accept(e.getPlayer());
			if (test != null) e.getCommands().remove(test);
		});
	}

	@EventHandler
	public void onTabInsert(TabCompleteEvent e) {

	}

}
