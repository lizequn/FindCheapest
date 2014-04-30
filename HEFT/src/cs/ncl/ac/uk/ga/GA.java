package cs.ncl.ac.uk.ga;

import cs.ncl.ac.uk.test.Workflow;

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

    public GA(Workflow w){
        this.workflow = w.getWorkflow();
        this.dataSecurity = w.getDataSecurity();
        this.ccost = w.comCost();
        this.cpucost = w.deployCost();
        this.cloud = w.getCloud();
        this.ssecurity = w.getSsecurity();
    }
}
