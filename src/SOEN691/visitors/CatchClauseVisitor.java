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
import org.eclipse.jdt.core.dom.LineComment;
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

import SOEN691.handlers.DetectException;
import SOEN691.patterns.ExceptionFinder;

public class CatchClauseVisitor extends ASTVisitor{
	public String visitFile = "";
	
	
	HashSet<CatchClause> generalCatches = new HashSet<>();
	HashSet<CatchClause> indebtCatches = new HashSet<>();
	HashSet<CatchClause> emptyCatches = new HashSet<>();
	HashSet<CatchClause> multipleLineCatches = new HashSet<CatchClause>();
	HashSet<CatchClause> destructiveWrappingCatches = new HashSet<CatchClause>();
	HashSet<CatchClause> overCatches = new HashSet<CatchClause>();
	public HashMap<CatchClause,String> overCatchesDetails = new HashMap<CatchClause, String>();
	public int countOfCatchBlock = 0;
	public int lOC= 0;

	public CatchClauseVisitor(String name) {
		this.visitFile = name;
	}

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
	
	public void updateResult(int overcatch, int multipleline, int destructive, int donothing, 
			int returnnull, int catchgeneric, int ignore, 
			int getClause, int overcatchAbort, int logReturnnull, int logThrow, 
			int throwFinally, int nestedTry, int subSumption, int specific, int unrecover, int recover, int dummy) {
		
		
		
		// 13 anti-patterns
		Result res = DetectException.resultMap.get(this.visitFile);
		res.addDoNothingCount(donothing);
		res.addDestructiveWrappingCount(destructive);
		res.addMultipleLineLogCount(multipleline);
		res.addOvercatchCount(overcatch);
		res.addReturnNullCount(returnnull);
		res.addCatchGenericCount(catchgeneric);
		res.addIgnoreInterruptedExceptionCountCount(ignore);
		res.addGetClauseCountCount(getClause);
		res.addOvercatchAbortCount(overcatchAbort);
		res.addLogReturnNullCount(logReturnnull);
		res.addLogThrowCount(logThrow);
		res.addThrowInFinally(throwFinally);
		res.addNestedTry(nestedTry);
		res.addDummyCount(dummy);
		//another metrics
		res.addSubSumption(subSumption);
		res.addSpecific(specific);
		res.addUnRecoverExCount(unrecover);
		res.addRecoverExCount(recover);
//		ExceptionFinder.resultMap.put(res.FileName, res);
		
	}
	
	public boolean checkNested(ASTNode node) {
		int depth = 0;
		while(node !=null) {
			if(node.getParent() instanceof TryStatement) {
				
				depth++;
				if(depth>1) {
					break;
				}
	
				
			}
			node = node.getParent();
			
		}
		if(depth>1)
			return true;

		
		return false;
	}
	
	public boolean CheckOverCatch(Set<String> exInCatch, Set<String> exInTry) {
			if(exInCatch.size()==0) {
				return false;
			}
		
			for(String ex : exInCatch) {
				for(String exTry :exInTry) {
					if(ex.equals(exTry)||HighCatchLow(ex,exTry)){
						// if catch same exception or the lower exception.
						// not over catch
						return false;
					}			
				}
			}
		
		return true;
	}
	public boolean CheckSubSumption(Set<String> exInCatch, Set<String> exInTry) {
		
		for(String ex : exInCatch) {
			for(String exTry :exInTry) {
				if(HighCatchLow(ex,exTry)){
					// super exception catch sub exception
					return true;
				}			
			}
		}
	
	return false;
	}
	public boolean CheckSpecific(Set<String> exInCatch, Set<String> exInTry) {
		
		for(String ex : exInCatch) {
			for(String exTry :exInTry) {
				if(exTry.equals(ex)){
					return true;
				}			
			}
		}
	
	return false;
	}
	
	

	

