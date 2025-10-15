package dev.tocraft.walkers.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.impl.generic.*;
import dev.tocraft.walkers.ability.impl.specific.*;
import dev.tocraft.walkers.integrations.AbstractIntegration;
import dev.tocraft.walkers.integrations.Integrations;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class AbilityRegistry {

    private static final Map<Predicate<LivingEntity>, ShapeAbility<?>> specificAbilities = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Predicate<LivingEntity>, GenericShapeAbility<?>> genericAbilities = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<ResourceLocation, MapCodec<? extends GenericShapeAbility<?>>> abilityCodecById = new HashMap<>();
    private static final Map<MapCodec<? extends GenericShapeAbility<?>>, ResourceLocation> abilityIdByCodec = new IdentityHashMap<>();

    @ApiStatus.Internal
    public static void initialize() {
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
        registerCodec(GetItemAbility.ID, GetItemAbility.CODEC);
    }

    @ApiStatus.Internal
    public static void registerDefault() {
        // Register generic Abilities first (since the last registered ability will be the used one
        registerByPredicate(livingEntity -> livingEntity instanceof NeutralMob, new AngerAbility<>());
        registerByTag(EntityTypeTags.RAIDERS, new RaidAbility<>());

        // Register 'normal' Abilities
        registerByPredicate(entity -> entity instanceof AbstractHorse && !(entity instanceof Llama), new JumpAbility<>());
        registerByClass(Blaze.class, new ShootFireballAbility<>(Items.BLAZE_POWDER, false));
        // higher explosion radius when charged
        registerByPredicate(entity -> entity instanceof Creeper && !((Creeper) entity).isPowered(), new ExplosionAbility<>());
        registerByPredicate(entity -> entity instanceof Creeper && ((Creeper) entity).isPowered(), new ExplosionAbility<>(6));
        registerByClass(EnderDragon.class, new ShootDragonFireball<>());
        registerByClass(EnderMan.class, new TeleportationAbility<>());
        registerByClass(Ghast.class, new ShootFireballAbility<>(Items.FIRE_CHARGE, true));
        registerByClass(SnowGolem.class, new ShootSnowballAbility<>());
        registerByClass(WitherBoss.class, new WitherAbility<>());
        registerByClass(Cow.class, new ClearEffectsAbility<>());
        registerByClass(Goat.class, new GoatAbility<>());
        registerByClass(Guardian.class, new GuardianAbility<>());
        registerByClass(Endermite.class, new RandomTeleportationAbility<>());
        registerByClass(Llama.class, new LlamaAbility<>());
        registerByClass(Witch.class, new ThrowPotionsAbility<>());
        registerByClass(Evoker.class, new EvokerAbility<>());
        registerByClass(Warden.class, new WardenAbility<>());
        registerByClass(Wolf.class, new AngerAbility<>(SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.CUTE).whineSound().value(), SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.ANGRY).growlSound().value()));
        registerByClass(Sheep.class, new SheepAbility<>());
        registerByClass(Sniffer.class, new SnifferAbility<>());
        registerByClass(Chicken.class, new ChickenAbility<>());
        registerByClass(MushroomCow.class, new SaturateAbility<>());
        registerByClass(Bee.class, new AngerAbility<>(SoundEvents.BEE_LOOP, SoundEvents.BEE_LOOP_AGGRESSIVE));
        registerByClass(Shulker.class, new ShulkerAbility<>());
        registerByClass(Pufferfish.class, new PufferfishAbility<>());
        registerByClass(Turtle.class, new TurtleAbility<>());
        registerByClass(Rabbit.class, new RabbitAbility<>());
        registerByClass(Breeze.class, new BreezeAbility<>());
        // get item ability
        registerByClass(Skeleton.class, new GetItemAbility<>(new ItemStack(Items.ARROW, 4)));
        ItemStack slownessArrows = new ItemStack(Items.TIPPED_ARROW, 4);
        ItemStack poisonedArrows = new ItemStack(Items.TIPPED_ARROW, 4);
        slownessArrows.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SLOWNESS));
        poisonedArrows.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
        registerByClass(Bogged.class, new GetItemAbility<>(poisonedArrows));
        registerByClass(Stray.class, new GetItemAbility<>(slownessArrows));

        // handle Integrations
        Integrations.registerAbilities();

        // registration warnings
        for (GenericShapeAbility<?> ability : genericAbilities.values()) {
            if (!abilityCodecById.containsKey(ability.getId())) {
                Walkers.LOGGER.warn("{} isn't registered!", ability.getId());
            }
            if (ability.getId() == null || ability.codec() == null) {
                Walkers.LOGGER.warn("{} isn't correctly setup!", ability.getClass().getSimpleName());
            }
        }
    }

    /**
     * @return the last registered {@link ShapeAbility} for the specified shape
     */
    @SuppressWarnings("unchecked")
    public static <L extends LivingEntity> @Nullable ShapeAbility<L> get(@NotNull L shape) {
        // check ability blacklist
        if (Walkers.CONFIG.abilityBlacklist.contains(EntityType.getKey(shape.getType()).toString()))
            return null;

        // cache the ability so the latest registered can be used
        ShapeAbility<L> ability = null;
        Set<Map.Entry<Predicate<LivingEntity>, ShapeAbility<?>>> specificAbilities = new LinkedHashSet<>(AbilityRegistry.specificAbilities.entrySet());
        for (Map.Entry<Predicate<LivingEntity>, ShapeAbility<?>> entry : specificAbilities) {
            if (entry.getKey().test(shape)) {
                ability = (ShapeAbility<L>) entry.getValue();
                // don't break so it'll access the last registered ability
            }
        }
        Set<Map.Entry<Predicate<LivingEntity>, GenericShapeAbility<?>>> genericAbilities = new LinkedHashSet<>(AbilityRegistry.genericAbilities.entrySet());
        for (Map.Entry<Predicate<LivingEntity>, GenericShapeAbility<?>> entry : genericAbilities) {
            if (entry.getKey().test(shape)) {
                ability = (ShapeAbility<L>) entry.getValue();
                // don't break so it'll access the last registered ability
            }
        }

        return ability;
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerAbilities() Integration.registerAbilities()}
     */
    public static <A extends LivingEntity> void registerByType(EntityType<A> type, ShapeAbility<A> ability) {
        registerByPredicate(livingEntity -> type.equals(livingEntity.getType()), ability);
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerAbilities() Integration.registerAbilities()}
     */
    public static void registerByTag(TagKey<EntityType<?>> entityTag, ShapeAbility<LivingEntity> ability) {
        registerByPredicate(livingEntity -> livingEntity.getType().is(entityTag), ability);
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerAbilities() Integration.registerAbilities()}
     */
    public static <A extends LivingEntity> void registerByClass(@NotNull Class<A> entityClass, ShapeAbility<A> ability) {
        registerByPredicate(entityClass::isInstance, ability);
    }

    /**
     * Register an ability for a predicate
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerAbilities() Integration.registerAbilities()}
     *
     * @param entityPredicate this should only be true, if the entity is the correct class for the ability!
     * @param ability         your {@link ShapeAbility}
     */
    public static void registerByPredicate(Predicate<LivingEntity> entityPredicate, ShapeAbility<?> ability) {
        if (ability instanceof GenericShapeAbility<?> genericShapeAbility) {
            genericAbilities.put(entityPredicate, genericShapeAbility);
        } else {
            specificAbilities.put(entityPredicate, ability);
        }
    }

    public static <L extends LivingEntity> boolean has(@NotNull L shape) {
        // check ability blacklist
        if (Walkers.CONFIG.abilityBlacklist.contains(EntityType.getKey(shape.getType()).toString())) {
            return false;
        }
        return Stream.concat(specificAbilities.keySet().stream(), genericAbilities.keySet().stream()).parallel().anyMatch(predicate -> predicate.test(shape));
    }

    @ApiStatus.Internal
    public static void clearAll() {
        specificAbilities.clear();
        genericAbilities.clear();
    }

    public static void registerCodec(ResourceLocation abilityId, MapCodec<? extends GenericShapeAbility<?>> abilityCodec) {
        abilityCodecById.put(abilityId, abilityCodec);
        abilityIdByCodec.put(abilityCodec, abilityId);
    }

    @ApiStatus.Internal
    @Nullable
    public static MapCodec<? extends GenericShapeAbility<?>> getAbilityCodec(ResourceLocation abilityId) {
        return abilityCodecById.get(abilityId);
    }

    @ApiStatus.Internal
    @Nullable
    public static ResourceLocation getAbilityId(MapCodec<? extends GenericShapeAbility<?>> traitCodec) {
        return abilityIdByCodec.get(traitCodec);
    }

    @ApiStatus.Internal
    public static Codec<GenericShapeAbility<?>> getAbilityCodec() {
        Codec<MapCodec<? extends GenericShapeAbility<?>>> codec = ResourceLocation.CODEC.flatXmap(
                resourceLocation -> Optional.ofNullable(AbilityRegistry.getAbilityCodec(resourceLocation))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape ability: " + resourceLocation)),
                abilityCodec -> Optional.ofNullable(getAbilityId(abilityCodec))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape ability codec: " + abilityCodec))
        );
        return codec.dispatchStable(GenericShapeAbility::codec, Function.identity());
    }
}
