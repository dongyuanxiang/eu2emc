package com.dongyuanxing.eu2emc;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class RegisterHandler {

    public static BlockEnergyConverter blockEnergyConverter;


    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        blockEnergyConverter = new BlockEnergyConverter();
        event.getRegistry().register(blockEnergyConverter);

        // 注册方块实体
        GameRegistry.registerTileEntity(
                TileEntityEnergyConverter.class,
                new ResourceLocation(EU2EMCConverter.MODID, "converter")
        );



    }




    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        // 注册方块对应的物品形式
        ItemBlock itemBlock = new ItemBlock(blockEnergyConverter);
        itemBlock.setRegistryName(blockEnergyConverter.getRegistryName());
        itemBlock.setUnlocalizedName(blockEnergyConverter.getUnlocalizedName());
        event.getRegistry().register(itemBlock);
        if (FMLCommonHandler.instance().getSide().isClient()){
            ModelResourceLocation inventory = new ModelResourceLocation(blockEnergyConverter.getRegistryName(), "inventory");
            ModelLoader.setCustomModelResourceLocation(itemBlock, 0,inventory );
        }
    }

}