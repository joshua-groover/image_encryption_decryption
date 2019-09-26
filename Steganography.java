/*-----------------------------------------------------------------------------
GWU CSCI1112 Fall 2019
author: Joshua Groover

This class encapsulates the logic necessary to perform simple steganography 
cyphering.
------------------------------------------------------------------------------*/
import java.awt.*;
import java.awt.image.*;
import java.lang.*;
import java.util.*;

public class Steganography {

    //------------------------------------------------------------------------- 
    // Base Problems
    //------------------------------------------------------------------------- 
    /// Performs a deep copy of the input pixels and returns the copy
    /// @param px the pixels from an image to copy
    /// @return the deep copy of the pixels that were copied
    public static int[][][] copy(int[][][] px) {
        //TODO: IMPLEMENT
	    
	//create a new 3d array with 1std same as px
	int[][][] copy = new int[px.length][][];

	//iterate through each of the 1stds, create 2ndD equal to each px 2nd
	for (int i=0; i<px.length; i++){
	    copy[i] = new int[px[i].length][];

	    //iterate through 2ndDs, create 3rdD equal to each px 3rd
	    for (int j=0; j<px[i].length; j++){
		copy[i][j] = new int[px[i][j].length];

		//iterate through each pixel and hard copy it primitivaley
		for (int k=0; k<px[i][j].length; k++){
		    int temp = px[i][j][k];
		    copy[i][j][k] = temp;
		}
	    }
	}
        return copy;
    }

    //------------------------------------------------------------------------- 
    /// Computes the error in the individual color channels (RGB only) between 
    /// a pixel in the key image and a pixel in the cypher image.
    /// @param keyPixel An array containing ARGB values that represents a pixel
    ///        in the key image    
    /// @param cypherPixel An array containing ARGB values that represents a 
    ///        pixel in the cypher image
    /// @return An array containing the error (positive values only) between 
    ///         the RGB channels of the input pixels. 
    public static int[] colorError( int[] keyPixel, int[] cypherPixel ) {
        //TODO: IMPLEMENT

	//create the error array (l=3 for each color val)
	int[] error = new int[3];

	//initialize dif
	int dif = 0;

	//iterate through rgb vals, find elementwise dif, add to error []
	for (int i=1; i<keyPixel.length; i++){
	    dif = keyPixel[i] - cypherPixel[i];
	    if (dif<0)
		dif = dif*(-1);
	    error[i-1] = dif;
	}
	return error;
    }

    //------------------------------------------------------------------------- 
    /// Computes the RGB error based on the position of a character in the 
    /// alphabet.  The error is represented using the same value in each
    /// cell of the array.
    /// @param chpos The characters ordinal position in the alphabet
    /// @return an array of three values that represents the error to introduce
    ///         into to a color 
    public static int[] positionToError( int chpos ) {
        //TODO: IMPLEMENT
	    
	//create and return array of the chpos+1 (error val)
	int error_val = chpos + 1;;
	int[] error = {error_val, error_val, error_val};
	return error;
    }

    //------------------------------------------------------------------------- 
    /// Computes the ordinal position of a character based on the error uniform
    /// represented in all cells in an input array of three values.
    /// @param error An array of RGB values (Note that this excludes the alpha 
    ///        channel).  
    /// @return The ordinal position of a character in the alphabet based on
    ///         the amount of error in the input 
    public static int errorToPosition( int[] error ) {
        //TODO: IMPLEMENT

	//chpos is error-1 (opposite of posToError), element error same for all
	//pos of error, return error[0]-1
	int chpos = error[0]-1;
        return chpos;
    }
    //------------------------------------------------------------------------- 
    /// Encrypts a string of characters into a copy of the key image
    /// @input s the string of characters to encrypt
    /// @input pxKey the image to encrypt the string into
    /// @return the encrypted image
    public static int[][][] encrypt(String s, int[][][] pxKey) {
        //TODO: IMPLEMENT
        
	//make deep copy of the array
	int[][][] pxCopy = copy(pxKey);

	//initialize things
	int char_pos = 0;
	int char_val = 0;

	//find the start num of the alphabet for ref
	int start_alpha = (int) 'a';

	//convert the string to lower
	s = s.toLowerCase();

	//iterate through every tenth row and col
	for (int row=0; row<pxCopy.length; row = row+10){
	    for (int col=0; col<pxCopy[row].length; col = col+10){

		//find the ordinal val of the char in s at pos char_pos
		char_val = (int) s.charAt(char_pos) - start_alpha;

		//convert ordinal val to encoded array
		int[] le_code = positionToError(char_val);

		//iterate through RGB vals, shift by error in le_code
		for (int pixel_val=1; pixel_val<4; pixel_val++){
		    int org_val = pxCopy[row][col][pixel_val];
		    int error_val = le_code[pixel_val-1];
		    pxCopy[row][col][pixel_val] = org_val - error_val;
		}

		//increment the char_pos in s
		char_pos++;
		
		//if no more letters in string, stop encrypting
		if (char_pos > s.length()-1)
		    break;    
	    }

	    //if no more letters in string, stop encrypting
	    if (char_pos > s.length()-1)
		break;
	} 
	return pxCopy;
    }

