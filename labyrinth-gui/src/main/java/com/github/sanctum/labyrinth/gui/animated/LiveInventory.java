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

	// Base fields (relocated from old constructor)
	private final Map<ItemStack, Integer> extras = new HashMap<>();
	private final Map<Player, Listener> listener = new HashMap<>();
	private final Map<Player, Long> viewTime = new HashMap<>();
	private final Map<Player, Inventory> inv = new HashMap<>();
	private final Map<Integer, InventorySlide.Element.Action> actions = new HashMap<>();
	private final LinkedList<InventorySlide> slides = new LinkedList<>();
	private final Map<Player, Integer> position = new HashMap<>();
	private final Map<Player, Boolean> reversing = new HashMap<>();
	private final Set<Player> viewers = new HashSet<>();
	private final Map<Player, Asynchronous> tasks = new HashMap<>();

	private String title;
	private InventorySlide.Element.Action.Shutdown close;
	private InventoryRows rows;
	private int delay = 1;
	private int period = 4;
	private boolean revert;
	private ItemStack borderItem;
	private ItemStack fillerItem;
	private Plugin plugin;

	private Consumer<InventorySlide.Element.Update> UPDATE;

	public LiveInventory initialize(Plugin plugin) {
		if (this.plugin == null) {
			this.plugin = plugin;
		}
		return this;
	}

	public LiveInventory title(String title) {
		this.title = title;
		return this;
	}

	public LiveInventory size(InventoryRows size) {
		this.rows = size;
		return this;
	}

	public LiveInventory fill(ItemStack item) {
		this.fillerItem = new ItemStack(item);
		return this;
	}

	public LiveInventory border(ItemStack item) {
		this.borderItem = new ItemStack(item);
		return this;
	}

	public LiveInventory layer(int slides) {
		for (int i = 0; i < slides; i++) {
			InventorySlide slide = new InventorySlide().fill(slides);
			this.slides.add(slide);
			for (InventorySlide.Element.Action.Passthrough passthrough : slide.action) {
				this.actions.put(Math.max(passthrough.getSlot() - 1, 0), passthrough.getAction());
			}
		}
		return this;
	}

	public LiveInventory add(ItemStack item, int slot, InventorySlide.Element.Action action) {
		this.extras.put(item, slot);
		this.actions.put(slot, action);
		return this;
	}

	public LiveInventory then(InventorySlide slide) {
		this.slides.add(slide);
		for (InventorySlide.Element.Action.Passthrough passthrough : slide.action) {
			this.actions.put(Math.max(passthrough.getSlot() - 1, 0), passthrough.getAction());
		}
		return this;
	}

	public LiveInventory decorate(Consumer<InventorySlide.Element.Update> consumer) {
		this.UPDATE = consumer;
		return this;
	}

	public LiveInventory withRevert() {
		this.revert = true;
		return this;
	}

	public LiveInventory delay(int delay) {
		this.delay = delay;
		return this;
	}

	public LiveInventory personal(int delay, int period, Player p) {
		schedule(p).repeat(delay, period);
		return this;
	}

	public LiveInventory setClose(InventorySlide.Element.Action.Shutdown action) {
		this.close = action;
		return this;
	}

	public LiveInventory timeout(int period) {
		this.period = period;
		return this;
	}

	protected int getPosition(Player target) {
		if (!position.containsKey(target)) {
			position.put(target, 0);
		}
		return position.getOrDefault(target, 0);
	}

	public InventorySlide getSlide(int index) {
		return this.slides.get(Math.max(Math.max(index, this.slides.size() - 1), 0));
	}

	public InventorySlide getSlide(Player p) {
		return this.slides.get(getPosition(p));
	}

	public List<InventorySlide> getSlides() {
		return Collections.unmodifiableList(this.slides);
	}

	protected Asynchronous schedule(Player p) {

		if (!this.listener.containsKey(p)) {
			this.listener.put(p, new L(p));
			Bukkit.getPluginManager().registerEvents(this.listener.get(p), this.plugin);
		}

		this.viewTime.put(p, System.currentTimeMillis());

		if (!this.inv.containsKey(p)) {
			this.inv.put(p, Bukkit.createInventory(null, rows.getSlotCount(), StringUtils.use(this.title).translate()));
		}

		if (this.tasks.containsKey(p)) {
			this.tasks.get(p).cancelTask();
			this.tasks.remove(p);
		}

		this.tasks.put(p, Schedule.async(() -> {

			int pos = getPosition(p);

			if (pos + 1 >= slides.size()) {
				if (this.revert) {
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

			InventorySlide slide = slides.get(pos);

			for (InventorySlide.Element object : slide.getElements()) {
				InventorySlide.Element.Update event = new InventorySlide.Element.Update(this, object.getItem(), Math.max(Math.min(object.getSlot() + 1, this.rows.getSlotCount()), 0), pos + 1, this.viewTime.get(p), p);
				if (this.UPDATE != null) {
					this.UPDATE.accept(event);
				}
				Schedule.sync(() -> this.inv.get(p).setItem(object.getSlot(), event.getItem())).run();
			}

			Schedule.sync(() -> {

				if (this.borderItem != null) {
					switch (this.rows.getSlotCount()) {
						case 27:
							int f;
							for (f = 0; f < 10; f++) {
								if (getInventory(p).getItem(f) == null)
									getInventory(p).setItem(f, this.borderItem);
							}
							getInventory(p).setItem(17, this.borderItem);
							for (f = 18; f < 27; f++) {
								if (getInventory(p).getItem(f) == null)
									getInventory(p).setItem(f, this.borderItem);
							}
							break;
						case 36:
							int h;
							for (h = 0; h < 10; h++) {
								if (getInventory(p).getItem(h) == null)
									getInventory(p).setItem(h, this.borderItem);
							}
							getInventory(p).setItem(17, this.borderItem);
							getInventory(p).setItem(18, this.borderItem);
							getInventory(p).setItem(26, this.borderItem);
							for (h = 27; h < 36; h++) {
								if (getInventory(p).getItem(h) == null)
									getInventory(p).setItem(h, this.borderItem);
							}
							break;
						case 45:
							int o;
							for (o = 0; o < 10; o++) {
								if (getInventory(p).getItem(o) == null)
									getInventory(p).setItem(o, this.borderItem);
							}
							getInventory(p).setItem(17, this.borderItem);
							getInventory(p).setItem(18, this.borderItem);
							getInventory(p).setItem(26, this.borderItem);
							getInventory(p).setItem(27, this.borderItem);
							getInventory(p).setItem(35, this.borderItem);
							getInventory(p).setItem(36, this.borderItem);
							for (o = 36; o < 45; o++) {
								if (getInventory(p).getItem(o) == null)
									getInventory(p).setItem(o, this.borderItem);
							}
							break;
						case 54:
							int j;
							for (j = 0; j < 10; j++) {
								if (getInventory(p).getItem(j) == null)
									getInventory(p).setItem(j, this.borderItem);
							}
							getInventory(p).setItem(17, this.borderItem);
							getInventory(p).setItem(18, this.borderItem);
							getInventory(p).setItem(26, this.borderItem);
							getInventory(p).setItem(27, this.borderItem);
							getInventory(p).setItem(35, this.borderItem);
							getInventory(p).setItem(36, this.borderItem);
							for (j = 44; j < 54; j++) {
								if (getInventory(p).getItem(j) == null)
									getInventory(p).setItem(j, this.borderItem);
							}
							break;
					}
				}

				for (Map.Entry<ItemStack, Integer> entry : this.extras.entrySet()) {
					getInventory(p).setItem(entry.getValue(), entry.getKey());
				}

				if (this.fillerItem != null) {
					for (int l = 0; l < this.rows.getSlotCount(); l++) {
						if (getInventory(p).getItem(l) == null) {
							getInventory(p).setItem(l, this.fillerItem);
						}
					}
				}

			}).run();

		}).debug().cancelAfter(task -> {

			if (!this.getViewers().contains(p)) {
				task.cancel();
			}

		}));

		this.tasks.get(p);
		return this.tasks.get(p);
	}

	@SuppressWarnings("UnusedReturnValue")
	public LiveInventory setDirection(Player target, InventorySlide.Direction direction) {
		if (direction == InventorySlide.Direction.RESET) {
			reversing.put(target, false);
			position.put(target, 0);
			return this;
		}
		if (direction == InventorySlide.Direction.BACKWARD) {
			reversing.put(target, true);
		} else {
			reversing.put(target, false);
		}
		int result = 0;
		if (direction == InventorySlide.Direction.BACKWARD) {
			result = getPosition(target) + -1;
		} else {
			if (direction == InventorySlide.Direction.FORWARD) {
				result = getPosition(target) + 1;
			}
		}
		position.put(target, result);
		return this;
	}

	public Inventory getInventory(Player viewer) {
		return this.inv.getOrDefault(viewer, null);
	}

	public Set<Player> getViewers() {
		return this.viewers;
	}

	private boolean isReverting(Player target) {
		return reversing.getOrDefault(target, false);
	}

	public void open(Player p) {

		if (!this.inv.containsKey(p)) {
			this.inv.put(p, Bukkit.createInventory(null, rows.getSlotCount(), StringUtils.use(this.title).translate()));
		}

		this.viewers.add(p);

		Schedule.sync(() -> p.openInventory(getInventory(p))).run();

		if (!this.listener.containsKey(p)) {
			this.listener.put(p, new L(p));
			Bukkit.getPluginManager().registerEvents(this.listener.get(p), this.plugin);
		}

		schedule(p).repeat(this.delay, this.period);

	}

	public void open(Player p, int minutes) {

		if (!this.inv.containsKey(p)) {
			this.inv.put(p, Bukkit.createInventory(null, rows.getSlotCount(), StringUtils.use(this.title).translate()));
		}

		this.viewers.add(p);

		Schedule.sync(() -> p.openInventory(getInventory(p))).run();

		if (!this.listener.containsKey(p)) {
			this.listener.put(p, new L(p));
			Bukkit.getPluginManager().registerEvents(this.listener.get(p), this.plugin);
		}

		schedule(p).cancelAfter(task -> {

			if (this.viewTime.containsKey(p)) {
				if (TimeUtils.isMinutesSince(new Date(this.viewTime.get(p)), minutes)) {
					Schedule.sync(p::closeInventory).run();
					task.cancel();
				}
			}

		}).repeat(this.delay, this.period);

	}

	public void open(Player p, Map.Entry<TimeUnit, Long> measurement) {

		if (!this.inv.containsKey(p)) {
			this.inv.put(p, Bukkit.createInventory(null, rows.getSlotCount(), StringUtils.use(this.title).translate()));
		}

		this.viewers.add(p);

		Schedule.sync(() -> p.openInventory(getInventory(p))).run();

		if (!this.listener.containsKey(p)) {
			this.listener.put(p, new L(p));
			Bukkit.getPluginManager().registerEvents(this.listener.get(p), this.plugin);
		}

		schedule(p).cancelAfter(task -> {

			if (this.viewTime.containsKey(p)) {
				if (TimeWatch.start(this.viewTime.get(p)).isGreaterThan(measurement.getKey(), measurement.getValue())) {
					Schedule.sync(p::closeInventory).run();
					task.cancel();
				}
			}

		}).repeat(this.delay, this.period);

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

			LiveInventory.this.viewers.remove(e.getPlayer());
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

					InventorySlide.Element.Action data = LiveInventory.this.actions.get(e.getSlot());

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
				if (LiveInventory.this.tasks.containsKey(p)) {
					if (LiveInventory.this.close != null) {
						LiveInventory.this.close.accept(LiveInventory.this, p, e.getView());
					}
					LiveInventory.this.tasks.get(p).cancelTask();
					LiveInventory.this.viewTime.remove(p);
					LiveInventory.this.inv.remove(p);
					LiveInventory.this.tasks.remove(p);
					LiveInventory.this.viewers.remove(p);
					HandlerList.unregisterAll(LiveInventory.this.listener.get(p));
					LiveInventory.this.listener.remove(p);
				}
			}
		}


	}


}
