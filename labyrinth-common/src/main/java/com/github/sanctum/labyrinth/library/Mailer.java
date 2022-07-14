package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.ComponentChunk;
import com.github.sanctum.labyrinth.formatting.TextChunk;
import com.github.sanctum.panther.util.Check;
import com.github.sanctum.panther.util.HUID;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object responsible for sending messages to command senders
 */
public class Mailer {

	private CommandSender sender;
	private final MailerPrefix prefix;
	private Plugin plugin = LabyrinthProvider.getInstance().getPluginInstance();

	public Mailer() {
		this.prefix = new MailerPrefix(this);
	}

	public Mailer(@NotNull CommandSender sender) {
		this.sender = sender;
		this.prefix = new MailerPrefix(this);
	}

	public Mailer(@NotNull Plugin plugin) {
		this.plugin = plugin;
		this.prefix = new MailerPrefix(this);
	}

	public static Mailer empty() {
		return new Mailer();
	}

	public static Mailer empty(CommandSender sender) {
		return new Mailer(sender);
	}

	public static Mailer empty(Plugin plugin) {
		return new Mailer(plugin);
	}

	/**
	 * Who's this mailer for? Tell us here:
	 *
	 * @param user The user to message.
	 * @return The same mailer.
	 */
	public Mailer accept(@NotNull CommandSender user) {
		this.sender = user;
		return this;
	}

	/**
	 * Who's this mailer for? Tell us here:
	 *
	 * @param plugin The user to message.
	 * @return The same mailer.
	 */
	public Mailer accept(@NotNull Plugin plugin) {
		this.plugin = plugin;
		return this;
	}

	/**
	 * @return The prefix for this mailer ready for customization.
	 */
	public MailerPrefix prefix() {
		return this.prefix;
	}

	/**
	 * Send a message to the configured command sender {@linkplain Mailer#accept(CommandSender)}
	 * 
	 * @param text The message to mail to the sender.
	 * @return A mailer deployable.
	 */
	public Deployable<Mailer> chat(@NotNull String text) {
		if (!this.prefix.isEmpty()) {
			return new Mailable(this.sender, MailType.CHAT, prefix.join() + " " + text);
		}
		return new Mailable(this.sender, MailType.CHAT, text);
	}

	/**
	 * Send a message to the registered command sender {@linkplain Mailer#accept(CommandSender)}
	 *
	 * @param components The message components to mail to the sender.
	 * @return A mailer deployable.
	 */
	public Deployable<Mailer> chat(@NotNull BaseComponent... components) {
		if (!this.prefix.isEmpty()) {
			BaseComponent[] comps = new FancyMessage().append(new TextChunk(this.prefix.join())).append(new TextChunk(" ")).append(new ComponentChunk(components)).build();
			return new Mailable(this.sender, MailType.CHAT, comps);
		}
		return new Mailable(this.sender, MailType.CHAT, components);
	}

	/**
	 * Send an action bar message to the registered command sender {@linkplain Mailer#accept(CommandSender)}
	 *
	 * NOTE: Sender must be a {@linkplain Player}
	 *
	 * @param text The message to mail to the sender.
	 * @return A mailer deployable.
	 */
	public Deployable<Mailer> action(@NotNull String text) {
		if (!this.prefix.isEmpty()) {
			return new Mailable(this.sender, MailType.ACTION, prefix.join() + " " + text);
		}
		return new Mailable(this.sender, MailType.ACTION, text);
	}

	/**
	 * Send an action bar message to the registered command sender {@linkplain Mailer#accept(CommandSender)}
	 *
	 * NOTE: Sender must be a {@linkplain Player}
	 *
	 * @param components The message components to mail to the sender.
	 * @return A mailer deployable.
	 */
	public Deployable<Mailer> action(@NotNull BaseComponent... components) {
		if (!this.prefix.isEmpty()) {
			BaseComponent[] comps = new FancyMessage().append(new TextChunk(this.prefix.join())).append(new TextChunk(" ")).append(new ComponentChunk(components)).build();
			return new Mailable(this.sender, MailType.ACTION, comps);
		}
		return new Mailable(this.sender, MailType.ACTION, components);
	}

