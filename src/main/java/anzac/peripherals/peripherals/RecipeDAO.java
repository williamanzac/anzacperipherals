package anzac.peripherals.peripherals;

import static cpw.mods.fml.common.registry.GameRegistry.findUniqueIdentifierFor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import anzac.peripherals.tile.InternalInventoryCrafting;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import dan200.computercraft.api.filesystem.IWritableMount;

public class RecipeDAO {
	@SuppressWarnings("serial")
	private static final Type NAME_TO_OUTPUT_TYPE = new TypeToken<Map<String, Integer>>() {
	}.getType();
	@SuppressWarnings("serial")
	private static final Type RECIPES_BY_OUTPUT_TYPE = new TypeToken<Map<Integer, List<Integer>>>() {
	}.getType();
	@SuppressWarnings("serial")
	private static final Type RECIPES_TYPE = new TypeToken<List<Integer[]>>() {
	}.getType();
	private final ItemStackDAO itemStackDAO;
	private IWritableMount mount;

	private List<Integer[]> recipes = new ArrayList<Integer[]>();
	private Map<Integer, List<Integer>> recipesByOutput = new HashMap<Integer, List<Integer>>();
	private Map<String, Integer> nameToOutput = new HashMap<String, Integer>();

	public RecipeDAO(final IWritableMount mount) {
		super();
		this.itemStackDAO = new ItemStackDAO(mount);
		this.mount = mount;
		readFromMount();
	}

	private void writeToMount() {
		if (mount == null) {
			return;
		}
		try {
			final OutputStream outputStream = mount.openForWrite("data");
			final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream));
			final Gson gson = new Gson();
			String json = gson.toJson(recipes, RECIPES_TYPE);
			out.write(json);
			json = gson.toJson(recipesByOutput, RECIPES_BY_OUTPUT_TYPE);
			out.write(json);
			json = gson.toJson(nameToOutput, NAME_TO_OUTPUT_TYPE);
			out.write(json);
			out.close();
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
			if (!mount.exists("data")) {
				return;
			}
			final InputStream inputStream = mount.openForRead("data");
			final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			final Gson gson = new Gson();
			recipes = gson.fromJson(in, RECIPES_TYPE);
			recipesByOutput = gson.fromJson(in, RECIPES_BY_OUTPUT_TYPE);
			nameToOutput = gson.fromJson(in, NAME_TO_OUTPUT_TYPE);
			in.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int create(final ItemStack output, final InternalInventoryCrafting craftMatrix) {
		final int outputId = itemStackDAO.create(output);
		final Integer[] recipe = new Integer[craftMatrix.getSizeInventory()];
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			final ItemStack itemStack = craftMatrix.getStackInSlot(i);
			if (itemStack != null) {
				final int itemId = itemStackDAO.create(itemStack);
				recipe[i] = itemId;
			}
		}
		if (!recipes.contains(recipe)) {
			recipes.add(recipe);

		}
		final int recipeId = recipes.indexOf(recipe);
		if (!recipesByOutput.containsKey(outputId)) {
			recipesByOutput.put(outputId, new ArrayList<Integer>());
			nameToOutput.put(findUniqueIdentifierFor(output.getItem()).toString(), outputId);
		}
		recipesByOutput.get(outputId).add(recipeId);
		writeToMount();
		return recipeId;
	}

	public Recipe read(final int id) {
		final Integer[] integers = recipes.get(id);
		final Recipe recipe = new Recipe();
		for (int i = 0; i < integers.length; i++) {
			final Integer itemId = integers[i];
			if (itemId != null) {
				recipe.craftMatrix[i] = itemStackDAO.read(itemId);
			}
		}
		return recipe;
	}

	public List<Recipe> read(final ItemStack output) {
		final int outputId = itemStackDAO.create(output);
		return readRecipes(outputId);
	}

	public List<Recipe> read(final String name) {
		final int outputId = nameToOutput.get(name);
		return readRecipes(outputId);
	}

	private List<Recipe> readRecipes(final int outputId) {
		final List<Recipe> recipes = new ArrayList<Recipe>();
		final List<Integer> list = recipesByOutput.get(outputId);
		if (list != null && !list.isEmpty()) {
			for (final Integer integer : list) {
				final Recipe recipe = read(integer);
				recipes.add(recipe);
			}
		}
		return recipes;
	}

	public Set<String> listNames() {
		return nameToOutput.keySet();
	}

	public void setMount(final IWritableMount mount) {
		itemStackDAO.setMount(mount);
		this.mount = mount;
		readFromMount();
	}
}
