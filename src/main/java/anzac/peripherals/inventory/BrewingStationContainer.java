package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import anzac.peripherals.tile.BrewingStationTileEntity;

public class BrewingStationContainer extends BaseContainer<BrewingStationTileEntity> {

	private int lastAmount;
	private int lastId;

	public BrewingStationContainer(final InventoryPlayer inventoryPlayer, final BrewingStationTileEntity te) {
		super(te);

		// empty bottles
		addSlotToContainer(new Slot(te, 0, 8, 89));

		int row;
		int col;

		// potion input
		for (row = 0; row < 3; ++row) {
			addSlotToContainer(new ValidatingSlot(te, 1 + row, 44, 53 + row * 18));
		}

		// input
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 3; ++col) {
				addSlotToContainer(new Slot(te, 4 + col + row * 3, 80 + col * 18, 53 + row * 18));
			}
		}
		// potion output
		for (row = 0; row < 3; ++row) {
			addSlotToContainer(new SlotOutput(te, 13 + row, 152, 53 + row * 18));
		}

		// ingredients
		for (col = 0; col < 6; ++col) {
			addSlotToContainer(new SlotPhantom(te.ingredients, col, 44 + col * 18, 17) {
				@Override
				public boolean isItemValid(final ItemStack stack) {
					return stack.getItem().isPotionIngredient(stack);
				}
			});
		}

		// player inventory
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 9; ++col) {
				addSlotToContainer(new Slot(inventoryPlayer, 9 + col + row * 9, 8 + col * 18, 120 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row) {
			addSlotToContainer(new Slot(inventoryPlayer, row, 8 + row * 18, 178));
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
			break;
		}
	}
}
