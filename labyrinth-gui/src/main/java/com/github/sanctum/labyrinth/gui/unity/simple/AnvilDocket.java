package com.github.sanctum.labyrinth.gui.unity.simple;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.MenuRegistration;
import com.github.sanctum.labyrinth.gui.unity.impl.MenuType;
import com.github.sanctum.labyrinth.interfacing.UnknownGeneric;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.panther.file.MemorySpace;
import com.github.sanctum.panther.util.Check;
import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;

public class AnvilDocket implements Docket<UnknownGeneric>, UniqueHolder {

	final MemorySpace memory;
	String title;
	MemoryItem item;
	Object uniqueData;
	BiFunction<String, Object, String> uniqueDataConverter;
	Menu menu;

	public AnvilDocket(@NotNull MemorySpace memorySpace) {
		this.memory = memorySpace;
	}

	@Override
	public <V> AnvilDocket setUniqueDataConverter(@NotNull V data, @NotNull BiFunction<String, V, String> function) {
		this.uniqueData = data;
		this.uniqueDataConverter = (BiFunction<String, Object, String>) function;
		return this;
	}

	@Override
	public @NotNull Menu toMenu() {
		return menu;
	}

	@Override
	public @NotNull AnvilDocket load() {
		this.title = Check.forNull(memory.getNode("title").toPrimitive().getString(), "Configured menus cannot have null titles please correct under path '" + memory.getPath() + "'");
		if (uniqueData != null) {
			this.title = uniqueDataConverter.apply(this.title, uniqueData);
		}
		this.item = new MemoryItem(memory.getNode("item"));
		this.menu = MenuType.PRINTABLE.build()
				.setTitle(title)
				.setSize(Menu.Rows.ONE)
				.setHost(LabyrinthProvider.getInstance().getPluginInstance())
				.setStock(i -> i.addItem(b -> b.setElement(it -> it.setItem(item.toItem()).build()).setSlot(0).setClick(c -> {
					c.setCancelled(true);
					c.setHotbarAllowed(false);
				}))).join()
				.addAction(click -> {
					if (click.getSlot() == 2) {
						if (item.isNotRemovable()) {
							handleClickEvent(item, click.getParent().getName()).apply(click);
						}
					}

				});
		return this;
	}

	protected Menu.Click handleClickEvent(MemoryItem item, String text) {
		return click -> {
			click.setCancelled(true);
			if (item.isExitOnClick()) click.getParent().getParent().getParent().close(click.getElement());
			if (item.getMessage() != null) {
				String message = item.getMessage().replace(":message:", text);
				if (uniqueData != null) {
					message = uniqueDataConverter.apply(message, uniqueData);
				}
				Mailer.empty(click.getElement()).chat(message).deploy();
			}
			if (item.getOpenOnClick() != null) {
				String open = item.getOpenOnClick();
				MenuRegistration registration = MenuRegistration.getInstance();
				Menu registered = registration.get(open).get();
				if (registered != null) {
					registered.open(click.getElement());
				} else {
					if (item.getOpenOnClick().startsWith("/")) {
						String command = item.getOpenOnClick().replace("/", "").replace(":message:", text);
						if (uniqueData != null) {
							command = uniqueDataConverter.apply(command, uniqueData);
						}
						click.getElement().performCommand(command);
					}
				}
			}
		};
	}

	@Override
	public @NotNull Type getType() {
		return Type.MEMORY;
	}
}
