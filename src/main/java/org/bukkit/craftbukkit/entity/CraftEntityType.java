package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityTypes;
import org.apache.commons.lang.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.jetbrains.annotations.NotNull;

public class CraftEntityType<E extends Entity> extends EntityType.Typed<E> implements Handleable<EntityTypes<?>> {
    private static int count = 0;

    public static EntityType minecraftToBukkit(EntityTypes<?> minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.ENTITY_TYPE, Registry.ENTITY_TYPE);
    }

    public static EntityTypes<?> bukkitToMinecraft(EntityType bukkit) {
        Preconditions.checkArgument(bukkit != EntityType.UNKNOWN, "Cannot get minecraft entity from unknown entity");

        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static String bukkitToString(EntityType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return bukkit.getKey().toString();
    }

    public static EntityType stringToBukkit(String string) {
        Preconditions.checkArgument(string != null);

        // We currently do not have any version-dependent remapping, so we can use current version
        // First convert from when only the names where saved
        string = FieldRename.convertEntityTypeName(ApiVersion.CURRENT, string);
        string = string.toLowerCase(Locale.ROOT);
        NamespacedKey key = NamespacedKey.fromString(string);

        // Now also convert from when keys where saved
        return CraftRegistry.get(Registry.ENTITY_TYPE, key, ApiVersion.CURRENT);
    }

    private final NamespacedKey key;
    private final EntityTypes<?> entityType;
    private final Class<E> clazz;
    private final Supplier<Boolean> spawnAble;
    private final boolean alive;
    private final String name;
    private final int ordinal;

    public CraftEntityType(NamespacedKey key, EntityTypes<?> entityType, Class<E> clazz) {
        this.key = key;
        this.entityType = entityType;
        this.clazz = clazz;
        this.spawnAble = Suppliers.memoize(() -> isSpawnable(this));
        this.alive = clazz != null && LivingEntity.class.isAssignableFrom(clazz);
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive entityType specific values.
        // Custom entityType will return the key with namespace. For a plugin this should look than like a new entityType
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase(Locale.ROOT);
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    private static boolean isSpawnable(CraftEntityType<?> entity) {
        CraftEntityTypes.EntityTypeData<?, ?> data = CraftEntityTypes.getEntityTypeData(entity);
        if (data == null) {
            return false;
        }

        return data.spawnFunction() != null;
    }

    public EntityTypes<?> getHandle() {
        return entityType;
    }

    @NotNull
    @Override
    public <E extends Entity> Typed<E> typed(@NotNull Class<E> aClass) {
        Preconditions.checkArgument(aClass == getEntityClass(), "Provided entity class do not match expected %s but got %s", getEntityClass(), aClass);
        return (Typed<E>) this;
    }

    @Override
    public boolean isTyped() {
        return true;
    }

    @Override
    public boolean isSpawnable() {
        return spawnAble.get();
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public Class<E> getEntityClass() {
        return clazz;
    }

    @Override
    public boolean isEnabledByFeature(@NotNull World world) {
        Preconditions.checkArgument(world != null, "World cannot be null");
        return getHandle().isEnabled(((CraftWorld) world).getHandle().enabledFeatures());
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public String getName() {
        // UNKNOWN has no name and entityType
        if (entityType == null) {
            return null;
        }
        return name.toLowerCase(Locale.ROOT);
    }

    @Override
    public int compareTo(EntityType entityType) {
        return ordinal - entityType.ordinal();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        // For backwards compatibility
        return name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftEntityType)) {
            return false;
        }

        return getKey().equals(((EntityType) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return entityType.getDescriptionId();
    }

    public static class CraftEntityTypeRegistry extends CraftRegistry<EntityType, EntityTypes<?>> {
        private static final Map<NamespacedKey, Class<? extends Entity>> CLASS_MAP = new HashMap<>();

        private static void add(String name, Class<? extends Entity> clazz) {
            CLASS_MAP.put(NamespacedKey.fromString(name), clazz);
        }

        static {
            // Add class which cannot be automatically be detected
            add("leash_knot", LeashHitch.class);
            add("eye_of_ender", EnderSignal.class);
            add("potion", ThrownPotion.class);
            add("experience_bottle", ThrownExpBottle.class);
            add("tnt", TNTPrimed.class);
            add("firework_rocket", Firework.class);
            add("command_block_minecart", CommandMinecart.class);
            add("chest_minecart", StorageMinecart.class);
            add("furnace_minecart", PoweredMinecart.class);
            add("tnt_minecart", ExplosiveMinecart.class);
            add("hopper_minecart", HopperMinecart.class);
            add("spawner_minecart", SpawnerMinecart.class);
            add("zombified_piglin", PigZombie.class);
            add("mooshroom", MushroomCow.class);
            add("snow_golem", Snowman.class);
            add("end_crystal", EnderCrystal.class);
            add("pufferfish", PufferFish.class);
            add("fishing_bobber", FishHook.class);
            add("lightning_bolt", LightningStrike.class);
        }

        public CraftEntityTypeRegistry(IRegistry<EntityTypes<?>> minecraftRegistry) {
            super(EntityType.class, minecraftRegistry, null, FieldRename.ENTITY_TYPE_RENAME);
        }

        @Override
        public EntityType createBukkit(NamespacedKey namespacedKey, EntityTypes<?> entityType) {
            if (entityType == null) {
                return null;
            }

            Class<? extends Entity> clazz = getEntityClass(namespacedKey);

            return new CraftEntityType<>(namespacedKey, entityType, clazz);
        }

        public Class<? extends Entity> getEntityClass(NamespacedKey namespacedKey) {
            Class<? extends Entity> clazz = CLASS_MAP.get(namespacedKey);

            if (clazz == null) {
                StringBuilder className = new StringBuilder();
                String[] words = namespacedKey.getKey().split("_");

                for (String word : words) {
                    className.append(StringUtils.capitalize(word));
                }

                try {
                    clazz = (Class<? extends Entity>) Class.forName("org.bukkit.entity." + className);
                } catch (ClassNotFoundException e) {
                    clazz = null;
                }
            }

            return clazz;
        }
    }
}
