package org.bukkit.craftbukkit.inventory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemBlockWallable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ITileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.ItemStackTest.BukkitWrapper;
import org.bukkit.craftbukkit.inventory.ItemStackTest.CraftWrapper;
import org.bukkit.craftbukkit.inventory.ItemStackTest.StackProvider;
import org.bukkit.craftbukkit.inventory.ItemStackTest.StackWrapper;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

public class ItemMetaTest extends AbstractTestingBase {

    static final int MAX_FIREWORK_POWER = 127; // Please update ItemStackFireworkTest if/when this gets changed.

    @Test(expected = IllegalArgumentException.class)
    public void testPowerLimitExact() {
        newFireworkMeta().setPower(MAX_FIREWORK_POWER + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPowerLimitMax() {
        newFireworkMeta().setPower(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPowerLimitMin() {
        newFireworkMeta().setPower(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPowerLimitNegative() {
        newFireworkMeta().setPower(-1);
    }

    @Test
    public void testPowers() {
        for (int i = 0; i <= MAX_FIREWORK_POWER; i++) {
            FireworkMeta firework = newFireworkMeta();
            firework.setPower(i);
            assertThat(String.valueOf(i), firework.getPower(), is(i));
        }
    }

    @Test
    public void testConflictingEnchantment() {
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(ItemType.DIAMOND_PICKAXE);
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.UNBREAKING), is(false));

        itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.UNBREAKING), is(false));
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.FORTUNE), is(true));
        assertThat(itemMeta.hasConflictingEnchant(null), is(false));
    }

