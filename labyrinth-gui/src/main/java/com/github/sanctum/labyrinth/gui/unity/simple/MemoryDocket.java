package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.MemorySpace;
import com.github.sanctum.labyrinth.data.Node;
import com.github.sanctum.labyrinth.data.WideFunction;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.data.service.Check;
import com.github.sanctum.labyrinth.formatting.string.FormattedString;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.MenuRegistration;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.PrintableMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;
import com.github.sanctum.labyrinth.gui.unity.impl.BorderElement;
import com.github.sanctum.labyrinth.gui.unity.impl.FillerElement;
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
import java.util.function.Predicate;
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
	protected MemoryItem pagination, next, previous, exit, filler, border;
	protected T target;
	protected Supplier<List<T>> supplier;
	protected Comparator<T> comparator;
	protected Predicate<T> predicate;
	protected WideFunction<String, T, String> function = (s, t) -> s;
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

	public MemoryDocket<T> filter(Predicate<T> predicate) {
		this.predicate = predicate;
		return this;
	}

	public MemoryDocket<T> replace(WideFunction<String, T, String> function) {
		this.function = function;
		return this;
	}

	public MemoryDocket<T> select(T t) {
		this.target = t;
		return this;
	}

	@Override
	public @NotNull Docket<T> load() {
		this.title = memory.getNode("title").toPrimitive().getString();
		if (this.target != null) {
			this.title = function.accept(title, target);
		}
		this.rows = Menu.Rows.valueOf(memory.getNode("rows").toPrimitive().getString());
		this.type = Menu.Type.valueOf(memory.getNode("type").toPrimitive().getString());
		this.shared = memory.getNode("shared").toPrimitive().getBoolean();
		if (memory.getNode("id").toPrimitive().isString()) {
			if (this.target != null) {
				this.key = function.accept(memory.getNode("id").toPrimitive().getString(), target);
			} else {
				this.key = memory.getNode("id").toPrimitive().getString();
			}
		}
		if (memory.isNode("filler")) {
			this.filler = new MemoryItem(memory.getNode("filler"));
		}
		if (memory.isNode("border")) {
			this.border = new MemoryItem(memory.getNode("border"));
		}
		if (memory.isNode("pagination")) {
			pagination = new MemoryItem(memory.getNode("pagination"));
			if (memory.getNode("pagination").isNode("navigation")) {
				Node parent = memory.getNode("pagination").getNode("navigation");
				next = new MemoryItem(parent.getNode("next"));
				previous = new MemoryItem(parent.getNode("previous"));
				if (parent.isNode("exit")) {
					exit = new MemoryItem(parent.getNode("exit"));
				}
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
					if (this.predicate != null) {
						element.setFilter(tItemElement -> predicate.test(tItemElement.getData().orElse(null)));
					}
					if (this.border != null) {
						BorderElement<?> border = new BorderElement<>(i);
						for (Menu.Panel p : Menu.Panel.values()) {
							if (p == Menu.Panel.MIDDLE) continue;
							border.add(p, ed -> {
								ItemStack built = this.border.toItem();
								ed.setElement(built);
								if (pagination != null) {
									if (pagination.isNotRemovable()) {
										ed.setClick(click -> {
											click.setCancelled(true);
											if (pagination.isExitOnClick()) click.getElement().closeInventory();
											if (pagination.getMessage() != null) {
												Mailer.empty(click.getElement()).chat(pagination.getMessage()).deploy();
											}
											if (pagination.getOpenOnClick() != null) {
												MenuRegistration registration = MenuRegistration.getInstance();
												Menu registered = registration.get(pagination.getOpenOnClick()).get();
												if (registered != null) {
													registered.open(click.getElement());
												} else {
													if (pagination.getOpenOnClick().startsWith("/")) {
														String command = pagination.getOpenOnClick().replace("/", "");
														click.getElement().performCommand(command);
													}
												}
											}
										});
									}
								}
								ed.setType(ItemElement.ControlType.ITEM_BORDER);
							});
						}
						i.addItem(border);
					}
					if (this.filler != null) {
						FillerElement<?> filler = new FillerElement<>(i);
						filler.add(ed -> {
							ItemStack built = this.filler.toItem();
							ed.setElement(built);
							if (pagination != null) {
								if (pagination.isNotRemovable()) {
									ed.setClick(click -> {
										click.setCancelled(true);
										if (pagination.isExitOnClick()) click.getElement().closeInventory();
										if (pagination.getMessage() != null) {
											Mailer.empty(click.getElement()).chat(pagination.getMessage()).deploy();
										}
										if (pagination.getOpenOnClick() != null) {
											MenuRegistration registration = MenuRegistration.getInstance();
											Menu registered = registration.get(pagination.getOpenOnClick()).get();
											if (registered != null) {
												registered.open(click.getElement());
											} else {
												if (pagination.getOpenOnClick().startsWith("/")) {
													String command = pagination.getOpenOnClick().replace("/", "");
													click.getElement().performCommand(command);
												}
											}
										}
									});
								}
							}
							ed.setType(ItemElement.ControlType.ITEM_FILLER);
						});
						i.addItem(filler);
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
										String res = append(pagination, pagination.getMessage(), value);
										if (function != null) {
											res = function.accept(res, value);
										}
										Mailer.empty(click.getElement()).chat(res).deploy();
									}
									if (pagination.getOpenOnClick() != null) {
										MenuRegistration registration = MenuRegistration.getInstance();
										Menu registered = registration.get(pagination.getOpenOnClick()).get();
										if (registered != null) {
											registered.open(click.getElement());
										} else {
											if (pagination.getOpenOnClick().startsWith("/")) {
												String command = pagination.getOpenOnClick().replace("/", "");
												String res = append(pagination, command, value);
												if (function != null) {
													res = function.accept(res, value);
												}
												click.getElement().performCommand(res);
											}
										}
									}
								});
							}
							if (pagination.getReplacements() != null) {
								if (item.getElement().hasItemMeta() && item.getElement().getItemMeta().hasLore()) {
									List<String> lore = new ArrayList<>();
									for (String s : item.getElement().getItemMeta().getLore()) {
										String res = append(pagination, s, value);
										if (function != null) {
											res = function.accept(res, value);
										}
										lore.add(res);
									}
									item.setElement(edit -> edit.setLore(lore).build());
								}
								String res = append(pagination, title, value);
								if (function != null ) {
									res = function.accept(res, value);
								}
								String finalRes = res;
								item.setElement(edit -> edit.setTitle(finalRes).build());
							}
						}
					});
					i.addItem(element);
					if (!Check.isNull(next, previous)) {
						i.addItem(b -> b.setElement(it -> it.setItem(next.toItem()).build()).setType(ItemElement.ControlType.BUTTON_NEXT).setSlot(next.getSlot()))
								.addItem(b -> b.setElement(it -> it.setItem(previous.toItem()).build()).setType(ItemElement.ControlType.BUTTON_BACK).setSlot(previous.getSlot()));
					}
				});
				this.instance = paginated.join();
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
				singular.setStock(i -> {
					items.forEach(i::addItem);
					if (this.border != null) {
						BorderElement<?> border = new BorderElement<>(i);
						for (Menu.Panel p : Menu.Panel.values()) {
							if (p == Menu.Panel.MIDDLE) continue;
							border.add(p, ed -> {
								ItemStack built = this.border.toItem();
								ed.setElement(built);
								if (pagination != null) {
									if (pagination.isNotRemovable()) {
										ed.setClick(click -> {
											click.setCancelled(true);
											if (pagination.isExitOnClick()) click.getElement().closeInventory();
											if (pagination.getMessage() != null) {
												Mailer.empty(click.getElement()).chat(pagination.getMessage()).deploy();
											}
											if (pagination.getOpenOnClick() != null) {
												MenuRegistration registration = MenuRegistration.getInstance();
												Menu registered = registration.get(pagination.getOpenOnClick()).get();
												if (registered != null) {
													registered.open(click.getElement());
												} else {
													if (pagination.getOpenOnClick().startsWith("/")) {
														String command = pagination.getOpenOnClick().replace("/", "");
														click.getElement().performCommand(command);
													}
												}
											}
										});
									}
								}
								ed.setType(ItemElement.ControlType.ITEM_BORDER);
							});
						}
						i.addItem(border);
					}
					if (this.filler != null) {
						FillerElement<?> filler = new FillerElement<>(i);
						filler.add(ed -> {
							ItemStack built = this.filler.toItem();
							ed.setElement(built);
							if (pagination != null) {
								if (pagination.isNotRemovable()) {
									ed.setClick(click -> {
										click.setCancelled(true);
										if (pagination.isExitOnClick()) click.getElement().closeInventory();
										if (pagination.getMessage() != null) {
											Mailer.empty(click.getElement()).chat(pagination.getMessage()).deploy();
										}
										if (pagination.getOpenOnClick() != null) {
											MenuRegistration registration = MenuRegistration.getInstance();
											Menu registered = registration.get(pagination.getOpenOnClick()).get();
											if (registered != null) {
												registered.open(click.getElement());
											} else {
												if (pagination.getOpenOnClick().startsWith("/")) {
													String command = pagination.getOpenOnClick().replace("/", "");
													click.getElement().performCommand(command);
												}
											}
										}
									});
								}
							}
							ed.setType(ItemElement.ControlType.ITEM_FILLER);
						});
						i.addItem(filler);
					}
				});
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
					Method method = value.getClass().getMethod(steps[0]);
					Object step = method.invoke(value);
					int position = 1;
					do {
						method = step.getClass().getMethod(steps[position]);
						step = method.invoke(step);
						position++;
					} while (position != steps.length);
					String rep = step.toString();
					string.replace(entry.getKey(), rep);
				} else {
					Method m = value.getClass().getMethod(entry.getValue());
					String rep = m.invoke(value).toString();
					string.replace(entry.getKey(), rep);
				}
			} catch (Exception ex) {
				plugin.getLogger().severe("Unable to resolve method name " + '"' + entry.getValue() + '"' + " from class " + value.getClass().getSimpleName() + " in menu " + '"' + this.title + '"');
			}
		}
		return string.get();
	}

	@Override
	public @NotNull Type getType() {
		return Type.MEMORY;
	}
}
