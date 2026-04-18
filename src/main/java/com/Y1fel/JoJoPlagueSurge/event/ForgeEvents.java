package com.Y1fel.JoJoPlagueSurge.event;

import com.Y1fel.JoJoPlagueSurge.item.Skill1TestPotionItem;
import com.Y1fel.JoJoPlagueSurge.network.packet.BlueHawaiiSkillLogic;
import com.Y1fel.JoJoPlagueSurge.network.packet.OzoneSkillLogic;
import com.Y1fel.JoJoPlagueSurge.network.packet.SkillCooldowns;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
//import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEvents {

    //@SubscribeEvent
    //public static void addEntitySpawn(BiomeLoadingEvent event) {
        // 这里只是模板：默认把实体加到所有群系的怪物刷怪表里。
        // 真做项目时你最好按群系名、温度、维度再过滤一层。
   //     event.getSpawns().getSpawner(MobCategory.MONSTER).add(
    //            new MobSpawnSettings.SpawnerData(ModEntities.EXAMPLE_MOB.get(), 80, 1, 3)
    //    );
    //}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }

        if (event.player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            Skill1TestPotionItem.onServerPlayerTick(serverPlayer);
            BlueHawaiiSkillLogic.onServerPlayerTick(serverPlayer);
            OzoneSkillLogic.onServerPlayerTick(serverPlayer);
            SkillCooldowns.sync(serverPlayer);
        }
    }

}
