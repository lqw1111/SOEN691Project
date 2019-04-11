package SOEN691.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class InvokedMethodsVisitor extends ASTVisitor {
	
	public int invoked_method_count = 0;

	@Override
	public boolean visit(MethodInvocation node) {
		this.invoked_method_count++;
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	
	

}
