package com.github.sanctum.labyrinth.placeholders;

import com.github.sanctum.labyrinth.interfacing.Nameable;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for development information related to {@link PlaceholderTranslation} like the creator's of the
 * implementation and the version and name of the implementation.
 */
public interface PlaceholderTranslationInformation extends Nameable {

	/**
	 * @return the name for this implementation.
	 */
	@NotNull String getName();

	@NotNull String[] getAuthors();

	@NotNull String getVersion();


}
