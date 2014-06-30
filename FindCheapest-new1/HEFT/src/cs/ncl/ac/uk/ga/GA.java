package cs.ncl.ac.uk.ga;


import cs.ncl.ac.uk.logs.LogAccess;
import cs.ncl.ac.uk.security.Security;
import cs.ncl.ac.uk.security.securityCheck;
import cs.ncl.ac.uk.test.RandomInt;
import cs.ncl.ac.uk.test.Workflow;
import cs.ncl.ac.uk.test.WorkflowModel;
import cs.ncl.ac.uk.test.WorkflowTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ZequnLi
 *         Date: 14-4-30
 */
public class GA {
    double[][] workflow;
    int[][] dataSecurity;
    double [][] ccost;
    double [][] cpucost;
    double[][] storageTime;
    double[] storageCost;
    int [] cloud;
    int [][] ssecurity;
    private Security scheck;
    private List<List<Integer>> store;
    private Comparator<List<Integer>> comparator;

    public GA(WorkflowTemplate w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.getCcost();
        this.cpucost = w.getCpucost();
        this.cloud = w.getCloud();
        this.storageCost=w.getStorageCost();
        this.storageTime=w.getStorageTime();
        this.ssecurity = w.getSsecurity();
        this.scheck = new Security(w);
    }

