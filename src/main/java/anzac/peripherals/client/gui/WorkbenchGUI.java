package anzac.peripherals.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import anzac.peripherals.inventory.WorkbenchContainer;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Names.Blocks;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.tile.WorkbenchTileEntity;

public class WorkbenchGUI extends GuiContainer {

	public static final ResourceLocation gui = new ResourceLocation(Reference.MOD_ID.toLowerCase(),
			"textures/gui/workbench.png");

	public WorkbenchGUI(InventoryPlayer inventoryPlayer, WorkbenchTileEntity tileEntity) {
		super(new WorkbenchContainer(inventoryPlayer, tileEntity));
		ySize = 170;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(gui);
		final int x = (width - xSize) / 2;
		final int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		final String title = StatCollector.translateToLocal(Names.getGUIKey(Blocks.workbench));
		fontRendererObj.drawString(title, 8, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 4210752);
	}
}
