package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.formatting.string.GradientColor;
import com.github.sanctum.labyrinth.formatting.string.RandomID;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * strings by. It is the premise for all string-based utility.
	 *
	 * @param context the star context to provide for usage
	 * @return a string utility
	 */
	public static StringUtils use(String context) {
		return new StringUtils(context);
	}

	/**
	 * Check if the provided context exists (using case insensitive options) a target regex.
	 *
	 * @param regex the target regex to check for
	 * @return true if the provided regex exists a case insensitive match from the target regex
	 */
	public boolean containsIgnoreCase(CharSequence regex) {
		return Pattern.compile(Pattern.quote(regex.toString()), Pattern.CASE_INSENSITIVE).matcher(this.context).find();
	}

	/**
	 * Generate a string id from the provided context pattern.
	 * <p>
	 * The more complex the pattern, the more variation--and the slimmer
	 * a chance it has of ever possibly repeating.
	 * <p>
	 * Ex. "SANTO20432"
	 *
	 * @param size the max size in length the id can be
	 * @return the generated string id
	 */
	public String generateID(int size) {
		return new RandomID(size, this.context).generate();
	}

	/**
	 * Form a custom gradient to wrap the provided context with.
	 *
	 * @param from the starting hex code
	 * @param to the ending hex code
	 * @return a custom color gradient using the provided context
	 */
	public CustomColor gradient(CharSequence from, CharSequence to) {
		return new GradientColor(this.context, from, to);
	}

	/**
	 * Form a custom gradient to wrap the provided context with.
	 *
	 * @param from the starting hex code
	 * @param to the ending hex code
	 * @return a custom color gradient using the provided context
	 */
	public GradientColor modifiableGradient(CharSequence from, CharSequence to) {
		return new GradientColor(this.context, from, to);
	}

	/**
	 * Automatically color both HEX &amp; Normal MC color codes for versions 1.16+ or only MC color codes for versions below.
	 *
	 * @return the translated "colored" string
	 */
	public String translate() {
		return new ColoredString(this.context).toString();
	}

	/**
	 * Simply set placeholders to the given context from the provided source. Nothing more.
	 *
	 * @param source the player to target
	 * @return the translated non-colored placeholder set string
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
	 * @param source the player to target
	 * @return the translated ("colored") AND placeholder-set string--provided
	 * your server has PlaceholderAPI installed, of course;)
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
	 * @param regex the regex to look for
	 * @param replacement the regex to replace the target with
	 * @return the formatted origin string
	 */
	public String replaceIgnoreCase(String regex, String replacement) {
		return Pattern.compile(regex, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(this.context)
				.replaceAll(Matcher.quoteReplacement(replacement));
	}

	/**
	 * Automatically append the provided context to the end of each list entry
	 * excluding the final entry.
	 *
	 * @param list the list to append the provided context to
	 * @return the formatted origin list
	 * @deprecated replaced by new {@link ListUtils} class
	 */
	@Deprecated
	public List<String> join(List<String> list) {
		return ListUtils.use(list).append(string -> string + this.context);
	}


}
