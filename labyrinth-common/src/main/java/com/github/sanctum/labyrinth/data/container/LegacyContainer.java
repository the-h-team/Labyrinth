package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.library.NamespacedKey;
import java.util.HashSet;
import java.util.Set;
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
			final org.bukkit.NamespacedKey k = new org.bukkit.NamespacedKey(key.getNamespace(), key.getKey());
			container.set(k, type, value);
		}

		@Override
		public <T, Z> boolean has(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
			final org.bukkit.NamespacedKey k = new org.bukkit.NamespacedKey(key.getNamespace(), key.getKey());
			return container.has(k, type);
		}

		@Override
		public <T, Z> @Nullable Z get(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
			final org.bukkit.NamespacedKey k = new org.bukkit.NamespacedKey(key.getNamespace(), key.getKey());
			return container.get(k, type);
		}

		@Override
		public <T, Z> @NotNull Z getOrDefault(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z defaultValue) {
			final org.bukkit.NamespacedKey k = new org.bukkit.NamespacedKey(key.getNamespace(), key.getKey());
			return container.getOrDefault(k, type, defaultValue);
		}

		@Override
		public @NotNull Set<NamespacedKey> getKeys() {
			Set<NamespacedKey> k = new HashSet<>();
			for (org.bukkit.NamespacedKey key : container.getKeys()) {
				k.add(new NamespacedKey(key.getNamespace(), key.getKey()));
			}
			return k;
		}

		@Override
		public void remove(@NotNull NamespacedKey key) {
			final org.bukkit.NamespacedKey k = new org.bukkit.NamespacedKey(key.getNamespace(), key.getKey());
			container.remove(k);
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
