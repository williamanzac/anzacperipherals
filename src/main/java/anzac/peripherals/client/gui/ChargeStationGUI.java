package anzac.peripherals.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import anzac.peripherals.inventory.ChargeStationContainer;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Names.Blocks;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.tile.ChargingStationTileEntity;

public class ChargeStationGUI extends GuiContainer {

	public static final ResourceLocation gui = new ResourceLocation(Reference.MOD_ID.toLowerCase(),
			"textures/gui/chargestation.png");
	private final ChargingStationTileEntity entity;

	public ChargeStationGUI(final InventoryPlayer inventoryPlayer, final ChargingStationTileEntity tileEntity) {
		super(new ChargeStationContainer(inventoryPlayer, tileEntity));
		ySize = 166;
		this.entity = tileEntity;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float f, final int i, final int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(gui);
		final int x = (width - xSize) / 2;
		final int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		final float energyStored = entity.getEnergyStored();
		final float maxEnergyStored = entity.getMaxEnergyStored();
		final int energy = (int) (40f * (energyStored / maxEnergyStored));
		if (energy > 0) {
			drawTexturedModalRect(x + 136, y + 69 - energy, 191, 41 - energy, 12, energy);
		}

		final float burnTime = entity.getBurnTime();
		final float totalBurnTime = entity.getTotalBurnTime();
		final int time = (int) (12f * (burnTime / totalBurnTime));
		if (time > 0) {
			drawTexturedModalRect(x + 26, y + 49 - time, 176, 12 - time, 14, time + 2);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int p_146979_1_, final int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		final String title = StatCollector.translateToLocal(Names.getGUIKey(Blocks.chargestation));
		fontRendererObj.drawString(title, 8, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 4210752);
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		super.drawScreen(i, j, f);
		if (func_146978_c(136, 29, 12, 40, i, j)) {
			final int energyStored = entity.getEnergyStored();
			final int maxEnergyStored = entity.getMaxEnergyStored();
			drawCreativeTabHoveringText(energyStored + " / " + maxEnergyStored, i, j);
		}
	}
}
