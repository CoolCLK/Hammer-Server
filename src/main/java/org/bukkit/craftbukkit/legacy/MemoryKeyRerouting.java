package org.bukkit.craftbukkit.legacy;

import org.bukkit.craftbukkit.legacy.reroute.NotInBukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.memory.MemoryKey;

public class MemoryKeyRerouting {

    @NotInBukkit
    public static <T> T getMemory(LivingEntity livingEntity, MemoryKey memoryKey) {
        if (memoryKey.isTyped()) {
            return livingEntity.getMemory((MemoryKey.Typed<T>) memoryKey.typed(memoryKey.getMemoryClass()));
        }

        // Would at the time this was added throw an error anyway, so just thrown now also
        throw new IllegalArgumentException("Memory key is not typed: " + memoryKey);
    }

    @NotInBukkit
    public static <T> void setMemory(LivingEntity livingEntity, MemoryKey memoryKey, T value) {
        if (memoryKey.isTyped()) {
            livingEntity.setMemory((MemoryKey.Typed<T>) memoryKey.typed(memoryKey.getMemoryClass()), value);
            return;
        }

        // Would at the time this was added throw an error anyway, so just thrown now also
        throw new IllegalArgumentException("Memory key is not typed: " + memoryKey);
    }
}
