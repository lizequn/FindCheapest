package cs.ncl.ac.uk.normal;

import cs.ncl.ac.uk.logs.LogAccess;
import cs.ncl.ac.uk.test.Workflow;
import cs.ncl.ac.uk.test.WorkflowModel;
import cs.ncl.ac.uk.test.WorkflowRandomCreator;
import cs.ncl.ac.uk.test.WorkflowTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ZequnLi
 *         Date: 14-4-25
 */
public class Normal {
//    private  int[][] servicesSecurity; //connection matrix ,-1 refers to no relation
//    private  int[][] servicesCommunication;  //data transfer
//    private  int[][] servicesCPUCost;        //cpu cost on different platform
//    private  int[][] platformInfo;           //platform security, input and output cost

    double[][] workflow;
    int[][] dataSecurity;
    double [][] ccost;
    double [][] cpucost;
    int [] cloud;
    int [][] ssecurity;
    double[][] storageTime;
    double[] storageCost;
 
    public Normal(WorkflowTemplate w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.getCcost();
        this.cpucost = w.getCpucost();
        this.cloud = w.getCloud();
        this.ssecurity = w.getSsecurity();
        this.storageCost=w.getStorageCost();
        this.storageTime=w.getStorageTime();
    }
      // check

    public List<List<Integer>> sort(){
        // platform sort  ordered by security level
        int minLvl= Integer.MAX_VALUE;
        int maxLvl = Integer.MIN_VALUE;
        final List<List<Integer>> sortedPlatform = new ArrayList<List<Integer>>();
        //find max security lvl
        for(int i =0;i<cloud.length;i++){
            final int current = cloud[i];
            if(current<minLvl){
                minLvl = current;
            }
            if(current>maxLvl){
                maxLvl = current;
            }
        }

        //init
        for(int i =0;i<maxLvl+1;i++){
            sortedPlatform.add(null);
        }
        // order and cluster clouds by its security
        for(int i =0;i<cloud.length;i++){
            final int current = cloud[i];
            List<Integer> list = sortedPlatform.get(current);
            if(null == list){
                List<Integer> temp = new ArrayList<Integer>();
                temp.add(i);
                sortedPlatform.set(current,temp);
            } else {
                list.add(i);
            }
        }
        //
        List<List<Integer>> combination = new ArrayList<List<Integer>>();
        //for each service get all possible deployment
        final List<List<Integer>> possibleDeploy = new ArrayList<List<Integer>>();
        for(int i=0;i<ssecurity.length;i++){
            List<Integer> list = new ArrayList<Integer>();
            int min = ssecurity[i][1]; //location
            //int min = minLvl;
            int max = maxLvl;
            // consider data security
            int dataMin = calMinDataSecurity(i);
            if(min<dataMin) min = dataMin;

            while (min<=max){
                if(sortedPlatform.get(min) != null){
                    list.addAll(sortedPlatform.get(min));
                }
                min++;
            }
            possibleDeploy.add(list);
        }
        //get Permutation
        //get maximum choices
        int max = 0;
        for(List list: possibleDeploy){
            if(list.size()>max){
                max = list.size();
            }
        }
        // regard it as a max * service size full matrix
        double it = Math.pow(max,possibleDeploy.size()) -1;
       // System.out.println("number of it"+it);
        while (it>=0){
            boolean ignore = false;
            List<Integer> list = new ArrayList<Integer>();
            double temp = it;
            for(int m =0;m<possibleDeploy.size();m++){
                if(temp % max >= possibleDeploy.get(m).size()){
                    ignore = true;
                    break;
                }
                list.add(possibleDeploy.get(m).get((int)(temp%max)));
                temp/=max;
            }
            if(!ignore){
                //result
                combination.add(list);
            }
            it--;
        }
        return combination;
        // all combination get

    }

    public List<Integer> sortBest(){
        // platform sort  ordered by security level
        int minLvl= Integer.MAX_VALUE;
        int maxLvl = Integer.MIN_VALUE;
        final List<List<Integer>> sortedPlatform = new ArrayList<List<Integer>>();
        //find max security lvl
        for(int i =0;i<cloud.length;i++){
            final int current = cloud[i];
            if(current<minLvl){
                minLvl = current;
            }
            if(current>maxLvl){
                maxLvl = current;
            }
        }

        //init
        for(int i =0;i<maxLvl+1;i++){
            sortedPlatform.add(null);
        }
        // order and cluster clouds by its security
        for(int i =0;i<cloud.length;i++){
            final int current = cloud[i];
            List<Integer> list = sortedPlatform.get(current);
            if(null == list){
                List<Integer> temp = new ArrayList<Integer>();
                temp.add(i);
                sortedPlatform.set(current,temp);
            } else {
                list.add(i);
            }
        }
   //    System.out.println("sp:"+sortedPlatform);
        //

        //for each service get all possible deployment
        final List<List<Integer>> possibleDeploy = new ArrayList<List<Integer>>();
        for(int i=0;i<ssecurity.length;i++){
            List<Integer> list = new ArrayList<Integer>();
            int min = ssecurity[i][1]; //location
            //int min = minLvl;
            int max = maxLvl;
            // consider data security
            int dataMin = calMinDataSecurity(i);
            if(min<dataMin) min = dataMin;

            while (min<=max){
                if(sortedPlatform.get(min) != null){
                    list.addAll(sortedPlatform.get(min));
                }
                min++;
            }
            possibleDeploy.add(list);
        }
      //  System.out.println("sp:"+possibleDeploy);
        //get Permutation
        //get maximum choices
        int max = 0;
        for(List list: possibleDeploy){
            if(list.size()>max){
                max = list.size();
            }
        }
        // regard it as a max * service size full matrix
        double it = Math.pow(max,possibleDeploy.size()) -1;

        double min = Double.MAX_VALUE;
        List<Integer> best = null;

        // System.out.println("number of it"+it);
        while (it>=0){
            boolean ignore = false;
            List<Integer> list = new ArrayList<Integer>();
            double temp = it;
            for(int m =0;m<possibleDeploy.size();m++){
                if(temp % max >= possibleDeploy.get(m).size()){
                    ignore = true;
                    break;
                }
                list.add(possibleDeploy.get(m).get((int)(temp%max)));
                temp/=max;
            }
            if(!ignore){
                //result
                double re =  this.calCost(list);
                if(min>re){
                    min =re;
                    best = list;
                }
            }
            it--;
        }
        return best;
    }

