package com.Y1fel.JoJoPlagueSurge.entity.custom.trackingtornado;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TrackingTornadoEntity extends Entity implements GeoEntity{
    private int lifeTicks;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Optional<UUID>> TARGET_UUID =
            SynchedEntityData.defineId(TrackingTornadoEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(TrackingTornadoEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public TrackingTornadoEntity(EntityType<? extends TrackingTornadoEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_UUID, Optional.empty());
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setTarget(LivingEntity target) {
        this.entityData.set(TARGET_UUID, Optional.of(target.getUUID()));
    }

    public void setOwner(Player owner) {
        this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
    }
    @Nullable
    public LivingEntity getTargetEntity() {
        Optional<UUID> optional = this.entityData.get(TARGET_UUID);
        if (optional.isEmpty() || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        Entity entity = serverLevel.getEntity(optional.get());
        return entity instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    public void tick() {
        super.tick();

        lifeTicks++;
        if (lifeTicks > 20 * 15) { // 8秒寿命
            this.discard();
            return;
        }

        if (this.level().isClientSide) {
            return;
        }

        LivingEntity target = getTargetEntity();
        if (target == null || !target.isAlive()) {
            this.discard();
            return;
        }

        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5D, 0);
        Vec3 currentPos = this.position();
        Vec3 toTarget = targetPos.subtract(currentPos);

        double distance = toTarget.length();
        if (distance < 1.0D) {
            onHitTarget(target);
            return;
        }

        Vec3 desiredVelocity = toTarget.normalize().scale(0.45D);
        Vec3 smoothVelocity = this.getDeltaMovement().scale(0.75D).add(desiredVelocity.scale(0.25D));
        this.setDeltaMovement(smoothVelocity);
        this.move(MoverType.SELF, smoothVelocity);

        if (this.tickCount % 2 == 0 && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    this.getX(), this.getY() + 0.4D, this.getZ(),
                    4, 0.15D, 0.25D, 0.15D, 0.01D);
        }
    }

    private void onHitTarget(LivingEntity target) {
        target.hurt(this.damageSources().magic(), 2.0F);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2));
        this.discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Target")) {
            this.entityData.set(TARGET_UUID, Optional.of(tag.getUUID("Target")));
        }
        if (tag.hasUUID("Owner")) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID("Owner")));
        }
        this.lifeTicks = tag.getInt("LifeTicks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.entityData.get(TARGET_UUID).ifPresent(uuid -> tag.putUUID("Target", uuid));
        this.entityData.get(OWNER_UUID).ifPresent(uuid -> tag.putUUID("Owner", uuid));
        tag.putInt("LifeTicks", this.lifeTicks);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.tracking_tornado.idle"));
            return PlayState.CONTINUE;
        }));
    }

}
