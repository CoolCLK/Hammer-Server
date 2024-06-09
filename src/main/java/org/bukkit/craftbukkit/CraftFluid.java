package org.bukkit.craftbukkit;

import java.util.Locale;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FluidType;
import org.bukkit.Fluid;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.Handleable;

public class CraftFluid extends Fluid implements Handleable<FluidType> {
    private static int count = 0;

    public static Fluid minecraftToBukkit(FluidType minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.FLUID, Registry.FLUID);
    }

    public static FluidType bukkitToMinecraft(Fluid bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    private final NamespacedKey key;
    private final FluidType fluidType;
    private final String name;
    private final int ordinal;

    public CraftFluid(NamespacedKey key, FluidType fluidType) {
        this.key = key;
        this.fluidType = fluidType;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive fluid specific values.
        // Custom fluids will return the key with namespace. For a plugin this should look than like a new fluid
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase(Locale.ROOT);
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    @Override
    public FluidType getHandle() {
        return fluidType;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(Fluid fluid) {
        return ordinal - fluid.ordinal();
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

        if (!(other instanceof CraftFluid)) {
            return false;
        }

        return getKey().equals(((Fluid) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