    private int calMinDataSecurity(int pos){
        int result = -1;
        for(int i = 0;i<this.workflow.length;i++){
            if(this.workflow[pos][i] != -1){
                if(result< this.dataSecurity[pos][i]){
                    result = this.dataSecurity[pos][i];
                }
            }
            if(this.workflow[i][pos] != -1){
                if(result<this.dataSecurity[i][pos]){
                    result = this.dataSecurity[i][pos];
                }
            }
        }
        return result;
    }

    public double calCost(List<Integer> combination){
        double result = 0;
        for(int i =0;i<combination.size();i++){
            int c = combination.get(i);
            result+= this.cpucost[i][c];
            // output and input cost
            for(int j = 0;j<this.workflow.length;j++){
                // calculate only two services deploy in different cloud.
                if(c!= combination.get(j)){
                    double outputData = this.workflow[i][j];
                    if(outputData != -1){
                       result+= this.ccost[c][combination.get(j)] * outputData;
                       // the cost of storage is source cloud storagecost * datasize * storagetime
                //       System.out.println(storageCost[c]);
                //       System.out.println(storageTime[i][j]);
                       result+=this.storageCost[c]*storageTime[i][j]*outputData;
                    }
                }
            }

        }
        return result;
    }

    public static void main(String [] args) throws SQLException, IOException, ClassNotFoundException {
        LogAccess logAccess = new LogAccess("result");
        logAccess.init();
        String url="/Users/zhenyuwen/git/FindCheapest-new1/HEFT/";
 //       for(int x = 2 ; x<= 5;x ++){
            for(int y = 2;y<= 12;y++){
                long result = 0;
                double cost = 0;
                for(int i = 0;i<10;i++){
//                    WorkflowModel workflowModel =new WorkflowRandomCreator().create(x,y,2);
//                    WorkflowModel.store(workflowModel,"model"+x+""+y+""+i);
                	  WorkflowModel workflowModel =WorkflowModel.read(url+"newmodel" + 5 + "" + y + "" + i);
                    Normal n = new Normal(workflowModel);
                    long before = System.nanoTime();
                    List<Integer> lists =n.sortBest();
//                    double minCost = Double.MAX_VALUE;
//                    for(List<Integer> list: lists){
//                        double temp = n.calCost(list);
//                        if(minCost>temp){
//                            minCost = temp;
//                        }
//                    }
                      cost+= n.calCost(lists);
//                      System.out.println(cost);
//                    System.out.println(lists);
//                    System.out.println("----------------");
//                    print(workflowModel.getWorkflow());
//                    System.out.println("----------------");
//                    print(workflowModel.getCcost());
//                    System.out.println("----------------");
//                    print(workflowModel.getCpucost());
//                    System.out.println("----------------");
//                    print(workflowModel.getSsecurity());
//                    print(workflowModel.getCloud());

                    long after = System.nanoTime();
                    long time = TimeUnit.MICROSECONDS.convert(after-before,TimeUnit.NANOSECONDS);
                    result+=time;
             //       System.exit(-1);
                }

                result/=10;
                cost/=10;
                logAccess.insertTuple("normal",5+"",y+"",result+"",cost+"");
                System.out.println(5+" "+y);
         //   }

        }
            logAccess.output2CSV("/Users/zhenyuwen/Desktop/result", "normal.csv");
//        Normal n = new Normal(new Workflow());
//       // Normal n = new Normal(new WorkflowRandomCreator().create(4,12,2));
//        System.out.println("finish creating");
//        long before = System.nanoTime();
//        List<List<Integer>> lists =n.sort();
//        System.out.println("choices"+lists.size());
//        double minCost = Double.MAX_VALUE;
//        List<Integer> minList = null;
//        for(List<Integer> list: lists){
//            double temp = n.calCost(list);
//            if(minCost>temp){
//                minCost = temp;
//                minList = list;
//            }
//            //System.out.println(list);
//        }
//        System.out.println(minList);
//        System.out.println("min cost: "+ minCost);
//        long after = System.nanoTime();
//        long time = TimeUnit.MILLISECONDS.convert(after-before,TimeUnit.NANOSECONDS);
//        System.out.println("time:"+time);
    }
    public static void print(int [][] result){
        for(int h=0;h<result.length;h++){
            for(int f=0;f<result[h].length;f++){
                System.out.print(result[h][f]+",");
            }
            System.out.println("");
        }
    }
    public static void print(int [] result){
        for(int h = 0;h<result.length;h++){
            System.out.print(result[h]+",");
        }
    }
}