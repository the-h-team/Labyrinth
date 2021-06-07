package com.github.sanctum.labyrinth.gui.animated;

import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.task.Asynchronous;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class LiveInventory implements Listener {

	private String TITLE;
	private InventorySlide.Element.Action.Shutdown CLOSE;
	private InventoryRows ROWS;
	private int DELAY = 1;
	private int PERIOD = 4;
	private boolean REVERT;
	private ItemStack BORDER_ITEM;
	private ItemStack FILLER_ITEM;
	private Plugin PLUGIN;

	private Consumer<InventorySlide.Element.Update> UPDATE;

	private final LinkedList<InventorySlide> SLIDES;
	private final Set<Player> VIEWERS;
	private final Map<Player, Long> VIEW_TIME;
	private final Map<Player, Listener> LISTENER;
	private final Map<Player, Integer> POSITION;
	private final Map<Player, Inventory> INV;
	private final Map<ItemStack, Integer> EXTRAS;
	private final Map<Integer, InventorySlide.Element.Action> ACTIONS;
	private final Map<Player, Asynchronous> TASKS;
	private final Map<Player, Boolean> REVERSING;

	public LiveInventory() {
		this.EXTRAS = new HashMap<>();
		this.LISTENER = new HashMap<>();
		this.VIEW_TIME = new HashMap<>();
		this.INV = new HashMap<>();
		this.ACTIONS = new HashMap<>();
		this.SLIDES = new LinkedList<>();
		this.POSITION = new HashMap<>();
		this.REVERSING = new HashMap<>();
		this.VIEWERS = new HashSet<>();
		this.TASKS = new HashMap<>();
	}

	public LiveInventory initialize(Plugin plugin) {
		if (this.PLUGIN == null) {
			this.PLUGIN = plugin;
		}
		return this;
	}

	public LiveInventory title(String title) {
		this.TITLE = title;
		return this;
	}

	public LiveInventory size(InventoryRows size) {
		this.ROWS = size;
		return this;
	}

	public LiveInventory fill(ItemStack item) {
		this.FILLER_ITEM = new ItemStack(item);
		return this;
	}

	public LiveInventory border(ItemStack item) {
		this.BORDER_ITEM = new ItemStack(item);
		return this;
	}

	public LiveInventory layer(int slides) {
		for (int i = 0; i < slides; i++) {
			InventorySlide slide = new InventorySlide().fill(slides);
			this.SLIDES.add(slide);
			if (slide.ACTION != null) {
				for (InventorySlide.Element.Action.Passthrough passthrough : slide.ACTION) {
					this.ACTIONS.put(Math.max(passthrough.getSlot() - 1, 0), passthrough.getAction());
				}
			}
		}
		return this;
	}

	public LiveInventory add(ItemStack item, int slot, InventorySlide.Element.Action action) {
		this.EXTRAS.put(item, slot);
		this.ACTIONS.put(slot, action);
		return this;
	}

	public LiveInventory then(InventorySlide slide) {
		this.SLIDES.add(slide);
		if (slide.ACTION != null) {
			for (InventorySlide.Element.Action.Passthrough passthrough : slide.ACTION) {
				this.ACTIONS.put(Math.max(passthrough.getSlot() - 1, 0), passthrough.getAction());
			}
		}
		return this;
	}

	public LiveInventory decorate(Consumer<InventorySlide.Element.Update> consumer) {
		this.UPDATE = consumer;
		return this;
	}

	public LiveInventory withRevert() {
		this.REVERT = true;
		return this;
	}

	public LiveInventory delay(int delay) {
		this.DELAY = delay;
		return this;
	}

	public LiveInventory personal(int delay, int period, Player p) {
		schedule(p).repeat(delay, period);
		return this;
	}

	public LiveInventory setClose(InventorySlide.Element.Action.Shutdown action) {
		this.CLOSE = action;
		return this;
	}

	public LiveInventory timeout(int period) {
		this.PERIOD = period;
		return this;
	}

	protected int getPosition(Player target) {
		if (!POSITION.containsKey(target)) {
			POSITION.put(target, 0);
		}
		return POSITION.getOrDefault(target, 0);
	}

	public InventorySlide getSlide(int index) {
		return this.SLIDES.get(Math.max(Math.max(index, this.SLIDES.size() - 1), 0));
	}

	public InventorySlide getSlide(Player p) {
		return this.SLIDES.get(getPosition(p));
	}

	public List<InventorySlide> getSlides() {
		return Collections.unmodifiableList(this.SLIDES);
	}

	protected Asynchronous schedule(Player p) {

		if (!this.LISTENER.containsKey(p)) {
			this.LISTENER.put(p, new L(p));
			Bukkit.getPluginManager().registerEvents(this.LISTENER.get(p), this.PLUGIN);
		}

		this.VIEW_TIME.put(p, System.currentTimeMillis());

		if (!this.INV.containsKey(p)) {
			this.INV.put(p, Bukkit.createInventory(null, ROWS.getSlotCount(), StringUtils.use(this.TITLE).translate()));
		}

		if (this.TASKS.containsKey(p)) {
			this.TASKS.get(p).cancelTask();
			this.TASKS.remove(p);
		}

		this.TASKS.put(p, Schedule.async(() -> {

			int pos = getPosition(p);

			if (pos + 1 >= SLIDES.size()) {
				if (this.REVERT) {
					setDirection(p, InventorySlide.Direction.BACKWARD);
				} else {
					setDirection(p, InventorySlide.Direction.RESET);
				}
			} else {
				if (isReverting(p)) {
					if (getPosition(p) == 0) {
						setDirection(p, InventorySlide.Direction.FORWARD);
					} else {
						setDirection(p, InventorySlide.Direction.BACKWARD);
					}
				} else {
					setDirection(p, InventorySlide.Direction.FORWARD);
				}
			}

			InventorySlide slide = SLIDES.get(pos);

			for (InventorySlide.Element object : slide.getElements()) {
				InventorySlide.Element.Update event = new InventorySlide.Element.Update(this, object.getItem(), Math.max(Math.min(object.getSlot() + 1, this.ROWS.getSlotCount()), 0), pos + 1, this.VIEW_TIME.get(p), p);
				if (this.UPDATE != null) {
					this.UPDATE.accept(event);
				}
				Schedule.sync(() -> this.INV.get(p).setItem(object.getSlot(), event.getItem())).run();
			}

			Schedule.sync(() -> {

				if (this.BORDER_ITEM != null) {
					switch (this.ROWS.getSlotCount()) {
						case 27:
							int f;
							for (f = 0; f < 10; f++) {
								if (getInventory(p).getItem(f) == null)
									getInventory(p).setItem(f, this.BORDER_ITEM);
							}
							getInventory(p).setItem(17, this.BORDER_ITEM);
							for (f = 18; f < 27; f++) {
								if (getInventory(p).getItem(f) == null)
									getInventory(p).setItem(f, this.BORDER_ITEM);
							}
							break;
						case 36:
							int h;
							for (h = 0; h < 10; h++) {
								if (getInventory(p).getItem(h) == null)
									getInventory(p).setItem(h, this.BORDER_ITEM);
							}
							getInventory(p).setItem(17, this.BORDER_ITEM);
							getInventory(p).setItem(18, this.BORDER_ITEM);
							getInventory(p).setItem(26, this.BORDER_ITEM);
							for (h = 27; h < 36; h++) {
								if (getInventory(p).getItem(h) == null)
									getInventory(p).setItem(h, this.BORDER_ITEM);
							}
							break;
						case 45:
							int o;
							for (o = 0; o < 10; o++) {
								if (getInventory(p).getItem(o) == null)
									getInventory(p).setItem(o, this.BORDER_ITEM);
							}
							getInventory(p).setItem(17, this.BORDER_ITEM);
							getInventory(p).setItem(18, this.BORDER_ITEM);
							getInventory(p).setItem(26, this.BORDER_ITEM);
							getInventory(p).setItem(27, this.BORDER_ITEM);
							getInventory(p).setItem(35, this.BORDER_ITEM);
							getInventory(p).setItem(36, this.BORDER_ITEM);
							for (o = 36; o < 45; o++) {
								if (getInventory(p).getItem(o) == null)
									getInventory(p).setItem(o, this.BORDER_ITEM);
							}
							break;
						case 54:
							int j;
							for (j = 0; j < 10; j++) {
								if (getInventory(p).getItem(j) == null)
									getInventory(p).setItem(j, this.BORDER_ITEM);
							}
							getInventory(p).setItem(17, this.BORDER_ITEM);
							getInventory(p).setItem(18, this.BORDER_ITEM);
							getInventory(p).setItem(26, this.BORDER_ITEM);
							getInventory(p).setItem(27, this.BORDER_ITEM);
							getInventory(p).setItem(35, this.BORDER_ITEM);
							getInventory(p).setItem(36, this.BORDER_ITEM);
							for (j = 44; j < 54; j++) {
								if (getInventory(p).getItem(j) == null)
									getInventory(p).setItem(j, this.BORDER_ITEM);
							}
							break;
					}
				}

				for (Map.Entry<ItemStack, Integer> entry : this.EXTRAS.entrySet()) {
					getInventory(p).setItem(entry.getValue(), entry.getKey());
				}

				if (this.FILLER_ITEM != null) {
					for (int l = 0; l < this.ROWS.getSlotCount(); l++) {
						if (getInventory(p).getItem(l) == null) {
							getInventory(p).setItem(l, this.FILLER_ITEM);
						}
					}
				}

			}).run();

		}).debug().cancelAfter(task -> {

			if (!this.getViewers().contains(p)) {
				task.cancel();
			}

		}));

		this.TASKS.get(p);
		return this.TASKS.get(p);
	}

	public LiveInventory setDirection(Player target, InventorySlide.Direction direction) {
		if (direction == InventorySlide.Direction.RESET) {
			REVERSING.put(target, false);
			POSITION.put(target, 0);
			return this;
		}
		if (direction == InventorySlide.Direction.BACKWARD) {
			REVERSING.put(target, true);
		} else {
			REVERSING.put(target, false);
		}
		int result = 0;
		if (direction == InventorySlide.Direction.BACKWARD) {
			result = getPosition(target) + -1;
		} else {
			if (direction == InventorySlide.Direction.FORWARD) {
				result = getPosition(target) + 1;
			}
		}
		POSITION.put(target, result);
		return this;
	}

	public Inventory getInventory(Player viewer) {
		return this.INV.getOrDefault(viewer, null);
	}

	public Set<Player> getViewers() {
		return this.VIEWERS;
	}

	private boolean isReverting(Player target) {
		return REVERSING.getOrDefault(target, false);
	}

	public void open(Player p) {

		if (!this.INV.containsKey(p)) {
			this.INV.put(p, Bukkit.createInventory(null, ROWS.getSlotCount(), StringUtils.use(this.TITLE).translate()));
		}

		this.VIEWERS.add(p);

		Schedule.sync(() -> p.openInventory(getInventory(p))).run();

		if (!this.LISTENER.containsKey(p)) {
			this.LISTENER.put(p, new L(p));
			Bukkit.getPluginManager().registerEvents(this.LISTENER.get(p), this.PLUGIN);
		}

		schedule(p).repeat(this.DELAY, this.PERIOD);

	}

	public void open(Player p, int minutes) {

		if (!this.INV.containsKey(p)) {
			this.INV.put(p, Bukkit.createInventory(null, ROWS.getSlotCount(), StringUtils.use(this.TITLE).translate()));
		}

		this.VIEWERS.add(p);

		Schedule.sync(() -> p.openInventory(getInventory(p))).run();

		if (!this.LISTENER.containsKey(p)) {
			this.LISTENER.put(p, new L(p));
			Bukkit.getPluginManager().registerEvents(this.LISTENER.get(p), this.PLUGIN);
		}

		schedule(p).cancelAfter(task -> {

			if (this.VIEW_TIME.containsKey(p)) {
				if (TimeUtils.isMinutesSince(new Date(this.VIEW_TIME.get(p)), minutes)) {
					Schedule.sync(p::closeInventory).run();
					task.cancel();
				}
			}

		}).repeat(this.DELAY, this.PERIOD);

	}

	public void open(Player p, Map.Entry<TimeUnit, Long> measurement) {

		if (!this.INV.containsKey(p)) {
			this.INV.put(p, Bukkit.createInventory(null, ROWS.getSlotCount(), StringUtils.use(this.TITLE).translate()));
		}

		this.VIEWERS.add(p);

		Schedule.sync(() -> p.openInventory(getInventory(p))).run();

		if (!this.LISTENER.containsKey(p)) {
			this.LISTENER.put(p, new L(p));
			Bukkit.getPluginManager().registerEvents(this.LISTENER.get(p), this.PLUGIN);
		}

		schedule(p).cancelAfter(task -> {

			if (this.VIEW_TIME.containsKey(p)) {
				if (TimeWatch.start(this.VIEW_TIME.get(p)).isGreaterThan(measurement.getKey(), measurement.getValue())) {
					Schedule.sync(p::closeInventory).run();
					task.cancel();
				}
			}

		}).repeat(this.DELAY, this.PERIOD);

	}


	protected class L implements Listener {

		private final Player p;

		public L(Player p) {
			this.p = p;
		}

		@EventHandler
		public void onLeave(PlayerQuitEvent e) {
			if (!e.getPlayer().equals(p))
				return;

			LiveInventory.this.VIEWERS.remove(e.getPlayer());
		}

		@EventHandler
		public void onClick(InventoryClickEvent e) {

			if (!(e.getWhoClicked() instanceof Player))
				return;

			if (!e.getWhoClicked().equals(p))
				return;

			if (e.getClickedInventory() == null)
				return;

			Inventory test = LiveInventory.this.getInventory((Player) e.getWhoClicked());

			if (test == null)
				return;

			if (e.getClickedInventory().equals(test)) {

				if (e.getCurrentItem() != null) {

					InventorySlide.Element.Action data = LiveInventory.this.ACTIONS.get(e.getSlot());

					if (data != null) {

						if (data.apply(LiveInventory.this, (Player) e.getWhoClicked(), e.getView(), e.getCurrentItem())) {
							e.setCancelled(true);
						}

					}

				}

			}

		}

		@EventHandler
		public void onClose(InventoryCloseEvent e) {

			if (!(e.getPlayer() instanceof Player))
				return;

			Inventory test = LiveInventory.this.getInventory((Player) e.getPlayer());

			if (test == null)
				return;

			Player p = (Player) e.getPlayer();

			if (!p.equals(this.p))
				return;

			if (e.getInventory().equals(test)) {
				if (LiveInventory.this.TASKS.containsKey(p)) {
					if (LiveInventory.this.CLOSE != null) {
						LiveInventory.this.CLOSE.accept(LiveInventory.this, p, e.getView());
					}
					LiveInventory.this.TASKS.get(p).cancelTask();
					LiveInventory.this.VIEW_TIME.remove(p);
					LiveInventory.this.INV.remove(p);
					LiveInventory.this.TASKS.remove(p);
					LiveInventory.this.VIEWERS.remove(p);
					HandlerList.unregisterAll(LiveInventory.this.LISTENER.get(p));
					LiveInventory.this.LISTENER.remove(p);
				}
			}
		}


	}


}
