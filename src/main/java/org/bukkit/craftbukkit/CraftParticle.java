package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Vibration;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class CraftParticle implements Particle, Handleable<net.minecraft.core.particles.Particle<?>> {

    private static int count = 0;

    public static Particle minecraftToBukkit(net.minecraft.core.particles.Particle<?> minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.PARTICLE_TYPE, Registry.PARTICLE_TYPE);
    }

    public static net.minecraft.core.particles.Particle<?> bukkitToMinecraft(Particle bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static ParticleParam createParticleParam(Particle particle) {
        Preconditions.checkArgument(particle != null, "particle cannot be null");
        Preconditions.checkArgument(!particle.isTyped(), "particle %s is typed and requires data of type %s", particle, particle.isTyped() ? particle.getDataType() : null);

        return (ParticleParam) ((CraftParticle) particle).getHandle();
    }

    public static <D> ParticleParam createParticleParam(Particle.Typed<D> particle, D data) {
        Preconditions.checkArgument(particle != null, "particle cannot be null");

        data = CraftParticle.convertLegacy(data);

        Preconditions.checkArgument(data != null, "missing required data %s", particle.getDataType());
        Preconditions.checkArgument(particle.getDataType().isInstance(data), "data (%s) should be %s", data.getClass(), particle.getDataType());

        return ((CraftParticle.CraftTyped<D>) particle).createParticleParam(data);
    }

    public static <T> T convertLegacy(T object) {
        if (object instanceof MaterialData mat) {
            return (T) CraftBlockData.fromData(CraftMagicNumbers.getBlock(mat));
        }

        return object;
    }

    private final NamespacedKey key;
    private final net.minecraft.core.particles.Particle<?> particle;
    private final String name;
    private final int ordinal;

    public CraftParticle(NamespacedKey key, net.minecraft.core.particles.Particle<?> particle) {
        this.key = key;
        this.particle = particle;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive particle specific values.
        // Custom particles will return the key with namespace. For a plugin this should look than like a new particle
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase(Locale.ROOT);
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    @Override
    public net.minecraft.core.particles.Particle<?> getHandle() {
        return particle;
    }

    @NotNull
    @Override
    public <D> Typed<D> typed(@NotNull Class<D> aClass) {
        throw new UnsupportedOperationException(String.format("Particle %s is not typed", name));
    }

    @Override
    public boolean isTyped() {
        return false;
    }

    @NotNull
    @Override
    public Class<?> getDataType() {
        throw new UnsupportedOperationException(String.format("Particle %s does not have a data type", name));
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(Particle particle) {
        return ordinal - particle.ordinal();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
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

        if (!(other instanceof CraftParticle)) {
            return false;
        }

        return getKey().equals(((Particle) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    public abstract static class CraftTyped<D> extends CraftParticle implements Particle.Typed<D> {

        private final Class<D> clazz;

        public CraftTyped(NamespacedKey key, net.minecraft.core.particles.Particle<?> particle, Class<D> clazz) {
            super(key, particle);
            this.clazz = clazz;
        }

        public abstract ParticleParam createParticleParam(D data);

        @NotNull
        @Override
        public <D> Typed<D> typed(@NotNull Class<D> aClass) {
            Preconditions.checkArgument(aClass == getDataType(), "Provided data type do not match expected %s but got %s", getDataType(), aClass);
            return (Typed<D>) this;
        }

        @Override
        public boolean isTyped() {
            return true;
        }

        @NotNull
        @Override
        public Class<D> getDataType() {
            return clazz;
        }
    }

    public static class CraftParticleRegistry extends CraftRegistry<Particle, net.minecraft.core.particles.Particle<?>> {

        private static final Map<NamespacedKey, BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle>> PARTICLE_MAP = new HashMap<>();

        private static final BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> voidFunction = (name, particle) -> new CraftParticle(name, particle);

        static {
            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> dustOptionsFunction = (name, particle) -> new CraftTyped<>(name, particle, DustOptions.class) {
                @Override
                public ParticleParam createParticleParam(DustOptions data) {
                    Color color = data.getColor();
                    return new ParticleParamRedstone(new Vector3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f), data.getSize());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> itemStackFunction = (name, particle) -> new CraftTyped<>(name, particle, ItemStack.class) {
                @Override
                public ParticleParam createParticleParam(ItemStack data) {
                    return new ParticleParamItem((net.minecraft.core.particles.Particle<ParticleParamItem>) getHandle(), CraftItemStack.asNMSCopy(data));
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> blockDataFunction = (name, particle) -> new CraftTyped<>(name, particle, BlockData.class) {
                @Override
                public ParticleParam createParticleParam(BlockData data) {
                    return new ParticleParamBlock((net.minecraft.core.particles.Particle<ParticleParamBlock>) getHandle(), ((CraftBlockData) data).getState());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> dustTransitionFunction = (name, particle) -> new CraftTyped<>(name, particle, DustTransition.class) {
                @Override
                public ParticleParam createParticleParam(DustTransition data) {
                    Color from = data.getColor();
                    Color to = data.getToColor();
                    return new DustColorTransitionOptions(new Vector3f(from.getRed() / 255.0f, from.getGreen() / 255.0f, from.getBlue() / 255.0f), new Vector3f(to.getRed() / 255.0f, to.getGreen() / 255.0f, to.getBlue() / 255.0f), data.getSize());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> vibrationFunction = (name, particle) -> new CraftTyped<>(name, particle, Vibration.class) {
                @Override
                public ParticleParam createParticleParam(Vibration data) {
                    PositionSource source;
                    if (data.getDestination() instanceof Vibration.Destination.BlockDestination) {
                        Location destination = ((Vibration.Destination.BlockDestination) data.getDestination()).getLocation();
                        source = new BlockPositionSource(CraftLocation.toBlockPosition(destination));
                    } else if (data.getDestination() instanceof Vibration.Destination.EntityDestination) {
                        Entity destination = ((CraftEntity) ((Vibration.Destination.EntityDestination) data.getDestination()).getEntity()).getHandle();
                        source = new EntityPositionSource(destination, destination.getEyeHeight());
                    } else {
                        throw new IllegalArgumentException("Unknown vibration destination " + data.getDestination());
                    }

                    return new VibrationParticleOption(source, data.getArrivalTime());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> floatFunction = (name, particle) -> new CraftTyped<>(name, particle, Float.class) {
                @Override
                public ParticleParam createParticleParam(Float data) {
                    return new SculkChargeParticleOptions(data);
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> integerFunction = (name, particle) -> new CraftTyped<>(name, particle, Integer.class) {
                @Override
                public ParticleParam createParticleParam(Integer data) {
                    return new ShriekParticleOption(data);
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> colorFunction = (name, particle) -> new CraftTyped<>(name, particle, Color.class) {
                @Override
                public ParticleParam createParticleParam(Color color) {
                    return ColorParticleOption.create((net.minecraft.core.particles.Particle<ColorParticleOption>) particle, color.asARGB());
                }
            };

            add("dust", dustOptionsFunction);
            add("item", itemStackFunction);
            add("block", blockDataFunction);
            add("falling_dust", blockDataFunction);
            add("dust_color_transition", dustTransitionFunction);
            add("vibration", vibrationFunction);
            add("sculk_charge", floatFunction);
            add("shriek", integerFunction);
            add("block_marker", blockDataFunction);
            add("entity_effect", colorFunction);
            add("dust_pillar", blockDataFunction);
        }

        private static void add(String name, BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> function) {
            PARTICLE_MAP.put(NamespacedKey.fromString(name), function);
        }

        public CraftParticleRegistry(IRegistry<net.minecraft.core.particles.Particle<?>> minecraftRegistry) {
            super(Particle.class, minecraftRegistry, null, FieldRename.PARTICLE_TYPE_RENAME);
        }

        @Override
        public Particle createBukkit(NamespacedKey namespacedKey, net.minecraft.core.particles.Particle<?> particle) {
            if (particle == null) {
                return null;
            }

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, Particle> function = PARTICLE_MAP.getOrDefault(namespacedKey, voidFunction);

            return function.apply(namespacedKey, particle);
        }
    }
}
