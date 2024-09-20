package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerMerchant;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.item.trading.IMerchant;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.bukkit.craftbukkit.inventory.CraftMerchantCustom;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.view.builder.MerchantInventoryViewBuilder;

public class CraftMerchantInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> implements MerchantInventoryViewBuilder<V> {

    private IMerchant merchant;

    public CraftMerchantInventoryViewBuilder(final Containers<?> handle) {
        super(handle);
    }

    @Override
    public MerchantInventoryViewBuilder<V> merchant(final Merchant merchant) {
        final CraftMerchantInventoryViewBuilder<V> copy = copy();
        copy.merchant = ((CraftMerchant) merchant).getMerchant();
        return copy;
    }

    @Override
    protected Container buildContainer(final EntityPlayer player) {
        final IMerchant definedMerchant = merchant == null ? new CraftMerchantCustom("Custom Merchant").getMerchant() : merchant;
        return new ContainerMerchant(player.nextContainerCounter(), player.getInventory(), definedMerchant);
    }

    @Override
    protected CraftMerchantInventoryViewBuilder<V> copy() {
        CraftMerchantInventoryViewBuilder<V> copy = new CraftMerchantInventoryViewBuilder<>(super.handle);
        copy.checkReachable = super.checkReachable;
        copy.merchant = this.merchant;
        return copy;
    }
}
