package SOEN691.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryBlockVisitor1 extends ASTVisitor {
	int startComment = -1;
	int endComment;

	HashSet<TryStatement> generalCatches = new HashSet<>();
	HashSet<TryStatement> indebtCatches = new HashSet<>();
	HashSet<TryStatement> emptyCatches = new HashSet<>();
	public TryBlockVisitor1(HashSet<TryStatement> previouslyFoundCatches) {
		indebtCatches.addAll(previouslyFoundCatches);
	}

	public TryBlockVisitor1(int startComment, int endComment) {
		this.startComment = startComment;
		this.endComment = endComment;

	}
	@Override
	public boolean visit(TryStatement node) {
		Block block = node.getBody();
		


		if(startComment > 0) {
			if(commentIsInNode(block)) {
				
				
				indebtCatches.add(node);
			}
			return super.visit(node);
		}

		return super.visit(node);
	}



	public HashSet<TryStatement> getIndebtCatches() {
		return indebtCatches;
	}

	public HashSet<TryStatement> getEmptyCatches() {
		return emptyCatches;
	}
	private boolean commentIsInNode(Block node) {
		int nodeStart = node.getStartPosition();
		int nodeEnd = nodeStart + node.getLength();

		if(startComment > nodeStart && endComment < nodeEnd) {
			return true;
		}
		return false;
	}


}
