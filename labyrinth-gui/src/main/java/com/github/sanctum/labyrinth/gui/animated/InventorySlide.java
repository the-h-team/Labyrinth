package com.github.sanctum.labyrinth.gui.animated;

import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventorySlide {

	protected final List<Element> items = new LinkedList<>();
	protected final List<Element.Action.Passthrough> action = new LinkedList<>();

	public InventorySlide fill(int amount) {
		for (int i = 1; i < amount + 2; i++) {
			then(i).skip();
		}
		return this;
	}

	public Element.Action.Passthrough then(int slot) {
		return new Element.Action.Passthrough(slot, this);
	}

	public LinkedList<Element> getElements() {
		LinkedList<Element> obj = new LinkedList<>(this.items);
		obj.sort(Comparator.comparingInt(Element::getSlot));
		return obj;
	}

	public enum Direction {
		FORWARD, BACKWARD, RESET
	}

	public static class Element {

		private final ItemStack item;

		private final int slot;

		protected Element(ItemStack item, int slot) {
			this.item = item;
			this.slot = slot;
		}

		public ItemStack getItem() {
			return item;
		}

		public int getSlot() {
			return slot;
		}

		public static class Update {

			private final LiveInventory inventory;
			private ItemStack item;
			private final int slot;
			private final int slide;
			private final long time;
			private final Player player;

			public Update(LiveInventory inventory, ItemStack item, int slot, int slide, long time, Player p) {
				this.inventory = inventory;
				this.player = p;
				this.time = time;
				this.slide = slide;
				this.slot = slot;
				this.item = item;
			}

			public Player getPlayer() {
				return player;
			}

			public int getSlide() {
				return slide;
			}

			public LiveInventory getInventory() {
				return inventory;
			}

			public TimeWatch.Recording getRecording() {
				return TimeWatch.Recording.subtract(time);
			}

			public int getSlot() {
				return slot;
			}

			public void setType(Material material) {
				setItem(new ItemStack(material));
			}

			public void setItem(ItemStack item) {
				this.item = item;
			}

			public void setItem(Supplier<ItemStack> item) {
				this.item = item.get();
			}

			public void build(int slot, Consumer<Update> updateConsumer) {
				if (getSlot() == slot) {
					updateConsumer.accept(this);
				}
			}

			public void build(Consumer<Update> update, int... i) {
				for (int in : i) {
					if (getSlot() == in) {
						update.accept(this);
					}
				}
			}

			public void setTitle(String text) {
				setProperties(meta -> meta.setDisplayName(StringUtils.use(text).translate()));
			}

			public void setLore(String... text) {
				List<String> list = new ArrayList<>();
				for (String s : text) {
					list.add(StringUtils.use(s).translate());
				}
				setProperties(meta -> meta.setLore(list));
			}

			public void setProperties(Consumer<ItemMeta> consumer) {
				ItemMeta meta = item.getItemMeta();
				consumer.accept(meta);
				item.setItemMeta(meta);
			}

			protected ItemStack getItem() {
				return item;
			}
		}

		@FunctionalInterface
		public interface Action {

			boolean apply(LiveInventory gui, Player player, InventoryView view, ItemStack item);

			class Passthrough {

				private final int slot;

				private Action action;

				private final InventorySlide slide;

				public Passthrough(int slot, InventorySlide slide) {
					this.slot = slot;
					this.slide = slide;
				}

				protected int getSlot() {
					return slot;
				}

				@SuppressWarnings("UnusedReturnValue")
				protected InventorySlide skip() {
					this.slide.items.add(new Element(new Item.Edit(Material.PAPER).setTitle(" ").setLore("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&cNon-customized item.", "&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").build(), Math.max(slot - 1, 0)));
					return this.slide;
				}

				public InventorySlide apply(Action action) {
					this.action = action;
					this.slide.items.add(new Element(new Item.Edit(Material.PAPER).setTitle(" ").setLore("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&cNon-customized item.", "&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").build(), Math.max(slot - 1, 0)));
					this.slide.action.add(this);
					return this.slide;
				}

				protected Action getAction() {
					return action;
				}
			}

			@FunctionalInterface
			interface Shutdown {

				void accept(LiveInventory gui, Player p, InventoryView view);

			}
		}
	}
}
