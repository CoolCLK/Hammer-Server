package org.bukkit.craftbukkit.block.banner;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.NotNull;

public class CraftPatternType extends PatternType implements Handleable<EnumBannerPatternType> {

    private static int count = 0;

    public static PatternType minecraftToBukkit(EnumBannerPatternType minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.BANNER_PATTERN, Registry.BANNER_PATTERN);
    }

    public static PatternType minecraftHolderToBukkit(Holder<EnumBannerPatternType> minecraft) {
        return minecraftToBukkit(minecraft.value());
    }

    public static EnumBannerPatternType bukkitToMinecraft(PatternType bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static Holder<EnumBannerPatternType> bukkitToMinecraftHolder(PatternType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        IRegistry<EnumBannerPatternType> registry = CraftRegistry.getMinecraftRegistry(Registries.BANNER_PATTERN);

        if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.c<EnumBannerPatternType> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own banner pattern without properly registering it.");
    }

    private final NamespacedKey key;
    private final EnumBannerPatternType bannerPatternType;
    private final String name;
    private final int ordinal;

    public CraftPatternType(NamespacedKey key, EnumBannerPatternType bannerPatternType) {
        this.key = key;
        this.bannerPatternType = bannerPatternType;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive pattern type specific values.
        // Custom pattern types will return the key with namespace. For a plugin this should look than like a new pattern type
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase(Locale.ROOT);
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    @Override
    public EnumBannerPatternType getHandle() {
        return bannerPatternType;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(PatternType patternType) {
        return ordinal - patternType.ordinal();
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

        if (!(other instanceof CraftPatternType)) {
            return false;
        }

        return getKey().equals(((PatternType) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        if (this == PatternType.BASE) {
            return "b";
        }
        if (this == PatternType.SQUARE_BOTTOM_LEFT) {
            return "bl";
        }
        if (this == PatternType.SQUARE_BOTTOM_RIGHT) {
            return "br";
        }
        if (this == PatternType.SQUARE_TOP_LEFT) {
            return "tl";
        }
        if (this == PatternType.SQUARE_TOP_RIGHT) {
            return "tr";
        }
        if (this == PatternType.STRIPE_BOTTOM) {
            return "bs";
        }
        if (this == PatternType.STRIPE_TOP) {
            return "ts";
        }
        if (this == PatternType.STRIPE_LEFT) {
            return "ls";
        }
        if (this == PatternType.STRIPE_RIGHT) {
            return "rs";
        }
        if (this == PatternType.STRIPE_CENTER) {
            return "cs";
        }
        if (this == PatternType.STRIPE_MIDDLE) {
            return "ms";
        }
        if (this == PatternType.STRIPE_DOWNRIGHT) {
            return "drs";
        }
        if (this == PatternType.STRIPE_DOWNLEFT) {
            return "dls";
        }
        if (this == PatternType.SMALL_STRIPES) {
            return "ss";
        }
        if (this == PatternType.CROSS) {
            return "cr";
        }
        if (this == PatternType.STRAIGHT_CROSS) {
            return "sc";
        }
        if (this == PatternType.TRIANGLE_BOTTOM) {
            return "bt";
        }
        if (this == PatternType.TRIANGLE_TOP) {
            return "tt";
        }
        if (this == PatternType.TRIANGLES_BOTTOM) {
            return "bts";
        }
        if (this == PatternType.TRIANGLES_TOP) {
            return "tts";
        }
        if (this == PatternType.DIAGONAL_LEFT) {
            return "ld";
        }
        if (this == PatternType.DIAGONAL_UP_RIGHT) {
            return "rd";
        }
        if (this == PatternType.DIAGONAL_UP_LEFT) {
            return "lud";
        }
        if (this == PatternType.DIAGONAL_RIGHT) {
            return "rud";
        }
        if (this == PatternType.CIRCLE) {
            return "mc";
        }
        if (this == PatternType.RHOMBUS) {
            return "mr";
        }
        if (this == PatternType.HALF_VERTICAL) {
            return "vh";
        }
        if (this == PatternType.HALF_HORIZONTAL) {
            return "hh";
        }
        if (this == PatternType.HALF_VERTICAL_RIGHT) {
            return "vhr";
        }
        if (this == PatternType.HALF_HORIZONTAL_BOTTOM) {
            return "hhb";
        }
        if (this == PatternType.BORDER) {
            return "bo";
        }
        if (this == PatternType.CURLY_BORDER) {
            return "cbo";
        }
        if (this == PatternType.CREEPER) {
            return "cre";
        }
        if (this == PatternType.GRADIENT) {
            return "gra";
        }
        if (this == PatternType.GRADIENT_UP) {
            return "gru";
        }
        if (this == PatternType.BRICKS) {
            return "bri";
        }
        if (this == PatternType.SKULL) {
            return "sku";
        }
        if (this == PatternType.FLOWER) {
            return "flo";
        }
        if (this == PatternType.MOJANG) {
            return "moj";
        }
        if (this == PatternType.GLOBE) {
            return "glb";
        }
        if (this == PatternType.PIGLIN) {
            return "pig";
        }
        if (this == PatternType.FLOW) {
            return "flw";
        }
        if (this == PatternType.GUSTER) {
            return "gus";
        }

        return getKey().toString(); // Default to key as identifier
    }
}
