package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import anzac.peripherals.tile.FluidStorageTileEntity;

public class FluidStorageContainer extends BaseContainer<FluidStorageTileEntity> {

	private final int[] lastAmount = new int[8];
	private final int[] lastId = new int[8];

	public FluidStorageContainer(final InventoryPlayer inventoryPlayer, final FluidStorageTileEntity te) {
		super(te);

		// inventory
		// addSlotToContainer(new ValidatingSlot(te, 0, 152, 53));

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
		for (int i = 0; i < lastAmount.length; i++) {
			final int amount = tileEntity.getFluidAmount(i);
			final int id = tileEntity.getFluid(i);
			par1iCrafting.sendProgressBarUpdate(this, i, amount);
			par1iCrafting.sendProgressBarUpdate(this, i + lastAmount.length, id);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		boolean changed = false;
		for (int i = 0; i < lastAmount.length; i++) {
			final int amount = tileEntity.getFluidAmount(i);
			final int id = tileEntity.getFluid(i);
			if (lastAmount[i] != amount || lastId[i] != id) {
				changed = true;
			}
			lastAmount[i] = amount;
			lastId[i] = id;
		}
		if (changed) {
			for (final Object crafting : crafters) {
				sendUpdate((ICrafting) crafting);
			}
		}
	}

	@Override
	public void updateProgressBar(final int index, final int value) {
		if (index < lastAmount.length) {
			tileEntity.setFluidAmount(index, value);
		} else {
			tileEntity.setFluid(index - lastAmount.length, value);
		}
	}
}
