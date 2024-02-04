package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.panther.annotation.Note;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Wraps NMS data to be used with creation of simple NPCs
 *
 * @author Hempfest
 * @since 1.0
 */
public interface EntityMechanics extends Service {




	static @Note @NotNull EntityMechanics getInstance() {
		return Objects.requireNonNull(Bukkit.getServicesManager().load(EntityMechanics.class));
	}

}