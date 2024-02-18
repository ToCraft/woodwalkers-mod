package tocraft.walkers.mixin.player;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.ability.impl.GrassEaterAbility;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.WalkersTickHandler;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.VehiclePackets;

import java.util.function.Predicate;

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

    @Unique
    private static final Predicate<BlockState> walkers$IS_TALL_GRASS = BlockStatePredicate.forBlock(Blocks.GRASS);

    @Inject(method = "tick", at = @At("HEAD"))
    private void sheepServerTick(CallbackInfo info) {
        if (!level().isClientSide && this.isAlive()) {
            ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
            LivingEntity shape = PlayerShape.getCurrentShape(serverPlayer);
            if (shape != null && AbilityRegistry.get(shape) instanceof GrassEaterAbility<?> grassEaterAbility) {
                if (grassEaterAbility.eatTick.get(serverPlayer.getUUID()) != null && grassEaterAbility.eatTick.get(serverPlayer.getUUID()) != 0) {
                    grassEaterAbility.eatTick.put(serverPlayer.getUUID(), Math.max(0, grassEaterAbility.eatTick.get(serverPlayer.getUUID()) - 1));

                    if (shape instanceof Sheep sheepShape)
                        sheepShape.eatAnimationTick = grassEaterAbility.eatTick.get(serverPlayer.getUUID());

                    if (grassEaterAbility.eatTick.get(serverPlayer.getUUID()) == Mth.positiveCeilDiv(4, 2)) {
                        BlockPos blockPos = serverPlayer.blockPosition();
                        if (walkers$IS_TALL_GRASS.test(level().getBlockState(blockPos)) && walkers$isLookingAtPos(blockPos)) {
                            level().destroyBlock(blockPos, false);

                            gameEvent(GameEvent.EAT);
                            serverPlayer.getFoodData().eat(3, 0.2F);

                            if (shape instanceof Sheep sheepShape) sheepShape.setSheared(false);
                        } else {
                            BlockPos blockPos2 = blockPos.below();
                            if (level().getBlockState(blockPos2).is(Blocks.GRASS_BLOCK) && walkers$isLookingAtPos(blockPos2)) {
                                level().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos2, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                                level().setBlock(blockPos2, Blocks.DIRT.defaultBlockState(), 2);

                                gameEvent(GameEvent.EAT);
                                serverPlayer.getFoodData().eat(3, 0.1F);

                                if (shape instanceof Sheep sheepShape) sheepShape.setSheared(false);
                            }
                        }

                    }
                }
            }
        }
    }

    @Unique
    private boolean walkers$isLookingAtPos(BlockPos blockPos) {
        Player player = (Player) (Object) this;
        return player.pick(2, 0, false) instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos().equals(blockPos);
    }
}
