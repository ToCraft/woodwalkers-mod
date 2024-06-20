package tocraft.walkers.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.impl.generic.*;
import tocraft.walkers.ability.impl.specific.*;
import tocraft.walkers.integrations.Integrations;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class AbilityRegistry {

    private static final Map<Predicate<LivingEntity>, ShapeAbility<?>> abilities = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Codec<? extends GenericShapeAbility<?>>> abilityCodecById = new HashMap<>();
    private static final Map<Codec<? extends GenericShapeAbility<?>>, ResourceLocation> abilityIdByCodec = new IdentityHashMap<>();

    public static void registerDefault() {
        // register codecs
        registerCodec(ShootFireballAbility.ID, ShootFireballAbility.CODEC);
        registerCodec(ClearEffectsAbility.ID, ClearEffectsAbility.CODEC);
        registerCodec(ExplosionAbility.ID, ExplosionAbility.CODEC);
        registerCodec(ShootDragonFireball.ID, ShootDragonFireball.CODEC);
        registerCodec(TeleportationAbility.ID, TeleportationAbility.CODEC);
        registerCodec(RandomTeleportationAbility.ID, RandomTeleportationAbility.CODEC);
        registerCodec(JumpAbility.ID, JumpAbility.CODEC);
        registerCodec(ThrowPotionsAbility.ID, ThrowPotionsAbility.CODEC);
        registerCodec(SaturateAbility.ID, SaturateAbility.CODEC);
        registerCodec(ShootSnowballAbility.ID, ShootSnowballAbility.CODEC);

        // Register generic Abilities first (since the last registered ability will be the used one
        registerByPredicate(livingEntity -> livingEntity instanceof NeutralMob, new AngerAbility<>());
        registerByPredicate(entity -> entity.getType().is(EntityTypeTags.RAIDERS), new RaidAbility<>());

        // Register 'normal' Abilities
        registerByClass(AbstractHorse.class, new JumpAbility<>());
        registerByClass(Blaze.class, new ShootFireballAbility<>(Items.BLAZE_POWDER, false));
        registerByClass(Creeper.class, new ExplosionAbility<>());
        registerByClass(EnderDragon.class, new ShootDragonFireball<>());
        registerByClass(EnderMan.class, new TeleportationAbility<>());
        registerByClass(Ghast.class, new ShootFireballAbility<>(Items.FIRE_CHARGE, true));
        registerByClass(SnowGolem.class, new ShootSnowballAbility<>());
        registerByClass(WitherBoss.class, new WitherAbility<>());
        registerByClass(Cow.class, new ClearEffectsAbility<>());
        registerByClass(Goat.class, new ClearEffectsAbility<>());
        registerByClass(Endermite.class, new RandomTeleportationAbility<>());
        registerByClass(Llama.class, new LlamaAbility<>());
        registerByClass(Witch.class, new ThrowPotionsAbility<>());
        registerByClass(Evoker.class, new EvokerAbility<>());
        registerByClass(Wolf.class, new AngerAbility<>(SoundEvents.WOLF_PANT, SoundEvents.WOLF_GROWL));
        registerByClass(Sheep.class, new SheepAbility<>());
        registerByClass(Chicken.class, new ChickenAbility<>());
        registerByClass(MushroomCow.class, new SaturateAbility<>());
        registerByClass(Bee.class, new AngerAbility<>(SoundEvents.BEE_LOOP, SoundEvents.BEE_LOOP_AGGRESSIVE));
        registerByClass(Shulker.class, new ShulkerAbility<>());
        registerByClass(Pufferfish.class, new PufferfishAbility<>());
        registerByClass(Turtle.class, new TurtleAbility<>());
        registerByClass(Rabbit.class, new RabbitAbility<>());

        // handle Integrations
        Integrations.registerAbilities();

        for (ShapeAbility<?> sAbility : abilities.values()) {
            if (sAbility instanceof GenericShapeAbility<?> ability) {
                if (!abilityCodecById.containsKey(ability.getId())) {
                    Walkers.LOGGER.warn("{} isn't registered!", ability.getId());
                }
                if (ability.getId() == null || ability.codec() == null) {
                    Walkers.LOGGER.warn("{} isn't correctly setup!", ability.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * @return the last registered {@link ShapeAbility} for the specified shape
     */
    @SuppressWarnings("unchecked")
    public static <L extends LivingEntity> ShapeAbility<L> get(L shape) {
        // check ability blacklist
        if (Walkers.CONFIG.abilityBlacklist.contains(Registry.ENTITY_TYPE.getKey(shape.getType()).toString()))
            return null;

        // cache the ability so the latest registered can be used
        ShapeAbility<L> ability = null;
        for (Map.Entry<Predicate<LivingEntity>, ShapeAbility<?>> entry : abilities.entrySet()) {
            if (entry.getKey().test(shape)) {
                ability = (ShapeAbility<L>) entry.getValue();
            }
        }

        return ability;
    }

    public static <A extends LivingEntity> void registerByType(EntityType<A> type, ShapeAbility<A> ability) {
        registerByPredicate(livingEntity -> type.equals(livingEntity.getType()), ability);
    }

    public static void registerByTag(TagKey<EntityType<?>> entityTag, GenericShapeAbility<LivingEntity> ability) {
        registerByPredicate(livingEntity -> livingEntity.getType().is(entityTag), ability);
    }

    public static <A extends LivingEntity> void registerByClass(Class<A> entityClass, ShapeAbility<A> ability) {
        registerByPredicate(entityClass::isInstance, ability);
    }

    /**
     * Register an ability for a predicate
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param ability         your {@link ShapeAbility}
     */
    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, ShapeAbility<?> ability) {
        abilities.put(entityPredicate, ability);
    }

    public static <L extends LivingEntity> boolean has(L shape) {
        // check ability blacklist

        if (Walkers.CONFIG.abilityBlacklist.contains(Registry.ENTITY_TYPE.getKey(shape.getType()).toString()))
            return false;
        return abilities.keySet().stream().anyMatch(predicate -> predicate.test(shape));
    }

    public static void clearAll() {
        abilities.clear();
    }

    public static void registerCodec(ResourceLocation abilityId, Codec<? extends GenericShapeAbility<?>> abilityCodec) {
        abilityCodecById.put(abilityId, abilityCodec);
        abilityIdByCodec.put(abilityCodec, abilityId);
    }

    @Nullable
    public static Codec<? extends GenericShapeAbility<?>> getAbilityCodec(ResourceLocation abilityId) {
        return abilityCodecById.get(abilityId);
    }

    @Nullable
    public static ResourceLocation getAbilityId(Codec<? extends GenericShapeAbility<?>> traitCodec) {
        return abilityIdByCodec.get(traitCodec);
    }

    public static Codec<GenericShapeAbility<?>> getAbilityCodec() {
        Codec<Codec<? extends GenericShapeAbility<?>>> codec = ResourceLocation.CODEC.flatXmap(
                resourceLocation -> Optional.ofNullable(AbilityRegistry.getAbilityCodec(resourceLocation))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error("Unknown shape ability: " + resourceLocation)),
                abilityCodec -> Optional.ofNullable(getAbilityId(abilityCodec))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error("Unknown shape ability codec: " + abilityCodec))
        );
        return codec.dispatchStable(GenericShapeAbility::codec, Function.identity());
    }
}
