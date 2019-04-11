package SOEN691.visitors;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;

public class CatchClauseVisitor1 extends ASTVisitor{
	int startComment = -1;
	int endComment;

	HashSet<CatchClause> generalCatches = new HashSet<>();
	HashSet<CatchClause> indebtCatches = new HashSet<>();
	HashSet<CatchClause> emptyCatches = new HashSet<>();


	public CatchClauseVisitor1(HashSet<CatchClause> previouslyFoundCatches) {
		indebtCatches.addAll(previouslyFoundCatches);
	}

	public CatchClauseVisitor1(int startComment, int endComment) {
		this.startComment = startComment;
		this.endComment = endComment;

	}

	@Override
	public boolean visit(CatchClause node) {


		if(startComment > 0) {
			if(commentIsInNode(node)) {
				
				
				indebtCatches.add(node);
			}
			return super.visit(node);
		}

		return super.visit(node);
	}



	public HashSet<CatchClause> getIndebtCatches() {
		return indebtCatches;
	}

	public HashSet<CatchClause> getEmptyCatches() {
		return emptyCatches;
	}



	private boolean commentIsInNode(CatchClause node) {
		int nodeStart = node.getStartPosition();
		int nodeEnd = nodeStart + node.getLength();

		if(startComment > nodeStart && endComment < nodeEnd) {
			return true;
		}
		return false;
	}



}
