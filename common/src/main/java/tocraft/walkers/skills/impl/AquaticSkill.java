package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class AquaticSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("aquatic");
    public static final Codec<AquaticSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.optionalFieldOf("is_aquatic", 0).forGetter(o -> o.isAquatic)
    ).apply(instance, instance.stable(AquaticSkill::new)));

    public final int isAquatic;

    /**
     * @param isAquatic 0 - water mob, 1 - land and water mob, 2 - land mob
     */
    public AquaticSkill(int isAquatic) {
        this.isAquatic = isAquatic;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        if (isAquatic == 0) {
            BakedModel itemIcon = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(Items.HEART_OF_THE_SEA);
            if (itemIcon != null) {
                return itemIcon.getParticleIcon();
            }
        } else if (isAquatic == 1) {
            return Minecraft.getInstance().getMobEffectTextures().get(MobEffects.WATER_BREATHING);
        }
        return super.getIcon();
    }
}
