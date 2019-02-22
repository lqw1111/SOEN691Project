package SOEN691.visitors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
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
	
	public List<String> FindExceptions(Block block) throws JavaModelException {
		List<String> exceptionList = new ArrayList<String>();
		
		List<ASTNode> bodies = block.statements();
		
		for(ASTNode node: bodies) {
			if (node instanceof ExpressionStatement) {
				ExpressionStatement ex = (ExpressionStatement) node;
				MethodInvocation mInvocation = (MethodInvocation) ex.getExpression();
				MethodDeclaration unitDeclaration = FindMethodDeclaration(mInvocation);
				
				//1. Add all runtime exception from javadoc
				exceptionList.addAll(FindRuntimeExceptions(mInvocation));
				//2. Add all non-runtime exception from declaration
				exceptionList.addAll(FindNonRuntimeExceptions(mInvocation));
				//3. Recursively call (Step to inside of the source code of the method.)
				if(unitDeclaration!=null) {
					exceptionList.addAll(FindExceptions(unitDeclaration.getBody()));
				}
			} 
			else if (node instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement nVDS = (VariableDeclarationStatement) node;
				VariableDeclarationFragment declarationFragment = (VariableDeclarationFragment) nVDS.fragments().get(0);

				Expression initializer = declarationFragment.getInitializer();

				// TODO To detect exception in variable declaration expression.
			}
			
			
			
		}
		return exceptionList;
		
	}

	public List<String> FindRuntimeExceptions(MethodInvocation mInvocation) throws JavaModelException {
		IMethodBinding imb = mInvocation.resolveMethodBinding().getMethodDeclaration();
		List<String> exceptionList = new ArrayList<>();
		IMethod imethod = (IMethod) imb.getJavaElement();
		ISourceRange javadocRange;
		javadocRange = imethod.getJavadocRange();
		if (javadocRange != null) {
			String javadocString = getJavadocFast(imethod);
			exceptionList.addAll(FindExceptionsInJavadoc(javadocString));
		}

		return exceptionList;

	}
	
	public List<String> FindNonRuntimeExceptions(MethodInvocation mInvocation) throws JavaModelException {
		IMethodBinding imb = mInvocation.resolveMethodBinding().getMethodDeclaration();
		List<String> exceptionList = new ArrayList<>();
		//Add all Non-Runtime exception in try block
		//Example:  void print() throw XXXException
		for(ITypeBinding b:imb.getExceptionTypes()) {
			String str = b.getQualifiedName();
			if(str.contains(".")) {
				String ss ;
				int flag = str.lastIndexOf(".")+1;
				ss = str.substring(flag, str.length());
				exceptionList.add(ss);
				
			}
			else {
				exceptionList.add(str);
			}
		
		}
		
		return exceptionList;
	}
	
	
	@Override
	public boolean visit(CatchClause node) {
		
		//catch exceptions set in try block (init)
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
		try {
			tryExceptionStringSet.addAll(FindExceptions(tryBlock));
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//to compare exceptions between try and block
		boolean overcatch = true;

		for(String etype:tryExceptionStringSet) {
			if(etype.equals(exceptionNameInCatch)) {
				overcatch = false;
				break;
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
	

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param   pathname  A pathname string
     * @throws  TestException
     *          If the <code>pathname</code> argument is <code>null</code>
     */
	public List<String> FindExceptionsInJavadoc(String javadocText){
		List<String> exceptionList = new ArrayList<>();
		String[] array = javadocText.split("\n");
		for(int i =0;i<array.length;i++) {
			if(array[i].contains("@throws")) {
				String[] temp = array[i].split(" ");
				for(String ss: temp) {
					if(ss.contains("Exception")) {
						exceptionList.add(ss);
						break;
					}
				}
			
				
			}
		}	
		return exceptionList;
	}
	public MethodDeclaration FindMethodDeclaration(MethodInvocation node) {
		
		IMethodBinding binding = (IMethodBinding) node.getName().resolveBinding();
		ICompilationUnit unit = (ICompilationUnit) binding.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
		if ( unit == null ) {
			return null;
		   // not available, external declaration
		}
		ASTParser parser = ASTParser.newParser( AST.JLS8 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT );
		parser.setSource( unit );
		parser.setResolveBindings( true );
		CompilationUnit cu = (CompilationUnit) parser.createAST( null );
		MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode( binding.getKey() );
		System.out.println();
		return decl;
		
		
	}
	
	public HashSet<CatchClause> getDestructiveWrappingCatches() {
		return destructiveWrappingCatches;
	}

	public HashSet<CatchClause> getOverCatches() {
		return overCatches;
	}
}
