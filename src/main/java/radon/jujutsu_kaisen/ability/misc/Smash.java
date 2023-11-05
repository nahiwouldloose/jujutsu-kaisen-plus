package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.damage.JJKDamageSources;

import java.util.List;

public class Smash extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final double LAUNCH_POWER = 2.5D;

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.distanceTo(target) <= 5.0D;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner instanceof Player) || !owner.level().isClientSide) return;
        ClientWrapper.setOverlayMessage(Component.translatable(String.format("chat.%s.charge", JujutsuKaisen.MOD_ID),
                Math.round(((float) Math.min(20, this.getCharge(owner)) / 20) * 100)), false);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return JJKAbilities.isChanneling(owner, this) && this.getCharge(owner) >= 20 ? 0.0F : 0.5F;
    }

    @Override
    public int getCooldown() {
        return 3 * 20;
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        float radius = 1.0F + this.getPower(owner) * 0.5F * ((float) Math.min(20, this.getCharge(owner)) / 20);

        Vec3 explosionPos = owner.getEyePosition().add(owner.getLookAngle());

        float f2 = radius * 2.0F;
        int k1 = Mth.floor(explosionPos.x() - (double) f2 - 1.0D);
        int l1 = Mth.floor(explosionPos.x() + (double) f2 + 1.0D);
        int i2 = Mth.floor(explosionPos.y() - (double) f2 - 1.0D);
        int i1 = Mth.floor(explosionPos.y() + (double) f2 + 1.0D);
        int j2 = Mth.floor(explosionPos.z() - (double) f2 - 1.0D);
        int j1 = Mth.floor(explosionPos.z() + (double) f2 + 1.0D);
        List<Entity> entities = owner.level().getEntities(owner, new AABB(k1, i2, j2, l1, i1, j1));

        for (Entity entity : entities) {
            if (!entity.ignoreExplosion()) {
                double d12 = Math.sqrt(entity.distanceToSqr(explosionPos)) / (double) f2;

                if (d12 <= 1.0D) {
                    double d5 = entity.getX() - explosionPos.x();
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - explosionPos.y();
                    double d9 = entity.getZ() - explosionPos.z();
                    double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                    if (d13 != 0.0D) {
                        d5 /= d13;
                        d7 /= d13;
                        d9 /= d13;
                        double d14 = Explosion.getSeenPercent(explosionPos, entity);
                        double d10 = (1.0D - d12) * d14;
                        double d11;

                        if (entity instanceof LivingEntity living) {
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(living, d10);
                        } else {
                            d11 = d10;
                        }

                        d5 *= d11;
                        d7 *= d11;
                        d9 *= d11;
                        Vec3 movement = new Vec3(d5, d7, d9);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(movement.scale(LAUNCH_POWER)));
                        entity.hurtMarked = true;
                    }
                }
            }
        }
        owner.level().explode(owner, JJKDamageSources.indirectJujutsuAttack(owner, owner, this), null, explosionPos, radius, false,
                owner.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
