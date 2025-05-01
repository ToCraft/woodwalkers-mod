package tocraft.walkers.api.model.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.model.ClassArmProvider;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class GenericEntityArm<L extends LivingEntity, R extends LivingEntityRenderState, T extends EntityModel<R>> implements ClassArmProvider<T> {

    public GenericEntityArm() {
        this((ModelLayerLocation) null, "right_front_leg");
    }

    public GenericEntityArm(@NotNull ModelLayerLocation modelLayerLocation) {
        this(modelLayerLocation, "right_front_leg");
    }

    /**
     * @param modelParts the model parts in the order they appear - e.g. ["root", "body", "right_arm"]
     */
    public GenericEntityArm(@NotNull String... modelParts) {
        this(null, modelParts);
    }

    public GenericEntityArm(@Nullable ModelLayerLocation modelLayerLocation, @NotNull String... modelParts) {
        this.modelLayerLocation = modelLayerLocation;
        this.modelParts = modelParts;
    }

    @Nullable
    private final ModelLayerLocation modelLayerLocation;
    @NotNull
    private final String[] modelParts;

    private boolean failed = false;

    @Nullable
    @Override
    public ModelPart getArm(LivingEntity entity, T model) {
        ModelLayerLocation modelLayer = modelLayerLocation != null ? modelLayerLocation : new ModelLayerLocation(EntityType.getKey(entity.getType()), "main");
        try {
            ModelPart modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(modelLayer);
            for (String part : modelParts) {
                modelPart = modelPart.getChild(part);
            }
            return modelPart;
        } catch (Exception e) {
            // make error note only once
            if (!failed) {
                Walkers.LOGGER.error("{}: Caught an error rendering the arm for the {}.", GenericEntityArm.class.getSimpleName(), EntityType.getKey(entity.getType()), e);
                failed = true;
            }
            return null;
        }
    }
}
