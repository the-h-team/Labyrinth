package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.interfacing.Identifiable;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.util.Check;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class Teleport {
	private static final Set<Teleport> REQUESTS = new HashSet<>();
	private final Set<CompletionResult> completionResults = new HashSet<>();

	public abstract State getState();

	public abstract void setState(State state);

	public abstract Identifiable getEntity();

	public abstract Location getLocation();

	public abstract org.bukkit.Location getInitialLocation();

	public abstract TimeWatch.Recording getTimeAccepted();

	public abstract void execute();

	public abstract void flush();

	public void success(@NotNull Teleport.CompletionResult operator) {
		this.completionResults.add(operator);
	}

	public static Teleport get(Identifiable entity) {
		return REQUESTS.stream().filter(r -> r.getEntity().equals(entity)).findFirst().orElse(null);
	}

	public static Teleport get(Player player) {
		return get(Identifiable.wrap(player));
	}

	@FunctionalInterface
	public interface CompletionResult {

		void onTeleportSuccess(Identifiable parent);

	}

	public static class Impl extends Teleport {

		private final Identifiable entity;
		private final Location location;
		private final String teleportMsg;
		private final int seconds;
		private Date accepted;
		private State state;
		private org.bukkit.Location initialLocation;

		public Impl(Identifiable entity, Location targetLocation) {
			this.location = targetLocation;
			this.entity = entity;
			this.state = State.INITIALIZED;
			REQUESTS.add(this);
			this.teleportMsg = "Teleporting in 10 seconds dont move!";
			this.seconds = 10;
		}

		public org.bukkit.Location getInitialLocation() {
			return initialLocation;
		}

		public Identifiable getEntity() {
			return entity;
		}

		public TimeWatch.Recording getTimeAccepted() {
			if (accepted == null) return null;
			return TimeWatch.Recording.subtract(accepted.getTime());
		}

		@Override
		public void execute() {

		}

		public Location getLocation() {
			return this.location;
		}

		public void setState(State state) {
			this.state = state;
		}

		public State getState() {
			return this.state;
		}
/*
		public void execute() {
			if (entity.isAssociate()) {
				initialLocation = entity.getAsAssociate().getTag().getPlayer().getPlayer().getLocation();
				if (this.target != null) {
					if (entity.getAsAssociate().getTag().getPlayer().getPlayer().getNearbyEntities(30, 0, 30).stream().noneMatch(e -> e instanceof Player && getEntity().getAsAssociate().getClan().getMember(m -> m.getName().equals(e.getName())) == null)) {
						this.state = State.TELEPORTING;
						this.accepted = new Date();
						if (!REQUESTS.contains(this)) return;
						Clan.ACTION.sendMessage(getLocation().getAsPlayer(), "&a" + entity.getAsAssociate().getName() + " is teleporting to you.");
						AssociateTeleportEvent event = ClanVentBus.call(new AssociateTeleportEvent(getEntity().getAsAssociate(), new Location(this.target)));
						if (!event.isCancelled()) {
							completionResults.forEach(operator -> operator.onTeleportSuccess(entity));
							entity.getAsAssociate().getTag().getPlayer().getPlayer().teleport(event.getTarget().getAsPlayer(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
							flush();
							entity.getAsAssociate().getTag().getPlayer().getPlayer().getWorld().playSound(entity.getAsAssociate().getTag().getPlayer().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
						}
					} else {
						getEntity().getAsAssociate().getMailer().chat(MessageFormat.format(teleportMsg, seconds)).deploy();
						Clan.ACTION.sendMessage(getLocation().getAsPlayer(), "&a" + entity.getAsAssociate().getName() + " is teleporting to you.");
						this.state = State.TELEPORTING;
						this.accepted = new Date();
						TaskScheduler.of(() -> {
							if (!REQUESTS.contains(this)) return;
							if (getState() == State.TELEPORTING) {
								AssociateTeleportEvent event = ClanVentBus.call(new AssociateTeleportEvent(getEntity().getAsAssociate(), new Location(this.target)));
								if (!event.isCancelled()) {
									completionResults.forEach(operator -> operator.onTeleportSuccess(entity));
									entity.getAsAssociate().getTag().getPlayer().getPlayer().teleport(event.getTarget().getAsPlayer(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
									flush();
									entity.getAsAssociate().getTag().getPlayer().getPlayer().getWorld().playSound(entity.getAsAssociate().getTag().getPlayer().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
								}
							} else {
								flush();
							}
						}).scheduleLater(20 * 10);
					}
				} else {
					if (entity.getAsAssociate().getTag().getPlayer().getPlayer().getNearbyEntities(30, 0, 30).stream().noneMatch(e -> e instanceof Player && getEntity().getAsAssociate().getClan().getMember(m -> m.getName().equals(e.getName())) == null)) {
						this.state = State.TELEPORTING;
						this.accepted = new Date();
						AssociateTeleportEvent event = ClanVentBus.call(new AssociateTeleportEvent(getEntity().getAsAssociate(), new Location(this.location)));
						if (!event.isCancelled()) {
							completionResults.forEach(operator -> operator.onTeleportSuccess(entity));
							entity.getAsAssociate().getTag().getPlayer().getPlayer().teleport(event.getTarget().getAsLocation(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
							flush();
							entity.getAsAssociate().getTag().getPlayer().getPlayer().getWorld().playSound(entity.getAsAssociate().getTag().getPlayer().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
						}
					} else {
						getEntity().getAsAssociate().getMailer().chat(MessageFormat.format(teleportMsg, seconds)).deploy();
						this.state = State.TELEPORTING;
						this.accepted = new Date();
						TaskScheduler.of(() -> {
							if (!REQUESTS.contains(this)) return;
							if (getState() == State.TELEPORTING) {
								AssociateTeleportEvent event = ClanVentBus.call(new AssociateTeleportEvent(getEntity().getAsAssociate(), new Location(this.location)));
								if (!event.isCancelled()) {
									completionResults.forEach(operator -> operator.onTeleportSuccess(entity));
									entity.getAsAssociate().getTag().getPlayer().getPlayer().teleport(event.getTarget().getAsLocation(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
									flush();
									entity.getAsAssociate().getTag().getPlayer().getPlayer().getWorld().playSound(entity.getAsAssociate().getTag().getPlayer().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
								}
							} else {
								flush();
							}
						}).scheduleLater(20 * 10);
					}
				}
			} else {
				if (entity.isClan()) {
					if (this.target != null) {
						getEntity().getAsClan().getMembers().forEach(a -> {
							if (a.getTag().isPlayer() && !a.getTag().getPlayer().isOnline()) return;
							a.getMailer().chat(MessageFormat.format(teleportMsg, seconds)).deploy();
							Clan.ACTION.sendMessage(getLocation().getAsPlayer(), "&a" + a.getName() + " is teleporting to you.");
							this.state = State.TELEPORTING;
							this.accepted = new Date();
							TaskScheduler.of(() -> {
								if (!REQUESTS.contains(this)) return;
								if (getState() == State.TELEPORTING) {
									AssociateTeleportEvent event = ClanVentBus.call(new AssociateTeleportEvent(a, new Location(this.target)));
									if (!event.isCancelled()) {
										completionResults.forEach(operator -> operator.onTeleportSuccess(a));
										a.getTag().getPlayer().getPlayer().teleport(event.getTarget().getAsPlayer(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
										flush();
										a.getTag().getPlayer().getPlayer().getWorld().playSound(entity.getAsAssociate().getTag().getPlayer().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
									}
								} else {
									flush();
								}
							}).scheduleLater(20 * 10);
						});
					} else {
						getEntity().getAsClan().getMembers().forEach(a -> {
							if (a.getTag().isPlayer() && !a.getTag().getPlayer().isOnline()) return;
							a.getMailer().chat(MessageFormat.format(teleportMsg, seconds)).deploy();
							this.state = State.TELEPORTING;
							this.accepted = new Date();
							TaskScheduler.of(() -> {
								if (!REQUESTS.contains(this)) return;
								if (getState() == State.TELEPORTING) {
									AssociateTeleportEvent event = ClanVentBus.call(new AssociateTeleportEvent(a, new Location(this.location)));
									if (!event.isCancelled()) {
										completionResults.forEach(operator -> operator.onTeleportSuccess(a));
										a.getTag().getPlayer().getPlayer().teleport(event.getTarget().getAsLocation());
										flush();
										a.getTag().getPlayer().getPlayer().getWorld().playSound(entity.getAsAssociate().getTag().getPlayer().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
									}
								} else {
									flush();
								}
							}).scheduleLater(20 * 10);
						});
					}
				}
				if (entity.isPlayer()) {
					initialLocation = getEntity().getAsPlayer().getPlayer().getLocation();
					if (this.target != null) {
						Clan.ACTION.sendMessage(getEntity().getAsPlayer().getPlayer(), MessageFormat.format(teleportMsg, seconds));
						Clan.ACTION.sendMessage(getLocation().getAsPlayer(), "&a" + entity.getAsAssociate().getName() + " is teleporting to you.");
						this.state = State.TELEPORTING;
						this.accepted = new Date();
						TaskScheduler.of(() -> {
							if (!REQUESTS.contains(this)) return;
							if (getState() == State.TELEPORTING) {
								PlayerTeleportEvent event = ClanVentBus.call(new PlayerTeleportEvent(getEntity().getAsPlayer().getPlayer(), new Location(this.target)));
								if (!event.isCancelled()) {
									completionResults.forEach(operator -> operator.onTeleportSuccess(entity));
									getEntity().getAsPlayer().getPlayer().teleport(event.getTarget().getAsPlayer());
									flush();
									getEntity().getAsPlayer().getPlayer().getWorld().playSound(getEntity().getAsPlayer().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
								}
							} else {
								flush();
							}
						}).scheduleLater(20 * 10);
					} else {
						Clan.ACTION.sendMessage(getEntity().getAsPlayer().getPlayer(), MessageFormat.format(teleportMsg, seconds));
						this.state = State.TELEPORTING;
						this.accepted = new Date();
						TaskScheduler.of(() -> {
							if (!REQUESTS.contains(this)) return;
							if (getState() == State.TELEPORTING) {
								PlayerTeleportEvent event = ClanVentBus.call(new PlayerTeleportEvent(getEntity().getAsPlayer().getPlayer(), new Location(this.location)));
								if (!event.isCancelled()) {
									completionResults.forEach(operator -> operator.onTeleportSuccess(entity));
									getEntity().getAsPlayer().getPlayer().teleport(event.getTarget().getAsLocation());
									flush();
									getEntity().getAsPlayer().getPlayer().getWorld().playSound(getEntity().getAsPlayer().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10, 1);
								}
							} else {
								flush();
							}
						}).scheduleLater(20 * 10);
					}
				}
			}
		}

 */

		public void flush() {
			setState(State.EXPIRED);
			REQUESTS.remove(this);
		}

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

	}
}
