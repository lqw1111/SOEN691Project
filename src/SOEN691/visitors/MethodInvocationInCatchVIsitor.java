package SOEN691.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationInCatchVIsitor extends ASTVisitor {
	
	public boolean abort = false;
	public boolean containsGetClause = false;
	// not record yet
//	public int invokedMethod = 0;
	public int countOfLog = 0;
	public boolean dummy =true;

	@Override
	public boolean visit(MethodInvocation node) {
		
		String methodFullName = node.toString();
		String methodName = node.getName().toString();
		
//		this.invokedMethod++;
		if(CheckLogLevel(methodName)) {
			this.countOfLog++;
		}
		else {
			this.dummy = false;
		}
		if(methodFullName.contains("System.exit("))
			this.abort = true;//here
		if(node.getName().toString().contains("getClause")) {
			this.containsGetClause=true;
		}

		
		
		
		
		
		
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	public  boolean CheckLogLevel(String name) {
		if(name.equals("info")) {
			return true;
		}
		if(name.equals("warning")) {
			return true;
		}
		if(name.equals("severe")) {
			return true;
		}
		if(name.equals("log")) {
			return true;
		}
		if(name.equals("finest")) {
			return true;
		}
		if(name.equals("finer")) {
			return true;
		}
		if(name.equals("fine")) {
			return true;
		}
		if(name.equals("exiting")) {
			return true;
		}
		if(name.equals("entering")) {
			return true;
		}
		if(name.equals("config")) {
			return true;
		}
		if(name.equals("debug")) {
			return true;
		}
		return false;

	}
	
	
	

}
