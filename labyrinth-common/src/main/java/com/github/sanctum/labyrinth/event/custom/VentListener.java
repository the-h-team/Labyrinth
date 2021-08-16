package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.AnnotationDiscovery;
import java.util.Objects;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class VentListener {

	private final Object o;

	private final Plugin host;

	public VentListener(Plugin host, Object o) {
		this.o = o;
		this.host = host;
	}

	public Plugin getHost() {
		return host;
	}

	public @Nullable String getKey() {
		return AnnotationDiscovery.of(LabeledAs.class, getListener()).map((r, u) -> r.value());
	}

	public Object getListener() {
		return o;
	}

	public void remove() {
		LabyrinthProvider.getInstance().getEventMap().unregister(this);
	}

	@Override
	public boolean equals(Object o1) {
		if (this == o1) return true;
		if (!(o1 instanceof VentListener)) return false;
		VentListener listener = (VentListener) o1;
		return o.equals(listener.o) &&
				getHost().equals(listener.getHost());
	}

	@Override
	public int hashCode() {
		return Objects.hash(o, getHost());
	}
}
