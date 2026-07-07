package com.github.sanctum.labyrinth.permissions.entity;

import com.github.sanctum.labyrinth.data.PlayerInfo;

public interface User extends Permissible {

	PlayerInfo toLabyrinth();

	Group getGroup();

	Group getGroup(String world);

}
