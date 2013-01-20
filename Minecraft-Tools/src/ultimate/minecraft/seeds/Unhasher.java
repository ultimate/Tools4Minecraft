package ultimate.minecraft.seeds;

import java.util.LinkedList;
import java.util.List;

public class Unhasher
{
	/**
	 * Create a matching String for which {@link String#hashCode()} returns the given hash value.<br>
	 * This algorithm especially is designed for returning a String that matches the following
	 * criterions:<br>
	 * <ul>
	 * <li>{@link String#hashCode()} will return the desired hash value</li>
	 * <li>{@link String#length()} is lower or equal to 7</li>
	 * <li>all characters in the String are within the range of 32 (0x20) to 126 (0x7E) and are
	 * therefore "typeable" with your keyboard.<br>
	 * The reason for this feature is offering the ability of copying or writing down the returned
	 * String to be portable to other programs, code snippets or other purposes without the need of
	 * unicode characters like '\\uXXXX'.</li>
	 * </ul>
	 * Examples:
	 * <ul>
	 * <li>unhash(0) will return " 9\"-4+;"</li>
	 * <li>unhash(17) will return " 9\"-4,-"</li>
	 * <li>unhash(123) will return "{"</li>
	 * <li>unhash(987654321) will return "!-+5=8"</li>
	 * </ul>
	 * This algorithm is only working for the standard String hashcode implementation using the
	 * following algorithm:<br>
	 * <code>s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]</code><br>
	 * <br>
	 * 
	 * @param hash - the desired hash code of the String
	 * @return the matching String for the hash code
	 */
	public static String unhash(int hash)
	{
		// we need at maximum 7 chars
		// the chars and there potencies are stored in 3 arrays:
		// chars -> the chars forming the String
		// maxChars -> the maximum char value for the matching char at the position in the array
		// those values are initialized with 127 (exclusive) and are updating during the algorithm,
		// if following chars do not match the required range
		// base -> the potencies of 31 going from base[0]=31^6 to base[6]=31^0
		char[] chars = new char[7];
		char[] maxChars = new char[chars.length];
		long[] base = new long[chars.length];

		// in order to handle negative numbers they are calculated to their positive counterpart and
		// converted to long
		// this way all factors for the potencies are positive
		long target = (hash > 0 ? hash : hash + 0xFFFFFFFFL + 1);
		// define some variables used later on
		long t;
		long c;
		int i, i2;
		// the start index for the maximum potency (base) to use
		int iStart = 0;

		// initialize the maxChars- and base-array
		for(i = 0; i < chars.length; i++)
		{
			// initialize with 127
			maxChars[i] = Byte.MAX_VALUE;
			// initialize with 31^x
			base[i] = (long) Math.pow(31, chars.length - i - 1);
			// System.out.println("base[" + i + "]=" + base[i]);
			// check the size of the target hash value and set iStart
			// we will start deviding the hash value with the base at this index, because lower
			// indexes contain bases higher than those bases
			if(target / 126 < base[i])
				iStart = i;
		}
		// System.out.println("iStart=" + iStart);

		// start finding a matching string
		do
		{
			// set t to the hash value since we do not want to modify the original value
			t = target;

			// for each base determine the factor that is required to reach the target hash
			for(i = iStart; i < chars.length; i++)
			{
				// calculate the factor / char
				c = (long) (t / base[i]);
				// check for the dynamically range with the max char value
				if(c > maxChars[i])
					c = maxChars[i];
				// reduce the hash by c*31^x (inverse hash algorithm)
				t -= base[i] * c;
				// store the factor / char in the char array
				chars[i] = (char) c;
				// System.out.println("target=" + t + "\tc=" + c + "(" + i + ")");
				// check for "typeability" (all chars < 32 are not easily typeable with a keyboard)
				if(c < 32)
				{
					if(i != iStart)
					{
						// this is not the first char
						// we can reduce the previouse char by 1 if we increase this char by 31 (due
						// to the hash algorithm)
						maxChars[i - 1] = (char) (chars[i - 1] - 1);
						// if we do this, we have to start the process all over
						// therefore we have to reinitialize the following maxChars values (reinit
						// to 127)
						for(i2 = i; i2 < chars.length; i2++)
							maxChars[i2] = Byte.MAX_VALUE;
						// cancel the for loop and start all over
						break;
					}
					else
					{
						// this is the first char generated: we cannot reduce the previouse char by
						// 1, but we can increase the hash value by one integer range (2^32) and
						// start all over (since all longs in a step distance of 2^32 would result
						// in the same int, which is indeed accepted whithin the hash algorithm,
						// when int overflows occur)
						target += 0xFFFFFFFFL + 1;
						// since we changed the target hash, we have to recalculate iStart (target
						// is bigger now -> we need more potencies)
						for(i2 = 0; i2 < chars.length; i2++)
						{
							if(target / (Byte.MAX_VALUE-1) < base[i2])
								iStart = i2;
						}
						// cancel the for loop and start all over
						break;
					}
				}
			}
			// calculation is done, when we reach here and the for loop has finished with all valid
			// chars (i has gone through until the last char)
		} while(i != chars.length);

		// return the char array as a String starting from iStart (ignore unused leading chars)
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
		for(i = start; i <= Integer.MAX_VALUE; i += 1000)// ++)
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
