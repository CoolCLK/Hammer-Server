package org.bukkit.craftbukkit.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.TagKey;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.enchantments.Enchantment;

public class CraftEnchantmentTag extends CraftTag<net.minecraft.world.item.enchantment.Enchantment, Enchantment> {

    public CraftEnchantmentTag(IRegistry<net.minecraft.world.item.enchantment.Enchantment> registry, TagKey<net.minecraft.world.item.enchantment.Enchantment> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(Enchantment enchantment) {
        return CraftEnchantment.bukkitToMinecraftHolder(enchantment).is(tag);
    }

    @Override
    public Set<Enchantment> getValues() {
        return getHandle().stream().map(Holder::value).map(CraftEnchantment::minecraftToBukkit).collect(Collectors.toUnmodifiableSet());
    }
}
