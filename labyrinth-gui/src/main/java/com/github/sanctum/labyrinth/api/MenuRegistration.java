package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.ServiceManager;
import com.github.sanctum.labyrinth.data.ServiceType;
import com.github.sanctum.labyrinth.data.container.ImmutableLabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import com.github.sanctum.labyrinth.data.service.Check;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.library.Deployable;
import java.util.function.Supplier;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface MenuRegistration extends Service {

	<M extends Menu> Deployable<Void> register(M menu) throws MenuNotCacheableException, MenuDuplicationException;

	Deployable<Void> unregister(Menu menu) throws MenuNotCacheableException;

	@NotNull Deployable<LabyrinthCollection<Menu>> get(@NotNull Plugin host);

	@NotNull Deployable<LabyrinthCollection<Menu>> getAll();

	@NotNull Deployable<Menu> get(@NotNull String key);

	static MenuRegistration getInstance() {
		ServiceManager services = LabyrinthProvider.getInstance().getServiceManager();
		MenuRegistration cache = services.get(MenuRegistration.class);
		if (cache != null) return cache;
		ServiceType<MenuRegistration> type = new ServiceType<>(() -> new MenuRegistration() {

			private final LabyrinthMap<Plugin, LabyrinthCollection<Menu>> cache = new LabyrinthEntryMap<>();

			@Override
			public <M extends Menu> Deployable<Void> register(M menu) throws MenuDuplicationException, MenuNotCacheableException {
				return Deployable.of(null, unused -> {
					LabyrinthCollection<Menu> menus = cache.get(menu.getHost());
					if (menus != null) {
						if (menus.contains(menu))
							throw new MenuDuplicationException("Menu's can only be registered one time!");
						if (!menu.getProperties().contains(Menu.Property.CACHEABLE))
							throw new MenuNotCacheableException("Menu doesn't contain required property '" + Menu.Property.CACHEABLE + "'.");
						menus.add(menu);
					} else {
						LabyrinthCollection<Menu> n = new LabyrinthList<>();
						n.add(menu);
						cache.put(menu.getHost(), n);
					}
				});
			}

			@Override
			public Deployable<Void> unregister(Menu menu) throws MenuNotCacheableException {
				return Deployable.of(null, unused -> {
					LabyrinthCollection<Menu> menus = cache.get(menu.getHost());
					if (menus != null) {
						if (!menus.contains(menu)) throw new MenuNotCacheableException("Menu not cached.");
						menus.remove(menu);
					} else throw new MenuNotCacheableException("No menu's cached for plugin " + menu.getHost());
				});
			}

			@Override
			public @NotNull Deployable<LabyrinthCollection<Menu>> get(@NotNull Plugin host) {
				return Deployable.of(new LabyrinthList<>(), list -> list.addAll(Check.forNull(cache.get(host), "No registrations logged for provided plugin.")));
			}

			@Override
			public @NotNull Deployable<LabyrinthCollection<Menu>> getAll() {
				return Deployable.of(cache.values().stream().reduce((menus, menus2) -> {
					ImmutableLabyrinthCollection.Builder<Menu> b = ImmutableLabyrinthCollection.builder();
					for (Menu o : menus) {
						b.add(o);
					}
					for (Menu t : menus2) {
						b.add(t);
					}
					return b.build();
				}).orElse(new LabyrinthList<>()), unused -> {
				});
			}

			@Override
			public @NotNull Deployable<Menu> get(@NotNull String key) {
				Supplier<Menu> getter = () -> {
					for (LabyrinthCollection<Menu> menus : cache.values()) {
						for (Menu m : menus) {
							if (m.getKey().map(key::equals).orElse(false)) {
								return m;
							}
						}
					}
					return null;
				};
				return Deployable.of(getter.get(), unused -> {
				});
			}
		});
		services.load(type);
		return type.getLoader().get();
	}

}
