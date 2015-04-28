package anzac.peripherals.tile;

import static anzac.peripherals.utility.InvUtils.consumeItem;
import static anzac.peripherals.utility.InvUtils.createSlotArray;
import static anzac.peripherals.utility.XpUtils.experienceToLiquid;
import static anzac.peripherals.utility.XpUtils.getExperienceForLevel;
import static net.minecraft.enchantment.EnchantmentHelper.buildEnchantmentList;
import static net.minecraft.item.ItemStack.loadItemStackFromNBT;
import static net.minecraftforge.fluids.FluidRegistry.getFluidID;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.utility.InvUtils;

@Peripheral(type = "enchanter")
public class EnchanterTileEntity extends BaseTileEntity implements ISidedInventory, IFluidHandler {
	private static final String XPJUICE = "xpjuice";
	private static final String INVENTORY = "inventory";
	private static final String SLOT = "slot";
	private static final String TANK_TAG = "tank";

	private final FluidTank tank = new FluidTank(experienceToLiquid(getExperienceForLevel(40)));
	private final SimpleInventory input = new SimpleInventory(1, "", 1);
	private final SimpleInventory output = new SimpleInventory(1, "", 1);

	private int enchantmentLevel = 0;
	private final Random rand = new Random();

	private final InventoryWrapper all = InventoryWrapper.make().add(input).add(output);

	@PeripheralMethod
	public void setEnchantmentLevel(final int level) throws Exception {
		if (level < 0 || level > 30) {
			throw new Exception("level must be between 0 and 30");
		}
		enchantmentLevel = level;
	}

	@PeripheralMethod
	public int getEnchantmentLevel() {
		return enchantmentLevel;
	}

	@PeripheralMethod
	public boolean hasXpForLevel() {
		return getXpForLevel() <= tank.getFluidAmount();
	}

	@PeripheralMethod
	public int getXpForLevel() {
		return experienceToLiquid(getExperienceForLevel(enchantmentLevel));
	}

	@PeripheralMethod
	public Map<Integer, ItemStack> contents() throws Exception {
		return InvUtils.contents(all, ForgeDirection.UNKNOWN);
	}

	@PeripheralMethod
	public boolean hasItemToEnchant() {
		final ItemStack stack = input.getStackInSlot(0);
		return stack != null && stack.stackSize > 0;
	}

	@PeripheralMethod
	public boolean canEnchantItem() {
		return hasItemToEnchant() && hasXpForLevel() && output.getStackInSlot(0) == null;
	}

	@SuppressWarnings("unchecked")
	@PeripheralMethod
	public boolean enchantItem() throws Exception {
		if (canEnchantItem()) {
			final ItemStack itemstack = input.getStackInSlot(0);
			final ItemStack copy = itemstack.copy();
			final List<EnchantmentData> list = buildEnchantmentList(rand, copy, enchantmentLevel);
			final boolean flag = copy.getItem() == Items.book;

			if (list != null) {
				tank.drain(getXpForLevel(), true);
				setInventorySlotContents(0, consumeItem(itemstack));

				if (flag) {
					copy.func_150996_a(Items.enchanted_book);
				}

				final int j = flag && list.size() > 1 ? rand.nextInt(list.size()) : -1;

				for (int k = 0; k < list.size(); ++k) {
					final EnchantmentData enchantmentdata = list.get(k);

					if (!flag || k != j) {
						if (flag) {
							Items.enchanted_book.addEnchantment(copy, enchantmentdata);
						} else {
							copy.addEnchantment(enchantmentdata.enchantmentobj, enchantmentdata.enchantmentLevel);
						}
					}
				}

				output.setInventorySlotContents(0, copy);
			}

			return true;
		} else {
			return false;
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
		return 1;
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
			// enchantable?
			return stack.isItemEnchantable();
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
		return fluid.getID() == getFluidID(XPJUICE);
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
		return createSlotArray(all);
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
