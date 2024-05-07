package tocraft.walkers.skills;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class ShapeSkill<E extends LivingEntity> {
    public abstract ResourceLocation getId();

    public abstract MapCodec<? extends ShapeSkill<?>> codec();

    public boolean canBeRegisteredMultipleTimes() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean iconMightDiffer() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        return null;
    }
}
