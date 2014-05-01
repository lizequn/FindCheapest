package cs.ncl.ac.uk.test;

import java.util.*;

/**
 * @author ZequnLi
 *         Date: 14-4-30
 */
public class RandomInt {
    public static int randomInt(int min,int max){
        if(min == max) return max;
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
    public static List<Integer> randomInt(int min,int max, int size){

        List<Integer> list  = new ArrayList<Integer>();
        if(max - min+1 <size){
            throw new IllegalArgumentException();
        }
        Set<Integer> set = new HashSet< Integer >();
        while (set.size()<size){
            int temp = randomInt(min,max);
            if(set.contains(temp)) continue;
            set.add(temp);
            list.add(temp);
        }
        return list;
    }
    public static boolean randomBoolean(double chance){
        final int precision =1000;
        int range = (int)(chance*precision);
        int pos = randomInt(1,precision);
        if(pos<=range){
            return true;
        }
        return false;

    }
}
