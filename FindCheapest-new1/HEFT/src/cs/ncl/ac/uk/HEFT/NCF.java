package cs.ncl.ac.uk.HEFT;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import cs.ncl.ac.uk.log.LogAccess;
import cs.ncl.ac.uk.security.Security;
import cs.ncl.ac.uk.test.WorkflowModel;
import cs.ncl.ac.uk.test.WorkflowTemplate;

public class NCF {
	int[][] workflow;
    int [][] ccost;
    int [][] cpucost;
    int [][] deployment;
    int [][] finaldeployment;
    int aveCom=0;
    ArrayList<Integer> root=new ArrayList<Integer>();
    ArrayList<Integer> leaf=new ArrayList<Integer>();
    HashMap<Integer,Integer> rank=new HashMap<Integer,Integer>();
    HashMap<Integer,Integer> SOC=new  HashMap<Integer,Integer>();
    Security checking;
    
    public NCF(WorkflowTemplate getInfo){
    	this.workflow=getInfo.getWorkflow();
        this.ccost=getInfo.getCcost();
        this.cpucost=getInfo.getCpucost();
        this.checking=new Security(getInfo);
        deployment=new int[workflow.length][ccost.length];
        finaldeployment=new int[workflow.length][ccost.length];
        averageCommunication();
        startNode();
    }
    
    
    public int NCFAlgorithm(){
    	preDeploy();
   	 LinkedList<Integer> queue = sortRank(rank);
   //	 System.out.println(queue);
   	 while(!queue.isEmpty()){
   		 for(int i=0;i<queue.size();i++){
   			 int block=queue.get(i);
   			ArrayList<Integer> parentNodes=getParents(block);
		//	if(!parentNodes.isEmpty()){
				if(deployCheck(parentNodes)||parentNodes.isEmpty()){
				System.out.println(block);
					ArrayList<Integer> offSprings=getOffSpring(block);
			   		
				   	//		 if(getParents(block).isEmpty()){
				   				ArrayList<Integer>deploy=isCross(offSprings);
				   				ArrayList<Integer>newDeploy=checkCross(offSprings,block,parentNodes);
				   				
				   				if(deploy.isEmpty()&&newDeploy.isEmpty()){
				   			//		System.out.println("fffffff");

				   				    int min = 0;
				                    int cloud = 0;
				                        for (int a = 0; a < ccost.length; a++) {
				                            if (checking.allowedDeploy(block, a)) {
				                            
				                                int SOCcost = newSOC(block, a, parentNodes);
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
				                 
	   	     						  setfianlDeploy(block,cloud);
	   	  	                          queue.remove((Object)block);
				   				}else{
				   					if(!deploy.isEmpty()&&!newDeploy.isEmpty()){
				   						
				   						if(deploy.get(deploy.size()-1)>newDeploy.get(newDeploy.size()-1)){
				   		//					System.out.println("aaaaaa");
						   					 int cloud=deploy.get(deploy.size()-2);
				   	    					 for(int a=0;a<deploy.size()-2;a++){
				   	    						 int deployBlock=deploy.get(a);
				   	    						 if(isoccupied(deployBlock)==-1){
				   	    							 setfianlDeploy(deployBlock,cloud);
				   		    						 queue.remove((Object)deployBlock);
				   	    						 }
				   	    					  }
						   					}else{
						//   						System.out.println("zzzzz");
						   					 int cloud=newDeploy.get(newDeploy.size()-2);
				   	    					 for(int a=0;a<newDeploy.size()-2;a++){
				   	    						 int deployBlock=newDeploy.get(a);
				   	    						 if(isoccupied(deployBlock)==-1){
				   	    							 setfianlDeploy(deployBlock,cloud);
				   		    						 queue.remove((Object)deployBlock);
				   	    						 }
				   	    					  }
						   					}
				   					}else{
				   						if(!deploy.isEmpty()){
				   		//					System.out.println("hhhhhhh");
				   						 int cloud=deploy.get(deploy.size()-2);
			   	    					 for(int a=0;a<deploy.size()-2;a++){
			   	    						 int deployBlock=deploy.get(a);
			   	    						 if(isoccupied(deployBlock)==-1){
			   	    							 setfianlDeploy(deployBlock,cloud);
			   		    						 queue.remove((Object)deployBlock);
			   	    						 }
			   	    					  }
				   						}else{
				   			//				System.out.println("ssssssss");
				   						 int cloud=newDeploy.get(newDeploy.size()-2);
			   	    					 for(int a=0;a<newDeploy.size()-2;a++){
			   	    						 int deployBlock=newDeploy.get(a);
			   	    						 if(isoccupied(deployBlock)==-1){
			   	    							 setfianlDeploy(deployBlock,cloud);
			   		    						 queue.remove((Object)deployBlock);
			   	    						 }
			   	    					  }
				   						}
				   					}
				   					
				   				}
				   			/*	 if(deploy.size()>offSprings.size()+1){
				   					 // empty means this node is not ready to deploy
				   	   				 if(deploy.isEmpty()){
				   	   					 if(getParents(block).isEmpty()){
				   	   						 int cloud = getCloud(block);
				   							 setfianlDeploy(block, cloud);
				   			                    queue.remove((Object)block);
				   		//	                    System.out.println("head");
				   	   					 }
				   	   				 }else{
				   	   					 if(deploy.size()==1 && deploy.get(0)==-1){
				   	   				//		 ArrayList<Integer> parent = getParents(block);
				   	   				//		if (deployCheck(parent) || parent.isEmpty()) {
				   	   							int cloud= returnDeployedCloud(block);
				   	     						  setfianlDeploy(block,cloud);
				   	  	                         queue.remove((Object)block);
				   	  //	                        System.out.println(block);
				   	  	 //                       System.out.println("cod");
				   	   					//	}
				   	   						 
				   	   					 }else{
				   	   						 int cloud=deploy.get(deploy.size()-1);
				   	    					 for(int a=0;a<deploy.size()-1;a++){
				   	    						 int deployBlock=deploy.get(a);
				   	    						 if(isoccupied(deployBlock)==-1){
				   	    							 setfianlDeploy(deployBlock,cloud);
				   		    						 queue.remove((Object)deployBlock);
				   	    						 }
				   	    					  }
				   	   					 }
				   	   				 }
				   				 }else{
				   					
				   			//		System.out.println(newDeploy);
				   					if(newDeploy.isEmpty()){
				   						int cloud= returnDeployedCloud(block);
		   	     						  setfianlDeploy(block,cloud);
		   	  	                          queue.remove((Object)block);
				   					}else{
				   					 int cloud=newDeploy.get(newDeploy.size()-1);
		   	    					 for(int a=0;a<newDeploy.size()-1;a++){
		   	    						 int deployBlock=newDeploy.get(a);
		   	    						 if(isoccupied(deployBlock)==-1){
		   	    							 setfianlDeploy(deployBlock,cloud);
		   		    						 queue.remove((Object)deployBlock);
		   	    						 }
		   	    					  }
				   					}
				   				 }*/
				   	
				     }
	//		}
   			
   		
   	//		 }
   		 }
   	 }
     	print(finaldeployment);
    	return theCost(root,0,new ArrayList<Integer>());
    }
    
    // when a node has lots of son
    private ArrayList<Integer> checkCross(ArrayList<Integer> offSprings,int block,ArrayList<Integer> parents){
    	ArrayList<Integer> deploySet=new ArrayList<Integer>();
    	int min=Integer.MAX_VALUE;
    	int cloud=0;
    	// put all nodes in one cloud
    	for(int a=0;a<ccost.length;a++){
    		boolean isValid=false;
    		if(checking.allowedDeploy(block, a)){
    			isValid=true;
    			for(int i=0;i<offSprings.size();i++){
    				int node=offSprings.get(i);
    				if(!checking.allowedDeploy(node, a)){
    					isValid=false;
    					break;
    				}
    			}
    		}
    		
    		if(isValid){
    			int comCost=thecommunication(parents,block,a);
    			int costoffSprings=0;
    			for(int h=0;h<offSprings.size();h++){
    				int offNode=offSprings.get(h);
    				costoffSprings+=cpucost[offNode][a];
    			}
    			if(min>comCost+costoffSprings){
    				min=comCost+costoffSprings;
    				cloud=a;
    			}
    		}
    	}
    	
    //	System.out.println(cloud);
    	
    	int predeploycost=SOC.get(block);
    	for(int f=0;f<offSprings.size();f++){
    		int offNode=offSprings.get(f);
    		predeploycost+=SOC.get(offNode);
    	}
    	 
    	if(min<predeploycost){
    		deploySet=(ArrayList<Integer>) offSprings.clone();
    		deploySet.add(block);
    		deploySet.add(cloud);
    		deploySet.add(min);
    	}
    	
    	return deploySet;
    }
    
    // calculate the final cost
    private int theCost(ArrayList<Integer> start, int cost,ArrayList<Integer> isVisited){
 	   
    	if(start.isEmpty()){
    		return cost;
    	}else{
    		ArrayList<Integer> offSpring=new ArrayList<Integer>();
    
    		for(int a=0;a<start.size();a++){
    		
    			int startNode=start.get(a);
    			int startCloud=isoccupied(startNode);
    			if(!isVisited.contains(startNode)){
    				System.out.println("Node:"+startNode);
    				cost+=cpucost[startNode][startCloud];
    				System.out.println(cpucost[startNode][startCloud]);
    				isVisited.add(startNode);
    			
    			// get nodes' offspring
    			for(int i=0;i<workflow.length;i++){
    				if(workflow[startNode][i]>0){
    				
    					int endNode=i;
    					int endCloud=isoccupied(endNode);
    					int comCost=communicationCost(startNode,endNode,startCloud,endCloud);
    					System.out.println(comCost);
    					cost+=comCost;
    					if(!offSpring.contains(i)){
    						offSpring.add(i);
    					}
    				}
    			}
    			isVisited.add(startNode);
    		}
    		}
    		return theCost(new ArrayList<Integer>(offSpring),cost,isVisited);
    	}
    	
    }
    
    // get offspring node
    private ArrayList<Integer> getOffSpring(int node){
    	ArrayList<Integer> offSpring=new ArrayList<Integer>();
    	for(int i=0;i<workflow.length;i++){
    		if(workflow[node][i]>0){
    			if(!offSpring.add(i)){
    				offSpring.add(i);
    			}
    		}
    	}
    	return offSpring;
    }
    
    private ArrayList<Integer> isCross(ArrayList<Integer> offSprings){
    	int max=Integer.MIN_VALUE;
    	int son=0;
    	ArrayList<Integer> deploySet=new ArrayList<Integer>();
    	for(int a=0;a<offSprings.size();a++){
    		int node=offSprings.get(a);
    		int SOCcost=SOC.get(node);
    		if(max<SOCcost){
    			max=SOCcost;
    			son=node;
    		}
    	}
    	
    	ArrayList<Integer> siblingNode=getParents(son);
    	ArrayList<Integer> UDsiblingNode=unDeploySibling(siblingNode);
    	// check the siblingNode's parent's nodes are all deployed
    	if(isDeployed(UDsiblingNode)){
    		int min=Integer.MAX_VALUE;
        	int cloud=0;
        	for(int a=0;a<ccost.length;a++){
        		boolean isValid=false;
        		if(checking.allowedDeploy(son, a)){
        			isValid=true;
        			for(int i=0;i<UDsiblingNode.size();i++){
        				int parentNode=UDsiblingNode.get(i);
        				if(!checking.allowedDeploy(parentNode, a)){
        					isValid=false;
        					break;
        				}
        			}
        			if(isValid){
        				if(min>miniCost(son,UDsiblingNode,a)){
        					min=miniCost(son,UDsiblingNode,a);
        					cloud=a;
        							
        				}
        			}
        		}
        	}
        	
        	int currentCost=parentCost(UDsiblingNode);
        	if(min<(currentCost+max)){
        		deploySet=(ArrayList<Integer>) UDsiblingNode.clone();
        		deploySet.add(son);
        		deploySet.add(cloud);
        		deploySet.add(min);
        	}
    	}
    	
    	return deploySet;
    }
    
 // check the siblingNode's parent's nodes are all deployed
    private boolean isDeployed(ArrayList<Integer> UDsiblingNode){
    	boolean allDeployed=true;
    	for(int a=0;a<UDsiblingNode.size();a++){
    		ArrayList<Integer> parentNodes=getParents(UDsiblingNode.get(a));
    		if(!deployCheck(parentNodes,UDsiblingNode)){
    			allDeployed=false;
    			break;
    		}
    	}
    	
    	return allDeployed;
    }
    
    // check the set of node is deployed or its parents include in the set
    private boolean deployCheck(ArrayList<Integer> parentNodes,ArrayList<Integer> UDsiblingNode){
    	boolean isDeployed=true;
    	for(int a=0;a<parentNodes.size();a++){
    		if(isoccupied(parentNodes.get(a))==-1){
    			if(!UDsiblingNode.contains(parentNodes.get(a))){
    				isDeployed=false;
        			break;
    			}
    		}
    	}
    	return isDeployed;
    }
    
    // check the set of node is deployed 
    private boolean deployCheck(ArrayList<Integer> parentNodes){
    	boolean isDeployed=true;
    	for(int a=0;a<parentNodes.size();a++){
    		if(isoccupied(parentNodes.get(a))==-1){
    				isDeployed=false;
        			break;
    		}
    	}
    	return isDeployed;
    }
    
    private int parentCost(ArrayList<Integer> siblingNode){
    	int total=0;
    	for(int a=0;a<siblingNode.size();a++){
    		int node=siblingNode.get(a);
    		total+=SOC.get(node);
    	}
    	return total;
    }
    
    /*
     * if the SOCcost+the cost of each node is greater than the minimize cost of put all node in one valid cloud
     * put them in one cloud. otherwise, keep using SOC
     * */
    
    private int miniCost(int son, ArrayList<Integer>  siblingNode, int cloud){
    	int totalcost=cpucost[son][cloud];
    	for(int a=0;a<siblingNode.size();a++){
    		int node=siblingNode.get(a);
    		if(!getParents(node).isEmpty()){
    			int cost=thecommunication(getParents(node),node,cloud);
    			totalcost+=cost;
    		}
    		totalcost+=cpucost[node][cloud];
    	}
    
    	return totalcost;
    }
    // communication cost with node's parent nodes
    private int thecommunication(ArrayList<Integer> parents,int node,int nodeCloud){
    	int cost=0;
    	for(int a=0;a<parents.size();a++){
    		int parentNode=parents.get(a);
    		int parentCloud=isoccupied(parentNode);
    		if(parentCloud==-1){
    			return 0;
    		}
    		cost+=communicationCost(parentNode,node,parentCloud,nodeCloud);
    	}
    	
    	return cost;
    }
    private ArrayList<Integer> unDeploySibling(ArrayList<Integer> siblingNode){
    	ArrayList<Integer> unDeploySi=new ArrayList<Integer>();
    	for(int a=0;a<siblingNode.size();a++){
    		int node=siblingNode.get(a);
    		if(isoccupied(node)==-1){
    			unDeploySi.add(node);
    		}
    	}
    	return unDeploySi;
    }
    
    private void preDeploy(){
    	 ranking();
    	// no-descend order sort the nodes
	        LinkedList<Integer> queue = sortRank(rank);
 	        while (!queue.isEmpty()) {

	            for (int i = 0;i<queue.size();i++) {
	            	
	                int block = queue.get(i);
	             
	                if (getParents(block).isEmpty()) {
	                	
	                    int cloud = getCloud(block);
	                    setDeployment(block, cloud);
	                    queue.remove((Object)block);
	                    int SOCcost=cpucost[block][cloud];
	                    SOC.put(block, SOCcost);
	          //          total += SOCcost;

	                } else {
	                	
	                    ArrayList<Integer> parent = getParents(block);
	                    int min = 0;
	                    int cloud = 0;
	                    if (!getUndeploy(parent)) {
	                        for (int a = 0; a < ccost.length; a++) {
	                            if (checking.allowedDeploy(block, a)) {
	                            
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
	                        SOC.put(block, min);
	                  //      total+=min;
	                    }
	                }
	            }
	        }
	     //   print(deployment);
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
    
    private boolean getUndeploy(ArrayList<Integer> parent){
        //	ArrayList<Integer> undeployedParent=new ArrayList<Integer>();
    
    //	print(deployment);
        for(int a=0;a<parent.size();a++){
            int node=parent.get(a);
            if(returnDeployedCloud(node)==-1){
                return true;
            }
        }

        return false;
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
    // sum of the communication cost depend of new deployment
    
    public int newSOC(int node,int cloud,ArrayList<Integer> parent){
    	int sum=0;
    	  int computCost=cpucost[node][cloud];
          sum+=computCost;
          for(int a=0;a<parent.size();a++){
              int singleNode=parent.get(a);
              int parentCloud= isoccupied(singleNode);
              if(parentCloud==-1){
                  return -1;
              }else{
                  sum+=communicationCost(singleNode,node,parentCloud,cloud);
              }
          }
          
    	return sum;
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
    
    // return the communication cost between two deployed nodes
    private int communicationCost(int startNode,int endNode,int startCloud,int endCloud){
        if(startCloud==endCloud){
            return 0;
        }else{
            return workflow[startNode][endNode]*ccost[startCloud][endCloud];
        }
    }
    
 // get cheapest computing cloud
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
    
    // pre deployment 
    private void setDeployment(int node,int cloud){
        deployment[node][cloud]=1;
    }
    
   // final deployment
    private void setfianlDeploy(int node,int cloud){
    	
    	finaldeployment[node][cloud]=1;
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
        upForward(leaf, rank, visited);
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
    
    // ranking by average
    private int rankCost(int node,HashMap<Integer,Integer> rank){
        int rankcost=0;
        // the communication cost from one node to its offspring nodes
        //	int communCost=0;
        // the single step ranking cost
        int singleRank=0;
        for(int a=0;a<workflow.length;a++){
            if(workflow[node][a]>0){
             //   System.out.println(node);
                //   System.out.println(node);
                int son=a;
                //    System.out.println(son);
                if(rank.keySet().contains(son)){
                    singleRank+=avCommunication(node,son)+rank.get(son);
                }

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
    
    private int isoccupied(int node){
   	 for(int a=0;a<finaldeployment[node].length;a++){
	            if(finaldeployment[node][a]==1){
	                return a;
	            }
	        }
	        return -1;
   }
    private void print(int[][] matrix){
   	 for(int h=0;h<matrix.length;h++){
	            for(int f=0;f<matrix[h].length;f++){
	                System.out.print(matrix[h][f]+",");
	            }
	            System.out.println("");
	        }
   }
    // get the startNode
    private void  startNode(){
    	for (int a=0; a<workflow.length; a++){
    		boolean isroot=true;
    		for(int i=0;i<workflow.length;i++){
    			if(workflow[i][a]>0){
    				isroot=false;
    				break;
    			}
    		}
    		if(isroot==true){
    			root.add(a);
    		}
    	}
    }
	    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
	        LogAccess logAccess = new LogAccess("result");
	        logAccess.init();
	        String url="/Users/zhenyuwen/git/FindCheapest-new1/HEFT/";
	    //    for(int x = 2 ; x<= 5;x ++){
	            for(int y = 2;y<= 30;y++){
	                long result = 0;
	                double cost = 0;
	                for(int i = 0;i<10;i++){
	                    WorkflowModel workflowModel =WorkflowModel.read(url+"newmodel" + 5 + "" + y + "" + i);
//	                    print(workflowModel.getWorkflow());
	                    NCF n = new NCF(workflowModel);
	                    long before = System.nanoTime();

	                    cost+=n. NCFAlgorithm();
	              //      System.out.println(cost);
	              //      System.out.println("-------------------------------------");
//	                    double minCost = Double.MAX_VALUE;
//	                    for(List<Integer> list: lists){
//	                        double temp = n.calCost(list);
//	                        if(minCost>temp){
//	                            minCost = temp;
//	                        }
//	                    }
//	                    cost += n.calCost(lists);
	                    long after = System.nanoTime();
	                    long time = TimeUnit.MICROSECONDS.convert(after-before,TimeUnit.NANOSECONDS);
	                    result+=time;

	               }


	                result/=10;
	                cost/=10;
	                logAccess.insertTuple(5 +"",y+"",result+"",cost+"");
	           //     System.out.println(x+" "+y);
	       //     }

	        }
	       logAccess.output2CSV("/Users/zhenyuwen/Desktop/result", "NFC.csv");
//	        algorithm n = new algorithm(new Workflow());
//	        System.out.println("-----");
//	        System.out.println(n.HEFTalgorithm());
	    }
}
