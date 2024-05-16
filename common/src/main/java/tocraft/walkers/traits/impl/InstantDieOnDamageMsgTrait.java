package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

public class InstantDieOnDamageMsgTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("instant_die_on_damage_msg");
    public static final Codec<InstantDieOnDamageMsgTrait<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("msgId").forGetter(o -> o.msgId)
    ).apply(instance, instance.stable(InstantDieOnDamageMsgTrait::new)));

    public InstantDieOnDamageMsgTrait(String msgId) {
        this.msgId = msgId;
    }

    public final String msgId;

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }
}
