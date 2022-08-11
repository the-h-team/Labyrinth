package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.component.ActionComponent;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.library.ListUtils;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.annotation.Experimental;
import com.github.sanctum.panther.util.Applicable;
import com.github.sanctum.panther.util.HUID;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public abstract class ToolTip<T> {

	public abstract T get();

	public abstract Type getType();

	public abstract ToolTip<T> style(ChatColor color);

	public abstract ToolTip<T> style(ChatColor... style);

	public abstract ToolTip<T> style(CustomColor color);

	public abstract ToolTip<T> color(ChatColor color);

	public abstract ToolTip<T> color(Color color);

	public enum Type {

		ACTION,
		COMMAND,
		URL,
		COPY,
		HOVER,
		SUGGEST


	}

	public static class Copy extends ToolTip<String> {

		private final String clipboard;

		public Copy(String clipboard) {
			this.clipboard = clipboard;
		}

		@Override
		public String get() {
			return this.clipboard;
		}

		@Override
		public Type getType() {
			return Type.COPY;
		}

		@Override
		public ToolTip<String> style(ChatColor style) {
			return this;
		}

		@Override
		public ToolTip<String> style(ChatColor... style) {
			return null;
		}

		@Override
		public ToolTip<String> style(CustomColor color) {
			return this;
		}

		@Override
		public ToolTip<String> color(ChatColor color) {
			return this;
		}

		@Override
		public ToolTip<String> color(Color color) {
			return this;
		}

	}

	public static class Url extends ToolTip<String> {

		private final String url;

		public Url(String url) {
			this.url = url;
		}

		@Override
		public String get() {
			return this.url;
		}

		@Override
		public Type getType() {
			return Type.URL;
		}

		@Override
		public ToolTip<String> style(ChatColor style) {
			return this;
		}

		@Override
		public ToolTip<String> style(ChatColor... style) {
			return null;
		}

		@Override
		public ToolTip<String> style(CustomColor color) {
			return this;
		}

		@Override
		public ToolTip<String> color(ChatColor color) {
			return this;
		}

		@Override
		public ToolTip<String> color(Color color) {
			return this;
		}

	}

	public static class Text extends ToolTip<String> {

		private String message;
		private String color;
		private String style;

		public Text(String message) {
			this.message = message;
		}

		@Override
		public String get() {
			if (this.style != null) {
				if (this.color != null) {
					return this.color + this.style + this.message;
				} else
				return this.style + this.message;
			} else if (this.color != null) {
				return this.color + this.message;
			} else return this.message;
		}

		@Override
		public Type getType() {
			return Type.HOVER;
		}

		@Override
		public ToolTip<String> style(ChatColor style) {
			List<ChatColor> targets =
					Arrays.asList(ChatColor.BOLD,
							ChatColor.ITALIC,
							ChatColor.UNDERLINE,
							ChatColor.STRIKETHROUGH,
							ChatColor.RESET,
							ChatColor.MAGIC);
			if (targets.contains(style)) {
				this.style = style.toString();
			} else throw new IllegalArgumentException("ToolTip: Invalid text style provided.");
			return this;
		}

		@Override
		public ToolTip<String> style(ChatColor... style) {
			this.style = ListUtils.use(style).join(colors -> colors.stream().map(ChatColor::toString).collect(Collectors.joining()));
			return this;
		}

		@Override
		public ToolTip<String> style(CustomColor color) {
			this.style = null;
			this.color = null;
			this.message = color.context(this.message).join();
			return this;
		}

		@Override
		public ToolTip<String> color(ChatColor color) {
			this.color = color.toString();
			return this;
		}

		@Override
		public ToolTip<String> color(Color color) {
			this.color = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
			return this;
		}

	}

	public static class Item extends ToolTip<ItemStack> {

		private final @Experimental
		Supplier<Class<?>> ITEMSTACK_NMS = () -> {
			String name = Bukkit.getServer().getClass().getPackage().getName();
			String clazzName = "org.bukkit.craftbukkit." + (name.substring(name.lastIndexOf('.') + 1) + ".") + "inventory.CraftItemStack";
			try {
				return Class.forName(clazzName);
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		};
		private final Supplier<Method> NMS_COPY = () -> getMethod(ITEMSTACK_NMS.get(), "asNMSCopy", ItemStack.class);

		private final ItemStack message;
		private final String json;

		public Item(ItemStack message) {
			this.message = message;
			this.json = itemToJson(message);
		}

		private Class<?> getNMSClass(String nmsClass) {
			String name = Bukkit.getServer().getClass().getPackage().getName();
			String version = name.substring(name.lastIndexOf('.') + 1) + ".";
			String clazzName = "net.minecraft.server." + version + nmsClass;
			try {
				return Class.forName(clazzName);
			} catch (Throwable t) {
				LabyrinthProvider.getInstance().getLogger().severe("- You should never see this message, class '" + nmsClass + "' not found.");
				return null;
			}
		}

		private Class<?> getNewClass(String nonNMSClass) {
			try {
				return Class.forName(nonNMSClass);
			} catch (Throwable t) {
				LabyrinthProvider.getInstance().getLogger().severe("- You should never see this message, class '" + nonNMSClass + "' not found.");
				return null;
			}
		}

		private Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
			try {
				return clazz.getMethod(methodName, params);
			} catch (Exception ignored) {
				LabyrinthProvider.getInstance().getLogger().severe("- Method with name '" + methodName + "' doesn't exist on runtime.");
				return null;
			}
		}

		private String itemToJson(ItemStack itemStack) {
			boolean isBrandNew = Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19");
			boolean isNew = Bukkit.getVersion().contains("1.17") || isBrandNew;
			Class<?> itemStackClass = isNew ? getNewClass("net.minecraft.world.item.ItemStack") : getNMSClass("ItemStack");
			Class<?> nbtTagCompoundClass = isNew ? getNewClass("net.minecraft.nbt.NBTTagCompound") : getNMSClass("NBTTagCompound");
			// on runtime some 1.18 bukkit source is still obfuscated for some reason, so we have to use it's obfuscated name 'b'
			Method saveNBT = getMethod(itemStackClass, isBrandNew ? "b" : "save", nbtTagCompoundClass);
			if (saveNBT == null || nbtTagCompoundClass == null) return null;
			try {
				return saveNBT.invoke(NMS_COPY.get().invoke(null, itemStack), nbtTagCompoundClass.getDeclaredConstructor().newInstance()).toString();
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		@Override
		public ItemStack get() {
			return this.message;
		}

		public String toJson() {
			return this.json;
		}

		@Override
		public Type getType() {
			return Type.HOVER;
		}

		@Override
		public ToolTip<ItemStack> style(ChatColor style) {
			return this;
		}

		@Override
		public ToolTip<ItemStack> style(ChatColor... style) {
			return null;
		}

		@Override
		public ToolTip<ItemStack> style(CustomColor color) {
			return this;
		}

		@Override
		public ToolTip<ItemStack> color(ChatColor color) {
			return this;
		}

		@Override
		public ToolTip<ItemStack> color(Color color) {
			return this;
		}

	}

	public static class Action extends ToolTip<Applicable> implements ActionComponent {

		private final HUID ID = HUID.randomID();
		private boolean marked;
		private final Applicable applicable;

		public Action(Applicable applicable) {
			this.applicable = applicable;
		}

		@Override
		public Applicable get() {
			return action();
		}

		@Override
		public String getId() {
			return ID.toString();
		}

		@Override
		public Type getType() {
			return Type.ACTION;
		}

		@Override
		public Applicable action() {
			return applicable != null ? applicable : () -> {
			};
		}

		@Override
		public boolean isMarked() {
			return marked;
		}

		@Override
		public void setMarked(boolean marked) {
			this.marked = marked;
		}

		@Override
		public void remove() {
			TaskScheduler.of(() -> LabyrinthProvider.getInstance().removeComponent(this).deploy()).scheduleLater(1);
		}

		@Override
		public ToolTip<Applicable> style(ChatColor style) {
			return this;
		}

		@Override
		public ToolTip<Applicable> style(ChatColor... style) {
			return null;
		}

		@Override
		public ToolTip<Applicable> style(CustomColor color) {
			return this;
		}

		@Override
		public ToolTip<Applicable> color(ChatColor color) {
			return this;
		}

		@Override
		public ToolTip<Applicable> color(Color color) {
			return this;
		}

	}

	public static class Command extends ToolTip<String> {

		private final String command;

		public Command(String command) {
			this.command = command.startsWith("/") ? command : "/" + command;
		}

		@Override
		public String get() {
			return this.command;
		}

		@Override
		public Type getType() {
			return Type.COMMAND;
		}

		@Override
		public ToolTip<String> style(ChatColor style) {
			return this;
		}

		@Override
		public ToolTip<String> style(ChatColor... style) {
			return null;
		}

		@Override
		public ToolTip<String> style(CustomColor color) {
			return this;
		}

		@Override
		public ToolTip<String> color(ChatColor color) {
			return this;
		}

		@Override
		public ToolTip<String> color(Color color) {
			return this;
		}

	}

	public static class Suggestion extends ToolTip<String> {

		private final String command;

		public Suggestion(String command) {
			this.command = command;
		}

		@Override
		public String get() {
			return this.command;
		}

		@Override
		public Type getType() {
			return Type.SUGGEST;
		}

		@Override
		public ToolTip<String> style(ChatColor style) {
			return this;
		}

		@Override
		public ToolTip<String> style(ChatColor... style) {
			return null;
		}

		@Override
		public ToolTip<String> style(CustomColor color) {
			return this;
		}

		@Override
		public ToolTip<String> color(ChatColor color) {
			return this;
		}

		@Override
		public ToolTip<String> color(Color color) {
			return this;
		}

	}

	public interface Factory {

		default ToolTip<String> hover(String text) {
			return new Text(text);
		}

		default ToolTip<ItemStack> hover(ItemStack itemStack) {
			return new Item(itemStack);
		}

		default ToolTip<String> suggest(String command) {
			return new Suggestion(command);
		}

		default ToolTip<String> url(String url) {
			return new Url(url);
		}

		default ToolTip<String> copy(String text) {
			return new Copy(text);
		}

		default ToolTip<String> command(String command) {
			return new Command(command);
		}

		default ToolTip<Applicable> action(Applicable data) {
			return new Action(data);
		}

	}
}
