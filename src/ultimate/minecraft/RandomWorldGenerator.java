package ultimate.minecraft;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import ultimate.karoapi4j.utils.PropertiesUtil;
import ultimate.minecraft.seeds.SeedGenerator;
import ultimate.minecraft.seeds.UrbanDictionarySeedGenerator;

public class RandomWorldGenerator
{
	public static String	mlgCMD	= "java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar %s %s -x0 -y0";
	public static String	zmcCMD	= "mcmap \"../MinecraftLandGenerator/%w\"";

	protected SeedGenerator	seedGenerator;

	protected File			toolsDir;
	protected File			mlgDir;
	protected File			zmcDir;
	protected File			outputDir;

	protected int			worldSize;
	protected boolean		removeWorlds;

	public RandomWorldGenerator(Class<? extends SeedGenerator> generatorClass, File toolsDir, File outputDir, int worldSize, boolean removeWorlds)
			throws InstantiationException, IllegalAccessException
	{
		super();

		this.toolsDir = toolsDir;
		this.mlgDir = new File(toolsDir.getAbsolutePath() + "/MinecraftLandGenerator");
		this.zmcDir = new File(toolsDir.getAbsolutePath() + "/zmcmap");
		this.outputDir = outputDir;
		this.worldSize = worldSize;
		this.removeWorlds = removeWorlds;

		this.seedGenerator = generatorClass.newInstance();
		this.seedGenerator.setGenerator(this);
	}

	public SeedGenerator getSeedGenerator()
	{
		return seedGenerator;
	}

	public File getToolsDir()
	{
		return toolsDir;
	}

	public File getMlgDir()
	{
		return mlgDir;
	}

	public File getZmcDir()
	{
		return zmcDir;
	}

	public File getOutputDir()
	{
		return outputDir;
	}

	public int getWorldSize()
	{
		return worldSize;
	}

	public boolean isRemoveWorlds()
	{
		return removeWorlds;
	}

	public String generateWorld()
	{
		System.out.print("  Generating seed...     ");
		String seed = seedGenerator.getSeed();
		System.out.println(seed);

		if(seed == null)
			return null;

		System.out.print("  Updating properties... ");
		String worldName = modifyProperties(seed);
		System.out.println("OK");

		System.out.print("  Running MLG...         ");
		execMLG(this.worldSize);
		System.out.println("OK");

		System.out.print("  Running ZMCMAP...      ");
		execZMC(worldName);
		System.out.println("OK");

		System.out.print("  Renaming image...      ");
		renameImage(seed);
		System.out.println("OK");

		if(removeWorlds)
		{
			System.out.print("  Removing wolrd...      ");
			removeWorld(worldName);
			System.out.println("OK");
		}

		return seed;
	}

	protected String modifyProperties(String seed)
	{
		return modifyProperties(new File(mlgDir.getAbsolutePath() + "/server.properties"), seed);
	}

	protected String modifyProperties(File propertiesFile, String seed)
	{
		try
		{
			Properties p = PropertiesUtil.loadProperties(propertiesFile);

			p.setProperty("level-name", seed);
			p.setProperty("level-seed", seed);

			PropertiesUtil.storeProperties(propertiesFile, p, "");

			return p.getProperty("level-name");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	protected void renameImage(String seed)
	{
		File generated = new File(zmcDir.getAbsolutePath() + "/output.png");
		File target = new File(outputDir.getAbsolutePath() + "/" + seed + ".png");
		int i = 1;
		while(target.exists())
			target = new File(target.getAbsolutePath().substring(0, target.getAbsolutePath().lastIndexOf(".")) + "_" + (i++) + ".png");
		generated.renameTo(target);
	}

	protected void removeWorld(String worldName)
	{
		removeFile(new File(mlgDir + "/" + worldName));
	}

	private void removeFile(File file)
	{
		if(file.isDirectory())
		{
			for(File child : file.listFiles())
				removeFile(child);
		}
		file.delete();
	}

	protected void execMLG(int size)
	{
		try
		{
			exec(mlgDir, "cmd /c " + mlgCMD.replace("%s", "" + size), false);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	protected void execZMC(String worldName)
	{
		try
		{
			exec(zmcDir, "cmd /c " + zmcCMD.replace("%w", "" + worldName), false);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	protected int exec(File dir, String cmd, boolean print) throws IOException
	{
		// System.out.println("  executing: " + cmd);
		Process p = Runtime.getRuntime().exec(cmd, null, dir);
		int c;
		while((c = p.getInputStream().read()) != -1)
		{
			if(print)
				System.out.print((char) c);
		}
		return p.exitValue();
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException
	{
		RandomWorldGenerator rg = new RandomWorldGenerator(UrbanDictionarySeedGenerator.class, new File("C:/H2O/Verkin/WS/Minecraft-Tools/tools"),
				new File("C:/H2O/Verkin/WS/Minecraft-Tools/levels"), 20, true);

		rg.generateWorld();

		System.exit(0);
	}
}
