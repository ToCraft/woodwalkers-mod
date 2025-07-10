package tocraft.walkers.ability.impl.specific;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

import java.util.List;

public class GuardianAbility<T extends Guardian> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("guardian");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(@NotNull ServerPlayer player, @NotNull T shape, @NotNull ServerLevel world) {
        int beamLength = 10; // 10 blocks
        int particleFreq = 5;

        // Calculate the direction vector from player to target (assuming shape provides target coordinates)
        Vec3 lookVec = player.getForward().normalize();

        // Emit particles along the beam's path
        for (int i = 0; i <= beamLength * particleFreq; i++) {
            Vec3 beamPos = player.getEyePosition(1.0F).add(lookVec.scale((double) i / particleFreq));
            world.sendParticles(ParticleTypes.BUBBLE, beamPos.x, beamPos.y, beamPos.z, 0, 0, 0, 0, 0);
        }

        // Play a sound effect
        player.playSound(SoundEvents.GUARDIAN_ATTACK, 1.0F, 1.5F);

        // damage entities in the beam's path if the player is in water
        if (player.isInWater()) {
            AABB box = new AABB(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(lookVec.scale(beamLength)));
            List<LivingEntity> entitiesInBeam = world.getEntitiesOfClass(LivingEntity.class, box);
            for (LivingEntity entity : entitiesInBeam) {
                if (entity != player) {
                    entity.hurtServer(world, world.damageSources().indirectMagic(player, player), 5);
                    player.doHurtTarget(world, entity);
                }
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.HEART_OF_THE_SEA;
    }

    @Override
    public int getDefaultCooldown() {
        return 160; // the attacks of a guardian last 80 ticks
    }
}
