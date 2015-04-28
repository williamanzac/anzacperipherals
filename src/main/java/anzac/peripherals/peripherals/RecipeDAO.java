package anzac.peripherals.peripherals;

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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import anzac.peripherals.peripherals.Recipe.RecipeType;
import anzac.peripherals.tile.InternalInventoryCrafting;
import dan200.computercraft.api.filesystem.IWritableMount;

public class RecipeDAO {
	private static final String TYPE_TAG = "type";
	private static final String OUTPUT_TAG = "output";
	private static final String INPUT_TAG = "input";
	private static final String RECIPES_TAG = "recipes";
	private static final String DATA_TAG = "data";

	private IWritableMount mount;

	private final List<Recipe> recipes = new ArrayList<Recipe>();
	private final Map<ItemStack, List<Recipe>> recipesByOutput = new HashMap<ItemStack, List<Recipe>>();

	public RecipeDAO(final IWritableMount mount) {
		super();
		this.mount = mount;
		readFromMount();
	}

	private void writeToMount() {
		if (mount == null) {
			return;
		}
		try {
			final OutputStream outputStream = mount.openForWrite(DATA_TAG);
			final NBTTagCompound data = new NBTTagCompound();
			final NBTTagList list = new NBTTagList();
			for (final Recipe recipe : recipes) {
				final NBTTagCompound tag = new NBTTagCompound();
				if (recipe.type != null) {
					tag.setInteger(TYPE_TAG, recipe.type.ordinal());
				}
				final NBTTagList inputList = new NBTTagList();
				final NBTTagList outputList = new NBTTagList();
				for (final ItemStack stack : recipe.craftMatrix) {
					if (stack != null) {
						final NBTTagCompound stackTag = new NBTTagCompound();
						stack.writeToNBT(stackTag);
						inputList.appendTag(stackTag);
					}
				}
				for (final ItemStack stack : recipe.craftResult) {
					if (stack != null) {
						final NBTTagCompound stackTag = new NBTTagCompound();
						stack.writeToNBT(stackTag);
						outputList.appendTag(stackTag);
					}
				}
				tag.setTag(INPUT_TAG, inputList);
				tag.setTag(OUTPUT_TAG, outputList);
				list.appendTag(tag);
			}
			data.setTag(RECIPES_TAG, list);
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
			if (!mount.exists(DATA_TAG)) {
				return;
			}
			final InputStream inputStream = mount.openForRead(DATA_TAG);
			final DataInputStream in = new DataInputStream(inputStream);
			try {
				recipes.clear();
				recipesByOutput.clear();
				final NBTTagCompound data = CompressedStreamTools.read(in);
				final NBTTagList list = data.getTagList(RECIPES_TAG, NBT.TAG_COMPOUND);
				for (int i = 0; i < list.tagCount(); i++) {
					final NBTTagCompound tag = list.getCompoundTagAt(i);
					final Recipe recipe = new Recipe();
					if (tag.hasKey(TYPE_TAG)) {
						final int type = tag.getInteger(TYPE_TAG);
						recipe.type = RecipeType.values()[type];
					}
					final NBTTagList inputList = data.getTagList(INPUT_TAG, NBT.TAG_COMPOUND);
					for (int j = 0; j < inputList.tagCount(); j++) {
						final NBTTagCompound stackTag = list.getCompoundTagAt(j);
						final ItemStack stack = loadItemStackFromNBT(stackTag);
						recipe.craftMatrix[j] = stack;
					}
					final NBTTagList outputList = data.getTagList(OUTPUT_TAG, NBT.TAG_COMPOUND);
					for (int j = 0; j < outputList.tagCount(); j++) {
						final NBTTagCompound stackTag = list.getCompoundTagAt(j);
						final ItemStack stack = loadItemStackFromNBT(stackTag);
						recipe.craftResult[j] = stack;
					}
					recipes.add(recipe);
				}
			} finally {
				in.close();
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int create(final IInventory craftResult, final InternalInventoryCrafting craftMatrix) {
		final Recipe recipe = new Recipe();
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			final ItemStack itemStack = craftMatrix.getStackInSlot(i);
			if (itemStack != null) {
				recipe.craftMatrix[i] = itemStack;
			}
		}
		for (int i = 0; i < craftResult.getSizeInventory(); i++) {
			final ItemStack itemStack = craftResult.getStackInSlot(i);
			if (itemStack != null) {
				recipe.craftResult[i] = itemStack;
			}
		}

		if (!recipes.contains(recipe)) {
			recipes.add(recipe);
		}
		for (final ItemStack output : recipe.craftResult) {
			if (!recipesByOutput.containsKey(output)) {
				recipesByOutput.put(output, new ArrayList<Recipe>());
				// nameToOutput.put(findUniqueIdentifierFor(output.getItem()).toString(), outputId);
			}
			recipesByOutput.get(output).add(recipe);
		}
		writeToMount();
		return recipes.indexOf(recipe);
	}

	public Recipe read(final int id) {
		return recipes.get(id);
	}

	public List<Recipe> read(final ItemStack output) {
		return recipesByOutput.get(output);
	}

	public void setMount(final IWritableMount mount) {
		this.mount = mount;
		readFromMount();
	}

	public void remove(final int id) {
		final Recipe recipe = recipes.remove(id);
		for (final ItemStack output : recipe.craftResult) {
			if (recipesByOutput.containsKey(output)) {
				recipesByOutput.get(output).remove(recipe);
			}
		}
		writeToMount();
	}
}
