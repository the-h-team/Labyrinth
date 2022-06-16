package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.Node;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MemoryItem {

	private final Node node;
	private Map<String, String> replacements;
	private boolean notRemovable;
	private boolean exitOnClick;
	private String openOnClick;
	private String message;
	private int slot = -1;
	private int limit = 5;

	public MemoryItem(Node node) {
		this.node = node;
	}

	public boolean isNotRemovable() {
		return notRemovable;
	}

	public boolean isExitOnClick() {
		return exitOnClick;
	}

	@Note("This could return either a menu id or a command.")
	public String getOpenOnClick() {
		return openOnClick;
	}

	public String getMessage() {
		return message;
	}

	public int getSlot() {
		return slot;
	}

	public int getLimit() {
		return limit;
	}

	public Map<String, String> getReplacements() {
		return replacements;
	}

	public @NotNull ItemStack toItem() {
		Item.Edit edit = new Item.Edit(Items.findMaterial(node.getNode("type").toPrimitive().getString()));
		String label = node.getPath();
		String[] split = label.split("\\.");
		String c = split[split.length - 1];
		if (!c.startsWith("!")) {
			edit.setTitle(c);
		}
		if (node.getNode("name").toPrimitive().isString()) edit.setTitle(node.getNode("name").toPrimitive().getString());
		if (node.getNode("limit").toPrimitive().isInt()) {
			limit = node.getNode("limit").toPrimitive().getInt();
		}
		if (node.isNode("replacements")) {
			Map<String, String> r = new HashMap<>();
			for (String key : node.getNode("replacements").getKeys(false)) {
				r.put(key, node.getNode("replacements").getNode(key).toPrimitive().getString());
			}
			replacements = r;
		}
		if (node.getNode("message").toPrimitive().isString()) {
			message = node.getNode("message").toPrimitive().getString();
		}
		if (node.getNode("slot").toPrimitive().isInt()) {
			slot = node.getNode("slot").toPrimitive().getInt();
		}
		if (node.getNode("locked").toPrimitive().isBoolean()) {
			notRemovable = node.getNode("locked").toPrimitive().getBoolean();
		}
		if (node.getNode("close").toPrimitive().isBoolean()) {
			exitOnClick = node.getNode("close").toPrimitive().getBoolean();
		}
		if (node.getNode("open").toPrimitive().isString()) {
			openOnClick = node.getNode("open").toPrimitive().getString();
		}
		if (node.isNode("enchantments")) {
			Node enchants = node.getNode("enchantments");
			for (String enchant : enchants.getKeys(false)) {
				Arrays.stream(Enchantment.values()).filter(en -> en.getKey().getKey().equals(enchant)).findFirst().ifPresent(e -> edit.addEnchantment(e, enchants.getNode(enchant).toPrimitive().getInt()));
			}
		}
		if (node.getNode("lore").toPrimitive().isStringList()) {
			edit.setLore(node.getNode("lore").toPrimitive().getStringList());
		}
		if (node.getNode("flags").toPrimitive().isStringList()) {
			LabyrinthCollection<ItemFlag> flags = new LabyrinthList<>();
			for (String flag : node.getNode("flags").toPrimitive().getStringList()) {
				try {
					ItemFlag f = ItemFlag.valueOf(flag);
					flags.add(f);
				} catch (Exception ignored) {}
			}
			edit.setFlags(flags.stream().toArray(ItemFlag[]::new));
		}
		return edit.build();
	}

}
