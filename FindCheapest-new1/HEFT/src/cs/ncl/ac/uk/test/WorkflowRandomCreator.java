package cs.ncl.ac.uk.test;

import cs.ncl.ac.uk.security.Security;

import java.io.*;
import java.util.*;

/**
 * @author ZequnLi
 *         Date: 14-4-30
 */
public class WorkflowRandomCreator {
    private static int count;

    public WorkflowModel create(int cloudn,int servicen,int securityRange){
        while (true){
            WorkflowModel workflowModel = new WorkflowModel();
            double [][] workflow = new double[servicen][servicen];
            int [][] dataSecurity = new int[servicen][servicen];
            double [][] ccost = new double[cloudn][cloudn];
            double [][] cpucost = new double[servicen][cloudn];
            int [] cloud = new int[cloudn];
            int [][] ssecurity = new int[servicen][2];
            double [][] storageTime=new double[servicen][servicen];
            double [] storageCost=new double[cloudn];
            int max = 0;
            for(int i =0;i<servicen;i++){
                for(int j =0; j<servicen;j++){
                    if(i >=j){
                        workflow[i][j] = -1;
                        dataSecurity[i][j] = -1;
                        storageTime[i][j]=-1;
                    }else {
                        if(1 == RandomInt.randomInt(1,servicen) ) {
                            workflow[i][j] = RandomInt.randomInt(1,100);
                            dataSecurity[i][j] = RandomInt.randomInt(0,securityRange-1);
                            storageTime[i][j]=RandomInt.randomInt(1,40);
                            if(max<dataSecurity[i][j]) max = dataSecurity[i][j];
                        } else {
                            workflow[i][j] = -1;
                            dataSecurity[i][j] = -1;
                            storageTime[i][j]=-1;
                        }
                    }
                }
                ssecurity[i][0] = securityRange-1; // temp
                ssecurity[i][1] = RandomInt.randomInt(0,securityRange-1);
                for(int j = 0;j<cloudn;j++){
                    //cpu cost
                    cpucost[i][j] = RandomInt.randomInt(1,400);
                }
            }

            for(int i =0;i<servicen;i++){
                boolean island = true;
                for(int j =0; j<servicen;j++){
                    if(workflow[i][j] != -1 || workflow[j][i] != -1){
                        island = false;
                        break;
                    }
                }
                while(island){
                    int con = RandomInt.randomInt(0,servicen-1);
                    if(con == i) continue;
                    if(i< con){
                        workflow[i][con] = RandomInt.randomInt(1,100);
                        dataSecurity[i][con] = RandomInt.randomInt(0,securityRange-1);
                        storageTime[i][con]=RandomInt.randomInt(1,40);
                    }else {
                        workflow[con][i] = RandomInt.randomInt(1,100);
                        dataSecurity[con][i] = RandomInt.randomInt(0,securityRange-1);
                        storageTime[con][i]=RandomInt.randomInt(1,40);
                    }
                    island = false;
                }
            }
            for(int i =0;i<cloudn;i++){
                for(int j = 0;j<cloudn;j++){
                    if(i ==j) continue;
                    //communication cost
                    ccost[i][j] = RandomInt.randomInt(1,10);
                }
                cloud[i] = RandomInt.randomInt(0,securityRange-1);
                storageCost[i]= new Random().nextDouble()*0.1;
            }

            for(int i = 0;i<servicen;i++){
                if(max< ssecurity[i][1]){
                    max = ssecurity[i][1];
                }

            }
            max = securityRange -1;
            boolean flag = false;
            for(int i = 0;i<cloudn;i++){
                if(cloud[1]>=max) {
                    flag = true;
                    break;
                }
            }
            if(!flag){
                cloud[RandomInt.randomInt(0,cloudn-1)] = max;
            }


            workflowModel.setWorkflow(workflow);
            workflowModel.setCcost(ccost);
            workflowModel.setCloud(cloud);
            workflowModel.setCpucost(cpucost);
            workflowModel.setDataSecurity(dataSecurity);
            workflowModel.setSsecurity(ssecurity);
            workflowModel.setStorageCost(storageCost);
            workflowModel.setStorageTime(storageTime);
            Security security = new Security(workflowModel);

//                return workflowModel;
               // System.out.println(count++);
                while(security.workflowSecurity()){
                    List<Integer> set = new ArrayList<Integer>();
                    Stack<Integer> stack = new Stack<Integer>();
                    int root = 0;
                    set.add(root);
                    stack.push(root);

                    while (stack.size()>0){
                        int curPos = stack.pop();
                        for(int i =0 ;i<workflow.length;i++){
                            if(workflow[curPos][i]>0){
                                if(!set.contains(i)){
                                    set.add(i);
                                    stack.push(i);
                                }
                            }
                            if(workflow[curPos][i]>0){
                                if(!set.contains(i)){
                                    set.add(i);
                                    stack.push(i);
                                }
                            }
                        }
                    }
                   // System.out.println(set.size()+ " " + Workflow.length);
                    if(set.size()==workflow.length){
                        return workflowModel;
                    }
                    for(int i = 0; i<workflow.length;i++){
                        if(!set.contains(i)){
                            int con = set.get(RandomInt.randomInt(0,set.size()-1));
                            if(i< con){
                                workflow[i][con] = RandomInt.randomInt(1,100);
                            }else {
                                workflow[con][i] = RandomInt.randomInt(1,100);
                            }
                        }
                    }
                }

            }
        }


