import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

//first command line input is the file name with a plaintext 504 characters or less
//second is a c (for calculate), and v (for verify)
//for verify, the last 8 characters are the CRC

public class crcheck 
{
	public static void main(String [] args) 
	{
		String text = new String();
		
		//calculate CRC
		if(args[0].equals("c"))
		{
			try
			{
				text = getInputText(args[1]);
			}
			catch(Exception e)
			{
				System.out.println("Non-ascii character found");
				e.printStackTrace();
			}
			
			calculateAndPrintCRC(text);
		}
		//verify CRC
		else if(args[0].equals("v"))
		{
			try
			{
				text = getInputText(args[1]);
			}
			catch(Exception e)
			{
				System.out.println("Non-ascii character found");
				e.printStackTrace();
			}
			
			verifyAndPrintCRC(text);
		}
		else
		{
			System.out.println("Error. Error. First input must be a \"c\" or \"v\"");
		}
		
	}
	
	public static void calculateAndPrintCRC(String text)
	{
		System.out.println("CRC 16 calculation progress: \n");
		
		//pads the input text with "."
		for(int i = text.length(); i < 504; i++)
			text += ".";
		
		//calculates the first seven lines of CRC and prints it to the screen
		int calculatedCRC = 0;
		String substring;
		for(int i = 0; i < 7; i++)
		{
			//calculates the CRC
			substring = text.substring(i*64, (i+1)*64);
			calculatedCRC = calculateLineCRC(substring, calculatedCRC);
			
			//prints the necessary text to the screen
			System.out.print(substring + " - ");
			//pads the right of the CRC with the necessary zeros
			int numZeros = Integer.numberOfLeadingZeros(calculatedCRC)/4;
			for(int j = 0; j < numZeros; j++)
				System.out.print("0");
			//prints the CRC at the end of each line
			System.out.println(Integer.toHexString(calculatedCRC));
		}
		
		//calculates the final CRC
		substring = text.substring(448, 504);
		calculatedCRC = calculateLineCRC(substring, calculatedCRC);
		
		//prints the necessary text to the screen
		System.out.print(substring);
		
		String crcString = new String();
		//pads the right of the CRC with the necessary zeros
		int numZeros = Integer.numberOfLeadingZeros(calculatedCRC)/4;
		for(int j = 0; j < numZeros; j++)
			crcString += "0";
		crcString += Integer.toHexString(calculatedCRC);
		
		//prints the CRC at the end of the text
		System.out.println(crcString + " - " + crcString + "\n");	
		
		System.out.println("CRC16 result : " + crcString);
	}
	
	public static void verifyAndPrintCRC(String text)
	{
		System.out.println("CRC 16 calculation progress: \n");
		
		//calculates the first seven lines of CRC and prints it to the screen
		int calculatedCRC = 0;
		String substring;
		for(int i = 0; i < 7; i++)
		{
			//calculates the CRC
			substring = text.substring(i*64, (i+1)*64);
			calculatedCRC = calculateLineCRC(substring, calculatedCRC);
			
			//prints the necessary text to the screen
			System.out.print(substring + " - ");
			//pads the right of the CRC with the necessary zeros
			int numZeros = Integer.numberOfLeadingZeros(calculatedCRC)/4;
			for(int j = 0; j < numZeros; j++)
				System.out.print("0");
			//prints the CRC at the end of each line
			System.out.println(Integer.toHexString(calculatedCRC));
		}
		
		//calculates the final CRC
		substring = text.substring(448, 504);
		calculatedCRC = calculateLineCRC(substring, calculatedCRC);
		
		//prints the necessary text to the screen
		System.out.print(text.substring(448, 512));
		
		String crcString = new String();
		//pads the right of the CRC with the necessary zeros
		int numZeros = Integer.numberOfLeadingZeros(calculatedCRC)/4;
		for(int j = 0; j < numZeros; j++)
			crcString += "0";
		crcString += Integer.toHexString(calculatedCRC);
		
		//prints the CRC at the end of the text
		System.out.println(" - " + crcString + "\n");	
		
		System.out.println("CRC16 result : " + crcString + "\n");
		
		//checks if CRC verification passes or fails
		System.out.print("CRC 16 verification ");
		if(crcString.equals(text.substring(504, 512)))
			System.out.println("passed");
		else 
			System.out.println("failed");
	}
	
	public static int calculateLineCRC(String s, int calculatedCRC)
	{
		int letterValue;
		
		for(int i = 0; i < s.length(); i+=2)
		{
			//gets the value of the next two letters and makes them into a 16 bit integer
			letterValue = s.charAt(i);
			letterValue <<= 8;
			letterValue |= s.charAt(i+1);
			
			letterValue = calculateXOR(letterValue, calculatedCRC<<1);
			
			calculatedCRC = calculateCRC(letterValue);
		}
		
		
		return calculatedCRC;
	}
	
	public static int calculateCRC(int sixteenBits)
	{
		//16 bit CRC polynomial
		int crcPoly = 0b1010000001010011;

		//pads the letterValue by 15, since this is a CRC-16
		int crcResult = sixteenBits << 15;
		//calculates the number of leading zeros
		int numOfZeros = Integer.numberOfLeadingZeros(crcResult);
		
		int xorCRCValue;
		
		//continues using xor until there are at least 17 leading zeros,
		//there are only 15 bits left
		while(numOfZeros < 17)
		{
			//lines up the CRC polynomial with the first 1
			xorCRCValue = crcPoly << (16-numOfZeros);
			//XOR them to get the CRC result
			crcResult = calculateXOR(crcResult, xorCRCValue);
			
			//recalculates the number of leading zeros
			numOfZeros = Integer.numberOfLeadingZeros(crcResult);
		}
		
		return crcResult;
	}
	
	public static int calculateXOR(int a, int b)
	{
		return a ^ b;
	}
	
	public static String getInputText(String fileName) throws Exception
	{
		File file = new File(fileName);
		BufferedReader br;
		
		String text = new String();
		String buffer = new String();
		
		try
		{
			br = new BufferedReader(new FileReader(file));
			//puts contents of the file into the inputBuffer
			while((buffer = br.readLine()) != null)
				text += buffer;
				
			//closes the RufferedReader
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//ensures all characters are ascii characters
		for(int i = 0; i < text.length(); i++)
		{
			if(text.charAt(i) >= 128)
				throw new Exception();
		}
		
		return text;
	}
}
