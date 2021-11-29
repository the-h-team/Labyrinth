package com.github.sanctum.labyrinth.placeholders;

import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.data.service.Constant;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface is set to be a flag for numerous types of placeholder prefix & suffix combinations and can also contain a raw placeholder value via {@link Placeholder#parameters()}
 */
public interface Placeholder {

	/**
	 * "<>"
	 */
	Placeholder ANGLE_BRACKETS = new Placeholder() {
		@Override
		public char start() {
			return '<';
		}

		@Override
		public boolean isDefault() {
			return true;
		}

		@Override
		public char end() {
			return '>';
		}
	};
	/**
	 * "{}"
	 */
	Placeholder CURLEY_BRACKETS = new Placeholder() {
		@Override
		public char start() {
			return '{';
		}

		@Override
		public boolean isDefault() {
			return true;
		}

		@Override
		public char end() {
			return '}';
		}
	};
	/**
	 * "%%"
	 */
	Placeholder PERCENT = new Placeholder() {
		@Override
		public char start() {
			return '%';
		}

		@Override
		public boolean isDefault() {
			return true;
		}

		@Override
		public char end() {
			return '%';
		}
	};


	char start();

	default @Note("This might be empty!") CharSequence parameters() {
		return "";
	}

	char end();

	/**
	 * Check if this placeholder is equal to another.
	 *
	 * @param placeholder The placeholder to compare.
	 * @return true if the placeholders are the same.
	 */
	default boolean isSame(Placeholder placeholder) {
		return start() == placeholder.start() && end() == placeholder.end() && parameters().equals(placeholder.parameters());
	}

	/**
	 * Check if this placeholder is equal to a set of characters.
	 *
	 * @param start The starting character.
	 * @param end The ending character.
	 * @return true if this placeholder has the same prefix and suffix regex.
	 */
	default boolean isSame(char start, char end) {
		return start() == start && end() == end;
	}

	/**
	 * Check if this placeholder contains no parameters.
	 *
	 * @return true if this placeholder is only a prefix & suffix.
	 */
	default boolean isEmpty() {
		return parameters().length() == 0 || parameters().equals(" ");
	}

	/**
	 * Check if this placeholder is default.
	 *
	 * @return true if this placeholder is default.
	 */
	default boolean isDefault() {
		return false;
	}

	/**
	 * Get this placeholder and parameters as a string.
	 *
	 * @return this placeholder as a string.
	 */
	default @NotNull String toRaw() {
		return String.valueOf(start()) + parameters() + end();
	}

	/**
	 * Get this placeholder and parameters as a translated string.
	 *
	 * @param variable The optional translation variable.
	 * @return this placeholder translated.
	 */
	default @NotNull String toTranslated(@Nullable PlaceholderVariable variable) {
		return PlaceholderRegistration.getInstance().replaceAll(toRaw(), variable, this);
	}

	static Placeholder[] values() {
		return Constant.values(Placeholder.class, Placeholder.class).toArray(new Placeholder[0]);
	}

	@Note("Weak reference for new instances, keep track of new placeholders!")
	static @NotNull Placeholder valueOf(char start, char end) {
		return Arrays.stream(values()).filter(v -> v.isSame(start, end)).findFirst().orElse(new Placeholder() {
			@Override
			public char start() {
				return start;
			}

			@Override
			public char end() {
				return end;
			}
		});
	}


}
