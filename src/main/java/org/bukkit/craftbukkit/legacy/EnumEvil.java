package org.bukkit.craftbukkit.legacy;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.bukkit.Art;
import org.bukkit.Fluid;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.legacy.reroute.DoNotReroute;
import org.bukkit.craftbukkit.legacy.reroute.InjectPluginVersion;
import org.bukkit.craftbukkit.legacy.reroute.NotInBukkit;
import org.bukkit.craftbukkit.legacy.reroute.RequireCompatibility;
import org.bukkit.craftbukkit.legacy.reroute.RequirePluginVersion;
import org.bukkit.craftbukkit.legacy.reroute.RerouteArgumentType;
import org.bukkit.craftbukkit.legacy.reroute.RerouteReturnType;
import org.bukkit.craftbukkit.legacy.reroute.RerouteStatic;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.ClassTraverser;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionType;
import org.bukkit.util.OldEnum;

@NotInBukkit
@RequireCompatibility("enum-compatibility-mode")
@RequirePluginVersion(maxInclusive = "1.20.6")
public class EnumEvil {

    private static final Map<Class<?>, LegacyRegistryData> REGISTRIES = new HashMap<>();

    static {
        REGISTRIES.put(Biome.class, new LegacyRegistryData(Registry.BIOME, Biome::valueOf));
        REGISTRIES.put(Art.class, new LegacyRegistryData(Registry.ART, Art::valueOf));
        REGISTRIES.put(Fluid.class, new LegacyRegistryData(Registry.FLUID, Fluid::valueOf));
        REGISTRIES.put(EntityType.class, new LegacyRegistryData(Registry.ENTITY_TYPE, EntityType::valueOf));
        REGISTRIES.put(Statistic.class, new LegacyRegistryData(Registry.STATISTIC, Statistic::valueOf));
        REGISTRIES.put(Sound.class, new LegacyRegistryData(Registry.SOUNDS, Sound::valueOf));
        REGISTRIES.put(Attribute.class, new LegacyRegistryData(Registry.ATTRIBUTE, Attribute::valueOf));
        REGISTRIES.put(Villager.Type.class, new LegacyRegistryData(Registry.VILLAGER_TYPE, Villager.Type::valueOf));
        REGISTRIES.put(Villager.Profession.class, new LegacyRegistryData(Registry.VILLAGER_PROFESSION, Villager.Profession::valueOf));
        REGISTRIES.put(Frog.Variant.class, new LegacyRegistryData(Registry.FROG_VARIANT, Frog.Variant::valueOf));
        REGISTRIES.put(Cat.Type.class, new LegacyRegistryData(Registry.CAT_VARIANT, Cat.Type::valueOf));
        REGISTRIES.put(PatternType.class, new LegacyRegistryData(Registry.BANNER_PATTERN, PatternType::valueOf));
        REGISTRIES.put(Particle.class, new LegacyRegistryData(Registry.PARTICLE_TYPE, Particle::valueOf));
        REGISTRIES.put(PotionType.class, new LegacyRegistryData(Registry.POTION, PotionType::valueOf));
        REGISTRIES.put(MapCursor.Type.class, new LegacyRegistryData(Registry.MAP_DECORATION_TYPE, MapCursor.Type::valueOf));
    }

    public static LegacyRegistryData getRegistryData(Class<?> clazz) {
        ClassTraverser it = new ClassTraverser(clazz);
        LegacyRegistryData registryData;
        while (it.hasNext()) {
            registryData = REGISTRIES.get(it.next());
            if (registryData != null) {
                return registryData;
            }
        }

        return null;
    }

    @DoNotReroute
    public static Object getFromAnnotation(Annotation annotation, Function<Annotation, Object> annotationMaterialFunction) {
        // TODO 2024-06-09: Write this
        return annotationMaterialFunction.apply(annotation);
    }

    @DoNotReroute
    public static Registry<?> getRegistry(Class<?> clazz) {
        LegacyRegistryData registryData = getRegistryData(clazz);

        if (registryData != null) {
            return registryData.registry();
        }

        return null;
    }

