package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.formatting.string.GradientColor;
import com.github.sanctum.labyrinth.formatting.string.RandomID;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Encapsulate string data to modify.
 *
 * @author Hempfest
 */
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
	 * Check if the provided context exists (using case insensitive options) a target regex.
	 *
	 * @param regex The target regex to check for.
	 * @return true if the provided regex exists a case insensitive match from the target regex.
	 */
	public boolean containsIgnoreCase(CharSequence regex) {
		return Pattern.compile(Pattern.quote(regex.toString()), Pattern.CASE_INSENSITIVE).matcher(this.context).find();
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
	 * Form a custom gradient to wrap the provided context with.
	 * Then decide whether or not to translate it or get the raw joined string back.
	 *
	 * @param from The starting Hex code.
	 * @param to   The ending Hex code.
	 * @return A custom color gradient using the provided context.
	 */
	public CustomColor gradient(CharSequence from, CharSequence to) {
		return new GradientColor(this.context, from, to);
	}

	/**
	 * Form a custom gradient to wrap the provided context with.
	 * Then decide whether or not to translate it or get the raw joined string back.
	 *
	 * @param from The starting Hex code.
	 * @param to   The ending Hex code.
	 * @return A custom color gradient using the provided context.
	 */
	public GradientColor modifyableGradient(CharSequence from, CharSequence to) {
		return new GradientColor(this.context, from, to);
	}

	/**
	 * Automatically color both HEX & Normal MC color codes for versions 1.16+ or only MC color codes for versions below.
	 *
	 * @return The translated "colored" string.
	 */
	public String translate() {
		return new ColoredString(this.context).toString();
	}

	/**
	 * Simply set placeholder's to the given context from the provided source. Nothing more.
	 *
	 * @param source The player to target.
	 * @return The translated non-colored placeholder set string.
	 */
	public String papi(OfflinePlayer source) {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			return PlaceholderAPI.setPlaceholders(source, this.context);
		}
		return "{PAPI-MISSING}:" + this.context;
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
	 * @deprecated replaced by new {@link ListUtils} class.
	 */
	@Deprecated
	public List<String> join(List<String> list) {
		return ListUtils.use(list).append(string -> string + this.context);
	}


}
