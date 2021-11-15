package com.github.sanctum.labyrinth.paste.type;

import com.github.sanctum.labyrinth.paste.operative.PasteReader;
import com.github.sanctum.labyrinth.paste.operative.PasteWriter;
import org.jetbrains.annotations.NotNull;

public interface Manipulable extends PasteWriter, PasteReader {

	@NotNull String getApiKey();

	@NotNull
	PasteOptions getOptions();


}