	/**
	 * Send a title to the registered command sender {@linkplain Mailer#accept(CommandSender)}
	 *
	 * @param title The initial title
	 * @param subtitle The sub title.
	 * @return A mailer deployable.
	 */
	public Deployable<Mailer> title(final @NotNull String title, final @Nullable String subtitle) {
		return new Mailable(this.sender, MailType.TITLE, new MailableContext<String>() {
			@Override
			public String getString() {
				return title;
			}

			@Override
			public String getAttachment() {
				return subtitle;
			}
		});
	}

	/**
	 * Announce a text message to specific players online.
	 *
	 * @param predicate The player based predicate.
	 * @param message The message to send.
	 * @return A mailer deployable.
	 */
	public Deployable<Mailer> announce(final @NotNull Predicate<Player> predicate, @NotNull String message) {
		final String target = !this.prefix.isEmpty() ? this.prefix.join() + " " + message : message;
		return new Mailable(Bukkit.getConsoleSender(), MailType.BROADCAST, new MailableContext<Predicate<Player>>() {
			@Override
			public String getString() {
				return target;
			}

			@Override
			public Predicate<Player> getAttachment() {
				return predicate;
			}
		});
	}

	/**
	 * Announce message components to specific players online.
	 *
	 * @param predicate The player based predicate.
	 * @param components The message components to send.
	 * @return A mailer deployable.
	 */
	public Deployable<Mailer> announce(final @NotNull Predicate<Player> predicate, @NotNull BaseComponent... components) {
		if (!this.prefix.isEmpty()) {
			components = new FancyMessage().append(new TextChunk(this.prefix.join())).append(new TextChunk(" ")).append(new ComponentChunk(components)).build();
		}
		@NotNull BaseComponent[] finalComponents = components;
		return new Mailable(Bukkit.getConsoleSender(), MailType.BROADCAST, new MailableContext<Predicate<Player>>() {
			@Override
			public String getString() {
				return new ComponentChunk(finalComponents).toJson();
			}

			@Override
			public Predicate<Player> getAttachment() {
				return predicate;
			}
		});
	}

	public Deployable<Mailer> info(@NotNull String text) {
		return new Mailable(this.plugin, Level.INFO, text);
	}

	public Deployable<Mailer> warn(@NotNull String text) {
		return new Mailable(this.plugin, Level.WARNING, text);
	}

	public Deployable<Mailer> error(@NotNull String text) {
		return new Mailable(this.plugin, Level.SEVERE, text);
	}


	class Mailable implements Deployable<Mailer> {

		private final MailerResult result;
		private String text;
		private Level level;
		private BaseComponent[] components;
		private MailableContext<String> title;
		private MailableContext<Predicate<Player>> predicate;
		private MailType type;

		Mailable(CommandSender player, MailType type, MailableContext<?> context) {
			if (context.getAttachment() instanceof String) {
				this.title = (MailableContext<String>) context;
			} else {
				this.predicate = (MailableContext<Predicate<Player>>) context;
			}
			this.result = new MailerResult(player);
			this.type = type;
		}

		Mailable(CommandSender player, MailType type, BaseComponent... components) {
			this.result = new MailerResult(player);
			this.type = type;
			this.components = components;
		}

		Mailable(CommandSender player, MailType type, String text) {
			this.result = new MailerResult(player);
			this.type = type;
			this.text = text;
		}

		Mailable(Plugin plugin, Level level, String text) {
			this.result = new MailerResult(plugin);
			this.level = level;
			this.text = text;
		}

		@Override
		public Deployable<Mailer> deploy() {
			queue(0);
			return this;
		}

		@Override
		public Deployable<Mailer> deploy(Consumer<? super Mailer> consumer) {
			deploy();
			consumer.accept(Mailer.this);
			return this;
		}

		@Override
		public Deployable<Mailer> queue() {
			queue(1);
			return this;
		}

		@Override
		public Deployable<Mailer> queue(Consumer<? super Mailer> consumer, long timeout) {
			if (result.isForPlayer()) {
				if (timeout <= 0) {
					switch (type) {
						case ACTION:
							if (text != null) {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate((Player) toSender(result.getSource()))));
							} else {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, new FancyMessage().append(new ComponentChunk(components)).build());
							}
							consumer.accept(Mailer.this);
							break;
						case CHAT:
							if (components != null) {
								((Player)toSender(result.getSource())).spigot().sendMessage(components);
							} else {
								toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate((Player) toSender(result.getSource())));
							}

