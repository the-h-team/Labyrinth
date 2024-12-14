package com.github.sanctum.skulls;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.LegacyCheckService;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.panther.file.MemorySpace;
import com.github.sanctum.panther.file.Node;
import com.github.sanctum.panther.util.HUID;
import com.google.common.base.Preconditions;
import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public final class SkullReferenceDocker {

    private final MemorySpace memory;
    boolean loaded;
    final Map<SkullReferenceTicket, SkullReferenceLookup> queue;
    final Map<SkullReferenceTicket, ItemStack> additions;

    public SkullReferenceDocker(MemorySpace memory) {
        this.memory = memory;
        this.queue = new HashMap<>();
        this.additions = new HashMap<>();
    }

    public SkullReferenceDocker(Plugin plugin, String fileName, String directory) {
        this.memory = FileList.search(plugin).get(fileName, directory).getRoot();
        this.queue = new HashMap<>();
        this.additions = new HashMap<>();
    }

    /**
     * Search through a specific section in your config for the heads.
     * <p>
     * Example:
     * <pre>ConfigHeader.My_heads</pre>
     *
     * Which would contain a name, category & base64 serialized value.
     *
     * @param section the key of a {@link org.bukkit.configuration.ConfigurationSection} from your file to use
     * @throws InvalidSkullReferenceException if one or more skull references provided are invalid.
     * @return this head loader instance with attempted values
     */
    public SkullReferenceDocker load(String section) throws InvalidSkullReferenceException {
        if (memory.isNode(section)) {
            Node parent = memory.getNode(section);
            for (String id : parent.getKeys(false)) {
                boolean custom = parent.getNode(id).getNode("custom").toPrimitive().getBoolean();
                String name = parent.getNode(id).getNode("name").toPrimitive().getString();
                if (name == null) continue;
                if (custom) {
                    String category = parent.getNode(id).getNode("category").toPrimitive().getString();
                    String value = null;

                    if (parent.getNode(id).getNode("value").toPrimitive().isString()) {
                        value = parent.getNode(id).getNode("value").toPrimitive().getString();
                    }

                    if (value != null) {
                        additions.put(new SkullReferenceTicket(name, category), provide(value));
                    } else {
                        LabyrinthProvider.getInstance().getLogger().severe("- Custom head #" + id + " has no value to use.");
                    }


                } else {
                    String category = parent.getNode(id).getNode("category").toPrimitive().getString();
                    String user = parent.getNode(id).getNode("user").toPrimitive().getString();

                    boolean isID = user != null && user.contains("-");

                    if (isID) {
                        queue.put(new SkullReferenceTicket(name, category), new SkullReferenceLookup(UUID.fromString(user)));
                    } else {
                        queue.put(new SkullReferenceTicket(name, category), new SkullReferenceLookup(user));
                    }
                }
            }
        }
        return this;
    }

    /**
     * Optionally pre-map all loaded reference tickets (Handled internally)
     *
     * @param ignoreMapped whether to remap the values fresh from the additions map.
     * @return The same skull reference docker.
     */
    public SkullReferenceDocker map(boolean ignoreMapped) {
        if (!loaded) {
            this.loaded = true;
            for (Map.Entry<SkullReferenceTicket, SkullReferenceLookup> entry : this.queue.entrySet()) {
                ItemStack result = entry.getValue().getResult();
                this.additions.put(entry.getKey(), result);
            }
        }
        return this;
    }

    /**
     * Complete the requirements to load the desired head database into cache.
     */
    public void complete() {
        if (!additions.isEmpty()) {
            SkullReferenceUtility.load(this);
        } else {
            LabyrinthProvider.getInstance().getLogger().warning("- No heads were loaded from memory space " + memory.getPath());
        }
    }


    // TODO: decide nullity (does not seem to be Nullable); maybe add throws


    private static Optional<URL> parseHeadValue(String headValue) {
        if (headValue == null)
            throw new NullPointerException("headValue is marked non-null but is null");
        return decodeBase64(headValue)
                .flatMap(SkullReferenceDocker::decodeJson)
                .flatMap(jsonObject -> getObject(jsonObject, "textures"))
                .flatMap(texturesObject -> getObject(texturesObject, "SKIN"))
                .flatMap(skinObject -> getString(skinObject, "url"))
                .flatMap(SkullReferenceDocker::parseUrl);
    }


    private static Optional<String> decodeBase64(String encodedValue) {
        if (encodedValue == null)
            throw new NullPointerException("encodedValue is marked non-null but is null");
        try {
            return Optional.of(new String(Base64.getDecoder().decode(encodedValue), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }


    private static Optional<JsonObject> decodeJson(String decodedValue) {
        if (decodedValue == null)
            throw new NullPointerException("decodedValue is marked non-null but is null");
        try {
            return Optional.of(JsonParser.parseString(decodedValue))
                    .filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject);
        } catch (JsonParseException e) {
            return Optional.empty();
        }
    }


    private static Optional<JsonObject> getObject(JsonObject parent, String key) {
        if (parent == null)
            throw new NullPointerException("parent is marked non-null but is null");
        if (key == null)
            throw new NullPointerException("key is marked non-null but is null");
        if (!parent.has(key)) {
            return Optional.empty();
        }
        return Optional.of(parent.get(key))
                .filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject);
    }


    private static Optional<JsonPrimitive> getPrimitive(JsonObject parent, String key) {
        if (parent == null)
            throw new NullPointerException("parent is marked non-null but is null");
        if (key == null)
            throw new NullPointerException("key is marked non-null but is null");
        if (!parent.has(key)) {
            return Optional.empty();
        }
        return Optional.of(parent.get(key))
                .filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive);
    }


    private static Optional<String> getString(JsonObject parent, String key) {
        if (parent == null)
            throw new NullPointerException("parent is marked non-null but is null");
        if (key == null)
            throw new NullPointerException("key is marked non-null but is null");
        return getPrimitive(parent, key)
                .map(JsonPrimitive::getAsString);
    }


    private static Optional<URL> parseUrl(String urlString) {
        if (urlString == null)
            throw new NullPointerException("urlString is marked non-null but is null");
        try {
            return Optional.of(new URL(urlString));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    /**
     * Apply Base64 data for a custom skin value.
     * <p>
     * *NOTE: Not cached.
     *
     * @param headValue the target head value to apply to a skull item
     * @return the specified custom head
     */
    public static ItemStack provide(String headValue) {
        boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
        Material type = Material.matchMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
        Preconditions.checkNotNull(type);
        ItemStack skull;
        if (isNew) {
            skull = new ItemStack(type);
        } else {
            //noinspection deprecation
            skull = new ItemStack(type, 1, (short) 3);
        }
        if (headValue != null) {

            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

            if (LabyrinthProvider.getInstance().isServerProfiles()) {
                try {
                    Class<?> playerProfileClass = Class.forName("org.bukkit.profile.PlayerProfile");
                    Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                    Method createPlayerProfile = bukkitClass.getMethod("createPlayerProfile", UUID.class, String.class);
                    // create new player profile
                    Object playerProfile = createPlayerProfile.invoke(null, UUID.nameUUIDFromBytes(headValue.getBytes()), HUID.parseID(headValue).newID().toString());
                    Object craftTextures = playerProfile.getClass().getMethod("getTextures").invoke(playerProfile);
                    // apply texture to player profile
                    Method setSkin = craftTextures.getClass().getMethod("setSkin", URL.class);
                    Optional<URL> url = parseHeadValue(headValue);
                    if (url.isPresent()) {
                        setSkin.invoke(craftTextures, url.get());
                    }
                    // update skull meta
                    Method setOwnerProfile = skullMeta.getClass().getMethod("setOwnerProfile", playerProfileClass);
                    setOwnerProfile.setAccessible(true);
                    setOwnerProfile.invoke(skullMeta, playerProfile);
                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (LabyrinthProvider.getInstance().isLegacy()) {
                if (LegacyCheckService.VERSION.contains("1_8")) {
                    return getCustomHead(type, headValue);
                } else {
                    try {
                        Class<?> gameProfileAdapt = Class.forName("com.mojang.authlib.GameProfile");
                        Constructor<?> constructor = gameProfileAdapt.getConstructor(UUID.class, String.class);
                        constructor.setAccessible(true);
                        Object gameProfile = constructor.newInstance(UUID.nameUUIDFromBytes(headValue.getBytes()), HUID.parseID(headValue).newID().toString());
                        Method getPropertiesMethod = gameProfileAdapt.getMethod("getProperties");
                        getPropertiesMethod.setAccessible(true);
                        Class<?> propertyAdapt = Class.forName("com.mojang.authlib.properties.Property");
                        Constructor<?> propertyCon = propertyAdapt.getConstructor(String.class, String.class);
                        propertyCon.setAccessible(true);
                        getPropertiesMethod.invoke(gameProfile, "textures", propertyCon.newInstance("textures", headValue));
                        Field profileField = skullMeta.getClass().getDeclaredField("profile");
                        profileField.setAccessible(true);
                        profileField.set(skullMeta, gameProfile);
                    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                             IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                        LabyrinthProvider.getInstance().getLogger().severe("Unable to resolve head loader. Cannot access GameProfile");
                    }
                }
            } else {
                try {
                    Class<?> gameProfileAdapt = Class.forName("com.mojang.authlib.GameProfile");
                    Constructor<?> constructor = gameProfileAdapt.getConstructor(UUID.class, String.class);
                    constructor.setAccessible(true);
                    Object gameProfile = constructor.newInstance(UUID.nameUUIDFromBytes(headValue.getBytes()), HUID.randomID().toString());
                    Method getPropertiesMethod = gameProfileAdapt.getMethod("getProperties");
                    getPropertiesMethod.setAccessible(true);
                    Class<?> propertyAdapt = Class.forName("com.mojang.authlib.properties.Property");
                    Constructor<?> propertyCon = propertyAdapt.getConstructor(String.class, String.class);
                    propertyCon.setAccessible(true);
                    getPropertiesMethod.invoke(gameProfile, "textures", propertyCon.newInstance("textures", headValue));
                    Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", gameProfileAdapt);
                    mtd.setAccessible(true);
                    mtd.invoke(skullMeta, gameProfile);
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                         IllegalAccessException |
                         InvocationTargetException e) {
                    LabyrinthProvider.getInstance().getLogger().severe("Unable to resolve head loader. Cannot access GameProfile");
                }
            }

            skull.setItemMeta(skullMeta);
            return skull;
        }
        return skull;
    }

    static ItemStack getCustomHead(Material mat, String headValue) {

        ItemStack skull = new ItemStack(mat, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        assert skullMeta != null;

        try {
            // We get the bytes from the texture in Base64 encoded that comes from the Minecraft-URL.
            Class<?> gameProfileAdapt = Class.forName("com.mojang.authlib.GameProfile");
            Constructor<?> constructor = gameProfileAdapt.getConstructor(UUID.class, String.class);
            constructor.setAccessible(true);
            Object gameProfile = constructor.newInstance(UUID.nameUUIDFromBytes(headValue.getBytes()), HUID.parseID(headValue).newID().toString());
            Method getPropertiesMethod = gameProfileAdapt.getMethod("getProperties");
            getPropertiesMethod.setAccessible(true);
            Class<?> propertyAdapt = Class.forName("com.mojang.authlib.properties.Property");
            Constructor<?> propertyCon = propertyAdapt.getConstructor(String.class, String.class);
            propertyCon.setAccessible(true);

            // Access the properties map of the GameProfile using reflection
            Field propertiesField = gameProfileAdapt.getDeclaredField("properties");
            propertiesField.setAccessible(true);
            Object properties = propertiesField.get(gameProfile);


            Method addPropertyMethod = properties.getClass().getMethod("put", Object.class, Object.class);
            // Create a Property object with the texture (base64-encoded)
            addPropertyMethod.invoke(properties, "textures", propertyCon.newInstance("textures", headValue));

            Field field = skullMeta.getClass().getDeclaredField("profile"); // We get the field profile.

            field.setAccessible(true); // We set as accessible to modify.
            field.set(skullMeta, gameProfile); // We set in the skullMeta the modified GameProfile that we created.

        } catch (Exception e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);

        return skull; //Finally, you have the custom head!

    }

}
