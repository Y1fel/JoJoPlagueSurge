package com.Y1fel.JoJoPlagueSurge.entity.custom.villager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

public abstract class BaseDuVillager extends PathfinderMob {
    protected BaseDuVillager(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag){
        super.readAdditionalSaveData(tag);
    }

    public abstract SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                                 DifficultyInstance difficulty,
                                                 MobSpawnType spawnType,
                                                 SpawnGroupData spawnGroupData);
}
