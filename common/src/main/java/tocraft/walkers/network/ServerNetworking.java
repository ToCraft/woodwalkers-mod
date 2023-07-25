package tocraft.walkers.network;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.network.impl.DevSwapPackets;
import tocraft.walkers.network.impl.SwapPackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworking implements NetworkHandler {

    public static void initialize() {
        SwapPackets.registerWalkersRequestPacketHandler();
        DevSwapPackets.registerDevRequestPacketHandler();
    }

    public static void registerUseAbilityPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, USE_ABILITY, (buf, context) -> {
            PlayerEntity player = context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                // Verify we should use ability for the player's current shape
                if(shape != null) {
                    EntityType<?> shapeType = shape.getType();

                    if(AbilityRegistry.has(shapeType)) {

                        // Check cooldown
                        if(PlayerAbilities.canUseAbility(player)) {
                            AbilityRegistry.get(shapeType).onUse(player, shape, context.getPlayer().world);
                            PlayerAbilities.setCooldown(player, AbilityRegistry.get(shapeType).getCooldown(shape));
                            PlayerAbilities.sync((ServerPlayerEntity) player);
                        }
                    }
                }
            });
        });
    }
}
