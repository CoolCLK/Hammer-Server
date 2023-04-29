package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftMaterial;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftItemType implements ItemType {
    private final NamespacedKey key;
    private final Item item;
    private final String name;
    private final int ordinal;

    public CraftItemType(NamespacedKey key, Item item) {
        this.key = key;
        this.item = item;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive material specific values.
        // Custom materials will return the key with namespace. For a plugin this should look than like a new material
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = CraftMaterial.getNextOrdinal();
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return item.getMaxStackSize();
    }

    @Override
    public short getMaxDurability() {
        return (short) item.getMaxDamage();
    }

    @Override
    public BlockData createBlockData() {
        return Bukkit.createBlockData(this);
    }

    @Override
    public BlockData createBlockData(Consumer<BlockData> consumer) {
        return Bukkit.createBlockData(this, consumer);
    }

    @Override
    public BlockData createBlockData(String data) {
        return Bukkit.createBlockData(this, data);
    }

    @Override
    public Class<?> getBlockDataClass() {
        return MaterialData.class;
    }

    @Override
    public boolean isBlock() {
        return false;
    }

    @Override
    public boolean isEdible() {
        return item.isEdible();
    }

    @Override
    public boolean isRecord() {
        return item instanceof ItemRecord;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isAir() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return false;
    }

    @Override
    public boolean isFlammable() {
        return false;
    }

    @Override
    public boolean isBurnable() {
        return false;
    }

    @Override
    public boolean isFuel() {
        return TileEntityFurnace.isFuel(new net.minecraft.world.item.ItemStack(item));
    }

    @Override
    public boolean isOccluding() {
        return false;
    }

    @Override
    public boolean hasGravity() {
        return false;
    }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public ItemType asItemType() {
        return this;
    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public float getHardness() {
        throw new IllegalArgumentException("The Material is not an block!");
    }

    @Override
    public float getBlastResistance() {
        throw new IllegalArgumentException("The Material is not an block!");
    }

    @Override
    public float getSlipperiness() {
        throw new IllegalArgumentException("The Material is not an block!");
    }

    @Override
    public Material getCraftingRemainingItem() {
        Item expectedItem = item.getCraftingRemainingItem();
        return expectedItem == null ? null : CraftMagicNumbers.getMaterial(expectedItem);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return CraftEquipmentSlot.getSlot(EntityInsentient.getEquipmentSlotForItem(CraftItemStack.asNMSCopy(new ItemStack(this))));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> defaultAttributes = ImmutableMultimap.builder();

        Multimap<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> nmsDefaultAttributes = item.getDefaultAttributeModifiers(CraftEquipmentSlot.getNMS(equipmentSlot));
        for (Map.Entry<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> mapEntry : nmsDefaultAttributes.entries()) {
            Attribute attribute = CraftAttributeMap.fromMinecraft(CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.ATTRIBUTE).getKey(mapEntry.getKey()).toString());
            defaultAttributes.put(attribute, CraftAttributeInstance.convert(mapEntry.getValue(), equipmentSlot));
        }

        return defaultAttributes.build();
    }

    @Override
    public CreativeCategory getCreativeCategory() {
        return CreativeCategory.BUILDING_BLOCKS;
    }

    @Nullable
    @Override
    public String getItemTranslationKey() {
        return item.getDescriptionId();
    }

    @Override
    public int compareTo(Material material) {
        return ordinal - material.ordinal();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return item.getDescriptionId();
    }

    @Nullable
    @Override
    public String getBlockTranslationKey() {
        return null;
    }

    @Override
    public BlockType<?> asBlockType() {
        throw new UnsupportedOperationException("Material " + this + " is not a block");
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int getId() {
        throw new IllegalArgumentException("Cannot get ID of Modern Material");
    }

    @Override
    public Class<? extends MaterialData> getData() {
        throw new IllegalArgumentException("Cannot get data class of Modern Material");
    }

    @Override
    public MaterialData getNewData(byte b) {
        throw new IllegalArgumentException("Cannot get new data of Modern Material");
    }

    @Override
    public String toString() {
        // For backwards compatibility
        return name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftItemType)) {
            return false;
        }

        return getKey().equals(((CraftItemType) other).getKey());
    }

}
