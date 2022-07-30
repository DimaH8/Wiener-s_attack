
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Second_attack {
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
    // Prime random numbers generation
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	static Random gRandomGen = new Random();
	
	public static BigInteger generateRandomNumber(int numBits) {
		//System.out.println("number of bits = " + numBits);
		// Constructs a randomly generated BigInteger, uniformly distributed over the range 0 to (2^numBits - 1), inclusive.
		return new BigInteger(numBits, 10, gRandomGen);
	}
	
	// Test Miller-Rabin
    public static boolean testPrimeNumber(BigInteger p) {       
        // step 0
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);
        ///System.out.println("Test Miller-Rabin: Step 0:");
        ///System.out.println("Test Miller-Rabin: p - 1 = " + pMinus1.toString());
        // find s
        int s = 0;
        // divide by 2
        while (pMinus1.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
        	s++;
        	pMinus1 = pMinus1.divide(BigInteger.TWO);
        }
        BigInteger d = pMinus1;
        ///System.out.println("Test Miller-Rabin: d = " + d.toString());
        ///System.out.println("Test Miller-Rabin: s = " + s);
        
        pMinus1 = p.subtract(BigInteger.ONE); // refresh value
        
        for (int k = 1; k < 10; k++) {
	        // step 1
	        ///System.out.println("Test Miller-Rabin: step 1, k = " + k);
	        BigInteger x = generateRandomNumber(p.bitLength());
	        
	        if (x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE) || x.equals(pMinus1) || x.compareTo(p) >= 0) {
//	        	System.out.println("Test Miller-Rabin 1: bad number - generate one more time");
	        	continue;
	        }
	        
	        BigInteger resGcd = x.gcd(p);
	        if (!resGcd.equals(BigInteger.ONE)) {
//	        	System.out.println("Test Miller-Rabin 1: number failed - not prime");
	        	return false;
	        }
	        
	        // step 2
	        ///System.out.println("Test Miller-Rabin: step 2");
	        BigInteger x_r = x.modPow(d, p);
	        //System.out.println("Test Miller-Rabin 2: x_r = " + x_r.toString());
	        // step 2.1
	        if (x_r.equals(BigInteger.ONE) || x_r.equals(pMinus1)) {
	        	//System.out.println("Test Miller-Rabin 2.1: number is pseudosimple : x^d = +-1(mod p)");
	        } else {
		        // step 2.2
		        for (int r = 1; r < s; r++) {
		        	x_r = x_r.modPow(BigInteger.TWO, p);
		        	
		        	if (x_r.equals(pMinus1)) {
			        	///System.out.println("Test Miller-Rabin 2.2: number is pseudosimple : x^(d*2^r) = -1(mod p)");
			        	continue;
		        	}
		        	
		        	if (x_r.equals(BigInteger.ONE)) {
		        		///System.out.println("Test Miller-Rabin 2.2: number failed - not prime, r = " + r);
		        		return false;
		        	}
		        }
		        ///System.out.println("Test Miller-Rabin: number failed - not prime. Step 2.1 and 2.2 failed");
		        ///System.out.println("Test Miller-Rabin: x_r = " + x_r.toString());
		        return false;
	        }
	    
        }
        
        return true;
    }
	public static BigInteger generatePrimeNumber(int numBits) {
		BigInteger newRndNumber = BigInteger.TWO; // just to avoid errors - set NOT prime number 
		boolean isPrime = false;
		
		while (isPrime == false) {
			newRndNumber = generateRandomNumber(numBits);
//			System.out.println("generatePrimeNumber: posible prime number " + newRndNumber.toString(16));
			isPrime = testPrimeNumber(newRndNumber);
		}
		//System.out.println("generatePrimeNumber: new random prime number = " + newRndNumber.toString(16));
		return newRndNumber;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
    // RSA functions
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
  
    public static ArrayList<BigInteger> WinnerGenerateKeyPair(BigInteger p, BigInteger q) {
        BigInteger n = p.multiply(q);
        BigInteger funOylera = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        //BigInteger e = BigInteger.TWO.pow(16).add(BigInteger.ONE);
        BigInteger e = null, d = null;
        
      
        while (true) {
         int numbits = (int) (Math.random()*(n.bitLength()/4 - 2) + 2);

         d = generatePrimeNumber(numbits);
         
         if (d.gcd(funOylera).equals(BigInteger.ONE) == true && (BigInteger.valueOf(36).multiply(d.pow(4)).compareTo(n)) == -1) {
          // nothing here
         } else {
          // need to re-generate d
          continue;
         }
         /* 
         System.out.println("d don't fit condition d < (1/sqrt(6))*n^(1/4)");
         System.out.println("d            = " + d);
         System.out.println("temp2 = " + numbits);
         */
         e = d.modInverse(funOylera);
         //check gcd (e, funOylera)
         if (e.gcd(funOylera).equals(BigInteger.ONE) == false) {
//       System.out.println("GenerateKeyPair: gcd (e, funOylera) != 1 !!!!!");
          continue;
         }
         break;
        }
        ArrayList<BigInteger> keys = new ArrayList<BigInteger>();
        keys.add(n); // index 0 - public Key
        keys.add(e); // index 1 - public Key
        keys.add(d); // index 2 - private Key
        return keys;
        
       }
    
    public static BigInteger Encrypt(BigInteger M, BigInteger pubKeyE, BigInteger n) {
        return M.modPow(pubKeyE, n);
    }

    public static BigInteger Decrypt(BigInteger C, BigInteger privKeyD, BigInteger n) {
        return C.modPow(privKeyD, n);
    }
   
    public static void wienner_attack(BigInteger e, BigInteger n) {
    	BigInteger frac = BigInteger.ONE;
    	
		BigInteger r1 = e;
		BigInteger r2 = n;
		
    	ArrayList<BigInteger> array = new ArrayList<BigInteger>(); 
   
    	for ( ; !frac.equals(BigInteger.ZERO); ) {

    		BigInteger cf = r1.divide(r2);
        	frac = r1.mod(r2);
        	array.add(cf);
        	r1 = r2;
        	r2 = frac;
        //	System.out.println("cf = " + cf);
        //	System.out.println("r1 = " + r1);
        //	System.out.println("r2 = " + r2);
    	}
    	
    	 
    	// step 2
    	ArrayList<BigInteger> P = new ArrayList<BigInteger>();
    	ArrayList<BigInteger> Q = new ArrayList<BigInteger>();
    	// add p0, q0
    	P.add(array.get(0));
    	Q.add(BigInteger.ONE);
    	// add p1, q1
    	P.add(array.get(1).multiply(P.get(0)).add(BigInteger.ONE));
    	Q.add(array.get(1).multiply(Q.get(0)).add(BigInteger.ZERO));
    	// add p2..pn, q2..qn
    	for (int i = 2 ; i < array.size(); i++) {
    		P.add(array.get(i).multiply(P.get(i - 1)).add(P.get(i - 2)));
        	Q.add(array.get(i).multiply(Q.get(i - 1)).add(Q.get(i - 2)));
    	}
    /*	
    	for (int i = 0; i < P.size(); i++) {
    		 System.out.println("P = " + P.get(i));
    		 System.out.println("Q = " + Q.get(i));
    		 System.out.println();
    	}
   */
    	
    	//step 3
    	boolean found = false;
    	for ( int i = 1; i < P.size(); i++) {
    		// 3.1
	    	BigInteger k = P.get(i);
	    	BigInteger d = Q.get(i);
	    //	System.out.println("k = " + k);
    	//	System.out.println("d = " + d);
	    	// 3.2
	    	if (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
	    //		System.out.println("3.2: k = " + k);
	    //		System.out.println("3.2: d = " + d);
	    //		System.out.println();
	    		continue;
	    	}
	    	// 3.3
	    	if (!k.equals(BigInteger.ONE) && !e.multiply(d).mod(k).equals(BigInteger.ONE)) {
	    //		System.out.println("3.3: k = " + k);
	    //		System.out.println("3.3: d = " + d);
	    //		System.out.println();
	    		continue;
	    	}
	    	// 3.4
	    	BigInteger phi = e.multiply(d).subtract(BigInteger.ONE).divide(k);
	    	// x^2 - (n - phi + 1)x + n == x^2 + bx + c
	    	// b = (phi - n - 1)
	    	// c = n
	    	BigInteger b = phi.subtract(n).subtract(BigInteger.ONE);
	    	BigInteger c = n;
	    	BigInteger D = b.multiply(b).subtract(BigInteger.valueOf(4).multiply(c));
	    	if (D.compareTo(BigInteger.ZERO) == -1) {
	  //  		System.out.println("3.4: D = " + D);
	  //  		System.out.println("3.4: k = " + k);
	  //  		System.out.println("3.4: d = " + d);
	  //  		System.out.println();
	    		continue;
	    	}
	    	BigInteger D_sqrt = D.sqrt();
	    	if (!D_sqrt.multiply(D_sqrt).equals(D)) {
	   // 		System.out.println("3.4 sqrt: D = " + D);
	   // 		System.out.println("3.4 sqrt: k = " + k);
	   // 		System.out.println("3.4 sqrt: d = " + d);
	   // 		System.out.println();
	    		continue;
	    	}
	    	BigInteger x1 = BigInteger.ZERO.subtract(b).add(D.sqrt());
	    	if (x1.mod(BigInteger.TWO).equals(BigInteger.ONE)) {
	  //  		System.out.println("3.4 x1: D = " + D);
	  //  		System.out.println("3.4 x1: k = " + k);
	  //  		System.out.println("3.4 x1: d = " + d);
	   // 		System.out.println();
	    		continue;
	    	}
	    	BigInteger x2 = BigInteger.ZERO.subtract(b).subtract(D.sqrt());
	    	if (x2.mod(BigInteger.TWO).equals(BigInteger.ONE)) {
	    //		System.out.println("3.4 x2: D = " + D);
	   // 		System.out.println("3.4 x2: k = " + k);
	   // 		System.out.println("3.4 x2: d = " + d);
	   // 		System.out.println();
	    		continue;
	    	}
	    	x1 = x1.divide(BigInteger.TWO);
	    	x2 = x2.divide(BigInteger.TWO);
	    	found = true;
//	    	System.out.println("found x1: " + x1);
//	    	System.out.println("found x2: " + x2);
	    	break;
    	}
    	
    	if (!found) {
    		System.out.println("Given RSA parameters are not vulnerable to Wiener's attack");
    	}
    }
    
	public static void main(String[] args) {
		
		int lenght;
		for (lenght = 32; lenght < 4097; lenght = lenght*2) {
			if (lenght == 4096) { lenght = 3072;}
			double count2 = 0;
			for (int j = 0; j < 500; j++ ) {
				// Generate p,q
				BigInteger p, q;
				while (true) {
					p = generatePrimeNumber(lenght/2);
					q = generatePrimeNumber(lenght/2);	
//					System.out.println("p: " + p);
//					System.out.println("q: " + q);
					int temp = q.compareTo(p);
					int temp2 = p.compareTo(BigInteger.TWO.multiply(q));
					if (temp == -1 && temp2 == -1) { 
//						System.out.println("Generated p i q. lenght = " + lenght);
						break;
					}
				} 
//				BigInteger p = new BigInteger("8e4efc3e972aa169a40d5dc16edd7c7f", 16);
//				BigInteger q = new BigInteger("f44bf0ab9a9f1a45287dfc466b06deb7", 16);
				
//				System.out.println("");
//				System.out.println("");
				ArrayList<BigInteger> keys = WinnerGenerateKeyPair(p, q);
				BigInteger pubKey_n = keys.get(0);
				BigInteger pubKey_e = keys.get(1);
				BigInteger privKey_d = keys.get(2);
	//			System.out.println("RSA: private key part: p  = " + p.toString(16));
	//			System.out.println("RSA: private key part: q  = " + q.toString(16));
	//			System.out.println("RSA: private key: d  = " + privKey_d.toString(16));
	//			System.out.println("RSA: public key: e  = " + pubKey_e.toString(16));
	//			System.out.println("RSA: public key: n  = " + pubKey_n.toString(16));
				
				double count = 0;
				for (int i = 0; i < 100; i++ ) { 
					double startTime = System.nanoTime();
					wienner_attack(pubKey_e, pubKey_n);
					double elapsedNanos = System.nanoTime() - startTime;
	//				System.out.println("Time of one work in nanoseconds: " + elapsedNanos);
					count = count + elapsedNanos;
				}
				double average_one_lenght = count/100;
//				System.out.println("Average specific lenght time in nanoseconds of attack: " + average_one_lenght);
				count2 = count2 + average_one_lenght;
				//wienner_attack(new BigInteger("17993", 10), new BigInteger("90581", 10));
				//wienner_attack(new BigInteger("1073780833", 10), new BigInteger("1220275921", 10));
				//wienner_attack(new BigInteger("1779399043", 10), new BigInteger("2796304957", 10));
			}
			double average = count2/500;
			System.out.println("For lenght " + lenght + " Average time in nanoseconds of attack: " + average);
		}		
//		System.out.println("Average time in nanoseconds of attack: " + average);
	}
}

