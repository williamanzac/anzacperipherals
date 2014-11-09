package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import anzac.peripherals.peripherals.CraftingRecipe;
import anzac.peripherals.peripherals.Recipe;

public class RecipeConverter implements Converter<Recipe> {

	private static final String OUTPUT = "output";
	private static final String INPUT = "input";
	private final ItemStackConverter converter = new ItemStackConverter();

	@SuppressWarnings("unchecked")
	@Override
	public Object javaToLUA(Object object) {
		final Recipe recipe = (Recipe) object;
		final Map<Integer, Map<String, Integer>> inputTable = new HashMap<Integer, Map<String, Integer>>();
		for (int i = 0; i < recipe.craftMatrix.length; i++) {
			if (recipe.craftMatrix[i] != null) {
				inputTable.put(i, (Map<String, Integer>) converter.javaToLUA(recipe.craftMatrix[i]));
			}
		}
		if (object instanceof CraftingRecipe) {
			final CraftingRecipe craftingRecipe = (CraftingRecipe) object;
			final Map<Integer, Map<String, Integer>> outputTable = new HashMap<Integer, Map<String, Integer>>();
			for (int i = 0; i < craftingRecipe.craftResult.length; i++) {
				if (craftingRecipe.craftResult[i] != null) {
					outputTable.put(i, (Map<String, Integer>) converter.javaToLUA(craftingRecipe.craftResult[i]));
				}
			}
			final Map<String, Object> ret = new HashMap<String, Object>();
			ret.put(INPUT, inputTable);
			ret.put(OUTPUT, outputTable);
			return ret;
		}
		return inputTable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Recipe luaToJava(Object object) {
		final Recipe recipe;
		final Map<Double, Map<String, Double>> inputTable;
		if (((Map<Object, Object>) object).containsKey(INPUT)) {
			recipe = new CraftingRecipe();
			inputTable = (Map<Double, Map<String, Double>>) ((Map<Object, Object>) object).get(INPUT);
			final Map<Double, Map<String, Double>> outputTable = (Map<Double, Map<String, Double>>) ((Map<Object, Object>) object)
					.get(OUTPUT);
			for (final Entry<Double, Map<String, Double>> entry : outputTable.entrySet()) {
				final ItemStack stackInfo = converter.luaToJava(entry.getValue());
				((CraftingRecipe) recipe).craftResult[entry.getKey().intValue()] = stackInfo;
			}
		} else {
			recipe = new Recipe();
			inputTable = (Map<Double, Map<String, Double>>) object;
		}
		for (final Entry<Double, Map<String, Double>> entry : inputTable.entrySet()) {
			final ItemStack stackInfo = converter.luaToJava(entry.getValue());
			recipe.craftMatrix[entry.getKey().intValue()] = stackInfo;
		}
		return recipe;
	}
}
