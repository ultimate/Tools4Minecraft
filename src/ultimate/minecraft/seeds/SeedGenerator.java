package ultimate.minecraft.seeds;

public abstract class SeedGenerator
{
	
	public static final String seedRegExp1 = "[^/?*:;{}\\\\]+";
	public static final String seedRegExp2 = "[\\x00-\\x7F]+";
	public abstract String getSeed();
	
	public boolean isValidSeed(String seed)
	{
		if(seed == null)
			return false;
		return seed.matches(seedRegExp1) && seed.matches(seedRegExp2);
	}
}
