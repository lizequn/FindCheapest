package cs.ncl.ac.uk.security;

import cs.ncl.ac.uk.test.WorkflowTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZequnLi
 *         Date: 14-5-1
 */
public class Security {
    int[][] workflow;
    int[][] dataSecurity;
    int [][] ccost;
    int [][] cpucost;
    int [] cloud;
    int [][] ssecurity;
    private final List<Integer> possibleDeploy;

    public Security(WorkflowTemplate w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.getCcost();
        this.cpucost = w.getCpucost();
        this.cloud = w.getCloud();
        this.ssecurity = w.getSsecurity();
        possibleDeploy = init();
    }

    public boolean workflowSecurity(){
        for(int a=0;a<dataSecurity.length;a++){
            for(int i=0;i<dataSecurity[a].length;i++){
                if(dataSecurity[a][i]>=0){
                    int startNode=a;
                    int sonNode=i;
                    if(!compare(startNode,sonNode)){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public boolean compare(int startNode,int sonNode){
        int clearanceNode=ssecurity[startNode][0];
        int locationNode=ssecurity[startNode][1];
        int clearancesonNode=ssecurity[sonNode][0];
        int locationsonNode=ssecurity[sonNode][1];
        int datalocation=dataSecurity[startNode][sonNode];
        if(locationNode>datalocation||clearanceNode<locationNode||clearancesonNode<locationsonNode||datalocation>clearancesonNode){
//            System.out.println(startNode);
//            System.out.println(sonNode);
            return false;
        }
        return true;
    }
    private List<Integer> init(){
        // platform sort  ordered by security level
        List<Integer> list = new ArrayList<Integer>();
        for(int i=0;i<ssecurity.length;i++){

            int min = ssecurity[i][1]; //location
            //int min = minLvl;

            // consider data security
            int dataMin = calMinDataSecurity(i);
            if(min<dataMin) min = dataMin;
             list.add(min);
        }
        return list;

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
    public boolean allowedDeploy(int serviceId,int cloudId){
        int security = cloud[cloudId];

        if(security>=possibleDeploy.get(serviceId)){
            return true;
        }
        return false;
    }
    public boolean deployListCheck(List<Integer> list){
        boolean flag = true;
        int i = 0;
        for(int j:list){
            flag = allowedDeploy(i,j);
            i++;
            if(!flag){
                return false;
            }
        }
        return true;
    }


}
