package anzac.peripherals.utility.converters;

public abstract class NumberConverter<J extends Number> extends NoOpConverter<J> {

	@Override
	public J luaToJava(final Object object) {
		return toNumber(object);
	}

	protected abstract J toNumber(final Object object);
}
