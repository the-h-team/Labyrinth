package com.github.sanctum.labyrinth.paste.type;

import com.github.sanctum.labyrinth.paste.option.Context;
import com.github.sanctum.labyrinth.paste.option.Expiration;
import com.github.sanctum.labyrinth.paste.option.Visibility;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PasteOptions {

	@NotNull Context getLanguage();

	@Nullable Context getFolder();

	@NotNull Expiration getExpiration();

	@NotNull Visibility getVisibility();

	void setFolder(@NotNull Context context);

	void setLanguage(@NotNull Context context);

	default void setLanguage(@NotNull @MagicConstant(valuesFromClass = Context.class) String language) {
		setLanguage(() -> language);
	}

	void setExpiration(@NotNull Expiration expiration);

	void setVisibility(@NotNull Visibility visibility);

	static @NotNull PasteOptions empty() {
		return new PasteOptions() {
			@Override
			public @NotNull Context getLanguage() {
				return () -> Context.TEXT;
			}

			@Override
			public @Nullable Context getFolder() {
				return null;
			}

			@Override
			public @NotNull Expiration getExpiration() {
				return Expiration.NEVER;
			}

			@Override
			public @NotNull Visibility getVisibility() {
				return Visibility.PUBLIC;
			}

			@Override
			public void setFolder(@NotNull Context context) {

			}

			@Override
			public void setLanguage(@NotNull Context context) {

			}

			@Override
			public void setExpiration(@NotNull Expiration expiration) {

			}

			@Override
			public void setVisibility(@NotNull Visibility visibility) {

			}
		};
	}

}
