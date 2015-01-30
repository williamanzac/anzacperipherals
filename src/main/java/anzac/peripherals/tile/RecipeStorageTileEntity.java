package anzac.peripherals.tile;

import static dan200.computercraft.api.ComputerCraftAPI.createSaveDirMount;
import static net.minecraft.item.ItemStack.loadItemStackFromNBT;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.peripherals.PeripheralEvent;
import anzac.peripherals.peripherals.Recipe;
import anzac.peripherals.peripherals.RecipeDAO;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.peripheral.IComputerAccess;

/**
 * This block allows you to store recipes on its internal HDD. The interface is similar to that of a vanilla crafting
 * table. This block is only usable when connected to a Computer. You must have all the items required for the recipe
 * and they are consumed when storing the recipe.The {@link PeripheralEvent#recipe_changed} event is fired when a valid
 * recipe has been defined. To save the recipe you need to call the {@link #storeRecipe()} method from a connected
 * Computer.The {@link #getRecipe(int)} method can be used to load a recipe in to a variable. That variable can then be
 * used to {@link WorkbenchTileEntity#setRecipe(Recipe)} on a connected {@link WorkbenchTileEntity}.
 */
@Peripheral(type = "recipestorage")
public class RecipeStorageTileEntity extends BaseTileEntity {
	private static final String SLOT = "slot";
	private static final String MATRIX = "matrix";

	public InternalInventoryCrafting craftMatrix = new InternalInventoryCrafting(3);
	public IInventory craftResult = new InventoryCraftResult();

	private final RecipeDAO recipeDAO = new RecipeDAO(getMount());

	public void updateCraftingRecipe() {
		final ItemStack matchingRecipe = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj);
		craftResult.setInventorySlotContents(0, matchingRecipe);
	}

	private IWritableMount getMount() {
		return createSaveDirMount(worldObj, "anzac/hdd/recipies", ConfigurationHandler.hddSize);
	}

	@PeripheralMethod
	public List<String> getRecipeNames() throws Exception {
		final List<String> recipes = new ArrayList<String>();
		recipes.addAll(recipeDAO.listNames());
		return recipes;
	}

	@PeripheralMethod
	public Recipe getRecipe(final int id) throws Exception {
		return recipeDAO.read(id);
	}

	@PeripheralMethod
	public List<Recipe> getRecipe(final String name) throws Exception {
		return recipeDAO.read(name);
	}

	@PeripheralMethod
	public boolean removeRecipe(final int id) throws Exception {
		// final Recipe recipe = recipies.remove(id);
		// if (recipe != null) {
		// final String name = recipiesToName.remove(id);
		// recipiesByName.get(name).remove(recipe);
		// if (recipiesByName.get(name).isEmpty()) {
		// recipiesByName.remove(name);
		// }
		// }
		// markDirty();
		return true;
	}

	@PeripheralMethod
	public boolean removeRecipe(final String name) throws Exception {
		// final List<Integer> list = recipiesByName.remove(name);
		// for (final Integer id : list) {
		// recipies.remove(id);
		// recipiesToName.remove(id);
		// }
		// markDirty();
		return true;
	}

	@PeripheralMethod
	public boolean storeRecipe() throws Exception {
		if (!hasValidRecipe()) {
			return false;
		}
		recipeDAO.create(craftResult.getStackInSlot(0), craftMatrix);
		markDirty();
		return true;
	}

	/**
	 * @return
	 */
	@PeripheralMethod
	public boolean hasValidRecipe() {
		return craftResult.getStackInSlot(0) != null;
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		// read craft slots
		if (tagCompound.hasKey(MATRIX)) {
			final NBTTagList list = tagCompound.getTagList(MATRIX, Constants.NBT.TAG_COMPOUND);
			for (byte entry = 0; entry < list.tagCount(); entry++) {
				final NBTTagCompound itemTag = list.getCompoundTagAt(entry);
				final int slot = itemTag.getByte(SLOT);
				if (slot >= 0 && slot < craftMatrix.getSizeInventory()) {
					final ItemStack stack = loadItemStackFromNBT(itemTag);
					craftMatrix.setInventorySlotContents(slot, stack);
				}
			}
			updateCraftingRecipe();
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		// write craft slots
		final NBTTagList list = new NBTTagList();
		for (byte slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			final ItemStack stack = craftMatrix.getStackInSlot(slot);
			if (stack != null) {
				final NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte(SLOT, slot);
				stack.writeToNBT(itemTag);
				list.appendTag(itemTag);
			}
		}
		tagCompound.setTag(MATRIX, list);
	}

	@Override
	public void attach(final IComputerAccess computer) {
		super.attach(computer);
		final IWritableMount mount = getMount();
		computer.mountWritable("/recipies", mount);
		recipeDAO.setMount(mount);
	}

	@Override
	public void detach(final IComputerAccess computer) {
		super.detach(computer);
		computer.unmount("/recipies");
	}
}
