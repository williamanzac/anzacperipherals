package anzac.peripherals.tile;

import static anzac.peripherals.utility.InvUtils.createSlotArray;
import static anzac.peripherals.utility.XpUtils.experienceToLiquid;
import static anzac.peripherals.utility.XpUtils.getExperienceForLevel;
import static net.minecraft.item.ItemStack.loadItemStackFromNBT;
import static net.minecraftforge.fluids.FluidRegistry.getFluidID;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
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

import org.apache.commons.lang3.StringUtils;

import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.utility.InvUtils;

@Peripheral(type = "anvil")
public class AnvilTileEntity extends BaseTileEntity implements ISidedInventory, IFluidHandler {
	private static final String XPJUICE = "xpjuice";
	private static final String INVENTORY = "inventory";
	private static final String SLOT = "slot";
	private static final String TANK_TAG = "tank";
	private static final String NAME_TAG = "repairName";

	private final FluidTank tank = new FluidTank(experienceToLiquid(getExperienceForLevel(40)));
	private final SimpleInventory input = new SimpleInventory(2, "", 1);
	private final SimpleInventory output = new SimpleInventory(1, "", 1);
	private final InventoryCraftResult result = new InventoryCraftResult();

	private int enchantmentLevel = 0;
	private int stackSizeToBeUsedInRepair;
	private String repairedItemName;

	private final InventoryWrapper all = InventoryWrapper.make().add(input).add(output);

	public AnvilTileEntity() {
		input.addListener(this);
	}

