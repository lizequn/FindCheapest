package cs.ncl.ac.uk.test;

import java.io.IOException;
import java.util.List;

import cs.ncl.ac.uk.HEFT.NCF;

import cs.ncl.ac.uk.ga.GA;
import cs.ncl.ac.uk.gready.gready;
import cs.ncl.ac.uk.normal.Normal;

public class compare {
	
	public static void main(String args[]) throws ClassNotFoundException, IOException{
		 String url="/Users/zhenyuwen/git/FindCheapest-new1/HEFT/";
		 int x=5;
		 for(int y=2;y<12;y++){
	  	 for(int i = 0;i<10;i++){
			 WorkflowModel workflowModel =WorkflowModel.read(url+"newmodel" + x + "" + y + "" + i);
	//	 WorkflowModel workflowModel =WorkflowModel.read(url+"newmodel" + x + "" + 15 + "" + 8);

		   //         Workflow workflowModel=new Workflow();
		 
	//	 			escWorkflow workflowModel=new escWorkflow();
	//	 			double workflow[][] =workflowModel.getWorkflow();
		//			
	//				gready n2=new gready(workflowModel);
					Normal n1 = new Normal(workflowModel);
	//				newHEFT n3=new newHEFT(workflowModel);
					NCF n5= new NCF(workflowModel); 
					 GA n4 = new GA(workflowModel);
		//			oneStepForward n4=new oneStepForward(workflowModel);
			//		print(workflow);
		//			System.out.println("....................");
					
		//			printcom(workflowModel.ccost);
		//			System.out.println("....................");
		//			printcpu(workflowModel.getCpucost());
		//			System.out.println("....................");
				
			//		cloudsecurity(workflowModel.cloud);
			//		System.out.println("....................");
			//		wSecurity(workflowModel.ssecurity);
			//		System.out.println("....................");
		//			dataSecurity(workflowModel.dataSecurity );
		//			System.out.println("....................");
					List<Integer> lists =n1.sortBest();
			//        System.out.println(lists);
				//	n.HEFTalgorithm();
	//				System.out.println("HEFT:"+n.HEFTalgorithm());
	//				System.out.println("Gready:"+n2.greadyAlgorithm());
					System.out.println("Sort:"+n1.calCost(lists));
					System.out.println("NCF:"+n5.NCFAlgorithm());
					System.out.println("GA:"+n4.begin(100,.8,.1));
	//				
		//			System.out.println("newHEFT:"+n3.newAlgorithm());
		//			System.out.println("oneStep:"+n4.newAlgorithm());
					
		 }
			 
	 }
		
	}
	


	private static void print(double [][] workflow){
		for(int a=0;a<workflow.length;a++){
			for(int i=0;i<workflow[a].length;i++){
				System.out.print(workflow[a][i]+",");
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
  
  private static void cloudsecurity(int [] result){
	 
          for(int f=0;f<result.length;f++){
              System.out.print(result[f]+",");
          }
          System.out.println("");
	}
  
  private static void wSecurity(int [][] result){
	  for(int h=0;h<result.length;h++){
          for(int f=0;f<result[h].length;f++){
              System.out.print(result[h][f]+",");
          }
          System.out.println("");
      }
  }
  
  private static void dataSecurity(int[][] result){
	  for(int h=0;h<result.length;h++){
          for(int f=0;f<result[h].length;f++){
              System.out.print(result[h][f]+",");
          }
          System.out.println("");
      }
  }
  
}
