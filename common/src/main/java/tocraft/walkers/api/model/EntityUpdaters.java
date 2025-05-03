package tocraft.walkers.api.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import tocraft.walkers.api.model.impl.AbstractHorseEntityUpdater;
import tocraft.walkers.api.model.impl.ShulkerEntityUpdater;
import tocraft.walkers.api.model.impl.SquidEntityUpdater;
import tocraft.walkers.impl.NearbySongAccessor;
import tocraft.walkers.mixin.accessor.AllayAccessor;
import tocraft.walkers.mixin.accessor.BatAccessor;
import tocraft.walkers.mixin.accessor.CreeperEntityAccessor;
import tocraft.walkers.mixin.accessor.ParrotEntityAccessor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for {@link EntityUpdater} instances.
 *
 * <p>
 * {@link EntityUpdater}s are used to apply changes to shape entity instances on
 * the client using information from the player. As an example, an
 * {@link EntityUpdater} can be used to tell a shape bat to "stop roosting,"
 * which triggers the flight animation. {@link EntityUpdater}s are called once
 * every render tick
 * {@link net.minecraft.client.renderer.entity.EntityRenderer#render(EntityRenderState, PoseStack, MultiBufferSource, int)}
 */
@Environment(EnvType.CLIENT)
public class EntityUpdaters {

    private static final Map<EntityType<? extends LivingEntity>, EntityUpdater<? extends LivingEntity>> map = new LinkedHashMap<>();

    /**
     * Returns a {@link EntityUpdater} if one has been registered for the given
     * {@link EntityType}, or null.
     *
     * @param entityType entity type key to retrieve a value registered in
     *                   {@link EntityUpdaters#register(EntityType, EntityUpdater)}
     * @param <T>        passed in {@link EntityType} generic
     * @return registered {@link EntityUpdater} instance for the given
     * {@link EntityType}, or null if one does not exist
     */
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> EntityUpdater<T> getUpdater(EntityType<T> entityType) {
        return (EntityUpdater<T>) map.getOrDefault(entityType, null);
    }

    /**
     * Registers an {@link EntityUpdater} for the given {@link EntityType}.
     *
     * <p>
     * Note that a given {@link EntityType} can only have 1 {@link EntityUpdater}
     * associated with it.
     *
     * @param type          entity type key associated with the given
     *                      {@link EntityUpdater}
     * @param entityUpdater {@link EntityUpdater} associated with the given
     *                      {@link EntityType}
     * @param <T>           passed in {@link EntityType} generic
     */
    public static <T extends LivingEntity> void register(EntityType<T> type, EntityUpdater<T> entityUpdater) {
        map.put(type, entityUpdater);
    }

    public static void init() {
        EntityUpdaters.register(EntityType.HORSE, new AbstractHorseEntityUpdater<>());
        EntityUpdaters.register(EntityType.DONKEY, new AbstractHorseEntityUpdater<>());
        EntityUpdaters.register(EntityType.MULE, new AbstractHorseEntityUpdater<>());

        EntityUpdaters.register(EntityType.ALLAY, (player, allay) -> {
            ((AllayAccessor) allay).setHoldingItemAnimationTicks0(((AllayAccessor) allay).getHoldingItemAnimationTicks());
            if (allay.hasItemInHand()) {
                ((AllayAccessor) allay).setHoldingItemAnimationTicks(Mth.clamp(((AllayAccessor) allay).getHoldingItemAnimationTicks() + 1.0F, 0.0F, 5.0F));
            } else {
                ((AllayAccessor) allay).setHoldingItemAnimationTicks(Mth.clamp(((AllayAccessor) allay).getHoldingItemAnimationTicks() - 1.0F, 0.0F, 5.0F));
            }
        });

        // register specific entity animation handling
        EntityUpdaters.register(EntityType.BAT, (player, bat) -> {
            bat.setResting(!player.level().getBlockState(player.blockPosition().above()).isAir());
            ((BatAccessor) bat).callSetupAnimationStates();
        });

        EntityUpdaters.register(EntityType.PARROT, (player, parrot) -> {
            parrot.setRecordPlayingNearby(player.blockPosition(), ((NearbySongAccessor) player).shape_isNearbySongPlaying());
            ((ParrotEntityAccessor) parrot).callCalculateFlapping();
            // imitate sounds
            if (player.getRandom().nextInt(400) == 0) {
                Parrot.imitateNearbyMobs(player.level(), player);
            }
        });

        EntityUpdaters.register(EntityType.ENDERMAN, (player, enderman) -> {
            ItemStack heldStack = player.getMainHandItem();

            if (heldStack.getItem() instanceof BlockItem) {
                enderman.setCarriedBlock(((BlockItem) heldStack.getItem()).getBlock().defaultBlockState());
            }
        });

        // To prevent Creeper shapes from flickering white, we reset currentFuseTime to
        // 0.
        // Creepers normally tick their fuse timer in tick(), but:
        // 1. shapes do not tick
        // 2. The Creeper ability is instant, so we do not need to re-implement ticking
        EntityUpdaters.register(EntityType.CREEPER, (player, creeper) -> ((CreeperEntityAccessor) creeper).setSwell(0));

        EntityUpdaters.register(EntityType.SQUID, new SquidEntityUpdater<>());
        EntityUpdaters.register(EntityType.GLOW_SQUID, new SquidEntityUpdater<>());

        EntityUpdaters.register(EntityType.SHULKER, new ShulkerEntityUpdater());

        EntityUpdaters.register(EntityType.CHICKEN, (player, chicken) -> {
            chicken.oFlap = chicken.flap;
            chicken.oFlapSpeed = chicken.flapSpeed;
            chicken.flapSpeed += (player.onGround() ? -1.0F : 4.0F) * 0.3F;
            chicken.flapSpeed = Mth.clamp(chicken.flapSpeed, 0.0F, 1.0F);
            if (!player.onGround() && chicken.flapping < 1.0F) {
                chicken.flapping = 1.0F;
            }
            chicken.flapping *= 0.9F;
            chicken.flap += chicken.flapping * 2.0F;
        });

        // make strider shaking and purple when out of lava
        EntityUpdaters.register(EntityType.STRIDER, (player, strider) -> {
            BlockState blockState = player.level().getBlockState(player.blockPosition());
            boolean bl = blockState.is(BlockTags.STRIDER_WARM_BLOCKS) || player.getFluidHeight(FluidTags.LAVA) > 0.0;
            strider.setSuffocating(!bl);
        });

        EntityUpdaters.register(EntityType.CAT, (player, cat) -> cat.setInSittingPose(false));

        EntityUpdaters.register(EntityType.HOGLIN, (player, hoglin) -> {
            if (player.level().dimensionType().piglinSafe()) {
                hoglin.setImmuneToZombification(true);
            } else {
                hoglin.setImmuneToZombification(false);
            }
        });

        EntityUpdaters.register(EntityType.PIGLIN, (player, piglin) -> {
            if (player.level().dimensionType().piglinSafe()) {
                piglin.setImmuneToZombification(true);
            } else {
                piglin.setImmuneToZombification(false);
            }
        });

        EntityUpdaters.register(EntityType.PIGLIN_BRUTE, (player, piglinBrute) -> {
            if (player.level().dimensionType().piglinSafe()) {
                piglinBrute.setImmuneToZombification(true);
            } else {
                piglinBrute.setImmuneToZombification(false);
            }
        });
    }
}
