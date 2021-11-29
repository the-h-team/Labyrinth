package com.github.sanctum.labyrinth.formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public final class TabInfo {

	public static final int HEADER = 0;
	public static final int FOOTER = 1;

	static final Map<Player, TablistInstance> instances = new HashMap<>();
	private final Map<Integer, String> messages = new HashMap<>();

	private TabInfo() {
	}

	public TabInfo put(int line, String text) {
		messages.put(line, text);
		return this;
	}

	public TabInfo put(String text) {
		messages.put(messages.size() + 1, text);
		return this;
	}

	@Override
	public String toString() {
		List<Integer> list = new ArrayList<>(messages.keySet());
		Collections.sort(list);
		StringBuilder builder = new StringBuilder();
		for (Integer i : list) {
			builder.append(messages.get(i)).append("\n&r");
		}
		int stop = builder.length() - 3;
		return builder.substring(0, stop);
	}

	public static TabInfo of() {
		return new TabInfo();
	}
}
