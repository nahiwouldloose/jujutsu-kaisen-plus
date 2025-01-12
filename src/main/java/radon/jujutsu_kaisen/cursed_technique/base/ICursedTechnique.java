package radon.jujutsu_kaisen.cursed_technique.base;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;

import java.util.Set;

public interface ICursedTechnique {
    @Nullable
    default Ability getDomain() {
        return null;
    }

    Set<Ability> getAbilities();

    default Component getName() {
        ResourceLocation key = JJKCursedTechniques.getKey(this);
        return Component.translatable(String.format("cursed_technique.%s.%s", key.getNamespace(), key.getPath()));
    }
}
