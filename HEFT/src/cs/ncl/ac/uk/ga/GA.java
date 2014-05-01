package cs.ncl.ac.uk.ga;

import com.sun.deploy.util.ArrayUtil;
import cs.ncl.ac.uk.security.securityCheck;
import cs.ncl.ac.uk.test.RandomInt;
import cs.ncl.ac.uk.test.Workflow;
import cs.ncl.ac.uk.test.WorkflowTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ZequnLi
 *         Date: 14-4-30
 */
public class GA {
    int[][] workflow;
    int[][] dataSecurity;
    int [][] ccost;
    int [][] cpucost;
    int [] cloud;
    int [][] ssecurity;
    private securityCheck scheck;

    public GA(WorkflowTemplate w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.getCcost();
        this.cpucost = w.getCpucost();
        this.cloud = w.getCloud();
        this.ssecurity = w.getSsecurity();
        this.scheck = new securityCheck(new Workflow());
    }

    public void begin(int population,double crossover,double mutation){

        //check
        if(!scheck.workflowSecurity()){
            System.out.println("workflow not security");
            System.exit(0);
        }
        List<List<Integer>> pop = getPopulation(population);
        System.out.println("A");
        double m = Double.MAX_VALUE;
        for(int i =0;i<1000;i++){
            List<List<Integer>> pop1 = doSelection(pop);
            List<List<Integer>> pop2 = doCrossover(pop1,crossover);
            List<List<Integer>> pop3 = doMutation(pop2,mutation);
            double temp = getBest(pop3);
            if(m>temp){
                m = temp;
            }
        }
        System.out.println(m);

    }

    public static void main(String [] args){
        GA ga = new GA(new Workflow());
        ga.begin(10,.5,.05);
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
        System.out.println(number);
        if(population>number){
            throw new IllegalArgumentException("for the given workflow, there are not enough valid combinations to build so much population");
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
        List<Double> fitness = new ArrayList<Double>();
        double total = 0;
        for(List<Integer> list:population){
            double temp = calCost(list);
            total+= temp;
            fitness.add(temp);
        }
        for(int i =0;i<fitness.size();i++){
            fitness.set(i,(total-fitness.get(i)));
            if(i!=0){
                fitness.set(i,fitness.get(i)+fitness.get(i-1));
            }
        }
        double range = fitness.get(fitness.size()-1);
        while (newPopulation.size()<population.size()){
            int posValue = RandomInt.randomInt(0,(int)range);
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
                    int newTemp = RandomInt.randomInt(0,this.cloud.length-1);
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
            double temp = calCost(r);
            if(min>temp){
                min = temp;
                list = r;
            }
        }
        System.out.println(list);
        System.out.println(min);
        return min;
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


}
