package org.bukkit.craftbukkit.potion;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.PotionRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftPotionType extends PotionType implements Handleable<PotionRegistry> {
    private static int count = 0;

    public static PotionType minecraftHolderToBukkit(Holder<PotionRegistry> minecraft) {
        return minecraftToBukkit(minecraft.value());
    }

    public static PotionType minecraftToBukkit(PotionRegistry minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.POTION, Registry.POTION);
    }

    public static PotionRegistry bukkitToMinecraft(PotionType bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static Holder<PotionRegistry> bukkitToMinecraftHolder(PotionType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        IRegistry<PotionRegistry> registry = CraftRegistry.getMinecraftRegistry(Registries.POTION);

        if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.c<PotionRegistry> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
    }

    public static String bukkitToString(PotionType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return bukkit.getKey().toString();
    }

    public static PotionType stringToBukkit(String string) {
        Preconditions.checkArgument(string != null);

        // We currently do not have any version-dependent remapping, so we can use current version
        // First convert from when only the names where saved
        string = FieldRename.convertPotionTypeName(ApiVersion.CURRENT, string);
        string = string.toLowerCase(Locale.ROOT);
        NamespacedKey key = NamespacedKey.fromString(string);

        // Now also convert from when keys where saved
        return CraftRegistry.get(Registry.POTION, key, ApiVersion.CURRENT);
    }

    private final NamespacedKey key;
    private final PotionRegistry potion;
    private final Supplier<List<PotionEffect>> potionEffects;
    private final Supplier<Boolean> upgradeable;
    private final Supplier<Boolean> extendable;
    private final Supplier<Integer> maxLevel;
    private final String name;
    private final int ordinal;

    public CraftPotionType(NamespacedKey key, PotionRegistry potion) {
        this.key = key;
        this.potion = potion;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive potion specific values.
        // Custom potions will return the key with namespace. For a plugin this should look than like a new potion
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase(Locale.ROOT);
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;

        this.potionEffects = Suppliers.memoize(() -> potion.getEffects().stream().map(CraftPotionUtil::toBukkit).toList());
        this.upgradeable = Suppliers.memoize(() -> Registry.POTION.get(new NamespacedKey(key.getNamespace(), "strong_" + key.getKey())) != null);
        this.extendable = Suppliers.memoize(() -> Registry.POTION.get(new NamespacedKey(key.getNamespace(), "long_" + key.getKey())) != null);
        this.maxLevel = Suppliers.memoize(() -> isUpgradeable() ? 2 : 1);
    }

    @Override
    public PotionRegistry getHandle() {
        return potion;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(PotionType potionType) {
        return ordinal - potionType.ordinal();
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

        if (!(other instanceof CraftPotionType)) {
            return false;
        }

        return getKey().equals(((PotionType) other).getKey());
    }

    @Nullable
    @Override
    public PotionEffectType getEffectType() {
        return getPotionEffects().isEmpty() ? null : getPotionEffects().get(0).getType();
    }

    @NotNull
    @Override
    public List<PotionEffect> getPotionEffects() {
        return potionEffects.get();
    }

    @Override
    public boolean isInstant() {
        return potion.hasInstantEffects();
    }

    @Override
    public boolean isUpgradeable() {
        return upgradeable.get();
    }

    @Override
    public boolean isExtendable() {
        return extendable.get();
    }

    @Override
    public int getMaxLevel() {
        return maxLevel.get();
    }
}
