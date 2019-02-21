package SOEN691.visitors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.core.JavaElement;

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
		HashSet<String> tryExceptionStringSet = new HashSet<String>();
		
		
		
		// catch exception type
		ITypeBinding exceptionTypeInCatch = 
				node.getException().getType().resolveBinding();
		String wholeException = exceptionTypeInCatch.getQualifiedName();
		String exceptionNameInCatch = wholeException.substring(wholeException.lastIndexOf(".")+1,wholeException.length());
		
		// get try block
		TryStatement tryStatement = (TryStatement)node.getParent();
		Block tryBlock = tryStatement.getBody();
		List<ASTNode> tryBodies = tryBlock.statements();
		
		for(ASTNode nn: tryBodies) {
			// each statement in try
			if( nn instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement nVDS = (VariableDeclarationStatement)nn;
				VariableDeclarationFragment declarationFragment =  (VariableDeclarationFragment) nVDS.fragments().get(0);
				
				Expression initializer = declarationFragment.getInitializer();

				//TODO  To detect exception in variable declaration expression.
			}
			
			if (nn instanceof ExpressionStatement) {
				ExpressionStatement ex = (ExpressionStatement) nn;
				MethodInvocation mInvocation  = (MethodInvocation)ex.getExpression();
				IMethodBinding mDeclaration = mInvocation.resolveMethodBinding().getMethodDeclaration();
				
				IMethod imethod = (IMethod) mDeclaration.getJavaElement();
				ISourceRange javadocRange;
				try {
					javadocRange = imethod.getJavadocRange();
					 
					 if (javadocRange != null) {
//						 	IBuffer buf= imethod.getOpenable().getBuffer();
//							String javadocText = buf.getText(javadocRange.getOffset(), javadocRange.getLength());
//							// comment in javadoc, need to parse exception type.
						 	
							String javadocString = getJavadocFast(imethod);
							
							tryExceptionStringSet.addAll(FindExceptionsInJavadoc(javadocString));
						
							//Add All RunTime exception in try block
							
							
					
						}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				//Add all Non-Runtime exception in try block
				for(ITypeBinding b:mDeclaration.getExceptionTypes()) {
					String str = b.getQualifiedName();
					if(str.contains(".")) {
						String ss ;
						int flag = str.lastIndexOf(".")+1;
						ss = str.substring(flag, str.length());
						tryExceptionStringSet.add(ss);
						
					}
					else {
						tryExceptionStringSet.add(str);
					}
				
					tryExceptionSet.add(b);
				}

			}
			
		}
		//to compare exceptions between try and block
		boolean overcatch = true;
		
//		for(ITypeBinding itype : tryExceptionSet) {
//			if(itype.equals(exceptionTypeInCatch)) {
//				overcatch = false;
//			}
//			
//		}
//		
		for(String etype:tryExceptionStringSet) {
			if(etype.equals(exceptionNameInCatch)) {
				overcatch = false;
			}
		}
		
		
		if(overcatch) {
			overCatches.add(node);
		}
		
		
		
		//MuitipleLine logs
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
	
	private static String getJavadocFast(IMember member) throws JavaModelException {
		IBuffer buffer = member.getOpenable().getBuffer();

		ISourceRange javadocRange = member.getJavadocRange();
		String javadocText = buffer.getText(javadocRange.getOffset(), javadocRange.getLength());
		
			javadocText = javadocText.replaceAll("^/[*][*][ \t]*\n?", "");  // Filter starting /**
			javadocText = javadocText.replaceAll("\n?[ \t]*[*]/$", "");  // Filter ending */
			javadocText = javadocText.replaceAll("^\\s*[*]", "\n");  // Trim leading whitespace.
			javadocText = javadocText.replaceAll("\n\\s*[*]", "\n");  // Trim whitespace at beginning of line.
			javadocText = javadocText.replaceAll("<[^>]*>", "");  // Remove html tags.
			javadocText = javadocText.replaceAll("[{]@code([^}]*)[}]", "$1");  // Replace {@code foo} blocks with foo.
			javadocText = javadocText.replaceAll("&nbsp;", " ").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"");  // Replace html formatting.
		
		
//		javadocText = Flags.toString(member.getFlags()) + " " + JavaElement.getElementLabel(member, JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.F_PRE_TYPE_SIGNATURE) + "\n" + javadocText;
		return javadocText;
	}
	
	
	public List<String> FindExceptionsInJavadoc(String javadocText){
		List<String> exceptionList = new ArrayList<>();
		String[] array = javadocText.split(" ");
		for(int i =0;i<array.length;i++) {
			if(array[i].equals("@throws")) {
				exceptionList.add(array[i+1]);
				
			}
		}	
		return exceptionList;
	}
	public HashSet<CatchClause> getDestructiveWrappingCatches() {
		return destructiveWrappingCatches;
	}

	public HashSet<CatchClause> getOverCatches() {
		return overCatches;
	}
}
