package com.Y1fel.JoJoPlagueSurge.item;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final String MOD_ID = ModEntrance.MODID;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> BLOODY_TOOTH =
            ITEMS.register("bloody_tooth", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> STAND_DISC =
            ITEMS.register("stand_disc", () -> new StandDiscItem(new Item.Properties().stacksTo(1),
                    StandType.DUWANG));
    public static final RegistryObject<Item> BLUEHAWAII_STAND_DISC =
            ITEMS.register("bluehawaii_stand_disc", () -> new StandDiscItem(new Item.Properties().stacksTo(1),
                    StandType.BLUE_HAWAII));
    public static final RegistryObject<Item> CRESCENT_MOON_DISC =
            ITEMS.register("crescent_moon_disc", () -> new CrescentMoonDiscItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SPIRIT_VISION_POTION =
            ITEMS.register("spirit_vision_potion", () -> new SpiritVisionPotionItem(new Item.Properties()));
    public static final RegistryObject<Item> SKILL_1_TEST_POTION =
            ITEMS.register("skill_1_test_potion", () -> new Skill1TestPotionItem(new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> DUVILLAGER_SPAWN_EGG =
            ITEMS.register("duvillager_spawn_egg", () -> new ForgeSpawnEggItem(
                    ModEntities.DUVILLAGER,
                    0x3B2A22,
                    0xD6C3A5,
                    new Item.Properties()
            ));
    public static final RegistryObject<ForgeSpawnEggItem> CRIMINAL_DUVILLAGER_SPAWN_EGG =
            ITEMS.register("criminal_duvillager_spawn_egg", () -> new ForgeSpawnEggItem(
                    ModEntities.CRIMINAL_DUVILLAGER,
                    0x201914,
                    0xA32C24,
                    new Item.Properties()
            ));
    public static final RegistryObject<ForgeSpawnEggItem> MIRACLE_SPAWN_EGG =
            ITEMS.register("miracle_spawn_egg", () -> new ForgeSpawnEggItem(
                    ModEntities.MIRACLE,
                    0x140C22,
                    0xE1C56A,
                    new Item.Properties()
            ));
    public static final RegistryObject<ForgeSpawnEggItem> STONEINSECT_SPAWN_EGG =
            ITEMS.register("stoneinsect_spawn_egg", () -> new ForgeSpawnEggItem(
                    ModEntities.STONEINSECT,
                    0x4D4B45,
                    0xA8936B,
                    new Item.Properties()
            ));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
