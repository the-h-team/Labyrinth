package com.github.sanctum.labyrinth.permissions.entity;

import com.github.sanctum.labyrinth.data.LabyrinthUser;

public interface User extends Permissible {

	LabyrinthUser getLabyrinth();

	Group getGroup();

	Group getGroup(String world);

}
