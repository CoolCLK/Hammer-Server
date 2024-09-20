package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.inventory.ITileEntityContainer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;

public class CraftBlockEntityInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> implements LocationInventoryViewBuilder<V> {

    private final Block block;

    private World world;
    private BlockPosition position;

    public CraftBlockEntityInventoryViewBuilder(final Containers<?> handle, final Block block) {
        super(handle);
        this.block = block;
    }

    @Override
    public LocationInventoryViewBuilder<V> checkReachable(final boolean checkReachable) {
        final CraftBlockEntityInventoryViewBuilder<V> copy = copy();
        copy.checkReachable = checkReachable;
        return copy;
    }

    @Override
    public LocationInventoryViewBuilder<V> location(final Location location) {
        Preconditions.checkArgument(location != null, "The provided location must not be null");
        Preconditions.checkArgument(location.getWorld() != null, "The provided location must be associated with a world");
        final CraftBlockEntityInventoryViewBuilder<V> copy = copy();
        copy.world = ((CraftWorld) location.getWorld()).getHandle();
        copy.position = CraftLocation.toBlockPosition(location);
        return copy;
    }

    @Override
    protected Container buildContainer(final EntityPlayer player) {
        final TileEntity entity = this.world.getBlockEntity(position);
        if (!(entity instanceof ITileEntityContainer container)) {
            return block.defaultBlockState().getMenuProvider(this.world, this.position).createMenu(player.nextContainerCounter(), player.getInventory(), player);
        }

        final Container atBlock = container.createMenu(player.nextContainerCounter(), player.getInventory(), player);
        if (atBlock.getType() != super.handle) {
            return block.defaultBlockState().getMenuProvider(this.world, this.position).createMenu(player.nextContainerCounter(), player.getInventory(), player);
        }

        return atBlock;
    }

    @Override
    protected CraftBlockEntityInventoryViewBuilder<V> copy() {
        final CraftBlockEntityInventoryViewBuilder<V> copy = new CraftBlockEntityInventoryViewBuilder<>(super.handle, this.block);
        copy.world = this.world;
        copy.position = this.position;
        copy.checkReachable = super.checkReachable;
        return copy;
    }
}