	@Override
	public boolean visit(CatchClause node) {
		

		
		countOfCatchBlock++;
		int loc = node.getBody().toString().split("\n").length;
		this.lOC = this.lOC+loc;
		int overcatch_count = 0;
		int multipleline_count = 0;
		int destructive_count = 0;
		int donothing_count = 0;
		int returnnull_count = 0;
		int catchgeneric_count = 0;
		//condition
		boolean catch_InterruptedException = false;
		//result
		int ignoreInterruptedException_count = 0;
		int getClause_count = 0;
		int overcatch_abort_count = 0;
		int log_returnnull_count = 0;
		//condition
		boolean throwsomething=false;
		//result
		int log_throw_count =0;
		int throw_in_finally_count = 0;
		int nestedtry_count = 0;
		int dummy_count = 0;
		
		//another metrics
		int subsumption_count = 0;
		int specific_count = 0;
		int recover_exception_count = 0;
		int unrecover_exception_count = 0;
		
		if(checkNested(node))
			nestedtry_count++;
		// throw in finally?
		TryStatement tryStatement = (TryStatement)node.getParent();
		Block finallyBlock = tryStatement.getFinally();
		if(finallyBlock!=null) {
			ThrowInFinallyVisitor tifv = new ThrowInFinallyVisitor();
			finallyBlock.accept(tifv);
			if(tifv.throwsomething)
				throw_in_finally_count++;
			
		}

		//get exception type
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
		
		
		
		for(String ex:wholeExceptionsInCatch) {
			if(ExceptionFinder.runtimeExceptionExtends.containsKey("ex")) {
				unrecover_exception_count++;
			}
			else{
				recover_exception_count++;
			}
			
			if(ex.equals("Exception")) {
				catchgeneric_count++;
			}
			else if(ex.equals("InterruptedException")) {
				catch_InterruptedException = true;
			}
		}
		
		// get try block
				Block tryBlock = tryStatement.getBody();
				MethodInvocationInTryVisitor mVisitor = new MethodInvocationInTryVisitor();

				tryBlock.accept(mVisitor);
				//result of exceptions in tryBlock is in mVisitor.ResultExceptionSet
				//All exception

				//to compare exceptions between try and block
			
				String exceptionsInTry = "";

				boolean overcatch = false;
				overcatch = CheckOverCatch(wholeExceptionsInCatch, mVisitor.ResultExceptionSet);
				if(overcatch)
					overcatch_count++;
				boolean subsumption = false;
				subsumption = CheckSubSumption(wholeExceptionsInCatch, mVisitor.ResultExceptionSet);
				if(subsumption)
					subsumption_count++;
				boolean specific = false;
				specific = CheckSpecific(wholeExceptionsInCatch, mVisitor.ResultExceptionSet);
				if(specific)
					specific_count++;


		//-------
		if(node.getBody().statements().size()==0) {

			donothing_count++;
			if(catch_InterruptedException)
				ignoreInterruptedException_count++;
			updateResult(overcatch_count,multipleline_count,destructive_count, donothing_count, returnnull_count,catchgeneric_count,
							ignoreInterruptedException_count,getClause_count,overcatch_abort_count,
							log_returnnull_count,log_throw_count,throw_in_finally_count,nestedtry_count, subsumption_count,specific_count,
							unrecover_exception_count,recover_exception_count,dummy_count);
			return super.visit(node);
		}
		
		
		int countOfLog = 0;
		// return null detection
		ReturnNullVisitor rnv = new ReturnNullVisitor();
		node.getBody().accept(rnv);
		if(rnv.containsReturnNull) {
			returnnull_count++;
		}
		// method invocation visitor in Catch Block
		MethodInvocationInCatchVIsitor micv = new MethodInvocationInCatchVIsitor();
		node.getBody().accept(micv);
		
		if(micv.dummy&&!rnv.containsReturn) {
			dummy_count++;
		}
			
		if(overcatch&&micv.abort) {
			overcatch_abort_count++;
		}
		// getClause detection
		if(micv.containsGetClause)
			getClause_count++;
		countOfLog = micv.countOfLog;

		
		
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


		Block block = node.getBody();

		List<ASTNode> bodies = block.statements();
		for(ASTNode nn: bodies) {

			if (nn instanceof ThrowStatement) {
				throwsomething= true;
				
				ThrowStatement throwSt = (ThrowStatement)nn;
				if(throwSt.getExpression() instanceof ClassInstanceCreation) {
					SingleVariableDeclaration sv = node.getException();
					String ename = sv.getName().toString();
					ClassInstanceCreation cic = (ClassInstanceCreation)throwSt.getExpression();
					List<Object> argList = cic.arguments();
					boolean des = true;
					if(argList.size()==0) {
						des = true;
					}
					else {
						for(Object o:argList) {
							String s = o.toString();
							s = s.trim();
							if(s.equals(ename)||s.equals(ename+".getStackTrace()")) {
								des = false;
								break;
							}	
						}
					}
					
					if(des) {
						destructiveWrappingCatches.add(node);
						destructive_count++;
					}
					
				}
				
				
			}
		}

		if (countOfLog >1) {
			multipleLineCatches.add(node);
			multipleline_count = multipleline_count+1;
		}
		if (countOfLog >=1&&returnnull_count>0) {
			log_returnnull_count++;
			
		}
		if (countOfLog >=1&&throwsomething) {
			log_throw_count++;
			
		}
		
		updateResult(overcatch_count,multipleline_count,destructive_count, donothing_count, returnnull_count,
					catchgeneric_count,ignoreInterruptedException_count, getClause_count,
					overcatch_abort_count,log_returnnull_count,log_throw_count,throw_in_finally_count,nestedtry_count,
					subsumption_count,specific_count, unrecover_exception_count,recover_exception_count,dummy_count);
		return super.visit(node);
	}

	// Throwable Exception IOException
	public boolean HighCatchLow(String exceptionCatch, String exceptionTry) {
		String higherException = SOEN691.patterns.ExceptionFinder.exceptionExtends.get(exceptionTry);

		while(true) {
			if(higherException == null)
				break;
			
			if(higherException.equals(exceptionCatch))
				return true;
			higherException = SOEN691.patterns.ExceptionFinder.exceptionExtends.get(higherException);
		}
		return false;
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
