package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.ServiceType;

/**
 * Used to mark objects that are recursively used.
 */
public interface Service {

	ServiceType<ActionComponentService> COMPONENTS = new ServiceType<ActionComponentService>(LabyrinthProvider::getInstance);
	ServiceType<CooldownService> COOLDOWNS = new ServiceType<CooldownService>(LabyrinthProvider::getInstance);
	ServiceType<LegacyCheckService> LEGACY = new ServiceType<LegacyCheckService>(LabyrinthProvider::getInstance);
	ServiceType<MessagingService> MESSENGER = new ServiceType<MessagingService>(LabyrinthProvider::getInstance);
	ServiceType<PersistentDataService> DATA = new ServiceType<PersistentDataService>(LabyrinthProvider::getInstance);
	ServiceType<RecordService> RECORDING = new ServiceType<RecordService>(LabyrinthProvider::getInstance);
	ServiceType<TaskService> TASK = new ServiceType<TaskService>(LabyrinthProvider::getInstance);

}
