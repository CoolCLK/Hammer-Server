package org.bukkit.support;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

public final class LegacyHelper {

    // Materials that only exist in block form (or are legacy)
    private static final List<Material> INVALIDATED_MATERIALS;

    static {
        INVALIDATED_MATERIALS = Arrays.stream(Material.values())
                .filter(m -> m.isLegacy() || CraftMagicNumbers.getItem(m) == null)
                .toList();
    }

    private LegacyHelper() {
    }

    public static List<Material> getInvalidatedMaterials() {
        return INVALIDATED_MATERIALS;
    }
}