	@PeripheralMethod
	public int getLevel() {
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
	public boolean hasItemToRepair() {
		final ItemStack stack1 = input.getStackInSlot(0);
		final ItemStack stack2 = input.getStackInSlot(1);
		return stack1 != null && stack1.stackSize > 0 && stack2 != null && stack2.stackSize > 0;
	}

	@PeripheralMethod
	public boolean canRepairItem() {
		return hasItemToRepair() && getResult() != null && hasXpForLevel() && output.getStackInSlot(0) == null;
	}

	@PeripheralMethod
	public ItemStack getResult() {
		return result.getStackInSlot(0);
	}

	@PeripheralMethod
	public void setItemName(final String name) {
		repairedItemName = name;

		final ItemStack itemstack = result.getStackInSlot(0);
		if (itemstack != null) {
			if (StringUtils.isBlank(name)) {
				itemstack.func_135074_t();
			} else {
				itemstack.setStackDisplayName(repairedItemName);
			}
		}

		updateRepairOutput();
	}

	@PeripheralMethod
	public String getItemName() {
		return repairedItemName;
	}

	@PeripheralMethod
	public boolean repairItem() throws Exception {
		if (canRepairItem()) {
			tank.drain(getXpForLevel(), true);
			final ItemStack copy = result.getStackInSlot(0).copy();

			input.setInventorySlotContents(0, null);

			if (stackSizeToBeUsedInRepair > 0) {
				final ItemStack itemstack1 = input.getStackInSlot(1);

				if (itemstack1 != null && itemstack1.stackSize > stackSizeToBeUsedInRepair) {
					itemstack1.stackSize -= stackSizeToBeUsedInRepair;
					input.setInventorySlotContents(1, itemstack1);
				} else {
					input.setInventorySlotContents(1, null);
				}
			} else {
				input.setInventorySlotContents(1, null);
			}
			output.setInventorySlotContents(0, copy);
			result.setInventorySlotContents(0, null);

			enchantmentLevel = 0;

			worldObj.playAuxSFX(1021, xCoord, yCoord, zCoord, 0);

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
		if (slot < 2) {
			return true;
		}
		return false;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		updateRepairOutput();
	}

	@SuppressWarnings("unchecked")
	public void updateRepairOutput() {
		final ItemStack itemstack = input.getStackInSlot(0);
		enchantmentLevel = 0;
		int i = 0;
		final byte b0 = 0;
		int j = 0;

		if (itemstack == null) {
			result.setInventorySlotContents(0, (ItemStack) null);
			enchantmentLevel = 0;
		} else {
			ItemStack itemstack1 = itemstack.copy();
			final ItemStack itemstack2 = input.getStackInSlot(1);
			final Map<Integer, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
			boolean flag = false;
			int k2 = b0 + itemstack.getRepairCost() + (itemstack2 == null ? 0 : itemstack2.getRepairCost());
			stackSizeToBeUsedInRepair = 0;
			int k;
			int l;
			int i1;
			int k1;
			int l1;
			Iterator<Integer> iterator1;
			Enchantment enchantment;

			if (itemstack2 != null) {
				flag = itemstack2.getItem() == Items.enchanted_book
						&& Items.enchanted_book.func_92110_g(itemstack2).tagCount() > 0;

				if (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
					k = Math.min(itemstack1.getItemDamageForDisplay(), itemstack1.getMaxDamage() / 4);

					if (k <= 0) {
						result.setInventorySlotContents(0, (ItemStack) null);
						enchantmentLevel = 0;
						return;
					}

					for (l = 0; k > 0 && l < itemstack2.stackSize; ++l) {
						i1 = itemstack1.getItemDamageForDisplay() - k;
						itemstack1.setItemDamage(i1);
						i += Math.max(1, k / 100) + map.size();
						k = Math.min(itemstack1.getItemDamageForDisplay(), itemstack1.getMaxDamage() / 4);
					}

					stackSizeToBeUsedInRepair = l;
				} else {
					if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable())) {
						result.setInventorySlotContents(0, (ItemStack) null);
						enchantmentLevel = 0;
						return;
					}

					if (itemstack1.isItemStackDamageable() && !flag) {
						k = itemstack.getMaxDamage() - itemstack.getItemDamageForDisplay();
						l = itemstack2.getMaxDamage() - itemstack2.getItemDamageForDisplay();
						i1 = l + itemstack1.getMaxDamage() * 12 / 100;
						final int j1 = k + i1;
						k1 = itemstack1.getMaxDamage() - j1;

						if (k1 < 0) {
							k1 = 0;
						}

						if (k1 < itemstack1.getItemDamage()) {
							itemstack1.setItemDamage(k1);
							i += Math.max(1, i1 / 100);
						}
					}

					final Map<Integer, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
					iterator1 = map1.keySet().iterator();

					while (iterator1.hasNext()) {
						i1 = iterator1.next().intValue();
						enchantment = Enchantment.enchantmentsList[i1];
						k1 = map.containsKey(Integer.valueOf(i1)) ? map.get(Integer.valueOf(i1)).intValue() : 0;
						l1 = map1.get(Integer.valueOf(i1)).intValue();
						int i3;

						if (k1 == l1) {
							++l1;
							i3 = l1;
						} else {
							i3 = Math.max(l1, k1);
						}

						l1 = i3;
						final int i2 = l1 - k1;
						boolean flag1 = enchantment.canApply(itemstack);

						if (itemstack.getItem() == Items.enchanted_book) {
							flag1 = true;
						}

						final Iterator<Integer> iterator = map.keySet().iterator();

						while (iterator.hasNext()) {
							final int j2 = iterator.next().intValue();

							final Enchantment e2 = Enchantment.enchantmentsList[j2];
							if (j2 != i1 && !(enchantment.canApplyTogether(e2) && e2.canApplyTogether(enchantment))) {
								flag1 = false;
								i += i2;
							}
						}

						if (flag1) {
							if (l1 > enchantment.getMaxLevel()) {
								l1 = enchantment.getMaxLevel();
							}

							map.put(i1, l1);
							int l2 = 0;

							switch (enchantment.getWeight()) {
							case 1:
								l2 = 8;
								break;
							case 2:
								l2 = 4;
							case 3:
							case 4:
							case 6:
							case 7:
							case 8:
							case 9:
							default:
								break;
							case 5:
								l2 = 2;
								break;
							case 10:
								l2 = 1;
							}

							if (flag) {
								l2 = Math.max(1, l2 / 2);
							}

							i += l2 * i2;
						}
					}
				}
			}

			if (StringUtils.isBlank(repairedItemName)) {
				if (itemstack.hasDisplayName()) {
					j = itemstack.isItemStackDamageable() ? 7 : itemstack.stackSize * 5;
					i += j;
					itemstack1.func_135074_t();
				}
			} else if (!repairedItemName.equals(itemstack.getDisplayName())) {
				j = itemstack.isItemStackDamageable() ? 7 : itemstack.stackSize * 5;
				i += j;

				if (itemstack.hasDisplayName()) {
					k2 += j / 2;
				}

				itemstack1.setStackDisplayName(repairedItemName);
			}

			k = 0;

			for (iterator1 = map.keySet().iterator(); iterator1.hasNext(); k2 += k + k1 * l1) {
				i1 = iterator1.next().intValue();
				enchantment = Enchantment.enchantmentsList[i1];
				k1 = map.get(Integer.valueOf(i1)).intValue();
				l1 = 0;
				++k;

				switch (enchantment.getWeight()) {
				case 1:
					l1 = 8;
					break;
				case 2:
					l1 = 4;
				case 3:
				case 4:
				case 6:
				case 7:
				case 8:
				case 9:
				default:
					break;
				case 5:
					l1 = 2;
					break;
				case 10:
					l1 = 1;
				}

				if (flag) {
					l1 = Math.max(1, l1 / 2);
				}
			}

			if (flag) {
				k2 = Math.max(1, k2 / 2);
			}

			if (flag && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2)) {
				itemstack1 = null;
			}

			enchantmentLevel = k2 + i;

			if (i <= 0) {
				itemstack1 = null;
			}

			if (j == i && j > 0 && enchantmentLevel >= 40) {
				enchantmentLevel = 39;
			}

			if (itemstack1 != null) {
				l = itemstack1.getRepairCost();

				if (itemstack2 != null && l < itemstack2.getRepairCost()) {
					l = itemstack2.getRepairCost();
				}

				if (itemstack1.hasDisplayName()) {
					l -= 9;
				}

				if (l < 0) {
					l = 0;
				}

				l += 2;
				itemstack1.setRepairCost(l);
				EnchantmentHelper.setEnchantments(map, itemstack1);
			}

			result.setInventorySlotContents(0, itemstack1);
		}
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
			updateRepairOutput();
		}

		if (tagCompound.hasKey(TANK_TAG)) {
			tank.readFromNBT(tagCompound.getCompoundTag(TANK_TAG));
		}

		if (tagCompound.hasKey(NAME_TAG)) {
			repairedItemName = tagCompound.getString(NAME_TAG);
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		// write craft slots
		final NBTTagList list = new NBTTagList();
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

		if (StringUtils.isNotBlank(repairedItemName)) {
			tagCompound.setString(NAME_TAG, repairedItemName);
		}
	}
}
