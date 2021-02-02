package com.youtube.hempfest.hempcore.library;

import com.youtube.hempfest.hempcore.HempCore;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class HeadFinder {

	public final Map<String, ItemStack> heads = new HashMap<>();

	public static ItemStack find(String name) {
		return HempCore.getInstance().findHead.getHead(name);
	}

	public ItemStack getHead(String name) {
		return heads.get(name);
	}
}
