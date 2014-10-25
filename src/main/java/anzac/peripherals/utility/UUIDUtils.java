package anzac.peripherals.utility;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class UUIDUtils {

	public static int getUUID(final ItemStack stack) {
		if (stack == null) {
			return -1;
		}
		return (stack.getItemDamage() << 15) + Item.getIdFromItem(stack.getItem());
	}

	public static ItemStack getItemStack(final int uuid) {
		return getItemStack(uuid, 1);
	}

	public static ItemStack getItemStack(final int uuid, final int stackSize) {
		if (uuid == -1) {
			return null;
		}
		final int meta = getMeta(uuid);
		final int id = getId(uuid);
		return new ItemStack(Item.getItemById(id), stackSize, meta);
	}

	public static int getId(final int uuid) {
		return uuid & 32767;
	}

	public static int getMeta(final int uuid) {
		return uuid >> 15;
	}

	public static FluidStack getFluidStack(final int uuid, final int amount) {
		if (uuid == -1) {
			return null;
		}
		final Block block = Block.getBlockById(uuid);
		final Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
		return new FluidStack(fluid, amount);
	}

	public static int getUUID(final FluidStack stack) {
		if (stack == null) {
			return -1;
		}
		return getUUID(stack.getFluid());
	}

	public static int getUUID(final Fluid fluid) {
		if (fluid == null) {
			return -1;
		}
		return Block.getIdFromBlock(fluid.getBlock());
	}
}
