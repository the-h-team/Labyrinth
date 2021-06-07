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

/**
 * Functional interface which defines behavior on the closing of a Menu.
 */
@FunctionalInterface
public interface CloseAction {
	/**
	 * When the menu is closed, what should happen?
	 *
	 * @param menuClose encapsulation which provides data about the close event
	 */
	void onClose(MenuClose menuClose);
}
