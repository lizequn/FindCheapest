package cs.ncl.ac.uk.test;

import java.io.*;

/**
 * @author ZequnLi
 *         Date: 14-5-1
 */
public class WorkflowModel implements WorkflowTemplate,Serializable {
    double[][] workflow;
    int[][] dataSecurity;
    double [][] ccost;
    double [][] cpucost;
    // cloud means cloud security level
    int [] cloud;
    int [][] ssecurity;
    double [][] StorageTime;
    double [] StorageCost;
    public static void store(WorkflowModel workflowModel,String address) throws IOException {
        FileOutputStream fout = new FileOutputStream(address);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(workflowModel);
    }
    public static WorkflowModel read(String address) throws IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(address);
        ObjectInputStream ois = new ObjectInputStream(fin);
        return (WorkflowModel) ois.readObject();
    }


    void setWorkflow(double[][] workflow) {
        this.workflow = workflow;
    }

    void setDataSecurity(int[][] dataSecurity) {
        this.dataSecurity = dataSecurity;
    }

    void setCcost(double[][] ccost) {
        this.ccost = ccost;
    }

    void setCpucost(double[][] cpucost) {
        this.cpucost = cpucost;
    }

    void setCloud(int [] cloud) {
        this.cloud = cloud;
    }

    void setSsecurity(int[][] ssecurity) {
        this.ssecurity = ssecurity;
    }
    
    void setStorageTime(double [][] StorageTime){
    	this.StorageTime=StorageTime;
    }
    void setStorageCost(double [] StorageCost){
    	this.StorageCost=StorageCost;
    }

    @Override
    public double[][] getWorkflow() {
        return this.workflow;
    }

    @Override
    public int[][] getDataSecurity() {
        return this.dataSecurity;
    }

    @Override
    public double[][] getCcost() {
        return this.ccost;
    }

    @Override
    public double[][] getCpucost() {
        return this.cpucost;
    }

    @Override
    public int [] getCloud() {
        return this.cloud;
    }

    @Override
    public int[][] getSsecurity() {
        return this.ssecurity;
    }
	@Override
	public double[][] getStorageTime() {
		// TODO Auto-generated method stub
		
		return this.StorageTime;
	}
	@Override
	public double[] getStorageCost() {
		// TODO Auto-generated method stub
		return this.StorageCost;
	}
}