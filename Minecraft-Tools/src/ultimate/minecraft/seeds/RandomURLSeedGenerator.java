package ultimate.minecraft.seeds;

import java.net.URL;

import ultimate.karoapi4j.utils.URLLoaderUtil;

public class RandomURLSeedGenerator extends SeedGenerator
{
	protected String	randomURL	= "http://en.wikipedia.org/wiki/Special:Random";

	protected String	startTag	= "<h1 id=\"firstHeading\" class=\"firstHeading\" lang=\"en\"><span dir=\"auto\">";
	protected String	endTag		= "</span></h1>";

	public RandomURLSeedGenerator(String randomURL, String startTag, String endTag)
	{
		super();
		this.randomURL = randomURL;
		this.startTag = startTag;
		this.endTag = endTag;
	}

	public String getRandomURL()
	{
		return randomURL;
	}

	public String getStartTag()
	{
		return startTag;
	}

	public String getEndTag()
	{
		return endTag;
	}

	/*
	 * (non-Javadoc)
	 * @see ultimate.minecraft.SeedGenerator#getSeed()
	 */
	@Override
	public String getSeed()
	{
		String seed = null;
		do
		{
			try
			{
				String randomPage = URLLoaderUtil.readURL(new URL(randomURL));// , "GET", null, 10000);

				int start = randomPage.indexOf(startTag) + startTag.length();
				int end = randomPage.indexOf(endTag);

				seed = randomPage.substring(start, end);
			}
			catch (Exception e)
			{
				seed = null;
			}
		}
		while(!isValidSeed(seed));
		return seed;
	}

	public static void main(String[] args)
	{
		SeedGenerator sg;

		sg = new WikipediaSeedGenerator();

		for(int i = 0; i < 10; i++)
		{
			System.out.print("Seed #" + i + ":\t");
			System.out.println(sg.getSeed());
		}

		sg = new UrbanDictionarySeedGenerator();

		for(int i = 0; i < 10; i++)
		{
			System.out.print("Seed #" + i + ":\t");
			System.out.println(sg.getSeed());
		}

		System.exit(0);
	}
}
