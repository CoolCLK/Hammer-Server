package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.ItemType;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaEntityTag extends CraftMetaItem {

    private static final Set<ItemType> ENTITY_TAGGABLE_ITEM_TYPES = Sets.newHashSet(
            ItemType.COD_BUCKET,
            ItemType.PUFFERFISH_BUCKET,
            ItemType.SALMON_BUCKET,
            ItemType.ITEM_FRAME,
            ItemType.GLOW_ITEM_FRAME,
            ItemType.PAINTING
    );

    static final ItemMetaKeyType<CustomData> ENTITY_TAG = new ItemMetaKeyType<>(DataComponents.ENTITY_DATA, "EntityTag", "entity-tag");
    NBTTagCompound entityTag;

    CraftMetaEntityTag(CraftMetaItem meta) {
        super(meta);

        if (!(meta instanceof CraftMetaEntityTag)) {
            return;
        }

        CraftMetaEntityTag entity = (CraftMetaEntityTag) meta;
        this.entityTag = entity.entityTag;
    }

    CraftMetaEntityTag(DataComponentPatch tag) {
        super(tag);

        getOrEmpty(tag, ENTITY_TAG).ifPresent((nbt) -> {
            entityTag = nbt.copyTag();
        });
    }

    CraftMetaEntityTag(Map<String, Object> map) {
        super(map);
    }

    @Override
    void deserializeInternal(NBTTagCompound tag, Object context) {
        super.deserializeInternal(tag, context);

        if (tag.contains(ENTITY_TAG.NBT)) {
            entityTag = tag.getCompound(ENTITY_TAG.NBT);
        }
    }

    @Override
    void serializeInternal(Map<String, NBTBase> internalTags) {
        if (entityTag != null && !entityTag.isEmpty()) {
            internalTags.put(ENTITY_TAG.NBT, entityTag);
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);

        if (entityTag != null) {
            tag.put(ENTITY_TAG, CustomData.of(entityTag));
        }
    }

    @Override
    boolean applicableTo(ItemType type) {
        return ENTITY_TAGGABLE_ITEM_TYPES.contains(type);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isEntityTagEmpty();
    }

    boolean isEntityTagEmpty() {
        return !(entityTag != null);
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaEntityTag) {
            CraftMetaEntityTag that = (CraftMetaEntityTag) meta;

            return entityTag != null ? that.entityTag != null && this.entityTag.equals(that.entityTag) : entityTag == null;
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaEntityTag || isEntityTagEmpty());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();

        if (entityTag != null) {
            hash = 73 * hash + entityTag.hashCode();
        }

        return original != hash ? CraftMetaEntityTag.class.hashCode() ^ hash : hash;
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        return builder;
    }

    @Override
    public CraftMetaEntityTag clone() {
        CraftMetaEntityTag clone = (CraftMetaEntityTag) super.clone();

        if (entityTag != null) {
            clone.entityTag = entityTag.copy();
        }

        return clone;
    }
}
