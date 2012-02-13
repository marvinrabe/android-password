package de.echtzeitraum.openpassword;

import java.util.Arrays;

/**
 * Generates passwords.
 * @author Marvin Thomas Rabe <m.rabe@echtzeitraum.de>
 * @license GNU General Public License 3 <http://www.gnu.org/licenses/>
 */
public class Generator {

	private static int passwordLength = 8;
	private static boolean numbers = true;
	private static boolean upperChars = true;
	private static boolean lowerChars = true;
	private static boolean punctuation = false;
	private static String include = "";

	final protected String[] punctuationArray = {".",",",":","-","_","$","%","&"};

	protected static String[] charArray = null;
	protected static String[] charExcludeArray = null;


	public void setLength (int n) {
		Generator.passwordLength = n;
	}

	public void setNumbers (boolean usage) {
		if(usage != Generator.numbers)
			Generator.charArray = null;
		Generator.numbers = usage;
	}

	public void setUpperChars (boolean usage) {
		if(usage != Generator.upperChars)
			Generator.charArray = null;
		Generator.upperChars = usage;
	}

	public void setLowerChars (boolean usage) {
		if(usage != Generator.lowerChars)
			Generator.charArray = null;
		Generator.lowerChars = usage;
	}

	public void setPunctuation(boolean usage) {
		if(usage != Generator.punctuation)
			Generator.charArray = null;
		Generator.punctuation = usage;
	}

	public void setInclude(String string) {
		if(!string.equals(Generator.include))
			Generator.charArray = null;
		Generator.include = string;
	}

	public void setExclude(String string) {
		if(string.length() > 0)
			Generator.charExcludeArray = stringToCharArray(string);
		else
			Generator.charExcludeArray = null;
	}

	protected String[] getNumbersArray () {
		String[] resultArray = new String[10];
		for(int i = 0; i < 10; i++) {
			resultArray[i] = new Integer(i).toString();
		}
		return resultArray;
	}

	protected String[] getCharsArray (char start) {
		String[] resultArray = new String[26];
		for(int i = 0; i < 26; i++) {
			int buffer = start + i;
			resultArray[i] = new Character((char) buffer).toString();
		}
		return resultArray;
	}

	protected int getPasswordCharacterAmount () {
		int amount = 0;
		if(Generator.numbers)
			amount += 10;
		if(Generator.upperChars ^ Generator.lowerChars)
			amount += 26;
		if(Generator.upperChars && Generator.lowerChars)
			amount += 52;
		if(Generator.punctuation)
			amount += this.punctuationArray.length;
		amount += Generator.include.length();
		return amount;
	}

	protected String[] getPasswordCharacterArray () {
		String[] array = new String[this.getPasswordCharacterAmount()];
		int index = 0;
		if(Generator.numbers) {
			System.arraycopy(this.getNumbersArray(), 0, array, index, 10);
			index += 10;
		}
		if(Generator.upperChars) {
			System.arraycopy(this.getCharsArray('A'), 0, array, index, 26);
			index += 26;
		}
		if(Generator.lowerChars) {
			System.arraycopy(this.getCharsArray('a'), 0, array, index, 26);
			index += 26;
		}
		if(Generator.punctuation) {
			System.arraycopy(this.punctuationArray, 0, array, index, this.punctuationArray.length);
			index += this.punctuationArray.length;
		}
		if(Generator.include.length() > 0) {
			System.arraycopy(this.stringToCharArray(Generator.include), 0, array, index, Generator.include.length());
			index += Generator.include.length();
		}
		return array;
	}

	public String getPassword() throws RuntimeException {
		if(this.getPasswordCharacterAmount() == 0) {
			return "";
		} else {
			if(Generator.charArray == null)
				Generator.charArray = this.getPasswordCharacterArray();

			int tryNumber = 0;
			int maxTries = 0;
			if(Generator.charExcludeArray != null) {
				maxTries = (Generator.charArray.length - Generator.charExcludeArray.length);
				maxTries *= maxTries;
				if(maxTries < Generator.passwordLength * Generator.passwordLength)
					maxTries = Generator.passwordLength * Generator.passwordLength;
			}

			String pw = new String();
			for (int i=0; i < Generator.passwordLength; i++) {
				String newChar = Generator.charArray[ (int) (Math.random() * Generator.charArray.length) ];
				if(Generator.charExcludeArray != null) {
					if(Arrays.asList(Generator.charExcludeArray).contains(newChar)) {
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
			output[i] = new Character(temp[i]).toString();
		}
		return output;
	}

	public boolean isOk() {
		if(Generator.passwordLength <= 0) {
			return false;
		}
		if(Generator.charArray == null) {
			Generator.charArray = this.getPasswordCharacterArray();
			if(Generator.charArray.length <= 0) {
				return false;
			}
		}
		return true;
	}

}