    //------------------------------------------------------------------------- 
    /// Decrypts a string of characters encoded into an image by comparing
    /// pixels in the cypher with the key image
    /// @input pxCypher the encrypted image containing the message
    /// @input pxKey the key image that was used for the encryption
    /// @return the decrypted string of characters
    public static String decrypt(int[][][] pxCypher, int[][][] pxKey) {
        //TODO: IMPLEMENT

	//initiaize things
        String s = "";
	int dif = 0;
	int alpha_number = 0;
	int dif_vec[] = new int[3];
	
	//find val for start of alphabet for ref
	int start = (int) 'a';
	
	//iterate through every tenth row, every tenth column
	for (int row=0; row<pxCypher.length; row=row+10){
	    for (int col=0; col<pxCypher[row].length; col=col+10){
		
		//look through the RGB vals, find the dif btween org and cyp
		dif_vec = colorError(pxKey[row][col], pxCypher[row][col]);

		//if val of error 0, message finished, exit decryption
		if (dif_vec[0]==0)
		    break;

		//convert error to ACSII char val, add to s
		alpha_number = errorToPosition(dif_vec) + start;
		s = s + (char) alpha_number;
	    }

	    //if error val 0, message finished, exit decryption
	    if (dif_vec[0]==0)
		break;
	}
	
	//convert message to upper case
	s = s.toUpperCase();
	return s;
    }

    //------------------------------------------------------------------------- 
    // Extension Problems
    //------------------------------------------------------------------------- 
    /// Computes the RGB error based on the position of a character in the 
    /// alphabet.  The error is spread across each cell in the array.
    /// @param chpos The characters ordinal position in the alphabet
    /// @return an array of three values that represents the error to introduce
    ///         into to a color 
    public static int[] positionToError2( int chpos ) {
        //TODO: IMPLEMENT

	//break chpos into two elements, int(chpos/2) and rem(chpos/2)
	int num = (chpos+1)/3;
	int rem = (chpos+1)%3;

	//initialize error array
	int[] error = new int[3];
	
	//if num>0, chpos/3 is greater than 2, add chpos/3 to each error elem
	if (num>0){
	    for (int i=0; i<3; i++){
		error[i] = num;
	    }
	}

	//add 1 to the indexs from 0-(rem-1)
	for (int i=0; i<rem; i++){
	    error[i]++;
	}
        return error;
    }

    //------------------------------------------------------------------------- 
    /// Computes the ordinal position of a character based on the error spread
    /// across different cells in an input array of three values.
    /// @param error An array of RGB values (Note that this excludes the alpha 
    ///        channel).  
    /// @return The ordinal position of a character in the alphabet based on
    ///         the amount of error in the input 
    public static int errorToPosition2( int[] error ) {
        //TODO: IMPLEMENT

	//sum the error elements to get chpos
	int chpos = -1;
	for (int i=0; i<error.length; i++) 
	   chpos = chpos + error[i];
        return chpos;
    }

    //------------------------------------------------------------------------- 
    /// Encrypts a string of characters into a copy of the key image
    /// @input s the string of characters to encrypt
    /// @input pxKey the image to encrypt the string into
    /// @return the encrypted image
    public static int[][][] encrypt2(String s, int[][][] pxKey) {
        //TODO: IMPLEMENT

	//convert string to lowercase
	s = s.toLowerCase();

	//create a deep copy of the image
	int[][][] pxCopy = copy(pxKey);

	//initialize things
        int char_pos = 0;
        int char_val = 0;

	//make reference value for int val of first char in alphabet
        int start_alpha = (int) 'a';

	//iterate through the tenth row, and tenth columns of the row
        for (int row=0; row<pxCopy.length; row = row+10){
            for (int col=0; col<pxCopy[row].length; col = col+10){

		//find ordinal char val of the char in s at pos char_pos
		char_val = (int) s.charAt(char_pos) - start_alpha;

		//create encoded array from letter
                int[] le_code = positionToError2(char_val);

		//enter the code as the original val - code on the img copy
                for (int pixel_val=1; pixel_val<4; pixel_val++){
                    int org_val = pxCopy[row][col][pixel_val];
                    int error_val = le_code[pixel_val-1];
                    pxCopy[row][col][pixel_val] = org_val - error_val;
                }

		//move to next char in s
                char_pos++;

		//if no more letters left, stop encryption
                if (char_pos > s.length()-1)
                    break;
            }

	    //if no more letters in s, stop encryption
            if (char_pos > s.length()-1)
                break;
        }
        return pxCopy;

    }

    //------------------------------------------------------------------------- 
    /// Decrypts a string of characters encoded into an image by comparing
    /// pixels in the cypher with the key image
    /// @input pxCypher the encrypted image containing the message
    /// @input pxKey the key image that was used for the encryption
    /// @return the decrypted string of characters
    public static String decrypt2(int[][][] pxCypher, int[][][] pxKey) {
        //TODO: IMPLEMENT
	
	//initialize things
	String s = "";
        int dif = 0;
        int alpha_number = 0;
	int[] dif_vec = new int[3];
	
	//create reference number for num value of first letter in alphabet	
        int start = (int) 'a';

	//iterate through each tenth row, tenth column    
        for (int row=0; row<pxCypher.length; row=row+10){
            for (int col=0; col<pxCypher[row].length; col=col+10){
		
		//find difference between RGB vals in original and cypher
		dif_vec = colorError(pxKey[row][col], pxCypher[row][col]);

		//if all of the difs are 0, message complete, stop decrypt
                if (dif_vec[0]==0 && dif_vec[1]==0 && dif_vec[2]==0)
                    break;

		//find the ACSII value of the error
                alpha_number = errorToPosition2(dif_vec) + start;

		//add the char to the string
                s = s + (char) alpha_number;
            }

	    //if all difs are 0, message complete, stop decrypt
            if (dif_vec[0]==0 && dif_vec[1]==0 && dif_vec[2]==0)
                break;
        }

	//convert the message to upper case
	s = s.toUpperCase();
	return s;
    }
}
