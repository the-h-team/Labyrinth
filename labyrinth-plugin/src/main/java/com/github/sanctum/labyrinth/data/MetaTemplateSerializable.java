package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.interfacing.JsonIntermediate;
import com.github.sanctum.templates.MetaTemplate;
import com.github.sanctum.templates.SimpleMetaTemplate;
import com.google.gson.JsonElement;
import java.util.Map;

@NodePointer("com.github.sanctum.templates.MetaTemplate")
public final class MetaTemplateSerializable implements JsonAdapter<MetaTemplate> {

	@Override
	public JsonElement write(MetaTemplate template) {
		return JsonIntermediate.toJsonObject(template.serialize());
	}

	@Override
	public MetaTemplate read(Map<String, Object> object) {
		return SimpleMetaTemplate.deserialize(object);
	}

	@Override
	public Class<MetaTemplate> getClassType() {
		return MetaTemplate.class;
	}

}