    public double begin(int population,double crossover,double mutation){
        comparator = new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return (int)calCost(o1) - (int)calCost(o2);
            }
        };
        store = new ArrayList<List<Integer>>();
        Collections.sort(store,comparator);

        //check
        if(!scheck.workflowSecurity()){
            System.out.println("workflow not security");
            System.exit(0);
        }
        List<List<Integer>> pop = getPopulation(population);
        double m = Double.MAX_VALUE;
        for(int i =0;i<500;i++){
     //       List<List<Integer>> pop0 = updateStoreAndPopulation(pop,store);
            List<List<Integer>> pop1 = doSelection1(pop);
            List<List<Integer>> pop2 = doCrossover(pop1,crossover);
            List<List<Integer>> pop3 = doMutation(pop2,mutation);
            double temp = getBest(pop3);
            if(m>temp){
                m = temp;
            }
            pop = pop3;
        }
   //     printBest(pop);
        return m;
    }

    public static void main(String [] args) throws SQLException, IOException, ClassNotFoundException {
//            WorkflowModel workflowModel =WorkflowModel.read("model" + 5 + "" + 12 + "" + 2);
//
//            GA n = new GA(workflowModel);
//            long before = System.nanoTime();
//            System.out.println(n.begin(100,.6,.2));
//            long after = System.nanoTime();
//            long time = TimeUnit.MICROSECONDS.convert(after-before,TimeUnit.NANOSECONDS);


        LogAccess logAccess = new LogAccess("result");
        logAccess.init();
        String url="/Users/zhenyuwen/git/FindCheapest-new1/HEFT/";
      //  for(int x = 2 ; x<= 12;x ++){
            for(int y = 5;y<= 30;y++){
                long result = 0;
                double cost = 0;
                for(int i = 0;i<10;i++){
//                    WorkflowModel workflowModel =new WorkflowRandomCreator().create(x,y,2);
//                    WorkflowModel.store(workflowModel,"model"+x+""+y+""+i);
              	  WorkflowModel workflowModel =WorkflowModel.read(url+"newmodel" + 5 + "" + y + "" + i);
                    GA n = new GA(workflowModel);
                    long before = System.nanoTime();
                    cost +=n.begin(100,.8,.1);
                    long after = System.nanoTime();
                    long time = TimeUnit.MICROSECONDS.convert(after-before,TimeUnit.NANOSECONDS);
                    result+=time;
                }

                result/=10;
                cost/=10;
                logAccess.insertTuple(5+"",y+"",result+"",cost+"");
                System.out.println(5+" "+y);
            }

     //   }
            logAccess.output2CSV("/Users/zhenyuwen/Desktop/result", "GB.csv");
           // logAccess.Output2Screen();

    }

    private List<List<Integer>> getPopulation(int population){
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
        double number = 1;
        int max = 0;
        for(List list: possibleDeploy){
            number*=list.size();
            if(list.size()>max){
                max = list.size();
            }
        }
        //System.out.println(number);
        while (population>number){
            //throw new IllegalArgumentException("for the given workflow, there are not enough valid combinations to build so much population");
            population--;
        }
        // regard it as a max * service size full matrix
        double it = Math.pow(max,possibleDeploy.size()) -1;

        // System.out.println("number of it"+it);
        while (combination.size()<population){
            List<Integer> newPop = new ArrayList<Integer>();
            for(int i =0;i<possibleDeploy.size();i++){
                List<Integer> temp = possibleDeploy.get(i);
                newPop.add(temp.get(RandomInt.randomInt(0,temp.size()-1)));
            }
            boolean ignore = false;
            for(List<Integer> exist:combination){
                for(int i =0;i<exist.size();i++){
                    if(exist.get(i) != newPop.get(i)) break;
                    if(i == exist.size() -1){
                        ignore = true;
                    }
                }
            }
            if(!ignore){
                combination.add(newPop);
            }
        }
        return combination;
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

    private List<List<Integer>> doSelection(List<List<Integer>> population){
        List<List<Integer>> newPopulation = new ArrayList<List<Integer>>();
        List<Long> fitness = new ArrayList<Long>();
        long total = 0;
        for(List<Integer> list:population){
            long temp = (long)calCost(list);

            if(!scheck.deployListCheck(list)){
                temp*=10;
            }

            total+= temp;
            fitness.add(temp);
        }
        for(int i =0;i<fitness.size();i++){
            fitness.set(i,(total-fitness.get(i)));
            if(i!=0){
                fitness.set(i,fitness.get(i)+fitness.get(i-1));
            }
        }
        long range = fitness.get(fitness.size()-1);
        while (newPopulation.size()<population.size()){
            //int i = (int)range;
            long posValue = RandomInt.randomLong(0, (range));
            int pos= 0;
            while (posValue>fitness.get(pos)){
                pos++;
            }
            List<Integer> newList = new ArrayList<Integer>();
            newList.addAll(population.get(pos));
            newPopulation.add(newList);
        }
        return newPopulation;
    }

    private List<List<Integer>> doSelection1(List<List<Integer>> population) {
        List<List<Integer>> newPopulation = new ArrayList<List<Integer>>();
        List<Long> fitness = new ArrayList<Long>();
        long total = 0;
        for(int i=0;i< population.size();i++){
            List<Integer> list = population.get(i);
            long temp = (long)calCost(list);

            if(!scheck.deployListCheck(list)){
                population.set(i,getPopulation(1).get(0));
                temp = (long)calCost(population.get(i));
            }

            total+= temp;
            fitness.add(temp);
        }
        for(int i =0;i<fitness.size();i++){
            fitness.set(i,(total-fitness.get(i)));
            if(i!=0){
                fitness.set(i,fitness.get(i)+fitness.get(i-1));
            }
        }
        long range = fitness.get(fitness.size()-1);
        while (newPopulation.size()<population.size()){
            //int i = (int)range;
            long posValue = RandomInt.randomLong(0,(range));
            int pos= 0;
            while (posValue>fitness.get(pos)){
                pos++;
            }
            List<Integer> newList = new ArrayList<Integer>();
            newList.addAll(population.get(pos));
            newPopulation.add(newList);
        }
        return newPopulation;
    }

    private List<List<Integer>> doCrossover(List<List<Integer>> population,double crossover){
        List<List<Integer>> newPopulation = new ArrayList<List<Integer>>();
        int pair = (int)((population.size()/2) * crossover);
        List<Integer> pairs = RandomInt.randomInt(0,population.size()-1,pair*2);
        Set<Integer> added = new HashSet<Integer>();
        for(int i =0;i< pairs.size()-1;i+=2){
            List<Integer> x1 = population.get(pairs.get(i));
            List<Integer> x2 = population.get(pairs.get(i+1));
            added.add(pairs.get(i));
            added.add(pairs.get(i+1));
            int length = RandomInt.randomInt(0,x1.size()-2);
            List<Integer> x1New = new ArrayList<Integer>();
            List<Integer> x2New = new ArrayList<Integer>();
            int j =0;
            for(;j<=length;j++){
                x1New.add(x2.get(j));
                x2New.add(x1.get(j));
            }
            while (j<x1.size()){
                x1New.add(x1.get(j));
                x2New.add(x2.get(j));
                j++;
            }
            newPopulation.add(x1New);
            newPopulation.add(x2New);
        }
        for(int i=0;i<population.size();i++){
            if(added.contains(i)) continue;
            newPopulation.add(population.get(i));
        }
        return newPopulation;
    }

    private List<List<Integer>> doMutation(List<List<Integer>> population,double mutation){
        List<List<Integer>> newPopulation = new ArrayList<List<Integer>>();

        for(int i =0;i<population.size();i++){
            newPopulation.add(new ArrayList<Integer>());
            for(int j =0;j<population.get(i).size();j++){
                newPopulation.get(i).add(population.get(i).get(j));
                if(RandomInt.randomBoolean(mutation)){
                    //int temp = population.get(i).get(j);
//                    int newTemp = RandomInt.randomInt(0,this.cloud.length-1);
                    List<Integer> allowed = scheck.getAllowedDeployList(j);
                    int newTemp = allowed.get(RandomInt.randomInt(0,allowed.size()-1));
                    newPopulation.get(i).set(j,newTemp);
                }
            }
        }
        return newPopulation;
    }

    private double getBest(List<List<Integer>> input){
        List<Integer> list = null;
        double min = Double.MAX_VALUE;
        for(List<Integer> r:input){
            if(scheck.deployListCheck(r)){
                double temp = calCost(r);
                if(min>temp){
                    min = temp;
                    list = r;
                }
            }
        }
//        System.out.println(list);
//        System.out.println(min);
        return min;
    }
    
    private void printBest(List<List<Integer>> input){
        List<Integer> list = null;
        double min = Double.MAX_VALUE;
        for(List<Integer> r:input){
            if(scheck.deployListCheck(r)){
                double temp = calCost(r);
                if(min>temp){
                    min = temp;
                    list = r;
                }
            }
        }
        System.out.println(list);
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
                        result+=this.storageCost[c]*storageTime[i][j]*outputData;
                    }
                }
            }

        }
        return result;
    }

    private List<List<Integer>> updateStoreAndPopulation(List<List<Integer>> population,List<List<Integer>> list){
        List<List<Integer>> newPop = new ArrayList<List<Integer>>();
        for(List<Integer> c:population){
            list.add(c);
        }
        Collections.sort(list,comparator);
        for(int i = 0;i<population.size();i++){
            List<Integer> newC = new ArrayList<Integer>();
            for(int r:list.get(i)){
                newC.add(r);
            }
            newPop.add(newC);
        }
        while (list.size()>10){
            list.remove(list.size()-1);
        }

        return newPop;
    }

}