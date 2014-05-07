package cs.ncl.ac.uk.gready;

import java.util.ArrayList;
import java.util.LinkedList;

import cs.ncl.ac.uk.security.Security;
import cs.ncl.ac.uk.test.WorkflowTemplate;

public class gready {
	int[][] workflow;
    int [][] ccost;
    int [][] cpucost;
    int [][] deployment;
    int aveCom=0;
    Security checking;
	public gready(WorkflowTemplate getInfo){
		  this.workflow=getInfo.getWorkflow();
	        this.ccost=getInfo.getCcost();
	        this.cpucost=getInfo.getCpucost();
	        this.checking=new Security(getInfo);
	        deployment=new int[workflow.length][ccost.length];
	       
	}
	public int greadyAlgorithm(){
		 int total = 0;
		 LinkedList<Integer> queue =new  LinkedList<Integer>();
		 for(int a=0;a<workflow.length;a++){
			 queue.add(a);
		 }
	       while (!queue.isEmpty()) {

	            for (int i = 0;i<queue.size();i++) {
	            	
	                int block = queue.get(i);
	               
	                if (getParents(block).isEmpty()) {
	                
	                    int cloud = getCloud(block);
	                    setDeployment(block, cloud);
	                    queue.remove((Object)block);
	                    total += cpucost[block][cloud];

	                } else {
	                	
	                    ArrayList<Integer> parent = getParents(block);
	                    int min = 0;
	                    int cloud = 0;
	                    if (!getUndeploy(parent)) {
	                        for (int a = 0; a < ccost.length; a++) {
	                            if (checking.allowedDeploy(block, a)) {
	                     //       	System.out.println(a);
	                                int SOCcost = SOC(block, a, parent);
	                                if (SOCcost == -1) {
	                                  //  show = true;
	                                    System.out.println("parent node has not been deployed");
	                                } else {
	                                    if(min==0){
	                                        min=SOCcost;
	                                        cloud = a;
	                                    }else{
	                                        if (min > SOCcost) {
	                                            min = SOCcost;
	                                            cloud = a;
	                                        }
	                                    }

	                                }
	                            }

	                        }
	                        setDeployment(block,cloud);
	                        queue.remove((Object)block);

	                        total+=min;
	                    }
	                }
	            }
	        }
		 return total;
		 
	}
	
    private int getCloud(int node){
        int cloud=0;
        int min=0;
        for(int a=0;a<cpucost[node].length;a++){
            if(min==0 &&checking.allowedDeploy(node, a)){
                min=cpucost[node][a];
                cloud=a;
            }else{
                if(min>cpucost[node][a] &&checking.allowedDeploy(node, a)){
                    min=cpucost[node][a];
                    cloud=a;
                }
            }

        }
        return cloud;
    }
    
    
	   private boolean getUndeploy(ArrayList<Integer> parent){
	        //	ArrayList<Integer> undeployedParent=new ArrayList<Integer>();
	    	
	    	
	        for(int a=0;a<parent.size();a++){
	            int node=parent.get(a);
	            if(returnDeployedCloud(node)==-1){
	                return true;
	            }
	        }

	        return false;
	    }
	
    // get parent nodes
    private ArrayList<Integer> getParents(int node){
        ArrayList<Integer> parent=new ArrayList<Integer>();
        for(int i=0;i<workflow[node].length;i++){
            if(workflow[i][node]>0){
                if(!parent.contains(i)){
                    parent.add(i);
                }
            }
        }
        return parent;
    }
    
    // the sum of the communication cost from parent nodes
    public int SOC(int node,int cloud,ArrayList<Integer> parent){
        int sum=0;
        int computCost=cpucost[node][cloud];
        sum+=computCost;
        for(int a=0;a<parent.size();a++){
            int singleNode=parent.get(a);
            int parentCloud= returnDeployedCloud(singleNode);
            if(parentCloud==-1){
                return -1;
            }else{
                sum+=communicationCost(singleNode,node,parentCloud,cloud);
            }
        }
        return sum;
    }
	
	   
	    // get deployed cloud of nodes
	    private int returnDeployedCloud(int node){
	        for(int a=0;a<deployment[node].length;a++){
	            if(deployment[node][a]==1){
	                return a;
	            }
	        }
	        return -1;
	    }
	   // return the communication cost between two deployed nodes
    private int communicationCost(int startNode,int endNode,int startCloud,int endCloud){
        if(startCloud==endCloud){
            return 0;
        }else{
            return workflow[startNode][endNode]*ccost[startCloud][endCloud];
        }
    }
    
    // put deployment
    private void setDeployment(int node,int cloud){
        deployment[node][cloud]=1;
    }
}
