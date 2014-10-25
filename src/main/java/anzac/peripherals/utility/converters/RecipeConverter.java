package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import anzac.peripherals.peripherals.Recipe;

public class RecipeConverter implements Converter<Recipe> {

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
		return inputTable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Recipe luaToJava(Object object) {
		final Map<Double, Map<String, Double>> table = (Map<Double, Map<String, Double>>) object;
		final Recipe recipe = new Recipe();
		for (final Entry<Double, Map<String, Double>> entry : table.entrySet()) {
			final ItemStack stackInfo = converter.luaToJava(entry.getValue());
			recipe.craftMatrix[entry.getKey().intValue()] = stackInfo;
		}
		return recipe;
	}
}
