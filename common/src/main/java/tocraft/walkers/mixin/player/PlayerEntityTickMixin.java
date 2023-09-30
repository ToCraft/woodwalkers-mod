package tocraft.walkers.mixin.player;

import tocraft.walkers.api.WalkersTickHandler;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityTickMixin extends LivingEntity {

    private PlayerEntityTickMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
    @Inject(method = "tick", at = @At("HEAD"))
    private void serverTick(CallbackInfo info) {
        // Tick WalkersTickHandlers on the client & server.
        @Nullable LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if(shape != null) {
            @Nullable WalkersTickHandler handler = WalkersTickHandlers.getHandlers().get(shape.getType());
            if(handler != null) {
                handler.tick((Player) (Object) this, shape);
            }
        }

        // Update misc. server-side entity properties for the player.
        if(!level.isClientSide) {
            PlayerDataProvider data = (PlayerDataProvider) this;
            data.setRemainingHostilityTime(Math.max(0, data.getRemainingHostilityTime() - 1));

            // Update cooldown & Sync
            ServerPlayer player = (ServerPlayer) (Object) this;
            PlayerAbilities.setCooldown(player, Math.max(0, data.getAbilityCooldown() - 1));
            PlayerAbilities.sync(player);
        }
    }
}
