package cs.ncl.ac.uk.HEFT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import cs.ncl.ac.uk.test.*;


public class algorithm {
	int[][] workflow;
	int [][] ccost;
	int [][] cpucost;
	int [][] deployment;
	int aveCom=0;
	ArrayList<Integer> root=new ArrayList<Integer>();
	ArrayList<Integer> leaf=new ArrayList<Integer>();
	HashMap<Integer,Integer> rank=new HashMap<Integer,Integer>();
	
	public algorithm(){
		Workflow getInfo=new Workflow();
		this.workflow=getInfo.getWorkflow();
		this.ccost=getInfo.comCost();
		this.cpucost=getInfo.deployCost();
		deployment=new int[workflow.length][ccost.length];
		averageCommunication();
	}
	
	// running HEFT algorithm
	public void HEFTalgorithm(){
		// total cost
		
		int total=0;
		// ranking the nodes
		ranking();
		// no-descend order sort the nodes
		LinkedList<Integer> queue=sortRank(rank);
		for(int block:queue){
			if(getParents(block).isEmpty()){
				int cloud=getCloud(block);
				setDeployment(block,cloud);
				total+=cpucost[block][cloud];
			}else{
				ArrayList<Integer> parent=getParents(block);
				int min=0;
				int cloud=0;
				for(int a=0;a<ccost.length;a++){
					int SOCcost=SOC(block,a,parent);
					if(SOCcost==-1){
						System.out.println("parent node has not been deployed");
					}else{
						if(min==0){
							min=SOCcost;
							cloud=a;
						}else{
							if(min>SOCcost){
								min=SOCcost;
								cloud=a;
							}
						}
					}
					
					}
			    	setDeployment(block,cloud);
				    total+=min;
			    }
		   }
		System.out.println(total);
		for(int h=0;h<deployment.length;h++){
			for(int f=0;f<deployment[h].length;f++){
				System.out.print(deployment[h][f]);
			}
			System.out.println("");
		}
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
	// ranking all node 
	public void ranking(){
		// store the ranked value of node
		
		ArrayList<Integer> visited=new ArrayList<Integer>();
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
				visited.add(a);
				int leafCost=computeAva(a);
				rank.put(a, leafCost);
			}
		}
		upForward(leaf,rank,visited);
	
	
	}
	
	private LinkedList<Integer> sortRank(final HashMap<Integer,Integer> rank){
		LinkedList<Integer> queue = new LinkedList<Integer>();
	    Comparator<Integer> valueComparator =  new Comparator<Integer>(){
			public int compare(Integer o1, Integer o2) {
				int compare=rank.get(o1).compareTo(rank.get(o2));
				if(compare==0){
					return 1;
				}else{
					return compare;
				}
			}
	    };
	    
	    Map<Integer,Integer> sortedByValues=new TreeMap<Integer,Integer>(valueComparator);
	    	sortedByValues.putAll(rank);
	    	for(Map.Entry<Integer, Integer>entry: sortedByValues.entrySet()){
	    		int key=entry.getKey();
	    		queue.addFirst(key);
	    	}
		return queue;
	}
	private void upForward(ArrayList<Integer>offSpringNodes,HashMap<Integer,Integer> rank,ArrayList<Integer> visited){
		ArrayList<Integer> parentNodes=new ArrayList<Integer>();
		for(int a=0;a<offSpringNodes.size();a++){
			int nodeName=offSpringNodes.get(a);
			for(int i=0;i<workflow[nodeName].length;i++){
				if(workflow[i][nodeName]>0){
					int parent=i;
					if(!parentNodes.contains(parent)){
						parentNodes.add(parent);
					}
					if(!visited.contains(parent)){
						int rankcost=rankCost(parent,rank);
						rank.put(parent, rankcost);
						visited.add(parent);
					}
				}
			}
		}
		if(!parentNodes.isEmpty()){
			upForward(parentNodes,rank,visited);
		}
	}
	
	private int rankCost(int node,HashMap<Integer,Integer> rank){
		int rankcost=0;
		// the communication cost from one node to its offspring nodes
	//	int communCost=0;
		// the single step ranking cost
		int singleRank=0;
		for(int a=0;a<workflow.length;a++){
			if(workflow[node][a]>0){
				int son=a;
				singleRank+=avCommunication(node,son)+rank.get(son);
			}
		}
		
		int computeCost=computeAva(node);
		rankcost=singleRank+computeCost;
		return rankcost;
	}
	
	private int avCommunication(int startNode,int endNode){
		int avcomCost=0;
		int dataSize=workflow[startNode][endNode];
		avcomCost=aveCom*dataSize;
		
		return avcomCost;
	}
	
	private void averageCommunication(){
		int sum=0;
		for(int a=0;a<ccost.length;a++){
			for(int i=0;i<ccost[a].length;i++){
				sum+=ccost[a][i];
			}
		}
		int ave=sum/(2*ccost.length);
		this.aveCom=ave;
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
	// get cheapest computing cloud
	private int getCloud(int node){
		int cloud=0;
		int min=0;
		for(int a=0;a<cpucost[node].length;a++){
			if(min==0){
				min=cpucost[node][a];
				cloud=a;
			}else{
				if(min>cpucost[node][a]){
					min=cpucost[node][a];
					cloud=a;
				}
			}
			
		}
		return cloud;
	}
	
	// put deployment
	private void setDeployment(int node,int cloud){
		deployment[node][cloud]=1;
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
	public static void main(String[] args){
		algorithm test=new algorithm();
		test.HEFTalgorithm();
	}
}
