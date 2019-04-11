package SOEN691.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class ThrowInFinallyVisitor extends ASTVisitor {
	public boolean throwsomething = false;

	@Override
	public boolean visit(ThrowStatement node) {
		this.throwsomething = true;
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	

}
