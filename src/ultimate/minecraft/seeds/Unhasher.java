package ultimate.minecraft.seeds;

import java.util.LinkedList;
import java.util.List;

public class Unhasher
{
	public static String unhash(int hash)
	{
		char[] chars = new char[7];
		char[] maxChars = new char[chars.length];
		long[] base = new long[chars.length];

		long target = (hash > 0 ? hash : hash + 0xFFFFFFFFL + 1);
		long t;
		long c;
		int i, i2;
		int iStart = 0;

		for(i = 0; i < chars.length; i++)
		{
			maxChars[i] = Byte.MAX_VALUE;
			base[i] = (long) Math.pow(31, chars.length - i - 1);
			// System.out.println("base[" + i + "]=" + base[i]);
			if(target / 126 < base[i])
				iStart = i;
		}
		// System.out.println("iStart=" + iStart);

		while(true)
		{
			t = target;
			// for(i = 0; i < chars.length; i++)
			// {
			// if(target/126 < base[i])
			// iStart = i;
			// }
			// System.out.println("target=" + t);

			for(i = iStart; i < chars.length; i++)
			{
				c = (long) (t / base[i]);
				if(c > maxChars[i])
					c = maxChars[i];
				t -= base[i] * c;
				chars[i] = (char) c;
				// System.out.println("target=" + t + "\tc=" + c + "(" + i + ")");
				if(c < 32)
				{
					if(i == iStart)
					{
						target += 0xFFFFFFFFL + 1;
						for(i2 = 0; i2 < chars.length; i2++)
						{
							if(target / 126 < base[i2])
								iStart = i2;
						}
						break;
					}
					else
					{
						maxChars[i - 1] = (char) (chars[i - 1] - 1);
						for(i2 = i; i2 < chars.length; i2++)
							maxChars[i2] = Byte.MAX_VALUE;
						break;
					}
				}
			}
			if(i == chars.length)
				break;
		}

		return new String(chars, iStart, chars.length - iStart);
	}

	public static void main(String[] args)
	{
		int start = Integer.MIN_VALUE;
		start = -2000000000;
		String s;
		long i;
		List<Integer> failures = new LinkedList<Integer>();
		long startTime = System.currentTimeMillis();
		double percent;
		long timeElapsed;
		long timeTotal;
		long timeLeft;
		for(i = start; i <= Integer.MAX_VALUE; i += 1000)//++)
		{
			// System.out.println("i: " + i);
			s = unhash((int) i);
			// System.out.println("i: " + i + " : " + s + " : " + s.hashCode() + " : " +
			// (s.hashCode() == i));
			if(s.hashCode() != (int) i)
			{
				failures.add((int) i);
				break;
			}
			if(i % 1000000 == 0)
			{
				percent = (double) (i + Integer.MAX_VALUE) / (Integer.MAX_VALUE * 2.0) * 100;
				timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
				timeTotal = (long) (timeElapsed * 100 / percent);
				timeLeft = timeTotal - timeElapsed;
				System.out.println(Math.round(percent * 100) / 100.0 + "% (" + timeElapsed + "s of " + timeTotal + "s = " + timeLeft + "s) - i=" + i
						+ " (failures: " + failures.size() + ")");
				if(failures.size() > 0)
					System.out.println(failures);
			}
		}
		System.out.println(failures);
	}
}
