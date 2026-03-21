package com.Y1fel.JoJoPlagueSurge.event;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiRenderer;
import com.Y1fel.JoJoPlagueSurge.entity.custom.villager.DuVillagerRenderer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = ModEntrance.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("HELLO FROM CLIENT SETUP");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BLUEHAWAII.get(), BlueHawaiiRenderer::new);
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager1"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager2"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager3"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager4"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager5"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager6"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager7"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager8"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager9"));
        event.registerEntityRenderer(ModEntities.DUVILLAGER.get(), context ->
                new DuVillagerRenderer<>(context, "Villager10"));
    }
}
