package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.world.inventory.Containers;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.bukkit.craftbukkit.inventory.CraftMerchantCustom;
import org.bukkit.craftbukkit.inventory.util.CraftMenuBuilder.MerchantMenuBuilder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.view.builder.MerchantViewBuilder;
import org.jetbrains.annotations.NotNull;

public class CraftMerchantViewBuilder<V extends InventoryView> extends CraftViewBuilder<V, MerchantMenuBuilder> implements MerchantViewBuilder<V> {

    public CraftMerchantViewBuilder(final Containers<?> handle, final MerchantMenuBuilder builder) {
        super(handle, builder);
    }

    @Override
    public MerchantViewBuilder<V> merchant(@NotNull final Merchant merchant) {
        builder.merchant = ((CraftMerchant) merchant).getMerchant();
        return this;
    }

    @Override
    public V build(final HumanEntity player, final String title) {
        if (builder.merchant == null) {
            builder.merchant = new CraftMerchantCustom(title).getMerchant();
        }
        return super.build(player, title);
    }
}
