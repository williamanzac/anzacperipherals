package anzac.peripherals.utility;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import anzac.peripherals.peripherals.Recipe;
import anzac.peripherals.peripherals.Target;
import anzac.peripherals.tile.NoteTileEntity.Instrument;
import anzac.peripherals.utility.converters.BooleanConverter;
import anzac.peripherals.utility.converters.Converter;
import anzac.peripherals.utility.converters.DirectionConverter;
import anzac.peripherals.utility.converters.FluidStackConverter;
import anzac.peripherals.utility.converters.FluidTankInfoConverter;
import anzac.peripherals.utility.converters.HashMapConverter;
import anzac.peripherals.utility.converters.InstrumentConverter;
import anzac.peripherals.utility.converters.IntConverter;
import anzac.peripherals.utility.converters.ItemStackConverter;
import anzac.peripherals.utility.converters.LongConverter;
import anzac.peripherals.utility.converters.PotionEffectConverter;
import anzac.peripherals.utility.converters.RecipeConverter;
import anzac.peripherals.utility.converters.StringConverter;
import anzac.peripherals.utility.converters.TargetConverter;

public class TypeConverter {

	private static final Map<Class<?>, Converter<?>> converters = new HashMap<Class<?>, Converter<?>>();

	static {
		converters.put(String.class, new StringConverter());
		final IntConverter intConverter = new IntConverter();
		converters.put(Integer.class, intConverter);
		converters.put(int.class, intConverter);
		final LongConverter longConverter = new LongConverter();
		converters.put(Long.class, longConverter);
		converters.put(long.class, longConverter);
		final BooleanConverter booleanConverter = new BooleanConverter();
		converters.put(Boolean.class, booleanConverter);
		converters.put(boolean.class, booleanConverter);
		converters.put(ForgeDirection.class, new DirectionConverter());
		converters.put(Instrument.class, new InstrumentConverter());
		final RecipeConverter recipeConverter = new RecipeConverter();
		converters.put(Recipe.class, recipeConverter);
		converters.put(ItemStack.class, new ItemStackConverter());
		converters.put(Target.class, new TargetConverter());
		converters.put(FluidStack.class, new FluidStackConverter());
		converters.put(FluidTankInfo.class, new FluidTankInfoConverter());
		converters.put(PotionEffect.class, new PotionEffectConverter());
		converters.put(HashMap.class, new HashMapConverter());
	}

	public static Object[] convertArguments(final Object[] arguments, final Method method) throws Exception {
		if (arguments == null) {
			return null;
		}
		final Object[] parameters = new Object[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			parameters[i] = luaToJava(arguments[i], method.getParameterTypes()[i]);
		}
		return parameters;
	}

	@SuppressWarnings("unchecked")
	public static Object[] convertReturn(final Object object, final Class<?> toClass) throws Exception {
		if (object == null) {
			return null;
		}
		if (object.getClass().isArray()) {
			final Object[] objects = (Object[]) object;
			final Map<Integer, Object> rets = new HashMap<Integer, Object>();
			for (int i = 0; i < objects.length; i++) {
				final Object value = objects[i];
				rets.put(i + 1, javaToLUA(value, value.getClass()));
			}
			// LogHelper.info("returning array: " + rets);
			return new Object[] { rets };
		} else if (object instanceof List) {
			final List<Object> objects = (List<Object>) object;
			final Map<Integer, Object> rets = new HashMap<Integer, Object>();
			for (int i = 0; i < objects.size(); i++) {
				final Object value = objects.get(i);
				rets.put(i + 1, javaToLUA(value, value.getClass()));
			}
			// LogHelper.info("returning list: " + rets);
			return new Object[] { rets };
		} else if (object instanceof Map) {
			final Map<Object, Object> objects = (Map<Object, Object>) object;
			final Map<Object, Object> rets = new HashMap<Object, Object>();
			for (Entry<Object, Object> entry : objects.entrySet()) {
				final Object key = javaToLUA(entry.getKey(), entry.getKey().getClass());
				final Object value = javaToLUA(entry.getValue(), entry.getValue().getClass());
				rets.put(key, value);
			}
			// LogHelper.info("returning map: " + rets);
			return new Object[] { rets };
		}
		final Object ret = javaToLUA(object, toClass);
		// LogHelper.info("returning: " + ret);
		return new Object[] { ret };
	}

	public static Object javaToLUA(final Object object, final Class<?> toClass) {
		if (object == null) {
			return null;
		}
		for (final Entry<Class<?>, Converter<?>> entry : converters.entrySet()) {
			if (entry.getKey().isAssignableFrom(toClass)) {
				return entry.getValue().javaToLUA(object);
			}
		}
		return object;
	}

	public static Object luaToJava(final Object object, final Class<?> toClass) throws Exception {
		if (object == null) {
			return null;
		}
		for (final Entry<Class<?>, Converter<?>> entry : converters.entrySet()) {
			if (entry.getKey().isAssignableFrom(toClass)) {
				return entry.getValue().luaToJava(object);
			}
		}
		throw new Exception("Expected argument of type " + toClass.getName() + " got " + object.getClass());
	}
}
