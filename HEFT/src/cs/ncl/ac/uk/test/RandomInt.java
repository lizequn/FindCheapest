package cs.ncl.ac.uk.test;

import java.util.Random;

/**
 * @author ZequnLi
 *         Date: 14-4-30
 */
public class RandomInt {
    public static int randomInt(int min,int max){
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
