package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.panther.annotation.Experimental;
import com.github.sanctum.panther.file.JsonAdapter;
import com.github.sanctum.panther.file.Node;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Node.Pointer("org.bukkit.inventory.ItemStack")
public final class ItemStackSerializable implements JsonAdapter<ItemStack> {

	private final Gson gson = new GsonBuilder().create();

	@Experimental(dueTo = "Needing a way to make pdc data persistent too, otherwise item serialization/deserialization works flawlessly")
	@Override
	public JsonElement write(ItemStack l) {
		JsonObject o = new JsonObject();
		o.addProperty("type", l.getType().name());
		o.addProperty("amount", l.getAmount());

		ItemMeta meta = l.getItemMeta();
		JsonObject metaObj = new JsonObject();
		if (!Bukkit.getItemFactory().equals(meta, null)) {
			JsonObject enchants = new JsonObject();
			for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
				enchants.addProperty(entry.getKey().getKey().toString(), entry.getValue());
			}
			JsonArray flags = new JsonArray();
			for (ItemFlag flag : meta.getItemFlags()) {
				flags.add(flag.name());
			}
			if (meta.hasDisplayName()) {
				metaObj.addProperty("displayname", meta.getDisplayName());
			}
			if (meta.hasLore()) {
				metaObj.add("lore", gson.toJsonTree(meta.getLore()));
			}
			if (meta.getItemFlags().size() > 0) {
				metaObj.add("flags", flags);
			}
			metaObj.add("enchantments", enchants);

			metaObj.addProperty("data", l.getData().getData());

			o.add("meta", metaObj);
		}

		return o;
	}

	@Override
	public ItemStack read(Map<String, Object> o) {
		int amount = Integer.parseInt(String.valueOf(o.get("amount")));
		Material type = Material.valueOf((String) o.get("type"));
		ItemStack stack = new ItemStack(type);
		Item.Edit edit = Items.edit();
		Map<String,Object> ob = (Map<String, Object>) o.get("meta");
		if (ob != null) {
			byte data = Byte.parseByte(ob.get("data").toString());
			Map<String, Object> enchants = (Map<String, Object>) ob.get("enchantments");
			enchants.forEach((label, l) -> {
				int integer = Integer.parseInt(l.toString());
				String key = label.split(":")[0];
				String space = label.split(":")[1];
				edit.addEnchantment(Enchantment.getByKey(new NamespacedKey(key, space)), integer);
			});
			if (ob.get("displayname") != null) {
				String name = (String) ob.get("displayname");
				edit.setTitle(name);
			}
			if (ob.get("lore") != null) {
				List<String> name = (List<String>) ob.get("lore");
				edit.setLore(name);
			}
			if (ob.get("flags") != null) {
				List<String> flags = (List<String>) ob.get("flags");
				for (String f : flags) {
					edit.setFlags(ItemFlag.valueOf(f));
				}
			}
			stack.getData().setData(data);
		}
		edit.setItem(stack);
		edit.setAmount(amount);
		return edit.build();
	}

	@Override
	public Class<ItemStack> getSerializationSignature() {
		return ItemStack.class;
	}

}
