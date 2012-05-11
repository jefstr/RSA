///
/// Contents: RSA Driver.
/// Author:   Jeff Parvin
/// Date:     21 Feb 2012
///

import java.math.BigInteger ;
import java.util.Random ;

public class TestRSA {

	public static void main(String[] args) {

		///
		/// Test Result of a^b modm
		///
		
		System.out.println("===============================\n= Testing RSA Implementation =\n===============================\n");
		
		System.out.println("Test 1: COMPARE TO JAVA'S BIGINTEGER METHODS:\n------------------------------------------------") ;

		String a = "1011111" ;
		String b = "11111101010" ;
		String c = "110101" ;

		BigInteger aBig = new BigInteger(a,2) ;
		BigInteger bBig = new BigInteger(b,2) ;
		BigInteger cBig = new BigInteger(c,2) ;

		System.out.println("  Java's a^b mod c = " + aBig.modPow(bBig,cBig).toString(2)) ;
		System.out.println("  My a^b mod c = " + RSA.crypto(a,b,c)) ;

		///
		/// Test Encryption/Decryption
		///
		System.out.println("\nTest 2: TEST ENCRYPTION/DECRYPTION:\n------------------------------------------------") ;
	    System.out.println("Generating keys... ") ;
	    
	    String p = "11011011000001100111110011010010101111100001100100101111111100001101110010000001110111000101011111001000100111011110101101101000101100011110111011111011111110000011000110101001010110001011011000111001" ;
	    String q = "11111110011110110101011111001101011110001010100110110100111110111101011101100000001011110011011101000000010110011000011101001101100111100011011110000001100001011000001111101001000011010110010100010111" ;
	    String N = "1101100110111001111101110001011000010001101110000010001110101011000001001001100110100100111000011001010111101011010100110011111000001111001101011100000101110111100010011001110111101111101111110011111111111110010101111110010010101101000001011011110011111100110101110001101001101000001110101111010100001011010111111110100111100010011000111000000000000000000101111100110001011000110000011101110000011111" ;
	    String e = "111" ;
	    String phi = "1101100110111001111101110001011000010001101110000010001110101011000001001001100110100100111000011001010111101011010100110011111000001111001101011100000101110111100010011001110111101111101111110011111000100100110101100001000000001100110011101111101000010111111010100110011010000110001011110110011000000010011010000111011100101100000100110101100110000010100110100001011011000110010110111100000011010000" ;
	    String d = "101110101001111101100110000100101110101010011101110101010110111000000011111100010110100011000001010110111110111001000111010110011100001111100100111011101111100010111111000110011010100011101101000100001011000111011100000011011100000111010101111110101110111111101101011111000111001100000100000011100100101100110100111110000110111011101100000000111001010010000100000100111000010101110011001101111000111" ;
	    
	    System.out.println("  p = " + p) ;
	    System.out.println("  q = " + q) ;
	    System.out.println("  N = " + N) ;
	    System.out.println("  e = " + e) ;
	    System.out.println("  phi = " + phi) ;
	    System.out.println("  d = " + d) ;
	    
	    String message = "Attack at dawn." ;
	    System.out.println("\nOriginal Message = " + message) ;
	    
	    String clearBits = "1" + convertStringToBits(message) ;
	    System.out.println("  Clear bits = " + clearBits) ;
	    
	    System.out.println("\nEncrypting Message... ") ;
	    String encryptedBits = RSA.crypto(clearBits,e,N) ;
	    System.out.println("  Encrypted bits = " + encryptedBits) ;

	    System.out.println("\nDecrypting Message... ") ;
	    String decryptedBits = RSA.crypto(encryptedBits,d,N) ;
	    System.out.println("  Decrypted bits = " + decryptedBits) ;
	    message = convertBitsToString(decryptedBits.substring(1)) ;
	    System.out.println("  Decrypted Message = " + message) ;

		///
		/// Test Runtime of a^b modm
		///

		System.out.println("\nTest 3: TEST RUNTIME of a^b modm:\n------------------------------------------------") ;

		Random randGen = new Random();

		BigInteger randMessage;
		BigInteger randExponent;
		BigInteger randModulus;

		long product;
		long start;
		long runtime;

		for(int i=350; i<=400; i++){

			randMessage = new BigInteger(i, 1, randGen);			// generate random numbers of i bits
			randExponent = new BigInteger(i, 1, randGen);
			randModulus = new BigInteger(i, 1, randGen);
			product = randMessage.bitLength()*randExponent.bitLength()*randModulus.bitLength();		

			//System.out.println(randMessage.toString(2));
			//System.out.println(randMessage.bitLength()+"  "+randExponent.bitLength()+"  "+randModulus.bitLength());

			start =System.nanoTime();
			RSA.crypto(randMessage.toString(2), randExponent.toString(2), randModulus.toString(2));
			runtime = System.nanoTime() - start;

			System.out.println("Bit Lengths = "+ i +":  "+ "\n\truntime: "+runtime +" nanoseconds \n\tproduct: "+ product +"\n\truntime / product: "+ (double)runtime/product);
		}
		
	}



	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// CONVERSION METHODS FOR KEY GEN
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String convertCharToBits(char c) {
		String result = "" ;
		int n = (int)c ;
		for (int bit=15 ; bit>=0 ; bit--) {
			if ( n>=Math.pow(2,bit) ) { result+="1" ; n-=Math.pow(2,bit) ; }
			else { result+="0" ; }
		}
		return result ;
	}

	public static String convertStringToBits(String s) {
		String result = "" ;
		for (int i=0 ; i<s.length() ; i++) {
			result += convertCharToBits(s.charAt(i)) ;
		}
		return result ;
	}

	public static char convertBitsToChar(String bits) {
		int result = 0 ;
		for (int bit=0 ; bit<16 ; bit++) {
			if ( bits.charAt(bit)=='1' ) { result += Math.pow(2,15-bit) ; }
		}
		return (char)result ;
	}

	public static String convertBitsToString(String bits) {
		String result = "" ;
		String block ;
		while (bits.length()!=0) {
			block = bits.substring(0,16) ;
			bits = bits.substring(16) ;
			result += convertBitsToChar(block) ;
		}
		return result ;
	}

}

/// End-of-File

