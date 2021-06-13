package com.github.sanctum.labyrinth.data.container;

import java.util.Collection;
import java.util.Set;

public abstract class PersistentData {

	public abstract boolean exists(String key);

	public abstract <R> R get(Class<R> type, String key);

	public abstract Set<String> keySet();

	public abstract <R> Collection<? extends R> values(Class<R> type);

}
