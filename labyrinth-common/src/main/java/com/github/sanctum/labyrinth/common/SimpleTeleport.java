package com.github.sanctum.labyrinth.common;

import com.github.sanctum.labyrinth.interfacing.Identifiable;
import com.github.sanctum.labyrinth.task.BukkitTaskPredicate;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Date;

public class SimpleTeleport extends Teleport {

    private final Identifiable entity;
    private final Location location;
    private final Identifiable[] entities;
    private final PassthroughFailure passthroughFailure = new PassthroughFailure();
    private Date accepted;
    private State state;
    private Location initialLocation;
    private String successMessage = "You have been teleported.";

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

    public void setSuccessMessage(String teleportMessage) {
        this.successMessage = teleportMessage;
    }

    public PassthroughFailure getPassthroughFailure() {
        return passthroughFailure;
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

    public StopWatch.Recording getTimeAccepted() {
        if (accepted == null) return null;
        return StopWatch.Recording.subtract(accepted.getTime());
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

    // adding non-blocking chunk loading, wait for chunk to load if not and only teleport once chunks are loaded!!!
    boolean teleportPlayer(@NotNull Identifiable i, @NotNull Location loc) {
        org.bukkit.Location location = loc.isLocation() ? loc.getAsLocation() : (loc.isEntity() ? loc.getAsEntity().getLocation() : loc.getAsPlayer().getLocation());
        Chunk chunk = location.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load(true);
            MessageComposer.empty(i.getAsPlayer()).chat(successMessage).queue();
            // wait then tp with repeat task make sure chunk is loaded first (non-blocking!)
            TaskScheduler.of(() -> {
            }).scheduleTimer(i.getUniqueId() + ";TELEPORT" + hashCode(), 13, 13, BukkitTaskPredicate.cancelAfter(task -> {
                if (chunk.isLoaded()) {
                    //teleport them
                    i.getAsPlayer().teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
                    task.cancel();
                    return false;
                }
                return true;
            }));
            return false;
        }
        MessageComposer.empty(i.getAsPlayer()).chat(successMessage).deploy(messageComposer -> i.getAsPlayer().teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND));
        return true;
    }

    boolean teleportEntity(@NotNull Identifiable i, @NotNull Location loc) {
        org.bukkit.Location location = loc.isLocation() ? loc.getAsLocation() : (loc.isEntity() ? loc.getAsEntity().getLocation() : loc.getAsPlayer().getLocation());
        Chunk chunk = location.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load(true);
            TaskScheduler.of(() -> {
            }).scheduleTimer(i.getUniqueId() + ";ENTITY;TELEPORT" + hashCode(), 13, 13, BukkitTaskPredicate.cancelAfter(task -> {
                if (chunk.isLoaded()) {
                    //teleport them only once chunk is loaded for sure
                    i.getAsEntity().teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
                    task.cancel();
                    return false;
                }
                return true;
            }));
            return false;
        }
        i.getAsEntity().teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    void work(Identifiable i, RadiusFilter filter, Location loc) {
        if (i.getAsPlayer() != null) {
            boolean hasWait = !filter.hasPassthrough(i);
            setState(State.TELEPORTING);
            accepted = new Date();
            if (hasWait) {
                // wait for configured time
                MessageComposer.empty(i.getAsPlayer())
                        .chat(MessageFormat.format(passthroughFailure.getFailureMessage(), passthroughFailure.getPlayerWait())) // format failure message with wait time if inserted
                        .queue();
                TaskScheduler.of(() -> {
                    if (!traffic.contains(this)) return;
                    if (getState() == State.TELEPORTING) {
                        ActionbarInstance instance = ActionbarInstance.of(i.getAsPlayer());
                        if (!teleportPlayer(i, loc)) {
                        }
                    } else {
                        flush();
                    }
                }).scheduleLater(20L * passthroughFailure.getPlayerWait());
            } else {
                // instance teleport
                // theyre a player
                if (!teleportPlayer(i, loc)) {
                }
                flush();
            }
        }
    }

    void workFilterless(Identifiable i, Location loc) {
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

    @Override
    public void run() {
        RadiusFilter filter = this.radiusFilter;
        Location loc = getTargetLocation();
        Identifiable[] identifiable = hasMultipleEntities() ? getEntities() : new Identifiable[]{getEntity()};

        // check if were sending multiple entities or just one
        for (Identifiable i : identifiable) {
            if (filter != null) {
                work(i, filter, loc);
            } else workFilterless(i, loc);
        }
    }

    public void flush() {
        setState(State.EXPIRED);
        traffic.remove(this);
    }

}
