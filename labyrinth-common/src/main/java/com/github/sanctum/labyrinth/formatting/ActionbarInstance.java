package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.event.LabyrinthVentCall;
import com.github.sanctum.labyrinth.formatting.string.FormattedString;
import com.github.sanctum.labyrinth.library.Deployable;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.labyrinth.task.RenderedTask;
import com.github.sanctum.labyrinth.task.Task;
import com.github.sanctum.labyrinth.task.TaskMonitor;
import com.github.sanctum.labyrinth.task.TaskPredicate;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.event.Vent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ActionbarInstance {

	@NotNull Player getHolder();

	@NotNull BaseComponent[] getText();

	@Nullable BaseComponent[] getLastText();

	long getRepetition();

	long getLastKnownRepetition();

	boolean isRepeating();

	@NotNull Deployable<ActionBarEvent> setText(@NotNull String text);

	@NotNull Deployable<ActionBarEvent> setText(@NotNull BaseComponent... components);

	@NotNull Deployable<ActionBarEvent> setTextEvery(long repetitionInTicks, @NotNull String text);

	@NotNull Deployable<ActionBarEvent> setTextEvery(long repetitionInTicks, @NotNull BaseComponent... components);

	@NotNull Deployable<RenderedTask> refactor();

	ActionbarInstance pause();

	ActionbarInstance stop();

	static @NotNull ActionbarInstance of(@NotNull Player player) {
		ActionbarInstance cache = ActionBarEvent.instances.stream().filter(a -> a.getHolder().equals(player)).findFirst().orElse(null);
		if (cache != null) return cache;
		cache = new ActionbarInstance() {

			final Player holder = player;
			boolean paused;
			BaseComponent[] text = new FancyMessage("").build();
			BaseComponent[] lastText = new FancyMessage("").build();
			long repetition = -1;
			long lastKnownRepetition = -1;

			@Override
			public @NotNull Player getHolder() {
				return holder;
			}

			@Override
			public @NotNull BaseComponent[] getText() {
				return text;
			}

			@Override
			public @Nullable BaseComponent[] getLastText() {
				return lastText;
			}

			@Override
			public long getRepetition() {
				return repetition;
			}

			@Override
			public long getLastKnownRepetition() {
				return lastKnownRepetition;
			}

			@Override
			public boolean isRepeating() {
				return TaskMonitor.getLocalInstance().get(getHolder().getName() + ";Action-bar") != null;
			}

			@Override
			public @NotNull Deployable<ActionBarEvent> setText(@NotNull String text) {
				return setText(new TextComponent(TextComponent.fromLegacyText(text)));
			}

			@Override
			public @NotNull Deployable<ActionBarEvent> setText(@NotNull BaseComponent... components) {
				return Deployable.of(() -> {
					ActionBarEvent e = new LabyrinthVentCall<>(new ActionBarEvent(components, getHolder(), -1)).run();
					if (!e.isCancelled()) {
						this.lastText = this.text;
						this.text = e.getText();
						this.lastKnownRepetition = this.repetition;
						this.repetition = e.getRepetition();
					}
					return e;
				});
			}

			@Override
			public @NotNull Deployable<ActionBarEvent> setTextEvery(long repetitionInTicks, @NotNull String text) {
				return setTextEvery(repetitionInTicks, new TextComponent(TextComponent.fromLegacyText(text)));
			}

			@Override
			public @NotNull Deployable<ActionBarEvent> setTextEvery(long repetitionInTicks, @NotNull BaseComponent... components) {
				return Deployable.of(() -> {
					ActionBarEvent e = new LabyrinthVentCall<>(new ActionBarEvent(components, getHolder(), repetitionInTicks)).run();
					if (!e.isCancelled()) {
						this.lastKnownRepetition = this.repetition;
						this.repetition = e.getRepetition();
						this.lastText = this.text;
						this.text = e.getText();
					}
					return e;
				});
			}

			@Override
			public @NotNull Deployable<RenderedTask> refactor() {
				if (isRepeating()) stopNotClear();
				if (repetition <= -1) {
					return Deployable.of(() -> {
						this.paused = false;
						return TaskScheduler.of(() -> {
							ComponentChunk chunk = new ComponentChunk(this.text);
							chunk.map(s -> new FormattedString(s).translate(getHolder()).get());
							FancyMessage translation = new FancyMessage().append(chunk);
							Mailer.empty(getHolder()).action(translation.build()).deploy();
						}).schedule();
					});
				}
				return Deployable.of(() -> {
					this.paused = false;
					return TaskScheduler.of(() -> {
						ComponentChunk chunk = new ComponentChunk(this.text);
						chunk.map(s -> new FormattedString(s).translate(getHolder()).get());
						FancyMessage translation = new FancyMessage().append(chunk);
						Mailer.empty(getHolder()).action(translation.build()).deploy();
					}).scheduleTimer(getHolder().getName() + ";Action-bar", 0, repetition, TaskPredicate.cancelAfter(task -> {
						if (holder == null || !getHolder().isOnline()) {
							stop();
							task.cancel();
							return false;
						}
						return true;
					}));
				});
			}

			@Override
			public ActionbarInstance pause() {
				if (isRepeating() && !this.paused) {
					this.paused = true;
					stopNotClear();
				}
				return this;
			}

			@Override
			public ActionbarInstance stop() {
				Task t = TaskMonitor.getLocalInstance().get(getHolder().getName() + ";Action-bar");
				if (t != null) {
					t.cancel();
					this.lastText = this.text;
					this.text = new FancyMessage("").build();
					this.lastKnownRepetition = this.repetition;
					this.repetition = -1;
				}
				return this;
			}

			public void stopNotClear() {
				Task t = TaskMonitor.getLocalInstance().get(getHolder().getName() + ";Action-bar");
				if (t != null) {
					t.cancel();
				}
			}


		};
		ActionBarEvent.instances.add(cache);
		return cache;
	}

}
