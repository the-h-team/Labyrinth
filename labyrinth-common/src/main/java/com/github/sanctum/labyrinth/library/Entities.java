package com.github.sanctum.labyrinth.library;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Egg;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Llama;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mule;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TNTPrimed;
import org.jetbrains.annotations.NotNull;

/**
 * Access to entity related logic.
 *
 * @author Hempfest
 */
public final class Entities {
	private static final Map<String, EntityType> TYPE_MAP = new HashMap<>();

	public static final Spawner<Pig> PIG = new Spawner<>(EntityType.PIG);
	public static final Spawner<Cow> COW = new Spawner<>(EntityType.COW);
	public static final Spawner<Sheep> SHEEP = new Spawner<>(EntityType.SHEEP);
	public static final Spawner<AreaEffectCloud> AREA_EFFECT_CLOUD = new Spawner<>(EntityType.AREA_EFFECT_CLOUD);
	public static final Spawner<ArmorStand> ARMOR_STAND = new Spawner<>(EntityType.ARMOR_STAND);
	public static final Spawner<Arrow> ARROW = new Spawner<>(EntityType.ARROW);
	public static final Spawner<Bat> BAT = new Spawner<>(EntityType.BAT);
	public static final Spawner<Bee> BEE = new Spawner<>(EntityType.BEE);
	public static final Spawner<Blaze> BLAZE = new Spawner<>(EntityType.BLAZE);
	public static final Spawner<Boat> BOAT = new Spawner<>(EntityType.BOAT);
	public static final Spawner<Pig> CAT = new Spawner<>(EntityType.CAT);
	public static final Spawner<Cat> CAVE_SPIDER = new Spawner<>(EntityType.CAVE_SPIDER);
	public static final Spawner<Chicken> CHICKEN = new Spawner<>(EntityType.CHICKEN);
	public static final Spawner<Cod> COD = new Spawner<>(EntityType.COD);
	public static final Spawner<Creeper> CREEPER = new Spawner<>(EntityType.CREEPER);
	public static final Spawner<Dolphin> DOLPHIN = new Spawner<>(EntityType.DOLPHIN);
	public static final Spawner<Donkey> DONKEY = new Spawner<>(EntityType.DONKEY);
	public static final Spawner<DragonFireball> DRAGON_FIREBALL = new Spawner<>(EntityType.DRAGON_FIREBALL);
	public static final Spawner<Item> DROPPED_ITEM = new Spawner<>(EntityType.DROPPED_ITEM);
	public static final Spawner<Drowned> DROWNED = new Spawner<>(EntityType.DROWNED);
	public static final Spawner<Egg> EGG = new Spawner<>(EntityType.EGG);
	public static final Spawner<ElderGuardian> ELDER_GUARDIAN = new Spawner<>(EntityType.ELDER_GUARDIAN);
	public static final Spawner<EnderCrystal> ENDER_CRYSTAL = new Spawner<>(EntityType.ENDER_CRYSTAL);
	public static final Spawner<EnderDragon> ENDER_DRAGON = new Spawner<>(EntityType.ENDER_DRAGON);
	public static final Spawner<EnderPearl> ENDER_PEARL = new Spawner<>(EntityType.ENDER_PEARL);
	public static final Spawner<EnderSignal> ENDER_SIGNAL = new Spawner<>(EntityType.ENDER_SIGNAL);
	public static final Spawner<Enderman> ENDERMAN = new Spawner<>(EntityType.ENDERMAN);
	public static final Spawner<Endermite> ENDERMITE = new Spawner<>(EntityType.ENDERMITE);
	public static final Spawner<Evoker> EVOKER = new Spawner<>(EntityType.EVOKER);
	public static final Spawner<EvokerFangs> EVOKER_FANGS = new Spawner<>(EntityType.EVOKER_FANGS);
	public static final Spawner<ExperienceOrb> EXPERIENCE_ORB = new Spawner<>(EntityType.EXPERIENCE_ORB);
	public static final Spawner<FallingBlock> FALLING_BLOCK = new Spawner<>(EntityType.FALLING_BLOCK);
	public static final Spawner<Fireball> FIREBALL = new Spawner<>(EntityType.FIREBALL);
	public static final Spawner<Firework> FIREWORK = new Spawner<>(EntityType.FIREWORK);
	public static final Spawner<FishHook> FISHING_HOOK = new Spawner<>(EntityType.FISHING_HOOK);
	public static final Spawner<Fox> FOX = new Spawner<>(EntityType.FOX);
	public static final Spawner<Ghast> GHAST = new Spawner<>(EntityType.GHAST);
	public static final Spawner<Giant> GIANT = new Spawner<>(EntityType.GIANT);
	public static final Spawner<Guardian> GUARDIAN = new Spawner<>(EntityType.GUARDIAN);
	public static final Spawner<Hoglin> HOGLIN = new Spawner<>(EntityType.HOGLIN);
	public static final Spawner<Horse> HORSE = new Spawner<>(EntityType.HORSE);
	public static final Spawner<Husk> HUSK = new Spawner<>(EntityType.HUSK);
	public static final Spawner<Illusioner> ILLUSIONER = new Spawner<>(EntityType.ILLUSIONER);
	public static final Spawner<ItemFrame> ITEM_FRAME = new Spawner<>(EntityType.ITEM_FRAME);
	public static final Spawner<LeashHitch> LEASH_HITCH = new Spawner<>(EntityType.LEASH_HITCH);
	public static final Spawner<LightningStrike> LIGHTNING = new Spawner<>(EntityType.LIGHTNING);
	public static final Spawner<Llama> LLAMA = new Spawner<>(EntityType.LLAMA);
	public static final Spawner<LlamaSpit> LLAMA_SPIT = new Spawner<>(EntityType.LLAMA_SPIT);
	public static final Spawner<MagmaCube> MAGMA_CUBE = new Spawner<>(EntityType.MAGMA_CUBE);
	public static final Spawner<Minecart> MINECART = new Spawner<>(EntityType.MINECART);
	public static final Spawner<Minecart> MINECART_CHEST = new Spawner<>(EntityType.MINECART_CHEST);
	public static final Spawner<Minecart> MINECART_COMMAND = new Spawner<>(EntityType.MINECART_COMMAND);
	public static final Spawner<Minecart> MINECART_FURNACE = new Spawner<>(EntityType.MINECART_FURNACE);
	public static final Spawner<Minecart> MINECART_HOPPER = new Spawner<>(EntityType.MINECART_HOPPER);
	public static final Spawner<Minecart> MINECART_MOB_SPAWNER = new Spawner<>(EntityType.MINECART_MOB_SPAWNER);
	public static final Spawner<Minecart> MINECART_TNT = new Spawner<>(EntityType.MINECART_TNT);
	public static final Spawner<Mule> MULE = new Spawner<>(EntityType.MULE);
	public static final Spawner<MushroomCow> MUSHROOM_COW = new Spawner<>(EntityType.MUSHROOM_COW);
	public static final Spawner<Ocelot> OCELOT = new Spawner<>(EntityType.OCELOT);
	public static final Spawner<Painting> PAINTING = new Spawner<>(EntityType.PAINTING);
	public static final Spawner<Panda> PANDA = new Spawner<>(EntityType.PANDA);
	public static final Spawner<Pillager> PILLAGER = new Spawner<>(EntityType.PILLAGER);
	public static final Spawner<Player> PLAYER = new Spawner<>(EntityType.PLAYER);
	public static final Spawner<PolarBear> POLAR_BEAR = new Spawner<>(EntityType.POLAR_BEAR);
	public static final Spawner<TNTPrimed> PRIMED_TNT = new Spawner<>(EntityType.PRIMED_TNT);
	public static final Spawner<PufferFish> PUFFERFISH = new Spawner<>(EntityType.PUFFERFISH);
	public static final Spawner<Rabbit> RABBIT = new Spawner<>(EntityType.RABBIT);



	static {
		for (EntityType type : EntityType.values()) {
			TYPE_MAP.put(type.name().toLowerCase().replace("_", ""), type);
		}
	}

	/**
	 * Search for an entity type result ignoring case
	 * typical delimiting underscores.
	 *
	 * @param name name of the entity; disregards case and underscores
	 * @return the desired EntityType or null
	 */
	public static EntityType getEntity(String name) {
		return TYPE_MAP.get(name.toLowerCase().replaceAll("_", ""));
	}

	private Entities() {
	}

	public static class Spawner<T extends Entity> {

		private final EntityType type;

		public Spawner(@NotNull("Entity type cannot be null!") EntityType type) {
			this.type =  type;
		}

		public T spawn(@NotNull Location location) {
			return location.getWorld().spawn(location, getEntityClass());
		}

		public T spawn(@NotNull Location location, Consumer<T> consumer) {
			return location.getWorld().spawn(location, getEntityClass(), consumer::accept);
		}

		public Class<T> getEntityClass() {
			return (Class<T>)type.getEntityClass();
		}

		public EntityType getType() {
			return type;
		}

	}

}
