package dev.tocraft.walkers.api.platform;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/* TODO: Make unlocking work like this:
1. the player presses UNLOCK_KEY
2. a packet is send to the server
3. the server player checks with getTargetedLivingEntity if they ray cast a possible entity
4. a timer within the player entity ticking starts
5. every time the timer changes, the ray cast is checked again (with a different filter, just if the entity is the one or not, without blacklist and so on)
6. the timer aborts if the entity changes/is not ray cast anymore. IF it runs out, the entity is unlocked -> sync
*/
@SuppressWarnings("unused")
public class UnlockHandler {
    private static @Nullable LivingEntity getTargetedLivingEntity(@NotNull ServerPlayer player, double maxDistance) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);

        // check for blocks in the ray cast's way
        HitResult blockHit = player.level().clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        endPos = blockHit.getLocation();

        // perform entity ray cast
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(maxDistance)).inflate(1.0);
        Predicate<Entity> entityFilter = entity -> (entity instanceof LivingEntity) && !entity.isSpectator() && entity.isAlive();

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, eyePos, endPos, searchBox, entityFilter, maxDistance);

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity livingEntity) {
            return livingEntity;
        } else {
            return null;
        }
    }
}