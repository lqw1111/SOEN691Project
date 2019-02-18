package SOEN691.visitors;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class CatchClauseVisitor extends ASTVisitor{
	HashSet<CatchClause> generalCatches = new HashSet<>();
	HashSet<CatchClause> indebtCatches = new HashSet<>();
	HashSet<CatchClause> emptyCatches = new HashSet<>();
	HashSet<CatchClause> multipleLineCatches = new HashSet<CatchClause>();
	HashSet<CatchClause> destructiveWrappingCatches = new HashSet<CatchClause>();
	HashSet<CatchClause> overCatches = new HashSet<CatchClause>();
	
	public CatchClauseVisitor() {}
	
	public CatchClauseVisitor(HashSet<CatchClause> previouslyFoundCatches) {
		indebtCatches.addAll(previouslyFoundCatches);
	}

	
	@Override
	public boolean visit(CatchClause node) {
		
		//catch exceptions set in try block (init)
		HashSet<ITypeBinding> tryExceptionSet = new HashSet<ITypeBinding>();
		// catch exception type
		ITypeBinding exceptionTypeInCatch = 
				node.getException().getType().resolveBinding();
		
		// get try block
		TryStatement tryStatement = (TryStatement)node.getParent();
		Block tryBlock = tryStatement.getBody();
		List<ASTNode> tryBodies = tryBlock.statements();
		
		for(ASTNode nn: tryBodies) {
			// each statement in try
			if( nn instanceof VariableDeclarationStatement) {
				//TODO 
			}
			
			if (nn instanceof ExpressionStatement) {
				ExpressionStatement ex = (ExpressionStatement) nn;
				MethodInvocation mInvocation  = (MethodInvocation)ex.getExpression();
				IMethodBinding mDeclaration = mInvocation.resolveMethodBinding().getMethodDeclaration();
				for(ITypeBinding b:mDeclaration.getExceptionTypes()) {
					tryExceptionSet.add(b);
				}

			}
			
		}
		//to compare exceptions between try and block
		boolean overcatch = true;
		for(ITypeBinding itype : tryExceptionSet) {
			if(itype.equals(exceptionTypeInCatch)) {
				overcatch = false;
			}
			
		}
		if(overcatch) {
			overCatches.add(node);
		}
		
		int countOfLog = 0;
		
		MethodInvocationVisitor visitMethodInvocation = new MethodInvocationVisitor();
		Block block = node.getBody();

		List<ASTNode> bodies = block.statements();
		for(ASTNode nn: bodies) {
			if (nn instanceof ExpressionStatement) {
				ExpressionStatement ex = (ExpressionStatement) nn;
				MethodInvocation mInvocation  = (MethodInvocation)ex.getExpression();
				ITypeBinding type = mInvocation.resolveMethodBinding().getDeclaringClass();
				if (type.getQualifiedName().contentEquals("java.util.logging.Logger")) {
					countOfLog++;
				}
				
			}
			else if (nn instanceof ThrowStatement) {
				destructiveWrappingCatches.add(node);
				
			}
		}
		
		if (countOfLog >1) {
			multipleLineCatches.add(node);
		}
		return super.visit(node);
	}
	
	
	public HashSet<CatchClause> getMultipleLineLogCatches() {
		return multipleLineCatches;
	}
	
	public HashSet<CatchClause> getDestructiveWrappingCatches() {
		return destructiveWrappingCatches;
	}

	public HashSet<CatchClause> getOverCatches() {
		return overCatches;
	}
}
