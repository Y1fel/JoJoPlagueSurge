package com.Y1fel.JoJoPlagueSurge.event;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager.CriminalDuVillagerEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager.DuVillagerEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.miracle.MiracleEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModEntrance.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BLUEHAWAII.get(), BlueHawaiiEntity.createAttributes().build());
        event.put(ModEntities.DUVILLAGER.get(), DuVillagerEntity.createAttributes().build());
        event.put(ModEntities.CRIMINAL_DUVILLAGER.get(), CriminalDuVillagerEntity.createAttributes().build());
        event.put(ModEntities.DUWANG.get(), DuWangEntity.createAttributes().build());
        event.put(ModEntities.MIRACLE.get(), MiracleEntity.createAttributes().build());
    }
}
