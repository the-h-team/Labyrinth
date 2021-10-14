package com.github.sanctum.labyrinth.library;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Encapsulate number data to modify.
 *
 * @author Hempfest
 */
public class MathUtils {

	private final Number n;

	protected MathUtils(Number n) {
		this.n = n;
	}

	/**
	 * Get a fresh math utility instance using the provided number context.
	 *
	 * @param n the number to use
	 * @return a new math utility instance
	 */
	public static MathUtils use(Number n) {
		return new MathUtils(n);
	}

	/**
	 * Format/trim a given amount to a specific length format.
	 *
	 * @param precision the math precision to stop the decimal placing at
	 * @return the newly formatted double
	 */
	public double format(int precision) {
		BigDecimal b1 = BigDecimal.valueOf(n.doubleValue()).setScale(precision, RoundingMode.HALF_EVEN);
		return b1.doubleValue();
	}

	/**
	 * Format the number with a specified language.
	 *
	 * @param locale the language to format to
	 * @return the formatted amount as a string (number compliant)
	 */
	public String format(Locale locale) {
		return NumberFormat.getNumberInstance(locale).format(n.doubleValue());
	}

	/**
	 * Format the number with a specified language.
	 *
	 * @param locale    the language to format to
	 * @param precision the math context precision to apply
	 * @return the formatted amount as a string (number compliant)
	 */
	public String format(Locale locale, int precision) {
		return NumberFormat.getNumberInstance(locale).format(format(precision));
	}

	/**
	 * Format the number with a specified language.
	 *
	 * @param locale the language to format to
	 * @return the formatted amount as a string (number compliant)
	 */
	public String formatCurrency(Locale locale) {
		return NumberFormat.getCurrencyInstance(locale).format(n.doubleValue());
	}

	/**
	 * Format the number with a specified language.
	 *
	 * @param locale    the language to format to
	 * @param precision the math context precision to apply
	 * @return the formatted amount as a string (number compliant)
	 */
	public String formatCurrency(Locale locale, int precision) {
		return NumberFormat.getCurrencyInstance(locale).format(format(precision));
	}



}
