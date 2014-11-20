package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import anzac.peripherals.tile.ChargeStationTileEntity;

public class ChargeStationContainer extends BaseContainer<ChargeStationTileEntity> {

	private int lastStored;
	private int lastTime;
	private int lastTotalTime;

	public ChargeStationContainer(final InventoryPlayer inventoryPlayer, final ChargeStationTileEntity te) {
		super(te);

		// inventory
		addSlotToContainer(new Slot(te, 0, 26, 53));

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
		par1iCrafting.sendProgressBarUpdate(this, 0, tileEntity.getEnergyStored());
		par1iCrafting.sendProgressBarUpdate(this, 1, tileEntity.getBurnTime());
		par1iCrafting.sendProgressBarUpdate(this, 2, tileEntity.getTotalBurnTime());
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		final int storedEnergy = tileEntity.getEnergyStored();
		final int burnTime = tileEntity.getBurnTime();
		final int totalTime = tileEntity.getTotalBurnTime();
		if (lastStored != storedEnergy || lastTime != burnTime || lastTotalTime != totalTime) {
			for (final Object crafting : crafters) {
				sendUpdate((ICrafting) crafting);
			}
		}
		lastStored = storedEnergy;
		lastTime = burnTime;
		lastTotalTime = totalTime;
	}

	@Override
	public void updateProgressBar(final int index, final int value) {
		switch (index) {
		case 0:
			tileEntity.setEnergyStored(value);
			break;
		case 1:
			tileEntity.setBurnTime(value);
			break;
		case 2:
			tileEntity.setTotalBurnTime(value);
			break;
		}
	}
}
