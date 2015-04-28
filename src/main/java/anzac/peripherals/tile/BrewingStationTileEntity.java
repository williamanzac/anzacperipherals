package anzac.peripherals.tile;

import static net.minecraft.item.ItemStack.loadItemStackFromNBT;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.utility.InvUtils;

@Peripheral(type = "brewing")
public class BrewingStationTileEntity extends BaseTileEntity implements ISidedInventory, IFluidHandler {
	private static final String INVENTORY = "inventory";
	private static final String SLOT = "slot";
	private static final String INGREDIENTS = "ingredients";
	private static final String TANK_TAG = "tank";

	private final FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
	private final SimpleInventory emptyBottles = new SimpleInventory(1, "", 64);
	private final SimpleInventory potionInput = new SimpleInventory(3, "", 1);
	private final SimpleInventory input = new SimpleInventory(9, "", 64);
	public final SimpleInventory ingredients = new SimpleInventory(6, "", 1);
	private final SimpleInventory potionOutput = new SimpleInventory(3, "", 1);

	private final InventoryWrapper all = InventoryWrapper.make().add(emptyBottles).add(potionInput).add(input)
			.add(potionOutput);

	@PeripheralMethod
	public boolean setIngredients(final ItemStack[] stacks) throws Exception {
		final int length = stacks.length;
		for (int i = 0; i < ingredients.getSizeInventory(); i++) {
			if (i < length) {
				ingredients.setInventorySlotContents(i, stacks[i]);
			} else {
				ingredients.setInventorySlotContents(i, null);
			}
		}
		return isValidPotion();
	}

	@PeripheralMethod
	public boolean isValidPotion() throws Exception {
		final List<PotionEffect> potionEffects = getPotionEffects();
		return potionEffects != null && !potionEffects.isEmpty();
	}

	@PeripheralMethod
	public Map<Integer, ItemStack> getIngredients() throws Exception {
		return InvUtils.contents(ingredients, ForgeDirection.UNKNOWN);
	}

	@PeripheralMethod
	public String getPotionName() throws Exception {
		final int k = getPotionValue();
		final ItemStack stack = new ItemStack(Items.potionitem, 1, k);
		return Items.potionitem.getItemStackDisplayName(stack);
	}

