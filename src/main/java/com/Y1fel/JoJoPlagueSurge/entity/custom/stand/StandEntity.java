package com.Y1fel.JoJoPlagueSurge.entity.custom.stand;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * 基础替身实体：负责绑定主人、跟随和基础生命周期。
 * 目标是让所有替身（DuWang / BlueHawaii）共享同一套核心行为，接近 JCraft 的“替身附身”体验。
 */
public abstract class StandEntity extends Monster {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private int missingOwnerTicks;
    private int attackCooldownTicks;

    protected StandEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
    }

    public static AttributeSupplier.Builder createStandAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setOwner(Player player) {
        this.entityData.set(OWNER_UUID, Optional.of(player.getUUID()));
    }

    @Nullable
    public Player getOwner() {
        Optional<UUID> ownerId = this.entityData.get(OWNER_UUID);
        if (ownerId.isEmpty() || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        Entity entity = serverLevel.getEntity(ownerId.get());
        return entity instanceof Player player ? player : null;
    }

    public boolean isOwnedBy(Player player) {
        return this.entityData.get(OWNER_UUID).map(uuid -> uuid.equals(player.getUUID())).orElse(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        if (this.level().isClientSide) {
            return;
        }

        Player owner = getOwner();
        if (owner == null || !owner.isAlive()) {
            missingOwnerTicks++;
            if (missingOwnerTicks > 20 * 5) {
                this.discard();
            }
            return;
        }

        missingOwnerTicks = 0;
        followOwner(owner);
        syncTargetFromOwner(owner);
        tickCombat(owner);
    }

    protected void syncTargetFromOwner(Player owner) {
        if (owner.getLastHurtMob() != null && owner.getLastHurtMob().isAlive() && owner.getLastHurtMob() != owner) {
            this.setTarget(owner.getLastHurtMob());
        }
    }

    protected void tickCombat(Player owner) {
        if (attackCooldownTicks > 0) {
            attackCooldownTicks--;
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target == owner) {
            return;
        }

        Vec3 toTarget = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(this.position());
        double distanceToTarget = toTarget.length();

        if (distanceToTarget > 0.001D) {
            Vec3 chase = toTarget.normalize().scale(0.35D);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.55D).add(chase));
        }

        if (distanceToTarget <= 2.2D && attackCooldownTicks <= 0) {
            this.doHurtTarget(target);
            attackCooldownTicks = 10;
        }
    }

    protected void followOwner(Player owner) {
        float side = this.getUUID().hashCode() % 2 == 0 ? 1.0F : -1.0F;
        Vec3 sideOffset = owner.getLookAngle().yRot((float) (Math.PI / 2D)).normalize().scale(1.15D * side);
        Vec3 desiredPos = owner.position().add(sideOffset).add(0.0D, 1.1D, 0.0D);

        double distance = this.position().distanceTo(desiredPos);
        if (distance > 16.0D) {
            this.teleportTo(desiredPos.x, desiredPos.y, desiredPos.z);
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }

        Vec3 motion = desiredPos.subtract(this.position()).scale(0.22D);
        this.setDeltaMovement(this.getDeltaMovement().scale(0.72D).add(motion));
        this.setYRot(Mth.rotLerp(0.2F, this.getYRot(), owner.getYRot()));
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && isOwnedBy(player)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.entityData.get(OWNER_UUID).ifPresent(uuid -> tag.putUUID("StandOwner", uuid));
        tag.putInt("MissingOwnerTicks", missingOwnerTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("StandOwner")) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID("StandOwner")));
        }
        this.missingOwnerTicks = tag.getInt("MissingOwnerTicks");
    }
}
