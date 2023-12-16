package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.ArrayList;

public abstract class Transformation extends Ability implements Ability.IToggled, ITransformation {
    @Override
    public Status checkTriggerable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (Ability ability : new ArrayList<>(cap.getToggled())) {
            if (!(ability instanceof ITransformation transformation) || ability == this) continue;

            if (transformation.getBodyPart() == this.getBodyPart()) {
                cap.toggle(ability);
            }
        }
        return super.checkTriggerable(owner);
    }
}