    public static boolean checkDAG(WorkflowTemplate template){
        double [][] workflow = template.getWorkflow();
        Set<Integer> set = new HashSet<Integer>();
        Stack<Integer> stack = new Stack<Integer>();
        int root = 0;
        set.add(root);
        stack.push(root);

        while (stack.size()>0){
            int curPos = stack.pop();
            for(int i =0 ;i<workflow.length;i++){
                if(workflow[curPos][i]>0){
                    if(!set.contains(i)){
                        set.add(i);
                        stack.push(i);
                    }
                }
                if(workflow[curPos][i]>0){
                    if(!set.contains(i)){
                        set.add(i);
                        stack.push(i);
                    }
                }
            }
        }
        //System.out.println(set.size()+ " " + Workflow.length);
        if(set.size()==workflow.length){
            return true;
        }
        return false;
    }

    public static void main(String [] args) throws IOException, ClassNotFoundException {
        for(int x = 5 ; x<= 5;x ++) {
            for (int y = 2; y <= 30; y++) {
                for (int i = 0; i < 10; i++) {
                    WorkflowModel workflowModel = new WorkflowRandomCreator().create(x, y, 4);
                    assert(WorkflowRandomCreator.checkDAG(workflowModel));
                    WorkflowModel.store(workflowModel, "newmodel" + x + "" + y + "" + i);
                    System.out.println(x+" "+y);
                }
            }
        }
//        for(int x = 2 ; x<= 2;x ++) {
//            for (int y = 2; y <= 11; y++) {
//                for (int i = 0; i < 10; i++) {
//                    WorkflowModel model = WorkflowModel.read("newmodel" + x + "" + y + "" + i);
//                    WorkflowRandomCreator.checkDAG(new Workflow());
//                }
//            }
//        }
//        WorkflowModel model =new WorkflowRandomCreator().create(2,12,2);
//        WorkflowModel.store(model,"newmodel" + 2 + "" + 12 + "" + 1);
//        WorkflowModel model1 = WorkflowModel.read("newmodel" + 2 + "" + 12 + "" + 1);
//        WorkflowRandomCreator.checkDAG(model1);
//        print(model.Workflow);
    }
    public static void print(int [][] result){
        for(int h=0;h<result.length;h++){
            for(int f=0;f<result[h].length;f++){
                System.out.print(result[h][f]+",");
            }
            System.out.println("");
        }
    }
}