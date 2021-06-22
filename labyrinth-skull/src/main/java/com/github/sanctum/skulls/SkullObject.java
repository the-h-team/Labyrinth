package com.github.sanctum.skulls;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

interface SkullObject {

	ItemStack get();

	String name();

	String category();

	Optional<UUID> id();

	SkullType getType();

}
