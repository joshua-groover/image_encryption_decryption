/*-----------------------------------------------------------------------------
GWU CSCI1112 Fall 2019
author: James Taylor

This program performs unit testing on the Steganography class.  
------------------------------------------------------------------------------*/
import java.awt.*;
import java.awt.image.*;
import java.lang.*;
import java.util.*;


public class StegUnitTest {
    static ImageTool imTool = new ImageTool ();

    // ------------------------------------------------------------------------
    // Entry Point
    // ------------------------------------------------------------------------
    /// Entry Point.  The main program executes a set of UnitTests on the 
    /// various methods in the Steganography class to test different levels of 
    /// functionality.
    public static void main (String[] argv) {
        if( testCopy() ) {
            System.out.println("testCopy succeeded");
        } else {
            System.out.println("testCopy failed"); 
        }

        if( testColorError() ) {
            System.out.println("testColorError succeeded");
        } else {
            System.out.println("testColorError failed"); 
        }

        if( testColorPositionMapping() ) {
            System.out.println("testColorPositionMapping succeeded");
        } else {
            System.out.println("testColorPositionMapping failed"); 
        }

        if( testBaseCypher() ) {
            System.out.println("testBaseCypher succeeded");
        } else {
            System.out.println("testBaseCypher failed"); 
        }

        if( testColorPositionMapping2() ) {
            System.out.println("testColorPositionMapping2 succeeded");
        } else {
            System.out.println("testColorPositionMapping2 failed"); 
        }

        if( testExtensionCypher() ) {
            System.out.println("testExtensionCypher succeeded");
        } else {
            System.out.println("testExtensionCypher failed"); 
        }
    }

