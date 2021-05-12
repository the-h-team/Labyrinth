package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.Message;
import java.util.Optional;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class RegionFlag extends Cuboid.Flag {


	public static final Cuboid.Flag BUILD = new Cuboid.Flag(Labyrinth.getInstance().getName(), "build", "&4You can't do this here!") {

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onBuild(BlockPlaceEvent e) {

			Optional<Region> r = Region.match(e.getBlock().getLocation());
			if (r.isPresent()) {

				Region region = r.get();

				if (region.hasFlag(getId())) {
					Cuboid.Flag f = region.getFlag(getId()).get();
					if (f.isValid()) {
						if (!f.isAllowed()) {
							Message.form(e.getPlayer()).send(getMessage());
							e.setCancelled(true);
						}
					}
				}

			}
		}

	};

	public static final Cuboid.Flag BREAK = new Cuboid.Flag(Labyrinth.getInstance().getName(), "break", "&4You can't do this here!") {

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onBuild(BlockBreakEvent e) {

			Optional<Region> r = Region.match(e.getBlock().getLocation());

			if (r.isPresent()) {

				Region region = r.get();

				if (region.hasFlag(getId())) {
					Cuboid.Flag f = region.getFlag(getId()).get();
					if (f.isValid()) {
						if (!f.isAllowed()) {
							Message.form(e.getPlayer()).send(getMessage());
							e.setCancelled(true);
						} else {
							e.getPlayer().sendMessage(":D");
						}
					} else {
						e.getPlayer().sendMessage(":D s");
					}
				} else {
					e.getPlayer().sendMessage(":D");
				}
			}
		}

	};

	public static final Cuboid.Flag PVP = new Cuboid.Flag(Labyrinth.getInstance().getName(), "pvp", "&4You can't do this here!") {

		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onPlayerHit(EntityDamageByEntityEvent event) {
			if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
				Player target = (Player) event.getEntity();
				Player p = (Player) event.getDamager();

				Message msg = Message.form(p);

				Region.Resident r = Region.Resident.get(target);

				if (r.getRegion().isPresent()) {
					Region region = r.getRegion().get();

					if (region.hasFlag(getId())) {
						Cuboid.Flag f = region.getFlag(getId()).get();
						if (f.isValid()) {
							if (!f.isAllowed()) {
								msg.send(getMessage());
								event.setCancelled(true);
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

					if (region.hasFlag(getId())) {
						Cuboid.Flag f = region.getFlag(getId()).get();
						if (f.isValid()) {
							if (!f.isAllowed()) {
								msg.send(getMessage());
								event.setCancelled(true);
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

	};


	public RegionFlag(Cuboid.Flag flag) {
		super(flag);
	}

	public RegionFlag(String plugin, String id, String message) {
		super(plugin, id, message);
	}

}
