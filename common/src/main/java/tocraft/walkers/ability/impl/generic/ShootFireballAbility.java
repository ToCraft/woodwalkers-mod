package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.craftedcore.patched.CRegistries;
import tocraft.craftedcore.patched.Identifier;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

import java.util.Optional;

//#if MC>=1210
import net.minecraft.world.phys.Vec3;
//#endif

public class ShootFireballAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("shoot_fireball");
    @SuppressWarnings("unchecked")
    public static final MapCodec<ShootFireballAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(o -> {
                if (o.icon == Items.FIRE_CHARGE) return Optional.empty();
                else
                    return Optional.ofNullable(((Registry<Item>) CRegistries.getRegistry(Identifier.parse("item"))).getKey(o.icon));
            }),
            Codec.BOOL.fieldOf("is_large").forGetter(o -> o.isLarge)
    ).apply(instance, instance.stable((icon, isLarge) -> icon.<ShootFireballAbility<?>>map(resourceLocation -> new ShootFireballAbility<>(((Registry<Item>) CRegistries.getRegistry(Identifier.parse("item"))).get(resourceLocation), isLarge)).orElseGet(() -> new ShootFireballAbility<>(isLarge)))));

    private final boolean isLarge;
    private final Item icon;

    public ShootFireballAbility(boolean isLarge) {
        this(Items.FIRE_CHARGE, isLarge);
    }

    public ShootFireballAbility(Item icon, boolean isLarge) {
        this.icon = icon;
        this.isLarge = isLarge;
    }


    @Override
    public void onUse(Player player, T shape, Level world) {
        Fireball fireball = getFireball(player, world);
        world.addFreshEntity(fireball);
        if (shape instanceof Blaze) {
            world.playSound(null, player, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        } else if (shape instanceof Ghast) {
            world.playSound(null, player, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
            world.playSound(null, player, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        } else {
            world.playSound(null, player, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 2.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    private @NotNull Fireball getFireball(Player player, Level world) {
        Fireball fireball;
        if (isLarge) {
            //#if MC>1206
            fireball = new LargeFireball(
                    world,
                    player,
                    new Vec3(player.getLookAngle().x,
                            player.getLookAngle().y,
                            player.getLookAngle().z),
                    2);
            //#else
            //$$ fireball = new LargeFireball(
            //$$         world,
            //$$         player,
            //$$         player.getLookAngle().x,
            //$$         player.getLookAngle().y,
            //$$         player.getLookAngle().z,
            //$$         2);
            //#endif
            fireball.moveTo(fireball.getX(), fireball.getY() + 1.75, fireball.getZ(), fireball.getYRot(), fireball.getXRot());
            fireball.absMoveTo(fireball.getX(), fireball.getY(), fireball.getZ());
        } else {
            //#if MC>1206
            fireball = new SmallFireball(
                    world,
                    player.getX(),
                    player.getEyeY(),
                    player.getZ(),
                    new Vec3(player.getLookAngle().x,
                            player.getLookAngle().y,
                            player.getLookAngle().z));
            //#else
            //$$ fireball = new SmallFireball(
            //$$         world,
            //$$         player.getX(),
            //$$         player.getEyeY(),
            //$$         player.getZ(),
            //$$         player.getLookAngle().x,
            //$$         player.getLookAngle().y,
            //$$         player.getLookAngle().z);
            //#endif
        }

        fireball.setOwner(player);
        return fireball;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }

    @Override
    public Item getIcon() {
        return icon;
    }
}
