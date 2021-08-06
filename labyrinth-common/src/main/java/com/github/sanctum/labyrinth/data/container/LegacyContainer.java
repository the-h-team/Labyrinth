package com.github.sanctum.labyrinth.data.container;

import java.util.Set;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LegacyContainer {


	<T, Z> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value);

	<T, Z> boolean has(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type);

	@Nullable <T, Z> Z get(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type);

	@NotNull <T, Z> Z getOrDefault(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z defaultValue);

	@NotNull
	Set<NamespacedKey> getKeys();

	void remove(@NotNull NamespacedKey key);

	boolean isEmpty();

	@NotNull
	PersistentDataAdapterContext getAdapterContext();

	class Impl implements LegacyContainer {

		private final PersistentDataContainer container;

		public Impl(PersistentDataContainer container) {
			this.container = container;
		}

		@Override
		public <T, Z> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
			container.set(key, type, value);
		}

		@Override
		public <T, Z> boolean has(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
			return container.has(key, type);
		}

		@Override
		public <T, Z> @Nullable Z get(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
			return container.get(key, type);
		}

		@Override
		public <T, Z> @NotNull Z getOrDefault(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z defaultValue) {
			return container.getOrDefault(key, type, defaultValue);
		}

		@Override
		public @NotNull Set<NamespacedKey> getKeys() {
			return container.getKeys();
		}

		@Override
		public void remove(@NotNull NamespacedKey key) {
			container.remove(key);
		}

		@Override
		public boolean isEmpty() {
			return container.isEmpty();
		}

		@Override
		public @NotNull PersistentDataAdapterContext getAdapterContext() {
			return container.getAdapterContext();
		}
	}

}
