import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

//first input file must be a key formated as so
/* (size which is 3 here)
 * 4 6 12
 * 12 55 7
 * 22 5 2
 */
//second input file must be the plain text, less than 10000 characters
public class hillcipher 
{
	public static void main(String [] args)
	{
		int[][] key = getEncryptionKey(args[0]);
		String plaintext = getPlaintext(args[1]);
		//pads the plaintext
		plaintext = padPlaintext(plaintext, key.length);
		String ciphertext = getCiphertext(plaintext, key);
		
		printOutput(key, plaintext, ciphertext);
	}
	
	public static void printOutput(int[][] key, String plaintext, String ciphertext)
	{
		//Prints out the key Matrix
		System.out.println("Key Matrix:\n");
		for(int i = 0; i < key.length; i++)
		{
			for(int j = 0; j < key[i].length; j++)
			{
				System.out.print(key[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("\n");
		
		//prints out the plaintext
		System.out.println("Plaintext:\n");
		printEightyCharsPerLine(plaintext);
		System.out.println("\n\n");
		
		//prints out the ciphertext
		System.out.println("Ciphertext:\n");
		printEightyCharsPerLine(ciphertext);
		System.out.println("\n\n");
	}
	
	//given text is printed 80 characters per line
	public static void printEightyCharsPerLine(String ciphertext)
	{
		for(int i = 0; i < ciphertext.length(); i++)
		{
			//eightieth character, wrap text
			if(i != 0 && i % 80 == 0)
				System.out.println();
			System.out.print(ciphertext.charAt(i));
		}
	}
	
	//Using the key, the plaintext is converted to ciphertext, and is returned
	public static String getCiphertext(String plaintext, int[][] key)
	{
		String ciphertext = new String();
		
		for(int i = 0; i < plaintext.length(); i+=key.length)
		{
			ciphertext += CipherizeChunk(plaintext.substring(i, i+key.length), key);
		}
		
		return ciphertext;
	}
	
	//helper function for getCiphertext
	//multiplies the string by the key to get a chunk of ciphertext
	public static String CipherizeChunk(String s, int[][] key)
	{
		String ciphertext = new String();
		
		//gets the int values that correspond with the chars in s
		int[] stringValues = new int[s.length()];
		for(int i = 0; i < s.length(); i++)
		{
			stringValues[i] = s.charAt(i) - 'a';
		}
		
		
		//multiplies the matrices and mods 26, to get cyphertextValues
		//these ciphertext values are then converted to ciphertext
		int[] ciphertextValues = new int[stringValues.length];
		for(int i = 0; i < stringValues.length; i++)
		{
			for(int j = 0; j < key[i].length; j++)
			{
				ciphertextValues[i] += stringValues[j]*key[i][j];
			}
			ciphertextValues[i] %= 26;
			ciphertext += (char)('a' + ciphertextValues[i]);
		}
	
		return ciphertext;
	}

	//pads the plaintext until it is the appropriate length
	public static String padPlaintext(String plaintext, int keySize)
	{
		//if the plaintext can be partitioned evenly into strings of size keySize,
		//then no more padding is needed
		if(plaintext.length() % keySize == 0)
			return plaintext;
		
		return padPlaintext(plaintext + 'x', keySize);
	}
	
	
	//returns the plaintext from the file, only lowercase letters are returned
	public static String getPlaintext(String fileName)
	{
		File file = new File(fileName);
		BufferedReader br;
		
		char[] inputBuffer = new char[10000];
		String plaintext = new String();
		
		try
		{
			br = new BufferedReader(new FileReader(file));
			//puts contents of the file into the inputBuffer
			br.read(inputBuffer);
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//copies over all letters to the plaintext
		//excludes any non-letter characters
		for(int i = 0; i < inputBuffer.length; i++)
		{
			if(Character.isLetter(inputBuffer[i]))
				plaintext += inputBuffer[i];
		}
		
		//plaintext must be all lowercase
		plaintext = plaintext.toLowerCase();
		
		return plaintext;
	}


	//gets the Encryption key from the file and returns the 2d array with it
	public static int[][] getEncryptionKey(String fileName)
	{
		File file = new File(fileName);
		BufferedReader br;
		
		int arraySize = 0;
		int[][] key = new int[arraySize][arraySize];
		String s = new String();
		
		try
		{
			br = new BufferedReader(new FileReader(file));
			
			//gets the size of the array
			s = br.readLine();
			arraySize = Integer.parseInt(s);
			
			//creates an array of the correct size
			key = new int[arraySize][arraySize];
			
			//populates the 2d array with all the information
			for(int i = 0; i < arraySize; i++)
			{
				key[i] = getIntArrayFromString(br.readLine(), arraySize);
			}
			
			//closes the RufferedReader
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return key;
	}

	//helper function for getEncryptionKey
	//input must be integers seperated by a space
	public static int[] getIntArrayFromString(String s, int size)
	{
		int[] array = new int[size];
		int stringIndex = 0, arrayIndex = 0;
		
		//goes through a string, and returns all integers that are seperated by a space
		for(int i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) == ' ')
			{
				array[arrayIndex] = Integer.parseInt(s.substring(stringIndex, i));
				stringIndex = i + 1;
				arrayIndex++;
			}
		}
		//gets the final integer
		array[arrayIndex] = Integer.parseInt(s.substring(stringIndex, s.length()));
		
		return array;
	}
}
