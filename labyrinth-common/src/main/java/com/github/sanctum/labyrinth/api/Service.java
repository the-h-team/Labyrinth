package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.ServiceType;
import com.github.sanctum.panther.annotation.Removal;

/**
 * Used to mark objects that are recursively used.
 */
@Removal
public interface Service {

	ServiceType<ActionComponentService> COMPONENTS = new ServiceType<>(LabyrinthProvider::getInstance);
	ServiceType<CooldownService> COOLDOWNS = new ServiceType<>(LabyrinthProvider::getInstance);
	ServiceType<LegacyCheckService> LEGACY = new ServiceType<>(LabyrinthProvider::getInstance);
	ServiceType<MessagingService> MESSENGER = new ServiceType<>(LabyrinthProvider::getInstance);
	ServiceType<PersistentDataService> DATA = new ServiceType<>(LabyrinthProvider::getInstance);
	ServiceType<RecordingService> RECORDING = new ServiceType<>(LabyrinthProvider::getInstance);
	ServiceType<TaskService> TASK = new ServiceType<>(LabyrinthProvider::getInstance);

}
