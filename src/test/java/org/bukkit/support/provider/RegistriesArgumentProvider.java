package org.bukkit.support.provider;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import net.minecraft.world.level.material.FluidType;
import org.bukkit.Art;
import org.bukkit.Fluid;
import org.bukkit.GameEvent;
import org.bukkit.MusicInstrument;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftArt;
import org.bukkit.craftbukkit.CraftFluid;
import org.bukkit.craftbukkit.CraftGameEvent;
import org.bukkit.craftbukkit.CraftMusicInstrument;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.entity.CraftCat;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftFrog;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionType;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class RegistriesArgumentProvider implements ArgumentsProvider {

    private static final List<Arguments> DATA = Lists.newArrayList();

    static {
        // Order: Bukkit class, Minecraft Registry key, CraftBukkit class, Minecraft class
        register(Enchantment.class, Registries.ENCHANTMENT, CraftEnchantment.class, net.minecraft.world.item.enchantment.Enchantment.class);
        register(GameEvent.class, Registries.GAME_EVENT, CraftGameEvent.class, net.minecraft.world.level.gameevent.GameEvent.class);
        register(MusicInstrument.class, Registries.INSTRUMENT, CraftMusicInstrument.class, Instrument.class);
        register(PotionEffectType.class, Registries.MOB_EFFECT, CraftPotionEffectType.class, MobEffectList.class);
        register(Structure.class, Registries.STRUCTURE, CraftStructure.class, net.minecraft.world.level.levelgen.structure.Structure.class);
        register(StructureType.class, Registries.STRUCTURE_TYPE, CraftStructureType.class, net.minecraft.world.level.levelgen.structure.StructureType.class);
        register(Biome.class, Registries.BIOME, CraftBiome.class, BiomeBase.class);
        register(Art.class, Registries.PAINTING_VARIANT, CraftArt.class, PaintingVariant.class);
        register(Fluid.class, Registries.FLUID, CraftFluid.class, FluidType.class);
        register(EntityType.class, Registries.ENTITY_TYPE, CraftEntityType.class, EntityTypes.class);
        register(Attribute.class, Registries.ATTRIBUTE, CraftAttribute.class, AttributeBase.class);
        register(Villager.Type.class, Registries.VILLAGER_TYPE, CraftVillager.CraftType.class, VillagerType.class);
        register(Villager.Profession.class, Registries.VILLAGER_PROFESSION, CraftVillager.CraftProfession.class, VillagerProfession.class);
        register(Sound.class, Registries.SOUND_EVENT, CraftSound.class, SoundEffect.class);
        register(TrimMaterial.class, Registries.TRIM_MATERIAL, CraftTrimMaterial.class, net.minecraft.world.item.armortrim.TrimMaterial.class);
        register(TrimPattern.class, Registries.TRIM_PATTERN, CraftTrimPattern.class, net.minecraft.world.item.armortrim.TrimPattern.class);
        register(DamageType.class, Registries.DAMAGE_TYPE, CraftDamageType.class, net.minecraft.world.damagesource.DamageType.class);
        register(Wolf.Variant.class, Registries.WOLF_VARIANT, CraftWolf.CraftVariant.class, WolfVariant.class);
        register(ItemType.class, Registries.ITEM, CraftItemType.class, net.minecraft.world.item.Item.class);
        register(BlockType.class, Registries.BLOCK, CraftBlockType.class, net.minecraft.world.level.block.Block.class);
        register(Frog.Variant.class, Registries.FROG_VARIANT, CraftFrog.CraftVariant.class, FrogVariant.class);
        register(Cat.Type.class, Registries.CAT_VARIANT, CraftCat.CraftType.class, CatVariant.class);
        register(PatternType.class, Registries.BANNER_PATTERN, CraftPatternType.class, EnumBannerPatternType.class);
        register(Particle.class, Registries.PARTICLE_TYPE, CraftParticle.class, net.minecraft.core.particles.Particle.class);
        register(PotionType.class, Registries.POTION, CraftPotionType.class, PotionRegistry.class);
    }

    private static void register(Class bukkit, ResourceKey registry, Class craft, Class minecraft) {
        DATA.add(Arguments.of(bukkit, registry, craft, minecraft));
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return getData();
    }

    public static Stream<? extends Arguments> getData() {
        return DATA.stream();
    }
}
