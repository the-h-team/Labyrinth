package com.github.sanctum.labyrinth.paste.type;

import org.jetbrains.annotations.NotNull;

public interface PastebinUser extends Manipulable {

	@NotNull String getId();

	boolean remove(@NotNull String id);


}
