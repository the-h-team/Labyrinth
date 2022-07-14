package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.interfacing.JsonIntermediate;
import com.github.sanctum.panther.file.JsonAdapter;
import com.github.sanctum.panther.file.Node;
import com.github.sanctum.templates.SimpleTemplate;
import com.github.sanctum.templates.Template;
import com.google.gson.JsonElement;
import java.util.Map;

@Node.Pointer("com.github.sanctum.templates.Template")
public final class TemplateSerializable implements JsonAdapter<Template> {

	@Override
	public JsonElement write(Template template) {
		return JsonIntermediate.toJsonObject(template.serialize());
	}

	@Override
	public Template read(Map<String, Object> object) {
		return SimpleTemplate.deserialize(object);
	}

	@Override
	public Class<Template> getSerializationSignature() {
		return Template.class;
	}
}
