package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.monster.EntityZombieVillager;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.registry.CraftOldEnumRegistryItem;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;

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
        return CraftProfession.minecraftToBukkit(getHandle().getVillagerData().getProfession());
    }

    @Override
    public void setProfession(Profession profession) {
        Preconditions.checkArgument(profession != null, "Profession cannot be null");
        getHandle().setVillagerData(getHandle().getVillagerData().setProfession(CraftProfession.bukkitToMinecraft(profession)));
    }

    @Override
    public Type getVillagerType() {
        return CraftType.minecraftToBukkit(getHandle().getVillagerData().getType());
    }

    @Override
    public void setVillagerType(Type type) {
        Preconditions.checkArgument(type != null, "Type cannot be null");
        getHandle().setVillagerData(getHandle().getVillagerData().setType(CraftType.bukkitToMinecraft(type)));
    }

    @Override
    public int getVillagerLevel() {
        return getHandle().getVillagerData().getLevel();
    }

    @Override
    public void setVillagerLevel(int level) {
        Preconditions.checkArgument(1 <= level && level <= 5, "level (%s) must be between [1, 5]", level);

        getHandle().setVillagerData(getHandle().getVillagerData().setLevel(level));
    }

    @Override
    public int getVillagerExperience() {
        return getHandle().getVillagerXp();
    }

    @Override
    public void setVillagerExperience(int experience) {
        Preconditions.checkArgument(experience >= 0, "Experience (%s) must be positive", experience);

        getHandle().setVillagerXp(experience);
    }

    @Override
    public boolean sleep(Location location) {
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
        Preconditions.checkArgument(location.getWorld().equals(getWorld()), "Cannot sleep across worlds");
        Preconditions.checkState(!getHandle().generation, "Cannot sleep during world generation");

        BlockPosition position = CraftLocation.toBlockPosition(location);
        IBlockData iblockdata = getHandle().level().getBlockState(position);
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
        EntityZombieVillager entityzombievillager = EntityZombie.convertVillagerToZombieVillager(getHandle().level().getMinecraftWorld(), getHandle(), getHandle().blockPosition(), isSilent(), EntityTransformEvent.TransformReason.INFECTION, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (entityzombievillager != null) ? (ZombieVillager) entityzombievillager.getBukkitEntity() : null;
    }

    public static class CraftType extends CraftOldEnumRegistryItem<Type, VillagerType> implements Type {
        private static int count = 0;

        public static Type minecraftToBukkit(VillagerType minecraft) {
            return CraftRegistry.minecraftToBukkit(minecraft, Registries.VILLAGER_TYPE, Registry.VILLAGER_TYPE);
        }

        public static VillagerType bukkitToMinecraft(Type bukkit) {
            return CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public CraftType(NamespacedKey key, Holder<VillagerType> handle) {
            super(key, handle, count++);
        }

        @Override
        public NamespacedKey getKey() {
            return getKeyOrThrow();
        }
    }

    public static class CraftProfession extends CraftOldEnumRegistryItem<Profession, VillagerProfession> implements Profession {
        private static int count = 0;

        public static Profession minecraftToBukkit(VillagerProfession minecraft) {
            return CraftRegistry.minecraftToBukkit(minecraft, Registries.VILLAGER_PROFESSION, Registry.VILLAGER_PROFESSION);
        }

        public static VillagerProfession bukkitToMinecraft(Profession bukkit) {
            return CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public CraftProfession(NamespacedKey key, Holder<VillagerProfession> handle) {
            super(key, handle, count++);
        }

        @Override
        public NamespacedKey getKey() {
            return getKeyOrThrow();
        }
    }
}
