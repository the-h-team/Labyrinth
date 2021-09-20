package com.github.sanctum.labyrinth.library;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class ItemCompost {

	private final List<ItemMatcher> matchers = new ArrayList<>();

	public ItemCompost registerMatcher(ItemMatcher matcher) {
		matchers.add(matcher);
		return this;
	}

	public ItemCompost unregisterMatcher(ItemMatcher matcher) {
		matchers.remove(matcher);
		return this;
	}

	public <T extends ItemMatcher> boolean remove(ItemSync<T> synchronizer) {
		int remainingAmount = synchronizer.getAmount();
		ItemStack[] inv = synchronizer.getParent().getContents();
		for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
			if (inv[j] == null) continue;
			for (ItemMatcher matcher : matchers) {
				if (matcher.compares(inv[j])) {
					if (!synchronizer.getMatcher().isAssignableFrom(matcher.getClass())) continue;
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
