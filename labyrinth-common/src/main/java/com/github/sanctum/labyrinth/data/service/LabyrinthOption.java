package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;

public enum LabyrinthOption {

	HEAD_PRE_CACHE,
	IMPL_REGION_SERVICES;

	public boolean enabled() {

		FileManager conf = FileList.search(LabyrinthProvider.getInstance().getPluginInstance()).get("config");

		switch (this) {
			case HEAD_PRE_CACHE:
				return conf.read(f -> f.getBoolean("automatic-head-cache"));
			case IMPL_REGION_SERVICES:
				return conf.read(f -> f.getBoolean("region-service-impl"));
			default:
				return false;
		}
	}

}
