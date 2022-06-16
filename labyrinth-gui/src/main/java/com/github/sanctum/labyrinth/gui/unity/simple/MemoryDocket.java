package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.MemorySpace;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.data.container.LabyrinthSet;
import com.github.sanctum.labyrinth.data.service.Check;
import com.github.sanctum.labyrinth.formatting.string.FormattedString;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.MenuRegistration;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.PrintableMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;
import com.github.sanctum.labyrinth.gui.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ItemElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ListElement;
import com.github.sanctum.labyrinth.gui.unity.impl.MenuType;
import com.github.sanctum.labyrinth.library.Mailer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MemoryDocket<T> implements Docket<T> {

	protected final LabyrinthCollection<ItemElement<?>> items = new LabyrinthList<>();
	protected final MemorySpace memory;
	protected Plugin plugin = LabyrinthProvider.getInstance().getPluginInstance();
	protected Menu.Type type;
	protected boolean shared;
	protected String title;
	protected String key;
	protected String error;
	protected MemoryItem pagination, next, back;
	protected Supplier<List<T>> supplier;
	protected Comparator<T> comparator;
	protected Menu.Rows rows;
	protected Menu instance;

	public MemoryDocket(MemorySpace memorySpace) {
		this.memory = memorySpace;
	}

	public MemoryDocket<T> add(Supplier<List<T>> supplier) {
		this.supplier = supplier;
		return this;
	}

	public MemoryDocket<T> sort(Comparator<T> comparator) {
		this.comparator = comparator;
		return this;
	}

	@Override
	public @NotNull Docket<T> load() {
		this.title = memory.getNode("title").toPrimitive().getString();
		this.rows = Menu.Rows.valueOf(memory.getNode("rows").toPrimitive().getString());
		this.type = Menu.Type.valueOf(memory.getNode("type").toPrimitive().getString());
		this.shared = memory.getNode("shared").toPrimitive().getBoolean();
		if (memory.getNode("id").toPrimitive().isString()) {
			this.key = memory.getNode("id").toPrimitive().getString();
		}
		if (memory.isNode("pagination")) {
			pagination = new MemoryItem(memory.getNode("pagination"));
			if (memory.getNode("pagination").isNode("next_button")) {
				next = new MemoryItem(memory.getNode("pagination").getNode("next_button"));
			}
			if (memory.getNode("pagination").isNode("back_button")) {
				back = new MemoryItem(memory.getNode("pagination").getNode("back_button"));
			}
		}
		if (memory.isNode("items")) {
			for (String item : memory.getNode("items").getKeys(false)) {
				MemoryItem i = new MemoryItem(memory.getNode("items").getNode(item));
				ItemStack result = i.toItem();
				ItemElement<?> element = new ItemElement<>();
				if (i.getSlot() > -1) {
					element.setSlot(i.getSlot());
				}
				if (i.isNotRemovable()) {
					element.setClick(click -> {
						click.setCancelled(true);
						if (i.isExitOnClick()) click.getElement().closeInventory();
						if (i.getMessage() != null) Mailer.empty(click.getElement()).chat(i.getMessage()).deploy();
						if (i.getOpenOnClick() != null) {
							MenuRegistration registration = MenuRegistration.getInstance();
							Menu registered = registration.get(i.getOpenOnClick()).get();
							if (registered != null) {
								registered.open(click.getElement());
							} else {
								if (i.getOpenOnClick().startsWith("/")) {
									click.getElement().performCommand(i.getOpenOnClick().replace("/", ""));
								}
							}
						}
					});
				}
				element.setElement(result);

				items.add(element);
			}
		}
		switch (type) {
			case PAGINATED:
				Menu.Builder<PaginatedMenu, InventoryElement.Paginated> paginated = MenuType.PAGINATED.build().setHost(plugin).setTitle(title).setSize(rows);
				if (key != null) paginated.setKey(key).setProperty(Menu.Property.CACHEABLE);
				if (shared) paginated.setProperty(Menu.Property.SHAREABLE);
				paginated.setStock(i -> {
					items.forEach(i::addItem);
					ListElement<T> element = new ListElement<>(supplier);
					if (this.comparator != null) {
						element.setComparator((o1, o2) -> comparator.compare(o2.getData().orElse(null), o1.getData().orElse(null)));
					}
					final ItemStack built = pagination.toItem();
					element.setLimit(pagination.getLimit());
					element.setPopulate((value, item) -> {
						item.setElement(built);
						String title = item.getName();
						if (pagination != null) {
							if (pagination.isNotRemovable()) {
								item.setClick(click -> {
									click.setCancelled(true);
									if (pagination.isExitOnClick()) click.getElement().closeInventory();
									if (pagination.getMessage() != null) {
										Mailer.empty(click.getElement()).chat(append(pagination, pagination.getMessage(), value)).deploy();
									}
									if (pagination.getOpenOnClick() != null) {
										MenuRegistration registration = MenuRegistration.getInstance();
										Menu registered = registration.get(pagination.getOpenOnClick()).get();
										if (registered != null) {
											registered.open(click.getElement());
										} else {
											if (pagination.getOpenOnClick().startsWith("/")) {
												String command = pagination.getOpenOnClick().replace("/", "");
												click.getElement().performCommand(append(pagination, command, value));
											}
										}
									}
								});
							}
							if (pagination.getReplacements() != null) {
								if (item.getElement().hasItemMeta() && item.getElement().getItemMeta().hasLore()) {
									List<String> lore = new ArrayList<>();
									for (String s : item.getElement().getItemMeta().getLore()) {
										lore.add(append(pagination, s, value));
									}
									item.setElement(edit -> edit.setLore(lore).build());
								}
								item.setElement(edit -> edit.setTitle(append(pagination, title, value)).build());
							}
						}
					});
					i.addItem(element);
					if (!Check.isNull(next, back)) {
						i.addItem(b -> b.setElement(it -> it.setItem(next.toItem()).build()).setType(ItemElement.ControlType.BUTTON_NEXT).setSlot(next.getSlot()))
								.addItem(b -> b.setElement(it -> it.setItem(back.toItem()).build()).setType(ItemElement.ControlType.BUTTON_BACK).setSlot(back.getSlot()));
					}
				});
				this.instance = paginated.join();
				plugin.getLogger().severe(error);
				break;
			case PRINTABLE:
				Menu.Builder<PrintableMenu, InventoryElement.Printable> printable = MenuType.PRINTABLE.build().setHost(plugin).setTitle(title).setSize(rows);
				if (key != null) printable.setKey(key).setProperty(Menu.Property.CACHEABLE);
				if (shared) printable.setProperty(Menu.Property.SHAREABLE);
				printable.setStock(i -> items.forEach(i::addItem));
				this.instance = printable.join();
				break;
			case SINGULAR:
				Menu.Builder<SingularMenu, InventoryElement.Normal> singular = MenuType.SINGULAR.build().setHost(plugin).setTitle(title).setSize(rows);
				if (key != null) singular.setKey(key).setProperty(Menu.Property.CACHEABLE);
				if (shared) singular.setProperty(Menu.Property.SHAREABLE);
				singular.setStock(i -> items.forEach(i::addItem));
				this.instance = singular.join();
				break;
		}
		return this;
	}

	@Override
	public @NotNull Menu toMenu() {
		return instance;
	}

	protected String append(MemoryItem item, String context, T value) {
		final FormattedString string = new FormattedString(context);
		for (Map.Entry<String, String> entry : item.getReplacements().entrySet()) {
			try {
				if (entry.getValue().contains(".")) {
					// Here we invoke method-ception, allow the jvm to point to each specified method result.
					String[] steps = entry.getValue().split("\\.");
					Method method = value.getClass().getDeclaredMethod(steps[0]);
					Object step = method.invoke(value);
					int position = 1;
					do {
						method = step.getClass().getDeclaredMethod(steps[position]);
						step = method.invoke(step);
						position++;
					} while (position != steps.length);
					String rep = step.toString();
					string.replace(entry.getKey(), rep);
				} else {
					Method m = value.getClass().getDeclaredMethod(entry.getValue());
					String rep = m.invoke(value).toString();
					string.replace(entry.getKey(), rep);
				}
			} catch (Exception ex) {
				error = "Unable to resolve method name " + '"' + entry.getValue() + '"' + " from class " + value.getClass().getSimpleName() + " in menu " + '"' + this.title + '"';
			}
		}
		return string.get();
	}

	@Override
	public @NotNull Type getType() {
		return Type.MEMORY;
	}
}
