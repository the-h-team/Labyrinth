package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.data.NodePointer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;

@NodePointer("com.github.sanctum.Message")
public class MessageSerializable implements JsonAdapter<Message> {

	public static Message fromJson(@Json String json) {
		return new FancyMessage().append(json);
	}

	@Override
	public JsonElement write(Message sections) {
		JsonObject o = new JsonObject();
		o.addProperty("data", sections.toJson());
		return o;
	}

	@Override
	public Message read(Map<String, Object> object) {
		String data = (String) object.get("data");
		return new FancyMessage().append(data);
	}

	@Override
	public Class<Message> getClassType() {
		return Message.class;
	}
}
