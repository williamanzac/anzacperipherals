package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import anzac.peripherals.tile.FluidRouterTileEntity;

public class FluidRouterContainer extends BaseContainer<FluidRouterTileEntity> {

	private int lastAmount;
	private int lastId;

	public FluidRouterContainer(final InventoryPlayer inventoryPlayer, final FluidRouterTileEntity te) {
		super(te);

		// inventory
		addSlotToContainer(new Slot(te, 0, 62, 53));

		int row;
		int col;
		// player inventory
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 9; ++col) {
				addSlotToContainer(new Slot(inventoryPlayer, 9 + col + row * 9, 8 + col * 18, 84 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row) {
			addSlotToContainer(new Slot(inventoryPlayer, row, 8 + row * 18, 142));
		}
	}

	@Override
	public void addCraftingToCrafters(final ICrafting par1iCrafting) {
		super.addCraftingToCrafters(par1iCrafting);
		sendUpdate(par1iCrafting);
	}

	private void sendUpdate(final ICrafting par1iCrafting) {
		final int amount = tileEntity.getFluidAmount();
		final int id = tileEntity.getFluid();
		par1iCrafting.sendProgressBarUpdate(this, 0, amount);
		par1iCrafting.sendProgressBarUpdate(this, 1, id);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		final int amount = tileEntity.getFluidAmount();
		final int id = tileEntity.getFluid();
		if (lastAmount != amount || lastId != id) {
			for (final Object crafting : crafters) {
				sendUpdate((ICrafting) crafting);
			}
		}
		lastAmount = amount;
		lastId = id;
	}

	@Override
	public void updateProgressBar(final int index, final int value) {
		switch (index) {
		case 0:
			tileEntity.setFluidAmount(value);
			break;
		case 1:
			tileEntity.setFluid(value);
		}
	}
}
