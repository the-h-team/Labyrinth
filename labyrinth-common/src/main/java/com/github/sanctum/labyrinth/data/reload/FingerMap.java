package com.github.sanctum.labyrinth.data.reload;

import java.util.Map;

/**
 * An object similar to a supplier that acts as a write-key to already existing values within a {@link FingerPrint}
 * Make sure if reading from a file to reload it before reading its values to make changes apply!
 */
@FunctionalInterface
public interface FingerMap {

	Map<String, Object> accept();

}
