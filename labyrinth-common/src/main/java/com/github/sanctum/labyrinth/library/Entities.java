package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.service.Constant;
import com.github.sanctum.panther.util.TypeAdapter;
import java.util.Locale;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

/**
 * Access to entity related logic.
 *
 * @author Hempfest
 */
public final class Entities {

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
	public static final BlockSpawner FALLING_BLOCK = new BlockSpawner();
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
	public static final Spawner<PolarBear> POLAR_BEAR = new Spawner<>(EntityType.POLAR_BEAR);
	public static final Spawner<TNTPrimed> PRIMED_TNT = new Spawner<>(EntityType.PRIMED_TNT);
	public static final Spawner<PufferFish> PUFFERFISH = new Spawner<>(EntityType.PUFFERFISH);
	public static final Spawner<Rabbit> RABBIT = new Spawner<>(EntityType.RABBIT);
	public static final Spawner<IronGolem> IRON_GOLEM = new Spawner<>(EntityType.IRON_GOLEM);
	public static final Spawner<Parrot> PARROT = new Spawner<>(EntityType.PARROT);
	public static final Spawner<Phantom> PHANTOM = new Spawner<>(EntityType.PHANTOM);
	public static final Spawner<Piglin> PIGLIN = new Spawner<>(EntityType.PIGLIN);
	public static final Spawner<PiglinBrute> PIGLIN_BRUTE = new Spawner<>(EntityType.PIGLIN_BRUTE);
	public static final Spawner<Ravager> RAVAGER = new Spawner<>(EntityType.RAVAGER);
	public static final Spawner<Salmon> SALMON = new Spawner<>(EntityType.SALMON);
	public static final Spawner<Shulker> SHULKER = new Spawner<>(EntityType.SHULKER);
	public static final Spawner<ShulkerBullet> SHULKER_BULLET = new Spawner<>(EntityType.SHULKER_BULLET);
	public static final Spawner<Silverfish> SILVERFISH = new Spawner<>(EntityType.SILVERFISH);
	public static final Spawner<Skeleton> SKELETON = new Spawner<>(EntityType.SKELETON);
	public static final Spawner<SkeletonHorse> SKELETON_HORSE = new Spawner<>(EntityType.SKELETON_HORSE);
	public static final Spawner<Slime> SLIME = new Spawner<>(EntityType.SLIME);
	public static final Spawner<SmallFireball> SMALL_FIREBALL = new Spawner<>(EntityType.SMALL_FIREBALL);
	public static final Spawner<Snowball> SNOWBALL = new Spawner<>(EntityType.SNOWBALL);
	public static final Spawner<Snowman> SNOWMAN = new Spawner<>(EntityType.SNOWMAN);
	public static final Spawner<SpectralArrow> SPECTRAL_aRROW = new Spawner<>(EntityType.SPECTRAL_ARROW);
	public static final Spawner<Spider> SPIDER = new Spawner<>(EntityType.SPIDER);
	public static final Spawner<Squid> SQUID = new Spawner<>(EntityType.SQUID);
	public static final Spawner<Stray> STRAY = new Spawner<>(EntityType.STRAY);
	public static final Spawner<Strider> STRIDER = new Spawner<>(EntityType.STRIDER);
	public static final Spawner<ThrownExpBottle> THROWN_EXP_BOTTLE = new Spawner<>(EntityType.THROWN_EXP_BOTTLE);
	public static final Spawner<TraderLlama> TRADER_LLAMA = new Spawner<>(EntityType.TRADER_LLAMA);
	public static final Spawner<Trident> TRIDEnt = new Spawner<>(EntityType.TRIDENT);
	public static final Spawner<TropicalFish> TROPICAL_FISH = new Spawner<>(EntityType.TROPICAL_FISH);
	public static final Spawner<Turtle> TURTLE = new Spawner<>(EntityType.TURTLE);
	public static final Spawner<Vex> VEX = new Spawner<>(EntityType.VEX);
	public static final Spawner<Villager> VILLAGER = new Spawner<>(EntityType.VILLAGER);
	public static final Spawner<Vindicator> VINDICATOR = new Spawner<>(EntityType.VINDICATOR);
	public static final Spawner<WanderingTrader> WANDERING_TRADER = new Spawner<>(EntityType.WANDERING_TRADER);
	public static final Spawner<Witch> WITCH = new Spawner<>(EntityType.WITCH);
	public static final Spawner<Wither> WITHER = new Spawner<>(EntityType.WITHER);
	public static final Spawner<WitherSkeleton> WITHER_SKELETON = new Spawner<>(EntityType.WITHER_SKELETON);
	public static final Spawner<WitherSkull> WITHER_SKULL = new Spawner<>(EntityType.WITHER_SKULL);
	public static final Spawner<Wolf> WOLF = new Spawner<>(EntityType.WOLF);
	public static final Spawner<Zoglin> ZOGLIN = new Spawner<>(EntityType.ZOGLIN);
	public static final Spawner<Zombie> ZOMBIE = new Spawner<>(EntityType.ZOMBIE);
	public static final Spawner<ZombieHorse> ZOMBIE_HORSE = new Spawner<>(EntityType.ZOMBIE_HORSE);
	public static final Spawner<ZombieVillager> ZOMBIE_VILLAGER = new Spawner<>(EntityType.ZOMBIE_VILLAGER);

