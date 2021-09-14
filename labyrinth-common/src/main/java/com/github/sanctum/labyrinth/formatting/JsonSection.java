package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.data.service.Check;
import net.md_5.bungee.chat.ComponentSerializer;

public class JsonSection extends ComponentSection {
	public JsonSection(@Json String message) {
		super(ComponentSerializer.parse(Check.forJson(message, "Bulletin: An invalid json message was provided for conversion.")));
	}
}
