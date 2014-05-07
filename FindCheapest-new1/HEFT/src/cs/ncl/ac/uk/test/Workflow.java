package cs.ncl.ac.uk.test;

public class Workflow implements WorkflowTemplate {
    // workflow the weight represents the data size
    int[][] workflow={{-1,10,5,-1,-1,-1,-1},
            {-1,-1,-1,15,-1,-1,-1},
            {-1,-1,-1,-1,14,-1,-1},
            {-1,-1,-1,-1,-1,200,-1},
            {-1,-1,-1,-1,-1,30,-1},
            {-1,-1,-1,-1,-1,-1,100},
            {-1,-1,-1,-1,-1,-1,-1}};

    // data security

    int [][] dsecurity={{-1,0,1,-1,-1,-1,-1},
            {-1,-1,-1,0,-1,-1,-1},
            {-1,-1,-1,-1,0,-1,-1},
            {-1,-1,-1,-1,-1,1,-1},
            {-1,-1,-1,-1,-1,1,-1},
            {-1,-1,-1,-1,-1,-1,0},
            {-1,-1,-1,-1,-1,-1,-1}};
    // communication cost
    int [][] ccost ={{0,2,4},
            {3,0,1},
            {2,1,0}};
    // CPU cost for each block on the clouds
    int [][] cpucost={{30,20,16},
            {25,30,29},
            {10,35,26},
            {43,29,17},
            {20,45,12},
            {18,27,19},
            {47,65,61}};
    // cloud security
    int [] cloud={0,1,1};

    // service security: clearance, location
    int [][] ssecurity={{1,0},
            {0,0},
            {1,0},
            {1,1},
            {0,0},
            {1,0},
            {0,0}
    };

    public int [][] getWorkflow(){
		return workflow;
	}
	
	public int[][] comCost(){
		return ccost;
	}
	
	public int[][] deployCost(){
		return cpucost;
	}

    public int[][] getCcost() {
        return ccost;
    }

    public int[][] getCpucost() {
        return cpucost;
    }

    public int[][] getDataSecurity() {
        return dsecurity;
    }
    public int[] getCloud() {
        return cloud;
    }
    public int[][] getSsecurity() {
        return ssecurity;
    }

}
