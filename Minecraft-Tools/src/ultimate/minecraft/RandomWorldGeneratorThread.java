package ultimate.minecraft;

public class RandomWorldGeneratorThread extends Thread
{
	protected RandomWorldGenerator	generator;

	private boolean					running;

	public RandomWorldGeneratorThread(RandomWorldGenerator generator)
	{
		this.generator = generator;
		this.running = false;
	}

	@Override
	public void run()
	{
		this.running = true;
		int i = 0;
		String seed;
		while(running)
		{
			i++;
			printStatus(i);
			seed = generator.generateWorld();
			if(seed == null)
			{
				System.out.println("seed is null: aborting!");
				running = false;
			}
		}
	}

	public void stopGeneration() throws InterruptedException
	{
		this.running = false;
		this.join();
	}
	
	public void printStatus(int i) {
		System.out.println("Generating world #" + i);
	}
}
