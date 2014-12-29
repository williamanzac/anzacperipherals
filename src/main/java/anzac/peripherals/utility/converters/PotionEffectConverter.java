package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

public class PotionEffectConverter implements Converter<PotionEffect> {

	@Override
	public Object javaToLUA(final Object object) {
		final PotionEffect effect = (PotionEffect) object;
		final Map<String, Object> table = new HashMap<String, Object>();
		table.put("name", StatCollector.translateToLocal(effect.getEffectName()).trim());
		table.put("id", effect.getPotionID());
		table.put("duration", effect.getDuration());
		table.put("amplifier", effect.getAmplifier());
		table.put("ambient", effect.getIsAmbient());
		return table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PotionEffect luaToJava(final Object object) {
		final Map<String, Object> table = (Map<String, Object>) object;
		final int id = ((Integer) table.get("id"));
		final int duration = ((Integer) table.get("duration"));
		final int amplifier = ((Integer) table.get("amplifier"));
		final boolean ambient = ((Boolean) table.get("ambient"));
		final PotionEffect effect = new PotionEffect(id, duration, amplifier, ambient);
		return effect;
	}
}
