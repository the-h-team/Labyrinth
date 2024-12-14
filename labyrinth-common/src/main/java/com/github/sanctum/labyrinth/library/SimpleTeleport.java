package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.interfacing.Identifiable;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Date;

public class SimpleTeleport extends Teleport {

    private final Identifiable entity;
    private final Location location;
    private final Identifiable[] entities;
    private final RadiusFilterOptions radiusFilterOptions = new RadiusFilterOptions(this);
    private Date accepted;
    private State state;
    private Location initialLocation;
    private String teleportMessage = "You have been teleported.";

    public SimpleTeleport(Identifiable entity, Location targetLocation) {
        this.location = targetLocation;
        this.entity = entity;
        this.entities = null;
        this.state = State.INITIALIZED;
        traffic.add(this);
        this.initialLocation = new Location(entity.isEntity() ? entity.getAsEntity().getLocation() : entity.getAsPlayer().getLocation());
    }

    public SimpleTeleport(Location targetLocation, Identifiable... entities) {
        this.location = targetLocation;
        this.entity = null;
        this.entities = entities;
        this.state = State.INITIALIZED;
        traffic.add(this);
        this.initialLocation = new Location(entity.isEntity() ? entity.getAsEntity().getLocation() : entity.getAsPlayer().getLocation());
    }

    public void setTeleportedMessage(String teleportMessage) {
        this.teleportMessage = teleportMessage;
    }

    public RadiusFilterOptions getRadiusFilterOptions() {
        return radiusFilterOptions;
    }

    public Location getInitialLocation() {
        return initialLocation;
    }

    public Identifiable getEntity() {
        return entity;
    }

    @Override
    public Identifiable[] getEntities() {
        return this.entities;
    }

    public TimeWatch.Recording getTimeAccepted() {
        if (accepted == null) return null;
        return TimeWatch.Recording.subtract(accepted.getTime());
    }

    public Location getTargetLocation() {
        return this.location;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

    void teleportPlayer(@NotNull Identifiable i, @NotNull Location loc) {
        if (loc.isLocation()) {
            i.getAsPlayer().teleport(loc.getAsLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
        if (loc.isEntity()) {
            i.getAsPlayer().teleport(loc.getAsEntity().getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
        if (loc.isPlayer()) {
            i.getAsPlayer().teleport(loc.getAsPlayer().getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
        Mailer.empty(i.getAsPlayer()).chat(teleportMessage).queue();
    }

    void teleportEntity(@NotNull Identifiable i, @NotNull Location loc) {
        if (loc.isLocation()) {
            i.getAsEntity().teleport(loc.getAsLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
        if (loc.isEntity()) {
            i.getAsEntity().teleport(loc.getAsEntity().getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
        if (loc.isPlayer()) {
            i.getAsEntity().teleport(loc.getAsPlayer().getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }

    @Override
    public void run() {
        RadiusFilter filter = this.radiusFilter;
        Location loc = getTargetLocation();
        // check if were sending multiple entities or just one
        if (hasMultipleEntities()) {
            if (filter != null) {
                for (Identifiable i : getEntities()) {
                    if (i.getAsPlayer() != null) {
                        boolean hasWait = !filter.accept(i);
                        if (hasWait) {
                            // wait for configured time
                            setState(State.TELEPORTING);
                            accepted = new Date();
                            Mailer.empty(i.getAsPlayer()).chat(MessageFormat.format(radiusFilterOptions.getDelayMessage(), radiusFilterOptions.getDelay())).queue();
                            TaskScheduler.of(() -> {
                                if (!traffic.contains(this)) return;
                                if (getState() == State.TELEPORTING) {
                                    teleportPlayer(i, loc);
                                } else {
                                    flush();
                                }
                            }).scheduleLater(20L * radiusFilterOptions.getDelay());
                        } else {
                            // instance teleport
                            // theyre a player
                            setState(State.TELEPORTING);
                            accepted = new Date();
                            teleportPlayer(i, loc);
                            flush();
                        }
                    }
                }
            } else {
                for (Identifiable i : getEntities()) {
                    if (i.isEntity()) {
                        setState(State.TELEPORTING);
                        accepted = new Date();
                        teleportEntity(i, loc);
                        flush();
                    } else if (i.getAsPlayer() != null) {
                        // theyre a player
                        setState(State.TELEPORTING);
                        accepted = new Date();
                        teleportPlayer(i, loc);
                        flush();
                    }
                    this.completionRunners.forEach(r -> r.run(i));
                }
            }
        } else {
            if (filter != null) {
                Identifiable i = getEntity();
                if (i.getAsPlayer() != null) {
                    boolean hasWait = !filter.accept(i);
                    if (hasWait) {
                        // wait for configured time
                        setState(State.TELEPORTING);
                        accepted = new Date();
                        Mailer.empty(i.getAsPlayer()).chat(MessageFormat.format(radiusFilterOptions.getDelayMessage(), radiusFilterOptions.getDelay())).queue();
                        TaskScheduler.of(() -> {
                            if (!traffic.contains(this)) return;
                            if (getState() == State.TELEPORTING) {
                                teleportPlayer(i, loc);
                            } else {
                                flush();
                            }
                        }).scheduleLater(20L * radiusFilterOptions.getDelay());
                    } else {
                        // instance teleport
                        // theyre a player
                        setState(State.TELEPORTING);
                        accepted = new Date();
                        teleportPlayer(i, loc);
                        flush();
                    }
                }
            } else {
                Identifiable i = getEntity();
                if (i.isEntity()) {
                    setState(State.TELEPORTING);
                    accepted = new Date();
                    teleportEntity(i, loc);
                    flush();
                } else if (i.getAsPlayer() != null) {
                    // theyre a player
                    setState(State.TELEPORTING);
                    accepted = new Date();
                    teleportPlayer(i, loc);
                    flush();
                }
                this.completionRunners.forEach(r -> r.run(i));
            }
        }
    }

    public void flush() {
        setState(State.EXPIRED);
        traffic.remove(this);
    }

}
