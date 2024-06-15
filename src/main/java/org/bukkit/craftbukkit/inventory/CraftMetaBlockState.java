package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.TileEntity;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BlockStateMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBlockState extends CraftMetaItem implements BlockStateMeta {

    private static final Set<ItemType> SHULKER_BOX_ITEM_TYPES = Sets.newHashSet(
            ItemType.SHULKER_BOX,
            ItemType.WHITE_SHULKER_BOX,
            ItemType.ORANGE_SHULKER_BOX,
            ItemType.MAGENTA_SHULKER_BOX,
            ItemType.LIGHT_BLUE_SHULKER_BOX,
            ItemType.YELLOW_SHULKER_BOX,
            ItemType.LIME_SHULKER_BOX,
            ItemType.PINK_SHULKER_BOX,
            ItemType.GRAY_SHULKER_BOX,
            ItemType.LIGHT_GRAY_SHULKER_BOX,
            ItemType.CYAN_SHULKER_BOX,
            ItemType.PURPLE_SHULKER_BOX,
            ItemType.BLUE_SHULKER_BOX,
            ItemType.BROWN_SHULKER_BOX,
            ItemType.GREEN_SHULKER_BOX,
            ItemType.RED_SHULKER_BOX,
            ItemType.BLACK_SHULKER_BOX
    );

    @ItemMetaKey.Specific(ItemMetaKey.Specific.To.NBT)
    static final ItemMetaKeyType<CustomData> BLOCK_ENTITY_TAG = new ItemMetaKeyType<>(DataComponents.BLOCK_ENTITY_DATA, "BlockEntityTag");

    final ItemType itemType;
    private CraftBlockEntityState<?> blockEntityTag;
    private NBTTagCompound internalTag;

    CraftMetaBlockState(CraftMetaItem meta, ItemType itemType) {
        super(meta);
        this.itemType = itemType;

        if (!(meta instanceof CraftMetaBlockState)
                || ((CraftMetaBlockState) meta).itemType != itemType) {
            blockEntityTag = null;
            return;
        }

        CraftMetaBlockState te = (CraftMetaBlockState) meta;
        this.blockEntityTag = te.blockEntityTag;
    }

    CraftMetaBlockState(DataComponentPatch tag, ItemType itemType) {
        super(tag);
        this.itemType = itemType;

        getOrEmpty(tag, BLOCK_ENTITY_TAG).ifPresent((nbt) -> {
            blockEntityTag = getBlockState(itemType, nbt.copyTag());
        });

        if (!tag.isEmpty()) {
            CraftBlockEntityState<?> blockEntityTag = this.blockEntityTag;
            if (blockEntityTag == null) {
                blockEntityTag = getBlockState(itemType, null);
            }

            // Convert to map
            PatchedDataComponentMap map = new PatchedDataComponentMap(DataComponentMap.EMPTY);
            map.applyPatch(tag);
            // Apply
            Set<DataComponentType<?>> applied = blockEntityTag.applyComponents(map, tag);
            // Mark applied components as handled
            for (DataComponentType<?> seen : applied) {
                unhandledTags.clear(seen);
            }
            // Only set blockEntityTag if something was applied
            if (!applied.isEmpty()) {
                this.blockEntityTag = blockEntityTag;
            }
        }
    }

    CraftMetaBlockState(Map<String, Object> map) {
        super(map);
        String matName = SerializableMeta.getString(map, "blockMaterial", true);
        Material m = Material.getMaterial(matName);
        if (m != null) {
            itemType = m.asItemType();
        } else {
            ItemType type = Registry.ITEM.get(NamespacedKey.fromString(matName));
            if (type != null) {
                itemType = type;
            } else {
                itemType = ItemType.AIR;
            }
        }
        blockEntityTag = getBlockState(itemType, internalTag);
        internalTag = null;
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);

        if (blockEntityTag != null) {
            tag.put(BLOCK_ENTITY_TAG, CustomData.of(blockEntityTag.getSnapshotNBTWithoutComponents()));

            for (TypedDataComponent<?> component : blockEntityTag.collectComponents()) {
                tag.putIfAbsent(component);
            }
        }
    }

    @Override
    void deserializeInternal(NBTTagCompound tag, Object context) {
        super.deserializeInternal(tag, context);

        if (tag.contains(BLOCK_ENTITY_TAG.NBT, CraftMagicNumbers.NBT.TAG_COMPOUND)) {
            internalTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT);
        }
    }

    @Override
    void serializeInternal(final Map<String, NBTBase> internalTags) {
        if (blockEntityTag != null) {
            internalTags.put(BLOCK_ENTITY_TAG.NBT, blockEntityTag.getSnapshotNBT());
        }
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        builder.put("blockMaterial", itemType.getKey());
        return builder;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (blockEntityTag != null) {
            hash = 61 * hash + this.blockEntityTag.hashCode();
        }
        return original != hash ? CraftMetaBlockState.class.hashCode() ^ hash : hash;
    }

    @Override
    public boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBlockState) {
            CraftMetaBlockState that = (CraftMetaBlockState) meta;

            return Objects.equal(this.blockEntityTag, that.blockEntityTag);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBlockState || blockEntityTag == null);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && blockEntityTag == null;
    }

    @Override
    public CraftMetaBlockState clone() {
        CraftMetaBlockState meta = (CraftMetaBlockState) super.clone();
        if (blockEntityTag != null) {
            meta.blockEntityTag = blockEntityTag.copy();
        }
        return meta;
    }

    @Override
    public boolean hasBlockState() {
        return blockEntityTag != null;
    }

    @Override
    public BlockState getBlockState() {
        return (blockEntityTag != null) ? blockEntityTag.copy() : getBlockState(itemType, null);
    }

    private static CraftBlockEntityState<?> getBlockState(ItemType itemType, NBTTagCompound blockEntityTag) {
        BlockPosition pos = BlockPosition.ZERO;
        ItemType stateType = (itemType != ItemType.SHIELD) ? itemType : shieldToBannerHack(); // Only actually used for jigsaws
        if (blockEntityTag != null) {
            if (itemType == ItemType.SHIELD) {
                blockEntityTag.putString("id", "minecraft:banner");
            } else if (itemType == ItemType.BEE_NEST || itemType == ItemType.BEEHIVE) {
                blockEntityTag.putString("id", "minecraft:beehive");
            } else if (SHULKER_BOX_ITEM_TYPES.contains(itemType)) {
                blockEntityTag.putString("id", "minecraft:shulker_box");
            }

            pos = TileEntity.getPosFromTag(blockEntityTag);
        }

        // This is expected to always return a CraftBlockEntityState for the passed material:
        return (CraftBlockEntityState<?>) CraftBlockStates.getBlockState(pos, stateType.getBlockType(), blockEntityTag);
    }

    @Override
    public void setBlockState(BlockState blockState) {
        Preconditions.checkArgument(blockState != null, "blockState must not be null");

        ItemType stateType = (itemType != ItemType.SHIELD) ? itemType : shieldToBannerHack();
        Class<?> blockStateType = CraftBlockStates.getBlockStateType(stateType.getBlockType());
        Preconditions.checkArgument(blockStateType == blockState.getClass() && blockState instanceof CraftBlockEntityState, "Invalid blockState for " + itemType);

        this.blockEntityTag = (CraftBlockEntityState<?>) blockState;
    }

    private static ItemType shieldToBannerHack() {
        return ItemType.WHITE_BANNER;
    }
}
