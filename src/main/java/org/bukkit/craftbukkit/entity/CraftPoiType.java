package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.PoiType;
import org.jetbrains.annotations.NotNull;

public class CraftPoiType implements PoiType, Handleable<VillagePlaceType> {

    private final NamespacedKey key;
    private final VillagePlaceType handle;

    public CraftPoiType(final NamespacedKey key, final VillagePlaceType handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public boolean is(@NotNull final BlockState blockState) {
        return this.handle.is(((CraftBlockState) blockState).getHandle());
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public VillagePlaceType getHandle() {
        return this.handle;
    }

    public static PoiType minecraftToBukkit(VillagePlaceType minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.POINT_OF_INTEREST_TYPE, Registry.POINT_OF_INTEREST_TYPE);
    }

    public static VillagePlaceType bukkitToMinecraft(PoiType bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static PoiType minecraftHolderToBukkit(Holder<VillagePlaceType> minecraft) {
        return minecraftToBukkit(minecraft.value());
    }

    public static Holder<VillagePlaceType> bukkitToMinecraftHolder(PoiType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        IRegistry<VillagePlaceType> registry = CraftRegistry.getMinecraftRegistry(Registries.POINT_OF_INTEREST_TYPE);

        if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.c<VillagePlaceType> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own banner pattern without properly registering it.");
    }

}
