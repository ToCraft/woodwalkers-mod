package dev.tocraft.walkers.ability.impl.specific;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.ShapeAbility;
import dev.tocraft.walkers.impl.SonicBoomUser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class WardenAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("warden");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        ((SonicBoomUser) player).shape$ability_startSonicBoom();
    }

    @Override
    public Item getIcon() {
        return Items.ECHO_SHARD;
    }

    @Override
    public int getDefaultCooldown() {
        return 200;
    }
}
