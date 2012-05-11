import java.lang.Math;
import java.util.*;

//////////////////////////////////////////////////////////////////////////////////////////
/// Contents: RSA encryption/decryption
/// Author:   Jeff Parvin
/// Date:     21 Feb 2012
/////////////////////////////////////////////////////////////////////////////////////////

public class RSA {

	// BigInt zero and one (for certain operations)
	private static int[] bigZero = {0};
	private static int[] bigOne = {1};


	///////////////////////////////////////////////////////////////////////////////////////
	/// Crypto Method - calls ModExp on arguments and returns result in String
	///////////////////////////////////////////////////////////////////////////////////////
	
	public static String crypto(String m, String e, String N){
		int[] message = bigInt(m);
		int[] exponent = bigInt(e);
		int[] modulus = bigInt(N);
		
		return bigIntToString(modexp(message, exponent, modulus));

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	/// Arithmetic Methods - Add, Subtract, Mult, Mod, and ModExp
	///////////////////////////////////////////////////////////////////////////////////////

	/// Modular Exponentiation Method
	public static int[] modexp(int[] base, int[] exp, int[] m){
		int[] result = new int[1];
		result[0]=1;		

		while(!(exp.length==1 && exp[0]==0)){		//while exp>0
			if(exp[0]==1){
				result = mod(mult(result,base),m);
			}
			exp = add(exp,bigZero,-1,0);	// shift exp right
			base = mod(mult(base,base),m);			
		}
		return result;
	}

	/// Mod Method
	public static int[] mod(int[] a, int[] m){
		int[] result = {0};
		int[] current = {1};		// 2^0 modm for first loop

		for(int i=0;i<a.length;i++){
			
			// if bit is 1, add to accumulator
			if(a[i]==1){	
				result = add(result,current);
				if(compare(result,m)>=0){			// if result >= m, subtract m
					result = sub(result, m);
				}
			}
			
			// calc new current
			if(compare(current,m,1,0)==-1){
				current = add(current,bigZero,1,0);		//return 2r - use add with shift instead?
			}
			else{
				current = sub(current,m,1,0);		//return 2r-m - use sub with shift
			}
		}

		return result;
	}

	///	Add Methods (w/ and w/o shifts)
	public static int[] add(int[] a, int[] b){
		return add(a,b,0,0);
	}

	public static int[] add(int[] a, int[] b, int aShift, int bShift){
		int[] result = new int[Math.max(a.length+aShift, b.length+bShift)+1];

		int aBit;
		int bBit;
		int carry = 0;

		for(int i=0; i<result.length; i++){

			// zero extend a
			if(i>=a.length+aShift || i<aShift) aBit=0;
			else aBit=a[i-aShift];

			// zero extend b
			if(i>=b.length+bShift || i<bShift) bBit=0;
			else bBit=b[i-bShift];

			// addition rules
			if(aBit+bBit+carry==0){
				result[i]=0;
				carry=0;
			}

			else if(aBit+bBit+carry==1){
				result[i]=1;
				carry=0;
			}

			else if(aBit+bBit+carry==2){
				result[i]=0;
				carry=1;
			}

			else if(aBit+bBit+carry==3){
				result[i]=1;
				carry=1;
			}

			else{
				System.out.println("ERROR: ADDITION ALGORITHM FAILURE");
				System.exit(0);
			}

		}

		// trim MSB if zero
		if(result[result.length-1]==0){		//needs trim
			return trim(result);
		}

		else{
			return result;
		}
	}

	///	Sub Methods (w/ and w/o shifts)
	public static int[] sub(int[] a, int[] b){			// a must be bigger than b in current iteration
		return sub(a,b,0,0);
	}

	public static int[] sub(int[] a, int[] b, int aShift, int bShift){
		int[] result = new int[Math.max(a.length+aShift, b.length+bShift)+1];

		int aBit;
		int bBit;
		int borrow = 0;

		for(int i=0; i<result.length; i++){

			// zero extend a
			if(i>=a.length+aShift || i<aShift) aBit=0;
			else aBit=a[i-aShift];

			// zero extend b
			if(i>=b.length+bShift || i<bShift) bBit=0;
			else bBit=b[i-bShift];

			// subtraction rules
			if((aBit-borrow)-bBit==0){
				result[i]=0;
				borrow=0;
			}

			else if((aBit-borrow)-bBit==-1){
				result[i]=1;
				borrow=1;
			}

			else if((aBit-borrow)-bBit==1){
				result[i]=1;
				borrow=0;
			}

			else if((aBit-borrow)-bBit==-2){
				result[i]=0;
				borrow=1;
			}

			else{
				System.out.println("ERROR: SUBTRACTION ALGORITHM FAILURE");
				System.exit(0);
			}

		}

		// trim MSB if zero
		if(result[result.length-1]==0){			//needs to be trimmed
			return trim(result);
		}

		else{
			return result;
		}
	}

	///	Mult Method
	public static int[] mult(int[] a, int[] b){
		int[] result = new int[a.length+b.length];
		int bBit;

		for(int i=0; i<b.length; i++){
			bBit = b[i];

			if(bBit==1){
				result = add(result,a,0,i);	
			}
		}

		if(result[result.length-1]==0){
			return trim(result);
		}

		else{
			return result;
		}

	}

	///////////////////////////////////////////////////////////////////////////////////////
	/// Accessory Methods - BigInt Compare and Trim
	///////////////////////////////////////////////////////////////////////////////////////

	/// Comparison Methods (w/ and w/o shifts)
	public static int compare(int[] a, int[] b){
		return compare(a,b,0,0);
	}

	public static int compare(int[] a, int[] b, int aShift, int bShift){		//returns 1 if a>b, 0 if equal, -1 if a<b

		if(a.length+aShift>b.length+bShift){						
			return 1;
		}
		else if(a.length+aShift<b.length+bShift){	
			return -1;
		}

		else{	//length is same

			for(int i=a.length-1+aShift;i>=0;i--){	
				int aBit;
				int bBit;

				if(i<aShift) aBit=0;
				else aBit=a[i-aShift];
				if(i<bShift) bBit=0;
				else bBit=b[i-bShift];

				if(aBit>bBit){
					return 1;
				}

				if(bBit>aBit){
					return -1;
				}
			}
			return 0;		//made it all the way through loop with indentical bits!
		}
	}

	///	Trim Method
	public static int[] trim(int[] a){
		int trimCount = 0;

		for(int i=a.length-1;i>0;i--){
			int aBit = a[i];

			if(aBit==0){
				trimCount++;
			}
			else if(aBit==1){
				break;
			}
			else{
				System.out.println("ERROR: TRIM METHOD FAILURE");
				System.exit(0);
			}
		}

		int[] aTrim = new int[a.length-trimCount];

		for(int i=0;i<aTrim.length;i++){
			aTrim[i] = a[i];
		}
		return aTrim;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	/// Static version Big Integer (stores number as array of 0 or 1 ints)
	///////////////////////////////////////////////////////////////////////////////////////
	
	/// BigInt Method - creates int array from string
	public static int[] bigInt(String binaryStr){
		int[] intArray = new int[binaryStr.length()];
		int len = binaryStr.length();

		for(int i=0; i<len;i++){
			intArray[i] = Integer.parseInt(binaryStr.substring(len-i-1,len-i));
			//System.out.println("index "+i+": "+intArray[i]);
		}

		return intArray;
	}

	/// BigInt toString Method - creates string from array of ints
	public static String bigIntToString(int[] intArray){
		String intStr="";
		int len = intArray.length;

		for(int i=len-1;i>=0;i--){
			intStr+=intArray[i];
		}

		return intStr;
	}

} // End of File
