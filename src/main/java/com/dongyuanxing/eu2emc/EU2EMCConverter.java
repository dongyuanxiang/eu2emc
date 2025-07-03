package com.dongyuanxing.eu2emc;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = EU2EMCConverter.MODID, name = EU2EMCConverter.NAME, version = EU2EMCConverter.VERSION)
public class EU2EMCConverter {
    public static final String MODID = "eu2emc";
    public static final String NAME = "eu2emc";
    public static final String VERSION = "1.0";

    @Mod.Instance(MODID)
    public static EU2EMCConverter instance;

    public static SimpleNetworkWrapper network;
    private static Logger logger;

    public static BlockEnergyConverter blockEnergyConverter;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        // 初始化网络
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(EMCCollectPacket.Handler.class, EMCCollectPacket.class, 0, Side.SERVER);

        // 注册GUI处理器

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }


}