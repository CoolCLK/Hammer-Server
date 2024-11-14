package org.bukkit.craftbukkit.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.BiomeBase;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBiome;

public class CraftBiomeTag extends CraftTag<BiomeBase, Biome> {

    public CraftBiomeTag(IRegistry<BiomeBase> registry, TagKey<BiomeBase> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(Biome biome) {
        Holder<BiomeBase> holder = CraftBiome.bukkitToMinecraftHolder(biome);
        if (holder == null) {
            return false;
        }
        return holder.is(tag);
    }

    @Override
    public Set<Biome> getValues() {
        return getHandle().stream().map(Holder::value).map(CraftBiome::minecraftToBukkit).collect(Collectors.toUnmodifiableSet());
    }
}
