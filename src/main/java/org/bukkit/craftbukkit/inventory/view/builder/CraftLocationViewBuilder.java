package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.util.CraftMenuBuilder;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationViewBuilder;
import org.jetbrains.annotations.NotNull;

public class CraftLocationViewBuilder<V extends InventoryView> extends CraftViewBuilder<V, CraftMenuBuilder.TileMenuBuilder> implements LocationViewBuilder<V> {

    public CraftLocationViewBuilder(final Containers<?> handle, final CraftMenuBuilder.TileMenuBuilder builder) {
        super(handle, builder);
    }

    @Override
    public LocationViewBuilder<V> checkReachable(final boolean checkReachable) {
        super.checkReachable = checkReachable;
        return this;
    }

    @Override
    public LocationViewBuilder<V> location(@NotNull final Location location) {
        Preconditions.checkArgument(location != null, "The provided location must not be null");
        builder.world = location.getWorld() == null ? ((CraftWorld) location.getWorld()).getHandle() : null;
        builder.position = CraftLocation.toBlockPosition(location);
        return this;
    }

    @Override
    public V build(final HumanEntity player, final String title) {
        if (builder.world == null) {
            builder.world = ((CraftWorld) player.getWorld()).getHandle();
        }

        if (builder.position == null) {
            builder.position = CraftLocation.toBlockPosition(player.getLocation());
        }
        return super.build(player, title);
    }
}
