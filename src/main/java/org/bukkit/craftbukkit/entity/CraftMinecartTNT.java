package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.vehicle.EntityMinecartTNT;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.ExplosiveMinecart;

final class CraftMinecartTNT extends CraftMinecart implements ExplosiveMinecart {
    CraftMinecartTNT(CraftServer server, EntityMinecartTNT entity) {
        super(server, entity);
    }

    @Override
    public void setFuseTicks(int ticks) {
        getHandle().fuse = ticks;
    }

    @Override
    public int getFuseTicks() {
        return getHandle().getFuse();
    }

    @Override
    public void ignite() {
        getHandle().primeFuse();
    }

    @Override
    public boolean isIgnited() {
        return getHandle().isPrimed();
    }

    @Override
    public void explode() {
        getHandle().explode(getHandle().getDeltaMovement().horizontalDistanceSqr());
    }

    @Override
    public void explode(double power) {
        getHandle().explode(power);
    }

    @Override
    public EntityMinecartTNT getHandle() {
        return (EntityMinecartTNT) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftMinecartTNT";
    }

    @Override
    public EntityType getType() {
        return EntityType.MINECART_TNT;
    }
}
