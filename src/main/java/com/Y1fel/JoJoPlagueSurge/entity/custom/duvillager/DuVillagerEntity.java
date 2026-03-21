package com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

public class DuVillagerEntity extends BaseDuVillager {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(DuVillagerEntity.class, EntityDataSerializers.INT);
    public DuVillagerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, -1);
    }
    public void setVariant(int variant) {
        this.entityData.set(VARIANT,variant);
    }
    public int getVariant() {
        return this.entityData.get(VARIANT);
    }
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Variant")) {
            this.setVariant(tag.getInt("Variant"));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        SpawnGroupData spawnGroupData,
                                        CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);

        if (!this.hasVariantAssigned()) {
            this.setVariant(this.random.nextInt(10) + 1);
        }

        return data;
    }

    private boolean hasVariantAssigned() {
        int v = this.getVariant();
        return v >= 1 && v <= 10;
    }
}
