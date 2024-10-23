package com.github.sanctum.labyrinth.library;

import org.jetbrains.annotations.NotNull;

public final class RadiusFilterOptions {
    private final SimpleTeleport teleportInstance;
    private long delay = 10L;
    private String delayMessage = "You will be teleported in {0} seconds";

    public RadiusFilterOptions(@NotNull SimpleTeleport teleport) {
        this.teleportInstance = teleport;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setDelayMessage(String delayMessage) {
        this.delayMessage = delayMessage;
    }

    public long getDelay() {
        return delay;
    }

    public String getDelayMessage() {
        return delayMessage;
    }

    public SimpleTeleport toTeleport() {
        return this.teleportInstance;
    }

}
