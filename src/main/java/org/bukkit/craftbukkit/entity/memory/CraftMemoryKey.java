package org.bukkit.craftbukkit.entity.memory;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.memory.MemoryKey;
import org.jetbrains.annotations.NotNull;

public class CraftMemoryKey<U> implements MemoryKey, Handleable<MemoryModuleType<U>> {

    public static MemoryKey minecraftToBukkit(MemoryModuleType<?> minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.MEMORY_MODULE_TYPE, Registry.MEMORY_MODULE_TYPE);
    }

    public static <U> MemoryModuleType<U> bukkitToMinecraft(MemoryKey bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    private final NamespacedKey key;
    private final MemoryModuleType<U> memoryModuleType;

    private CraftMemoryKey(NamespacedKey key, MemoryModuleType<U> memoryModuleType) {
        this.key = key;
        this.memoryModuleType = memoryModuleType;
    }

    @NotNull
    @Override
    public <O> Typed<O> typed(@NotNull Class<O> aClass) {
        throw new UnsupportedOperationException("Cannot typed memory key " + getKey());
    }

    @Override
    public boolean isTyped() {
        return false;
    }

    @NotNull
    @Override
    public <T> TypedList<T> typedList(@NotNull Class<T> aClass) {
        throw new UnsupportedOperationException("Cannot typed memory key " + getKey());
    }

    @Override
    public boolean isTypedList() {
        return false;
    }

    @NotNull
    @Override
    public Class<?> getMemoryClass() {
        throw new UnsupportedOperationException("Memory key does not have a memory class " + getKey());
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public MemoryModuleType<U> getHandle() {
        return memoryModuleType;
    }

    public static class CraftMemoryKeyTyped<T, U> extends CraftMemoryKey<U> implements MemoryKey.Typed<T> {

        private final Class<T> clazz;

        private CraftMemoryKeyTyped(NamespacedKey key, MemoryModuleType<U> memoryModuleType, Class<T> clazz) {
            super(key, memoryModuleType);
            this.clazz = clazz;
        }

        @NotNull
        @Override
        public <O> Typed<O> typed(@NotNull Class<O> aClass) {
            Preconditions.checkArgument(aClass != null, "Class cannot be null");
            Preconditions.checkArgument(aClass == getMemoryClass(), "Provided class %s does not match required class %s", aClass, getMemoryClass());

            return (MemoryKey.Typed<O>) this;
        }

        @Override
        public boolean isTyped() {
            return true;
        }

        @NotNull
        @Override
        public Class<T> getMemoryClass() {
            return clazz;
        }
    }

    public static class CraftMemoryKeyTypedList<T, U> extends CraftMemoryKey<U> implements MemoryKey.TypedList<T> {

        private final Class<T> clazz;

        private CraftMemoryKeyTypedList(NamespacedKey key, MemoryModuleType<U> memoryModuleType, Class<T> clazz) {
            super(key, memoryModuleType);
            this.clazz = clazz;
        }

        @NotNull
        @Override
        public <O> TypedList<O> typedList(@NotNull Class<O> aClass) {
            Preconditions.checkArgument(aClass != null, "Class cannot be null");
            Preconditions.checkArgument(aClass == getMemoryClass(), "Provided class %s does not match required class %s", aClass, getMemoryClass());

            return (MemoryKey.TypedList<O>) this;
        }

        @Override
        public boolean isTypedList() {
            return true;
        }

        @NotNull
        @Override
        public Class<T> getMemoryClass() {
            return clazz;
        }
    }

    public static class CraftMemoryKeyRegistry extends CraftRegistry<MemoryKey, MemoryModuleType<?>> {

        // Currently not all keys are supported by bukkit
        // See PR bukkit#430 and craftbukkit#557 as well as ticket SPIGOT-4833 for more information
        // TODO 2024-06-02: SPIGOT-7586: This should probably be revisit and support for more memory keys added (unit keys for example)
        private static final Set<NamespacedKey> ALLOWED_KEYS = new HashSet<>();
        private static final Set<NamespacedKey> LIST_KEYS = new HashSet<>();
        private static final Map<NamespacedKey, Class<?>> BUKKIT_CLASSES = new HashMap<>();

        private static void add(String name) {
            ALLOWED_KEYS.add(NamespacedKey.fromString(name));
        }

        private static void add(String name, Class<?> clazz) {
            add(name);
            BUKKIT_CLASSES.put(NamespacedKey.fromString(name), clazz);
        }

        private static void addList(String name, Class<Location> clazz) {
            add(name, clazz);
            LIST_KEYS.add(NamespacedKey.fromString(name));
        }

        static {
            // Add classes as well as allowed memory keys
            add("home", Location.class);
            add("potential_job_site", Location.class);
            add("job_site", Location.class);
            add("meeting_point", Location.class);
            add("golem_detected_recently", Boolean.class);
            add("last_slept", Long.class);
            add("last_woken", Long.class);
            add("last_worked_at_poi", Long.class);
            add("universal_anger", Boolean.class);
            add("angry_at", UUID.class);
            add("admiring_item", Boolean.class);
            add("admiring_disabled", Boolean.class);
            add("hunted_recently", Boolean.class);
            add("play_dead_ticks", Integer.class);
            add("temptation_cooldown_ticks", Integer.class);
            add("is_tempted", Boolean.class);
            add("long_jump_cooling_down", Integer.class);
            add("has_hunting_cooldown", Boolean.class);
            add("ram_cooldown_ticks", Integer.class);
            add("liked_player", UUID.class);
            add("liked_noteblock", Location.class);
            add("liked_noteblock_cooldown_ticks", Integer.class);
            add("item_pickup_cooldown_ticks", Integer.class);
            addList("sniffer_explored_positions", Location.class);
        }

        public CraftMemoryKeyRegistry(IRegistry<MemoryModuleType<?>> minecraftRegistry, BiFunction<NamespacedKey, ApiVersion, NamespacedKey> updater) {
            super(MemoryKey.class, minecraftRegistry, null, updater);
        }

        @Override
        public MemoryKey createBukkit(NamespacedKey namespacedKey, MemoryModuleType<?> minecraft) {
            if (namespacedKey == null) {
                return null;
            }

            if (!ALLOWED_KEYS.contains(namespacedKey)) {
                return null;
            }

            if (minecraft.getCodec().isEmpty()) {
                // This should not happen since ALLOWED_KEYS only contains keys with codec / persistent memories
                return null;
            }

            boolean list = LIST_KEYS.contains(namespacedKey);
            Class<?> clazz = BUKKIT_CLASSES.get(namespacedKey);

            if (clazz == null) {
                // This should not happen since ALLOWED_KEYS only contains keys with codec / persistent memories
                return null;
            }

            return list ? new CraftMemoryKeyTypedList<>(namespacedKey, minecraft, clazz) : new CraftMemoryKeyTyped<>(namespacedKey, minecraft, clazz);
        }

        @NotNull
        @Override
        public Stream<MemoryKey> stream() {
            return super.stream().filter(Objects::nonNull);
        }
    }
}
