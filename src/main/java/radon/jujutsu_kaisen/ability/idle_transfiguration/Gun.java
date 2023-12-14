package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Gun extends Transformation {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, this)) {
            return target != null && HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return target != null && HelperMethods.RANDOM.nextInt(5) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.1F;
    }

    @Override
    public boolean isReplacement() {
        return false;
    }

    @Override
    public Item getItem() {
        return JJKItems.GUN.get();
    }

    @Override
    public Part getBodyPart() {
        return Part.RIGHT_ARM;
    }

    @Override
    public void onRightClick(LivingEntity owner) {
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SHOOT.get(), SoundSource.MASTER, 1.0F, 1.0F);
    }

    @Override
    public void applyModifiers(LivingEntity owner) {

    }

    @Override
    public void removeModifiers(LivingEntity owner) {

    }
}