package anzac.peripherals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import anzac.peripherals.tile.RecipeStorageTileEntity;

public class RecipeStorageContainer extends BaseContainer<RecipeStorageTileEntity> {

	private ItemStack prevOutput;

	public RecipeStorageContainer(InventoryPlayer inventoryPlayer, RecipeStorageTileEntity te) {
		super(te);

		// result
		addSlotToContainer(new SlotUntouchable(te.craftResult, 0, 134, 35));
		int row;
		int col;
		// recipe
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 3; ++col) {
				addSlotToContainer(new SlotPhantom(te.craftMatrix, col + row * 3, 62 + col * 18, 17 + row * 18));
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

	@Override
	public void onCraftMatrixChanged(final IInventory par1IInventory) {
		super.onCraftMatrixChanged(par1IInventory);
		((RecipeStorageTileEntity) tileEntity).updateCraftingRecipe();
	}

	@Override
	public ItemStack slotClick(final int i, final int j, final int modifier, final EntityPlayer entityplayer) {
		final ItemStack stack = super.slotClick(i, j, modifier, entityplayer);
		onCraftMatrixChanged(((RecipeStorageTileEntity) tileEntity).craftMatrix);
		return stack;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		final RecipeStorageTileEntity te = ((RecipeStorageTileEntity) tileEntity);
		final ItemStack output = te.craftResult.getStackInSlot(0);
		if (output != prevOutput) {
			prevOutput = output;
			onCraftMatrixChanged(te.craftMatrix);
		}
	}
}
