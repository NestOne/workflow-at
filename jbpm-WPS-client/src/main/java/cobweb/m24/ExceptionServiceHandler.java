package cobweb.m24;

import java.util.Map;

import org.drools.core.process.instance.WorkItem;

public class ExceptionServiceHandler {
	public static String exceptionParameterName = "my.exception.parameter.name";

    

	  public void handleException(WorkItem workItem) {

	    System.out.println( "Handling exception caused by work item '" + workItem.getName() + "' (id: " + workItem.getId() + ")");

	        

	    Map<String, Object> params = workItem.getParameters();

	    Throwable throwable = (Throwable) params.get(exceptionParameterName);

	    throwable.printStackTrace();

	  }

	    

	  public String throwException(String message) {

	      throw new RuntimeException("Service failed with input: " + message );

	  }

	    

	  public static void setExceptionParameterName(String exceptionParam) { 

	      exceptionParameterName = exceptionParam;

	  }
}
