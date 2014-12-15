package anzac.peripherals.peripherals;

import static cpw.mods.fml.common.registry.GameRegistry.findUniqueIdentifierFor;
import static java.util.Collections.unmodifiableList;
import static net.minecraft.item.ItemStack.loadItemStackFromNBT;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import dan200.computercraft.api.filesystem.IWritableMount;

public class ItemStackDAO {

	private IWritableMount mount;

	private final List<ItemStack> stacks = new ArrayList<ItemStack>();
	private final Map<String, ItemStack> nameToStack = new HashMap<String, ItemStack>();

	public ItemStackDAO(final IWritableMount mount) {
		this.mount = mount;
		readFromMount();
	}

	private void writeToMount() {
		if (mount == null) {
			return;
		}
		try {
			final OutputStream outputStream = mount.openForWrite("items");
			final NBTTagCompound data = new NBTTagCompound();
			final NBTTagList list = new NBTTagList();
			for (final ItemStack stack : stacks) {
				final NBTTagCompound tag = new NBTTagCompound();
				stack.writeToNBT(tag);
				list.appendTag(tag);
			}
			data.setTag("stacks", list);
			final DataOutputStream out = new DataOutputStream(outputStream);
			try {
				CompressedStreamTools.write(data, out);
			} finally {
				out.close();
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readFromMount() {
		if (mount == null) {
			return;
		}
		try {
			if (!mount.exists("items")) {
				return;
			}
			final InputStream inputStream = mount.openForRead("items");
			final DataInputStream in = new DataInputStream(inputStream);
			try {
				stacks.clear();
				nameToStack.clear();
				final NBTTagCompound data = CompressedStreamTools.read(in);
				final NBTTagList list = data.getTagList("stacks", NBT.TAG_COMPOUND);
				for (int i = 0; i < list.tagCount(); i++) {
					final NBTTagCompound tag = list.getCompoundTagAt(i);
					final ItemStack stack = loadItemStackFromNBT(tag);
					final String name = findUniqueIdentifierFor(stack.getItem()).toString();
					stacks.add(stack);
					nameToStack.put(name, stack);
				}
			} finally {
				in.close();
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int create(final ItemStack itemStack) {
		final String name = findUniqueIdentifierFor(itemStack.getItem()).toString();
		if (!nameToStack.containsKey(name)) {
			stacks.add(itemStack);
			nameToStack.put(name, itemStack);
			writeToMount();
		}
		return stacks.indexOf(itemStack);
	}

	public ItemStack read(final int id) {
		return stacks.get(id);
	}

	public void update() {
		// TODO??
	}

	public void delete(final ItemStack itemStack) {
		stacks.remove(itemStack);
		writeToMount();
	}

	public void delete(final int id) {
		stacks.remove(id);
		writeToMount();
	}

	public List<ItemStack> list() {
		return unmodifiableList(stacks);
	}

	public void setMount(final IWritableMount mount) {
		this.mount = mount;
		readFromMount();
	}
}
