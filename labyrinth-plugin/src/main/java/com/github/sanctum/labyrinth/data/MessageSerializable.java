package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.panther.annotation.Json;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.panther.file.JsonAdapter;
import com.github.sanctum.panther.file.Node;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Node.Pointer("com.github.sanctum.Message")
public final class MessageSerializable implements JsonAdapter<Message> {

	public static Message fromJson(@Json String json) {
		return new FancyMessage().append(json);
	}

	@Override
	public JsonElement write(Message sections) {
		JsonObject o = new JsonObject();
		JsonAdapter<Message.Chunk> adapter = Configurable.getAdapter(Message.Chunk.class);
		List<Message.Chunk> toolTips = new ArrayList<>();
		for (Message.Chunk c : sections) {
			toolTips.add(c);
		}
		for (int i = 0; i < toolTips.size(); i++) {
			Message.Chunk c = toolTips.get(i);
			o.add(i + "", adapter.write(c));
		}
		return o;
	}

	@Override
	public Message read(Map<String, Object> object) {
		JsonAdapter<Message.Chunk> adapter = Configurable.getAdapter(Message.Chunk.class);
		FancyMessage message = new FancyMessage();
		for (Map.Entry<String, Object> entry : object.entrySet()) {
			if (StringUtils.use(entry.getKey()).isInt()) {
				message.append(adapter.read((Map<String, Object>) entry.getValue()));
			}
		}
		return message;
	}

	@Override
	public Class<Message> getSerializationSignature() {
		return Message.class;
	}
}
