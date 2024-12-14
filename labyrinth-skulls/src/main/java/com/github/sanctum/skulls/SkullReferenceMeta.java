package com.github.sanctum.skulls;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

interface SkullReferenceMeta {

	/**
	 * Get the itemstack for this skull reference.
	 *
	 * @return The skull reference that belongs to this data.
	 */
	ItemStack getItem();

	/**
	 * The name of the skull reference
	 *
	 * @return the skull reference name
	 */
	String getName();

	/**
	 * The type of skull reference
	 *
	 * @return the type of skull reference
	 */
	String getCategory();

	/**
	 * The skull reference id, only present if belonging to a player.
	 *
	 * @return the skull reference id.
	 */
	Optional<UUID> getId();

	/**
	 * The skull reference type.
	 *
	 * @return The type of skull reference.
	 */
	SkullReferenceType getType();

}
