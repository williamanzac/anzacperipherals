package anzac.peripherals.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.Peripherals;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.utility.InvUtils;
import anzac.peripherals.utility.LogHelper;
import dan200.computercraft.api.peripheral.IComputerAccess;

@Peripheral(type = "itemstorage")
public class ItemStorageTileEntity extends BaseTileEntity implements IInventory {
	private static final String ITEM_TAG_TAG = "tag";
	private static final String ITEM_DAMAGE_TAG = "Damage";
	private static final String ITEM_COUNT_TAG = "Count";
	private static final String ITEM_ID_TAG = "id";
	private static final String TOTAL_COUNT_TAG = "count";
	private static final String INVENTORY_TAG = "inventory";

	private final List<ItemStack> internalInv = new ArrayList<ItemStack>();
	private int totalCount = 0;

	public ItemStorageTileEntity() {
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@PeripheralMethod
	public List<ItemStack> contents() throws Exception {
		LogHelper.info("inventory: " + internalInv);
		return internalInv;
	}

	@PeripheralMethod
	public int routeTo(final int slot, final ForgeDirection toDir, final int amount) throws Exception {
		return routeTo(slot, toDir, toDir.getOpposite(), amount);
	}

	@PeripheralMethod
	public int routeTo(final int slot, final ForgeDirection toDir, final ForgeDirection insertDir, final int amount)
			throws Exception {
		final ItemStack stackInSlot = internalInv.get(slot);
		if (stackInSlot != null) {
			final ItemStack copy = stackInSlot.copy();
			copy.stackSize = amount;
			copy.stackSize -= InvUtils.routeTo(worldObj, xCoord, yCoord, zCoord, toDir, insertDir, copy);
			final int toDec = amount - copy.stackSize;
			if (toDec > 0) {
				stackInSlot.splitStack(toDec);
				final int maxStackSize = stackInSlot.getMaxStackSize();
				final int stackSize = (int) ((float) toDec / (float) maxStackSize * 64);
				totalCount -= stackSize;
			}
			return toDec;
		}
		return 0;
	}

	/**
	 * Transfer {@code amount} number of items from the internal cache to another connected peripheral with
	 * {@code label} label. The peripheral must be connected to the same computer.
	 *
	 * @param label
	 *            the label of the peripheral.
	 * @param amount
	 *            the number of items to transfer.
	 * @return the actual number of items transferred.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int sendTo(final int slot, final String label, final int amount) throws Exception {
		for (final IComputerAccess computer : computers) {
			final BaseTileEntity entity = Peripherals.peripheralMappings.get(computer, label);
			if (entity != null && (entity instanceof IInventory)) {
				final ItemStack stackInSlot = internalInv.get(slot);
				if (stackInSlot != null) {
					final ItemStack copy = stackInSlot.copy();
					copy.stackSize = amount;
					final int amount1 = copy.stackSize;
					copy.stackSize -= InvUtils.addItem(entity, copy, ForgeDirection.UNKNOWN);
					final int toDec = amount1 - copy.stackSize;
					if (toDec > 0) {
						stackInSlot.splitStack(toDec);
						final int maxStackSize = stackInSlot.getMaxStackSize();
						final int stackSize = (int) ((float) toDec / (float) maxStackSize * 64);
						totalCount -= stackSize;
					}
					return amount - copy.stackSize;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amt) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		final int maxStackSize = stack.getMaxStackSize();
		int stackSize = weightedSize(stack);
		final int totalSize = totalCount + stackSize;
		final int maxSize = getMaxSize();
		if (totalSize > maxSize) {
			final int requiredSize = totalSize - maxSize;
			final int splitSize = (int) ((float) requiredSize / 64 * maxStackSize);
			stack.splitStack(splitSize);
		}
		boolean matched = false;
		for (final ItemStack invStack : internalInv) {
			if (InvUtils.itemMatched(invStack, stack, true, true, false)) {
				invStack.stackSize += stack.stackSize;
				matched = true;
				break;
			}
		}
		if (!matched) {
			internalInv.add(stack);
		}
		stackSize = weightedSize(stack);
		totalCount += stackSize;
		// TODO fire event add stack
		markDirty();
	}

	private int getMaxSize() {
		return ConfigurationHandler.hddSize / 64;
	}

	private int weightedSize(final ItemStack stack) {
		final int maxStackSize = stack.getMaxStackSize();
		return (int) ((float) stack.stackSize / (float) maxStackSize * 64);
	}

	@Override
	public String getInventoryName() {
		return "Item Storage";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer player) {
		return isConnected() && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		final int maxSize = getMaxSize();
		return totalCount < maxSize;
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		if (tagCompound.hasKey(INVENTORY_TAG)) {
			internalInv.clear();
			final NBTTagList tagList = tagCompound.getTagList(INVENTORY_TAG, NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				final NBTTagCompound tagAt = tagList.getCompoundTagAt(i);
				final int id = tagAt.getShort(ITEM_ID_TAG);
				final Item item = Item.getItemById(id);
				// LogHelper.info("read id:" + id + ", item: " + item);
				final int stackSize = tagAt.getInteger(ITEM_COUNT_TAG);
				int itemDamage = tagAt.getShort(ITEM_DAMAGE_TAG);

				if (itemDamage < 0) {
					itemDamage = 0;
				}
				if (item != null) {
					final ItemStack stack = new ItemStack(item, stackSize, itemDamage);
					// LogHelper.info("read item:" + stack);

					if (tagAt.hasKey(ITEM_TAG_TAG, NBT.TAG_COMPOUND)) {
						stack.setTagCompound(tagAt.getCompoundTag(ITEM_TAG_TAG));
					}
					internalInv.add(stack);
					// } else {
					// LogHelper.info("read item: null");
				}
			}
		}

		totalCount = tagCompound.getInteger(TOTAL_COUNT_TAG);
	}

	@Override
	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		final NBTTagList tagList = new NBTTagList();
		for (final ItemStack stack : internalInv) {
			if (stack == null || stack.getItem() == null) {
				LogHelper.info("write item: null");
				continue;
			}
			// LogHelper.info("write item: " + stack);
			final NBTTagCompound stackTag = new NBTTagCompound();
			final int idFromItem = Item.getIdFromItem(stack.getItem());
			// final Item item = Item.getItemById(idFromItem);
			// LogHelper.info("item id: " + idFromItem + "short id: " + ((short) idFromItem) + ", item: " + item);
			stackTag.setShort(ITEM_ID_TAG, (short) idFromItem);
			stackTag.setInteger(ITEM_COUNT_TAG, stack.stackSize);
			stackTag.setShort(ITEM_DAMAGE_TAG, (short) stack.getItemDamage());

			if (stack.stackTagCompound != null) {
				stackTag.setTag(ITEM_TAG_TAG, stack.stackTagCompound);
			}

			// stack.writeToNBT(stackTag);
			tagList.appendTag(stackTag);
		}
		tagCompound.setTag(INVENTORY_TAG, tagList);

		tagCompound.setInteger(TOTAL_COUNT_TAG, totalCount);
	}
}
