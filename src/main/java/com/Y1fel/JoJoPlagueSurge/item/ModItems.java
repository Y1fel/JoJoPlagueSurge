package com.Y1fel.JoJoPlagueSurge.item;


import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final String MOD_ID = ModEntrance.MODID;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> FIRST_ITEM =
            ITEMS.register("first_item", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SECOND_ITEM =
            ITEMS.register("second_item", () -> new Item(new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> BLUEHAWAII_SPAWN_EGG =
            ITEMS.register("bluehawaii_spawn_egg", () -> new ForgeSpawnEggItem(
                    ModEntities.BLUEHAWAII,
                    0x1E4470, // 主颜色
                    0x2B5F89, // 斑点颜色
                    new Item.Properties()
            ));
    public static final RegistryObject<ForgeSpawnEggItem> VILLAGER1_SPAWN_EGG =
            ITEMS.register("villager_spawn_egg", () -> new ForgeSpawnEggItem(
                    ModEntities.DUVILLAGER,
                    ,
                    ,
                    new Item.Properties()
            ));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

}
