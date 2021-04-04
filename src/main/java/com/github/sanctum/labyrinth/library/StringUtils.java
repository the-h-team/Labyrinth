package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class StringUtils {

	/**
	 * Checks if a string ignoring all case sensitivity contains a
	 * specified target string.
	 *
	 * @param context The string to look through
	 * @param target The non-case-sensitive string to check for
	 * @return result = true if the string has an exact character match for the given target
	 */
	public static boolean containsIgnoreCase(String context, String target) {
		return Pattern.compile(Pattern.quote(context), Pattern.CASE_INSENSITIVE).matcher(target).find();
	}

	/**
	 * Translate a string automatically with minecraft color codes
	 * or HEX following '&' delimiters if your version allows for it.
	 *
	 * @param text The string to be color translated
	 * @return A fully color translated string.
	 */
	public static String translate(String text) {
		return Bukkit.getVersion().contains("1.16") ? new ColoredString(text, ColoredString.ColorType.HEX).toString() : new ColoredString(text, ColoredString.ColorType.MC).toString();
	}

	/**
	 * Translate a string automatically with minecraft color codes
	 * or HEX if your version allows for it, also check for placeholders.
	 *
	 * @param source The source player to grab data for.
	 * @param text The text to be colored.
	 * @return A fully colored placeholder translated string.
	 */
	public static String translate(OfflinePlayer source, String text) {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			return PlaceholderAPI.setPlaceholders(source, translate(text));
		}
		return translate(text);
	}

	/**
	 * Similar to the {@link String#join(CharSequence, CharSequence...)} method append a specified element
	 * to the end of each list entry except for the last one.
	 *
	 * @param delimiter The character to append.
	 * @param list The list to append characters to.
	 * @return A new list of strings containing the previous entries with the newly
	 * appended delimiters.
	 */
	public static List<String> join(CharSequence delimiter, List<String> list) {
		List<String> array = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (i != list.size() - 1) {
				array.add(list.get(i) + "\n");
			} else {
				array.add(list.get(i));
			}
		}
		return array;
	}

}
