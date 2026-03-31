package com.Y1fel.JoJoPlagueSurge.block;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.block.custom.OzoneBlock;
import com.Y1fel.JoJoPlagueSurge.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> TEST_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ModEntrance.MODID);
    public static final RegistryObject<Block> FIRST_BLOCK =
            registerBlock("first_block",()->new Block(BlockBehaviour.Properties.of().strength(1.5F,3.0F)));
    public static final RegistryObject<Block> OZONE =
            registerBlock("ozone", () -> new OzoneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .strength(1.0F, 3.0F)
                    .noOcclusion()), new Item.Properties().stacksTo(1));

    private static <T extends Block> void registerBlockItems(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, ()->new BlockItem(block.get(),new Item.Properties()));
    }

    private static <T extends Block> void registerBlockItems(String name, RegistryObject<T> block, Item.Properties properties) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> blocks = TEST_BLOCKS.register(name,block);
        registerBlockItems(name,blocks);
        return blocks;
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Item.Properties properties) {
        RegistryObject<T> blocks = TEST_BLOCKS.register(name, block);
        registerBlockItems(name, blocks, properties);
        return blocks;
    }
    public static void register(IEventBus eventBus) {
         TEST_BLOCKS.register(eventBus);
    }
}
