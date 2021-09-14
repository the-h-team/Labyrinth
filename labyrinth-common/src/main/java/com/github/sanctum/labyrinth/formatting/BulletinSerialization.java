package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.data.NodePointer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;

@NodePointer("com.github.sanctum.Bulletin")
public class BulletinSerialization implements JsonAdapter<Bulletin> {

	public static Bulletin fromJson(@Json String json) {
		return new BulletinMessage().append(json);
	}

	@Override
	public JsonElement write(Bulletin sections) {
		JsonObject o = new JsonObject();
		o.addProperty("data", sections.toJson());
		return o;
	}

	@Override
	public Bulletin read(Map<String, Object> object) {
		String data = (String) object.get("data");
		return new BulletinMessage().append(data);
	}

	@Override
	public Class<Bulletin> getClassType() {
		return Bulletin.class;
	}
}
