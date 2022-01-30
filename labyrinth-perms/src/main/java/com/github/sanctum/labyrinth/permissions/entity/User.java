package com.github.sanctum.labyrinth.permissions.entity;

import com.github.sanctum.labyrinth.data.service.PlayerSearch;

public interface User extends Permissible {

	PlayerSearch toLabyrinth();

	Group getGroup();

	Group getGroup(String world);

}
