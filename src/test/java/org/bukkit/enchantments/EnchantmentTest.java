package org.bukkit.enchantments;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.support.AbstractTestingBase;
import org.junit.jupiter.api.Test;

public class EnchantmentTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraftFieldName() {
        for (Field field : Enchantment.class.getFields()) {
            if (field.getType() != Enchantment.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            assertNotNull(Registry.ENCHANTMENT.get(NamespacedKey.fromString(name.toLowerCase(Locale.ROOT))), "No enchantment for field name " + name);
        }
    }

    @Test
    public void testMinecraftToBukkitFieldName() {
        for (net.minecraft.world.item.enchantment.Enchantment enchantment : CraftRegistry.getMinecraftRegistry(Registries.ENCHANTMENT)) {
            MinecraftKey minecraftKey = CraftRegistry.getMinecraftRegistry(Registries.ENCHANTMENT).getKey(enchantment);

            try {
                Enchantment bukkit = (Enchantment) Enchantment.class.getField(minecraftKey.getPath().toUpperCase(Locale.ROOT)).get(null);

                assertEquals(minecraftKey, CraftNamespacedKey.toMinecraft(bukkit.getKey()), "Keys are not the same for " + minecraftKey);
            } catch (NoSuchFieldException e) {
                fail("No Bukkit default enchantment for " + minecraftKey);
            } catch (IllegalAccessException e) {
                fail("Bukkit field is not access able for " + minecraftKey);
            } catch (ClassCastException e) {
                fail("Bukkit field is not of type enchantment for" + minecraftKey);
            }
        }
    }
}
