package cs.ncl.ac.uk.HEFT;

import java.util.ArrayList;
import java.util.HashMap;

import cs.ncl.ac.uk.test.*;


public class algorithm {
	int[][] workflow;
	int [][] ccost;
	int [][] cpucost;
	ArrayList<Integer> root=new ArrayList<Integer>();
	ArrayList<Integer> leaf=new ArrayList<Integer>();
	
	public algorithm(){
		workflow getInfo=new workflow();
		this.workflow=getInfo.getWorkflow();
		this.ccost=getInfo.comCost();
		this.cpucost=getInfo.deployCost();
	}
	public void ranking(){
		// store the ranked value of node
		HashMap<Integer,Integer> rank=new HashMap<Integer,Integer>();
		for(int a=0;a<workflow.length;a++){
			boolean isleaf=true;
			for(int i=0;i<workflow[a].length;i++){
				if(workflow[a][i]>0){
					isleaf=false;
					break;
				}
			}
			if(isleaf==true){
				leaf.add(a);
				int leafCost=computeAva(a);
				rank.put(a, leafCost);
			}
		}
	}
	
	private void traceBack(int node,){
		
	}
	// the average cost of each node
	private int computeAva(int node){
		int avCost = 0;
		int sumCost = 0;
		for(int a=0;a<cpucost[node].length;a++){
			sumCost+=cpucost[node][a];
		}
		avCost=sumCost/cpucost.length;
		return avCost;
	}
	public static void main(String[] args){
		algorithm test=new algorithm();
		test.ranking();
	}
}
