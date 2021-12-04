package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.component.ActionComponent;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.ListUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
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

		private final ItemStack message;
		private final String json;

		public Item(ItemStack message) {
			this.message = message;
			this.json = convertItemStackToJson(message);
		}

		private Class<?> getItemClass() {
			String name = Bukkit.getServer().getClass().getPackage().getName();
			String version = name.substring(name.lastIndexOf('.') + 1) + ".";
			String clazzName = "org.bukkit.craftbukkit." + version + "inventory.CraftItemStack";
			Class<?> clazz;

			try {
				clazz = Class.forName(clazzName);
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
			return clazz;
		}



		private Class<?> getNMSClass(String nmsClassName) {
			String name = Bukkit.getServer().getClass().getPackage().getName();
			String version = name.substring(name.lastIndexOf('.') + 1) + ".";
			String clazzName = "net.minecraft.server." + version + nmsClassName;
			Class<?> clazz;
			try {
				clazz = Class.forName(clazzName);
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
			return clazz;
		}

		private Class<?> getNewClass(String nmsClassName) {
			Class<?> clazz;
			try {
				clazz = Class.forName(nmsClassName);
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
			return clazz;
		}

		private Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
			try {
				return clazz.getMethod(methodName, params);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		private String convertItemStackToJson(ItemStack itemStack) {
			// ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
			Class<?> craftItemStackClazz = getItemClass();
			Method asNMSCopyMethod = getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

			// NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
			Class<?> nmsItemStackClazz = Bukkit.getVersion().contains("1.17") ? getNewClass("net.minecraft.world.item.ItemStack") : getNMSClass("ItemStack");
			Class<?> nbtTagCompoundClazz = Bukkit.getVersion().contains("1.17") ? getNewClass("net.minecraft.nbt.NBTTagCompound") : getNMSClass("NBTTagCompound");
			Method saveNmsItemStackMethod = getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

			Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
			Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
			Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

			try {
				nmsNbtTagCompoundObj = nbtTagCompoundClazz.getDeclaredConstructor().newInstance();
				nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
				itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}

			// Return a string representation of the serialized object
			return itemAsJsonObject.toString();
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
