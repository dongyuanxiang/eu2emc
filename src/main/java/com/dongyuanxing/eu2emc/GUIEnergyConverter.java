package com.dongyuanxing.eu2emc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class GUIEnergyConverter extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(EU2EMCConverter.MODID, "textures/gui/converter.png");
    private final TileEntityEnergyConverter te;
    private final BlockPos pos;

    // 添加客户端数据缓存
    private int clientCurrentEU = 0;
    private double clientStoredEMC = 0;
    private long clientTotalEU = 0;
    private double clientTotalEMC = 0;

    // 添加时间计数器
    private int tickCounter = 0;

    public GUIEnergyConverter(InventoryPlayer playerInv, TileEntityEnergyConverter te) {
        super(new ContainerEnergyConverter(playerInv, te));
        this.te = te;
        this.pos = te.getPos();
        this.xSize = 176;
        this.ySize = 166;

        // 初始化客户端数据
        updateClientData();
    }

    // 更新客户端数据
    private void updateClientData() {
        if (te != null && te.hasWorld() && !te.getWorld().isRemote) {
            // 如果是服务端，直接获取数据
            clientCurrentEU = te.getCurrentEU();
            clientStoredEMC = te.getStoredEMC();
            clientTotalEU = te.getTotalEUConsumed();
            clientTotalEMC = te.getTotalEMCProduced();
        } else {
            // 如果是客户端，使用缓存数据
            TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos);
            if (tile instanceof TileEntityEnergyConverter) {
                TileEntityEnergyConverter clientTE = (TileEntityEnergyConverter) tile;
                clientCurrentEU = clientTE.getCurrentEU();
                clientStoredEMC = clientTE.getStoredEMC();
                clientTotalEU = clientTE.getTotalEUConsumed();
                clientTotalEMC = clientTE.getTotalEMCProduced();
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, guiLeft + 108, guiTop + 60, 60, 20, I18n.format("gui.eu2emc.collect")));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("tile.converter.name");
        this.fontRenderer.drawString(title, (xSize - this.fontRenderer.getStringWidth(title)) / 2, 6, 0x404040);

        // 使用客户端缓存的数据
        this.fontRenderer.drawString(I18n.format("gui.eu2emc.eu_input", clientCurrentEU), 8, 20, 0x404040);
        this.fontRenderer.drawString(I18n.format("gui.eu2emc.stored_emc", String.format("%.2f", clientStoredEMC)), 8, 30, 0x404040);
        this.fontRenderer.drawString(I18n.format("gui.eu2emc.total_eu", clientTotalEU), 8, 40, 0x404040);
        this.fontRenderer.drawString(I18n.format("gui.eu2emc.total_emc", String.format("%.2f", clientTotalEMC)), 8, 50, 0x404040);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        // 更新计数器
        tickCounter++;

        // 每10tick更新一次客户端数据
        if (tickCounter % 10 == 0) {
            updateClientData();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            //点击收取按钮后自动关闭gui
            //Minecraft.getMinecraft().player.closeScreen();
            // 发送数据包到服务器处理收集请求
            EU2EMCConverter.network.sendToServer(new EMCCollectPacket(pos));
        }
    }
}