package anzac.peripherals.utility.converters;

import anzac.peripherals.tile.NoteTileEntity.Instrument;

public class InstrumentConverter extends EnumConverter<Instrument> {
	@Override
	public Instrument luaToJava(Object object) {
		if (object instanceof Number) {
			return Instrument.values()[((Number) object).intValue()];
		} else if (object instanceof String) {
			return Instrument.valueOf(((String) object).toLowerCase());
		}
		throw new RuntimeException("Expected an Instrument");
	}
}
