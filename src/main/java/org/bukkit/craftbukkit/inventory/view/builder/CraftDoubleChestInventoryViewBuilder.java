package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockFinder;
import net.minecraft.world.level.block.entity.TileEntityChest;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;

public class CraftDoubleChestInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> implements LocationInventoryViewBuilder<V> {

    private World world;
    private BlockPosition position;

    public CraftDoubleChestInventoryViewBuilder(final Containers<?> handle) {
        super(handle);
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
        this.world = ((CraftWorld) location.getWorld()).getHandle();
        this.position = CraftLocation.toBlockPosition(location);
        return this;
    }

    @Override
    protected Container buildContainer(final EntityPlayer player) {
        if (this.world == null) {
            return handle.create(player.nextContainerCounter(), player.getInventory());
        }

        BlockChest chest = (BlockChest) Blocks.CHEST;
        final DoubleBlockFinder.Result<? extends TileEntityChest> result = chest.combine(this.world.getBlockState(this.position), this.world, this.position, false);
        System.out.println(result.getClass());

        final ITileInventory combined = result.apply(BlockChest.MENU_PROVIDER_COMBINER).orElse(null);
        if (combined == null) {
            return handle.create(player.nextContainerCounter(), player.getInventory());
        }
        return combined.createMenu(player.nextContainerCounter(), player.getInventory(), player);
    }

    @Override
    public CraftDoubleChestInventoryViewBuilder<V> copy() {
        final CraftDoubleChestInventoryViewBuilder<V> copy = new CraftDoubleChestInventoryViewBuilder<>(super.handle);
        copy.world = this.world;
        copy.position = this.position;
        copy.checkReachable = super.checkReachable;
        return copy;
    }
}
