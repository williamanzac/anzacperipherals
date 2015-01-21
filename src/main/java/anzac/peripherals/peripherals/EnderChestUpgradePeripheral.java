package anzac.peripherals.peripherals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.utility.InvUtils;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.storage.item.EnderItemStorage;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;

@Peripheral(type = "enderchest")
public class EnderChestUpgradePeripheral extends BasePeripheral {

	public static String[] Names = { "White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray",
			"LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black", };

	private final ITurtleAccess turtle;
	private final TurtleSide side;
	private final NBTTagCompound nbt;

	public EnderChestUpgradePeripheral(final ITurtleAccess paramITurtleAccess, final TurtleSide side) {
		turtle = paramITurtleAccess;
		this.side = side;
		nbt = turtle.getUpgradeNBTData(side);
	}

	@PeripheralMethod
	public int getFreq() {
		return nbt.getInteger("freq");
	}

	@PeripheralMethod
	public List<Integer> getColours() {
		final int[] colours = EnderStorageManager.getColoursFromFreq(getFreq());
		final List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < colours.length; i++) {
			list.add(colourToCCColour(colours[i]));
		}
		return list;
	}

	@PeripheralMethod
	public String[] getColourNames() {
		final int[] colours = EnderStorageManager.getColoursFromFreq(getFreq());
		final String[] names = new String[colours.length];
		for (int i = 0; i < colours.length; i++) {
			names[i] = colourToName(colours[i]);
		}
		return names;
	}

	@PeripheralMethod
	public void setFreq(final int freq) {
		nbt.setInteger("freq", freq);
		turtle.updateUpgradeNBTData(side);
	}

	@PeripheralMethod
	public void setFreq(final Object a, final Object b, final Object c) {
		setFreq(EnderStorageManager.getFreqFromColours(convertColour(a), convertColour(b), convertColour(c)));
	}

	private int convertColour(final Object a) {
		return a instanceof String ? nameToColour((String) a) : ccColourToColour((Integer) a);
	}

	/**
	 * Will return a table containing the uuid and count of each item in the internal cache.
	 * 
	 * @return A table of the internal contents.
	 * @throws Exception
	 */
	@PeripheralMethod
	public Map<Integer, ItemStack> contents() throws Exception {
		return InvUtils.contents(getEnderItemStorage(), ForgeDirection.UNKNOWN);
	}

	@PeripheralMethod
	public int transferTo(final int amount) {
		final int selectedSlot = turtle.getSelectedSlot();
		final ItemStack selectedStack = turtle.getInventory().getStackInSlot(selectedSlot);
		if (selectedStack != null) {
			final ItemStack copy = selectedStack.copy();
			copy.stackSize = amount;
			final int amount1 = copy.stackSize;
			copy.stackSize -= InvUtils.addItem(getEnderItemStorage(), copy, ForgeDirection.UNKNOWN);
			final int toDec = amount1 - copy.stackSize;
			if (toDec > 0) {
				turtle.getInventory().decrStackSize(selectedSlot, toDec);
			}
			return amount - copy.stackSize;
		}
		return 0;
	}

	@PeripheralMethod
	public int transferFrom(final int slot, final int amount) throws Exception {
		final ItemStack stackInSlot = getEnderItemStorage().getStackInSlot(slot);
		if (stackInSlot != null) {
			final ItemStack copy = stackInSlot.copy();
			copy.stackSize = amount;
			final int amount1 = copy.stackSize;
			copy.stackSize -= InvUtils.addItem(turtle.getInventory(), copy, ForgeDirection.UNKNOWN);
			final int toDec = amount1 - copy.stackSize;
			if (toDec > 0) {
				getEnderItemStorage().decrStackSize(slot, toDec);
			}
			return amount - copy.stackSize;
		}
		return 0;
	}

	private EnderItemStorage getEnderItemStorage() {
		final boolean isRemote = turtle.getWorld().isRemote;
		final int freq = getFreq();
		return (EnderItemStorage) EnderStorageManager.instance(isRemote).getStorage(null, freq, "item");
	}

	private String colourToName(final int colour) {
		return Names[colour];
	}

	private int nameToColour(final String name) {
		for (int i = 0; i < 16; i++) {
			if (Names[i].equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	private int ccColourToColour(final int ccColour) {
		for (int i = 0; i < 16; i++) {
			final int mask = 1 << i;
			if ((ccColour & mask) == mask) {
				return i;
			}
		}
		return -1;
	}

	private int colourToCCColour(final int colour) {
		return 1 << colour;
	}
}
