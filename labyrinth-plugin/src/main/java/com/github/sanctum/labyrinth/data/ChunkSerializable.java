package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.formatting.TextChunk;
import com.github.sanctum.labyrinth.formatting.ToolTip;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;

@NodePointer("com.github.sanctum.Message.Chunk")
public final class ChunkSerializable implements JsonAdapter<Message.Chunk> {
	@Override
	public JsonElement write(Message.Chunk toolTips) {
		JsonObject o = new JsonObject();
		o.addProperty("text", toolTips.toComponent().toLegacyText());
		JsonArray array = new JsonArray();
		for (ToolTip<?> t : toolTips) {
			if (t instanceof ToolTip.Text) {
				ToolTip<String> tip = (ToolTip<String>) t;
				array.add("text:" + tip.get());
			}
			if (t instanceof ToolTip.Command) {
				ToolTip<String> tip = (ToolTip<String>) t;
				array.add("command:" + tip.get());
			}
			if (t instanceof ToolTip.Suggestion) {
				ToolTip<String> tip = (ToolTip<String>) t;
				array.add("suggestion:" + tip.get());
			}
			if (t instanceof ToolTip.Copy) {
				ToolTip<String> tip = (ToolTip<String>) t;
				array.add("copy:" + tip.get());
			}
			if (t instanceof ToolTip.Url) {
				ToolTip<String> tip = (ToolTip<String>) t;
				array.add("open:" + tip.get());
			}
		}
		if (array.size() > 0) {
			o.add("extras", array);
		}
		return o;
	}

	@Override
	public Message.Chunk read(Map<String, Object> object) {
		String text = (String) object.get("text");
		List<String> hover = (List<String>) object.get("extras");
		TextChunk chunk = new TextChunk(text);
		if (hover != null) {
			String tt = "text:";
			String ct = "command:";
			String st = "suggestion:";
			String cct = "copy:";
			String ut = "open:";
			for (String h : hover) {
				if (h.startsWith(tt)) {
					chunk.bind(new ToolTip.Text(h.replace(tt, "")));
				}
				if (h.startsWith(ct)) {
					chunk.bind(new ToolTip.Command(h.replace(ct, "")));
				}
				if (h.startsWith(st)) {
					chunk.bind(new ToolTip.Suggestion(h.replace(st, "")));
				}
				if (h.startsWith(cct)) {
					chunk.bind(new ToolTip.Copy(h.replace(cct, "")));
				}
				if (h.startsWith(ut)) {
					chunk.bind(new ToolTip.Url(h.replace(ut, "")));
				}
			}
		}
		return chunk;
	}

	@Override
	public Class<Message.Chunk> getClassType() {
		return Message.Chunk.class;
	}
}
