package cs.ncl.ac.uk.HEFT;

import cs.ncl.ac.uk.security.Security;
import cs.ncl.ac.uk.test.WorkflowTemplate;

public class Math {
	int[][] workflow;
    int [][] ccost;
    int [][] cpucost;
    
	public Math(WorkflowTemplate getInfo){
		this.workflow=getInfo.getWorkflow();
        this.ccost=getInfo.getCcost();
        this.cpucost=getInfo.getCpucost();
	}
	// the stand deviation of computing cost
	public static double getComputeAv(){
		double result = 0;
		return result;
	}
	
	// the 
}
