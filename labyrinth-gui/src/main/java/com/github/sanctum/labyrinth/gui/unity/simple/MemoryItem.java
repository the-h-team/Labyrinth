package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.file.Node;
import com.github.sanctum.panther.util.Check;
import com.github.sanctum.panther.util.PantherLogger;
import com.github.sanctum.skulls.CustomHead;
import com.github.sanctum.skulls.CustomHeadLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MemoryItem {

	protected final Node node;
	protected Map<String, String> replacements;
	protected boolean notRemovable;
	protected boolean exitOnClick;
	protected String openOnClick;
	protected String message;
	protected int slot = -1;
	protected int limit = 5;

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

	private ItemStack improvise(String value) {
		Material mat = Items.findMaterial(value);
		if (mat != null) {
			return new ItemStack(mat);
		} else {
			if (value.length() < 26) {
				return CustomHead.Manager.getHeads().stream().filter(h -> StringUtils.use(h.name()).containsIgnoreCase(value)).map(CustomHead::get).findFirst().orElse(null);
			} else {
				return CustomHeadLoader.provide(value);
			}
		}
	}

	public @NotNull ItemStack toItem() {
		String type = node.getNode("type").toPrimitive().getString();
		ItemStack test = improvise(type);
		Item.Edit edit = new Item.Edit(Check.forNull(test, "Item '" + type + "' not found. Key:" + node.getPath()));
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
			PantherCollection<ItemFlag> flags = new PantherList<>();
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
