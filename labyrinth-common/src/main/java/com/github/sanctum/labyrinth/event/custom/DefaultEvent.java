package com.github.sanctum.labyrinth.event.custom;

import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.data.service.LabyrinthOptions;
import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import com.github.sanctum.labyrinth.library.AFK;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.ListUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;
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

	public static class Leave extends Player {

		public Leave(org.bukkit.entity.Player p) {
			super(p, false);
			setState(CancelState.OFF);
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

	public static class Communication extends Player {
		private final Type type;
		private final CommunicationResult<?> result;

		public Communication(org.bukkit.entity.Player p, Type type, CommunicationResult<?> communication) {
			super(p, true);
			this.type = type;
			this.result = communication;
		}

		public Optional<ChatCommand> getCommand() {
			return this.result != null && this.result.get().getClass().isAssignableFrom(String[].class) ?
					Optional.ofNullable((ChatCommand) this.result) :
					Optional.empty();
		}

		public Optional<ChatMessage> getMessage() {
			return this.result != null && this.result.get().getClass().isAssignableFrom(BaseComponent[].class) ?
					Optional.ofNullable((ChatMessage) this.result) :
					Optional.empty();
		}

		public void setCommand(String command) {
			ChatCommand impl = getCommand().get();
			impl.setArgs(command);
		}

		public void setMessage(BaseComponent[] message) {
			ChatMessage impl = getMessage().get();
			impl.set(message);
		}

		public Type getCommunicationType() {
			return this.type != null ? this.type : Type.UNKNOWN;
		}

		public static abstract class CommunicationResult<T> {

			private final Set<? extends org.bukkit.entity.Player> recipients;

			public CommunicationResult(Set<? extends org.bukkit.entity.Player> recipients) {
				this.recipients = recipients;
			}

			public abstract T get();

			public String getText() {
				return "null";
			}

			public Set<? extends org.bukkit.entity.Player> getRecipients() {
				if (this.recipients != null) {
					return this.recipients;
				} else {
					return new HashSet<>();
				}
			}

		}

		public static final class ChatMessage extends CommunicationResult<BaseComponent[]> {

			private BaseComponent[] args;

			private boolean changed;

			private final String text;

			private String format;

			public ChatMessage(Collection<? extends org.bukkit.entity.Player> recipients, String text, String format) {
				super(new HashSet<>(recipients));
				List<BaseComponent> components = new LinkedList<>();
				for (String s : format.split(" ")) {
					components.add(new TextComponent(s));
				}
				this.args = ListUtils.use(components).append(b -> {
					b.addExtra(" ");
				}).toArray(new BaseComponent[0]);
				this.text = text;
				this.format = format;
			}

			@Override
			public BaseComponent[] get() {
				return this.args;
			}

			@Override
			public String getText() {
				return this.text;
			}

			public BaseComponent getMessage() {
				return new ColoredString(this.text, ColoredString.ColorType.MC_COMPONENT).toComponent();
			}

			public void set(BaseComponent[] components) {
				this.changed = true;
				this.args = components;
			}

			public boolean isChanged() {
				return changed;
			}

			protected String getFormat() {
				return this.format;
			}

			protected void setFormat(String format) {
				this.format = format;
			}

		}

		public static final class ChatCommand extends CommunicationResult<String[]> {

			private String[] args;

			private boolean changed;

			private String label;

			public ChatCommand(String[] args, Collection<? extends org.bukkit.entity.Player> recipients) {
				super(recipients != null ? new HashSet<>(recipients) : null);
				this.args = args;
				this.label = args[0];
			}

			@Override
			public String[] get() {
				return args;
			}

			@Override
			public String getText() {
				return this.label.replace("/", "");
			}

			public boolean isChanged() {
				return changed;
			}

			public void setArgs(String text) {
				this.args = text.split(" ");
				this.changed = true;
				this.label = args[0];
			}

		}

		public enum Type {
			/**
			 * A representation of a chat command.
			 */
			COMMAND,
			/**
			 * A representation of a chat message.
			 */
			CHAT,
			/**
			 * An unknown communication type.
			 */
			UNKNOWN;
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
		public void onChat(AsyncPlayerChatEvent e) {

			Communication c = new Call<>(Runtime.Asynchronous, new Communication(e.getPlayer(), Communication.Type.CHAT, new Communication.ChatMessage(Bukkit.getOnlinePlayers(), e.getMessage(), e.getFormat().replace("<%1$s>", "").replace("%2$s", "")))).complete().join();

			if (c.isCancelled()) {
				e.setCancelled(true);
			}

			Communication.ChatMessage impl = c.getMessage().orElse(null);

			if (impl != null) {
				if (impl.isChanged()) {
					String s = impl.getFormat();
					e.getRecipients().clear();
					e.setMessage(ListUtils.use(impl.args).join(b -> b.stream().map(bc -> bc.toLegacyText()).collect(Collectors.toList()).toString()));
					for (org.bukkit.entity.Player p : impl.getRecipients()) {
						p.spigot().sendMessage(impl.get());
					}
					if (s == null || s.isEmpty()) return;
					if (!s.equals(e.getFormat())) {
						e.setFormat(s);
					}
				}
			}


		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onChat(PlayerCommandPreprocessEvent e) {

			Communication c = new Call<>(Runtime.Asynchronous, new Communication(e.getPlayer(), Communication.Type.COMMAND, new Communication.ChatCommand(e.getMessage().split(" "), Bukkit.getOnlinePlayers()))).complete().join();

			if (c.isCancelled()) {
				e.setCancelled(true);
			}

			if (c.getCommand().get().isChanged()) {
				e.setMessage(StringUtils.join(c.getCommand().get().args, " "));
			}

		}

		@EventHandler
		public void onCommandHide(PlayerCommandSendEvent e) {
			CommandUtils.getVisibilityCalculations().forEach(calculation -> {
				String test = calculation.accept(e.getPlayer());
				if (test != null) e.getCommands().remove(test);
			});
		}

		@EventHandler
		public void onTabInsert(TabCompleteEvent e) {

		}

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
		public void onJoin(PlayerJoinEvent event) {

			Join e = new Call<>(Runtime.Synchronous, new Join(event.getPlayer())).run();


			if (e.isCancelled()) {
				event.getPlayer().kickPlayer(e.getKickMessage());
				return;
			}

			if (LabyrinthOptions.IMPL_AFK.enabled()) {

				AFK.supply(e.getPlayer());

			}

			OrdinalProcedure.select(LabyrinthUser.get(event.getPlayer().getName()), 4, event.getPlayer());

		}

		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onLeave(PlayerQuitEvent event) {

			new Call<>(Runtime.Synchronous, new Leave(event.getPlayer())).run();

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
		}


	}
}
