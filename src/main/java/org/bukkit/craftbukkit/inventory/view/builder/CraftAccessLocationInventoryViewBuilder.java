package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;

public class CraftAccessLocationInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> implements LocationInventoryViewBuilder<V> {

    private final CraftAccessContainerObjectBuilder containerBuilder;

    private Location location;

    public CraftAccessLocationInventoryViewBuilder(final Containers<?> handle, CraftAccessContainerObjectBuilder containerBuilder) {
        super(handle);
        this.containerBuilder = containerBuilder;
    }

    @Override
    public LocationInventoryViewBuilder<V> checkReachable(final boolean checkReachable) {
        this.checkReachable = checkReachable;
        return this;
    }

    @Override
    public LocationInventoryViewBuilder<V> location(final Location location) {
        Preconditions.checkArgument(location != null, "The provided location must not be null");
        Preconditions.checkArgument(location.getWorld() != null, "The provided location must be associated with a world");
        this.location = location;
        return this;
    }

    @Override
    protected Container buildContainer(final EntityPlayer player) {
        ContainerAccess access;
        if (location == null) {
            access = ContainerAccess.create(player.level(), player.blockPosition());
        } else {
            access = ContainerAccess.create(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toBlockPosition(location));
        }

        return this.containerBuilder.build(player.nextContainerCounter(), player.getInventory(), access);
    }

    @Override
    public CraftAccessLocationInventoryViewBuilder<V> copy() {
        CraftAccessLocationInventoryViewBuilder<V> copy = new CraftAccessLocationInventoryViewBuilder<>(this.handle, this.containerBuilder);
        copy.location = this.location;
        copy.checkReachable = super.checkReachable;
        return copy;
    }

    public interface CraftAccessContainerObjectBuilder {
        Container build(final int syncId, final PlayerInventory inventory, ContainerAccess access);
    }
}
