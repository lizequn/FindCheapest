package cs.ncl.ac.uk.security;

import cs.ncl.ac.uk.test.Workflow;
import cs.ncl.ac.uk.test.WorkflowTemplate;

import java.util.ArrayList;

/**
 * @author ZequnLi
 *         Date: 14-5-1
 */
public class securityCheck {
    ArrayList<Integer> root=new ArrayList<Integer>();
    int [][] ssecurity;
    int[][] dsecurity;
    int [] cloudsecurity;

    // checking the security of the Workflow
	/*public boolean workflowSecurity(int[][] dsecurity,int [][] ssecurity){
		Workflow getInfo=new Workflow();
		this.dsecurity=getInfo.getDataSecurity();
		this.ssecurity=getInfo.getServiceSecurity();

		for(int node:root){
			ArrayList<Integer> offspring=new ArrayList<Integer>();
			for(int a=0;a<dsecurity[node].length;a++){
				if(dsecurity[node][a]>=0){
					offspring.add(a);
				}
			}
		}
		return true;
	}*/
    public securityCheck(WorkflowTemplate workflowTemplate){
        WorkflowTemplate getInfo = workflowTemplate;
        this.dsecurity=getInfo.getDataSecurity();
        this.ssecurity=getInfo.getSsecurity();
        this.cloudsecurity=getInfo.getCloud();
    }

    public boolean workflowSecurity(){
        for(int a=0;a<dsecurity.length;a++){
            for(int i=0;i<dsecurity[a].length;i++){
                if(dsecurity[a][i]>=0){
                    int startNode=a;
                    int sonNode=i;
                    if(!compare(startNode,sonNode)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean compare(int startNode,int sonNode){
        int clearanceNode=ssecurity[startNode][0];
        int locationNode=ssecurity[startNode][1];
        int clearancesonNode=ssecurity[sonNode][0];
        int locationsonNode=ssecurity[sonNode][1];
        int datalocation=dsecurity[startNode][sonNode];
        if(locationNode>datalocation||clearanceNode<locationNode||clearancesonNode<locationsonNode||datalocation>clearancesonNode){
            System.out.println(startNode);
            System.out.println(sonNode);
            return false;
        }
        return true;
    }

	/*private void getrootNode(int [][] dsecurity){
		for(int a=0;a<dsecurity.length;a++){
			boolean isRoot=true;
			for(int b=0;b<dsecurity.length;b++){
				if(dsecurity[b][a]>=0){
					isRoot=false;
					break;
				}
			}
			if(isRoot){
				root.add(a);
			}
		}

	}*/

    public boolean allowedDeploy(int node,int cloud){
        int location=ssecurity[node][1];
        int cloudlocation=cloudsecurity[cloud];
        if(cloudlocation>=location){
            return true;
        }
        return false;
    }

    public boolean allowedTransfer(int node,int cloud,int parentNode, int parentcloud){
        if(cloud==parentcloud){
            return true;
        }else{
            System.out.println(node);
            int dataSecurity= dsecurity[parentNode][node];
            int soncloudSecurity=cloudsecurity[cloud];
            int parentCloudSecurity=cloudsecurity[parentcloud];
            if(soncloudSecurity<dataSecurity||parentCloudSecurity<dataSecurity){
                return false;
            }
        }

        return true;
    }

	/*public static void main(String[] args){
		securityCheck test=new securityCheck();
		System.out.println(test.workflowSecurity());
	//	test.workflowSecurity();
		//int Workflow[][]=getInfo.getWorkflow();
	//	test.getrootNode(Workflow);
	}*/
}
