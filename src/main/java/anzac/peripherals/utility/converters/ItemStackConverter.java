package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
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
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemStackConverter implements Converter<ItemStack> {

	private static final String TAG = "tag";
	private static final String SIZE = "count";
	private static final String NAME = "name";
	private static final String DAMAGE = "damage";

	@SuppressWarnings("unchecked")
	@Override
	public Object javaToLUA(final Object object) {
		final ItemStack itemStack = (ItemStack) object;
		if (itemStack == null || itemStack.getItem() == null) {
			return null;
		}
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put(SIZE, itemStack.stackSize);
		map.put(DAMAGE, itemStack.getItemDamage());
		map.put(NAME, GameRegistry.findUniqueIdentifierFor(itemStack.getItem()).toString());
		if (itemStack.hasTagCompound()) {
			final Map<String, Object> tagMap = (Map<String, Object>) nbtToLUA(itemStack.getTagCompound());
			map.put(TAG, tagMap);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ItemStack luaToJava(final Object object) {
		final Map<String, Object> map = (Map<String, Object>) object;
		final int count = ((Double) map.get(SIZE)).intValue();
		final String name = (String) map.get(NAME);
		final int damage = ((Double) map.get(DAMAGE)).intValue();
		final String[] parts = name.split(":");
		final ItemStack itemStack = GameRegistry.findItemStack(parts[0], parts[1], count);
		itemStack.setItemDamage(damage);
		if (map.containsKey(TAG)) {
			final NBTTagCompound tag = (NBTTagCompound) luaToNBT(map.get(TAG));
			itemStack.setTagCompound(tag);
		}
		return itemStack;
	}

	@SuppressWarnings("unchecked")
	private Object nbtToLUA(final NBTBase nbt) {
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
			final Map<Integer, Object> objects = new HashMap<Integer, Object>(tagList.tagCount());
			final int tagType = tagList.func_150303_d();
			for (int i = 0; i < tagList.tagCount(); i++) {
				switch (tagType) {
				case 10:
					objects.put(i + 1, nbtToLUA(tagList.getCompoundTagAt(i)));
					break;
				case 11:
					objects.put(i + 1, tagList.func_150306_c(i));
					break;
				case 6:
					objects.put(i + 1, tagList.func_150309_d(i));
					break;
				case 5:
					objects.put(i + 1, tagList.func_150308_e(i));
					break;
				case 8:
					objects.put(i + 1, tagList.getStringTagAt(i));
					break;
				}
			}
			return objects;
		case 10:
			final NBTTagCompound tagCompound = (NBTTagCompound) nbt;
			final Map<String, Object> map = new HashMap<String, Object>();
			final Set<String> keys = tagCompound.func_150296_c();
			for (final String key : keys) {
				map.put(key, nbtToLUA(tagCompound.getTag(key)));
			}
			return map;
		case 11:
			return ((NBTTagIntArray) nbt).func_150302_c();
		}
		return null;
	}

	private NBTBase luaToNBT(final Object object) {
		return null;
	}
}
