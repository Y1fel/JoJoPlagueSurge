package com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii;

import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlueHawaiiEntity extends StandEntity implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("Idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BlueHawaiiEntity(EntityType<? extends net.minecraft.world.entity.monster.Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return StandEntity.createStandAttributes();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
