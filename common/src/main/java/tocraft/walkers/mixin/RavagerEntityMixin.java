package tocraft.walkers.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(Ravager.class)
public abstract class RavagerEntityMixin extends LivingEntity {

    private RavagerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    // todo: move to inject
    @Override
    public void travel(Vec3 movementInput) {
        if (isAlive()) {

            // Ensure Ravager has a passenger
            if (isVehicle()) {
                LivingEntity rider = (LivingEntity) getFirstPassenger();

                // Only players should be able to control Ravager
                if (rider instanceof Player) {
                    // Assign rider properties to ravager
                    this.setYRot(rider.getYRot());
                    this.yRotO = this.getYRot();
                    this.setXRot(rider.getXRot() * 0.5F);
                    this.setRot(this.getYRot(), this.getXRot());
                    this.yBodyRot = this.getYRot();
                    this.yHeadRot = this.yBodyRot;
                    float sidewaysSpeed = rider.xxa * 0.5F;
                    float forwardSpeed = rider.zza;

                    // Going backwards, slow down!
                    if (forwardSpeed <= 0.0F) {
                        forwardSpeed *= 0.25F;
                    }

                    // Update movement/velocity
                    if (this.isControlledByLocalInstance()) {
                        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                        super.travel(new Vec3(sidewaysSpeed, movementInput.y, forwardSpeed));
                    } else if (rider instanceof Player) {
                        this.setDeltaMovement(Vec3.ZERO);
                    }

                    // Limb updates for movement
                    this.calculateEntityAnimation(this, false);
                    return;
                }
            }
            // Doesn't have a passenger, or passenger is not player,
            // but still alive, fall back to default travel logic
            super.travel(movementInput);
        }
    }
}
