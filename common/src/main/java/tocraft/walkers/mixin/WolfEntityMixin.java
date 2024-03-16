package tocraft.walkers.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.skills.SkillRegistry;
import tocraft.walkers.api.skills.impl.PreySkill;

import java.util.function.Predicate;

@SuppressWarnings({})
@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends TamableAnimal {

    private WolfEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private static final EntityDataAccessor<Boolean> walkers$isSpecial = SynchedEntityData.defineId(Wolf.class,
            EntityDataSerializers.BOOLEAN);

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void addPlayerTarget(CallbackInfo ci) {
        this.targetSelector.addGoal(7,
                new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, player -> {
                    // ensure wolves can attack players with a shape similar to their normal prey
                    if (!Walkers.CONFIG.wolvesAttack2ndShapedPrey) {
                        return false;
                    }

                    LivingEntity shape = PlayerShape.getCurrentShape((Player) player);

                    // wolves should ignore players that look like their prey if they have an owner,
                    // unless the config option is turned to true
                    LivingEntity owner = this.getOwner();
                    if (owner != null || Walkers.CONFIG.ownedWolvesAttack2ndShapedPrey) {
                        return false;
                    }

                    boolean bool = false;
                    if (shape != null) {
                        for (PreySkill<?> preySkill : SkillRegistry.get(shape, PreySkill.ID).stream().map(entry -> (PreySkill<?>) entry).toList()) {
                            for (Predicate<LivingEntity> hunterPredicate : preySkill.hunter) {
                                Walkers.LOGGER.warn("" + bool);
                                if (hunterPredicate.test((Wolf) (Object) this)) bool = true;
                                break;
                            }
                            if (bool) break;
                        }
                    }

                    return bool;
                }));
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (this.hasCustomName() && this.getCustomName().getString().equalsIgnoreCase("Patreon"))
            ((Wolf) (Object) this).getEntityData().set(walkers$isSpecial, true);
        else
            ((Wolf) (Object) this).getEntityData().set(walkers$isSpecial, false);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    protected void onInitDataTracker(CallbackInfo ci) {
        ((Wolf) (Object) this).getEntityData().define(walkers$isSpecial, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void onWriteCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("isSpecial", ((Wolf) (Object) this).getEntityData().get(walkers$isSpecial));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void onReadCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        ((Wolf) (Object) this).getEntityData().set(walkers$isSpecial, nbt.getBoolean("isSpecial"));
    }
}
