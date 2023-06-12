package org.bukkit;

import net.minecraft.world.level.biome.BiomeBase;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class BiomeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraft() {
        for (Biome biome : Biome.values()) {
            if (biome == Biome.CUSTOM) {
                continue;
            }

            Assert.assertNotNull("No NMS mapping for " + biome, CraftBiome.bukkitToMinecraft(biome));
        }
    }

    @Test
    public void testMinecraftToBukkit() {
        for (BiomeBase biomeBase : BIOMES) {
            // Should always return a biome, since we create the biome from the biome base
            Biome biome = CraftBiome.minecraftToBukkit(biomeBase);
            Assert.assertTrue("No Bukkit mapping for " + biomeBase, biome != null && biome != Biome.CUSTOM);
        }
    }
}
