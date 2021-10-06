package com.github.sanctum.labyrinth.library;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemCompost {

	private final List<ItemMatcher> matchers = new ArrayList<>();

	public ItemCompost registerMatcher(@NotNull ItemMatcher matcher) {
		matchers.add(matcher);
		return this;
	}

	public ItemCompost unregisterMatcher(@NotNull ItemMatcher matcher) {
		matchers.remove(matcher);
		return this;
	}

	public boolean has(@NotNull CompostElement synchronizer) {
		int amount = 0;
		if (synchronizer instanceof ItemSync) {
			for (int i = 0; i < synchronizer.getParent().getSize(); i++) {
				ItemStack slot = synchronizer.getParent().getItem(i);
				for (ItemMatcher matcher : matchers.stream().filter(m -> ((ItemSync<ItemMatcher>) synchronizer).getMatcher().isAssignableFrom(m.getClass())).collect(Collectors.toList())) {
					if (slot == null || !matcher.compares(slot)) continue;
					amount += slot.getAmount();
				}
			}
		} else {
			for (int i = 0; i < synchronizer.getParent().getSize(); i++) {
				ItemStack slot = synchronizer.getParent().getItem(i);
				for (ItemMatcher matcher : matchers) {
					if (slot == null || !matcher.compares(slot)) continue;
					amount += slot.getAmount();
				}
			}
		}
		return amount >= synchronizer.getAmount();
	}

	public boolean remove(CompostElement synchronizer) {
		int remainingAmount = synchronizer.getAmount();
		ItemStack[] inv = synchronizer.getParent().getContents();
		if (synchronizer instanceof ItemSync) {
			if (remainingAmount == -1) {
				for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
					if (inv[j] == null) continue;
					for (ItemMatcher matcher : matchers.stream().filter(m -> ((ItemSync<ItemMatcher>) synchronizer).getMatcher().isAssignableFrom(m.getClass())).collect(Collectors.toList())) {
						if (matcher.compares(inv[j])) {
							inv[j].setAmount(0);
							break;
						}
					}
				}
				return true;
			} else {
				for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
					if (inv[j] == null) continue;
					for (ItemMatcher matcher : matchers.stream().filter(m -> ((ItemSync<ItemMatcher>) synchronizer).getMatcher().isAssignableFrom(m.getClass())).collect(Collectors.toList())) {
						if (matcher.compares(inv[j])) {
							int newAmount = inv[j].getAmount() - remainingAmount;
							if (newAmount > 0) {
								inv[j].setAmount(newAmount);
								break;
							} else {
								synchronizer.getParent().clear(j);
								remainingAmount = -newAmount;
								if (remainingAmount == 0) break;
							}
							break;
						}
					}
				}
				return remainingAmount == 0;
			}
		} else {
			if (remainingAmount == -1) {
				for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
					if (inv[j] == null) continue;
					for (ItemMatcher matcher : matchers) {
						if (matcher.compares(inv[j])) {
							inv[j].setAmount(0);
							break;
						}
					}
				}
				return true;
			} else {
				for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
					if (inv[j] == null) continue;
					for (ItemMatcher matcher : matchers) {
						if (matcher.compares(inv[j])) {
							int newAmount = inv[j].getAmount() - remainingAmount;
							if (newAmount > 0) {
								inv[j].setAmount(newAmount);
								break;
							} else {
								synchronizer.getParent().clear(j);
								remainingAmount = -newAmount;
								if (remainingAmount == 0) break;
							}
							break;
						}
					}
				}
				return remainingAmount == 0;
			}
		}
	}

}
