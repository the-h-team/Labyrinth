package com.github.sanctum.labyrinth.event;

import com.github.sanctum.labyrinth.data.Region;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.task.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CuboidController implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBuild(BlockPlaceEvent e) {
		if (Region.match(e.getBlock().getLocation()).isPresent()) {
			RegionBuildEvent event = new RegionBuildEvent(e.getPlayer(), Region.match(e.getBlock().getLocation()).get(), e.getBlock());
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBuild(BlockBreakEvent e) {
		if (Region.match(e.getBlock().getLocation()).isPresent()) {
			RegionDestroyEvent event = new RegionDestroyEvent(e.getPlayer(), Region.match(e.getBlock().getLocation()).get(), e.getBlock());
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				e.setCancelled(true);
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
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {

			if (Region.match(e.getClickedBlock().getLocation()).isPresent()) {
				RegionInteractEvent event = new RegionInteractionEvent(e.getPlayer(), Region.match(e.getClickedBlock().getLocation()).get(), e.getClickedBlock(), RegionInteractionEvent.ClickType.LEFT);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					e.setCancelled(true);
				}
			}

			if (e.getItem() == null)
				return;

			if (!e.getPlayer().hasPermission("labyrinth.selection"))
				return;

			if (e.getItem().getType() == Material.WOODEN_AXE) {
				Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
				if (e.useInteractedBlock() != Event.Result.DENY) {
					e.setUseInteractedBlock(Event.Result.DENY);
				}

				selection.setPos1(e.getClickedBlock().getLocation());

				CuboidSelectionEvent event = new CuboidSelectionEvent(selection);
				Bukkit.getPluginManager().callEvent(event);

			}
		}
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (Region.match(e.getClickedBlock().getLocation()).isPresent()) {
				RegionInteractEvent event = new RegionInteractionEvent(e.getPlayer(), Region.match(e.getClickedBlock().getLocation()).get(), e.getClickedBlock(), RegionInteractionEvent.ClickType.RIGHT);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					e.setCancelled(true);
				}
			}

			if (e.getItem() == null)
				return;

			if (!e.getPlayer().hasPermission("labyrinth.selection"))
				return;

			if (e.getItem().getType() == Material.WOODEN_AXE) {
				Cuboid.Selection selection = Cuboid.Selection.source(e.getPlayer());
				if (e.useInteractedBlock() != Event.Result.DENY) {
					e.setUseInteractedBlock(Event.Result.DENY);
				}

				selection.setPos2(e.getClickedBlock().getLocation());

				CuboidSelectionEvent event = new CuboidSelectionEvent(selection);
				Bukkit.getPluginManager().callEvent(event);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player target = (Player) event.getEntity();
			Player p = (Player) event.getDamager();

			Message msg = Message.form(p);

			Region.Resident r = Region.Resident.get(target);

			if (r.getRegion().isPresent()) {
				Region region = r.getRegion().get();

				RegionPVPEvent e = new RegionPVPEvent(p, target, region);
				Bukkit.getPluginManager().callEvent(e);

				if (e.isCancelled()) {
					event.setCancelled(true);
					return;
				}

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

			Region.Resident r = Region.Resident.get(target);

			if (r.getRegion().isPresent()) {
				Region region = r.getRegion().get();

				RegionPVPEvent e = new RegionPVPEvent(p, target, region);
				Bukkit.getPluginManager().callEvent(e);

				if (e.isCancelled()) {
					event.setCancelled(true);
					return;
				}

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
