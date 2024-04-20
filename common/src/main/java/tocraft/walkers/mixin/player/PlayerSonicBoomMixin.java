package tocraft.walkers.mixin.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.SonicBoomUser;

import java.util.HashSet;
import java.util.Set;

@Mixin(Player.class)
public abstract class PlayerSonicBoomMixin extends LivingEntity implements SonicBoomUser {

    @Unique
    private int shape$ability_wardenBoomDelay = -1;

    private PlayerSonicBoomMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public void shape$ability_startSonicBoom() {
        @Nullable LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if (shape instanceof Warden) {
            level().broadcastEntityEvent(this, EntityEvent.SONIC_CHARGE);
            shape$ability_wardenBoomDelay = 40;

            // SFX
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.PLAYERS, 3.0f, 1.0f);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickSonicBoom(CallbackInfo ci) {
        if (!level().isClientSide) {
            shape$ability_wardenBoomDelay = Math.max(-1, shape$ability_wardenBoomDelay - 1);
            if (shape$ability_wardenBoomDelay == 0) {

                // SFX
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0f, 1.0f);

                // Raycast out for sonic boom effect
                float heightOffset = 1.6f;
                int distance = 16;
                Vec3 target = position().add(getLookAngle().scale(distance));
                Vec3 source = position().add(0.0, heightOffset, 0.0);
                Vec3 offsetToTarget = target.subtract(source);
                Vec3 normalized = offsetToTarget.normalize();

                // Spawn particles from the source to the target
                Set<Entity> hit = new HashSet<>();
                for (int particleIndex = 1; particleIndex < Mth.floor(offsetToTarget.length()) + 7; ++particleIndex) {
                    Vec3 particlePos = source.add(normalized.scale(particleIndex));
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);

                    // Locate entities around the particle location for damage
                    hit.addAll(level().getEntitiesOfClass(LivingEntity.class, new AABB(BlockPos.containing(particlePos.x(), particlePos.y(), particlePos.z())).inflate(2), it -> !(it instanceof Wolf)));
                }

                // Don't hit ourselves
                hit.remove((Player) (Object) this);

                // Find
                for (Entity hitTarget : hit) {
                    if (hitTarget instanceof LivingEntity living) {
                        living.hurt(level().damageSources().sonicBoom((Player) (Object) this), 10.0f);
                        double vertical = 0.5 * (1.0 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        double horizontal = 2.5 * (1.0 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        living.push(normalized.x() * horizontal, normalized.y() * vertical, normalized.z() * horizontal);
                    }
                }
            }
        }
    }
}
