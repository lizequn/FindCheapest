package cs.ncl.ac.uk.ga;

import cs.ncl.ac.uk.test.Workflow;
import cs.ncl.ac.uk.test.WorkflowTemplate;

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

    public GA(WorkflowTemplate w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.getCcost();
        this.cpucost = w.getCpucost();
        this.cloud = w.getCloud();
        this.ssecurity = w.getSsecurity();
    }

    public void begin(int population,double crossover,double mutation){


    }


}
