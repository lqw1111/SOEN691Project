package SOEN691.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MethodInvocationInTryVisitor extends ASTVisitor{
	Set<MethodDeclaration> suspectDeclarations = new HashSet<MethodDeclaration>();
	HashSet<MethodInvocation> suspectInvocations = new HashSet<MethodInvocation>();
	
	
	public static HashMap<Node,Set<Node>> CallGraph = new HashMap<Node,Set<Node>>();
	
	public static HashMap<Node,Set<String>> ExceptionMap = new HashMap<>();
	
	public Set<String> ResultExceptionSet;
	
	
	public static HashMap<Block,Set<MethodDeclaration>> tryMap = new HashMap<>();
	public MethodInvocationInTryVisitor(Set<MethodDeclaration> suspectDeclarations) {
		this.suspectDeclarations = suspectDeclarations;
	}

	
	public  MethodInvocationInTryVisitor() {
		// TODO Auto-generated constructor stub
		ResultExceptionSet = new HashSet<String>();
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		
		List<String> exceptionList = new ArrayList<String>();
		
		ITypeBinding itbCalled = node.resolveMethodBinding().getDeclaringClass();
		IMethodBinding imbCalled = node.resolveMethodBinding().getMethodDeclaration();
		IPackageBinding ipbCalled =	node.resolveMethodBinding().getMethodDeclaration().getDeclaringClass().getPackage();
		
		String classNameCalled = itbCalled.getName();
		String methodNameCalled = imbCalled.toString();
		String packageNameCalled = ipbCalled.getName();
		
	
		Node nodeCalled = new Node(methodNameCalled, classNameCalled, packageNameCalled);
		if(!SOEN691.patterns.ExceptionFinder.CallGraph.containsKey(nodeCalled)) {
			return super.visit(node);
		}
		Set<Node> calledNodeSet = new HashSet<Node>();
		
		calledNodeSet = SOEN691.patterns.ExceptionFinder.CallGraph.get(nodeCalled);
		if(calledNodeSet == null) {
			calledNodeSet = new HashSet<Node>();
			calledNodeSet.add(nodeCalled);
		}
		else {
			calledNodeSet.add(nodeCalled);
		}

		
		
		Set<String> exceptionSet = new HashSet<String>();
		exceptionSet = FindAllExceptions(calledNodeSet);
//		this.ResultExceptionSet = exceptionSet;
		this.ResultExceptionSet.addAll(exceptionSet);
		return super.visit(node);
	}
	
	public Set<String> FindAllExceptions(Set<Node> set) {
		Set<String> res = new HashSet<String>();
		
		for(Node node:set) {
			res.addAll(SOEN691.patterns.ExceptionFinder.ExceptionMap.get(node));
			
		}
		
		
		
		
		return res;
		
		
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
//		
//		if ( unit == null ) {
//			return null;
//		   // not available, external declaration
//		}
		ASTParser parser = ASTParser.newParser( AST.JLS8 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT );
		parser.setSource( unit );
		parser.setResolveBindings( true );
		CompilationUnit cu = (CompilationUnit) parser.createAST( null );
		MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode( binding.getKey() );
		
		return decl;
		
		
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
	public HashSet<MethodInvocation> getSuspectInvocations() {
		return suspectInvocations;
	}
	
}
