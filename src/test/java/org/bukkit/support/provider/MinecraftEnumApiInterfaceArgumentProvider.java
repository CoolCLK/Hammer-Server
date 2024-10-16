package org.bukkit.support.provider;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import org.bukkit.entity.PoiType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;


public class MinecraftEnumApiInterfaceArgumentProvider implements ArgumentsProvider {

    private static List<Arguments> DATA = Lists.newArrayList();

    static {
        register(PoiType.Occupancy.class, VillagePlace.Occupancy.class);
    }

    private static void register(Class bukkit, Class minecraft) {
        DATA.add(Arguments.of(bukkit, minecraft));
    }

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) throws Exception {
        return DATA.stream();
    }
}
