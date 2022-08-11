package com.github.sanctum.labyrinth.data.reload;

import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.panther.util.Deployable;
import com.github.sanctum.panther.util.TypeAdapter;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A fingerprint is an object containing mapped values that are possible to reload.
 */
public interface FingerPrint {

	@NotNull NamespacedKey getKey();

	@Nullable Object get(String key);

	default @Nullable <T> T get(String key, TypeAdapter<T> type) throws ClassCastException {
		return type.cast(get(key));
	}

	boolean getBoolean(String key);

	@Nullable String getString(String key);

	@NotNull Number getNumber(String key);

	@NotNull List<String> getStringList(String key);

	@NotNull Deployable<Map<String, Object>> clear();

	@NotNull Deployable<FingerPrint> reload(String key);

	@NotNull Deployable<FingerPrint> reload();

}
