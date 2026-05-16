package dev.tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.GenericShapeAbility;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class GetItemAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    private final ItemLike item;
    private final int count;
    private final Consumer<ItemStack> stackConsumer;

    public GetItemAbility(ItemLike item, int count) {
        this(item, count, _ -> {});
    }

    public GetItemAbility(ItemLike item, int count, Consumer<ItemStack> stackConsumer) {
        this.item = item;
        this.count = count;
        this.stackConsumer = stackConsumer;
    }

    public static final Identifier ID = Walkers.id("get_item");
    public static final MapCodec<GetItemAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(o -> BuiltInRegistries.ITEM.getKey(o.item.asItem())),
            Codec.INT.optionalFieldOf("amount", 1).forGetter(o -> o.count)
    ).apply(instance, instance.stable((item, amount) -> new GetItemAbility<>(BuiltInRegistries.ITEM.get(item).orElseThrow().value(), amount))));

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        ItemStack stack = new ItemStack(item, count);
        stackConsumer.accept(stack);
        player.getInventory().add(stack);
    }

    @Override
    public Item getIcon() {
        return item.asItem();
    }

    @Override
    public int getDefaultCooldown() {
        return 600;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }
}
