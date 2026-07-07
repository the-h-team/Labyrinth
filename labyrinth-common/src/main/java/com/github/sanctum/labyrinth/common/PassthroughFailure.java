package com.github.sanctum.labyrinth.common;

public final class PassthroughFailure {
    private long delay = 10L;
    private String delayMessage = "You will be teleported in {0} seconds";

    public void setPlayerWait(long delay) {
        this.delay = delay;
    }

    public void setFailureMessage(String delayMessage) {
        this.delayMessage = delayMessage;
    }

    public long getPlayerWait() {
        return delay;
    }

    public String getFailureMessage() {
        return delayMessage;
    }

}
