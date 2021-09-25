package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.library.StringUtils;
import java.util.Objects;

public interface EqualsOperator {

	default boolean equals(Object o, Object... objects) {
		for (Object ob : objects) {
			if (o instanceof String && ob instanceof String) {
				if (StringUtils.use(((String)o)).containsIgnoreCase(((String)ob))) {
					return true;
				}
			}
			if (Objects.equals(ob, o)) {
				return true;
			}
		}
		return false;
	}

}
