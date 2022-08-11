package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.PlayerSearch;
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
import com.github.sanctum.panther.annotation.Comment;
import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.annotation.Voluntary;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.file.MemorySpace;
import com.github.sanctum.panther.file.Node;
import com.github.sanctum.panther.util.Check;
import com.github.sanctum.panther.util.PantherLogger;
import com.github.sanctum.skulls.CustomHead;
import com.github.sanctum.skulls.SkullType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A type of unity docket that conforms written gui structure into a usable menu instance.
 *
 * @param <T> The type of docket.
 */
public class MemoryDocket<T> implements Docket<T>, UniqueHolder {

	protected final PantherCollection<ItemElement<?>> items = new PantherList<>();
	protected Plugin plugin = LabyrinthProvider.getInstance().getPluginInstance();
	protected MemoryItem pagination, next, previous, exit, filler, border;
	protected BiFunction<String, T, String> dataConverter = (s, t) -> s;
	protected BiFunction<String, Object, String> uniqueDataConverter = (s, t) -> s;
	protected final MemorySpace memory;
	protected Supplier<List<T>> supplier;
	protected Comparator<T> comparator;
	protected Predicate<T> predicate;
	protected Object uniqueData;
	protected boolean shared;
	protected String title;
	protected String key;
	protected String nameHolder;
	protected Menu.Type type;
	protected Menu.Rows rows;
	protected Menu instance;

	public MemoryDocket(MemorySpace memorySpace) {
		this.memory = memorySpace;
	}

	@Voluntary("Used only in tandem with pagination.")
	public MemoryDocket<T> setList(@NotNull Supplier<List<T>> supplier) {
		this.supplier = supplier;
		return this;
	}

