package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.monster.EntityZombieVillager;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;
import org.apache.commons.lang.Validate;
import org.bukkit.Fluid;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftFluid;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CraftVillager extends CraftAbstractVillager implements Villager {

    public CraftVillager(CraftServer server, EntityVillager entity) {
        super(server, entity);
    }

    @Override
    public EntityVillager getHandle() {
        return (EntityVillager) entity;
    }

    @Override
    public String toString() {
        return "CraftVillager";
    }

    @Override
    public void remove() {
        getHandle().releaseAllPois();

        super.remove();
    }

    @Override
    public Profession getProfession() {
        return CraftProfession.minecraftToBukkit(getRegistryAccess().registryOrThrow(Registries.VILLAGER_PROFESSION), getHandle().getVillagerData().getProfession());
    }

    @Override
    public void setProfession(Profession profession) {
        Validate.notNull(profession);
        getHandle().setVillagerData(getHandle().getVillagerData().setProfession(CraftProfession.bukkitToMinecraft(profession)));
    }

    @Override
    public Type getVillagerType() {
        return CraftType.minecraftToBukkit(getRegistryAccess().registryOrThrow(Registries.VILLAGER_TYPE), getHandle().getVillagerData().getType());
    }

    @Override
    public void setVillagerType(Type type) {
        Validate.notNull(type);
        getHandle().setVillagerData(getHandle().getVillagerData().setType(CraftType.bukkitToMinecraft(type)));
    }

    @Override
    public int getVillagerLevel() {
        return getHandle().getVillagerData().getLevel();
    }

    @Override
    public void setVillagerLevel(int level) {
        Preconditions.checkArgument(1 <= level && level <= 5, "level must be between [1, 5]");

        getHandle().setVillagerData(getHandle().getVillagerData().setLevel(level));
    }

    @Override
    public int getVillagerExperience() {
        return getHandle().getVillagerXp();
    }

    @Override
    public void setVillagerExperience(int experience) {
        Preconditions.checkArgument(experience >= 0, "Experience must be positive");

        getHandle().setVillagerXp(experience);
    }

    @Override
    public boolean sleep(Location location) {
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
        Preconditions.checkArgument(location.getWorld().equals(getWorld()), "Cannot sleep across worlds");
        Preconditions.checkState(!getHandle().generation, "Cannot sleep during world generation");

        BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        IBlockData iblockdata = getHandle().level.getBlockState(position);
        if (!(iblockdata.getBlock() instanceof BlockBed)) {
            return false;
        }

        getHandle().startSleeping(position);
        return true;
    }

    @Override
    public void wakeup() {
        Preconditions.checkState(isSleeping(), "Cannot wakeup if not sleeping");
        Preconditions.checkState(!getHandle().generation, "Cannot wakeup during world generation");

        getHandle().stopSleeping();
    }

    @Override
    public void shakeHead() {
        getHandle().setUnhappy();
    }

    @Override
    public ZombieVillager zombify() {
        EntityZombieVillager entityzombievillager = EntityZombie.zombifyVillager(getHandle().level.getMinecraftWorld(), getHandle(), getHandle().blockPosition(), isSilent(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (entityzombievillager != null) ? (ZombieVillager) entityzombievillager.getBukkitEntity() : null;
    }

    public static class CraftType extends Type {
        private static int count = 0;

        public static Type minecraftToBukkit(IRegistry<VillagerType> registry, VillagerType minecraft) {
            if (minecraft == null) {
                return null;
            }

            return Registry.VILLAGER_TYPE.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));
        }

        public static VillagerType bukkitToMinecraft(Type bukkit) {
            if (bukkit == null) {
                return null;
            }

            return ((CraftType) bukkit).getHandle();
        }

        private final NamespacedKey key;
        private final VillagerType villagerType;
        private final String name;
        private final int ordinal;

        public CraftType(NamespacedKey key, VillagerType villagerType) {
            this.key = key;
            this.villagerType = villagerType;
            // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
            // in case plugins use for example the name as key in a config file to receive type specific values.
            // Custom types will return the key with namespace. For a plugin this should look than like a new type
            // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
            if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
                this.name = key.getKey().toUpperCase();
            } else {
                this.name = key.toString();
            }
            this.ordinal = count++;
        }

        public VillagerType getHandle() {
            return villagerType;
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }

        @Override
        public int compareTo(Type type) {
            return ordinal - type.ordinal();
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

            if (!(other instanceof CraftType)) {
                return false;
            }

            return getKey().equals(((Type) other).getKey());
        }

        @Override
        public int hashCode() {
            return getKey().hashCode();
        }
    }

    public static class CraftProfession extends Profession {
        private static int count = 0;

        public static Profession minecraftToBukkit(IRegistry<VillagerProfession> registry, VillagerProfession minecraft) {
            if (minecraft == null) {
                return null;
            }

            return Registry.VILLAGER_PROFESSION.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft)));
        }

        public static VillagerProfession bukkitToMinecraft(Profession bukkit) {
            if (bukkit == null) {
                return null;
            }

            return ((CraftProfession) bukkit).getHandle();
        }

        private final NamespacedKey key;
        private final VillagerProfession villagerProfession;
        private final String name;
        private final int ordinal;

        public CraftProfession(NamespacedKey key, VillagerProfession villagerProfession) {
            this.key = key;
            this.villagerProfession = villagerProfession;
            // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
            // in case plugins use for example the name as key in a config file to receive profession specific values.
            // Custom professions will return the key with namespace. For a plugin this should look than like a new profession
            // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
            if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
                this.name = key.getKey().toUpperCase();
            } else {
                this.name = key.toString();
            }
            this.ordinal = count++;
        }

        public VillagerProfession getHandle() {
            return villagerProfession;
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }

        @Override
        public int compareTo(Profession profession) {
            return ordinal - profession.ordinal();
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

            if (!(other instanceof CraftProfession)) {
                return false;
            }

            return getKey().equals(((Profession) other).getKey());
        }

        @Override
        public int hashCode() {
            return getKey().hashCode();
        }
    }
}
