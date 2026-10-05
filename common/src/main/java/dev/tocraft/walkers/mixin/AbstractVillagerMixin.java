package dev.tocraft.walkers.mixin;

import net.minecraft.world.entity.npc.villager.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {

    @Shadow
    protected abstract void stopTrading();
}
