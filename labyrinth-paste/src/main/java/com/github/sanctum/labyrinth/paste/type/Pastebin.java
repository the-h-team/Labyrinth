package com.github.sanctum.labyrinth.paste.type;

import org.jetbrains.annotations.Nullable;

/**
 * An object for handling <strong>pastebin</strong> services over the web!
 *
 * @see <a href="http://pastebin.com">http://pastebin.com</a>
 */
public interface Pastebin extends Manipulable {

	@Nullable PastebinUser login(String username, String password);

}
