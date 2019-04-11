package SOEN691.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;

public class ReturnNullVisitor extends ASTVisitor{
	public boolean containsReturnNull = false;
	public boolean containsReturn = false;
	
	

	

	@Override
	public boolean visit(ReturnStatement node) {
		this.containsReturn = true;
		try {
			if(node.getExpression().toString().equals("null")) {
				this.containsReturnNull = true;
			}
		}catch(Exception e) {
			return super.visit(node);

		}

		// TODO Auto-generated method stub
		return super.visit(node);
	}
	

}
