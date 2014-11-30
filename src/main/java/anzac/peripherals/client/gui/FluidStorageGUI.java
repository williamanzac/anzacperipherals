package anzac.peripherals.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidTankInfo;

import org.lwjgl.opengl.GL11;

import anzac.peripherals.inventory.FluidStorageContainer;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Names.Blocks;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.tile.FluidStorageTileEntity;

public class FluidStorageGUI extends GuiContainer {

	public static final ResourceLocation gui = new ResourceLocation(Reference.MOD_ID.toLowerCase(),
			"textures/gui/fluidstorage.png");
	private final FluidStorageTileEntity entity;

	public FluidStorageGUI(final InventoryPlayer inventoryPlayer, final FluidStorageTileEntity tileEntity) {
		super(new FluidStorageContainer(inventoryPlayer, tileEntity));
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

		for (int index = 0; index < 8; index++) {
			final FluidTankInfo info = entity.getInfo(index);
			// AnzacPeripheralsCore.logger.info("info: " + info);
			if (info.fluid != null) {
				// AnzacPeripheralsCore.logger.info("fluid: " + info.fluid);
				final int capacity = info.capacity;
				final int amount = info.fluid.amount;
				final float scale = Math.min(amount, capacity) / (float) capacity;
				// AnzacPeripheralsCore.logger.info("amount: " + amount + ", capacity: " + capacity + ", ratio: " +
				// energy);
				mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
				final IIcon stillIcon = info.fluid.getFluid().getStillIcon();
				// AnzacPeripheralsCore.logger.info("icon: " + stillIcon);
				if (stillIcon != null) {
					for (int row = 0; row <= 47 / 16; row++) {
						// AnzacPeripheralsCore.logger.info("col: " + col + ", row: " + row);
						drawTexturedModelRectFromIcon(x + 8 + index * 18, 21 + y + row * 16, stillIcon, 16, 16);
					}
					this.mc.renderEngine.bindTexture(gui);
					drawTexturedModalRect(x + 8 + index * 18, y + 21, 8 + index * 18, 21, 16,
							47 - (int) Math.floor(47 * scale) + 1);
					// drawTexturedModalRect(x + 26, y + 29, 177, 1, 16, 47);
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int p_146979_1_, final int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		final String title = StatCollector.translateToLocal(Names.getGUIKey(Blocks.fluidstorage));
		fontRendererObj.drawString(title, 8, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 4210752);
	}

	@Override
	public void drawScreen(final int i, final int j, final float f) {
		super.drawScreen(i, j, f);

		for (int index = 0; index < 8; index++) {
			final FluidTankInfo info = entity.getInfo(index);
			// AnzacPeripheralsCore.logger.info("info: " + info);
			// AnzacPeripheralsCore.logger.info("fluid: " + info.fluid);
			final int capacity = info.capacity;
			final int amount = info.fluid != null ? info.fluid.amount : 0;
			// AnzacPeripheralsCore.logger.info("amount: " + amount + ", capacity: " + capacity + ", ratio: " + energy);
			if (func_146978_c(8 + index * 18, 22, 16, 47, i, j)) {
				drawCreativeTabHoveringText("Stored: " + amount + " / " + capacity, i, j);
			}
		}
	}
}
