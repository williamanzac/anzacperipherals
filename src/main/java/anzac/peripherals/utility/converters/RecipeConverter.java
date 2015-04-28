package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import anzac.peripherals.peripherals.Recipe;
import anzac.peripherals.peripherals.Recipe.RecipeType;

public class RecipeConverter implements Converter<Recipe> {

	private static final String TYPE = "type";
	private static final String OUTPUT = "output";
	private static final String INPUT = "input";
	private final ItemStackConverter converter = new ItemStackConverter();

	@SuppressWarnings("unchecked")
	@Override
	public Object javaToLUA(final Object object) {
		final Recipe recipe = (Recipe) object;
		final Map<Integer, Map<String, Integer>> inputTable = new HashMap<Integer, Map<String, Integer>>();
		for (int i = 0; i < recipe.craftMatrix.length; i++) {
			final ItemStack itemStack = recipe.craftMatrix[i];
			if (itemStack != null) {
				inputTable.put(i, (Map<String, Integer>) converter.javaToLUA(itemStack));
			}
		}
		final Map<Integer, Map<String, Integer>> outputTable = new HashMap<Integer, Map<String, Integer>>();
		for (int i = 0; i < recipe.craftResult.length; i++) {
			final ItemStack itemStack = recipe.craftResult[i];
			if (itemStack != null) {
				outputTable.put(i, (Map<String, Integer>) converter.javaToLUA(itemStack));
			}
		}
		final Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(INPUT, inputTable);
		ret.put(OUTPUT, outputTable);
		ret.put(TYPE, recipe.type);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Recipe luaToJava(final Object object) {
		final Recipe recipe = new Recipe();
		final Map<Object, Object> map = (Map<Object, Object>) object;
		final Map<Double, Map<String, Double>> inputTable = (Map<Double, Map<String, Double>>) map.get(INPUT);
		final Map<Double, Map<String, Double>> outputTable = (Map<Double, Map<String, Double>>) map.get(OUTPUT);
		for (final Entry<Double, Map<String, Double>> entry : outputTable.entrySet()) {
			final ItemStack stackInfo = converter.luaToJava(entry.getValue());
			recipe.craftResult[entry.getKey().intValue()] = stackInfo;
		}
		for (final Entry<Double, Map<String, Double>> entry : inputTable.entrySet()) {
			final ItemStack stackInfo = converter.luaToJava(entry.getValue());
			recipe.craftMatrix[entry.getKey().intValue()] = stackInfo;
		}
		recipe.type = (RecipeType) map.get(TYPE);
		return recipe;
	}
}
