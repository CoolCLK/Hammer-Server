package org.bukkit.craftbukkit;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.support.environment.Normal;
import org.bukkit.support.provider.MinecraftEnumApiInterfaceArgumentProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@Normal
public class MinecraftEnumApiInterfaceTest {

    @ParameterizedTest
    @ArgumentsSource(MinecraftEnumApiInterfaceArgumentProvider.class)
    public void testShouldKeysMatch(Class<?> bukkit, Class<?> craft, Class<? extends Enum> minecraft) {
        assertTrue(bukkit.isInterface(), "API class for all Minecraft Enum Api Interface tests must be interfaces");
        assertTrue(minecraft.isEnum(), "Minecraft class for all Minecraft Enum Api Interface tests must be enums");
        final Set<String> bukkitEntries = Arrays.stream(bukkit.getDeclaredFields())
                .filter((field) -> bukkit.isAssignableFrom(field.getType()))
                .map(Field::getName).collect(Collectors.toSet());

        final List<String> notAdded = new ArrayList<>();
        for (final String minecraftName : Arrays.stream(minecraft.getEnumConstants()).map(Enum::name).toList()) {
            final boolean removed = bukkitEntries.remove(minecraftName);
            if (!removed) {
                notAdded.add(minecraftName);
            }
        }

        assertTrue(notAdded.isEmpty(), "Bukkit interface %s missing entry(s) %s from Minecraft enum %s".formatted(bukkit.getName(), notAdded, minecraft.getName()));
        assertTrue(bukkitEntries.isEmpty(), "Extra entries %s not found in Minecraft enum %s, but is within Bukkit interface %s".formatted(bukkitEntries, bukkit.getName(), minecraft.getName()));
    }

    @ParameterizedTest
    @ArgumentsSource(MinecraftEnumApiInterfaceArgumentProvider.class)
    public void testEnsureClassStructure(Class<?> bukkit, Class<?> craft, Class<? extends Enum> minecraft) {
        String message = "Craftbukkit class %s must have \"public static %s minecraftToBukkit(%s)".formatted(
                craft.getSimpleName(), bukkit.getName(), minecraft.getName()
        );
        Method method = assertDoesNotThrow(() -> craft.getDeclaredMethod("minecraftToBukkit", minecraft), message);
        assertTrue(Modifier.isStatic(method.getModifiers()), message);
        assertEquals(method.getReturnType(), bukkit, message);

        message = "CraftBukkit class %s must have \"public static %s bukkitToMinecraft(%s)".formatted(
                craft.getSimpleName(), minecraft.getName(), bukkit.getName()
        );
        method = assertDoesNotThrow(() -> craft.getDeclaredMethod("bukkitToMinecraft", bukkit));
        assertTrue(Modifier.isStatic(method.getModifiers()), message);
        assertEquals(method.getReturnType(), minecraft);
    }
}
