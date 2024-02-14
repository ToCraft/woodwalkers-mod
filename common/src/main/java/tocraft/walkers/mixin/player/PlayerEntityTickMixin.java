package tocraft.walkers.mixin.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.WalkersTickHandler;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.VehiclePackets;

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
        if (shape != null) {
            @Nullable WalkersTickHandler handler = WalkersTickHandlers.getHandlers().get(shape.getType());
            if (handler != null) {
                handler.tick((Player) (Object) this, shape);
            }
        }

        // Update misc. server-side entity properties for the player.
        if (!level().isClientSide) {
            PlayerDataProvider data = (PlayerDataProvider) this;
            data.walkers$setRemainingHostilityTime(Math.max(0, data.walkers$getRemainingHostilityTime() - 1));

            // Update cooldown & Sync
            ServerPlayer player = (ServerPlayer) (Object) this;
            PlayerAbilities.setCooldown(player, Math.max(0, data.walkers$getAbilityCooldown() - 1));
            PlayerAbilities.sync(player);

            VehiclePackets.sync((ServerPlayer) (Object) this);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void pufferfishServerTick(CallbackInfo info) {
        if (!level().isClientSide && this.isAlive()) {
            LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
            if (shape instanceof Pufferfish pufferfishShape) {
                if (pufferfishShape.inflateCounter > 0) {
                    if (pufferfishShape.getPuffState() == 0) {
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
                        pufferfishShape.setPuffState(1);
                    } else if (pufferfishShape.inflateCounter > 40 && pufferfishShape.getPuffState() == 1) {
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
                        pufferfishShape.setPuffState(2);
                    }

                    ++pufferfishShape.inflateCounter;
                } else if (pufferfishShape.getPuffState() != 0) {
                    if (pufferfishShape.deflateTimer > 60 && pufferfishShape.getPuffState() == 2) {
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
                        pufferfishShape.setPuffState(1);
                    } else if (pufferfishShape.deflateTimer > 100 && pufferfishShape.getPuffState() == 1) {
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
                        pufferfishShape.setPuffState(0);
                    }

                    ++pufferfishShape.deflateTimer;
                }
            }
        }
    }
}
