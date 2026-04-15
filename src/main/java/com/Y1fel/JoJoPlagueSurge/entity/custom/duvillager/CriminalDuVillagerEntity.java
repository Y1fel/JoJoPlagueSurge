package com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class CriminalDuVillagerEntity extends DuVillagerEntity {
    private static final ResourceLocation SINNERS_SOUL_ID = ResourceLocation.tryParse("jcraft:sinners_soul");

    public CriminalDuVillagerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource damageSource, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(damageSource, looting, recentlyHit);

        Item sinnersSoul = SINNERS_SOUL_ID == null ? null : ForgeRegistries.ITEMS.getValue(SINNERS_SOUL_ID);
        if (sinnersSoul != null) {
            this.spawnAtLocation(new ItemStack(sinnersSoul));
        }
    }
}
