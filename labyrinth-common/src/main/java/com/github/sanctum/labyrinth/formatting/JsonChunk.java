package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.panther.annotation.Json;
import com.github.sanctum.panther.util.Check;
import net.md_5.bungee.chat.ComponentSerializer;

public class JsonChunk extends ComponentChunk {
	public JsonChunk(@Json String message) {
		super(ComponentSerializer.parse(Check.forJson(message, "Message: An invalid json message was provided for conversion.")));
	}
}
