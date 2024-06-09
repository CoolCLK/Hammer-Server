package org.bukkit.craftbukkit.legacy;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.legacy.reroute.InjectCompatibility;
import org.bukkit.craftbukkit.legacy.reroute.InjectPluginName;
import org.bukkit.craftbukkit.legacy.reroute.NotInBukkit;
import org.bukkit.craftbukkit.legacy.reroute.RequirePluginVersion;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;

public class ParticleRerouting {

    @RequirePluginVersion(maxInclusive = "1.20.6")
    public static Class<?> getDataType(Particle particle, @InjectPluginName String pluginName, @InjectCompatibility("return-void-particle-data-type") boolean returnVoidParticleDataType) {
        if (particle.isTyped()) {
            return particle.getDataType();
        }

        if (returnVoidParticleDataType) {
            return Void.class;
        }

        throw new UnsupportedOperationException(String.format("""
                Cannot get data type for particle '%s' since it is not typed.
                Please inform the developer(s) of the plugin '%s' to check if `Particle#isTyped` is `true` before calling `Particle#getDataType`.

                If you have an outdated plugin and cannot update it, you can set `settings.compatibility.return-void-particle-data-type` to `true` in `bukkit.yml`.
                Bukkit will then attempt to mitigate the issue. However, this should be considered a last resort or a temporary solution until you can update the plugin.
                Note that performance and the behavior of other plugins may be affected.
                This workaround is only available for plugins with an API version lower than or same as 1.20.6 and may be removed in the future.""", particle, pluginName));
    }

    @NotInBukkit
    public static <T> void setParticle(AreaEffectCloud areaEffectCloud, Particle particle, T data) {
        if (particle.isTyped()) {
            areaEffectCloud.setParticle((Particle.Typed<T>) particle.typed(particle.getDataType()), data);
            return;
        }

        Preconditions.checkArgument(data == null, "Particle %s does not take any data.", particle);

        areaEffectCloud.setParticle(particle);
    }

    @NotInBukkit
    public static <T> void spawnParticle(Player player, Particle particle, Location location, int count, T data) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(Player player, Particle particle, double x, double y, double z, int count, T data) {
        spawnParticle(player, particle, x, y, z, count, 0, 0, 0, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(Player player, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(Player player, Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(player, particle, x, y, z, count, offsetX, offsetY, offsetZ, 1, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(Player player, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(Player player, Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(player, particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, false);
    }

    @NotInBukkit
    public static <T> void spawnParticle(Player player, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(Player player, Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
        if (particle.isTyped()) {
            player.spawnParticle((Particle.Typed<T>) particle.typed(particle.getDataType()), x, y, z, count, offsetX, offsetY, offsetZ, extra, data, force);
            return;
        }

        Preconditions.checkArgument(data == null, "Particle %s does not take any data.", particle);

        player.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, force);
    }

    @NotInBukkit
    public static <T> void spawnParticle(World world, Particle particle, Location location, int count, T data) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(World world, Particle particle, double x, double y, double z, int count, T data) {
        spawnParticle(world, particle, x, y, z, count, 0, 0, 0, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(World world, Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(world, particle, x, y, z, count, offsetX, offsetY, offsetZ, 1, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
    }

    @NotInBukkit
    public static <T> void spawnParticle(World world, Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(world, particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, false);
    }

    @NotInBukkit
    public static <T> void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, force);
    }

    @NotInBukkit
    public static <T> void spawnParticle(World world, Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
        if (particle.isTyped()) {
            world.spawnParticle((Particle.Typed<T>) particle.typed(particle.getDataType()), x, y, z, count, offsetX, offsetY, offsetZ, extra, data, force);
            return;
        }

        Preconditions.checkArgument(data == null, "Particle %s does not take any data.", particle);

        world.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, force);
    }
}
