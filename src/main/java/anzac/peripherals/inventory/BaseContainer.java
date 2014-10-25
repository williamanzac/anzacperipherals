package anzac.peripherals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import anzac.peripherals.tile.BaseTileEntity;
import anzac.peripherals.utility.InvUtils;
import buildcraft.core.gui.slots.SlotPhantom;

public abstract class BaseContainer<T extends BaseTileEntity> extends Container {
	private final T tileEntity;

	public BaseContainer(final T tileEntity) {
		super();
		this.tileEntity = tileEntity;
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		return ((IInventory) tileEntity).isUseableByPlayer(player);
	}

	@Override
	public ItemStack slotClick(final int slotNum, final int mouseButton, final int modifier, final EntityPlayer player) {
		final Slot slot = slotNum < 0 ? null : (Slot) this.inventorySlots.get(slotNum);
		if (slot instanceof ISpecialSlot) {
			return slotClickSpecial(slot, mouseButton, modifier, player);
		}
		return super.slotClick(slotNum, mouseButton, modifier, player);
	}

	private ItemStack slotClickSpecial(final Slot slot, final int mouseButton, final int modifier,
			final EntityPlayer player) {
		ItemStack stack = null;

		if (mouseButton == 2) {
			if (((ISpecialSlot) slot).isAdjustable()) {
				slot.putStack(null);
			}
		} else if (mouseButton == 0 || mouseButton == 1) {
			final InventoryPlayer playerInv = player.inventory;
			slot.onSlotChanged();
			final ItemStack stackSlot = slot.getStack();
			final ItemStack stackHeld = playerInv.getItemStack();

			if (stackSlot != null) {
				stack = stackSlot.copy();
			}

			if (stackSlot == null) {
				if (stackHeld != null && slot.isItemValid(stackHeld)) {
					fillSpecialSlot(slot, stackHeld, mouseButton, modifier);
				}
			} else if (stackHeld == null) {
				adjustSpecialSlot(slot, mouseButton, modifier);
				slot.onPickupFromSlot(player, playerInv.getItemStack());
			} else if (slot.isItemValid(stackHeld)) {
				if (InvUtils.canMergeItemStack(stackSlot, stackHeld)) {
					adjustSpecialSlot(slot, mouseButton, modifier);
				} else {
					fillSpecialSlot(slot, stackHeld, mouseButton, modifier);
				}
			}
		}
		return stack;
	}

	protected void adjustSpecialSlot(final Slot slot, final int mouseButton, final int modifier) {
		if (!((ISpecialSlot) slot).isAdjustable()) {
			return;
		}
		final ItemStack stackSlot = slot.getStack();
		int stackSize;
		if (modifier == 1) {
			stackSize = mouseButton == 0 ? (stackSlot.stackSize + 1) / 2 : stackSlot.stackSize * 2;
		} else {
			stackSize = mouseButton == 0 ? stackSlot.stackSize - 1 : stackSlot.stackSize + 1;
		}

		if (stackSize > slot.getSlotStackLimit()) {
			stackSize = slot.getSlotStackLimit();
		}

		stackSlot.stackSize = stackSize;

		if (stackSlot.stackSize <= 0) {
			slot.putStack((ItemStack) null);
		}
	}

	protected void fillSpecialSlot(final Slot slot, final ItemStack stackHeld, final int mouseButton, final int modifier) {
		if (!((ISpecialSlot) slot).isAdjustable()) {
			return;
		}
		int stackSize = mouseButton == 0 ? stackHeld.stackSize : 1;
		if (stackSize > slot.getSlotStackLimit()) {
			stackSize = slot.getSlotStackLimit();
		}
		final ItemStack specialStack = stackHeld.copy();
		specialStack.stackSize = stackSize;

		slot.putStack(specialStack);
	}

	protected boolean tryMergeItemStack(final ItemStack stackToShift, final int numSlots) {
		for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++) {
			final Slot slot = (Slot) inventorySlots.get(machineIndex);
			// if (slot instanceof SlotBase && !((SlotBase) slot).canShift()) {
			// continue;
			// }
			if (slot instanceof SlotPhantom) {
				continue;
			}
			if (!slot.isItemValid(stackToShift)) {
				continue;
			}
			if (InvUtils.mergeItemStack(stackToShift, slot.getStack())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slot) {
		ItemStack stack = null;
		final Slot slotObject = (Slot) inventorySlots.get(slot);
		final int numSlots = inventorySlots.size();

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			final ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			final int palyerStart = numSlots - 9 * 4;
			final int hotbarStart = numSlots - 9;
			if (slot >= palyerStart && tryMergeItemStack(stackInSlot, numSlots)) {
				// NOOP
			} else if (slot >= palyerStart && slot < hotbarStart) {
				if (!mergeItemStack(stackInSlot, hotbarStart, numSlots, false)) {
					return null;
				}
			} else if (slot >= hotbarStart && slot < numSlots) {
				if (!mergeItemStack(stackInSlot, palyerStart, hotbarStart, false)) {
					return null;
				}
			} else if (!mergeItemStack(stackInSlot, palyerStart, numSlots, false)) {
				return null;
			}

			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}
}
