package cs.ncl.ac.uk.test;

import cs.ncl.ac.uk.security.Security;

import java.io.*;
import java.util.Random;

/**
 * @author ZequnLi
 *         Date: 14-4-30
 */
public class WorkflowRandomCreator {

    public WorkflowModel create(int cloudn,int servicen,int securityRange){
        WorkflowModel workflowModel = new WorkflowModel();
        while (true){
            int [][] workflow = new int[servicen][servicen];
            int [][] dataSecurity = new int[servicen][servicen];
            int [][] ccost = new int[cloudn][cloudn];
            int [][] cpucost = new int[servicen][cloudn];
            int [] cloud = new int[cloudn];
            int [][] ssecurity = new int[servicen][2];
            int max = 0;
            for(int i =0;i<servicen;i++){
                for(int j =0; j<servicen;j++){
                    if(i >=j){
                        workflow[i][j] = -1;
                        dataSecurity[i][j] = -1;
                    }else {
                        if(1 == RandomInt.randomInt(1,servicen) ) {
                            workflow[i][j] = RandomInt.randomInt(1,100);
                            dataSecurity[i][j] = RandomInt.randomInt(0,securityRange-1);
                            if(max<dataSecurity[i][j]) max = dataSecurity[i][j];
                        } else {
                            workflow[i][j] = -1;
                            dataSecurity[i][j] = -1;
                        }
                    }
                }
                ssecurity[i][0] = securityRange-1; // temp
                ssecurity[i][1] = RandomInt.randomInt(0,securityRange-1);
                for(int j = 0;j<cloudn;j++){
                    cpucost[i][j] = RandomInt.randomInt(1,100);
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
                    }else {
                        workflow[con][i] = RandomInt.randomInt(1,100);
                    }
                    island = false;
                }
            }
            for(int i =0;i<cloudn;i++){
                for(int j = 0;j<cloudn;j++){
                    if(i ==j) continue;
                    ccost[i][j] = RandomInt.randomInt(1,100);
                }
                cloud[i] = RandomInt.randomInt(0,securityRange-1);
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
            Security security = new Security(workflowModel);
            if(security.workflowSecurity()){
                return workflowModel;
            }
        }

    }

    public static void main(String [] args) throws IOException {
        for(int x = 2 ; x<= 12;x ++) {
            for (int y = 2; y <= 30; y++) {
                for (int i = 0; i < 10; i++) {
                    WorkflowModel workflowModel = new WorkflowRandomCreator().create(x, y, 2);
                    WorkflowModel.store(workflowModel, "newmodel" + x + "" + y + "" + i);
                }
            }
        }
    }
}