							consumer.accept(Mailer.this);
							break;
						case BROADCAST:
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (predicate.getAttachment().test(online)) {
									if (Check.isJson(predicate.getString())) {
										BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
										online.spigot().sendMessage(base);
									} else {
										online.sendMessage(StringUtils.use(predicate.getString()).translate((Player) toSender(result.getSource())));
									}
								}
							}
							consumer.accept(Mailer.this);
							break;
						case TITLE:
							Player player = ((Player) toSender(result.getSource()));
							if (this.title.getAttachment() != null) {
								player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), StringUtils.use(this.title.getAttachment()).translate((Player) toSender(result.getSource())), 60, 60, 60);
							} else {
								player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), "", 60, 60, 60);
							}
							consumer.accept(Mailer.this);
							break;
					}
				} else {
					switch (type) {
						case ACTION:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (text != null) {
									((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate((Player) toSender(result.getSource()))));
								} else {
									((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, new FancyMessage().append(new ComponentChunk(components)).build());
								}
								consumer.accept(Mailer.this);
							}, HUID.randomID().toString(), timeout);
						case CHAT:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (components != null) {
									((Player)toSender(result.getSource())).spigot().sendMessage(components);
								} else {
									toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate((Player) toSender(result.getSource())));
								}
								consumer.accept(Mailer.this);
							}, HUID.randomID().toString(), timeout);
							break;
						case BROADCAST:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								for (Player online : Bukkit.getOnlinePlayers()) {
									if (predicate.getAttachment().test(online)) {
										if (Check.isJson(predicate.getString())) {
											BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
											online.spigot().sendMessage(base);
										} else {
											online.sendMessage(StringUtils.use(predicate.getString()).translate((Player) toSender(result.getSource())));
										}
									}
								}
								consumer.accept(Mailer.this);
							}, HUID.randomID().toString(), timeout);
							break;
						case TITLE:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								Player player = ((Player) toSender(result.getSource()));
								if (this.title.getAttachment() != null) {
									player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), StringUtils.use(this.title.getAttachment()).translate((Player) toSender(result.getSource())), 60, 60, 60);
								} else {
									player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), "", 60, 60, 60);
								}
								consumer.accept(Mailer.this);
							}, HUID.randomID().toString(), timeout);
							break;
					}
				}
			} else {
				Plugin plugin = toPlugin(result.getSource());
				if (timeout <= 0) {
					plugin.getLogger().log(this.level, this.text);
					consumer.accept(Mailer.this);
				} else {
					LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
						plugin.getLogger().log(this.level, this.text);
						consumer.accept(Mailer.this);
					}, HUID.randomID().toString(), timeout);
				}
			}
			return this;
		}

		@Override
		public Deployable<Mailer> queue(Consumer<? super Mailer> consumer, Date date) {
			if (result.isForPlayer()) {
				if (date == null) {
					switch (type) {
						case ACTION:
							if (text != null) {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate((Player) toSender(result.getSource()))));
							} else {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, new FancyMessage().append(new ComponentChunk(components)).build());
							}
							consumer.accept(Mailer.this);
							break;
						case CHAT:
							if (components != null) {
								((Player)toSender(result.getSource())).spigot().sendMessage(components);
							} else {
								toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate((Player) toSender(result.getSource())));
							}
							consumer.accept(Mailer.this);
							break;
						case BROADCAST:
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (predicate.getAttachment().test(online)) {
									if (Check.isJson(predicate.getString())) {
										BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
										online.spigot().sendMessage(base);
									} else {
										online.sendMessage(StringUtils.use(predicate.getString()).translate((Player) toSender(result.getSource())));
									}
								}
							}
							consumer.accept(Mailer.this);
							break;
						case TITLE:
							Player player = ((Player) toSender(result.getSource()));
							if (this.title.getAttachment() != null) {
								player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), StringUtils.use(this.title.getAttachment()).translate((Player) toSender(result.getSource())), 60, 60, 60);
							} else {
								player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), "", 60, 60, 60);
							}
							consumer.accept(Mailer.this);
							break;
					}
				} else {
					switch (type) {
						case ACTION:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (text != null) {
									((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate((Player) toSender(result.getSource()))));
								} else {
									((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, new FancyMessage().append(new ComponentChunk(components)).build());
								}
								consumer.accept(Mailer.this);
							}, HUID.randomID().toString(), date);
						case CHAT:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (components != null) {
									((Player)toSender(result.getSource())).spigot().sendMessage(components);
								} else {
									toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate((Player) toSender(result.getSource())));
								}
								consumer.accept(Mailer.this);
							}, HUID.randomID().toString(), date);
							break;
						case BROADCAST:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								for (Player online : Bukkit.getOnlinePlayers()) {
									if (predicate.getAttachment().test(online)) {
										if (Check.isJson(predicate.getString())) {
											BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
											online.spigot().sendMessage(base);
										} else {
											online.sendMessage(StringUtils.use(predicate.getString()).translate((Player) toSender(result.getSource())));
										}
									}
								}
								consumer.accept(Mailer.this);
							}, HUID.randomID().toString(), date);
							break;
						case TITLE:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								Player player = ((Player) toSender(result.getSource()));
								if (this.title.getAttachment() != null) {
									player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), StringUtils.use(this.title.getAttachment()).translate((Player) toSender(result.getSource())), 60, 60, 60);
								} else {
									player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), "", 60, 60, 60);
								}
								consumer.accept(Mailer.this);
							}, HUID.randomID().toString(), date);
							break;
					}
				}
			} else {
				Plugin plugin = toPlugin(result.getSource());
				if (date == null) {
					plugin.getLogger().log(this.level, this.text);
					consumer.accept(Mailer.this);
				} else {
					LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
						plugin.getLogger().log(this.level, this.text);
						consumer.accept(Mailer.this);
					}, HUID.randomID().toString(), date);
				}
			}
			return this;
		}

		@Override
		public <O> DeployableMapping<O> mapLabyrinth(Function<? super Mailer, ? extends O> mapper) {
			return new DeployableMapping<>(() -> Mailer.this, (Function<? super Object, ? extends O>) mapper);
		}

		@Override
		public Deployable<Mailer> queue(long timeout) {
			if (result.isForPlayer()) {
				if (timeout <= 0) {
					switch (type) {
						case ACTION:
							if (text != null) {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate((Player) toSender(result.getSource()))));
							} else {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, new FancyMessage().append(new ComponentChunk(components)).build());
							}
							break;
						case CHAT:
							if (components != null) {
								((Player)toSender(result.getSource())).spigot().sendMessage(components);
							} else {
								toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate((Player) toSender(result.getSource())));
							}
							break;
						case BROADCAST:
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (predicate.getAttachment().test(online)) {
									if (Check.isJson(predicate.getString())) {
										BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
										online.spigot().sendMessage(base);
									} else {
										online.sendMessage(StringUtils.use(predicate.getString()).translate((Player) toSender(result.getSource())));
									}
								}
							}
							break;
						case TITLE:
							Player player = ((Player) toSender(result.getSource()));
							if (this.title.getAttachment() != null) {
								player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), StringUtils.use(this.title.getAttachment()).translate((Player) toSender(result.getSource())), 60, 60, 60);
							} else {
								player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), "", 60, 60, 60);
							}
							break;
					}
				} else {
					switch (type) {
						case ACTION:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (text != null) {
									((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate((Player) toSender(result.getSource()))));
								} else {
									((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, new FancyMessage().append(new ComponentChunk(components)).build());
								}
							}, HUID.randomID().toString(), timeout);
							break;
						case CHAT:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (components != null) {
									if (toSender(result.getSource()) instanceof Player) {
										((Player) toSender(result.getSource())).spigot().sendMessage(components);
									}
								} else {
									toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate((Player) toSender(result.getSource())));
								}
							}, HUID.randomID().toString(), timeout);
							break;
						case BROADCAST:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								for (Player online : Bukkit.getOnlinePlayers()) {
									if (predicate.getAttachment().test(online)) {
										if (Check.isJson(predicate.getString())) {
											BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
											online.spigot().sendMessage(base);
										} else {
											online.sendMessage(StringUtils.use(predicate.getString()).translate((Player) toSender(result.getSource())));
										}
									}
								}
							}, HUID.randomID().toString(), timeout);
							break;
						case TITLE:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								Player player = ((Player) toSender(result.getSource()));
								if (this.title.getAttachment() != null) {
									player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), StringUtils.use(this.title.getAttachment()).translate((Player) toSender(result.getSource())), 60, 60, 60);
								} else {
									player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), "", 60, 60, 60);
								}
							}, HUID.randomID().toString(), timeout);
							break;
					}
				}
			} else {
				Plugin plugin = toPlugin(result.getSource());
				if (timeout <= 0) {
					plugin.getLogger().log(this.level, this.text);
				} else {
					LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> plugin.getLogger().log(this.level, this.text), HUID.randomID().toString(), timeout);
				}
			}
			return this;
		}

		@Override
		public Deployable<Mailer> queue(Date date) {
			if (result.isForPlayer()) {
				if (date == null) {
					switch (type) {
						case ACTION:
							if (text != null) {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate((Player) toSender(result.getSource()))));
							} else {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, new FancyMessage().append(new ComponentChunk(components)).build());
							}
							break;
						case CHAT:
							if (components != null) {
								((Player)toSender(result.getSource())).spigot().sendMessage(components);
							} else {
								toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate((Player) toSender(result.getSource())));
							}
							break;
						case BROADCAST:
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (predicate.getAttachment().test(online)) {
									if (Check.isJson(predicate.getString())) {
										BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
										online.spigot().sendMessage(base);
									} else {
										online.sendMessage(StringUtils.use(predicate.getString()).translate((Player) toSender(result.getSource())));
									}
								}
							}
							break;
						case TITLE:
							Player player = ((Player) toSender(result.getSource()));
							if (this.title.getAttachment() != null) {
								player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), StringUtils.use(this.title.getAttachment()).translate((Player) toSender(result.getSource())), 60, 60, 60);
							} else {
								player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), "", 60, 60, 60);
							}
							break;
					}
				} else {
					switch (type) {
						case ACTION:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (text != null) {
									((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate((Player) toSender(result.getSource()))));
								} else {
									((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, new FancyMessage().append(new ComponentChunk(components)).build());
								}
							}, HUID.randomID().toString(), date);
						case CHAT:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (components != null) {
									((Player)toSender(result.getSource())).spigot().sendMessage(components);
								} else {
									toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate((Player) toSender(result.getSource())));
								}
							}, HUID.randomID().toString(), date);
							break;
						case BROADCAST:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								for (Player online : Bukkit.getOnlinePlayers()) {
									if (predicate.getAttachment().test(online)) {
										online.sendMessage(StringUtils.use(predicate.getString()).translate((Player) toSender(result.getSource())));
									}
								}
							}, HUID.randomID().toString(), date);
							break;
						case TITLE:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								Player player = ((Player) toSender(result.getSource()));
								if (this.title.getAttachment() != null) {
									player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), StringUtils.use(this.title.getAttachment()).translate((Player) toSender(result.getSource())), 60, 60, 60);
								} else {
									player.sendTitle(StringUtils.use(this.title.getString()).translate((Player) toSender(result.getSource())), "", 60, 60, 60);
								}
							}, HUID.randomID().toString(), date);
							break;
					}
				}
			} else {
				Plugin plugin = toPlugin(result.getSource());
				if (date == null) {
					plugin.getLogger().log(this.level, this.text);
				} else {
					LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> plugin.getLogger().log(this.level, this.text), HUID.randomID().toString(), date);
				}
			}
			return this;
		}

		@Override
		public CompletableFuture<Mailer> submit() {
			return CompletableFuture.supplyAsync(() -> Mailer.this);
		}

		CommandSender toSender(Object o) {
			if (CommandSender.class.isAssignableFrom(o.getClass())) {
				return (CommandSender) o;
			}
			return null;
		}

		Plugin toPlugin(Object o) {
			if (Plugin.class.isAssignableFrom(o.getClass())) {
				return (Plugin) o;
			}
			return null;
		}

	}


}
