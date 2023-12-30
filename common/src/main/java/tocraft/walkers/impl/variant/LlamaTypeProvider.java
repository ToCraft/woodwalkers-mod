package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class LlamaTypeProvider extends TypeProvider<Llama> {

    @Override
    public int getVariantData(Llama llama) {
        return llama.getVariant();
    }

    @Override
    public Llama create(EntityType<Llama> type, Level world, int data) {
    	Llama llama = new Llama(type, world);
    	llama.setVariant(data);
        return llama;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 3;
    }

	@Override
	public Component modifyText(Llama entity, MutableComponent text) {
		return new TextComponent(entity.getVariant() + " ").append(text);
	}
}
