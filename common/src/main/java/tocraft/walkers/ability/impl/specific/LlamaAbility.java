package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

public class LlamaAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("llama");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        LlamaSpit spit = new LlamaSpit(EntityType.LLAMA_SPIT, world);
        spit.setOwner(player);
        Vec3 rotation = player.getLookAngle();
        spit.shoot(rotation.x, rotation.y, rotation.z, 1.5F, 10.0F);
        spit.syncPacketPositionCodec(player.getX(), player.getEyeY(), player.getZ());
        spit.setPos(player.getX(), player.getEyeY(), player.getZ());

        // Play SFX
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LLAMA_SPIT, player.getSoundSource(), 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F);

        world.addFreshEntity(spit);
    }

    @Override
    public Item getIcon() {
        return Items.LEAD;
    }
}
