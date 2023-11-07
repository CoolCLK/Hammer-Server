package org.bukkit.craftbukkit.inventory.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.ContainerBeacon;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.inventory.ContainerDispenser;
import net.minecraft.world.inventory.ContainerEnchantTable;
import net.minecraft.world.inventory.ContainerGrindstone;
import net.minecraft.world.inventory.ContainerHopper;
import net.minecraft.world.inventory.ContainerWorkbench;
import net.minecraft.world.inventory.ITileEntityContainer;
import net.minecraft.world.inventory.InventoryCraftResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityBrewingStand;
import net.minecraft.world.level.block.entity.TileEntityContainer;
import net.minecraft.world.level.block.entity.TileEntityDispenser;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntityLectern;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.inventory.CraftInventoryBeacon;
import org.bukkit.craftbukkit.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryEnchanting;
import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.inventory.CraftInventoryGrindstone;
import org.bukkit.craftbukkit.inventory.CraftInventoryLectern;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.inventory.subcontainer.CraftChangeDetectingSubContainer;
import org.bukkit.craftbukkit.inventory.subcontainer.CraftTransientCraftingContainer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;


public class CraftInventoryBuilder {

    public static final CraftInventoryBuilder INSTANCE = new CraftInventoryBuilder();
    private final Map<MenuType<?>, InventoryBuilder> inventories;
    private final Map<MenuType<?>, VirtualContainerBuilder<?>> containers;

    private CraftInventoryBuilder() {
        this.inventories = new HashMap<>();
        this.containers = new HashMap<>();
        inventories.put(MenuType.GENERIC_9x1, InventoryBuilder.generic(1));
        containers.put(MenuType.GENERIC_9x1, VirtualContainerBuilder.generic(MenuType.GENERIC_9x1, 1));
        inventories.put(MenuType.GENERIC_9x2, InventoryBuilder.generic(2));
        containers.put(MenuType.GENERIC_9x2, VirtualContainerBuilder.generic(MenuType.GENERIC_9x2, 2));
        inventories.put(MenuType.GENERIC_9x3, InventoryBuilder.generic(3));
        containers.put(MenuType.GENERIC_9x3, VirtualContainerBuilder.generic(MenuType.GENERIC_9x3, 3));
        inventories.put(MenuType.GENERIC_9x4, InventoryBuilder.generic(4));
        containers.put(MenuType.GENERIC_9x4, VirtualContainerBuilder.generic(MenuType.GENERIC_9x4, 4));
        inventories.put(MenuType.GENERIC_9x5, InventoryBuilder.generic(5));
        containers.put(MenuType.GENERIC_9x5, VirtualContainerBuilder.generic(MenuType.GENERIC_9x5, 5));
        inventories.put(MenuType.GENERIC_9x6, InventoryBuilder.generic(6));
        containers.put(MenuType.GENERIC_9x6, VirtualContainerBuilder.generic(MenuType.GENERIC_9x6, 6));
        inventories.put(MenuType.GENERIC_3x3, InventoryBuilder.tile(CraftInventory::new, TileEntityDispenser::new, Blocks.DISPENSER));
        containers.put(MenuType.GENERIC_3x3, (int syncId, PlayerInventory playerinventory, CraftInventory inventory) -> new ContainerDispenser(syncId, playerinventory, inventory.getInventory()));
        inventories.put(MenuType.ANVIL, (holder, type) -> new CraftInventoryAnvil(null, new CraftChangeDetectingSubContainer(2, holder), new InventoryCraftResult()));
        containers.put(MenuType.ANVIL, (int syncId, PlayerInventory playerinventory, CraftInventory anvil) -> new ContainerAnvil(syncId, playerinventory, ContainerAccess.create(playerinventory.player.level(), playerinventory.player.blockPosition()), (CraftChangeDetectingSubContainer) anvil.getInventory(), (InventoryCraftResult) ((CraftInventoryAnvil) anvil).getResultInventory()));
        // no custom inventory needed here as a beacon won't work even if it is done. This minimizes dif
        inventories.put(MenuType.BEACON, (holder, type) -> new CraftInventoryBeacon(new InventorySubcontainer(1)));
        containers.put(MenuType.BEACON, (int syncId, PlayerInventory playerinventory, CraftInventory beacon) -> new ContainerBeacon(syncId, beacon.getInventory()));
        inventories.put(MenuType.BLAST_FURNACE, InventoryBuilder.tile(CraftInventoryFurnace::new, TileEntityBlastFurnace::new, Blocks.BLAST_FURNACE));
        containers.put(MenuType.BLAST_FURNACE, VirtualContainerBuilder.TILE);
        inventories.put(MenuType.BREWING_STAND, InventoryBuilder.tile(CraftInventoryBrewer::new, TileEntityBrewingStand::new, Blocks.BREWING_STAND));
        containers.put(MenuType.BREWING_STAND, VirtualContainerBuilder.TILE);
        inventories.put(MenuType.CRAFTING, (holder, type) -> new CraftInventoryCrafting(new CraftTransientCraftingContainer(3, 3), new InventoryCraftResult()));
        containers.put(MenuType.CRAFTING, (int syncId, PlayerInventory playerinventory, CraftInventory craft) -> new ContainerWorkbench(syncId, playerinventory, ContainerAccess.create(playerinventory.player.level(), playerinventory.player.blockPosition()), (CraftTransientCraftingContainer) craft.getInventory(), (InventoryCraftResult) ((CraftInventoryCrafting) craft).getResultInventory()));
        inventories.put(MenuType.ENCHANTMENT, (holder, type) -> new CraftInventoryEnchanting(new CraftChangeDetectingSubContainer(2, holder)));
        containers.put(MenuType.ENCHANTMENT, (int syncId, PlayerInventory playerinventory, CraftInventory enchanting) -> new ContainerEnchantTable(syncId, playerinventory, ContainerAccess.create(playerinventory.player.level(), playerinventory.player.blockPosition()), (InventorySubcontainer) enchanting.getInventory()));
        inventories.put(MenuType.FURNACE, InventoryBuilder.tile(CraftInventoryFurnace::new, TileEntityFurnaceFurnace::new, Blocks.FURNACE));
        containers.put(MenuType.FURNACE, VirtualContainerBuilder.TILE);
        inventories.put(MenuType.GRINDSTONE, (holder, type) -> new CraftInventoryGrindstone(new CraftChangeDetectingSubContainer(2, holder), new InventoryCraftResult()));
        containers.put(MenuType.GRINDSTONE, (int syncId, PlayerInventory playerinventory, CraftInventory grindstone) -> new ContainerGrindstone(syncId, playerinventory, ContainerAccess.create(playerinventory.player.level(), playerinventory.player.blockPosition()), (CraftChangeDetectingSubContainer) ((CraftInventoryGrindstone) grindstone).getInventory(), (InventoryCraftResult) ((CraftInventoryGrindstone) grindstone).getResultInventory()));
        inventories.put(MenuType.HOPPER, InventoryBuilder.generic(5, 1));
        containers.put(MenuType.HOPPER, (int syncId, PlayerInventory playerinventory, CraftInventory hopper) -> new ContainerHopper(syncId, playerinventory, hopper.getInventory()));
    }

