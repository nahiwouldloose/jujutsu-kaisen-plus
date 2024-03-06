package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.PackCursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class SquidCurseEntity extends PackCursedSpirit {
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");

    private static final EntityDataAccessor<Boolean> DATA_GRABBING = SynchedEntityData.defineId(SquidCurseEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation GRAB = RawAnimation.begin().thenPlayAndHold("misc.grab");

    private static final float EXPLOSIVE_POWER = 5.0F;
    private static final float MAX_EXPLOSION = 10.0F;

    @Nullable
    private LivingEntity target;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_GRABBING, false);
    }

    public SquidCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);

        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public SquidCurseEntity(SquidCurseEntity leader) {
        this(JJKEntities.SQUID_CURSE.get(), leader.level());

        this.setLeader(leader);
    }

    public void shoot(LivingEntity target) {
        this.target = target;
    }

    @Override
    public int getMinCount() {
        return 2;
    }

    @Override
    public int getMaxCount() {
        return 8;
    }

    @Override
    protected PackCursedSpirit spawn() {
        return new SquidCurseEntity(this);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean canFly() {
        return true;
    }

    @Override
    public boolean hasMeleeAttack() {
        return true;
    }

    @Override
    public boolean hasArms() {
        return false;
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public boolean canChant() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.MAX_HEALTH, 5.0D);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.GRADE_4.getRequiredExperience();
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return null;
    }

    private PlayState swimPredicate(AnimationState<SquidCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(SWIM);
        }
        return PlayState.STOP;
    }

    @Override
    public boolean isPushable() {
        return super.isPushable();
    }

    @Override
    public void tick() {
        super.tick();

        this.yHeadRot = this.getYRot();

        if (this.level().isClientSide) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        boolean isShootable = this.target != null && !this.target.isDeadOrDying() && !this.target.isRemoved();

        if (isShootable) {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, this.target.position().add(0.0D, this.target.getBbHeight() / 2.0F, 0.0D));

            this.setDeltaMovement(this.target.position().subtract(this.position()).normalize().scale(2.0D));

            if (this.distanceTo(this.target) < 1.0D) {
                Vec3 location = this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D)
                        .subtract(0.0D, this.target.getBbHeight() / 2.0F, 0.0D);
                ExplosionHandler.spawn(this.level().dimension(), location, Math.min(MAX_EXPLOSION, EXPLOSIVE_POWER),
                        20, 1, owner, JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.EMBER_INSECTS.get()), true);

            }
        } else {
            if (this.target == null) return;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Swim", this::swimPredicate));
    }
}
