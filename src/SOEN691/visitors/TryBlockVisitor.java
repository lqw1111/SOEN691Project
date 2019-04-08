package SOEN691.visitors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryBlockVisitor extends ASTVisitor {
	// tryBlocksList contains the number of the statement(No comment/blank line) in
	// the try block
	public Map<TryStatement, Integer> tryBlocksList = new HashMap<TryStatement, Integer>();

	@Override
	public boolean visit(TryStatement node) {

		tryBlocksList.put(node, node.getBody().statements().size());

		Block block = node.getBody();

		// TODO Auto-generated method stub
		return super.visit(node);
	}

}
