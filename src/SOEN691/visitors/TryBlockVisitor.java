package SOEN691.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryBlockVisitor extends ASTVisitor{
	

	@Override
	public boolean visit(TryStatement node) {
		
		Block block = node.getBody();
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	

}
