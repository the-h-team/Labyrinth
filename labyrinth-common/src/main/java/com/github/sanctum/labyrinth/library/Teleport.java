package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.service.PlayerSearch;
import com.github.sanctum.labyrinth.interfacing.Identifiable;
import com.github.sanctum.panther.util.Applicable;
import com.github.sanctum.panther.util.Check;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Teleport implements Applicable {
    protected static final Set<Teleport> traffic = new HashSet<>();
    protected final Set<CompletionRunner> completionRunners = new HashSet<>();
    protected RadiusFilter radiusFilter;

    public static @Nullable Teleport get(Identifiable entity) {
        return traffic.stream().filter(r -> (r.hasMultipleEntities() && Arrays.stream(r.getEntities()).anyMatch(i -> i.getUniqueId().equals(entity.getUniqueId()))) || (!r.hasMultipleEntities() && entity.getUniqueId().equals(r.getEntity().getUniqueId()))).findFirst().orElse(null);
    }

    public static @Nullable Teleport get(Player player) {
        return get(Identifiable.wrap(player));
    }

    public abstract State getState();

    public abstract void setState(State state);

    public abstract Identifiable getEntity();

    public abstract Identifiable[] getEntities();

    public abstract Location getTargetLocation();

    public abstract Location getInitialLocation();

    public abstract TimeWatch.Recording getTimeAccepted();

    public abstract void run();

    public abstract void flush();

    public void addCompletionRunner(@NotNull Teleport.CompletionRunner operator) {
        this.completionRunners.add(operator);
    }

    public void setRadiusFilter(@NotNull RadiusFilter filter) {
        this.radiusFilter = filter;
    }

    public boolean hasMultipleEntities() {
        return getEntity() == null & getEntities() != null;
    }

    /**
     * Completion runners are independent to their respective teleport object, there can be multiple, and they can help you handle operations AFTER a successful teleport.
     */
    @FunctionalInterface
    public interface CompletionRunner {

        void run(Identifiable parent);

    }

    public interface RadiusFilter {

        boolean accept(Identifiable player);

        int radius();

    }

    public enum State {
        INITIALIZED, TELEPORTING, EXPIRED
    }

    /**
     * An object container for either a {@link Player}, {@link org.bukkit.Location} or {@link org.bukkit.entity.Entity}
     */
    public static class Location {

        private final Object target;

        public Location(Object target) throws IllegalArgumentException {
            this.target = target;
            Check.argument(org.bukkit.Location.class.isAssignableFrom(target.getClass()) || Player.class.isAssignableFrom(target.getClass()) || Entity.class.isAssignableFrom(target.getClass()), "Teleportation target invalid! Expected: [Player, Entity, Location] Got: [" + target.getClass().getSimpleName() + "]");
        }

        public boolean isEntity() {
            return !isPlayer() && target instanceof Entity;
        }

        public boolean isPlayer() {
            return target instanceof Player;
        }

        public boolean isLocation() {
            return target instanceof org.bukkit.Location;
        }

        public org.bukkit.Location getAsLocation() {
            return (org.bukkit.Location) target;
        }

        public Player getAsPlayer() {
            return (Player) target;
        }

        public Entity getAsEntity() {
            return (Entity) target;
        }

    }

    /**
     * The main runner for teleportation flushing. Here you can check things like to see if the entity moved before teleport.
     */
    public abstract static class Runner implements Applicable {

        /**
         * You can choose to run this as its own task or simple plug the {@link Runner#run(Identifiable)} method into your own timer.
         */
        @Override
        public void run() {
            PlayerSearch.values().forEach(this::run);
        }

        public abstract void run(Identifiable identifiable);

    }
}
