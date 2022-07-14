package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.interfacing.JsonIntermediate;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.panther.file.JsonAdapter;
import com.github.sanctum.panther.util.OrdinalProcedure;
import com.github.sanctum.panther.util.TypeAdapter;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.io.BukkitObjectOutputStream;

final class LabyrinthObjectOutputStream extends BukkitObjectOutputStream {
	LabyrinthObjectOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	@Override
	protected Object replaceObject(Object obj) throws IOException {
		if (!(obj instanceof Serializable) && !(obj instanceof ConfigurationSerializable)) {
			JsonAdapter<Object> adapter = (JsonAdapter<Object>) Configurable.getAdapter(obj.getClass());
			if (adapter != null) {
				String pointer = OrdinalProcedure.select(adapter, 24).cast(TypeAdapter.STRING);
				JsonElement element = adapter.write(obj);
				if (element.isJsonObject()) {
					obj = new LabyrinthJsonObjectWrapper<>(JsonIntermediate.convertToMap(element.getAsJsonObject()), pointer);
				} else
					throw new IOException("Unable to serialize json information @ " + obj.getClass().getName() + " using " + adapter.getClass().getSimpleName());
			}
		}
		return super.replaceObject(obj);
	}

}
