package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Vault;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftVault extends CraftBlockEntityState<VaultBlockEntity> implements Vault {
    private final VaultConfigWrapper config;

    public CraftVault(World world, VaultBlockEntity tileEntity) {
        super(world, tileEntity);
        this.config = new VaultConfigWrapper(tileEntity.getConfig());
    }

    protected CraftVault(CraftVault state, Location location) {
        super(state, location);
        this.config = state.config;
    }

    @Override
    public void addRewardedPlayer(Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        getSnapshot().serverData.addToRewardedPlayers(((CraftPlayer) player).getHandle());
    }

    @Override
    public boolean removeRewardedPlayer(Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        return getSnapshot().serverData.rewardedPlayers.remove(player.getUniqueId());
    }

    @Override
    public boolean hasRewardedPlayer(Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");
        requirePlaced();

        return getSnapshot().serverData.rewardedPlayers.contains(player.getUniqueId());
    }

    @Override
    public Collection<UUID> getRewardedPlayers() {
        return Collections.unmodifiableCollection(getSnapshot().serverData.rewardedPlayers);
    }

    @Override
    public ItemStack getKeyItem() {
        return CraftItemStack.asBukkitCopy(config.keyItem);
    }

    @Override
    public void setKeyItem(ItemStack item) {
        Preconditions.checkArgument(item != null, "Item cannot be null");

        config.keyItem = CraftItemStack.asNMSCopy(item);
    }

    @Override
    protected void applyTo(VaultBlockEntity tileEntity) {
        tileEntity.serverData.set(getSnapshot().serverData);
        tileEntity.setConfig(config.toMinecraft());
    }

    @Override
    public CraftVault copy() {
        return new CraftVault(this, null);
    }

    @Override
    public CraftVault copy(Location location) {
        return new CraftVault(this, location);
    }

    private static class VaultConfigWrapper {
        private ResourceKey<LootTable> lootTable;
        private double activationRange;
        private double deactivationRange;
        private net.minecraft.world.item.ItemStack keyItem;
        private Optional<ResourceKey<LootTable>> overrideLootTableToDisplay;
        private PlayerDetector playerDetector;
        private PlayerDetector.a entitySelector;

        private VaultConfigWrapper(VaultConfig minecraft) {
            this.lootTable = minecraft.lootTable();
            this.activationRange = minecraft.activationRange();
            this.deactivationRange = minecraft.deactivationRange();
            this.keyItem = minecraft.keyItem();
            this.overrideLootTableToDisplay = minecraft.overrideLootTableToDisplay();
            this.playerDetector = minecraft.playerDetector();
            this.entitySelector = minecraft.entitySelector();
        }

        private VaultConfig toMinecraft() {
            return new VaultConfig(lootTable, activationRange, deactivationRange, keyItem, overrideLootTableToDisplay, playerDetector, entitySelector);
        }
    }
}
