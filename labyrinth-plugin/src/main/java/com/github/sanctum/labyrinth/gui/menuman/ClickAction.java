/*
 *  Copyright 2021 ms5984 (Matt) <https://github.com/ms5984>
 *
 *  This file is part of MenuMan, a module of Labyrinth.
 *
 *  MenuMan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  MenuMan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.sanctum.labyrinth.gui.menuman;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Functional interface which defines behavior on use of a Menu's actions.
 */
@FunctionalInterface
public interface ClickAction {
	/**
	 * When this action is called, what should happen?
	 *
	 * @param menuClick encapsulation which provides data about the click event
	 */
	void onClick(MenuClick menuClick);

	/**
	 * Close the Menu.
	 * <p>
	 * Note: closeInventory() <b>must</b> be called from a scheduled task;
	 * see {@link org.bukkit.event.inventory.InventoryClickEvent} for more
	 * information.
	 *
	 * @param menuClick encapsulation which provides data about the click event
	 */
	static void close(MenuClick menuClick) {
		// closeInventory must not be called from InventoryClickEvent handler
		new BukkitRunnable() {
			@Override
			public void run() {
				// task called on next tick
				menuClick.player.closeInventory();
			}
		}.runTask(JavaPlugin.getProvidingPlugin(ClickAction.class));
	}

	/**
	 * Easily run a command, closing the menu afterward by default.
	 * <p>
	 * See {@link RunCommand#closeOnClick()}.
	 */
	abstract class RunCommand implements ClickAction {

		/**
		 * Dispatches the command specified for the player who clicked.
		 * <p>
		 * If {@link #closeOnClick()} returns true, the menu will be closed.
		 *
		 * @param menuClick encapsulation which provides data about the click event
		 */
		@Override
		public void onClick(MenuClick menuClick) {
			Bukkit.dispatchCommand(menuClick.player, commandToRun());
			if (closeOnClick()) close(menuClick);
		}

		/**
		 * Specify the command to run. The command should be prefixed with "/".
		 *
		 * @return command string that will be dispatched on clicking
		 */
		public abstract @NotNull String commandToRun();

		/**
		 * Configure the menu to close or not after command dispatch.
		 * <p>
		 * Defaults to true.
		 *
		 * @return if the menu will close after the command is executed
		 */
		public boolean closeOnClick() {
			return true;
		}
	}
}
