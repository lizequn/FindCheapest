package cs.ncl.ac.uk;

import cs.ncl.ac.uk.test.WorkflowModel;
import cs.ncl.ac.uk.test.WorkflowRandomCreator;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @author ZequnLi
 *         Date: 14-5-2
 */
public class Test {
    public static void main(String [] args) throws IOException {
        WorkflowModel workflowModel =new WorkflowRandomCreator().create(10,100,2);
        WorkflowModel.store(workflowModel,"model"+10+""+100+""+1);

    }
}
