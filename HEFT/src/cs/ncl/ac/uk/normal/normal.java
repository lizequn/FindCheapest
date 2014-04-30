package cs.ncl.ac.uk.normal;

import cs.ncl.ac.uk.test.Workflow;
import cs.ncl.ac.uk.test.WorkflowRandomCreator;
import cs.ncl.ac.uk.test.WorkflowTemplate;

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

    int[][] workflow;
    int[][] dataSecurity;
    int [][] ccost;
    int [][] cpucost;
    int [] cloud;
    int [][] ssecurity;

    public Normal(WorkflowTemplate w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.getCcost();
        this.cpucost = w.getCpucost();
        this.cloud = w.getCloud();
        this.ssecurity = w.getSsecurity();
    }


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
            int max = ssecurity[i][0]; //clearance
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
        System.out.println("number of it"+it);
        while (it>=0){
            boolean ignore = false;
            List<Integer> list = new ArrayList<Integer>();
            double temp = it;
            for(int m =0;m<possibleDeploy.size();m++){
                if(temp % max >= possibleDeploy.get(m).size()){
                    ignore = true;
                    break;
                }
                list.add(possibleDeploy.get(m).get((int)(temp%max)));  //index out of bound
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
                    int outputData = this.workflow[i][j];
                    if(outputData != -1){
                       result+= this.ccost[c][combination.get(j)] * outputData;
                    }
                }
            }

        }
        return result;
    }

    public static void main(String [] args) {
        //Normal n = new Normal(new Workflow());
        Normal n = new Normal(new WorkflowRandomCreator().create(3,10,10));
        System.out.println("finish creating");
        long before = System.nanoTime();
        List<List<Integer>> lists =n.sort();
        long after = System.nanoTime();
        long time = TimeUnit.MILLISECONDS.convert(after-before,TimeUnit.NANOSECONDS);
        System.out.println("time:"+time);
        System.out.println("choices"+lists.size());
        double minCost = Double.MAX_VALUE;
        for(List<Integer> list: lists){
            double temp = n.calCost(list);
            if(minCost>temp){
                minCost = temp;
            }
        }
        System.out.println("min cost: "+ minCost);
    }
}
