package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackConverter implements Converter<FluidStack> {

	private static final String NAME = "name";
	private static final String AMOUNT = "amount";
	private static final String TAG = "tag";

	@SuppressWarnings("unchecked")
	@Override
	public Object javaToLUA(final Object object) {
		final FluidStack fluidStack = (FluidStack) object;
		final Map<String, Object> map = new HashMap<String, Object>();
		if (fluidStack != null) {
			map.put(AMOUNT, fluidStack.amount);
			map.put(NAME, fluidStack.getFluid().getName());
			if (fluidStack.tag != null) {
				final Map<String, Object> tagMap = (Map<String, Object>) nbtToJava(fluidStack.tag);
				map.put(TAG, tagMap);
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FluidStack luaToJava(final Object object) {
		final Map<String, Object> map = (Map<String, Object>) object;
		final int amount = ((Double) map.get(AMOUNT)).intValue();
		final String name = (String) map.get(NAME);
		final FluidStack fluidStack = FluidRegistry.getFluidStack(name, amount);
		if (map.containsKey(TAG)) {
			final NBTTagCompound tag = (NBTTagCompound) javaToNBT(map.get(TAG));
			fluidStack.tag = tag;
		}
		return fluidStack;
	}

	@SuppressWarnings("unchecked")
	private Object nbtToJava(final NBTBase nbt) {
		switch (nbt.getId()) {
		case 1:
			return ((NBTTagByte) nbt).func_150290_f();
		case 2:
			return ((NBTTagShort) nbt).func_150289_e();
		case 3:
			return ((NBTTagInt) nbt).func_150287_d();
		case 4:
			return ((NBTTagLong) nbt).func_150291_c();
		case 5:
			return ((NBTTagFloat) nbt).func_150288_h();
		case 6:
			return ((NBTTagDouble) nbt).func_150286_g();
		case 7:
			return ((NBTTagByteArray) nbt).func_150292_c();
		case 8:
			return ((NBTTagString) nbt).func_150285_a_();
		case 9:
			final NBTTagList tagList = (NBTTagList) nbt;
			final Object[] objects = new Object[tagList.tagCount()];
			final int tagType = tagList.func_150303_d();
			for (int i = 0; i < tagList.tagCount(); i++) {
				switch (tagType) {
				case 10:
					objects[i] = nbtToJava(tagList.getCompoundTagAt(i));
					break;
				case 11:
					objects[i] = tagList.func_150306_c(i);
					break;
				case 6:
					objects[i] = tagList.func_150309_d(i);
					break;
				case 5:
					objects[i] = tagList.func_150308_e(i);
					break;
				case 8:
					objects[i] = tagList.getStringTagAt(i);
					break;
				}
			}
			return objects;
		case 10:
			final NBTTagCompound tagCompound = (NBTTagCompound) nbt;
			final Map<String, Object> map = new HashMap<String, Object>();
			final Set<String> keys = tagCompound.func_150296_c();
			for (final String key : keys) {
				map.put(key, nbtToJava(tagCompound.getTag(key)));
			}
			return map;
		case 11:
			return ((NBTTagIntArray) nbt).func_150302_c();
		}
		return null;
	}

	private NBTBase javaToNBT(final Object object) {
		return null;
	}
}
