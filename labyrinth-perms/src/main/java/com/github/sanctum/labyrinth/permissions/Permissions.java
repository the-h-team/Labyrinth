package com.github.sanctum.labyrinth.permissions;

import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.data.service.PlayerSearch;
import com.github.sanctum.labyrinth.permissions.entity.Group;
import com.github.sanctum.labyrinth.permissions.entity.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public interface Permissions {

	Plugin getProvider();

	boolean isMultiWorld();

	boolean isSuperPerms();

	boolean isGroupsAllowed();

	User getUser(OfflinePlayer player);

	@Deprecated
	User getUser(LabyrinthUser user);

	default User getUser(PlayerSearch search) {
		return getUser(search.getPlayer());
	}

	Group getGroup(String name);

	User[] getUsers();

	Group[] getGroups();


}
