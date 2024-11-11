package org.bukkit.craftbukkit.inventory.components.consumable.effects;

import java.util.Map;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;

public abstract class CraftConsumableEffect<T extends ConsumeEffect> implements ConsumableEffect {

    public static <T extends CraftConsumableEffect> T minecraftToBukkitSpecific(ConsumeEffect effect) {
        if (effect instanceof TeleportRandomlyConsumeEffect nmsEffect) {
            return ((T) new CraftConsumableTeleportRandomly(nmsEffect));
        } else if (effect instanceof RemoveStatusEffectsConsumeEffect nmsEffect) {
            return ((T) new CraftConsumableRemoveEffect(nmsEffect));
        } else if (effect.getType().equals(ConsumeEffect.a.CLEAR_ALL_EFFECTS)) {

        } else if (effect.getType().equals(ConsumeEffect.a.PLAY_SOUND)) {

        } else if (effect.getType().equals(ConsumeEffect.a.TELEPORT_RANDOMLY)) {

        }
        throw new IllegalStateException("Unexpected value: " + effect.getType());
    }

    public static <T extends ConsumeEffect> T bukkitToMinecraftSpecific(CraftConsumableEffect effect) {
        if (effect instanceof CraftConsumableTeleportRandomly bukkitEffect) {
            return (T) bukkitEffect.getHandle();
        } else if (effect instanceof CraftConsumableRemoveEffect bukkitEffect) {
            return (T) bukkitEffect.getHandle();
        }
        throw new IllegalStateException("Unexpected value: " + effect.toString());
    }

    T handle;

    public CraftConsumableEffect(T consumeEffect) {
        this.handle = consumeEffect;
    }

    public CraftConsumableEffect(CraftConsumableEffect consumeEffect) {
        this.handle = (T) consumeEffect.handle;
    }

    public CraftConsumableEffect(Map<String, Object> map) {
    }

    public T getHandle() {
        return handle;
    }

}
