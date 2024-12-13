package tocraft.walkers.traits;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

//#if MC<1214
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.client.resources.model.BakedModel;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.Items;
//#endif

@SuppressWarnings("unused")
public abstract class ShapeTrait<E extends LivingEntity> {
    public abstract ResourceLocation getId();

    public abstract MapCodec<? extends ShapeTrait<?>> codec();

    public boolean canBeRegisteredMultipleTimes() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean iconMightDiffer() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        //#if MC<1214
        //$$ Item item = getItemIcon();
        //$$ if (item != null) {
        //$$     BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(item), null, null, 15);
        //$$     if (itemModel != null) {
        //$$         return itemModel.getParticleIcon();
        //$$     }
        //$$ }
        //#endif
        return null;
    }

    @Contract(pure = true)
    @Environment(EnvType.CLIENT)
    public @Nullable Item getItemIcon() {
        return null;
    }
}
