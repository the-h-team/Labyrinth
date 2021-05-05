package com.github.sanctum.labyrinth.library;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Encapsulate number data to modify.
 */
public class MathUtils {

	private final Number n;

	protected MathUtils(Number n) {
		this.n = n;
	}

	/**
	 * Get a fresh math utility instance using the provided number context.
	 *
	 * @param n The number to use.
	 * @return A new math utility instance.
	 */
	public static MathUtils use(Number n) {
		return new MathUtils(n);
	}

	/**
	 * Format/trim a given amount to a specific length format.
	 *
	 * @param precision The math precision to stop the decimal placing at.
	 * @return The newly formatted double.
	 */
	public double format(int precision) {
		BigDecimal b1 = BigDecimal.valueOf(n.doubleValue()).setScale(precision, RoundingMode.HALF_EVEN);
		return b1.doubleValue();
	}

	/**
	 * Format the number with a specified language.
	 *
	 * @param locale The language to format to.
	 * @return The formatted amount as a string. (Number compliant)
	 */
	public String format(Locale locale) {
		return NumberFormat.getNumberInstance(locale).format(format(2));
	}

}
