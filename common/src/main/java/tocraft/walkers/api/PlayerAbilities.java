package tocraft.walkers.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import tocraft.craftedcore.network.ModernNetworking;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.events.ShapeEvents;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.NetworkHandler;

public class PlayerAbilities {

    /**
     * Returns an integer representing the current ability cooldown of the specified {@link Player} in ticks.
     *
     * <p>
     * A return value of {@code 0} represents no cooldown, while 20 is 1 second.
     *
     * @param player player to retrieve ability cooldown for
     * @return cooldown, in ticks, of the specified player's ability
     */
    public static int getCooldown(Player player) {
        return ((PlayerDataProvider) player).walkers$getAbilityCooldown();
    }

    public static boolean canUseAbility(@NotNull Player player) {
        return !player.isSpectator() && ((PlayerDataProvider) player).walkers$getAbilityCooldown() <= 0;
    }

    public static void setCooldown(Player player, int cooldown) {
        ((PlayerDataProvider) player).walkers$setAbilityCooldown(cooldown);
    }

    @ApiStatus.Internal
    public static void sync(ServerPlayer player) {
        CompoundTag packet = new CompoundTag();
        packet.putInt("cooldown", ((PlayerDataProvider) player).walkers$getAbilityCooldown());
        ModernNetworking.sendToPlayer(player, NetworkHandler.ABILITY_SYNC, packet);
    }

    public static void useAbility(ServerPlayer player) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        // Verify we should use ability for the player's current shape
        if (shape != null) {
            if (AbilityRegistry.has(shape)) {
                // Check cooldown
                if (PlayerAbilities.canUseAbility(player)) {
                    ShapeAbility<LivingEntity> ability = AbilityRegistry.get(shape);
                    if (ability == null) {
                        return;
                    }

                    InteractionResult result = ShapeEvents.USE_SHAPE_ABILITY.invoke().use(player, ability);
                    if (result == InteractionResult.FAIL) {
                        return;
                    }

                    ability.onUse(player, shape, player.level());
                    PlayerAbilities.setCooldown(player, ability.getCooldown(shape));
                    PlayerAbilities.sync(player);
                }
            }
        }
    }
}