	@SuppressWarnings("unchecked")
	private int getPotionValue() {
		int k = 0;
		final ItemStack start = potionInput.getStackInSlot(0);
		if (start != null && start.stackSize > 0) {
			k = start.getItemDamage();
		}
		for (int i = 0; i < ingredients.getSizeInventory(); i++) {
			final ItemStack itemStack = ingredients.getStackInSlot(i);
			if (itemStack != null) {
				final int j = k;
				k = PotionHelper.applyIngredient(j, itemStack.getItem().getPotionEffect(itemStack));
				if (!ItemPotion.isSplash(j) && ItemPotion.isSplash(k)) {
					continue;
				}

				final List<PotionEffect> list = Items.potionitem.getEffects(j);
				final List<PotionEffect> list1 = Items.potionitem.getEffects(k);

				if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null) && j != k) {
					continue;
				}
			}
		}
		return k;
	}

	@SuppressWarnings("unchecked")
	@PeripheralMethod
	public List<PotionEffect> getPotionEffects() throws Exception {
		final int k = getPotionValue();
		return Items.potionitem.getEffects(k);
	}

	@PeripheralMethod
	public boolean hasBottles() throws Exception {
		final ItemStack stack = emptyBottles.getStackInSlot(0);
		if (stack == null || stack.stackSize <= 0) {
			throw new Exception("No empty bottles provided");
		}
		return true;
	}

	@PeripheralMethod
	public boolean hasWater() throws Exception {
		final FluidStack fluid = tank.getFluid();
		if (fluid == null || fluid.getFluidID() != FluidRegistry.WATER.getID()
				|| fluid.amount < FluidContainerRegistry.BUCKET_VOLUME * 3) {
			throw new Exception("Not enough water in the tank");
		}
		return true;
	}

	@PeripheralMethod
	public boolean hasIngredients() throws Exception {
		for (int i = 0; i < ingredients.getSizeInventory(); i++) {
			final ItemStack stackInSlot = ingredients.getStackInSlot(i);
			if (stackInSlot == null) {
				continue;
			}
			boolean foundMatch = false;
			for (int j = 0; j < input.getSizeInventory(); j++) {
				final ItemStack invStack = input.getStackInSlot(j);
				if (invStack == null) {
					continue;
				}
				final ItemStack copy = invStack.copy();
				final boolean itemEqual = InvUtils.itemMatched(copy, stackInSlot, true, true, true);
				if (itemEqual) {
					foundMatch = true;
					break;
				}
			}
			if (!foundMatch) {
				throw new Exception("Missing " + stackInSlot.getDisplayName());
			}
		}
		return true;
	}

	@PeripheralMethod
	public boolean canBrewPotion() throws Exception {
		return isValidPotion() && hasIngredients() && canOutput();
	}

	@PeripheralMethod
	public boolean canOutput() throws Exception {
		for (int row = 0; row < 3; row++) {
			final ItemStack outputStack = potionOutput.getStackInSlot(row);
			if (outputStack != null && outputStack.stackSize > 0) {
				// blocked
				throw new Exception("Output blocked");
			}
		}
		return true;
	}

	@PeripheralMethod
	public void brewPotion() throws Exception {
		if (!canBrewPotion()) {
			throw new Exception("The potion is not valid or the output is blocked");
		}
		final int potionValue = getPotionValue();
		final ItemStack potionStack = new ItemStack(Items.potionitem, 1, potionValue);
		boolean brewed = false;
		for (int row = 0; row < 3; row++) {
			if (potionInput.getStackInSlot(row) != null) {
				potionInput.setInventorySlotContents(row, null);
				final ItemStack copy = potionStack.copy();
				potionOutput.setInventorySlotContents(row, copy);
				brewed = false;
			}
		}
		if (brewed) {
			for (int i = 0; i < ingredients.getSizeInventory(); i++) {
				final ItemStack stackInSlot = ingredients.getStackInSlot(i);
				if (stackInSlot == null) {
					continue;
				}
				for (int j = 0; j < input.getSizeInventory(); j++) {
					final ItemStack invStack = input.getStackInSlot(j);
					if (invStack == null) {
						continue;
					}
					final ItemStack copy = invStack.copy();
					final boolean itemEqual = InvUtils.itemMatched(copy, stackInSlot, true, true, true);
					if (itemEqual) {
						input.decrStackSize(j, 1);
						break;
					}
				}
			}
			markDirty();
		}
	}

	@PeripheralMethod
	public void fillBottles() throws Exception {
		if (hasBottles() && hasWater()) {
			for (int row = 0; row < 3; row++) {
				final ItemStack outputStack = potionInput.getStackInSlot(row);
				final ItemStack stackInSlot = emptyBottles.getStackInSlot(0);
				if (outputStack == null || outputStack.stackSize == 0
						&& (stackInSlot != null && stackInSlot.stackSize > 0)) {
					final ItemStack waterBottle = new ItemStack(Items.potionitem);
					potionInput.setInventorySlotContents(row, waterBottle);
					emptyBottles.decrStackSize(0, 1);
					tank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
				}
			}
		}
	}

	public FluidTankInfo getInfo() {
		return tank.getInfo();
	}

	public int getFluidAmount() {
		return tank.getFluidAmount();
	}

	public void setFluidAmount(final int amount) {
		if (tank.getFluid() != null) {
			tank.getFluid().amount = amount;
		}
	}

	public int getFluid() {
		return tank.getFluid() == null ? 0 : tank.getFluid().getFluidID();
	}

	public void setFluid(final int fluidId) {
		final Fluid fluid = FluidRegistry.getFluid(fluidId);
		if (fluid != null) {
			tank.setFluid(new FluidStack(fluid, getFluidAmount()));
		}
	}

	@Override
	public int getSizeInventory() {
		return all.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		return all.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amt) {
		return all.decrStackSize(slot, amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		return all.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		all.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return null;
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
	public boolean isItemValidForSlot(final int slot, final ItemStack stack) {
		if (slot == 0) {
			// empty bottle only
			return stack.getItem().equals(Items.glass_bottle);
		} else if (slot >= 1 && slot <= 3) {
			// water bottles only, other potions?
			return stack.getItem().equals(Items.potionitem);
		} else if (slot >= 4 && slot <= 12) {
			// input
			return stack.getItem().isPotionIngredient(stack);
		}
		return false;
	}

	@Override
	public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(final ForgeDirection from, final Fluid fluid) {
		return fluid.getID() == FluidRegistry.WATER.getID();
	}

	@Override
	public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public int[] getAccessibleSlotsFromSide(final int side) {
		return InvUtils.createSlotArray(all);
	}

	@Override
	public boolean canInsertItem(final int slot, final ItemStack stack, final int side) {
		return isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(final int slot, final ItemStack stack, final int side) {
		return true;
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		// read craft slots
		if (tagCompound.hasKey(INGREDIENTS)) {
			final NBTTagList list = tagCompound.getTagList(INGREDIENTS, Constants.NBT.TAG_COMPOUND);
			for (byte entry = 0; entry < list.tagCount(); entry++) {
				final NBTTagCompound itemTag = list.getCompoundTagAt(entry);
				final int slot = itemTag.getByte(SLOT);
				if (slot >= 0 && slot < ingredients.getSizeInventory()) {
					final ItemStack stack = loadItemStackFromNBT(itemTag);
					ingredients.setInventorySlotContents(slot, stack);
				}
			}
		}

		// read inventory slots
		if (tagCompound.hasKey(INVENTORY)) {
			final NBTTagList list = tagCompound.getTagList(INVENTORY, Constants.NBT.TAG_COMPOUND);
			for (byte entry = 0; entry < list.tagCount(); entry++) {
				final NBTTagCompound itemTag = list.getCompoundTagAt(entry);
				final int slot = itemTag.getByte(SLOT);
				if (slot >= 0 && slot < getSizeInventory()) {
					final ItemStack stack = loadItemStackFromNBT(itemTag);
					setInventorySlotContents(slot, stack);
				}
			}
		}

		if (tagCompound.hasKey(TANK_TAG)) {
			tank.readFromNBT(tagCompound.getCompoundTag(TANK_TAG));
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		// write craft slots
		NBTTagList list = new NBTTagList();
		for (byte slot = 0; slot < ingredients.getSizeInventory(); slot++) {
			final ItemStack stack = ingredients.getStackInSlot(slot);
			if (stack != null) {
				final NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte(SLOT, slot);
				stack.writeToNBT(itemTag);
				list.appendTag(itemTag);
			}
		}
		tagCompound.setTag(INGREDIENTS, list);

		// write craft slots
		list = new NBTTagList();
		for (byte slot = 0; slot < getSizeInventory(); slot++) {
			final ItemStack stack = getStackInSlot(slot);
			if (stack != null) {
				final NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte(SLOT, slot);
				stack.writeToNBT(itemTag);
				list.appendTag(itemTag);
			}
		}
		tagCompound.setTag(INVENTORY, list);

		final NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tagCompound.setTag(TANK_TAG, tankTag);
	}
}
