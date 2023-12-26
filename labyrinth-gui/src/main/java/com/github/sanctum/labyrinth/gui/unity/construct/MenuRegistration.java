package com.github.sanctum.labyrinth.gui.unity.construct;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.panther.container.ImmutablePantherCollection;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.recursive.ServiceManager;
import com.github.sanctum.panther.util.Check;
import com.github.sanctum.panther.util.Deployable;
import java.util.function.Supplier;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface MenuRegistration extends Service {

	<M extends Menu> Deployable<Void> register(M menu) throws MenuNotCacheableException, MenuDuplicationException;

	Deployable<Void> unregister(Menu menu) throws MenuNotCacheableException;

	@NotNull Deployable<PantherCollection<Menu>> get(@NotNull Plugin host);

	@NotNull Deployable<PantherCollection<Menu>> getAll();

	@NotNull Deployable<Menu> get(@NotNull String key);

	static MenuRegistration getInstance() {
		ServiceManager services = LabyrinthProvider.getInstance().getServiceManager();
		MenuRegistration cache = services.newLoader(MenuRegistration.class).load();
		//noinspection ConstantValue
		if (cache != null) return cache;
		return services.newLoader(MenuRegistration.class).supply(new MenuRegistration() {

			private final PantherMap<Plugin, PantherCollection<Menu>> cache = new PantherEntryMap<>();

			@Override
			public <M extends Menu> Deployable<Void> register(M menu) throws MenuDuplicationException, MenuNotCacheableException {
				return Deployable.of(() -> {
					PantherCollection<Menu> menus = cache.get(menu.getHost());
					if (menus != null) {
						if (menus.contains(menu))
							throw new MenuDuplicationException("Menu's can only be registered one time!");
						if (!menu.getProperties().contains(Menu.Property.CACHEABLE))
							throw new MenuNotCacheableException("Menu doesn't contain required property '" + Menu.Property.CACHEABLE + "'.");
						menus.add(menu);
					} else {
						PantherCollection<Menu> n = new PantherList<>();
						n.add(menu);
						cache.put(menu.getHost(), n);
					}
				}, 0);
			}

			@Override
			public Deployable<Void> unregister(Menu menu) throws MenuNotCacheableException {
				return Deployable.of(() -> {
					PantherCollection<Menu> menus = cache.get(menu.getHost());
					if (menus != null) {
						if (!menus.contains(menu)) throw new MenuNotCacheableException("Menu not cached.");
						menus.remove(menu);
					} else throw new MenuNotCacheableException("No menu's cached for plugin " + menu.getHost());
				}, 0);
			}

			@Override
			public @NotNull Deployable<PantherCollection<Menu>> get(@NotNull Plugin host) {
				return Deployable.of(new PantherList<>(), list -> list.addAll(Check.forNull(cache.get(host), "No registrations logged for provided plugin.")), 0);
			}

			@Override
			public @NotNull Deployable<PantherCollection<Menu>> getAll() {
				return Deployable.of(cache.values().stream().reduce((menus, menus2) -> {
					ImmutablePantherCollection.Builder<Menu> b = ImmutablePantherCollection.builder();
					for (Menu o : menus) {
						b.add(o);
					}
					for (Menu t : menus2) {
						b.add(t);
					}
					return b.build();
				}).orElse(new PantherList<>()), unused -> {
				}, 0);
			}

			@Override
			public @NotNull Deployable<Menu> get(@NotNull String key) {
				Supplier<Menu> getter = () -> {
					for (PantherCollection<Menu> menus : cache.values()) {
						for (Menu m : menus) {
							if (m.getKey().map(key::equals).orElse(false)) {
								return m;
							}
						}
					}
					return null;
				};
				return Deployable.of(getter, 0);
			}
		}).load();
	}

}
