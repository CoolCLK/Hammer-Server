package org.bukkit.craftbukkit.legacy;

import org.bukkit.Location;
import org.bukkit.RegionAccessor;
import org.bukkit.craftbukkit.legacy.reroute.NotInBukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityRerouting {

    @NotInBukkit
    public static Entity spawnEntity(RegionAccessor regionAccessor, Location location, EntityType entityType) {
        return regionAccessor.spawnEntity(location, entityType.typed(entityType.getEntityClass()));
    }

    @NotInBukkit
    public static Entity spawnEntity(RegionAccessor regionAccessor, Location location, EntityType entityType, boolean randomizeData) {
        return regionAccessor.spawnEntity(location, entityType.typed(entityType.getEntityClass()), randomizeData);
    }
}
