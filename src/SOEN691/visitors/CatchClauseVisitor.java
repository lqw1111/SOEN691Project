package SOEN691.visitors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.core.JavaElement;

public class CatchClauseVisitor extends ASTVisitor{
	HashSet<CatchClause> generalCatches = new HashSet<>();
	HashSet<CatchClause> indebtCatches = new HashSet<>();
	HashSet<CatchClause> emptyCatches = new HashSet<>();
	HashSet<CatchClause> multipleLineCatches = new HashSet<CatchClause>();
	HashSet<CatchClause> destructiveWrappingCatches = new HashSet<CatchClause>();
	HashSet<CatchClause> overCatches = new HashSet<CatchClause>();
	public HashMap<CatchClause,String> overCatchesDetails = new HashMap<CatchClause, String>();
	public int countOfCatchBlock = 0;

	public CatchClauseVisitor() {}

	public CatchClauseVisitor(HashSet<CatchClause> previouslyFoundCatches) {
		indebtCatches.addAll(previouslyFoundCatches);
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
	public List<String> FindRuntimeExceptions(IMethodBinding imb ) throws JavaModelException {
		List<String> exceptionList = new ArrayList<>();
		IMethod imethod = (IMethod) imb.getJavaElement();
		if(imethod == null) {
			return exceptionList;
		}
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
	public List<String> FindNonRuntimeExceptions(IMethodBinding imb) throws JavaModelException {
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
		countOfCatchBlock++;
		//catch exceptions set in try block (init)
//		HashSet<String> tryExceptionStringSet = new HashSet<String>();

		// catch exception type
		ITypeBinding exceptionTypeInCatch =
				node.getException().getType().resolveBinding();


		SingleVariableDeclaration svd = node.getException();
		Set<String> wholeExceptionsInCatch = new HashSet<String>();
		try {
			UnionType unionType = (UnionType)svd.getType();
			List<ASTNode> listUnionType = unionType.types();
			for(ASTNode ut :listUnionType) {

				wholeExceptionsInCatch.add(ut.toString());
			}
		}
		catch(Exception e) {
			String ss = exceptionTypeInCatch.getQualifiedName();
			String exceptionNameInCatch = ss.substring(ss.lastIndexOf(".")+1,ss.length());
			wholeExceptionsInCatch.add(exceptionNameInCatch);
		}


		//wholeExceptionsInCatch stores all exceptions in catch
		//exceptionNameInCatch store only one exception in catch in previous version.


//		String wholeException = exceptionTypeInCatch.getQualifiedName();

//		String exceptionNameInCatch = wholeException.substring(wholeException.lastIndexOf(".")+1,wholeException.length());

		// get try block
		TryStatement tryStatement = (TryStatement)node.getParent();
		Block tryBlock = tryStatement.getBody();
		MethodInvocationInTryVisitor mVisitor = new MethodInvocationInTryVisitor();

		tryBlock.accept(mVisitor);
		//result of exceptions in tryBlock is in mVisitor.ResultExceptionSet
		//All exception

		//to compare exceptions between try and block
		boolean overcatch = true;
		String exceptionsInTry = "";

//		for(String etype:mVisitor.ResultExceptionSet) {
//			exceptionsInTry = exceptionsInTry +", "+etype;
//			if(etype.equals(exceptionNameInCatch)) {
//				overcatch = false;
//				break;
//			}
//		}
		//previous version

		for(String etype:mVisitor.ResultExceptionSet) {
			exceptionsInTry = exceptionsInTry +", "+etype;

			for(String etypeInCatch: wholeExceptionsInCatch) {
				if(etype.equals(etypeInCatch)) {
					overcatch = false;
					break;
				}
			}

		}



		exceptionsInTry = exceptionsInTry.replaceFirst(", ", "");


		if(overcatch) {
			overCatches.add(node);
			StringBuilder sb = new StringBuilder();
			sb.append("Exceptions detected in the try block: ");
			sb.append(exceptionsInTry +"\n");
			sb.append("Exception detected in the catch clause: ");
//			sb.append(exceptionNameInCatch+"\n"); //previous
			for(String etypeInCatch: wholeExceptionsInCatch) {
				sb.append(etypeInCatch+ " ");
			}
			sb.append("\n");
			overCatchesDetails.put(node, sb.toString());

		}

		//MuitipleLine logs and destructive wrapping catches
		int countOfLog = 0;

		Block block = node.getBody();

		List<ASTNode> bodies = block.statements();
		for(ASTNode nn: bodies) {
			if (nn instanceof ExpressionStatement) {
				ExpressionStatement ex = (ExpressionStatement) nn;
				if(ex.getExpression() instanceof MethodInvocation) {
					MethodInvocation mInvocation  = (MethodInvocation)ex.getExpression();
					ITypeBinding type;
					try {
						 type = mInvocation.resolveMethodBinding().getDeclaringClass();
					}
					catch (Exception e) {
						continue;
					}




//					if (type.getQualifiedName().contentEquals("java.util.logging.Logger")) {
						String name = mInvocation.getName().toString();
						if(CheckLogLevel(name))
							countOfLog++;
//					}

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

	public  boolean CheckLogLevel(String name) {
		if(name.equals("info")) {
			return true;
		}
		if(name.equals("warning")) {
			return true;
		}
		if(name.equals("severe")) {
			return true;
		}
		if(name.equals("log")) {
			return true;
		}
		if(name.equals("finest")) {
			return true;
		}
		if(name.equals("finer")) {
			return true;
		}
		if(name.equals("fine")) {
			return true;
		}
		if(name.equals("exiting")) {
			return true;
		}
		if(name.equals("entering")) {
			return true;
		}
		if(name.equals("config")) {
			return true;
		}
		if(name.equals("debug")) {
			return true;
		}
		return false;

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

		return decl;


	}

	public MethodDeclaration FindMethodDeclaration(IMethodBinding binding ) {
		if(binding == null) {
			return null;
		}
		IJavaElement ije = binding.getJavaElement();
		if(ije == null) {
			return null;
		}
		Object obj = ije.getAncestor( IJavaElement.COMPILATION_UNIT );

		ICompilationUnit unit;
		if(obj != null) {
			unit = (ICompilationUnit)obj;
		}
		else {
			return null;
		}

		ASTParser parser = ASTParser.newParser( AST.JLS8 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT );
		parser.setSource( unit );
		parser.setResolveBindings( true );
		CompilationUnit cu = (CompilationUnit) parser.createAST( null );
		MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode( binding.getKey() );

		return decl;


	}

	public HashSet<CatchClause> getDestructiveWrappingCatches() {
		return destructiveWrappingCatches;
	}

	public HashSet<CatchClause> getOverCatches() {
		return overCatches;
	}
}
