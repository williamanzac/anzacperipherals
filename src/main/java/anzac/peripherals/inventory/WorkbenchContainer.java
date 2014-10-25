package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import anzac.peripherals.tile.WorkbenchTileEntity;

public class WorkbenchContainer extends BaseContainer<WorkbenchTileEntity> {

	public WorkbenchContainer(InventoryPlayer inventoryPlayer, WorkbenchTileEntity te) {
		super(te);

		// result
		addSlotToContainer(new SlotUntouchable(te.craftResult, 0, 68, 35));
		int row;
		int col;
		// recipe
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 3; ++col) {
				addSlotToContainer(new SlotUntouchable(te.craftMatrix, col + row * 3, 8 + col * 18, 17 + row * 18));
			}
		}

		// output
		addSlotToContainer(new SlotOutput(te, 0, 152, 35));
		// input
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 3; ++col) {
				addSlotToContainer(new Slot(te, 1 + col + row * 3, 92 + col * 18, 17 + row * 18));
			}
		}

		// player inventory
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 9; ++col) {
				addSlotToContainer(new Slot(inventoryPlayer, 9 + col + row * 9, 8 + col * 18, 88 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row) {
			addSlotToContainer(new Slot(inventoryPlayer, row, 8 + row * 18, 146));
		}

		te.updateCraftingRecipe();
	}
}
