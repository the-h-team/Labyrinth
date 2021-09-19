package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.service.Check;
import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.ComponentSection;
import com.github.sanctum.labyrinth.formatting.TextSection;
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

	public Mailer accept(@NotNull CommandSender user) {
		this.sender = user;
		return this;
	}

	public Mailer accept(@NotNull Plugin plugin) {
		this.plugin = plugin;
		return this;
	}

	public MailerPrefix prefix() {
		return this.prefix;
	}

	public Deployable<Mailer> chat(@NotNull String text) {
		if (!this.prefix.isEmpty()) {
			return new Mailable(this, this.sender, MailType.CHAT, prefix.join() + " " + text);
		}
		return new Mailable(this, this.sender, MailType.CHAT, text);
	}

	public Deployable<Mailer> chat(@NotNull BaseComponent... components) {
		if (!this.prefix.isEmpty()) {
			BaseComponent[] comps = new FancyMessage().append(new TextSection(this.prefix.join())).append(new TextSection(" ")).append(new ComponentSection(components)).build();
			return new Mailable(this, this.sender, MailType.CHAT, comps);
		}
		return new Mailable(this, this.sender, MailType.CHAT, components);
	}

	public Deployable<Mailer> action(@NotNull String text) {
		if (!this.prefix.isEmpty()) {
			return new Mailable(this, this.sender, MailType.ACTION, prefix + " " + text);
		}
		return new Mailable(this, this.sender, MailType.ACTION, text);
	}

	public Deployable<Mailer> action(@NotNull BaseComponent... components) {
		if (!this.prefix.isEmpty()) {
			BaseComponent[] comps = new FancyMessage().append(new TextSection(this.prefix.join())).append(new TextSection(" ")).append(new ComponentSection(components)).build();
			return new Mailable(this, this.sender, MailType.ACTION, comps);
		}
		return new Mailable(this, this.sender, MailType.ACTION, components);
	}

	public Deployable<Mailer> title(final @NotNull String title, final @Nullable String subtitle) {
		return new Mailable(this, this.sender, MailType.TITLE, new MailableContext<String>() {
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

	public Deployable<Mailer> announce(final @NotNull Predicate<Player> predicate, @NotNull String message) {
		final String target = !this.prefix.isEmpty() ? this.prefix.join() + " " + message : message;
		return new Mailable(this, Bukkit.getConsoleSender(), MailType.BROADCAST, new MailableContext<Predicate<Player>>() {
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

	public Deployable<Mailer> announce(final @NotNull Predicate<Player> predicate, @NotNull BaseComponent... components) {
		if (!this.prefix.isEmpty()) {
			components = new FancyMessage().append(new TextSection(this.prefix.join())).append(new TextSection(" ")).append(new ComponentSection(components)).build();
		}
		@NotNull BaseComponent[] finalComponents = components;
		return new Mailable(this, Bukkit.getConsoleSender(), MailType.BROADCAST, new MailableContext<Predicate<Player>>() {
			@Override
			public String getString() {
				return new ComponentSection(finalComponents).toJson();
			}

			@Override
			public Predicate<Player> getAttachment() {
				return predicate;
			}
		});
	}

	public Deployable<Mailer> info(@NotNull String text) {
		return new Mailable(this, this.plugin, Level.INFO, text);
	}

	public Deployable<Mailer> warn(@NotNull String text) {
		return new Mailable(this, this.plugin, Level.WARNING, text);
	}

	public Deployable<Mailer> error(@NotNull String text) {
		return new Mailable(this, this.plugin, Level.SEVERE, text);
	}


	static class Mailable implements Deployable<Mailer> {

		private final Mailer parent;
		private final MailerResult result;
		private String text;
		private Level level;
		private BaseComponent[] components;
		private MailableContext<String> title;
		private MailableContext<Predicate<Player>> predicate;
		private MailType type;

		public Mailable(Mailer parent, CommandSender player, MailType type, MailableContext<?> context) {
			this.parent = parent;
			if (context.getAttachment() instanceof String) {
				this.title = (MailableContext<String>) context;
			} else {
				this.predicate = (MailableContext<Predicate<Player>>) context;
			}
			this.result = new MailerResult(player);
			this.type = type;
		}

		public Mailable(Mailer parent, CommandSender player, MailType type, BaseComponent... components) {
			this.parent = parent;
			this.result = new MailerResult(player);
			this.type = type;
			this.components = components;
		}

		public Mailable(Mailer parent, CommandSender player, MailType type, String text) {
			this.parent = parent;
			this.result = new MailerResult(player);
			this.type = type;
			this.text = text;
		}

		public Mailable(Mailer parent, Plugin plugin, Level level, String text) {
			this.parent = parent;
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
			consumer.accept(parent);
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
							((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate()));
							consumer.accept(parent);
							break;
						case CHAT:
							if (components != null) {
								((Player)toSender(result.getSource())).spigot().sendMessage(components);
							} else {
								toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate());
							}

							consumer.accept(parent);
							break;
						case BROADCAST:
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (predicate.getAttachment().test(online)) {
									if (Check.isJson(predicate.getString())) {
										BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
										online.spigot().sendMessage(base);
									} else {
										online.sendMessage(StringUtils.use(predicate.getString()).translate());
									}
								}
							}
							consumer.accept(parent);
							break;
						case TITLE:
							Player player = ((Player) toSender(result.getSource()));
							if (this.title.getAttachment() != null) {
								player.sendTitle(StringUtils.use(this.title.getString()).translate(), StringUtils.use(this.title.getAttachment()).translate(), 60, 60, 60);
							} else {
								player.sendTitle(StringUtils.use(this.title.getString()).translate(), "", 60, 60, 60);
							}
							consumer.accept(parent);
							break;
					}
				} else {
					switch (type) {
						case ACTION:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate()));
								consumer.accept(this.parent);
							}, HUID.randomID().toString(), timeout);
						case CHAT:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (components != null) {
									((Player)toSender(result.getSource())).spigot().sendMessage(components);
								} else {
									toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate());
								}
								consumer.accept(this.parent);
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
											online.sendMessage(StringUtils.use(predicate.getString()).translate());
										}
									}
								}
								consumer.accept(this.parent);
							}, HUID.randomID().toString(), timeout);
							break;
						case TITLE:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								Player player = ((Player) toSender(result.getSource()));
								if (this.title.getAttachment() != null) {
									player.sendTitle(StringUtils.use(this.title.getString()).translate(), StringUtils.use(this.title.getAttachment()).translate(), 60, 60, 60);
								} else {
									player.sendTitle(StringUtils.use(this.title.getString()).translate(), "", 60, 60, 60);
								}
								consumer.accept(this.parent);
							}, HUID.randomID().toString(), timeout);
							break;
					}
				}
			} else {
				Plugin plugin = toPlugin(result.getSource());
				if (timeout <= 0) {
					plugin.getLogger().log(this.level, this.text);
					consumer.accept(this.parent);
				} else {
					LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
						plugin.getLogger().log(this.level, this.text);
						consumer.accept(this.parent);
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
							((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate()));
							consumer.accept(parent);
							break;
						case CHAT:
							if (components != null) {
								((Player)toSender(result.getSource())).spigot().sendMessage(components);
							} else {
								toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate());
							}
							consumer.accept(parent);
							break;
						case BROADCAST:
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (predicate.getAttachment().test(online)) {
									if (Check.isJson(predicate.getString())) {
										BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
										online.spigot().sendMessage(base);
									} else {
										online.sendMessage(StringUtils.use(predicate.getString()).translate());
									}
								}
							}
							consumer.accept(parent);
							break;
						case TITLE:
							Player player = ((Player) toSender(result.getSource()));
							if (this.title.getAttachment() != null) {
								player.sendTitle(StringUtils.use(this.title.getString()).translate(), StringUtils.use(this.title.getAttachment()).translate(), 60, 60, 60);
							} else {
								player.sendTitle(StringUtils.use(this.title.getString()).translate(), "", 60, 60, 60);
							}
							consumer.accept(parent);
							break;
					}
				} else {
					switch (type) {
						case ACTION:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate()));
								consumer.accept(this.parent);
							}, HUID.randomID().toString(), date);
						case CHAT:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (components != null) {
									((Player)toSender(result.getSource())).spigot().sendMessage(components);
								} else {
									toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate());
								}
								consumer.accept(this.parent);
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
											online.sendMessage(StringUtils.use(predicate.getString()).translate());
										}
									}
								}
								consumer.accept(this.parent);
							}, HUID.randomID().toString(), date);
							break;
						case TITLE:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								Player player = ((Player) toSender(result.getSource()));
								if (this.title.getAttachment() != null) {
									player.sendTitle(StringUtils.use(this.title.getString()).translate(), StringUtils.use(this.title.getAttachment()).translate(), 60, 60, 60);
								} else {
									player.sendTitle(StringUtils.use(this.title.getString()).translate(), "", 60, 60, 60);
								}
								consumer.accept(this.parent);
							}, HUID.randomID().toString(), date);
							break;
					}
				}
			} else {
				Plugin plugin = toPlugin(result.getSource());
				if (date == null) {
					plugin.getLogger().log(this.level, this.text);
					consumer.accept(this.parent);
				} else {
					LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
						plugin.getLogger().log(this.level, this.text);
						consumer.accept(this.parent);
					}, HUID.randomID().toString(), date);
				}
			}
			return this;
		}

		@Override
		public <O> DeployableMapping<O> map(Function<? super Mailer, ? extends O> mapper) {
			return new DeployableMapping<>(this.parent, (Function<? super Object, ? extends O>) mapper);
		}

		@Override
		public Deployable<Mailer> queue(long timeout) {
			if (result.isForPlayer()) {
				if (timeout <= 0) {
					switch (type) {
						case ACTION:
							((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate()));
							break;
						case CHAT:
							if (components != null) {
								((Player)toSender(result.getSource())).spigot().sendMessage(components);
							} else {
								toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate());
							}
							break;
						case BROADCAST:
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (predicate.getAttachment().test(online)) {
									if (Check.isJson(predicate.getString())) {
										BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
										online.spigot().sendMessage(base);
									} else {
										online.sendMessage(StringUtils.use(predicate.getString()).translate());
									}
								}
							}
							break;
						case TITLE:
							Player player = ((Player) toSender(result.getSource()));
							if (this.title.getAttachment() != null) {
								player.sendTitle(StringUtils.use(this.title.getString()).translate(), StringUtils.use(this.title.getAttachment()).translate(), 60, 60, 60);
							} else {
								player.sendTitle(StringUtils.use(this.title.getString()).translate(), "", 60, 60, 60);
							}
							break;
					}
				} else {
					switch (type) {
						case ACTION:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> ((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate())), HUID.randomID().toString(), timeout);
						case CHAT:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (components != null) {
									((Player)toSender(result.getSource())).spigot().sendMessage(components);
								} else {
									toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate());
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
											online.sendMessage(StringUtils.use(predicate.getString()).translate());
										}
									}
								}
							}, HUID.randomID().toString(), timeout);
							break;
						case TITLE:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								Player player = ((Player) toSender(result.getSource()));
								if (this.title.getAttachment() != null) {
									player.sendTitle(StringUtils.use(this.title.getString()).translate(), StringUtils.use(this.title.getAttachment()).translate(), 60, 60, 60);
								} else {
									player.sendTitle(StringUtils.use(this.title.getString()).translate(), "", 60, 60, 60);
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
							((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate()));
							break;
						case CHAT:
							if (components != null) {
								((Player)toSender(result.getSource())).spigot().sendMessage(components);
							} else {
								toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate());
							}
							break;
						case BROADCAST:
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (predicate.getAttachment().test(online)) {
									if (Check.isJson(predicate.getString())) {
										BaseComponent[] base = new FancyMessage().append(predicate.getString()).build();
										online.spigot().sendMessage(base);
									} else {
										online.sendMessage(StringUtils.use(predicate.getString()).translate());
									}
								}
							}
							break;
						case TITLE:
							Player player = ((Player) toSender(result.getSource()));
							if (this.title.getAttachment() != null) {
								player.sendTitle(StringUtils.use(this.title.getString()).translate(), StringUtils.use(this.title.getAttachment()).translate(), 60, 60, 60);
							} else {
								player.sendTitle(StringUtils.use(this.title.getString()).translate(), "", 60, 60, 60);
							}
							break;
					}
				} else {
					switch (type) {
						case ACTION:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> ((Player) toSender(result.getSource())).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.use(this.text).translate())), HUID.randomID().toString(), date);
						case CHAT:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								if (components != null) {
									((Player)toSender(result.getSource())).spigot().sendMessage(components);
								} else {
									toSender(result.getSource()).sendMessage(StringUtils.use(this.text).translate());
								}
							}, HUID.randomID().toString(), date);
							break;
						case BROADCAST:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								for (Player online : Bukkit.getOnlinePlayers()) {
									if (predicate.getAttachment().test(online)) {
										online.sendMessage(StringUtils.use(predicate.getString()).translate());
									}
								}
							}, HUID.randomID().toString(), date);
							break;
						case TITLE:
							LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).wait(() -> {
								Player player = ((Player) toSender(result.getSource()));
								if (this.title.getAttachment() != null) {
									player.sendTitle(StringUtils.use(this.title.getString()).translate(), StringUtils.use(this.title.getAttachment()).translate(), 60, 60, 60);
								} else {
									player.sendTitle(StringUtils.use(this.title.getString()).translate(), "", 60, 60, 60);
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
			return CompletableFuture.supplyAsync(() -> this.parent);
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