    @RerouteStatic("com/google/common/collect/Maps")
    @RerouteReturnType("java/util/EnumSet")
    public static ImposterEnumMap newEnumMap(Class<?> objectClass) {
        return new ImposterEnumMap(objectClass);
    }

    @RerouteStatic("com/google/common/collect/Maps")
    @RerouteReturnType("java/util/EnumSet")
    public static ImposterEnumMap newEnumMap(Map map) {
        return new ImposterEnumMap(map);
    }

    @RerouteStatic("com/google/common/collect/Sets")
    public static Collector<?, ?, ?> toImmutableEnumSet() {
        return Collectors.toUnmodifiableSet();
    }

    @RerouteStatic("com/google/common/collect/Sets")
    @RerouteReturnType("java/util/EnumSet")
    public static ImposterEnumSet newEnumSet(Iterable<?> iterable, Class<?> clazz) {
        ImposterEnumSet set = ImposterEnumSet.noneOf(clazz);

        for (Object some : iterable) {
            set.add(some);
        }

        return set;
    }

    @RerouteStatic("com/google/common/collect/Sets")
    public static ImmutableSet<?> immutableEnumSet(Iterable<?> iterable) {
        return ImmutableSet.of(iterable);
    }

    @RerouteStatic("com/google/common/collect/Sets")
    public static ImmutableSet<?> immutableEnumSet(@RerouteArgumentType("java/util/Enum") Object first, @RerouteArgumentType("[java/util/Enum") Object... rest) {
        return ImmutableSet.of(first, rest);
    }

    public static Object[] getEnumConstants(Class<?> clazz) {
        if (clazz.isEnum()) {
            return clazz.getEnumConstants();
        }

        Registry<?> registry = getRegistry(clazz);

        if (registry == null) {
            return clazz.getEnumConstants();
        }

        // Need to do this in such away to avoid ClassCastException
        List<?> values = Lists.newArrayList(registry);
        Object array = Array.newInstance(clazz, values.size());

        for (int i = 0; i < values.size(); i++) {
            Array.set(array, i, values.get(i));
        }

        return (Object[]) array;
    }

    public static String name(@RerouteArgumentType("java/util/Enum") Object object) {
        if (object instanceof OldEnum<?>) {
            return ((OldEnum<?>) object).name();
        }

        return ((Enum<?>) object).name();
    }

    public static int compareTo(@RerouteArgumentType("java/util/Enum") Object object, @RerouteArgumentType("java/util/Enum") Object other) {
        if (object instanceof OldEnum<?>) {
            return ((OldEnum) object).compareTo((OldEnum) other);
        }

        return ((Enum) object).compareTo((Enum) other);
    }

    public static Class<?> getDeclaringClass(@RerouteArgumentType("java/util/Enum") Object object) {
        Class<?> clazz = object.getClass();
        Class<?> zuper = clazz.getSuperclass();
        return (zuper == Enum.class) ? clazz : zuper;
    }

    public static Optional<Enum.EnumDesc> describeConstable(@RerouteArgumentType("java/util/Enum") Object object) {
        return getDeclaringClass(object)
                .describeConstable()
                .map(c -> Enum.EnumDesc.of(c, name(object)));
    }

    @RerouteStatic("java/lang/Enum")
    @RerouteReturnType("java/lang/Enum")
    public static Object valueOf(Class enumClass, String name, @InjectPluginVersion ApiVersion apiVersion) {
        name = FieldRename.rename(apiVersion, enumClass.getName().replace('.', '/'), name);
        LegacyRegistryData registryData = getRegistryData(enumClass);
        if (registryData != null) {
            return registryData.function().apply(name);
        }

        return Enum.valueOf(enumClass, name);
    }

    public static String toString(@RerouteArgumentType("java/util/Enum") Object object) {
        return object.toString();
    }

    public static int ordinal(@RerouteArgumentType("java/util/Enum") Object object) {
        if (object instanceof OldEnum<?>) {
            return ((OldEnum<?>) object).ordinal();
        }

        return ((Enum<?>) object).ordinal();
    }

    public record LegacyRegistryData(Registry<?> registry, Function<String, ?> function) {
    }
}
