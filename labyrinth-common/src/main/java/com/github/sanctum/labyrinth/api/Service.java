package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.data.ServiceType;
import com.github.sanctum.panther.annotation.Removal;

/**
 * Used to mark objects that are recursively used.
 */
@Removal
public interface Service extends com.github.sanctum.panther.recursive.Service {

	ServiceType<ActionComponentService> COMPONENTS = new ServiceType<>();
	ServiceType<CooldownService> COOLDOWNS = new ServiceType<>();
	ServiceType<LegacyCheckService> LEGACY = new ServiceType<>();
	ServiceType<MessagingService> MESSENGER = new ServiceType<>();
	ServiceType<PersistentDataService> DATA = new ServiceType<>();
	ServiceType<RecordingService> RECORDING = new ServiceType<>();
	ServiceType<TaskService> TASK = new ServiceType<>();
	ServiceType<PlatformKeyService> KEYS = new ServiceType<>();

}
