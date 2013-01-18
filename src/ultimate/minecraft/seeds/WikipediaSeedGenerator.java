package ultimate.minecraft.seeds;


public class WikipediaSeedGenerator extends RandomURLSeedGenerator
{
	private static final String	randomURL	= "http://en.wikipedia.org/wiki/Special:Random";

	private static final String	startTag	= "<h1 id=\"firstHeading\" class=\"firstHeading\" lang=\"en\"><span dir=\"auto\">";
	private static final String	endTag		= "</span></h1>";
	
	public WikipediaSeedGenerator()
	{
		super(randomURL, startTag, endTag);
	}
}
