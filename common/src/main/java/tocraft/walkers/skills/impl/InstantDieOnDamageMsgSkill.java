package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class InstantDieOnDamageMsgSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("instant_die_on_damage_msg");
    public static final Codec<InstantDieOnDamageMsgSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("msgId").forGetter(o -> o.msgId)
    ).apply(instance, instance.stable(InstantDieOnDamageMsgSkill::new)));

    public InstantDieOnDamageMsgSkill(String msgId) {
        this.msgId = msgId;
    }

    public final String msgId;

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
