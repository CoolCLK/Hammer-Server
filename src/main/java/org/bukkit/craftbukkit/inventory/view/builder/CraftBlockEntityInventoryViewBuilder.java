package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.inventory.ITileEntityContainer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;

public class CraftBlockEntityInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> implements LocationInventoryViewBuilder<V> {

    private final Block block;
    private final CraftTileInventoryBuilder builder;

    private World world;
    private BlockPosition position;

    public CraftBlockEntityInventoryViewBuilder(final Containers<?> handle, final Block block, final CraftTileInventoryBuilder builder) {
        super(handle);
        this.block = block;
        this.builder = builder;
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
            this.world = player.level();
        }

        if (this.position == null) {
            this.position = player.blockPosition();
        }

        final TileEntity entity = this.world.getBlockEntity(position);
        if (!(entity instanceof ITileEntityContainer container)) {
            return this.builder.build(this.position, this.block.defaultBlockState()).createMenu(player.nextContainerCounter(), player.getInventory(), player);
        }

        final Container atBlock = container.createMenu(player.nextContainerCounter(), player.getInventory(), player);
        if (atBlock.getType() != super.handle) {
            final ITileInventory inventory = this.builder.build(this.position, this.block.defaultBlockState());
            if (inventory instanceof TileEntity tile) {
                tile.setLevel(this.world);
            }
            return inventory.createMenu(player.nextContainerCounter(), player.getInventory(), player);
        }

        return atBlock;
    }

    @Override
    public CraftBlockEntityInventoryViewBuilder<V> copy() {
        final CraftBlockEntityInventoryViewBuilder<V> copy = new CraftBlockEntityInventoryViewBuilder<>(super.handle, this.block, this.builder);
        copy.world = this.world;
        copy.position = this.position;
        copy.checkReachable = super.checkReachable;
        return copy;
    }

    public interface CraftTileInventoryBuilder {
        ITileInventory build(BlockPosition blockPosition, IBlockData blockData);
    }
}
