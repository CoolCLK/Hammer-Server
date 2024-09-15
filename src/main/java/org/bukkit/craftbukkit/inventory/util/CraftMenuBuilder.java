package org.bukkit.craftbukkit.inventory.util;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.inventory.ITileEntityContainer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;

public interface CraftMenuBuilder {
    Container build(EntityPlayer player, Containers<?> type);

    static CraftMenuBuilder worldAccess(LocationBoundContainerBuilder builder) {
        return (EntityPlayer player, Containers<?> type) -> {
            return builder.build(player.nextContainerCounter(), player.getInventory(), ContainerAccess.create(player.level(), player.blockPosition()));
        };
    }
    static TileMenuBuilder tileEntity(Block block) {
        return new TileMenuBuilder().block(block);
    }

    interface LocationBoundContainerBuilder {
        Container build(int syncId, PlayerInventory inventory, ContainerAccess access);
    }

    class TileMenuBuilder implements CraftMenuBuilder {

        private Block block;
        public World world;
        public BlockPosition position;

        public TileMenuBuilder block(Block block) {
            this.block = block;
            return this;
        }

        @Override
        public Container build(final EntityPlayer player, final Containers<?> type) {
            final TileEntity entity = this.world.getBlockEntity(position);
            if (!(entity instanceof ITileEntityContainer container)) {
                return block.defaultBlockState().getMenuProvider(this.world, this.position).createMenu(player.nextContainerCounter(), player.getInventory(), player);
            }

            final Container atBlock = container.createMenu(player.nextContainerCounter(), player.getInventory(), player);
            if (atBlock.getType() != type) {
                return block.defaultBlockState().getMenuProvider(this.world, this.position).createMenu(player.nextContainerCounter(), player.getInventory(), player);
            }

            return atBlock;

        }
    }
}