    public Inventory createInventory(InventoryHolder holder, MenuType<?> type) {
        return inventories.get(type).createInventory(holder, type);
    }

    public InventoryView openInventoryCustom(HumanEntity entity, Inventory inventory, String title) {
        EntityPlayer player = (EntityPlayer) ((CraftHumanEntity) entity).getHandle();
        final CraftMenuType<?> menuType = (CraftMenuType<?>) CraftMenuType.getMenuType(inventory);
        final CraftInventory craft = (CraftInventory) inventory;
        final Container container = this.containers.get(menuType).createContainer(player.nextContainerCounter(), player.getInventory(), craft);
        container.setTitle(IChatBaseComponent.literal(title));

        player.connection.send(new PacketPlayOutOpenWindow(container.containerId, menuType.getHandle(), CraftChatMessage.fromString(title)[0]));
        player.containerMenu = container;
        player.initMenu(container);
        container.checkReachable = false;
        return container.getBukkitView();
    }

    interface InventoryBuilder {
        Inventory createInventory(InventoryHolder holder, MenuType<?> type);

        static InventoryBuilder generic(int columns, int rows) {
            return (holder, type) -> new CraftInventory(new InventorySubcontainer(rows * columns));
        }

        static InventoryBuilder generic(int rows) {
            return generic(9, rows);
        }

        static <I extends Inventory, T extends ITileEntityContainer> InventoryBuilder tile(Function<T, I> inventory, BiFunction<BlockPosition, IBlockData, T> tile, Block block) {
            return (holder, type) -> inventory.apply(tile.apply(BlockPosition.ZERO, block.defaultBlockState()));
        }
    }

    interface VirtualContainerBuilder<T extends Inventory> {
        VirtualContainerBuilder<CraftInventory> TILE = (int syncId, PlayerInventory playerinventory, CraftInventory inventory) -> ((ITileEntityContainer) inventory.getInventory()).createMenu(syncId, playerinventory, playerinventory.player);

        Container createContainer(int syncId, PlayerInventory playerinventory, CraftInventory inventory);

        static VirtualContainerBuilder<CraftInventory> generic(MenuType<?> type, int rows) {
            CraftMenuType<?> craft = (CraftMenuType<?>) type;
            return (int syncId, PlayerInventory playerinventory, CraftInventory inventory) -> new ContainerChest(craft.getHandle(), syncId, playerinventory, inventory.getInventory(), rows);
        }

    }

}