	@Voluntary("Used only in tandem with pagination.")
	public MemoryDocket<T> setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
		return this;
	}

	@Voluntary("Used only in tandem with pagination.")
	public MemoryDocket<T> setFilter(Predicate<T> predicate) {
		this.predicate = predicate;
		return this;
	}

	@Override
	@Note("This method is used for translating player names for skull items, it is expected to be the placeholder for returning a player username and is used in tandem with a Unique Data Converter")
	public MemoryDocket<T> setNamePlaceholder(@NotNull String placeholder) {
		this.nameHolder = placeholder;
		return this;
	}

	@Voluntary("This method allows you to setup custom placeholders, used only in tandem with pagination.")
	public MemoryDocket<T> setDataConverter(@NotNull BiFunction<String, T, String> function) {
		this.dataConverter = function;
		return this;
	}

	@Override
	@Note("This method is used for setting up unique translations. Example; a singular parent object being attached for extra placeholders.")
	public <V> MemoryDocket<T> setUniqueDataConverter(@NotNull V t, @NotNull BiFunction<String, V, String> function) {
		this.uniqueData = t;
		this.uniqueDataConverter = (BiFunction<String, Object, String>) function;
		return this;
	}

	@Override
	public @NotNull MemoryDocket<T> load() {
		this.title = Check.forNull(memory.getNode("title").toPrimitive().getString(), "Configured menus cannot have null titles please correct under path '" + memory.getPath() + "'");
		if (this.uniqueData != null) {
			this.title = uniqueDataConverter.apply(title, uniqueData);
		}
		this.rows = Check.forNull(Menu.Rows.valueOf(memory.getNode("rows").toPrimitive().getString()), "Configured menus need a row size please correct under path '" + memory.getPath() + "'");
		this.type = Check.forNull(Menu.Type.valueOf(memory.getNode("type").toPrimitive().getString()), "Configured menus need a valid type please correct under path '" + memory.getPath() + "'");
		this.shared = memory.getNode("shared").toPrimitive().getBoolean();
		if (memory.getNode("id").toPrimitive().isString()) {
			if (this.uniqueData != null) {
				this.key = uniqueDataConverter.apply(memory.getNode("id").toPrimitive().getString(), uniqueData);
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
				element.setElement(result);
				handlePlayerHeadLookup(true, result, element);
				if (i.getSlot() > -1) {
					element.setSlot(i.getSlot());
				}
				handleClickEvent(i, element);
				if (element.getElement().hasItemMeta() && element.getElement().getItemMeta().hasLore()) {
					List<String> lore = new ArrayList<>();
					for (String s : element.getElement().getItemMeta().getLore()) {
						String res = s;
						if (uniqueData != null) {
							res = uniqueDataConverter.apply(res, uniqueData);
						}
						lore.add(res);
					}
					element.setElement(edit -> edit.setLore(lore).build());
				}
				String res = element.getName();
				if (uniqueData != null ) {
					res = uniqueDataConverter.apply(res, uniqueData);
				}
				String finalRes = res;
				element.setElement(edit -> edit.setTitle(finalRes).build());
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
						element.setComparator((o1, o2) -> comparator.compare(o1.getData().orElse(null), o2.getData().orElse(null)));
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
								handleClickEvent(this.border, ed);
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
							handleClickEvent(this.filler, ed);
							ed.setType(ItemElement.ControlType.ITEM_FILLER);
						});
						i.addItem(filler);
					}

					final ItemStack built = pagination.toItem();
					element.setLimit(pagination.getLimit());
					element.setPopulate((value, item) -> {
						item.setElement(built);
						handlePlayerHeadLookup(false, built, item, value);
						String title = item.getName();
						if (pagination != null) {
							if (pagination.isNotRemovable()) {
								item.setClick(click -> {
									click.setCancelled(true);
									if (pagination.isExitOnClick()) click.getParent().getParent().getParent().close(click.getElement());
									if (pagination.getMessage() != null) {
										String res = handlePaginationReplacements(pagination, pagination.getMessage(), value);
										if (dataConverter != null) {
											res = dataConverter.apply(res, value);
										}
										Mailer.empty(click.getElement()).chat(res).deploy();
									}
									if (pagination.getOpenOnClick() != null) {
										String open = pagination.getOpenOnClick();
										String r = handlePaginationReplacements(pagination, open, value);
										if (dataConverter != null) {
											r = dataConverter.apply(r, value);
										}
										MenuRegistration registration = MenuRegistration.getInstance();
										Menu registered = registration.get(r).get();
										if (registered != null) {
											registered.open(click.getElement());
										} else {
											if (pagination.getOpenOnClick().startsWith("/")) {
												String command = pagination.getOpenOnClick().replace("/", "");
												String res = handlePaginationReplacements(pagination, command, value);
												if (dataConverter != null) {
													res = dataConverter.apply(res, value);
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
										String res = handlePaginationReplacements(pagination, s, value);
										if (dataConverter != null) {
											res = dataConverter.apply(res, value);
										}
										lore.add(res);
									}
									item.setElement(edit -> edit.setLore(lore).build());
								}
								String res = handlePaginationReplacements(pagination, title, value);
								if (dataConverter != null ) {
									res = dataConverter.apply(res, value);
								}
								String finalRes = res;
								item.setElement(edit -> edit.setTitle(finalRes).build());
							}
						}
					});
					i.addItem(element);
					if (!Check.isNull(next, previous)) {
						i.addItem(b -> {
									b.setElement(it -> it.setItem(next.toItem()).build()).setType(ItemElement.ControlType.BUTTON_NEXT).setSlot(next.getSlot());
									handleClickEvent(next, b);
								})
								.addItem(b -> {
									b.setElement(it -> it.setItem(previous.toItem()).build()).setType(ItemElement.ControlType.BUTTON_BACK).setSlot(previous.getSlot());
									handleClickEvent(previous, b);
								});
					}
					if (exit != null) {
						i.addItem(b -> {
							b.setElement(it -> it.setItem(exit.toItem()).build()).setType(ItemElement.ControlType.BUTTON_EXIT).setSlot(exit.getSlot());
							handleClickEvent(exit, b);
						});
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
				singular.setProperty(Menu.Property.RECURSIVE);
				singular.setStock(i -> {
					items.forEach(i::addItem);
					if (this.border != null) {
						BorderElement<?> border = new BorderElement<>(i);
						for (Menu.Panel p : Menu.Panel.values()) {
							if (p == Menu.Panel.MIDDLE) continue;
							border.add(p, ed -> {
								ItemStack built = this.border.toItem();
								ed.setElement(built);
								if (this.border != null) {
									handleClickEvent(this.border, ed);
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
							if (this.filler != null) {
								handleClickEvent(this.filler, ed);
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

	protected void handleClickEvent(MemoryItem item, ItemElement<?> ed) {
		if (item.isNotRemovable()) {
			ed.setClick(click -> {
				click.setCancelled(true);
				if (item.isExitOnClick()) click.getParent().getParent().getParent().close(click.getElement());
				if (item.getMessage() != null) {
					String message = item.getMessage();
					if (uniqueData != null) {
						message = uniqueDataConverter.apply(message, uniqueData);
					}
					Mailer.empty(click.getElement()).chat(message).deploy();
				}
				if (item.getOpenOnClick() != null) {
					String open = item.getOpenOnClick();
					if (uniqueData != null) {
						open = uniqueDataConverter.apply(open, uniqueData);
					}
					MenuRegistration registration = MenuRegistration.getInstance();
					Menu registered = registration.get(open).get();
					if (registered != null) {
						registered.open(click.getElement());
					} else {
						if (item.getOpenOnClick().startsWith("/")) {
							String command = item.getOpenOnClick().replace("/", "");
							if (uniqueData != null) {
								command = uniqueDataConverter.apply(command, uniqueData);
							}
							click.getElement().performCommand(command);
						}
					}
				}
			});
		}
	}

	@Comment("Handle player head to user translations, local being a unique object instead of paginated.")
	protected void handlePlayerHeadLookup(boolean local, ItemStack built, ItemElement<?> item, Object... args) {
		boolean pass = local ? !Check.isNull(uniqueData, uniqueDataConverter, nameHolder) : !Check.isNull(dataConverter, nameHolder);
		if (pass && new FormattedString(built.getType().name()).contains("player_head", "skull_item")) {
			String name = local ? uniqueDataConverter.apply(nameHolder, uniqueData) : dataConverter.apply(nameHolder, (T) args[0]);
			PlayerSearch search = PlayerSearch.of(name);
			if (search != null) {
				ItemStack head = CustomHead.Manager.get(search.getPlayer());
				item.setElement(edit -> edit.setItem(head != null ? head : SkullType.PLAYER.get()).build());
				if (built.hasItemMeta()) {
					if (built.getItemMeta().hasDisplayName()) {
						item.setElement(edit -> edit.setTitle(built.getItemMeta().getDisplayName()).build());
					}
					if (built.getItemMeta().hasLore()) {
						item.setElement(edit -> edit.setLore(built.getItemMeta().getLore()).build());
					}
				}
			}
		}
	}

	@Comment("Handle memory item placeholder translation on a string with a provided value")
	protected String handlePaginationReplacements(MemoryItem item, String context, T value) {
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
