package de.echtzeitraum.openpassword;

import java.util.Arrays;
import java.util.Random;

/**
 * Generates passwords.
 * @author Marvin Rabe <me@marvinrabe.de>
 * @licenses GNU General Public License 3 <http://www.gnu.org/licenses/>
 */
public class PasswordGenerator {

	public int length;
	private boolean numbers;
	private boolean upperChars;
	private boolean lowerChars;
	private boolean punctuation;
	private String include = "";

    private Random random;

	final protected String[] punctuationArray = {".",",",":","-","_","$","%","&"};

	protected String[] charArray = null;
	protected String[] charExcludeArray = null;

    /**
     * Initializes PasswordGenerator.
     */
    public PasswordGenerator() {
        random = new Random();
    }

    /**
     * Initializes Generator with a seed.
     * @param seed Seed for random function.
     */
    public PasswordGenerator(long seed) {
        random = new Random(seed);
    }

    /**
     * Activates or deactivates numbers in password.
     * @param usage Set to true if password should contain numbers.
     */
	public void setNumbers (boolean usage) {
		if(usage != numbers)
			charArray = null;
		numbers = usage;
	}

    /**
     * Activates or deactivates uppercase characters in password.
     * @param usage Set to true if password should contain uppercase characters.
     */
	public void setUpperChars (boolean usage) {
		if(usage != upperChars)
			charArray = null;
		upperChars = usage;
	}

    /**
     * Activates or deactivates lowercase characters in password.
     * @param usage Set to true if password should contain lowercase characters.
     */
	public void setLowerChars (boolean usage) {
		if(usage != lowerChars)
			charArray = null;
		lowerChars = usage;
	}

    /**
     * Activates or deactivates numbers in password.
     * @param usage Set to true if password should contain numbers.
     */
	public void setPunctuation(boolean usage) {
		if(usage != punctuation)
			charArray = null;
		punctuation = usage;
	}

	public void setInclude(String string) {
		if(!string.equals(include))
			charArray = null;
		include = string;
	}

	public void setExclude(String string) {
		if(string.length() > 0)
			charExcludeArray = stringToCharArray(string);
		else
			charExcludeArray = null;
	}

	protected String[] getNumbersArray () {
		String[] resultArray = new String[10];
		for(int i = 0; i < 10; i++) {
			resultArray[i] = Integer.toString(i);
		}
		return resultArray;
	}

	protected String[] getCharsArray (char start) {
		String[] resultArray = new String[26];
		for(int i = 0; i < 26; i++) {
			int buffer = start + i;
			resultArray[i] = Character.toString((char) buffer);
		}
		return resultArray;
	}

	protected int getPasswordCharacterAmount () {
		int amount = 0;
		if(numbers)
			amount += 10;
		if(upperChars)
			amount += 26;
		if(lowerChars)
			amount += 26;
		if(punctuation)
			amount += this.punctuationArray.length;
		amount += include.length();
		return amount;
	}

	protected String[] getPasswordCharacterArray () {
		String[] array = new String[this.getPasswordCharacterAmount()];
		int index = 0;
		if(numbers) {
			System.arraycopy(this.getNumbersArray(), 0, array, index, 10);
			index += 10;
		}
		if(upperChars) {
			System.arraycopy(this.getCharsArray('A'), 0, array, index, 26);
			index += 26;
		}
		if(lowerChars) {
			System.arraycopy(this.getCharsArray('a'), 0, array, index, 26);
			index += 26;
		}
		if(punctuation) {
			System.arraycopy(this.punctuationArray, 0, array, index, this.punctuationArray.length);
			index += this.punctuationArray.length;
		}
		if(include.length() > 0) {
			System.arraycopy(this.stringToCharArray(include), 0, array, index, include.length());
			index += include.length();
		}
		return array;
	}

	public String getPassword() throws RuntimeException {
		if(this.getPasswordCharacterAmount() == 0) {
			return "";
		} else {
			if(charArray == null)
				charArray = this.getPasswordCharacterArray();

			int tryNumber = 0;
			int maxTries = 0;
			if(charExcludeArray != null) {
				maxTries = (charArray.length - charExcludeArray.length);
				maxTries *= maxTries;
				if(maxTries < length * length)
					maxTries = length * length;
			}

			String pw = "";
			String newChar;
			for (int i=0; i < length; i++) {
				newChar = charArray[ random.nextInt(charArray.length) ];
				if(charExcludeArray != null) {
					if(Arrays.asList(charExcludeArray).contains(newChar)) {
						if(tryNumber > maxTries) {
							pw = "";
							break;
						} else {
							i--;
							tryNumber++;
							continue;
						}
					}
				}
				pw += newChar;
			}
			return pw;
		}
	}

	private String[] stringToCharArray (String input) {
		int inputLength = input.length();
		char[] temp = new char[inputLength];
		String[] output = new String[inputLength];
		for(int i=0; i<inputLength; i++) {
			temp[i] = input.charAt(i);
			output[i] = Character.toString(temp[i]);
		}
		return output;
	}

	public boolean isOk() {
		if(length <= 0) {
			return false;
		}
		if(charArray == null) {
			charArray = this.getPasswordCharacterArray();
			if(charArray.length <= 0) {
				return false;
			}
		}
		return true;
	}

}
