package tocraft.walkers.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.network.impl.SwapVariantPackets;
import tocraft.walkers.network.impl.UnlockPackets;

public class ServerNetworking implements NetworkHandler {

    public static void initialize() {
        SwapPackets.registerWalkersRequestPacketHandler();
        UnlockPackets.registerShapeUnlockRequestPacketHandler();
        SwapVariantPackets.registerSwapVariantPacketHandler();
        registerUseAbilityPacketHandler();
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerUseAbilityPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, USE_ABILITY, (buf, context) -> {
            Player player = context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                // Verify we should use ability for the player's current shape
                if (shape != null) {
                    if (AbilityRegistry.has(shape)) {
                        // Check cooldown
                        if (PlayerAbilities.canUseAbility(player)) {
                            AbilityRegistry.get(shape).onUse(player, shape, context.getPlayer().level());
                            PlayerAbilities.setCooldown(player, AbilityRegistry.get(shape).getCooldown(shape));
                            PlayerAbilities.sync((ServerPlayer) player);
                        }
                    }
                }
            });
        });
    }
}
