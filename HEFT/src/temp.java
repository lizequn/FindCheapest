import java.util.ArrayList;
import java.util.List;

/**
 * @author ZequnLi
 *         Date: 14-4-25
 */
public class temp {
    private  int[][] servicesSecurity; //connection matrix ,-1 refers to no relation
    private  int[][] servicesCommunication;  //data transfer
    private  int[][] servicesCPUCost;        //cpu cost on different platform
    private  int[][] platformInfo;           //platform security, input and output cost

    public temp(){


    }

    public void inputInfo(int service,int platform,int range){
        servicesSecurity = new int[service][service];
        servicesCommunication = new int[service][service];
        servicesCPUCost = new int[service][platform];
        platformInfo = new int[platform][3];

        //generate random information

    }
    public void sort(){
        // platform sort  ordered by security level
        int minLvl= Integer.MAX_VALUE;
        int maxLvl = Integer.MIN_VALUE;
        final List<List<Integer>> sortedPlatform = new ArrayList<List<Integer>>();
        for(int i =0;i<platformInfo.length;i++){
            final int current = platformInfo[i][0];
            if(current<minLvl){
                minLvl = current;
            }
            if(current>maxLvl){
                maxLvl = current;
            }
            List<Integer> list = sortedPlatform.get(current);
            if(null == list){
                List<Integer> temp = new ArrayList<Integer>();
                temp.add(i);
                sortedPlatform.set(current,temp);
            } else {
                list.add(i);
            }
        }
        List<Integer> combination = new ArrayList<Integer>();
        //get possible deployment
        final List<List<Integer>> possibleDeploy = new ArrayList<List<Integer>>();
        for(int i=0;i<servicesSecurity.length;i++){
            List<Integer> list = new ArrayList<Integer>();
            int min = servicesSecurity[i][i];
            while (min<=maxLvl){
                list.addAll(sortedPlatform.get(min));
                min++;
            }
        }
        //get Permutation
        int max = 0;
        for(List list: possibleDeploy){
            if(list.size()>max){
                max = list.size();
            }
        }
        double it = Math.pow(max,possibleDeploy.size()) -1;
        while (it>=0){
            boolean ignore = false;
            List<Integer> list = new ArrayList<Integer>();
            double temp = it;
            for(int m =0;m<possibleDeploy.size();m++){
                if(temp % max >= possibleDeploy.get(m).size()){
                    ignore = true;
                    break;
                }
                list.add(possibleDeploy.get(m).get((int)temp%max));
                temp/=max;
            }
            if(!ignore){
                //result
            }
            it--;

        }

    }

    public static void main(String [] args) {
//        int a[][] = {{1 , 2  },{4 },{7 , 8 , 9},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2}};
//        String s;
//        int max = 0;
//        for(int []b :a){
//            if(b.length>max){
//                max = b.length;
//            }
//        }
//        int it = (int) Math.pow((max),(a.length)) -1;
//        while(it >= 0){
//            boolean ignore = false;
//            s = "";
//
//            int temp = it;
//            for(int m = 0 ; m < a.length ; m++){
//                if(temp%max >= a[m].length){
//                    ignore = true;
//                    break;
//                }
//                    s += a[m][temp % max];
//                temp /= max;
//             }
//            if(!ignore){
//                System.out.println(s);
//
//            }
//        it--;
//    }
    }


}
