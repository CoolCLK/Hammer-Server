package org.bukkit;

import static org.bukkit.support.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.bukkit.craftbukkit.CraftArt;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.support.AbstractTestingBase;
import org.junit.jupiter.api.Test;

public class ArtTest extends AbstractTestingBase {

    @Test
    public void verifyMapping() {
        List<Art> arts = Lists.newArrayList(Art.values());

        for (ResourceKey<PaintingVariant> key : CraftRegistry.getMinecraftRegistry(Registries.PAINTING_VARIANT).registryKeySet()) {
            Holder<PaintingVariant> enumArt = CraftRegistry.getMinecraftRegistry(Registries.PAINTING_VARIANT).getHolderOrThrow(key);
            String name = key.location().getPath();
            int width = enumArt.value().width();
            int height = enumArt.value().height();

            Art subject = CraftArt.minecraftHolderToBukkit(enumArt);

            String message = String.format("org.bukkit.Art is missing '%s'", name);
            assertNotNull(subject, message);

            assertThat(Art.getByName(name), is(subject));
            assertThat(subject.getBlockWidth(), is(width), "Art." + subject + "'s width");
            assertThat(subject.getBlockHeight(), is(height), "Art." + subject + "'s height");

            arts.remove(subject);
        }

        assertThat(arts, is(Collections.EMPTY_LIST), "org.bukkit.Art has too many arts");
    }

    @Test
    public void testCraftArtToNotch() {
        Map<Holder<PaintingVariant>, Art> cache = new HashMap<>();
        for (Art art : Art.values()) {
            Holder<PaintingVariant> enumArt = CraftArt.bukkitToMinecraftHolder(art);
            assertNotNull(enumArt, art.name());
            assertThat(cache.put(enumArt, art), is(nullValue()), art.name());
        }
    }

    @Test
    public void testCraftArtToBukkit() {
        Map<Art, Holder<PaintingVariant>> cache = new HashMap();
        for (Holder<PaintingVariant> enumArt : CraftRegistry.getMinecraftRegistry(Registries.PAINTING_VARIANT).asHolderIdMap()) {
            Art art = CraftArt.minecraftHolderToBukkit(enumArt);
            assertNotNull(art, "Could not CraftArt.NotchToBukkit " + enumArt);
            assertThat(cache.put(art, enumArt), is(nullValue()), "Duplicate artwork " + enumArt);
        }
    }

    @Test
    public void getByNullName() {
        assertThrows(IllegalArgumentException.class, () -> Art.getByName(null));
    }

    @Test
    public void getById() {
        for (Art art : Art.values()) {
            assertThat(Art.getById(art.getId()), is(art));
        }
    }

    @Test
    public void getByName() {
        for (Art art : Art.values()) {
            assertThat(Art.getByName(art.toString()), is(art));
        }
    }

    @Test
    public void getByNameWithMixedCase() {
        Art subject = Art.values()[0];
        String name = subject.toString().replace('E', 'e');

        assertThat(Art.getByName(name), is(subject));
    }
}
