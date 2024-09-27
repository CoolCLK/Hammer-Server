package org.bukkit.craftbukkit.util;

import org.bukkit.Location;
import org.bukkit.entity.PoiType;
import org.bukkit.util.PoiSearchResult;
import org.jetbrains.annotations.NotNull;

public class CraftPoiSearchResult implements PoiSearchResult {

    private final PoiType type;
    private final Location location;

    public CraftPoiSearchResult(PoiType type, Location location) {
        this.type = type;
        this.location = location;
    }

    @NotNull
    @Override
    public PoiType getPoiType() {
        return this.type;
    }

    @NotNull
    @Override
    public Location getLocation() {
        return this.location;
    }
}
