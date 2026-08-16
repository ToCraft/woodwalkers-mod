package dev.tocraft.walkers.api.model;

import com.mojang.datafixers.util.Pair;
import dev.tocraft.craftedcore.util.Maths;
import dev.tocraft.walkers.api.model.impl.GenericEntityArm;
import dev.tocraft.walkers.mixin.client.accessor.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.animal.allay.AllayModel;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.animal.feline.AdultOcelotModel;
import net.minecraft.client.model.animal.fox.FoxModel;
import net.minecraft.client.model.animal.golem.IronGolemModel;
import net.minecraft.client.model.animal.llama.LlamaModel;
import net.minecraft.client.model.animal.panda.PandaModel;
import net.minecraft.client.model.animal.pig.PigModel;
import net.minecraft.client.model.animal.polarbear.PolarBearModel;
import net.minecraft.client.model.animal.squid.SquidModel;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.model.monster.blaze.BlazeModel;
import net.minecraft.client.model.monster.ravager.RavagerModel;
import net.minecraft.client.model.monster.spider.SpiderModel;
import net.minecraft.client.model.monster.strider.StriderModel;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.model.monster.hoglin.HoglinModel;
import net.minecraft.client.model.monster.vex.VexModel;
import net.minecraft.client.model.monster.warden.WardenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class EntityArms {

    private static final Map<EntityType<? extends LivingEntity>, Pair<EntityArmProvider<? extends LivingEntity, ? extends LivingEntityRenderState>, ArmRenderingManipulator<?>>> DIRECT_PROVIDERS = new LinkedHashMap<>();
    private static final Map<Class<?>, Pair<ClassArmProvider<?>, ArmRenderingManipulator<?>>> CLASS_PROVIDERS = new LinkedHashMap<>();

    /**
     * non-specific, for easy use
     */
    public static <T extends LivingEntity, R extends LivingEntityRenderState> void register(EntityType<T> type, EntityArmProvider<T, R> provider) {
        register(type, provider, (stack, model) -> {
        });
    }

    /**
     * type-based, with optional manipulator
     */
    public static <T extends LivingEntity, R extends LivingEntityRenderState> void register(EntityType<T> type, EntityArmProvider<T, R> provider,
                                                                                            ArmRenderingManipulator<EntityModel<R>> manipulator) {
        DIRECT_PROVIDERS.put(type, new Pair<>(provider, manipulator));
    }

    /**
     * Specific, but for easy use
     */
    public static <T extends EntityModel<?>> void register(Class<T> modelClass, ClassArmProvider<T> provider) {
        register(modelClass, provider, (stack, model) -> {
        });
    }

    /**
     * Specific with optional manipulator
     */
    public static <T extends EntityModel<?>> void register(Class<T> modelClass, ClassArmProvider<T> provider,
                                                           ArmRenderingManipulator<T> manipulator) {
        CLASS_PROVIDERS.put(modelClass, new Pair<>(provider, manipulator));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity, R extends LivingEntityRenderState> Pair<ModelPart, ArmRenderingManipulator<?>> get(@NotNull T entity,
                                                                                                                               EntityModel<R> model) {
        // done to bypass type issues
        Pair<EntityArmProvider<? extends LivingEntity, ? extends LivingEntityRenderState>, ArmRenderingManipulator<?>> before = DIRECT_PROVIDERS
                .get(entity.getType());

        // Direct entity type provider was found, return it now
        if (before != null) {
            Pair<EntityArmProvider<T, R>, ArmRenderingManipulator<?>> provider = new Pair<>(
                    (EntityArmProvider<T, R>) before.getFirst(), before.getSecond());
            return new Pair<>(provider.getFirst().getArm(entity, model), provider.getSecond());
        } else {
            Optional<Pair<ClassArmProvider<?>, ArmRenderingManipulator<?>>> beforeClassProvider = CLASS_PROVIDERS
                    .entrySet().stream().filter(pair ->
                            pair.getKey().isInstance(model))
                    .findFirst().map(entry ->
                            new Pair<>(entry.getValue().getFirst(), entry.getValue().getSecond())
                    );

            // fall back to class providers
            if (beforeClassProvider.isPresent()) {
                Pair<ClassArmProvider<EntityModel<?>>, ArmRenderingManipulator<EntityModel<LivingEntityRenderState>>> classProvider = new Pair<>(
                        (ClassArmProvider<EntityModel<?>>) beforeClassProvider.get().getFirst(),
                        (ArmRenderingManipulator<EntityModel<LivingEntityRenderState>>) beforeClassProvider.get().getSecond());
                return new Pair<>(classProvider.getFirst().getArm(entity, model), classProvider.getSecond());
            } else {
                return null;
            }
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> EntityArmProvider<T, ? extends LivingEntityRenderState> get(EntityType<LivingEntity> type) {
        return (EntityArmProvider<T, ? extends LivingEntityRenderState>) DIRECT_PROVIDERS.get(type);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity, R extends LivingEntityRenderState> EntityArmProvider<T, R> get(
            Class<EntityModel<? extends LivingEntityRenderState>> modelClass) {
        return (EntityArmProvider<T, R>) CLASS_PROVIDERS.get(modelClass);
    }

    public static void init() {
        // specific
        register(LlamaModel.class, (llama, model) -> ((LlamaEntityModelAccessor) model).getRightFrontLeg());
        register(PandaModel.class, (panda, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(),
                (stack, model) -> stack.translate(0, -0.5, 0));
        register(BlazeModel.class, (blaze, model) -> ((BlazeEntityModelAccessor) model).getUpperBodyParts()[10],
                (stack, model) -> {
                    stack.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_Z(), 45));
                    stack.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_Y(), -15));
                    stack.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_X(), -25));
                    stack.translate(0, 0, -.25);
                });
        register(AdultOcelotModel.class, (ocelot, model) -> ((OcelotEntityModelAccessor) model).getRightFrontLeg());
        register(SpiderModel.class, (spider, model) -> ((SpiderEntityModelAccessor) model).getRightFrontLeg(),
                (stack, model) -> {
                    stack.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_Y(), -15));
                    stack.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_X(), 15));
                    stack.translate(0, 0, 0);
                });
        register(IronGolemModel.class,
                (golem, model) -> model.getFlowerHoldingArm(),
                (stack, model) -> stack.translate(0, 0, -.5));
        register(PigModel.class,
                (pig, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(),
                (stack, model) -> stack.translate(0, 0, .6));
        register(PolarBearModel.class,
                (bear, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(),
                (stack, model) -> stack.translate(0, -.3, 0));
        register(RavagerModel.class,
                (bear, model) -> ((RavagerEntityModelAccessor) model).getRightFrontLeg());
        register(SquidModel.class,
                (squid, model) -> ((SquidEntityModelAccessor) model).getTentacles()[0]);

        // something between specific & generic
        register(HorseModel.class, new GenericEntityArm<>(),
                (stack, model) -> {
                    stack.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_Y(), -15));
                    stack.translate(0, -.25, .25);
                });
        register(CamelModel.class, new GenericEntityArm<>(),
                (stack, model) -> stack.translate(0, -.25, 0));
        register(FoxModel.class, new GenericEntityArm<>(),
                (stack, model) -> stack.translate(0, -0.1, 0));
        register(WolfModel.class, new GenericEntityArm<>(),
                (stack, model) -> stack.translate(0, -0.1, 0));
        register(StriderModel.class, new GenericEntityArm<>("right_leg"));
        register(WardenModel.class, new GenericEntityArm<>("bone", "body", "right_arm"),
                ((stack, model) -> {
                    stack.scale(.5f, .5f, .5f);
                    stack.translate(0, .75, -1);
                }));
        register(AllayModel.class, new GenericEntityArm<>("root", "body", "right_arm"),
                (stack, model) -> {
                    stack.scale(5, 5, 5);
                    stack.translate(.2, .5, -.35);
                });
        register(VexModel.class, new GenericEntityArm<>("root", "body", "right_arm"),
                (stack, model) -> {
                    stack.scale(5, 5, 5);
                    stack.translate(.2, .5, -.35);
                });
        register(CreeperModel.class, new GenericEntityArm<>(),
                (stack, model) -> stack.translate(0, -.5, 0));
        register(HoglinModel.class, new GenericEntityArm<>(),
                (stack, model) -> stack.scale(.75f, .75f, .75f));

        // generic
        register(QuadrupedModel.class,
                (quad, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg());

        // types
        register(EntityTypes.PILLAGER,
                (pillager, model) -> ((IllagerEntityModelAccessor) model).getRightArm(),
                (stack, model) -> {
                    stack.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_X(), -10));
                    stack.translate(0, .5, -.3);
                });
    }
}
