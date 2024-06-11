package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
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
import org.bukkit.craftbukkit.entity.CraftEntitySnapshot;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.SpawnEggMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaSpawnEgg extends CraftMetaItem implements SpawnEggMeta {

    private static final Set<ItemType> SPAWN_EGG_ITEM_TYPES = Sets.newHashSet(
            ItemType.ARMADILLO_SPAWN_EGG,
            ItemType.ALLAY_SPAWN_EGG,
            ItemType.AXOLOTL_SPAWN_EGG,
            ItemType.BAT_SPAWN_EGG,
            ItemType.BEE_SPAWN_EGG,
            ItemType.BLAZE_SPAWN_EGG,
            ItemType.BOGGED_SPAWN_EGG,
            ItemType.BREEZE_SPAWN_EGG,
            ItemType.CAT_SPAWN_EGG,
            ItemType.CAMEL_SPAWN_EGG,
            ItemType.CAVE_SPIDER_SPAWN_EGG,
            ItemType.CHICKEN_SPAWN_EGG,
            ItemType.COD_SPAWN_EGG,
            ItemType.COW_SPAWN_EGG,
            ItemType.CREEPER_SPAWN_EGG,
            ItemType.DOLPHIN_SPAWN_EGG,
            ItemType.DONKEY_SPAWN_EGG,
            ItemType.DROWNED_SPAWN_EGG,
            ItemType.ELDER_GUARDIAN_SPAWN_EGG,
            ItemType.ENDER_DRAGON_SPAWN_EGG,
            ItemType.ENDERMAN_SPAWN_EGG,
            ItemType.ENDERMITE_SPAWN_EGG,
            ItemType.EVOKER_SPAWN_EGG,
            ItemType.FOX_SPAWN_EGG,
            ItemType.FROG_SPAWN_EGG,
            ItemType.GHAST_SPAWN_EGG,
            ItemType.GLOW_SQUID_SPAWN_EGG,
            ItemType.GOAT_SPAWN_EGG,
            ItemType.GUARDIAN_SPAWN_EGG,
            ItemType.HOGLIN_SPAWN_EGG,
            ItemType.HORSE_SPAWN_EGG,
            ItemType.HUSK_SPAWN_EGG,
            ItemType.IRON_GOLEM_SPAWN_EGG,
            ItemType.LLAMA_SPAWN_EGG,
            ItemType.MAGMA_CUBE_SPAWN_EGG,
            ItemType.MOOSHROOM_SPAWN_EGG,
            ItemType.MULE_SPAWN_EGG,
            ItemType.OCELOT_SPAWN_EGG,
            ItemType.PANDA_SPAWN_EGG,
            ItemType.PARROT_SPAWN_EGG,
            ItemType.PHANTOM_SPAWN_EGG,
            ItemType.PIGLIN_BRUTE_SPAWN_EGG,
            ItemType.PIGLIN_SPAWN_EGG,
            ItemType.PIG_SPAWN_EGG,
            ItemType.PILLAGER_SPAWN_EGG,
            ItemType.POLAR_BEAR_SPAWN_EGG,
            ItemType.PUFFERFISH_SPAWN_EGG,
            ItemType.RABBIT_SPAWN_EGG,
            ItemType.RAVAGER_SPAWN_EGG,
            ItemType.SALMON_SPAWN_EGG,
            ItemType.SHEEP_SPAWN_EGG,
            ItemType.SHULKER_SPAWN_EGG,
            ItemType.SILVERFISH_SPAWN_EGG,
            ItemType.SKELETON_HORSE_SPAWN_EGG,
            ItemType.SKELETON_SPAWN_EGG,
            ItemType.SLIME_SPAWN_EGG,
            ItemType.SNIFFER_SPAWN_EGG,
            ItemType.SNOW_GOLEM_SPAWN_EGG,
            ItemType.SPIDER_SPAWN_EGG,
            ItemType.SQUID_SPAWN_EGG,
            ItemType.STRAY_SPAWN_EGG,
            ItemType.STRIDER_SPAWN_EGG,
            ItemType.TADPOLE_SPAWN_EGG,
            ItemType.TRADER_LLAMA_SPAWN_EGG,
            ItemType.TROPICAL_FISH_SPAWN_EGG,
            ItemType.TURTLE_SPAWN_EGG,
            ItemType.VEX_SPAWN_EGG,
            ItemType.VILLAGER_SPAWN_EGG,
            ItemType.VINDICATOR_SPAWN_EGG,
            ItemType.WARDEN_SPAWN_EGG,
            ItemType.WANDERING_TRADER_SPAWN_EGG,
            ItemType.WITCH_SPAWN_EGG,
            ItemType.WITHER_SPAWN_EGG,
            ItemType.WITHER_SKELETON_SPAWN_EGG,
            ItemType.WOLF_SPAWN_EGG,
            ItemType.ZOGLIN_SPAWN_EGG,
            ItemType.ZOMBIE_HORSE_SPAWN_EGG,
            ItemType.ZOMBIE_SPAWN_EGG,
            ItemType.ZOMBIE_VILLAGER_SPAWN_EGG,
            ItemType.ZOMBIFIED_PIGLIN_SPAWN_EGG
    );

    static final ItemMetaKeyType<CustomData> ENTITY_TAG = new ItemMetaKeyType<>(DataComponents.ENTITY_DATA, "entity-tag");
    @ItemMetaKey.Specific(ItemMetaKey.Specific.To.NBT)
    static final ItemMetaKey ENTITY_ID = new ItemMetaKey("id");

    private NBTTagCompound entityTag;

    CraftMetaSpawnEgg(CraftMetaItem meta) {
        super(meta);

        if (!(meta instanceof CraftMetaSpawnEgg egg)) {
            return;
        }

        this.entityTag = egg.entityTag;
    }

    CraftMetaSpawnEgg(DataComponentPatch tag) {
        super(tag);

        getOrEmpty(tag, ENTITY_TAG).ifPresent((nbt) -> {
            entityTag = nbt.copyTag();
        });
    }

    CraftMetaSpawnEgg(Map<String, Object> map) {
        super(map);
    }

    @Override
    void deserializeInternal(NBTTagCompound tag, Object context) {
        super.deserializeInternal(tag, context);

        if (tag.contains(ENTITY_TAG.NBT)) {
            entityTag = tag.getCompound(ENTITY_TAG.NBT);

            // Tag still has some other data, lets try our luck with a conversion
            if (!entityTag.isEmpty()) {
                // SPIGOT-4128: This is hopeless until we start versioning stacks. RIP data.
                // entityTag = (NBTTagCompound) MinecraftServer.getServer().dataConverterManager.update(DataConverterTypes.ENTITY, new Dynamic(DynamicOpsNBT.a, entityTag), -1, CraftMagicNumbers.DATA_VERSION).getValue();
            }
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
        return SPAWN_EGG_ITEM_TYPES.contains(type);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isSpawnEggEmpty();
    }

    boolean isSpawnEggEmpty() {
        return !(entityTag != null);
    }

    @Override
    public EntityType getSpawnedType() {
        throw new UnsupportedOperationException("Must check item type to get spawned type");
    }

    @Override
    public void setSpawnedType(EntityType type) {
        throw new UnsupportedOperationException("Must change item type to set spawned type");
    }

    @Override
    public EntitySnapshot getSpawnedEntity() {
        return CraftEntitySnapshot.create(this.entityTag);
    }

    @Override
    public void setSpawnedEntity(EntitySnapshot snapshot) {
        Preconditions.checkArgument(snapshot.getEntityType().isSpawnable(), "Entity is not spawnable");
        this.entityTag = ((CraftEntitySnapshot) snapshot).getData();
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaSpawnEgg) {
            CraftMetaSpawnEgg that = (CraftMetaSpawnEgg) meta;

            return entityTag != null ? that.entityTag != null && this.entityTag.equals(that.entityTag) : entityTag == null;
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaSpawnEgg || isSpawnEggEmpty());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();

        if (entityTag != null) {
            hash = 73 * hash + entityTag.hashCode();
        }

        return original != hash ? CraftMetaSpawnEgg.class.hashCode() ^ hash : hash;
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        return builder;
    }

    @Override
    public CraftMetaSpawnEgg clone() {
        CraftMetaSpawnEgg clone = (CraftMetaSpawnEgg) super.clone();

        if (entityTag != null) {
            clone.entityTag = entityTag.copy();
        }

        return clone;
    }
}
