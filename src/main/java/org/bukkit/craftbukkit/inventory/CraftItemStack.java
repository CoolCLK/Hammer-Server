package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.bukkit.Tag;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

@DelegateDeserialization(ItemStack.class)
public final class CraftItemStack extends ItemStack {

    public static net.minecraft.world.item.ItemStack asNMSCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return stack.handle == null ? net.minecraft.world.item.ItemStack.EMPTY : stack.handle.copy();
        }
        if (original == null || original.getType() == ItemType.AIR) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        Item item = CraftItemType.bukkitToMinecraft(original.getType());

        if (item == null) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item, original.getAmount());
        if (original.hasItemMeta()) {
            setItemMeta(stack, original.getItemMeta());
        }
        return stack;
    }

    public static net.minecraft.world.item.ItemStack copyNMSStack(net.minecraft.world.item.ItemStack original, int amount) {
        net.minecraft.world.item.ItemStack stack = original.copy();
        stack.setCount(amount);
        return stack;
    }

    /**
     * Copies the NMS stack to return as a strictly-Bukkit stack
     */
    public static ItemStack asBukkitCopy(net.minecraft.world.item.ItemStack original) {
        if (original.isEmpty()) {
            return ItemStack.of(ItemType.AIR);
        }
        ItemStack stack = ItemStack.of(CraftItemType.minecraftToBukkit(original.getItem()), original.getCount());
        if (hasItemMeta(original)) {
            stack.setItemMeta(getItemMeta(original));
        }
        return stack;
    }

    public static CraftItemStack asCraftMirror(net.minecraft.world.item.ItemStack original) {
        return new CraftItemStack((original == null || original.isEmpty()) ? null : original);
    }

    public static CraftItemStack asCraftCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return new CraftItemStack(stack.handle == null ? null : stack.handle.copy());
        }
        return new CraftItemStack(original);
    }

    public static CraftItemStack asNewCraftStack(Item item) {
        return asNewCraftStack(item, 1);
    }

    public static CraftItemStack asNewCraftStack(Item item, int amount) {
        return new CraftItemStack(CraftItemType.minecraftToBukkit(item), amount, (short) 0, null);
    }

    net.minecraft.world.item.ItemStack handle;

    /**
     * Mirror
     */
    private CraftItemStack(net.minecraft.world.item.ItemStack item) {
        this.handle = item;
    }

    private CraftItemStack(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability(), item.hasItemMeta() ? item.getItemMeta() : null);
    }

    private CraftItemStack(ItemType type, int amount, short durability, ItemMeta itemMeta) {
        setType(type);
        setAmount(amount);
        setDurability(durability);
        setItemMeta(itemMeta);
    }

    @Override
    public MaterialData getData() {
        return handle != null ? CraftMagicNumbers.getMaterialData(handle.getItem()) : super.getData();
    }

    @Override
    public ItemType getType() {
        return handle != null ? CraftItemType.minecraftToBukkit(handle.getItem()) : ItemType.AIR;
    }

    @Override
    public void setType(ItemType type) {
        if (getType() == type) {
            return;
        } else if (type == ItemType.AIR) {
            handle = null;
        } else if (CraftItemType.bukkitToMinecraft(type) == null) { // :(  --Smile again, with the new system this should never happen
            handle = null;
        } else if (handle == null) {
            handle = new net.minecraft.world.item.ItemStack(CraftItemType.bukkitToMinecraft(type), 1);
        } else {
            handle.setItem(CraftItemType.bukkitToMinecraft(type));
            if (hasItemMeta()) {
                // This will create the appropriate item meta, which will contain all the data we intend to keep
                setItemMeta(handle, getItemMeta(handle));
            }
        }
        setData(null);
    }

    @Override
    public int getAmount() {
        return handle != null ? handle.getCount() : 0;
    }

    @Override
    public void setAmount(int amount) {
        if (handle == null) {
            return;
        }

        handle.setCount(amount);
        if (amount == 0) {
            handle = null;
        }
    }

    @Override
    public void setDurability(final short durability) {
        // Ignore damage if item is null
        if (handle != null) {
            handle.setDamageValue(durability);
        }
    }

    @Override
    public short getDurability() {
        if (handle != null) {
            return (short) handle.getDamageValue();
        } else {
            return -1;
        }
    }

    @Override
    public int getMaxStackSize() {
        return (handle == null) ? ItemType.AIR.getMaxStackSize() : handle.getMaxStackSize();
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
        Preconditions.checkArgument(ench != null, "Enchantment cannot be null");

        if (!makeTag(handle)) {
            return;
        }
        ItemEnchantments list = getEnchantmentList(handle);
        if (list == null) {
            list = ItemEnchantments.EMPTY;
        }
        ItemEnchantments.a listCopy = new ItemEnchantments.a(list);
        listCopy.set(CraftEnchantment.bukkitToMinecraft(ench), level);
        handle.set(DataComponents.ENCHANTMENTS, listCopy.toImmutable());
    }

    static boolean makeTag(net.minecraft.world.item.ItemStack item) {
        if (item == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean containsEnchantment(Enchantment ench) {
        return getEnchantmentLevel(ench) > 0;
    }

    @Override
    public int getEnchantmentLevel(Enchantment ench) {
        Preconditions.checkArgument(ench != null, "Enchantment cannot be null");
        if (handle == null) {
            return 0;
        }
        return EnchantmentManager.getItemEnchantmentLevel(CraftEnchantment.bukkitToMinecraft(ench), handle);
    }

    @Override
    public int removeEnchantment(Enchantment ench) {
        Preconditions.checkArgument(ench != null, "Enchantment cannot be null");

        ItemEnchantments list = getEnchantmentList(handle);
        if (list == null) {
            return 0;
        }
        int level = getEnchantmentLevel(ench);
        if (level <= 0) {
            return 0;
        }
        int size = list.size();

        if (size == 1) {
            handle.remove(DataComponents.ENCHANTMENTS);
            return level;
        }

        ItemEnchantments.a listCopy = new ItemEnchantments.a(list);
        listCopy.set(CraftEnchantment.bukkitToMinecraft(ench), -1); // Negative to remove
        handle.set(DataComponents.ENCHANTMENTS, listCopy.toImmutable());

        return level;
    }

    @Override
    public void removeEnchantments() {
        handle.remove(DataComponents.ENCHANTMENTS);
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return getEnchantments(handle);
    }

    static Map<Enchantment, Integer> getEnchantments(net.minecraft.world.item.ItemStack item) {
        ItemEnchantments list = (item != null && item.isEnchanted()) ? item.get(DataComponents.ENCHANTMENTS) : null;

        if (list == null || list.size() == 0) {
            return ImmutableMap.of();
        }

        ImmutableMap.Builder<Enchantment, Integer> result = ImmutableMap.builder();

        list.entrySet().forEach((entry) -> {
            Holder<net.minecraft.world.item.enchantment.Enchantment> id = entry.getKey();
            int level = entry.getIntValue();

            Enchantment enchant = CraftEnchantment.minecraftHolderToBukkit(id);
            if (enchant != null) {
                result.put(enchant, level);
            }
        });

        return result.build();
    }

    static ItemEnchantments getEnchantmentList(net.minecraft.world.item.ItemStack item) {
        return (item != null && item.isEnchanted()) ? item.get(DataComponents.ENCHANTMENTS) : null;
    }

    @Override
    public CraftItemStack clone() {
        CraftItemStack itemStack = (CraftItemStack) super.clone();
        if (this.handle != null) {
            itemStack.handle = this.handle.copy();
        }
        return itemStack;
    }

    @Override
    public ItemMeta getItemMeta() {
        return getItemMeta(handle);
    }

    public static ItemMeta getItemMeta(net.minecraft.world.item.ItemStack item) {
        if (!hasItemMeta(item)) {
            return CraftItemFactory.instance().getItemMeta(getType(item));
        }

        ItemType type = getType(item);
        if (type == ItemType.WRITTEN_BOOK) {
            return new CraftMetaBookSigned(item.getComponentsPatch());
        }
        if (type == ItemType.WRITABLE_BOOK) {
            return new CraftMetaBook(item.getComponentsPatch());
        }
        if (type == ItemType.CREEPER_HEAD || type == ItemType.DRAGON_HEAD
                || type == ItemType.PIGLIN_HEAD || type == ItemType.PLAYER_HEAD
                || type == ItemType.SKELETON_SKULL || type == ItemType.WITHER_SKELETON_SKULL
                || type == ItemType.ZOMBIE_HEAD) {
            return new CraftMetaSkull(item.getComponentsPatch());
        }
        if (type == ItemType.CHAINMAIL_HELMET || type == ItemType.CHAINMAIL_CHESTPLATE
                || type == ItemType.CHAINMAIL_LEGGINGS || type == ItemType.CHAINMAIL_BOOTS
                || type == ItemType.DIAMOND_HELMET || type == ItemType.DIAMOND_CHESTPLATE
                || type == ItemType.DIAMOND_LEGGINGS || type == ItemType.DIAMOND_BOOTS
                || type == ItemType.GOLDEN_HELMET || type == ItemType.GOLDEN_CHESTPLATE
                || type == ItemType.GOLDEN_LEGGINGS || type == ItemType.GOLDEN_BOOTS
                || type == ItemType.IRON_HELMET || type == ItemType.IRON_CHESTPLATE
                || type == ItemType.IRON_LEGGINGS || type == ItemType.IRON_BOOTS
                || type == ItemType.NETHERITE_HELMET || type == ItemType.NETHERITE_CHESTPLATE
                || type == ItemType.NETHERITE_LEGGINGS || type == ItemType.NETHERITE_BOOTS
                || type == ItemType.TURTLE_HELMET) {
            return new CraftMetaArmor(item.getComponentsPatch());
        }
        if (type == ItemType.LEATHER_HELMET || type == ItemType.LEATHER_CHESTPLATE
                || type == ItemType.LEATHER_LEGGINGS || type == ItemType.LEATHER_BOOTS
                || type == ItemType.WOLF_ARMOR) {
            return new CraftMetaColorableArmor(item.getComponentsPatch());
        }
        if (type == ItemType.LEATHER_HORSE_ARMOR) {
            return new CraftMetaLeatherArmor(item.getComponentsPatch());
        }
        if (type == ItemType.POTION || type == ItemType.SPLASH_POTION
                || type == ItemType.LINGERING_POTION || type == ItemType.TIPPED_ARROW) {
            return new CraftMetaPotion(item.getComponentsPatch());
        }
        if (type == ItemType.FILLED_MAP) {
            return new CraftMetaMap(item.getComponentsPatch());
        }
        if (type == ItemType.FIREWORK_ROCKET) {
            return new CraftMetaFirework(item.getComponentsPatch());
        }
        if (type == ItemType.FIREWORK_STAR) {
            return new CraftMetaCharge(item.getComponentsPatch());
        }
        if (type == ItemType.ENCHANTED_BOOK) {
            return new CraftMetaEnchantedBook(item.getComponentsPatch());
        }
        if (type.hasBlockType() && Tag.BANNERS.isTagged(type.getBlockType())) {
            return new CraftMetaBanner(item.getComponentsPatch());
        }
        if (type == ItemType.ARMADILLO_SPAWN_EGG || type == ItemType.ALLAY_SPAWN_EGG
                || type == ItemType.AXOLOTL_SPAWN_EGG || type == ItemType.BAT_SPAWN_EGG
                || type == ItemType.BEE_SPAWN_EGG || type == ItemType.BLAZE_SPAWN_EGG
                || type == ItemType.BOGGED_SPAWN_EGG || type == ItemType.BREEZE_SPAWN_EGG
                || type == ItemType.CAT_SPAWN_EGG || type == ItemType.CAMEL_SPAWN_EGG
                || type == ItemType.CAVE_SPIDER_SPAWN_EGG || type == ItemType.CHICKEN_SPAWN_EGG
                || type == ItemType.COD_SPAWN_EGG || type == ItemType.COW_SPAWN_EGG
                || type == ItemType.CREEPER_SPAWN_EGG || type == ItemType.DOLPHIN_SPAWN_EGG
                || type == ItemType.DONKEY_SPAWN_EGG || type == ItemType.DROWNED_SPAWN_EGG
                || type == ItemType.ELDER_GUARDIAN_SPAWN_EGG || type == ItemType.ENDER_DRAGON_SPAWN_EGG
                || type == ItemType.ENDERMAN_SPAWN_EGG || type == ItemType.ENDERMITE_SPAWN_EGG
                || type == ItemType.EVOKER_SPAWN_EGG || type == ItemType.FOX_SPAWN_EGG
                || type == ItemType.FROG_SPAWN_EGG || type == ItemType.GHAST_SPAWN_EGG
                || type == ItemType.GLOW_SQUID_SPAWN_EGG || type == ItemType.GOAT_SPAWN_EGG
                || type == ItemType.GUARDIAN_SPAWN_EGG || type == ItemType.HOGLIN_SPAWN_EGG
                || type == ItemType.HORSE_SPAWN_EGG || type == ItemType.HUSK_SPAWN_EGG
                || type == ItemType.IRON_GOLEM_SPAWN_EGG || type == ItemType.LLAMA_SPAWN_EGG
                || type == ItemType.MAGMA_CUBE_SPAWN_EGG || type == ItemType.MOOSHROOM_SPAWN_EGG
                || type == ItemType.MULE_SPAWN_EGG || type == ItemType.OCELOT_SPAWN_EGG
                || type == ItemType.PANDA_SPAWN_EGG || type == ItemType.PARROT_SPAWN_EGG
                || type == ItemType.PHANTOM_SPAWN_EGG || type == ItemType.PIGLIN_BRUTE_SPAWN_EGG
                || type == ItemType.PIGLIN_SPAWN_EGG || type == ItemType.PIG_SPAWN_EGG
                || type == ItemType.PILLAGER_SPAWN_EGG || type == ItemType.POLAR_BEAR_SPAWN_EGG
                || type == ItemType.PUFFERFISH_SPAWN_EGG || type == ItemType.RABBIT_SPAWN_EGG
                || type == ItemType.RAVAGER_SPAWN_EGG || type == ItemType.SALMON_SPAWN_EGG
                || type == ItemType.SHEEP_SPAWN_EGG || type == ItemType.SHULKER_SPAWN_EGG
                || type == ItemType.SILVERFISH_SPAWN_EGG || type == ItemType.SKELETON_HORSE_SPAWN_EGG
                || type == ItemType.SKELETON_SPAWN_EGG || type == ItemType.SLIME_SPAWN_EGG
                || type == ItemType.SNIFFER_SPAWN_EGG || type == ItemType.SNOW_GOLEM_SPAWN_EGG
                || type == ItemType.SPIDER_SPAWN_EGG || type == ItemType.SQUID_SPAWN_EGG
                || type == ItemType.STRAY_SPAWN_EGG || type == ItemType.STRIDER_SPAWN_EGG
                || type == ItemType.TADPOLE_SPAWN_EGG || type == ItemType.TRADER_LLAMA_SPAWN_EGG
                || type == ItemType.TROPICAL_FISH_SPAWN_EGG || type == ItemType.TURTLE_SPAWN_EGG
                || type == ItemType.VEX_SPAWN_EGG || type == ItemType.VILLAGER_SPAWN_EGG
                || type == ItemType.VINDICATOR_SPAWN_EGG || type == ItemType.WANDERING_TRADER_SPAWN_EGG
                || type == ItemType.WARDEN_SPAWN_EGG || type == ItemType.WITCH_SPAWN_EGG
                || type == ItemType.WITHER_SKELETON_SPAWN_EGG || type == ItemType.WITHER_SPAWN_EGG
                || type == ItemType.WOLF_SPAWN_EGG || type == ItemType.ZOGLIN_SPAWN_EGG
                || type == ItemType.ZOMBIE_HORSE_SPAWN_EGG || type == ItemType.ZOMBIE_SPAWN_EGG
                || type == ItemType.ZOMBIE_VILLAGER_SPAWN_EGG || type == ItemType.ZOMBIFIED_PIGLIN_SPAWN_EGG) {
            return new CraftMetaSpawnEgg(item.getComponentsPatch());
        }
        if (type == ItemType.ARMOR_STAND) {
            return new CraftMetaArmorStand(item.getComponentsPatch());
        }
        if (type == ItemType.KNOWLEDGE_BOOK) {
            return new CraftMetaKnowledgeBook(item.getComponentsPatch());
        }
        if (type == ItemType.FURNACE || type == ItemType.CHEST
                || type == ItemType.TRAPPED_CHEST || type == ItemType.JUKEBOX
                || type == ItemType.DISPENSER || type == ItemType.DROPPER
                || (type.hasBlockType() && Tag.SIGNS.isTagged(type.getBlockType()))
                || type == ItemType.SPAWNER || type == ItemType.BREWING_STAND
                || type == ItemType.ENCHANTING_TABLE || type == ItemType.COMMAND_BLOCK
                || type == ItemType.REPEATING_COMMAND_BLOCK || type == ItemType.CHAIN_COMMAND_BLOCK
                || type == ItemType.BEACON || type == ItemType.DAYLIGHT_DETECTOR
                || type == ItemType.HOPPER || type == ItemType.COMPARATOR
                || type == ItemType.SHIELD || type == ItemType.STRUCTURE_BLOCK
                || (type.hasBlockType() && Tag.SHULKER_BOXES.isTagged(type.getBlockType()))
                || type == ItemType.ENDER_CHEST || type == ItemType.BARREL
                || type == ItemType.BELL || type == ItemType.BLAST_FURNACE
                || type == ItemType.CAMPFIRE || type == ItemType.SOUL_CAMPFIRE
                || type == ItemType.JIGSAW || type == ItemType.LECTERN
                || type == ItemType.SMOKER || type == ItemType.BEEHIVE
                || type == ItemType.BEE_NEST || type == ItemType.SCULK_CATALYST
                || type == ItemType.SCULK_SHRIEKER || type == ItemType.SCULK_SENSOR
                || type == ItemType.CALIBRATED_SCULK_SENSOR || type == ItemType.CHISELED_BOOKSHELF
                || type == ItemType.DECORATED_POT || type == ItemType.SUSPICIOUS_SAND
                || type == ItemType.SUSPICIOUS_GRAVEL || type == ItemType.CRAFTER
                || type == ItemType.TRIAL_SPAWNER || type == ItemType.VAULT) {
            return new CraftMetaBlockState(item.getComponentsPatch(), CraftItemType.minecraftToBukkit(item.getItem()));
        }
        if (type == ItemType.TROPICAL_FISH_BUCKET) {
            return new CraftMetaTropicalFishBucket(item.getComponentsPatch());
        }
        if (type == ItemType.AXOLOTL_BUCKET) {
            return new CraftMetaAxolotlBucket(item.getComponentsPatch());
        }
        if (type == ItemType.CROSSBOW) {
            return new CraftMetaCrossbow(item.getComponentsPatch());
        }
        if (type == ItemType.SUSPICIOUS_STEW) {
            return new CraftMetaSuspiciousStew(item.getComponentsPatch());
        }
        if (type == ItemType.COD_BUCKET || type == ItemType.PUFFERFISH_BUCKET
                || type == ItemType.SALMON_BUCKET || type == ItemType.ITEM_FRAME
                || type == ItemType.GLOW_ITEM_FRAME || type == ItemType.PAINTING) {
            return new CraftMetaEntityTag(item.getComponentsPatch());
        }
        if (type == ItemType.COMPASS) {
            return new CraftMetaCompass(item.getComponentsPatch());
        }
        if (type == ItemType.BUNDLE) {
            return new CraftMetaBundle(item.getComponentsPatch());
        }
        if (type == ItemType.GOAT_HORN) {
            return new CraftMetaMusicInstrument(item.getComponentsPatch());
        }
        if (type == ItemType.OMINOUS_BOTTLE) {
            return new CraftMetaOminousBottle(item.getComponentsPatch());
        }

        return new CraftMetaItem(item.getComponentsPatch());
    }

    static ItemType getType(net.minecraft.world.item.ItemStack item) {
        return item == null ? ItemType.AIR : CraftItemType.minecraftToBukkit(item.getItem());
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        return setItemMeta(handle, itemMeta);
    }

    public static boolean setItemMeta(net.minecraft.world.item.ItemStack item, ItemMeta itemMeta) {
        if (item == null) {
            return false;
        }
        if (CraftItemFactory.instance().equals(itemMeta, null)) {
            item.restorePatch(DataComponentPatch.EMPTY);
            return true;
        }
        if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item))) {
            return false;
        }

        itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, getType(item));
        if (itemMeta == null) return true;

        if (!((CraftMetaItem) itemMeta).isEmpty()) {
            CraftMetaItem.Applicator tag = new CraftMetaItem.Applicator();

            ((CraftMetaItem) itemMeta).applyToItem(tag);
            item.restorePatch(tag.build());
        }
        // SpigotCraft#463 this is required now by the Vanilla client, so mimic ItemStack constructor in ensuring it
        if (item.getItem() != null && item.getMaxDamage() > 0) {
            item.setDamageValue(item.getDamageValue());
        }

        return true;
    }

    @Override
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack == this) {
            return true;
        }
        if (!(stack instanceof CraftItemStack)) {
            return stack.getClass() == ItemStack.class && stack.isSimilar(this);
        }

        CraftItemStack that = (CraftItemStack) stack;
        if (handle == that.handle) {
            return true;
        }
        if (handle == null || that.handle == null) {
            return false;
        }
        if (!(that.getType() == getType() && getDurability() == that.getDurability())) {
            return false;
        }
        return hasItemMeta() ? that.hasItemMeta() && handle.getComponents().equals(that.handle.getComponents()) : !that.hasItemMeta();
    }

    @Override
    public boolean hasItemMeta() {
        return hasItemMeta(handle) && !CraftItemFactory.instance().equals(getItemMeta(), null);
    }

    static boolean hasItemMeta(net.minecraft.world.item.ItemStack item) {
        return !(item == null || item.getComponentsPatch().isEmpty());
    }
}
