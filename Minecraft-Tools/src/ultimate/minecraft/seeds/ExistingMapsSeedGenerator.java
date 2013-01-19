package ultimate.minecraft.seeds;

import java.io.File;

import ultimate.minecraft.RandomWorldGenerator;

public class ExistingMapsSeedGenerator extends SeedGenerator
{
	private File[]	files;
	private int		index;

	@Override
	public void setGenerator(RandomWorldGenerator generator)
	{
		super.setGenerator(generator);
		files = generator.getOutputDir().listFiles();
		index = 0;
	}

	@Override
	public String getSeed()
	{
		if(index >= files.length)
			return null;
		
		File f = files[index++];
	
		String seed = f.getName();
		if(seed.contains("/"))
			seed = seed.substring(seed.lastIndexOf("/")+1);
		if(seed.contains("\\"))
			seed = seed.substring(seed.lastIndexOf("\\")+1);
		if(seed.contains("."))
			seed = seed.substring(0, seed.lastIndexOf("."));
		return seed;
	}
	
	
}
