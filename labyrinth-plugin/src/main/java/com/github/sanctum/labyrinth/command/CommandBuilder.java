package com.github.sanctum.labyrinth.command;

import com.github.sanctum.labyrinth.library.CommandUtils;
import org.bukkit.plugin.Plugin;

public class CommandBuilder {


	protected final Plugin plugin;

	protected CommandImpl impl;

	protected String label;

	protected String permission;

	protected String description;

	protected String usage;

	protected PlayerResultingExecutor playerResultingExecutor;

	protected ConsoleResultingExecutor consoleResultingExecutor;

	protected PlayerResultingCompleter playerResultingCompleter;

	protected CommandBuilder(Plugin plugin) {
		this.plugin = plugin;
	}

	public static CommandBuilder use(Plugin plugin) {
		return new CommandBuilder(plugin);
	}

	public CommandBuilder label(String command) {
		this.label = command;
		return this;
	}

	public CommandBuilder limit(String permission) {
		this.permission = permission;
		return this;
	}

	public CommandBuilder explain(String description) {
		this.description = description;
		return this;
	}

	public CommandBuilder example(String usage) {
		this.usage = usage;
		return this;
	}

	public CommandBuilder player(PlayerResultingExecutor executor) {
		this.playerResultingExecutor = executor;
		return this;
	}

	public CommandBuilder console(ConsoleResultingExecutor executor) {
		this.consoleResultingExecutor = executor;
		return this;
	}

	public CommandBuilder tab(PlayerResultingCompleter completer) {
		this.playerResultingCompleter = completer;
		return this;
	}

	public void unregister() {
		if (this.impl != null) {
			CommandUtils.unregister(this.impl);
		}
	}

	public void register() {
		if (impl == null) {
			CommandImpl implementation = new CommandImpl(this);
			this.impl = implementation;
			CommandRegistration.use(implementation);
		}
	}


}
