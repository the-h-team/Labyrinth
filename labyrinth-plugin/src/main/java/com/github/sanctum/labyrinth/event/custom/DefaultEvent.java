package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.data.Region;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Monster;
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
import org.bukkit.inventory.ItemStack;

public class DefaultEvent extends Vent {

	public DefaultEvent() {
	}

	public DefaultEvent(boolean isAsync) {
		super(isAsync, 420);
	}

	@Override
	public String getName() {
		return "Un-labeled";
	}

	public static class Player extends DefaultEvent {

		private final org.bukkit.entity.Player player;

		public Player(org.bukkit.entity.Player p, boolean isAsync) {
			super(isAsync);
			this.player = p;
		}

		public org.bukkit.entity.Player getPlayer() {
			return this.player;
		}

		@Override
		public String getName() {
			return "Player";
		}
	}

	public static class Join extends Player {

		private String kickMessage = ChatColor.RED + "You were blocked from connecting.";

		public Join(org.bukkit.entity.Player p) {
			super(p, false);
		}

		public String getKickMessage() {
			return kickMessage;
		}

		public void setKickMessage(String kickMessage) {
			this.kickMessage = kickMessage;
		}
	}

	public static class BlockBreak extends Player {

		private final Block block;

		public BlockBreak(org.bukkit.entity.Player player, Block block) {
			super(player, false);
			this.block = block;
		}

		public Block getBlock() {
			return block;
		}

		@Override
		public String getName() {
			return "BlockBreak";
		}
	}

	public static class BlockPlace extends Player {

		private final Block block;

		public BlockPlace(org.bukkit.entity.Player player, Block block) {
			super(player, false);
			this.block = block;
		}

		public Block getBlock() {
			return block;
		}

		@Override
		public String getName() {
			return "BlockPlace";
		}
	}

	public static class Interact extends Player {

		private Event.Result result;

		private final ItemStack item;

		private final Block block;

		private final Action action;

		public Interact(Action action, Event.Result result, Block clicked, ItemStack hand, org.bukkit.entity.Player p) {
			super(p, false);
			this.action = action;
			this.block = clicked;
			this.item = hand;
			this.result = result;
		}

		public Action getAction() {
			return action;
		}

		public Optional<Block> getBlock() {
			return Optional.ofNullable(this.block);
		}

		public ItemStack getItem() {
			return this.item;
		}

		public void setResult(Event.Result result) {
			this.result = result;
		}

		public Event.Result getResult() {
			return result;
		}
	}

	public static class PlayerDamagePlayer extends Player {

		private final org.bukkit.entity.Player victim;

		private final boolean physical;

		public PlayerDamagePlayer(org.bukkit.entity.Player attacker, org.bukkit.entity.Player victim, boolean isPhysical) {
			super(attacker, false);
			this.victim = victim;
			this.physical = isPhysical;
		}

		public boolean isPhysical() {
			return physical;
		}

		public org.bukkit.entity.Player getVictim() {
			return victim;
		}
	}

	public static class Controller implements Listener {

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onBuild(BlockPlaceEvent e) {
			BlockPlace b = new Call<>(Runtime.Synchronous, new BlockPlace(e.getPlayer(), e.getBlock())).run();

			if (b.isCancelled()) {
				e.setCancelled(true);
			}
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onBuild(BlockBreakEvent e) {

			BlockBreak b = new Call<>(Runtime.Synchronous, new BlockBreak(e.getPlayer(), e.getBlock())).run();

			if (b.isCancelled()) {
				e.setCancelled(true);
			}

		}

		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onFirstJoin(PlayerJoinEvent event) {

			Join e = new Call<>(Runtime.Synchronous, new Join(event.getPlayer())).run();

			if (e.isCancelled()) {
				event.getPlayer().kickPlayer(e.getKickMessage());
			}

		}

		@EventHandler
		public void onInteract(PlayerInteractEvent e) {

			Interact i = new Call<>(Runtime.Synchronous, new Interact(e.getAction(), e.useInteractedBlock(), e.getClickedBlock(), e.getItem(), e.getPlayer())).run();

			if (e.useInteractedBlock() != i.getResult()) {
				e.setUseInteractedBlock(i.getResult());
			}


		}

		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onPlayerHit(EntityDamageByEntityEvent event) {
			if (event.getEntity() instanceof org.bukkit.entity.Player && event.getDamager() instanceof org.bukkit.entity.Player) {
				org.bukkit.entity.Player target = (org.bukkit.entity.Player) event.getEntity();
				org.bukkit.entity.Player p = (org.bukkit.entity.Player) event.getDamager();

				PlayerDamagePlayer e = new Call<>(Runtime.Synchronous, new PlayerDamagePlayer(p, target, true)).run();

				if (e.isCancelled()) {
					event.setCancelled(true);
				}

			}

			if (event.getEntity() instanceof org.bukkit.entity.Player && event.getDamager() instanceof Projectile && (
					(Projectile) event.getDamager()).getShooter() instanceof org.bukkit.entity.Player) {
				Projectile pr = (Projectile) event.getDamager();
				org.bukkit.entity.Player p = (org.bukkit.entity.Player) pr.getShooter();
				org.bukkit.entity.Player target = (org.bukkit.entity.Player) event.getEntity();

				PlayerDamagePlayer e = new Call<>(Runtime.Synchronous, new PlayerDamagePlayer(p, target, false)).run();

				if (e.isCancelled()) {
					event.setCancelled(true);
				}

			}

			if (event.getEntity() instanceof org.bukkit.entity.Player && event.getDamager() instanceof Monster) {
				org.bukkit.entity.Player target = (org.bukkit.entity.Player) event.getEntity();
				if (Region.Resident.get(target).isSpawnTagged()) {
					event.setCancelled(true);
				}
			}
			if (event.getEntity() instanceof org.bukkit.entity.Player && event.getDamager() instanceof Projectile && (
					(Projectile) event.getDamager()).getShooter() instanceof Monster) {
				org.bukkit.entity.Player target = (org.bukkit.entity.Player) event.getEntity();
				if (Region.Resident.get(target).isSpawnTagged()) {
					event.setCancelled(true);
				}
			}
		}


	}
}
