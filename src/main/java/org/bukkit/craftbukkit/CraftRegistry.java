package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.bukkit.Art;
import org.bukkit.Fluid;
import org.bukkit.GameEvent;
import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.entity.CraftCat;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftFrog;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class CraftRegistry<B extends Keyed, M> implements Registry<B> {

    private static IRegistryCustom registry;

    public static void setMinecraftRegistry(IRegistryCustom registry) {
        Preconditions.checkState(CraftRegistry.registry == null, "Registry already set");
        CraftRegistry.registry = registry;
    }

    public static IRegistryCustom getMinecraftRegistry() {
        return registry;
    }

    public static <E> IRegistry<E> getMinecraftRegistry(ResourceKey<IRegistry<E>> key) {
        return getMinecraftRegistry().registryOrThrow(key);
    }

    public static <B extends Keyed> Registry<?> createRegistry(Class<? super B> bukkitClass, IRegistryCustom registryHolder) {
        if (bukkitClass == Enchantment.class) {
            return new CraftRegistry<>(Enchantment.class, registryHolder.registryOrThrow(Registries.ENCHANTMENT), CraftEnchantment::new);
        }
        if (bukkitClass == GameEvent.class) {
            return new CraftRegistry<>(GameEvent.class, registryHolder.registryOrThrow(Registries.GAME_EVENT), CraftGameEvent::new);
        }
        if (bukkitClass == MusicInstrument.class) {
            return new CraftRegistry<>(MusicInstrument.class, registryHolder.registryOrThrow(Registries.INSTRUMENT), CraftMusicInstrument::new);
        }
        if (bukkitClass == PotionEffectType.class) {
            return new CraftRegistry<>(PotionEffectType.class, registryHolder.registryOrThrow(Registries.MOB_EFFECT), CraftPotionEffectType::new);
        }
        if (bukkitClass == Structure.class) {
            return new CraftRegistry<>(Structure.class, registryHolder.registryOrThrow(Registries.STRUCTURE), CraftStructure::new);
        }
        if (bukkitClass == StructureType.class) {
            return new CraftRegistry<>(StructureType.class, registryHolder.registryOrThrow(Registries.STRUCTURE_TYPE), CraftStructureType::new);
        }
        if (bukkitClass == Biome.class) {
            return new CraftRegistry<>(Biome.class, registryHolder.registryOrThrow(Registries.BIOME), CraftBiome::new);
        }
        if (bukkitClass == Art.class) {
            return new CraftRegistry<>(Art.class, registryHolder.registryOrThrow(Registries.PAINTING_VARIANT), CraftArt::new);
        }
        if (bukkitClass == Fluid.class) {
            return new CraftRegistry<>(Fluid.class, registryHolder.registryOrThrow(Registries.FLUID), CraftFluid::new);
        }
        if (bukkitClass == EntityType.class) {
            return new CraftEntityType.CraftEntityTypeRegistry(registryHolder.registryOrThrow(Registries.ENTITY_TYPE));
        }
        if (bukkitClass == Attribute.class) {
            return new CraftRegistry<>(Attribute.class, registryHolder.registryOrThrow(Registries.ATTRIBUTE), CraftAttribute::new);
        }
        if (bukkitClass == Villager.Type.class) {
            return new CraftRegistry<>(Villager.Type.class, registryHolder.registryOrThrow(Registries.VILLAGER_TYPE), CraftVillager.CraftType::new);
        }
        if (bukkitClass == Villager.Profession.class) {
            return new CraftRegistry<>(Villager.Profession.class, registryHolder.registryOrThrow(Registries.VILLAGER_PROFESSION), CraftVillager.CraftProfession::new);
        }
        if (bukkitClass == Sound.class) {
            return new CraftRegistry<>(Sound.class, registryHolder.registryOrThrow(Registries.SOUND_EVENT), CraftSound::new);
        }
        if (bukkitClass == Statistic.class) {
            return new CraftStatistic.CraftStatisticRegistry(registryHolder.registryOrThrow(Registries.STAT_TYPE));
        }
        if (bukkitClass == TrimMaterial.class) {
            return new CraftRegistry<>(TrimMaterial.class, registryHolder.registryOrThrow(Registries.TRIM_MATERIAL), CraftTrimMaterial::new);
        }
        if (bukkitClass == TrimPattern.class) {
            return new CraftRegistry<>(TrimPattern.class, registryHolder.registryOrThrow(Registries.TRIM_PATTERN), CraftTrimPattern::new);
        }
        if (bukkitClass == BlockType.class) {
            return new CraftRegistry<>(BlockType.class, registryHolder.registryOrThrow(Registries.BLOCK), CraftBlockType::new);
        }
        if (bukkitClass == ItemType.class) {
            return new CraftRegistry<>(ItemType.class, registryHolder.registryOrThrow(Registries.ITEM), CraftItemType::new);
        }
        if (bukkitClass == Frog.Variant.class) {
            return new CraftRegistry<>(Frog.Variant.class, registryHolder.registryOrThrow(Registries.FROG_VARIANT), CraftFrog.CraftVariant::new);
        }
        if (bukkitClass == Cat.Type.class) {
            return new CraftRegistry<>(Cat.Type.class, registryHolder.registryOrThrow(Registries.CAT_VARIANT), CraftCat.CraftType::new);
        }
        if (bukkitClass == PatternType.class) {
            return new CraftRegistry<>(PatternType.class, registryHolder.registryOrThrow(Registries.BANNER_PATTERN), CraftPatternType::new);
        }
        if (bukkitClass == Particle.class) {
            return new CraftParticle.CraftParticleRegistry(registryHolder.registryOrThrow(Registries.PARTICLE_TYPE));
        }
        if (bukkitClass == PotionType.class) {
            return new CraftRegistry<>(PotionType.class, registryHolder.registryOrThrow(Registries.POTION), CraftPotionType::new);
        }

        return null;
    }

    private final Class<? super B> bukkitClass;
    private final Map<NamespacedKey, B> cache = new HashMap<>();
    private final IRegistry<M> minecraftRegistry;
    private final BiFunction<NamespacedKey, M, B> minecraftToBukkit;
    private boolean init;

    public CraftRegistry(Class<? super B> bukkitClass, IRegistry<M> minecraftRegistry, BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
        this.bukkitClass = bukkitClass;
        this.minecraftRegistry = minecraftRegistry;
        this.minecraftToBukkit = minecraftToBukkit;
    }

    @Override
    public B get(NamespacedKey namespacedKey) {
        B cached = cache.get(namespacedKey);
        if (cached != null) {
            return cached;
        }

        // Make sure that the bukkit class is loaded before creating an instance.
        // This ensures that only one instance with a given key is created.
        //
        // Without this code (when bukkit class is not loaded):
        // Registry#get -> #createBukkit -> (load class -> create default) -> put in cache
        // Result: Registry#get != <bukkitClass>.<field> for possible one registry item
        //
        // With this code (when bukkit class is not loaded):
        // Registry#get -> (load class -> create default) -> Registry#get -> get from cache
        // Result: Registry#get == <bukkitClass>.<field>
        if (!init) {
            init = true;
            try {
                Class.forName(bukkitClass.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not load registry class " + bukkitClass, e);
            }

            return get(namespacedKey);
        }

        B bukkit = createBukkit(namespacedKey, minecraftRegistry.getOptional(CraftNamespacedKey.toMinecraft(namespacedKey)).orElse(null));
        if (bukkit == null) {
            return null;
        }

        cache.put(namespacedKey, bukkit);

        return bukkit;
    }

    @NotNull
    @Override
    public Stream<B> stream() {
        return minecraftRegistry.keySet().stream().map(minecraftKey -> get(CraftNamespacedKey.fromMinecraft(minecraftKey)));
    }

    @Override
    public Iterator<B> iterator() {
        return stream().iterator();
    }

    public B createBukkit(NamespacedKey namespacedKey, M minecraft) {
        if (minecraft == null) {
            return null;
        }

        return minecraftToBukkit.apply(namespacedKey, minecraft);
    }
}
