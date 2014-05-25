package cs.ncl.ac.uk.ga;

import cs.ncl.ac.uk.log.LogAccess;
import cs.ncl.ac.uk.security.Security;
import cs.ncl.ac.uk.test.RandomInt;
import cs.ncl.ac.uk.test.Workflow;
import cs.ncl.ac.uk.test.WorkflowModel;
import cs.ncl.ac.uk.test.WorkflowTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author ZequnLi
 *         Date: 14-5-6
 */
public class Annealing {
    int[][] workflow;
    int[][] dataSecurity;
    int [][] ccost;
    int [][] cpucost;
    int [] cloud;
    int [][] ssecurity;
    private Security scheck;

    public Annealing(WorkflowTemplate w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.getCcost();
        this.cpucost = w.getCpucost();
        this.cloud = w.getCloud();
        this.ssecurity = w.getSsecurity();
        this.scheck = new Security(w);
    }

    private List<Integer> getInit(){
        List<Integer> init = new ArrayList<Integer>();
        for(int i =0;i<workflow.length;i++){
            List<Integer> list = scheck.getAllowedDeployList(i);
            init.add(list.get(RandomInt.randomInt(0,list.size()-1)));
        }
        return init;
    }
    private List<Integer> getNext(List<Integer> list,int num){
        int counter = 0;
        for(int i =0;i<num;i++){
            int pos = RandomInt.randomInt(0,list.size()-1);
            int oldPos = list.get(pos);
            int min = Integer.MAX_VALUE;
            for(int c = 0; c< this.cloud.length;c++){
                if(!scheck.allowedDeploy(pos,c)) continue;
                int temp = calSingleCost(list,pos,c);
                if(min>temp){
                    list.set(pos,c);
                    min = temp;
                }
            }
            if(oldPos == list.get(pos)){
                counter++;
            }
        }
        if(counter == num){
            return getInit();
        }
        return list;
    }
    public double begin(double highT,double lowT,double rate,int num){
        Random random = new Random();
        double currentT = highT;
        List<Integer> init = getInit();
        double oldCost = calCost(init);
        int i =0;
        List<Integer> oldList = init;
        double lowestCost = oldCost;
        while (currentT>lowT){
            i = 0;
            while (i<num){
                i++;
                List<Integer> newList = getNext(oldList,init.size());
                double newCost = calCost(newList);
                if(newCost<=oldCost){
                    //System.out.println(i);
                    oldCost = newCost;
                    oldList = newList;
                    if(lowestCost>newCost){
                        lowestCost = newCost;
                    }
                } else if (Math.exp((oldCost-newCost)/currentT) > random.nextDouble()){
                    //ystem.out.println(Math.exp((oldCost-newCost)/currentT));
                    oldCost = newCost;
                    oldList = newList;
                } else {
                   // System.out.println(Math.exp((oldCost-newCost)/currentT));
                    // do nothing
                }
            }
            currentT*=rate;
        }
        return lowestCost;
    }

    private double calCost(List<Integer> combination) {
        double result = 0;
        for (int i = 0; i < combination.size(); i++) {
            int c = combination.get(i);
            result += this.cpucost[i][c];
            // output and input cost
            for (int j = 0; j < this.workflow.length; j++) {
                // calculate only two services deploy in different cloud.
                if (c != combination.get(j)) {
                    int outputData = this.workflow[i][j];
                    if (outputData != -1) {
                        result += this.ccost[c][combination.get(j)] * outputData;
                    }
                }
            }

        }
        return result;
    }

    private int calSingleCost(List<Integer> list,int num,int cloud){
        int result = 0;
            int c = cloud;
            result += this.cpucost[num][c];
            // output and input cost
            for (int j = 0; j < this.workflow.length; j++) {
                // calculate only two services deploy in different cloud.
                if(j == num) continue;
                if (c != list.get(j)) {
                    int outputData = this.workflow[num][j];
                    if (outputData != -1) {
                        result += this.ccost[c][list.get(j)] * outputData;
                    }
                    int inputData = this.workflow[j][num];
                    if(inputData != -1){
                        result+=this.ccost[list.get(j)][c]*inputData;
                    }
                }
            }

        return result;
    }

    public static void main(String [] args) throws SQLException, IOException, ClassNotFoundException {
        LogAccess logAccess = new LogAccess("result");
        logAccess.init();
        for(int x = 2 ; x<= 12;x ++){
            for(int y = 2;y<= 30;y++){
                double result = 0;
                double cost = 0;
                for(int i = 0;i<10;i++){
                    WorkflowModel workflowModel =WorkflowModel.read("newmodel" + x + "" + y + "" + i);
                    Annealing annealing = new Annealing(workflowModel);
                    long before = System.nanoTime();
                    cost +=annealing.begin(1000,1,0.95,100);
                    long after = System.nanoTime();
                    long time = TimeUnit.MICROSECONDS.convert(after-before,TimeUnit.NANOSECONDS);
                    result+=time;
                }
                result/=10;
                cost/=10;
                logAccess.insertTuple(x+"",y+"",result+"",cost+"");
                System.out.println(x+" "+y);
            }

        }
//        WorkflowModel workflowModel =WorkflowModel.read("model" + 10 + "" + 100 + "" + 1);
//        Annealing annealing = new Annealing(workflowModel);
//        long before = System.nanoTime();
//        double cost =annealing.begin(100,0.01,0.90,100);
//        long after = System.nanoTime();
//        long time = TimeUnit.MICROSECONDS.convert(after-before,TimeUnit.NANOSECONDS);
//        System.out.println(cost);
       logAccess.output2CSV("D://","resultSANew.csv");
        logAccess.Output2Screen();
       // System.out.println(Math.exp((double)(-10)/(double)1));
    }

}
