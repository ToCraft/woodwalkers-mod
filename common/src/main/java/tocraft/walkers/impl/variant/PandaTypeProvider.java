package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;

public class PandaTypeProvider extends TypeProvider<Panda> {

    @Override
    public int getVariantData(Panda entity) {
        return entity.getMainGene().getId();
    }

    @Override
    public Panda create(EntityType<Panda> type, Level level, @NotNull Player player, int data) {
        Panda panda = new Panda(type, level);
        panda.setMainGene(Panda.Gene.byId(data));
        return panda;
    }

    @Override
    public int getFallbackData() {
        return Panda.Gene.NORMAL.getId();
    }

    @Override
    public int size(Level level) {
        return Panda.Gene.values().length;
    }

    @Override
    public Component modifyText(@NotNull Panda entity, MutableComponent text) {
        Panda.Gene gene = entity.getMainGene();
        if (gene.equals(Panda.Gene.NORMAL)) {
            return text;
        } else {
            String variantName = gene.getSerializedName();
            return Component.literal(formatTypePrefix(variantName) + " ").append(text);
        }
    }
}
