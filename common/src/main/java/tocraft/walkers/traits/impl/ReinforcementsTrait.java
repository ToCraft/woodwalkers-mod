package tocraft.walkers.traits.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ReinforcementsTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("reinforcements");
    public static final MapCodec<ReinforcementsTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.INT.optionalFieldOf("range", 32).forGetter(o -> o.range),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("reinforcements", new ArrayList<>()).forGetter(o -> o.reinforcementTypes.stream().map(BuiltInRegistries.ENTITY_TYPE::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("reinforcement_tags", new ArrayList<>()).forGetter(o -> o.reinforcementTags.stream().map(TagKey::location).toList())
    ).apply(instance, instance.stable((range, reinforcementsLocations, reinforcementTagsLocations) -> {
        List<EntityType<?>> reinforcements = new ArrayList<>();
        List<TagKey<EntityType<?>>> reinforcementTags = new ArrayList<>();
        for (ResourceLocation resourceLocation : reinforcementsLocations) {
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation)) {
                reinforcements.add(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation).orElseThrow().value());
            }
        }
        for (ResourceLocation resourceLocation : reinforcementTagsLocations) {
            reinforcementTags.add(TagKey.create(Registries.ENTITY_TYPE, resourceLocation));
        }
        return new ReinforcementsTrait<>(range, reinforcements, reinforcementTags);
    })));
    private final int range;
    private final List<EntityType<?>> reinforcementTypes;
    private final List<TagKey<EntityType<?>>> reinforcementTags;


    public ReinforcementsTrait() {
        this(32);
    }

    public ReinforcementsTrait(int range) {
        this(range, new ArrayList<>());
    }

    public ReinforcementsTrait(List<EntityType<?>> reinforcementTypes) {
        this(32, reinforcementTypes);
    }

    public ReinforcementsTrait(int range, @NotNull List<EntityType<?>> reinforcementTypes) {
        this(range, reinforcementTypes, new ArrayList<>());
    }

    public ReinforcementsTrait(int range, @NotNull List<EntityType<?>> reinforcementTypes, @NotNull List<TagKey<EntityType<?>>> reinforcementTags) {
        this.range = range;
        this.reinforcementTypes = reinforcementTypes;
        this.reinforcementTags = reinforcementTags;
    }

    public boolean hasReinforcements() {
        return !reinforcementTypes.isEmpty() || !reinforcementTags.isEmpty();
    }

    public boolean isReinforcement(Entity entity) {
        if (reinforcementTypes.contains(entity.getType())) return true;
        for (TagKey<EntityType<?>> reinforcementTag : reinforcementTags) {
            if (entity.getType().is(reinforcementTag)) return true;
        }
        return false;
    }

    public int getRange() {
        return range;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void renderIcon(RenderPipeline pipeline, @NotNull GuiGraphics graphics, int x, int y, int width, int height) {
        ItemStack stack = new ItemStack(Items.IRON_SWORD);
        graphics.renderItem(stack, x, y);
    }
}