    // ------------------------------------------------------------------------
    // UnitTests
    // ------------------------------------------------------------------------
    /// Tests the Steganography.copy function.
    /// @return Returns true if the function passes all defined tests; 
    ///         otherwise, returns false
    // TODO: Add line comments to this function by explain what each branch 
    // checks
    public static boolean testCopy() {
        Image img = imTool.readImageFile( "gradient.png" );
        int[][][] srcpx = imTool.imageToPixels( img );
        int[][][] destpx = Steganography.copy(srcpx);

        if( srcpx == null || destpx == null ) {
            return false;
        }
        if( srcpx.length != destpx.length ) {
            return false;
        }
        for (int i=0; i < srcpx.length; i++) {
            if( srcpx[i] == destpx[i] ) {
                return false;
            }
            if( srcpx[i].length != destpx[i].length ) {
                return false;
            }
            for (int j=0; j < srcpx[i].length; j++) {
                if( srcpx[i][j] == destpx[i][j] ) {
                    return false;
                }
                if( srcpx[i][j].length != destpx[i][j].length ) {
                    return false;
                }
                for (int k=0; k < 4; k++) {
                    if( srcpx[i][j][k] != destpx[i][j][k] ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //-------------------------------------------------------------------------
    /// Tests the Steganography.colorError function.
    /// @return Returns true if the function passes all defined tests; 
    ///         otherwise, returns false
    public static boolean testColorError() {
        int[] keyPixel = new int[4];
        int[] cypherPixel = new int[4];
        int[] error;

	// by Java standard, arrays are initialized to zero by default, so a 
	// test comparing black to black. 
	error = Steganography.colorError(keyPixel, cypherPixel);
	if( error == null || error.length != 3 && (error[0] != 0 || error[1] != 0 || error[2] != 0) ) {
            return false;
	}

        // introduce some error by changing the key pixel color channels
        keyPixel[1] = keyPixel[2] = keyPixel[3] = 8;
	error = Steganography.colorError(keyPixel, cypherPixel);
	if( error == null || error.length != 3 && (error[0] != 8 || error[1] != 8 || error[2] != 8) ) {
            return false;
	}

        // test error in the other direction by changing the cypher pixel
        // color channels 
        cypherPixel[1] = cypherPixel[2] = cypherPixel[3] = 16;
	error = Steganography.colorError(keyPixel, cypherPixel);
	if( error == null || error.length != 3 && (error[0] != 8 || error[1] != 8 || error[2] != 8) ) {
            return false;
	} 

        // test different magnitude of error accross the different channels
        cypherPixel[1] = 0;
        cypherPixel[2] = 8;
        cypherPixel[3] = 24;
	error = Steganography.colorError(keyPixel, cypherPixel);
	if( error == null || error.length != 3 && (error[0]!= 8 || error[1] != 0 || error[2] != 16) ) {
            return false;
	} 
        return true;
    }

    //-------------------------------------------------------------------------
    /// For the base encrytion problem, the same value is encoded into all 
    /// color channels.  This test checks to see that the correct magnitude for
    /// a color change is computed for encoding and the correct position is
    /// computed for decoding. 
    /// @return Returns true if the function passes all defined tests; 
    ///         otherwise, returns false
    public static boolean testColorPositionMapping() {
        int[] adelta = Steganography.positionToError(0);    // 'A'
        int[] zdelta = Steganography.positionToError(25);   // 'Z'

        // check that the amount of change for each letter produces a color
        // with the expected change 
        if( adelta == null || adelta[0] != 1 || adelta[1] != 1 || adelta[2] != 1 ) {
            return false;
        }
        if( zdelta == null || zdelta[0] != 26 || zdelta[1] != 26 || zdelta[2] != 26 ) {
            return false;
        }

        // check that a letter position is correctly mapped to the associated 
        // color change 
        int val = Steganography.errorToPosition(adelta);
        if( val != 0 ) {
            return false;
        }
        val = Steganography.errorToPosition(zdelta);
        if( val != 25 ) {
            return false;
        }
        return true;
    }

    //-------------------------------------------------------------------------
    /// For the extension encrytion problem, the error is spread across each
    /// color channel.  This test checks to see that the correct magnitude for
    /// a color change is computed for encoding and the correct position is
    /// computed for decoding. 
    /// @return Returns true if the function passes all defined tests; 
    ///         otherwise, returns false
    public static boolean testColorPositionMapping2() {
        int[] adelta = Steganography.positionToError2(0);    // 'A'
        int[] bdelta = Steganography.positionToError2(1);    // 'B'
        int[] cdelta = Steganography.positionToError2(2);    // 'C'
        int[] xdelta = Steganography.positionToError2(23);   // 'X'
        int[] ydelta = Steganography.positionToError2(24);   // 'Y'
        int[] zdelta = Steganography.positionToError2(25);   // 'Z'

        // check that the amount of change for each letter produces a color
        // with the expected change 
        if( adelta == null || adelta[0] != 1 || adelta[1] != 0 || adelta[2] != 0 ) {
            return false;
        }
        if( bdelta == null || bdelta[0] != 1 || bdelta[1] != 1 || bdelta[2] != 0 ) {
            return false;
        }
        if( cdelta == null || cdelta[0] != 1 || cdelta[1] != 1 || cdelta[2] != 1 ) {
            return false;
        }
        if( xdelta == null || xdelta[0] != 8 || xdelta[1] != 8 || xdelta[2] != 8 ) {
            return false;
        }
        if( ydelta == null || ydelta[0] != 9 || ydelta[1] != 8 || ydelta[2] != 8 ) {
            return false;
        }
        if( zdelta == null || zdelta[0] != 9 || zdelta[1] != 9 || zdelta[2] != 8 ) {
            return false;
        }

        // check that a letter position is correctly mapped to the associated 
        // color change 
        int val = Steganography.errorToPosition2(adelta);
        if( val != 0 ) {
            return false;
        }
        val = Steganography.errorToPosition2(bdelta);
        if( val != 1 ) {
            return false;
        }
        val = Steganography.errorToPosition2(cdelta);
        if( val != 2 ) {
            return false;
        }
        val = Steganography.errorToPosition2(xdelta);
        if( val != 23 ) {
            return false;
        }
        val = Steganography.errorToPosition2(ydelta);
        if( val != 24 ) {
            return false;
        }
        val = Steganography.errorToPosition2(zdelta);
        if( val != 25 ) {
            return false;
        }
        return true;
    }

    //-------------------------------------------------------------------------
    /// This test evaluates the base encryption/decryption problem
    /// @return Returns true if the function passes all defined tests; 
    ///         otherwise, returns false
    public static boolean testBaseCypher() {
        // Load and show the key image
        Image key = imTool.readImageFile ("white.png");
        imTool.showImage (key, "testBaseCypher::Key");
        // Convert the key image to pixels
        int[][][] pxKey = imTool.imageToPixels (key);

        // variables that we will reuse for each of the tests in this function
        String msgin, msgout;
        Image cypher;
        int[][][] pxCypher;

        // -------- Hello Cypher --------
        // Generate a message
        msgin = "Hello";
        // Clean the string up so it meets the specification
        msgin = clean(msgin);

        pxCypher = Steganography.encrypt(msgin, pxKey);
	if(pxCypher == null) {
	    return false;
	}
        // Show the cypher for reference
        cypher = imTool.pixelsToImage (pxCypher);
        imTool.showImage (cypher, "testBaseCypher::Hello");

        // Decrypt the message back out, print it to the console, and return
        // true if the message out is the same as the message in
        msgout = Steganography.decrypt(pxCypher, pxKey);
        //System.out.println(msgout);
        if(!msgout.equals(msgin)) {
            return false;
        } 

        // -------- Pangram --------
        // Generate a message
        msgin = pangram();
        // Clean the string up so it meets the specification
        msgin = clean(msgin);

        pxCypher = Steganography.encrypt(msgin, pxKey);
	if(pxCypher == null) {
	    return false;
	}
        // Show the cypher for reference
        cypher = imTool.pixelsToImage (pxCypher);
        imTool.showImage (cypher, "testBaseCypher::Pangram");

        // Decrypt the message back out, print it to the console, and return
        // true if the message out is the same as the message in
        msgout = Steganography.decrypt(pxCypher, pxKey);
        //System.out.println(msgout);
        if(!msgout.equals(msgin)) {
            return false;
        } 

        // -------- LoremIpsum --------
        // Generate a message
        msgin = loremipsum();
        // Clean the string up so it meets the specification
        msgin = clean(msgin);

        pxCypher = Steganography.encrypt(msgin, pxKey);
	if(pxCypher == null) {
	    return false;
	}
        // Show the cypher for reference
        cypher = imTool.pixelsToImage (pxCypher);
        imTool.showImage (cypher, "testBaseCypher::LoremIpsum");

        // Decrypt the message back out, print it to the console, and return
        // true if the message out is the same as the message in
        msgout = Steganography.decrypt(pxCypher, pxKey);
        //System.out.println(msgout);
        if(!msgout.equals(msgin)) {
            return false;
        } 

        return true;
    }

    //-------------------------------------------------------------------------
    /// This test evaluates the extension encryption/decryption problem
    /// @return Returns true if the function passes all defined tests; 
    ///         otherwise, returns false
    public static boolean testExtensionCypher() {
        // Load and show the key image
        Image key = imTool.readImageFile ("gradient.png");
        imTool.showImage (key, "testExtensionCypher::Key");
        // Convert the key image to pixels
        int[][][] pxKey = imTool.imageToPixels (key);

        // variables that we will reuse for each of the tests in this function
        String msgin, msgout;
        Image cypher;
        int[][][] pxCypher;

        // -------- Hello Cypher --------
        // Generate a message
        msgin = "Hello";
        // Clean the string up so it meets the specification
        msgin = clean(msgin);

        pxCypher = Steganography.encrypt2(msgin, pxKey);
	if(pxCypher == null) {
	    return false;
	}
        // Show the cypher for reference
        cypher = imTool.pixelsToImage (pxCypher);
        imTool.showImage (cypher, "testExtensionCypher::Hello");

        // Decrypt the message back out, print it to the console, and return
        // true if the message out is the same as the message in
        msgout = Steganography.decrypt2(pxCypher, pxKey);
        //System.out.println(msgout);
        if(!msgout.equals(msgin)) {
            return false;
        } 

        // -------- Pangram --------
        // Generate a message
        msgin = pangram();
        // Clean the string up so it meets the specification
        msgin = clean(msgin);

        pxCypher = Steganography.encrypt2(msgin, pxKey);
	if(pxCypher == null) {
	    return false;
	}
        // Show the cypher for reference
        cypher = imTool.pixelsToImage (pxCypher);
        imTool.showImage (cypher, "testExtensionCypher::Pangram");

        // Decrypt the message back out, print it to the console, and return
        // true if the message out is the same as the message in
        msgout = Steganography.decrypt2(pxCypher, pxKey);
        //System.out.println(msgout);
        if(!msgout.equals(msgin)) {
            return false;
        } 

        // -------- LoremIpsum --------
        // Generate a message
        msgin = loremipsum();
        // Clean the string up so it meets the specification
        msgin = clean(msgin);

        pxCypher = Steganography.encrypt2(msgin, pxKey);
	if(pxCypher == null) {
	    return false;
	}
        // Show the cypher for reference
        cypher = imTool.pixelsToImage (pxCypher);
        imTool.showImage (cypher, "testExtensionCypher::LoremIpsum");

        // Decrypt the message back out, print it to the console, and return
        // true if the message out is the same as the message in
        msgout = Steganography.decrypt2(pxCypher, pxKey);
        //System.out.println(msgout);
        if(!msgout.equals(msgin)) {
            return false;
        } 

        return true;
    }


    //-------------------------------------------------------------------------
    // Utilities
    //-------------------------------------------------------------------------
    /// This method trims out all characters that are not alphas and uppercases
    /// the string.  This is not ideal because we cannot have numeric 
    /// information in the string; however, it is good enough for this simple
    /// approach.
    /// @param s the string to clean
    /// @return the cleaned string 
    public static String clean(String s) {
        return s.replaceAll("[^a-zA-Z]","").toUpperCase();
    }

    /// This method generates the quick brown fox text.  It can be used to
    /// test for all alphas
    /// @return a string containing the quick brown text
    public static String pangram() {
        return "The quick brown fox jumps over the lazy dog";
    }

    /// This method generates the standard lorem ipsum text.  It can be used to
    /// test large blocks of text
    /// @return a string containing lorem ipsum text
    public static String loremipsum() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    }

}
