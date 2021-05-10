package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.Synchronous;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class CuboidController implements Listener {

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (event.getPlayer().getBedSpawnLocation() == null) {
			if (Region.spawn().isPresent()) {
				Region.Resident r = Region.Resident.get(event.getPlayer());
				Message msg = Message.form(event.getPlayer());
				event.setRespawnLocation(Region.spawn().get().location());
				r.setSpawnTagged(true);
				r.setPastSpawn(false);
				msg.send("&aWelcome to spawn! Try not to die again..");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFirstJoin(PlayerJoinEvent event) {
		Region.Resident r = Region.Resident.get(event.getPlayer());
		if (!event.getPlayer().hasPlayedBefore()) {
			Schedule.sync(() -> {
				if (Region.spawn().isPresent()) {
					event.getPlayer().teleport(Region.spawn().get().location());
					r.setSpawnTagged(true);
					r.setPastSpawn(false);
				}
			}).wait(2);
		} else {
			r.setSpawnTagged(false);
		}
		spawnListener(event.getPlayer()).repeatReal(5, 18);
	}

	public Synchronous spawnListener(Player p) {
		Region.Resident r = Region.Resident.get(p);
		return Schedule.sync(() -> {
			if (!Region.match(p.getLocation())) {
				if (!r.isPastSpawn()) {
					r.setPastSpawn(true);
					Message.form(p).send("&4&oYou can now build and are free to claim land. Be careful of locals..");
				}
				if (r.isSpawnTagged()) {
					r.setSpawnTagged(false);
					Message.form(p).send("&c&oYou are no longer spawn protected.");
				}
			}
			if (Region.get(p.getLocation()) instanceof Region.Spawn) {
				if (r.isPastSpawn()) {
					r.setPastSpawn(false);
					Message.form(p).send("&c&oYou are now within spawn area.. building and claiming prohibited.");
				}
				if (r.isSpawnTagged()) {
					Message.form(p).action("&7Spawn Protection: &aOn");
				} else {
					Message.form(p).action("&7Spawn Protection: &cNone");
				}
			} else {
				if (r.isSpawnTagged()) {
					r.setSpawnTagged(false);
					Message.form(p).send("&c&oYou are no longer spawn protected.");
				}
			}
		}).debug().cancelAfter(p);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() instanceof Player) {
			Player target = (Player) e.getTarget();
			Region.Resident r = Region.Resident.get(target);
			if (r.isSpawnTagged()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (e.getItem() == null)
				return;

			if (!e.getPlayer().hasPermission("labyrinth.selection"))
				return;

			if (e.getItem().getType() == Material.WOODEN_AXE) {
				Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
				Message msg = Message.form(e.getPlayer());
				if (e.useInteractedBlock() != Event.Result.DENY) {
					e.setUseInteractedBlock(Event.Result.DENY);
				}

				if (e.getPlayer().isSneaking()) {
					selection.setPos2(null);
					selection.setPos1(null);
					msg.send("&3You have cleared your cuboid selection.");
					return;
				}

				selection.setPos1(e.getClickedBlock().getLocation());

				msg.send("&aYou have set position 1 for a new cuboid.");
			}
		}
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getItem() == null)
				return;

			if (!e.getPlayer().hasPermission("labyrinth.selection"))
				return;

			if (e.getItem().getType() == Material.WOODEN_AXE) {
				Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
				Message msg = Message.form(e.getPlayer());
				if (e.useInteractedBlock() != Event.Result.DENY) {
					e.setUseInteractedBlock(Event.Result.DENY);
				}

				if (e.getPlayer().isSneaking()) {
					selection.setPos2(null);
					selection.setPos1(null);
					msg.send("&3You have cleared your cuboid selection.");
					return;
				}

				selection.setPos2(e.getClickedBlock().getLocation());

				msg.send("&aYou have set position 2 for a new cuboid.");
			}

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player target = (Player) event.getEntity();
			Player p = (Player) event.getDamager();

			Message msg = Message.form(p);

			if (Region.match(target.getLocation())) {
				Region region = Region.get(target.getLocation());

				if (region instanceof Region.Spawn) {

					Region.Spawn spawn = (Region.Spawn) region;

					Region.Resident o = Region.Resident.get(p);

					Region.Resident t = Region.Resident.get(target);

					if (t.isSpawnTagged()) {
						event.setCancelled(true);
						msg.send("&3&o" + target.getDisplayName() + " &bis protected, you can't hurt them.");
					}
					if (o.isSpawnTagged()) {
						if (!t.isSpawnTagged()) {
							o.setSpawnTagged(false);
							msg.send("&c&oRemoving protection under offensive maneuvers. &7" + target.getDisplayName() + "&c&o isn't protected.");
						}
					}
				}
			}

		}

		if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile && (
				(Projectile) event.getDamager()).getShooter() instanceof Player) {
			Projectile pr = (Projectile) event.getDamager();
			Player p = (Player) pr.getShooter();
			Player target = (Player) event.getEntity();

			Message msg = Message.form(p);

			if (Region.match(target.getLocation())) {
				Region region = Region.get(target.getLocation());

				if (region instanceof Region.Spawn) {

					Region.Resident o = Region.Resident.get(p);

					Region.Resident t = Region.Resident.get(target);

					if (t.isSpawnTagged()) {
						event.setCancelled(true);
						msg.send("&3&o" + target.getDisplayName() + " &bis protected, you can't hurt them.");
					}
					if (o.isSpawnTagged()) {
						if (t.isSpawnTagged()) {
							o.setSpawnTagged(false);
							msg.send("&c&oRemoving protection under offensive maneuvers. &7" + target.getDisplayName() + "&c&o isn't protected.");
						}
					}
				}
			}
		}


		if (event.getEntity() instanceof Player && event.getDamager() instanceof Monster) {
			Player target = (Player) event.getEntity();
			if (Region.Resident.get(target).isSpawnTagged()) {
				event.setCancelled(true);
			}
		}

		if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile && (
				(Projectile) event.getDamager()).getShooter() instanceof Monster) {
			Player target = (Player) event.getEntity();
			if (Region.Resident.get(target).isSpawnTagged()) {
				event.setCancelled(true);
			}
		}
	}


}
