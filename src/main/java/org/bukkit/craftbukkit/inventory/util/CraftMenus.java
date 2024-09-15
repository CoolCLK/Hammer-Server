package org.bukkit.craftbukkit.inventory.util;

import java.util.function.Supplier;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.TileInventory;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.ContainerCartography;
import net.minecraft.world.inventory.ContainerEnchantTable;
import net.minecraft.world.inventory.ContainerGrindstone;
import net.minecraft.world.inventory.ContainerSmithing;
import net.minecraft.world.inventory.ContainerStonecutter;
import net.minecraft.world.inventory.ContainerWorkbench;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntityHopper;
import net.minecraft.world.level.block.entity.TileEntityLectern;
import net.minecraft.world.level.block.entity.TileEntitySmoker;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.inventory.view.builder.CraftLocationViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftMerchantViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftViewBuilder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.inventory.view.BeaconView;
import org.bukkit.inventory.view.BrewingStandView;
import org.bukkit.inventory.view.CrafterView;
import org.bukkit.inventory.view.EnchantmentView;
import org.bukkit.inventory.view.FurnaceView;
import org.bukkit.inventory.view.LecternView;
import org.bukkit.inventory.view.LoomView;
import org.bukkit.inventory.view.MerchantView;
import org.bukkit.inventory.view.StonecutterView;
import org.bukkit.inventory.view.builder.ViewBuilder;

import static org.bukkit.craftbukkit.inventory.util.CraftMenuBuilder.merchant;
import static org.bukkit.craftbukkit.inventory.util.CraftMenuBuilder.tileEntity;
import static org.bukkit.craftbukkit.inventory.util.CraftMenuBuilder.worldAccess;

public final class CraftMenus {

    public record MenuTypeData<V extends InventoryView, B extends ViewBuilder<V>>(Class<V> viewClass,
                                                                                  Supplier<B> viewBuilder) {
    }

    private static final CraftMenuBuilder STANDARD = (player, menuType) -> menuType.create(player.nextContainerCounter(), player.getInventory());

    public static <V extends InventoryView, B extends ViewBuilder<V>> MenuTypeData<V, B> getMenuTypeData(CraftMenuType<?, ?> menuType) {
        final Containers<?> handle = menuType.getHandle();
        // this isn't ideal as both dispenser and dropper are 3x3, InventoryType can't currently handle generic 3x3s with size 9
        // this needs to be removed when inventory creation is overhauled
        if (menuType == MenuType.GENERIC_3X3) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.DISPENSER))));
        }
        if (menuType == MenuType.CRAFTER_3X3) {
            return asType(new MenuTypeData<>(CrafterView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.CRAFTER))));
        }
        if (menuType == MenuType.ANVIL) {
            return asType(new MenuTypeData<>(AnvilView.class, () -> new CraftViewBuilder<>(handle, worldAccess(ContainerAnvil::new))));
        }
        if (menuType == MenuType.BEACON) {
            return asType(new MenuTypeData<>(BeaconView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.BEACON))));
        }
        if (menuType == MenuType.BLAST_FURNACE) {
            return asType(new MenuTypeData<>(FurnaceView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.BLAST_FURNACE))));
        }
        if (menuType == MenuType.BREWING_STAND) {
            return asType(new MenuTypeData<>(BrewingStandView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.BREWING_STAND))));
        }
        if (menuType == MenuType.CRAFTING) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftViewBuilder<>(handle, worldAccess(ContainerWorkbench::new))));
        }
        if (menuType == MenuType.ENCHANTMENT) {
            return asType(new MenuTypeData<>(EnchantmentView.class, () -> new CraftViewBuilder<>(handle, (player, type) -> {
                return new TileInventory((syncId, inventory, human) -> {
                    return worldAccess(ContainerEnchantTable::new).build(player, type);
                }, IChatBaseComponent.empty()).createMenu(player.nextContainerCounter(), player.getInventory(), player);
            })));
        }
        if (menuType == MenuType.FURNACE) {
            return asType(new MenuTypeData<>(FurnaceView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.FURNACE))));
        }
        if (menuType == MenuType.GRINDSTONE) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftViewBuilder<>(handle, worldAccess(ContainerGrindstone::new))));
        }
        // We really don't need to be creating a tile entity for hopper but currently InventoryType doesn't have capacity
        // to understand otherwise
        if (menuType == MenuType.HOPPER) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.HOPPER))));
        }
        // We also don't need to create a tile entity for lectern, but again InventoryType isn't smart enough to know any better
        if (menuType == MenuType.LECTERN) {
            return asType(new MenuTypeData<>(LecternView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.LECTERN))));
        }
        if (menuType == MenuType.LOOM) {
            return asType(new MenuTypeData<>(LoomView.class, () -> new CraftViewBuilder<>(handle, STANDARD)));
        }
        if (menuType == MenuType.MERCHANT) {
            return asType(new MenuTypeData<>(MerchantView.class, () -> new CraftViewBuilder<>(handle, new CraftMerchantViewBuilder(handle, merchant()))));
        }
        if (menuType == MenuType.SMITHING) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftViewBuilder<>(handle, worldAccess(ContainerSmithing::new))));
        }
        if (menuType == MenuType.SMOKER) {
            return asType(new MenuTypeData<>(FurnaceView.class, () -> new CraftLocationViewBuilder<>(handle, tileEntity(Blocks.SMOKER))));
        }
        if (menuType == MenuType.CARTOGRAPHY_TABLE) {
            return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftViewBuilder<>(handle,worldAccess(ContainerCartography::new))));
        }
        if (menuType == MenuType.STONECUTTER) {
            return asType(new MenuTypeData<>(StonecutterView.class, () -> new CraftViewBuilder<>(handle,worldAccess(ContainerStonecutter::new))));
        }

        return asType(new MenuTypeData<>(InventoryView.class, () -> new CraftViewBuilder<>(handle, STANDARD)));
    }

    private static <V extends InventoryView, B extends ViewBuilder<V>> MenuTypeData<V, B> asType(MenuTypeData<?, ?> data) {
        return (MenuTypeData<V, B>) data;
    }
}
