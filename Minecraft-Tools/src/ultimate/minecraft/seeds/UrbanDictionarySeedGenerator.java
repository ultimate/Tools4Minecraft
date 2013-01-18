package ultimate.minecraft.seeds;


public class UrbanDictionarySeedGenerator extends RandomURLSeedGenerator
{
	private static final String	randomURL	= "http://www.urbandictionary.com/random.php";

	private static final String	startTag	= "<title>Urban Dictionary: ";
	private static final String	endTag		= "</title>";

	public UrbanDictionarySeedGenerator()
	{
		super(randomURL, startTag, endTag);
	}
}
