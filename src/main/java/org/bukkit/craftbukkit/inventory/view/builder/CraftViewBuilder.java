package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.util.CraftMenuBuilder;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.ViewBuilder;

public class CraftViewBuilder<V extends InventoryView, B extends CraftMenuBuilder> implements ViewBuilder<V> {

    private final Containers<?> handle;
    protected final B builder;

    protected boolean checkReachable = false;

    public CraftViewBuilder(Containers<?> handle, B builder) {
        this.handle = handle;
        this.builder = builder;
    }

    @Override
    public V build(final HumanEntity player, final String title) {
        Preconditions.checkArgument(player != null, "The given player must not be null");
        Preconditions.checkArgument(title != null, "The given title must not be null");
        Preconditions.checkArgument(player instanceof CraftHumanEntity, "The given player must be a CraftHumanEntity");
        final CraftHumanEntity craftHuman = (CraftHumanEntity) player;
        Preconditions.checkArgument(craftHuman.getHandle() instanceof EntityPlayer, "The given player must be an EntityPlayer");
        final EntityPlayer serverPlayer = (EntityPlayer) craftHuman.getHandle();
        final Container container = builder.build(serverPlayer, this.handle);
        container.setTitle(CraftChatMessage.fromString(title)[0]);
        container.checkReachable = this.checkReachable;
        return (V) container.getBukkitView();
    }
}
