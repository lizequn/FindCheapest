package cs.ncl.ac.uk;

import java.util.LinkedList;

/**
 * @author ZequnLi
 *         Date: 14-5-2
 */
public class Test {
    public static void main(String [] args){
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        linkedList.add(4);
        linkedList.add(5);
        linkedList.add(5);
        System.out.println(linkedList);
        linkedList.remove((Object)5);
        System.out.println(linkedList);

    }
}
