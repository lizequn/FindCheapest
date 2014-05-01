package cs.ncl.ac.uk.test;

import java.io.*;

/**
 * @author ZequnLi
 *         Date: 14-5-1
 */
public class WorkflowModel implements WorkflowTemplate,Serializable {
    int[][] workflow;
    int[][] dataSecurity;
    int [][] ccost;
    int [][] cpucost;
    int [] cloud;
    int [][] ssecurity;

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