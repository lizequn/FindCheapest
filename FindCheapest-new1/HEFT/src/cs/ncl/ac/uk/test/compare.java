package cs.ncl.ac.uk.test;

import java.io.IOException;
import java.util.List;

import cs.ncl.ac.uk.HEFT.algorithm;
import cs.ncl.ac.uk.HEFT.newHEFT;
import cs.ncl.ac.uk.HEFT.oneStepForward;
import cs.ncl.ac.uk.gready.gready;
import cs.ncl.ac.uk.normal.Normal;

public class compare {
	public static void main(String args[]) throws ClassNotFoundException, IOException{
		 String url="/Users/zhenyuwen/git/FindCheapest-new1/";
		 int x=5;
		 for(int y=2;y<12;y++){
	   	 for(int i = 0;i<10;i++){
	//		 WorkflowModel workflowModel =WorkflowModel.read(url+"model" + x + "" + 3 + "" + 1);
		 WorkflowModel workflowModel =WorkflowModel.read(url+"model" + x + "" + y + "" + i);
	//	 	int workflow[][] =workflowModel.getWorkflow();
	//	            Workflow workflowModel=new Workflow();
					algorithm n = new algorithm(workflowModel);
	//				gready n2=new gready(workflowModel);
			//		Normal n1 = new Normal(workflowModel);
					newHEFT n3=new newHEFT(workflowModel);
					oneStepForward n4=new oneStepForward(workflowModel);
				/*	print(workflow);
					System.out.println("....................");
					
					printcom(workflowModel.ccost);
					System.out.println("....................");
					printcpu(workflowModel.getCpucost());
					System.out.println("....................");*/
				//	List<Integer> lists =n1.sortBest();
				//	n.HEFTalgorithm();
					System.out.println("HEFT:"+n.HEFTalgorithm());
	//				System.out.println("Gready:"+n2.greadyAlgorithm());
					System.out.println("newHEFT:"+n3.newAlgorithm());
					System.out.println("oneStep:"+n4.newAlgorithm());
					
		 }
			 
		 }
		
	}
	


	private static void print(int [][] workflow){
		for(int a=0;a<workflow.length;a++){
			for(int i=0;i<workflow[a].length;i++){
				System.out.print(workflow[a][i]);
			}
			System.out.println("");
		}
	}
	
	private static void printcom(int [][] result){
		 for(int h=0;h<result.length;h++){
	            for(int f=0;f<result[h].length;f++){
	                System.out.print(result[h][f]+",");
	            }
	            System.out.println("");
	        }
	}
	
  private static void printcpu(int [][] result){
	  for(int h=0;h<result.length;h++){
          for(int f=0;f<result[h].length;f++){
              System.out.print(result[h][f]+",");
          }
          System.out.println("");
      }
	}
}
