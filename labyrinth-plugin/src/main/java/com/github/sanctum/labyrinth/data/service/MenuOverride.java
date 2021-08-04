package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MenuOverride extends LabyrinthAPI {

	<T extends Menu, R> R getMenu(Predicate<Menu> predicate, Function<T, R> function);

}