	/**
	 * Search for an entity type result ignoring case
	 * typical delimiting underscores.
	 *
	 * @param name name of the entity; disregards case and underscores
	 * @return the desired EntityType or null
	 */
	public static EntityType getEntity(String name) {
		TypeAdapter<Spawner<? extends Entity>> flag = TypeAdapter.get();
		return Constant.values(Entities.class, flag).stream().filter(spawnerConstant -> spawnerConstant.getName().toLowerCase(Locale.ROOT).replace("_", "").equals(name.toLowerCase(Locale.ROOT).replace("_", ""))).findFirst().map(Constant::getValue).map(Spawner::getType).orElse(null);
	}

	public static Spawner<? extends Entity> getSpawner(String name) {
		return Constant.values(Entities.class, () -> Spawner.class).stream().filter(spawnerConstant -> spawnerConstant.getName().equals(name)).findFirst().map(spawnerConstant -> (Spawner<?>) spawnerConstant.getValue()).orElse(null);
	}

	public static Spawner<? extends Entity> getSpawner(EntityType type) {
		return Constant.values(Entities.class, () -> Spawner.class).stream().filter(spawnerConstant -> spawnerConstant.getValue().type == type).findFirst().map(spawnerConstant -> (Spawner<?>) spawnerConstant.getValue()).orElse(null);
	}

	Entities() {
	}

	public static class BlockSpawner extends Spawner<FallingBlock> {

		public BlockSpawner() {
			super(EntityType.FALLING_BLOCK);
		}

		@Override
		public FallingBlock spawn(@NotNull Location location) {
			return super.spawn(location);
		}

		public FallingBlock spawn(@NotNull Location location, Material data) {
			if (location.getWorld() == null)
				throw new IllegalStateException("Cannot spawn entities in non existent worlds!");
			return location.getWorld().spawnFallingBlock(location, data.createBlockData());
		}

		public FallingBlock spawn(@NotNull Location location, BlockData data) {
			if (location.getWorld() == null)
				throw new IllegalStateException("Cannot spawn entities in non existent worlds!");
			return location.getWorld().spawnFallingBlock(location, data);
		}

	}

	public static class Spawner<T extends Entity> {

		private final EntityType type;

		public Spawner(@NotNull("Entity type cannot be null!") EntityType type) {
			this.type = type;
		}

		public T spawn(@NotNull Location location) {
			if (location.getWorld() == null)
				throw new IllegalStateException("Cannot spawn entities in non existent worlds!");
			return location.getWorld().spawn(location, getEntityClass());
		}

		public T spawn(@NotNull Location location, Consumer<T> consumer) {
			if (location.getWorld() == null)
				throw new IllegalStateException("Cannot spawn entities in non existent worlds!");
			return location.getWorld().spawn(location, getEntityClass(), consumer::accept);
		}

		public Class<T> getEntityClass() {
			return (Class<T>) getType().getEntityClass();
		}

		public EntityType getType() {
			return type;
		}

	}

}
