package cs.ncl.ac.uk.test;

import java.util.Random;

/**
 * @author ZequnLi
 *         Date: 14-4-30
 */
public class WorkflowRandomCreator {

    public WorkflowTemplate create(int cloudn,int servicen,int securityRange){
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

        WorkflowModel workflowModel = new WorkflowModel();
        workflowModel.setWorkflow(workflow);
        workflowModel.setCcost(ccost);
        workflowModel.setCloud(cloud);
        workflowModel.setCpucost(cpucost);
        workflowModel.setDataSecurity(dataSecurity);
        workflowModel.setSsecurity(ssecurity);
        return workflowModel;

    }
    class WorkflowModel implements WorkflowTemplate{
        int[][] workflow;
        int[][] dataSecurity;
        int [][] ccost;
        int [][] cpucost;
        int [] cloud;
        int [][] ssecurity;

        void setWorkflow(int[][] workflow) {
            this.workflow = workflow;
        }

        void setDataSecurity(int[][] dataSecurity) {
            this.dataSecurity = dataSecurity;
        }

        void setCcost(int[][] ccost) {
            this.ccost = ccost;
        }

        void setCpucost(int[][] cpucost) {
            this.cpucost = cpucost;
        }

        void setCloud(int[] cloud) {
            this.cloud = cloud;
        }

        void setSsecurity(int[][] ssecurity) {
            this.ssecurity = ssecurity;
        }

        @Override
        public int[][] getWorkflow() {
            return this.workflow;
        }

        @Override
        public int[][] getDataSecurity() {
            return this.dataSecurity;
        }

        @Override
        public int[][] getCcost() {
            return this.ccost;
        }

        @Override
        public int[][] getCpucost() {
            return this.cpucost;
        }

        @Override
        public int[] getCloud() {
            return this.cloud;
        }

        @Override
        public int[][] getSsecurity() {
            return this.ssecurity;
        }
    }
}
