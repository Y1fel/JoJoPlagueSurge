package com.Y1fel.JoJoPlagueSurge;

import com.Y1fel.JoJoPlagueSurge.block.ModBlocks;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.event.ForgeEvents;
import com.Y1fel.JoJoPlagueSurge.item.ModCreativeModeTabs;
import com.Y1fel.JoJoPlagueSurge.item.ModItems;
import com.Y1fel.JoJoPlagueSurge.network.ModNetwork;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(ModEntrance.MODID)
public class ModEntrance {
    public static final String MODID = "jojoplaguesurge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModEntrance(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetwork.register();
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) {
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        }

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
        Config.items.forEach(item -> LOGGER.info("ITEM >> {}", item));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.OZONE);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BLOODY_TOOTH);
            event.accept(ModItems.STAND_DISC);
            event.accept(ModItems.BLUEHAWAII_STAND_DISC);
        }
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS){
            event.accept(ModItems.BLUEHAWAII_SPAWN_EGG);
            event.accept(ModItems.DUVILLAGER_SPAWN_EGG);
            event.accept(ModItems.CRIMINAL_DUVILLAGER_SPAWN_EGG);
            event.accept(ModItems.DUWANG_SPAWN_EGG);
            event.accept(ModItems.MIRACLE_SPAWN_EGG);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
