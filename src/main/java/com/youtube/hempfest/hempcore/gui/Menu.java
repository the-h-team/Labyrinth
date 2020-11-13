package com.youtube.hempfest.hempcore.gui;

import com.youtube.hempfest.hempcore.HempCore;
import com.youtube.hempfest.hempcore.formatting.string.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;


public abstract class Menu implements InventoryHolder {

	/*
	 * Defines the behavior and attributes of all menus in our plugin
	 */

	// Protected values that can be accessed in the menus
	protected GuiLibrary guiLibrary;
	protected Inventory inventory;
	protected ItemStack FILLER_GLASS = makeItem(Material.BLACK_STAINED_GLASS_PANE, " ");
	protected ItemStack FILLER_GLASS_LIGHT = makeItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,
			new ColoredString("&7&oOther items will appear here.", ColoredString.ColorType.MC).toString());

	// Constructor for Menu. Pass in a gui so that
	// we have information on who's menu this is and
	// what info is to be transfered
	public Menu(GuiLibrary guiLibrary) {
		this.guiLibrary = guiLibrary;
	}

	// let each menu decide their name
	public abstract String getMenuName();

	// let each menu decide their slot amount
	public abstract int getSlots();

	// let each menu decide how the items in the menu will be handled when clicked
	public abstract void handleMenu(InventoryClickEvent e);

	// let each menu decide what items are to be placed in the inventory menu
	public abstract void setMenuItems();

	// When called, an inventory is created and opened for the player
	public void open() {
		// The owner of the inventory created is the Menu itself,
		// so we are able to reverse engineer the Menu object from the
		// inventoryHolder in the MenuListener class when handling clicks
		inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

		// grab all the items specified to be used for this menu and add to inventory
		this.setMenuItems();

		// open the inventory for the player
		guiLibrary.getViewer().openInventory(inventory);
	}

	public void start() {
		// The owner of the inventory created is the Menu itself,
		// so we are able to reverse engineer the Menu object from the
		// inventoryHolder in the MenuListener class when handling clicks
		inventory = Bukkit.createInventory(this, InventoryType.ANVIL, getMenuName());

		// grab all the items specified to be used for this menu and add to inventory
		this.setMenuItems();

		// open the inventory for the player
		guiLibrary.getViewer().openInventory(inventory);
	}


	// Overridden method from the InventoryHolder interface
	@Override
	public Inventory getInventory() {
		return inventory;
	}

	// Helpful utility method to fill all remaining slots with "filler glass"
	public void setFillerGlass() {
		for (int i = 0; i < getSlots(); i++) {
			if (inventory.getItem(i) == null) {
				inventory.setItem(i, FILLER_GLASS);
			}
		}
	}

	protected List<String> color(String... text) {
		ArrayList<String> convert = new ArrayList<>();
		for (String t : text) {
			convert.add(new ColoredString(t, ColoredString.ColorType.MC).toString());
		}
		return convert;
	}

	public ItemStack makeItem(Material material, String displayName, String... lore) {

		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		assert itemMeta != null;
		itemMeta.setDisplayName(new ColoredString(displayName, ColoredString.ColorType.MC).toString());

		itemMeta.setLore(color(lore));
		item.setItemMeta(itemMeta);

		return item;
	}

	public ItemStack makePersistentItem(Material material, String displayName, String key, String data, String... lore) {

		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		assert itemMeta != null;
		itemMeta.setDisplayName(new ColoredString(displayName, ColoredString.ColorType.MC).toString());
		itemMeta.getPersistentDataContainer().set(new NamespacedKey(HempCore.getInstance(), key), PersistentDataType.STRING, data);
		itemMeta.setLore(color(lore));
		item.setItemMeta(itemMeta);

		return item;
	}

}
