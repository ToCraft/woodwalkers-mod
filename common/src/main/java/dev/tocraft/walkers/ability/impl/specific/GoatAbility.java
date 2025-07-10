package dev.tocraft.walkers.ability.impl.specific;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.ShapeAbility;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GoatAbility<T extends Goat> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("goat");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(@NotNull ServerPlayer player, @NotNull T shape, @NotNull ServerLevel world) {
        if (player.onGround()) { // prevent flying
            // calculate force
            int i = Optional.ofNullable(shape.getEffect(MobEffects.SPEED)).map(e -> e.getAmplifier() + 1).orElse(0);
            int j = Optional.ofNullable(shape.getEffect(MobEffects.SLOWNESS)).map(e -> e.getAmplifier() + 1).orElse(0);
            float g = 0.25F * (i - j);
            float h = Mth.clamp(shape.getSpeed() * 1.65F, 0.2F, 3.0F) + g;
            float force = h * getForce(shape);

            Vec3 forward = player.getForward().normalize();
            Vec3 push = forward.multiply(force, force / 2, force); // jump less high than far
            player.push(push);

            world.playSound(null, player, getImpactSound(shape), SoundSource.NEUTRAL, 1.0F, 1.0F);
            player.connection.send(new ClientboundSetEntityMotionPacket(player)); // notify clients
        }
    }

    private static float getForce(@NotNull Goat shape) {
        return shape.isBaby() ? 5f : 12.5f;
    }

    private static SoundEvent getImpactSound(@NotNull Goat shape) {
        return shape.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_RAM_IMPACT : SoundEvents.GOAT_RAM_IMPACT;
    }

    @Override
    public Item getIcon() {
        return Items.GOAT_HORN;
    }

    @Override
    public int getDefaultCooldown() {
        return 165; // goats ram every 30-300 seconds, 165 is the arithmetic mean
    }
}
