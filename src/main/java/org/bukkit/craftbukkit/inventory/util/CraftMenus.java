package org.bukkit.craftbukkit.inventory.util;

import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.ContainerCartography;
import net.minecraft.world.inventory.ContainerGrindstone;
import net.minecraft.world.inventory.ContainerSmithing;
import net.minecraft.world.inventory.ContainerStonecutter;
import net.minecraft.world.inventory.ContainerWorkbench;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.inventory.view.builder.CraftAccessLocationInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftBlockEntityInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftMerchantInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftStandardInventoryViewBuilder;
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
import org.bukkit.inventory.view.builder.InventoryViewBuilder;

public final class CraftMenus {

    public record MenuTypeData<V extends InventoryView, B extends InventoryViewBuilder<V>>(Class<V> viewClass, B viewBuilder) {
    }

    public static <V extends InventoryView, B extends InventoryViewBuilder<V>> MenuTypeData<V, B> getMenuTypeData(CraftMenuType<?, ?> menuType) {
        final Containers<?> handle = menuType.getHandle();
        // this isn't ideal as both dispenser and dropper are 3x3, InventoryType can't currently handle generic 3x3s with size 9
        // this needs to be removed when inventory creation is overhauled
        if (menuType == MenuType.GENERIC_3X3) {
            return asType(new MenuTypeData<>(InventoryView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.DISPENSER)));
        }
        if (menuType == MenuType.CRAFTER_3X3) {
            return asType(new MenuTypeData<>(CrafterView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.CRAFTER)));
        }
        if (menuType == MenuType.ANVIL) {
            return asType(new MenuTypeData<>(AnvilView.class, new CraftAccessLocationInventoryViewBuilder<>(handle, ContainerAnvil::new)));
        }
        if (menuType == MenuType.BEACON) {
            return asType(new MenuTypeData<>(BeaconView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.BEACON)));
        }
        if (menuType == MenuType.BLAST_FURNACE) {
            return asType(new MenuTypeData<>(FurnaceView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.BLAST_FURNACE)));
        }
        if (menuType == MenuType.BREWING_STAND) {
            return asType(new MenuTypeData<>(BrewingStandView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.BREWING_STAND)));
        }
        if (menuType == MenuType.CRAFTING) {
            return asType(new MenuTypeData<>(InventoryView.class, new CraftAccessLocationInventoryViewBuilder<>(handle, ContainerWorkbench::new)));
        }
        if (menuType == MenuType.ENCHANTMENT) {
            return asType(new MenuTypeData<>(EnchantmentView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.ENCHANTING_TABLE)));
        }
        if (menuType == MenuType.FURNACE) {
            return asType(new MenuTypeData<>(FurnaceView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.FURNACE)));
        }
        if (menuType == MenuType.GRINDSTONE) {
            return asType(new MenuTypeData<>(InventoryView.class, new CraftAccessLocationInventoryViewBuilder<>(handle, ContainerGrindstone::new)));
        }
        // We really don't need to be creating a tile entity for hopper but currently InventoryType doesn't have capacity
        // to understand otherwise
        if (menuType == MenuType.HOPPER) {
            return asType(new MenuTypeData<>(InventoryView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.HOPPER)));
        }
        // We also don't need to create a tile entity for lectern, but again InventoryType isn't smart enough to know any better
        if (menuType == MenuType.LECTERN) {
            return asType(new MenuTypeData<>(LecternView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.LECTERN)));
        }
        if (menuType == MenuType.LOOM) {
            return asType(new MenuTypeData<>(LoomView.class, new CraftStandardInventoryViewBuilder<>(handle)));
        }
        if (menuType == MenuType.MERCHANT) {
            return asType(new MenuTypeData<>(MerchantView.class, new CraftMerchantInventoryViewBuilder<>(handle)));
        }
        if (menuType == MenuType.SMITHING) {
            return asType(new MenuTypeData<>(InventoryView.class, new CraftAccessLocationInventoryViewBuilder<>(handle, ContainerSmithing::new)));
        }
        if (menuType == MenuType.SMOKER) {
            return asType(new MenuTypeData<>(FurnaceView.class, new CraftBlockEntityInventoryViewBuilder<>(handle, Blocks.SMOKER)));
        }
        if (menuType == MenuType.CARTOGRAPHY_TABLE) {
            return asType(new MenuTypeData<>(InventoryView.class, new CraftAccessLocationInventoryViewBuilder<>(handle, ContainerCartography::new)));
        }
        if (menuType == MenuType.STONECUTTER) {
            return asType(new MenuTypeData<>(StonecutterView.class, new CraftAccessLocationInventoryViewBuilder<>(handle, ContainerStonecutter::new)));
        }

        return asType(new MenuTypeData<>(InventoryView.class, new CraftStandardInventoryViewBuilder<>(handle)));
    }

    private static <V extends InventoryView, B extends InventoryViewBuilder<V>> MenuTypeData<V, B> asType(MenuTypeData<?, ?> data) {
        return (MenuTypeData<V, B>) data;
    }
}
