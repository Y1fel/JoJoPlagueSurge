package com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class CriminalDuVillagerEntity extends DuVillagerEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation SINNERS_SOUL_ID = ResourceLocation.tryParse("jcraft:sinners_soul");
    private static final String CRIMINAL_TEAM_NAME = "jojoplaguesurge_criminal_red";
    private static boolean missingSinnersSoulLogged;

    public CriminalDuVillagerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && (this.tickCount <= 1 || this.tickCount % 40 == 0)) {
            ensureRedOutlineTeam();
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource damageSource, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(damageSource, looting, recentlyHit);

        Item sinnersSoul = SINNERS_SOUL_ID == null ? null : ForgeRegistries.ITEMS.getValue(SINNERS_SOUL_ID);
        if (sinnersSoul == null) {
            if (!missingSinnersSoulLogged) {
                missingSinnersSoulLogged = true;
                LOGGER.warn("Criminal DuVillager could not drop {} because the item is not registered on this server. Install the JCraft mod on both client and dedicated server.", SINNERS_SOUL_ID);
            }
            return;
        }

        this.spawnAtLocation(new ItemStack(sinnersSoul));
    }

    private void ensureRedOutlineTeam() {
        Scoreboard scoreboard = this.level().getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(CRIMINAL_TEAM_NAME);
        if (team == null) {
            team = scoreboard.addPlayerTeam(CRIMINAL_TEAM_NAME);
            team.setColor(ChatFormatting.RED);
        }

        String scoreboardName = this.getScoreboardName();
        PlayerTeam currentTeam = scoreboard.getPlayersTeam(scoreboardName);
        if (currentTeam != team) {
            if (currentTeam != null) {
                scoreboard.removePlayerFromTeam(scoreboardName, currentTeam);
            }
            scoreboard.addPlayerToTeam(scoreboardName, team);
        }
    }
}
