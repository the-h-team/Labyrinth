package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;

public enum  LabyrinthOptions {

	HEAD_PRE_CACHE, IMPL_REGION_SERVICES, IMPL_AFK;

	public boolean enabled() {

		FileManager conf = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).find("config");

		switch (this) {
			case HEAD_PRE_CACHE:
				return conf.getConfig().getBoolean("automatic-head-cache");
			case IMPL_REGION_SERVICES:
				return conf.getConfig().getBoolean("region-service-impl");
			case IMPL_AFK:
				return conf.getConfig().getBoolean("labyrinth-provided-afk");
			default:
				return false;
		}
	}

}