    @Test
    public void testConflictingStoredEnchantment() {
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(ItemType.ENCHANTED_BOOK);
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.UNBREAKING), is(false));

        itemMeta.addStoredEnchant(Enchantment.SILK_TOUCH, 1, false);
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.UNBREAKING), is(false));
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.FORTUNE), is(true));
        assertThat(itemMeta.hasConflictingStoredEnchant(null), is(false));
    }

    @Test
    public void testConflictingEnchantments() {
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(ItemType.DIAMOND_PICKAXE);
        itemMeta.addEnchant(Enchantment.UNBREAKING, 6, true);
        itemMeta.addEnchant(Enchantment.EFFICIENCY, 6, true);
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.FORTUNE), is(false));

        itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.FORTUNE), is(true));
        assertThat(itemMeta.hasConflictingEnchant(null), is(false));
    }

    @Test
    public void testConflictingStoredEnchantments() {
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(ItemType.ENCHANTED_BOOK);
        itemMeta.addStoredEnchant(Enchantment.UNBREAKING, 6, true);
        itemMeta.addStoredEnchant(Enchantment.EFFICIENCY, 6, true);
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.FORTUNE), is(false));

        itemMeta.addStoredEnchant(Enchantment.SILK_TOUCH, 1, false);
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.FORTUNE), is(true));
        assertThat(itemMeta.hasConflictingStoredEnchant(null), is(false));
    }

    private static FireworkMeta newFireworkMeta() {
        return ((FireworkMeta) Bukkit.getItemFactory().getItemMeta(ItemType.FIREWORK_ROCKET));
    }

    @Test
    public void testCrazyEquality() {
        CraftItemStack craft = CraftItemStack.asCraftCopy(ItemStack.of(ItemType.STONE));
        craft.setItemMeta(craft.getItemMeta());
        ItemStack bukkit = new ItemStack(craft);
        assertThat(craft, is(bukkit));
        assertThat(bukkit, is((ItemStack) craft));
    }

    @Test
    public void testBlockStateMeta() {
        List<Block> queue = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof ItemBlock) {
                queue.add(((ItemBlock) item).getBlock());
            }
            if (item instanceof ItemBlockWallable) {
                queue.add(((ItemBlockWallable) item).wallBlock);
            }
        }

        for (Block block : queue) {
            if (block != null) {
                ItemStack stack = CraftItemStack.asNewCraftStack(Item.byBlock(block));

                // Command blocks aren't unit testable atm
                if (stack.getType() == ItemType.COMMAND_BLOCK || stack.getType() == ItemType.CHAIN_COMMAND_BLOCK || stack.getType() == ItemType.REPEATING_COMMAND_BLOCK) {
                    return;
                }

                ItemMeta meta = stack.getItemMeta();
                if (block instanceof ITileEntity) {
                    assertTrue(stack + " has meta of type " + meta + " expected BlockStateMeta", meta instanceof BlockStateMeta);

                    BlockStateMeta blockState = (BlockStateMeta) meta;
                    assertNotNull(stack + " has null block state", blockState.getBlockState());

                    blockState.setBlockState(blockState.getBlockState());
                } else {
                    assertTrue(stack + " has unexpected meta of type BlockStateMeta (but is not a tile)", !(meta instanceof BlockStateMeta));
                }
            }
        }
    }

    @Test
    public void testSpawnEggsHasMeta() {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof net.minecraft.world.item.ItemMonsterEgg) {
                ItemType itemType = CraftItemType.minecraftToBukkit(item);
                CraftMetaItem baseMeta = (CraftMetaItem) Bukkit.getItemFactory().getItemMeta(itemType);
                ItemMeta baseMetaItem = CraftItemStack.getItemMeta(item.getDefaultInstance());

                assertTrue(itemType + " is not handled in CraftItemFactory", baseMeta instanceof CraftMetaSpawnEgg);
                assertTrue(itemType + " is not applicable to CraftMetaSpawnEgg", baseMeta.applicableTo(itemType));
                assertTrue(itemType + " is not handled in CraftItemStack", baseMetaItem instanceof SpawnEggMeta);
            }
        }
    }

    @Test
    public void testEachExtraData() {
        final List<StackProvider> providers = Arrays.asList(
            new StackProvider(ItemType.WRITABLE_BOOK) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final BookMeta meta = (BookMeta) cleanStack.getItemMeta();
                    meta.setAuthor("Some author");
                    meta.setPages("Page 1", "Page 2");
                    meta.setTitle("A title");
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.WRITTEN_BOOK) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final BookMeta meta = (BookMeta) cleanStack.getItemMeta();
                    meta.setAuthor("Some author");
                    meta.setPages("Page 1", "Page 2");
                    meta.setTitle("A title");
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            /* Skulls rely on a running server instance
            new StackProvider(Material.SKULL_ITEM) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final SkullMeta meta = (SkullMeta) cleanStack.getItemMeta();
                    meta.setOwner("Notch");
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            */
            new StackProvider(ItemType.FILLED_MAP) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                    meta.setScaling(true);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.DIAMOND_CHESTPLATE) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final ArmorMeta meta = (ArmorMeta) cleanStack.getItemMeta();
                    meta.setTrim(new ArmorTrim(TrimMaterial.AMETHYST, TrimPattern.COAST));
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.LEATHER_HORSE_ARMOR) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                    meta.setColor(Color.FUCHSIA);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.LEATHER_CHESTPLATE) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final ColorableArmorMeta meta = (ColorableArmorMeta) cleanStack.getItemMeta();
                    meta.setTrim(new ArmorTrim(TrimMaterial.COPPER, TrimPattern.DUNE));
                    meta.setColor(Color.MAROON);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.POTION) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                    meta.setBasePotionData(new PotionData(PotionType.EMPTY, false, false));
                    meta.addCustomEffect(PotionEffectType.NAUSEA.createEffect(1, 1), false);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.FIREWORK_ROCKET) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final FireworkMeta meta = (FireworkMeta) cleanStack.getItemMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.GREEN).withFade(Color.OLIVE).with(Type.BALL_LARGE).build());
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.ENCHANTED_BOOK) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                    meta.addStoredEnchant(Enchantment.FLAME, 1, true);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.FIREWORK_STAR) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                    meta.setEffect(FireworkEffect.builder().withColor(Color.MAROON, Color.BLACK).with(Type.CREEPER).withFlicker().build());
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.WHITE_BANNER) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final BannerMeta meta = (BannerMeta) cleanStack.getItemMeta();
                    meta.setBaseColor(DyeColor.CYAN);
                    meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BRICKS));
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            /* No distinguishing features, add back with virtual entity API
            new StackProvider(Material.ZOMBIE_SPAWN_EGG) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final SpawnEggMeta meta = (SpawnEggMeta) cleanStack.getItemMeta();
                    meta.setSpawnedType(EntityType.ZOMBIE);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            */
            new StackProvider(ItemType.KNOWLEDGE_BOOK) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final KnowledgeBookMeta meta = (KnowledgeBookMeta) cleanStack.getItemMeta();
                    meta.addRecipe(new NamespacedKey("minecraft", "test"), new NamespacedKey("plugin", "test"));
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.TROPICAL_FISH_BUCKET) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final TropicalFishBucketMeta meta = (TropicalFishBucketMeta) cleanStack.getItemMeta();
                    meta.setBodyColor(DyeColor.ORANGE);
                    meta.setPatternColor(DyeColor.BLACK);
                    meta.setPattern(TropicalFish.Pattern.DASHER);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.AXOLOTL_BUCKET) {
                @Override ItemStack operate(ItemStack cleanStack) {
                     final AxolotlBucketMeta meta = (AxolotlBucketMeta) cleanStack.getItemMeta();
                     meta.setVariant(Axolotl.Variant.BLUE);
                     cleanStack.setItemMeta(meta);
                     return cleanStack;
                }
            },
            new StackProvider(ItemType.CROSSBOW) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final CrossbowMeta meta = (CrossbowMeta) cleanStack.getItemMeta();
                    meta.addChargedProjectile(ItemStack.of(ItemType.ARROW));
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.ARMOR_STAND) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final CraftMetaArmorStand meta = (CraftMetaArmorStand) cleanStack.getItemMeta();
                    meta.entityTag = new NBTTagCompound();
                    meta.entityTag.putBoolean("Small", true);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.SUSPICIOUS_STEW) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final CraftMetaSuspiciousStew meta = ((CraftMetaSuspiciousStew) cleanStack.getItemMeta());
                    meta.addCustomEffect(PotionEffectType.NAUSEA.createEffect(1, 0), false);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.ITEM_FRAME) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final CraftMetaEntityTag meta = ((CraftMetaEntityTag) cleanStack.getItemMeta());
                    meta.entityTag = new NBTTagCompound();
                    meta.entityTag.putBoolean("Invisible", true);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.COMPASS) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final CraftMetaCompass meta = ((CraftMetaCompass) cleanStack.getItemMeta());
                    meta.setLodestoneTracked(true);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.BUNDLE) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final BundleMeta meta = (BundleMeta) cleanStack.getItemMeta();
                    meta.addItem(ItemStack.of(ItemType.STONE));
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(ItemType.GOAT_HORN) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final CraftMetaMusicInstrument meta = (CraftMetaMusicInstrument) cleanStack.getItemMeta();
                    meta.setInstrument(MusicInstrument.ADMIRE);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            }
        );

        assertThat("Forgotten test?", providers, hasSize(ItemStackTest.COMPOUND_ITEM_TYPES.length - 4/* Normal item meta, skulls, eggs and tile entities */));

        for (final StackProvider provider : providers) {
            downCastTest(new BukkitWrapper(provider));
            downCastTest(new CraftWrapper(provider));
        }
    }

    @Test
    public void testAttributeModifiers() {
        UUID sameUUID = UUID.randomUUID();
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(ItemType.DIAMOND_PICKAXE);
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(sameUUID, "Test Modifier", 10, AttributeModifier.Operation.ADD_NUMBER));

        ItemMeta equalMeta = Bukkit.getItemFactory().getItemMeta(ItemType.DIAMOND_PICKAXE);
        equalMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(sameUUID, "Test Modifier", 10, AttributeModifier.Operation.ADD_NUMBER));

        assertThat(itemMeta.equals(equalMeta), is(true));

        ItemMeta itemMeta2 = Bukkit.getItemFactory().getItemMeta(ItemType.DIAMOND_PICKAXE);
        itemMeta2.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(sameUUID, "Test Modifier", 10, AttributeModifier.Operation.ADD_NUMBER));

        ItemMeta notEqualMeta2 = Bukkit.getItemFactory().getItemMeta(ItemType.DIAMOND_PICKAXE);
        notEqualMeta2.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(sameUUID, "Test Modifier", 11, AttributeModifier.Operation.ADD_NUMBER));

        assertThat(itemMeta2.equals(notEqualMeta2), is(false));
    }

    @Test
    public void testBlockData() {
        BlockDataMeta itemMeta = (BlockDataMeta) Bukkit.getItemFactory().getItemMeta(ItemType.CHEST);
        itemMeta.setBlockData(CraftBlockData.newData(null, "minecraft:chest[waterlogged=true]"));
        assertThat(itemMeta.getBlockData(BlockType.CHEST), is((Object) CraftBlockData.newData(null, "minecraft:chest[waterlogged=true]")));
    }

    private void downCastTest(final StackWrapper provider) {
        final String name = provider.toString();
        final ItemStack blank = ItemStack.of(ItemType.STONE);
        final ItemStack craftBlank = CraftItemStack.asCraftCopy(blank);

        // Check that equality and similarity works for each meta implementation
        assertThat(name, provider.stack(), is(provider.stack()));
        assertThat(name, provider.stack().isSimilar(provider.stack()), is(true));

        downCastTest(name, provider.stack(), blank);
        blank.setItemMeta(blank.getItemMeta());
        downCastTest(name, provider.stack(), blank);

        downCastTest(name, provider.stack(), craftBlank);
        craftBlank.setItemMeta(craftBlank.getItemMeta());
        downCastTest(name, provider.stack(), craftBlank);
    }

    private void downCastTest(final String name, final ItemStack stack, final ItemStack blank) {
        assertThat(name, stack, is(not(blank)));
        assertThat(name, stack.getItemMeta(), is(not(blank.getItemMeta())));

        stack.setType(ItemType.STONE);

        assertThat(name, stack, is(blank));
    }
}
