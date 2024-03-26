package org.bukkit.craftbukkit.legacy;

import com.google.common.base.Preconditions;
import java.util.Collection;
import net.minecraft.world.entity.ai.attributes.AttributeRanged;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.entity.CraftEnderDragonPart;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.legacy.reroute.DoNotReroute;
import org.bukkit.craftbukkit.legacy.reroute.InjectCompatibility;
import org.bukkit.craftbukkit.legacy.reroute.InjectPluginName;
import org.bukkit.craftbukkit.legacy.reroute.RequirePluginVersion;
import org.bukkit.entity.Damageable;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.VisibleForTesting;

public class AutoExpandMaxAbsorptionCompatibility {

    private static final String VERSION = "1.20.6";
    private static final NamespacedKey MODIFIER_KEY = NamespacedKey.fromString("bukkit:auto-expand-max-absorption-compatibility");

    @RequirePluginVersion(maxInclusive = VERSION)
    public static void setAbsorptionAmount(Damageable damageable, double amount, @InjectPluginName String pluginName, @InjectCompatibility("auto-expand-max-absorption") boolean autoExpandMaxAbsorption) {
        Preconditions.checkArgument(amount >= 0 && Double.isFinite(amount), "amount < 0 or non-finite");
        Preconditions.checkArgument(amount <= ((AttributeRanged) GenericAttributes.MAX_ABSORPTION.value()).getMaxValue(), String.format("Absorption value (%s) must not be bigger than %s", amount, ((AttributeRanged) GenericAttributes.MAX_ABSORPTION.value()).getMaxValue()));
        CraftLivingEntity craftLiving = (CraftLivingEntity) (damageable instanceof CraftEnderDragonPart part ? part.getParent() : damageable);

        AttributeInstance instance = craftLiving.getAttribute(Attribute.GENERIC_MAX_ABSORPTION);
        instance.getModifiers()
                .stream()
                .filter(a -> MODIFIER_KEY.equals(a.getKey()))
                .forEach(instance::removeModifier);

        if (amount <= instance.getValue()) {
            craftLiving.setAbsorptionAmount(amount);
            return;
        }

        if (!autoExpandMaxAbsorption) {
            throw new IllegalArgumentException(String.format("""
                            Absorption value (%s) must be between 0 and %s.
                            Please inform the developer(s) of the plugin '%s' to set the `GENERIC_MAX_ABSORPTION` attribute before calling `LivingEntity#setAbsorptionAmount(double)`.

                            If you have an outdated plugin and cannot update it, you can set `settings.compatibility.auto-expand-max-absorption` to `true` in `bukkit.yml`.
                            Bukkit will then attempt to mitigate the issue. However, this should be considered a last resort or a temporary solution until you can update the plugin.
                            Note that performance and the behavior of other plugins may be affected.
                            This workaround is only available for plugins with an API version lower than or same as %s and may be removed in the future.""",
                    amount, craftLiving.getHandle().getMaxAbsorption(), pluginName, VERSION));
        }

        AttributeModifier modifier = new AttributeModifier(MODIFIER_KEY, calculateTarget(instance.getBaseValue(), amount, instance.getModifiers()), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
        instance.addModifier(modifier);
        craftLiving.setAbsorptionAmount(instance.getValue()); // Use #getValue to account for floating point error in the calculation above
    }

    @DoNotReroute
    @VisibleForTesting
    public static double calculateTarget(double baseValue, double target, Collection<AttributeModifier> modifiers) {
        // Make sure we expand only to the necessary amount
        double addNumber = 0;
        double addScalar = 1;
        double multiplyScalar1 = 1;

        for (AttributeModifier modifier : modifiers) {
            switch (modifier.getOperation()) {
                case ADD_NUMBER -> {
                    addNumber += modifier.getAmount();
                }
                case ADD_SCALAR -> {
                    addScalar += modifier.getAmount();
                }
                case MULTIPLY_SCALAR_1 -> {
                    multiplyScalar1 *= (1 + modifier.getAmount());
                }
                default -> throw new IllegalStateException("Unexpected value: " + modifier.getOperation());
            }
        }

        target /= (multiplyScalar1 * addScalar);
        target -= addNumber;
        target -= baseValue;

        return target;
    }
}
