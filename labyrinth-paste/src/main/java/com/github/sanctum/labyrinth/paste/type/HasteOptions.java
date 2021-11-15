package com.github.sanctum.labyrinth.paste.type;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.paste.option.Context;
import com.github.sanctum.labyrinth.paste.option.Expiration;
import com.github.sanctum.labyrinth.paste.option.Visibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Note("This class simply acts as an inherent delegate for Hastebin")
public interface HasteOptions extends PasteOptions {

	boolean isRaw();

	void setRaw(boolean raw);

	static @NotNull HasteOptions empty() {
		return new HasteOptions() {

			private boolean raw;

			@Override
			public boolean isRaw() {
				return raw;
			}

			@Override
			public void setRaw(boolean raw) {
				this.raw = raw;
			}

			@Override
			public @NotNull
			Context getLanguage() {
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
			public @NotNull
			Visibility getVisibility() {
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
