package ultimate.minecraft.seeds;

import ultimate.minecraft.RandomWorldGenerator;

public abstract class SeedGenerator
{

	public static final String	seedRegExp1	= "[^/?*:;{}\\\\]+";
	public static final String	seedRegExp2	= "[\\x00-\\x7F]+";

	public abstract String getSeed();

	protected RandomWorldGenerator	generator;

	public boolean isValidSeed(String seed)
	{
		if(seed == null)
			return false;
		return seed.matches(seedRegExp1) && seed.matches(seedRegExp2);
	}

	public RandomWorldGenerator getGenerator()
	{
		return generator;
	}

	public void setGenerator(RandomWorldGenerator generator)
	{
		this.generator = generator;
	}
}
