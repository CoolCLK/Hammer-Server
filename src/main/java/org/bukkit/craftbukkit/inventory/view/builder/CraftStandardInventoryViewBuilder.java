package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.inventory.InventoryView;

public class CraftStandardInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> {

    public CraftStandardInventoryViewBuilder(final Containers<?> handle) {
        super(handle);
    }

    @Override
    protected Container buildContainer(final EntityPlayer player) {
        return super.handle.create(player.nextContainerCounter(), player.getInventory());
    }

    @Override
    protected CraftStandardInventoryViewBuilder<V> copy() {
        return this;
    }
}
