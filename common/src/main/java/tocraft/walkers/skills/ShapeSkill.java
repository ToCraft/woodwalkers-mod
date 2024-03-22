package tocraft.walkers.skills;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public abstract class ShapeSkill<E extends LivingEntity> {
    public abstract ResourceLocation getId();

    public abstract Codec<? extends ShapeSkill<?>> codec();

    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        return null;
    }
}
