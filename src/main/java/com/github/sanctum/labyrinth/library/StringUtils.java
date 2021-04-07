package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import com.github.sanctum.labyrinth.formatting.string.RandomID;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class StringUtils {

	private final String context;

	protected StringUtils(String context) {
		this.context = context;
	}

	/**
	 * Here is where you specify either a regex for total completion results or a delimiter to separate
	 * strings by. Its the premise for all string based utility.
	 *
	 * @param context The star context to provide for usage.
	 * @return A string utility.
	 */
	public static StringUtils use(String context) {
		return new StringUtils(context);
	}

	/**
	 * Check if the provided regex contains (using case insensitive context) a target regex.
	 *
	 * @param regex The target regex to check for.
	 * @return true if the provided regex contains a case insensitive match from the target regex.
	 */
	public boolean containsIgnoreCase(CharSequence regex) {
		return Pattern.compile(Pattern.quote(this.context), Pattern.CASE_INSENSITIVE).matcher(regex).find();
	}

	/**
	 * Generate a string id from the provided context pattern.
	 * The more complex the pattern the more variation and slim chance it has of ever possibly repeating.
	 * <p>
	 * Ex. "SANTO20432"
	 *
	 * @param size The max size in length the id can be.
	 * @return The generated string id.
	 */
	public String generateID(int size) {
		return new RandomID(size, this.context).generate();
	}

	/**
	 * Automatically color both HEX & Normal MC color codes for versions 1.16+ or only MC color codes for versions below.
	 *
	 * @return The translated "colored" string.
	 */
	public String translate() {
		return Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") ? new ColoredString(this.context, ColoredString.ColorType.HEX).toString() : new ColoredString(this.context, ColoredString.ColorType.MC).toString();
	}

	/**
	 * Apply the same logic as {@link StringUtils#translate()} but also utilize set placeholders for a
	 * target player.
	 *
	 * @param source The player to target.
	 * @return The translated "colored" & placeholder set string. Providing your server has PlaceholderAPI installed of course.
	 */
	public String translate(OfflinePlayer source) {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			return PlaceholderAPI.setPlaceholders(source, translate());
		}
		return translate();
	}

	/**
	 * Replace a desired string regex with a replacement regex using the provided context.
	 *
	 * @param regex       The regex to look for.
	 * @param replacement The regex to replace the target with.
	 * @return The formatted origin string.
	 */
	public String replaceIgnoreCase(String regex, String replacement) {
		return Pattern.compile(regex, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(this.context)
				.replaceAll(Matcher.quoteReplacement(replacement));
	}

	/**
	 * Automatically append the provided context to the end of each list entry
	 * excluding the final entry.
	 *
	 * @param list The list to append the provided context to.
	 * @return The formatted origin list.
	 */
	public List<String> join(List<String> list) {
		List<String> array = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (i != list.size() - 1) {
				array.add(list.get(i) + this.context);
			} else {
				array.add(list.get(i));
			}
		}
		return array;
	}


	// ======== [ Deprecated static methods below] ======== //


	/**
	 * Checks if a string ignoring all case sensitivity contains a
	 * specified target string.
	 *
	 * @param context The string to look through
	 * @param target  The non-case-sensitive string to check for
	 * @return result = true if the string has an exact character match for the given target
	 * @deprecated Refer to formal method {@link StringUtils#use(String)#context}
	 */
	@Deprecated
	public static boolean containsIgnoreCase(String context, String target) {
		return Pattern.compile(Pattern.quote(context), Pattern.CASE_INSENSITIVE).matcher(target).find();
	}

	/**
	 * Translate a string automatically with minecraft color codes
	 * or HEX following '&' delimiters if your version allows for it.
	 *
	 * @param text The string to be color translated
	 * @return A fully color translated string.
	 * @deprecated Refer to formal method {@link StringUtils#use(String)#context}
	 */
	@Deprecated
	public static String translate(String text) {
		return Bukkit.getVersion().contains("1.16") ? new ColoredString(text, ColoredString.ColorType.HEX).toString() : new ColoredString(text, ColoredString.ColorType.MC).toString();
	}

	/**
	 * Translate a string automatically with minecraft color codes
	 * or HEX if your version allows for it, also check for placeholders.
	 *
	 * @param source The source player to grab data for.
	 * @param text   The text to be colored.
	 * @return A fully colored placeholder translated string.
	 * @deprecated Refer to formal method {@link StringUtils#use(String)#context}
	 */
	@Deprecated
	public static String translate(OfflinePlayer source, String text) {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			return PlaceholderAPI.setPlaceholders(source, translate(text));
		}
		return translate(text);
	}

	/**
	 * Find a specific case insensitive regex to match from a source sequence and replace it with
	 * the desired string.
	 *
	 * @param source      The source sequence to format.
	 * @param target      The target regex to replace.
	 * @param replacement The regex to replace the target with.
	 * @return The formatted string replaced with desired information.
	 * @deprecated Refer to formal method {@link StringUtils#use(String)#context}
	 */
	@Deprecated
	public static String replace(CharSequence source, String target, String replacement) {
		return Pattern.compile(target, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(source)
				.replaceAll(Matcher.quoteReplacement(replacement));
	}

	/**
	 * Similar to the {@link String#join(CharSequence, CharSequence...)} method append a specified element
	 * to the end of each list entry except for the last one.
	 *
	 * @param delimiter The character to append.
	 * @param list      The list to append characters to.
	 * @return A new list of strings containing the previous entries with the newly
	 * appended delimiters.
	 * @deprecated Refer to formal method {@link StringUtils#use(String)#context}
	 */
	@Deprecated
	public static List<String> join(CharSequence delimiter, List<String> list) {
		List<String> array = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (i != list.size() - 1) {
				array.add(list.get(i) + delimiter);
			} else {
				array.add(list.get(i));
			}
		}
		return array;
	}

}
