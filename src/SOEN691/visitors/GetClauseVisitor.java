package SOEN691.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class GetClauseVisitor extends ASTVisitor {
	boolean containsGetClause = false;

	@Override
	public boolean visit(MethodInvocation node) {
		
		if(node.getName().equals("getClause")) {
			this.containsGetClause=true;
		}
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	
	

}
