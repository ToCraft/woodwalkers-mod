package tocraft.walkers.ability.impl.specific;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrassEaterAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("eat_grass");
    public static final MapCodec<GrassEaterAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new GrassEaterAbility<>()));

    public final Map<UUID, Integer> eatTick = new HashMap<>();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        eatGrass(player);
    }

    public void eatGrass(ServerPlayer player) {
        eatTick.put(player.getUUID(), Mth.positiveCeilDiv(40, 2));
    }

    @Override
    public Item getIcon() {
        return Items.SHORT_GRASS;
    }
}
