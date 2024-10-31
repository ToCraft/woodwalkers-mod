package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

import java.util.Objects;

public class GetItemAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    private final ItemStack itemStack;

    public GetItemAbility(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static final ResourceLocation ID = Walkers.id("get_item");
    public static final MapCodec<GetItemAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(o -> BuiltInRegistries.ITEM.getKey(o.itemStack.getItem())),
            Codec.INT.optionalFieldOf("amount", 1).forGetter(o -> o.itemStack.getCount())
    ).apply(instance, instance.stable((item, amount) -> new GetItemAbility<>(new ItemStack(Objects.requireNonNull(BuiltInRegistries.ITEM.get(item)), amount)))));

    @Override
    public void onUse(@NotNull Player player, T shape, Level world) {
        player.getInventory().add(itemStack);
    }

    @Override
    public Item getIcon() {
        return itemStack.getItem();
    }

    @Override
    public int getDefaultCooldown() {
        return 600;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }
}
