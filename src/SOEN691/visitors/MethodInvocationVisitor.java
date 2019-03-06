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
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import SOEN691.patterns.ExceptionFinder;

public class MethodInvocationVisitor extends ASTVisitor {
	Set<MethodDeclaration> suspectDeclarations = new HashSet<MethodDeclaration>();
	HashSet<MethodInvocation> suspectInvocations = new HashSet<MethodInvocation>();

	public int countOfMethodInvocation = 0;

	public MethodInvocationVisitor(Set<MethodDeclaration> suspectDeclarations) {
		this.suspectDeclarations = suspectDeclarations;
	}

	public MethodInvocationVisitor() {
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	public boolean visit(ClassInstanceCreation node) {
		countOfMethodInvocation++;
		if( node == null) {
			return super.visit(node);
		}
		List<String> exceptionList = new ArrayList<String>();
		ITypeBinding itbCalled ;
		IMethodBinding imbCalled;
		IPackageBinding ipbCalled;
		
		try {
	
			itbCalled = node.resolveConstructorBinding().getDeclaringClass();
			 imbCalled = node.resolveConstructorBinding().getMethodDeclaration();
			 ipbCalled =	node.resolveConstructorBinding().getMethodDeclaration().getDeclaringClass().getPackage();
			
		}
		catch (Exception e) {
			return super.visit(node);
		}
		
		
		
		String classNameCalled = itbCalled.getName();
		String methodNameCalled = imbCalled.toString();
		String packageNameCalled = ipbCalled.getName();
		
		
		Node nodeCalled = new Node(methodNameCalled, classNameCalled, packageNameCalled);

		//itb, imb, ipb  called method
		// find call method ->
		ASTNode astNode = node;
		MethodDeclaration callMD = null ;
		Block tryBlock = null;
		while(true) {
			
			if(astNode instanceof MethodDeclaration) {
				callMD = (MethodDeclaration)astNode;
				break;
				
				
			}
			else if(astNode instanceof TypeDeclaration) {
				return super.visit(node);//to check
			}		
			try {
				astNode = astNode.getParent();
			}
			catch (Exception ex) {
				return super.visit(node);//to check
			}
			

		}
		
		IMethodBinding imbCall = callMD.resolveBinding();
		ITypeBinding itbCall = callMD.resolveBinding().getDeclaringClass();
		IPackageBinding ipbCall = callMD.resolveBinding().getDeclaringClass().getPackage();
		
		String classNameCall = itbCall.getName();
		String methodNameCall = imbCall.toString();
		String packageNameCall = ipbCall.getName();
		Node nodeCall = new Node(methodNameCall, classNameCall, packageNameCall);

		
		if(SOEN691.patterns.ExceptionFinder.CallGraph.containsKey(nodeCall)) {
			Set<Node> adjCall = new HashSet<Node>();
			adjCall = SOEN691.patterns.ExceptionFinder.CallGraph.get(nodeCall);
			adjCall.add(nodeCalled);
			SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCall, adjCall);
			
		}
		else {
			Set<Node> adjCall = new HashSet<Node>();
			adjCall.add(nodeCalled);
			SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCall, adjCall);
		}
		

		if(!SOEN691.patterns.ExceptionFinder.CallGraph.containsKey(nodeCalled)) {
			Set<Node> adjCall = new HashSet<Node>();
			SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCalled, adjCall);
			
		}

		Set<String> setExceptionCall = new HashSet<String>();
		Set<String> setExceptionCalled = new HashSet<String>();
		try {
			setExceptionCall.addAll(FindNonRuntimeExceptions(imbCall));
			setExceptionCall.addAll(FindRuntimeExceptions(imbCall));
			
			setExceptionCalled.addAll(FindNonRuntimeExceptions(imbCalled));
			setExceptionCalled.addAll(FindRuntimeExceptions(imbCalled));
		}
		catch(JavaModelException ex) {
			
		}

		if(SOEN691.patterns.ExceptionFinder.ExceptionMap.containsKey(nodeCalled)) {
			Set<String> tempSet = new HashSet<String>();
			tempSet = SOEN691.patterns.ExceptionFinder.ExceptionMap.get(nodeCalled);
			tempSet.addAll(setExceptionCalled);
			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCalled, tempSet);
		}
		else {

			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCalled, setExceptionCalled);
			
		}

		if(SOEN691.patterns.ExceptionFinder.ExceptionMap.containsKey(nodeCall)) {
			Set<String> tempSet = new HashSet<String>();
			tempSet = SOEN691.patterns.ExceptionFinder.ExceptionMap.get(nodeCall);
			tempSet.addAll(setExceptionCall);
			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCall, tempSet);
		}
		else {

			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCall, setExceptionCall);
			
		}
		

		return super.visit(node);
	}
	@Override
	public boolean visit(MethodInvocation node) {
		countOfMethodInvocation++;
		if (node == null) {
			return super.visit(node);
		}
		List<String> exceptionList = new ArrayList<String>();
		ITypeBinding itbCalled;
		IMethodBinding imbCalled;
		IPackageBinding ipbCalled;

		try {
			itbCalled = node.resolveMethodBinding().getDeclaringClass();
			imbCalled = node.resolveMethodBinding().getMethodDeclaration();
			ipbCalled = node.resolveMethodBinding().getMethodDeclaration().getDeclaringClass().getPackage();

		} catch (Exception e) {
			return super.visit(node);
		}

		String classNameCalled = itbCalled.getName();
		String methodNameCalled = imbCalled.toString();
		String packageNameCalled = ipbCalled.getName();

		Node nodeCalled = new Node(methodNameCalled, classNameCalled, packageNameCalled);

		if (!SOEN691.patterns.ExceptionFinder.CallGraph.containsKey(nodeCalled)) {
			Set<Node> adjCall = new HashSet<Node>();
			SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCalled, adjCall);

		}
		// itb, imb, ipb called method
		// find call method ->
		ASTNode astNode = node;
		MethodDeclaration callMD = null;
		Block tryBlock = null;
		while (true) {

			if (astNode instanceof MethodDeclaration) {
				callMD = (MethodDeclaration) astNode;
				break;

			} else if (astNode instanceof TypeDeclaration) {
				return super.visit(node);// to check
			}
			try {
				astNode = astNode.getParent();
			} catch (Exception ex) {
				return super.visit(node);// to check
			}

		}

		IMethodBinding imbCall = callMD.resolveBinding();
		ITypeBinding itbCall = callMD.resolveBinding().getDeclaringClass();
		IPackageBinding ipbCall = callMD.resolveBinding().getDeclaringClass().getPackage();

		String classNameCall = itbCall.getName();
		String methodNameCall = imbCall.toString();
		String packageNameCall = ipbCall.getName();
		Node nodeCall = new Node(methodNameCall, classNameCall, packageNameCall);

		if (SOEN691.patterns.ExceptionFinder.CallGraph.containsKey(nodeCall)) {
			if (!nodeCall.equals(nodeCalled)) {
				Set<Node> adjCall = new HashSet<Node>();

				adjCall = SOEN691.patterns.ExceptionFinder.CallGraph.get(nodeCall);
				adjCall.add(nodeCalled);
				SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCall, adjCall);
			}

		} else {
			if (!nodeCall.equals(nodeCalled)) {
				Set<Node> adjCall = new HashSet<Node>();
				adjCall.add(nodeCalled);
				SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCall, adjCall);
			}
		}

//		if (!SOEN691.patterns.ExceptionFinder.CallGraph.containsKey(nodeCalled)) {
//			if (!nodeCall.equals(nodeCalled)) {
//				Set<Node> adjCall = new HashSet<Node>();
//				SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCalled, adjCall);
//			}
//		}

		Set<String> setExceptionCall = new HashSet<String>();
		Set<String> setExceptionCalled = new HashSet<String>();
		try {
			setExceptionCall.addAll(FindNonRuntimeExceptions(imbCall));
			setExceptionCall.addAll(FindRuntimeExceptions(imbCall));

			setExceptionCalled.addAll(FindNonRuntimeExceptions(imbCalled));
			setExceptionCalled.addAll(FindRuntimeExceptions(imbCalled));
		} catch (JavaModelException ex) {

		}

		if (SOEN691.patterns.ExceptionFinder.ExceptionMap.containsKey(nodeCalled)) {
			Set<String> tempSet = new HashSet<String>();
			tempSet = SOEN691.patterns.ExceptionFinder.ExceptionMap.get(nodeCalled);
			tempSet.addAll(setExceptionCalled);
			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCalled, tempSet);
		} else {

			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCalled, setExceptionCalled);

		}

		if (SOEN691.patterns.ExceptionFinder.ExceptionMap.containsKey(nodeCall)) {
			Set<String> tempSet = new HashSet<String>();
			tempSet = SOEN691.patterns.ExceptionFinder.ExceptionMap.get(nodeCall);
			tempSet.addAll(setExceptionCall);
			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCall, tempSet);
		} else {

			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCall, setExceptionCall);

		}

		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		countOfMethodInvocation++;
		if( node == null) {
			return super.visit(node);
		}
		List<String> exceptionList = new ArrayList<String>();
		ITypeBinding itbCalled ;
		IMethodBinding imbCalled;
		IPackageBinding ipbCalled;
		
		try {
			
			itbCalled = node.resolveMethodBinding().getDeclaringClass();
			 imbCalled = node.resolveMethodBinding().getMethodDeclaration();
			 ipbCalled =	node.resolveMethodBinding().getMethodDeclaration().getDeclaringClass().getPackage();
			
		}
		catch (Exception e) {
			return super.visit(node);
		}
		
		
		
		String classNameCalled = itbCalled.getName();
		String methodNameCalled = imbCalled.toString();
		String packageNameCalled = ipbCalled.getName();
		
		
		Node nodeCalled = new Node(methodNameCalled, classNameCalled, packageNameCalled);

		//itb, imb, ipb  called method
		// find call method ->
		ASTNode astNode = node;
		MethodDeclaration callMD = null ;
		Block tryBlock = null;
		while(true) {
			
			if(astNode instanceof MethodDeclaration) {
				callMD = (MethodDeclaration)astNode;
				break;
				
				
			}
			else if(astNode instanceof TypeDeclaration) {
				return super.visit(node);//to check
			}		
			try {
				astNode = astNode.getParent();
			}
			catch (Exception ex) {
				return super.visit(node);//to check
			}
			

		}
		
		IMethodBinding imbCall = callMD.resolveBinding();
		ITypeBinding itbCall = callMD.resolveBinding().getDeclaringClass();
		IPackageBinding ipbCall = callMD.resolveBinding().getDeclaringClass().getPackage();
		
		String classNameCall = itbCall.getName();
		String methodNameCall = imbCall.toString();
		String packageNameCall = ipbCall.getName();
		Node nodeCall = new Node(methodNameCall, classNameCall, packageNameCall);

		
		if(SOEN691.patterns.ExceptionFinder.CallGraph.containsKey(nodeCall)) {
			Set<Node> adjCall = new HashSet<Node>();
			adjCall = SOEN691.patterns.ExceptionFinder.CallGraph.get(nodeCall);
			adjCall.add(nodeCalled);
			SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCall, adjCall);
			
		}
		else {
			Set<Node> adjCall = new HashSet<Node>();
			adjCall.add(nodeCalled);
			SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCall, adjCall);
		}
		

		if(!SOEN691.patterns.ExceptionFinder.CallGraph.containsKey(nodeCalled)) {
			Set<Node> adjCall = new HashSet<Node>();
			SOEN691.patterns.ExceptionFinder.CallGraph.put(nodeCalled, adjCall);
			
		}

		Set<String> setExceptionCall = new HashSet<String>();
		Set<String> setExceptionCalled = new HashSet<String>();
		try {
			setExceptionCall.addAll(FindNonRuntimeExceptions(imbCall));
			setExceptionCall.addAll(FindRuntimeExceptions(imbCall));
			
			setExceptionCalled.addAll(FindNonRuntimeExceptions(imbCalled));
			setExceptionCalled.addAll(FindRuntimeExceptions(imbCalled));
		}
		catch(JavaModelException ex) {
			
		}

		if(SOEN691.patterns.ExceptionFinder.ExceptionMap.containsKey(nodeCalled)) {
			Set<String> tempSet = new HashSet<String>();
			tempSet = SOEN691.patterns.ExceptionFinder.ExceptionMap.get(nodeCalled);
			tempSet.addAll(setExceptionCalled);
			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCalled, tempSet);
		}
		else {

			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCalled, setExceptionCalled);
			
		}

		if(SOEN691.patterns.ExceptionFinder.ExceptionMap.containsKey(nodeCall)) {
			Set<String> tempSet = new HashSet<String>();
			tempSet = SOEN691.patterns.ExceptionFinder.ExceptionMap.get(nodeCall);
			tempSet.addAll(setExceptionCall);
			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCall, tempSet);
		}
		else {

			SOEN691.patterns.ExceptionFinder.ExceptionMap.put(nodeCall, setExceptionCall);
			
		}
		

		return super.visit(node);
	}


	public MethodDeclaration FindMethodDeclaration(IMethodBinding binding) {
		if (binding == null) {
			return null;
		}
		IJavaElement ije = binding.getJavaElement();
		if (ije == null) {
			return null;
		}
		Object obj = ije.getAncestor(IJavaElement.COMPILATION_UNIT);

		ICompilationUnit unit;
		if (obj != null) {
			unit = (ICompilationUnit) obj;
		} else {
			return null;
		}
//		
//		if ( unit == null ) {
//			return null;
//		   // not available, external declaration
//		}
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		MethodDeclaration decl = (MethodDeclaration) cu.findDeclaringNode(binding.getKey());

		return decl;

	}

	public List<String> FindNonRuntimeExceptions(IMethodBinding imb) throws JavaModelException {
		List<String> exceptionList = new ArrayList<>();
		// Add all Non-Runtime exception in try block
		// Example: void print() throw XXXException
		for (ITypeBinding b : imb.getExceptionTypes()) {
			String str = b.getQualifiedName();
			if (str.contains(".")) {
				String ss;
				int flag = str.lastIndexOf(".") + 1;
				ss = str.substring(flag, str.length());
				exceptionList.add(ss);

			} else {
				exceptionList.add(str);
			}

		}

		return exceptionList;
	}

	private static String getJavadocFast(IMember member) throws JavaModelException {
		IBuffer buffer = member.getOpenable().getBuffer();

		ISourceRange javadocRange = member.getJavadocRange();
		String javadocText = buffer.getText(javadocRange.getOffset(), javadocRange.getLength());

		javadocText = javadocText.replaceAll("^/[*][*][ \t]*\n?", ""); // Filter starting /**
		javadocText = javadocText.replaceAll("\n?[ \t]*[*]/$", ""); // Filter ending */
		javadocText = javadocText.replaceAll("^\\s*[*]", "\n"); // Trim leading whitespace.
		javadocText = javadocText.replaceAll("\n\\s*[*]", "\n"); // Trim whitespace at beginning of line.
		javadocText = javadocText.replaceAll("<[^>]*>", ""); // Remove html tags.
		javadocText = javadocText.replaceAll("[{]@code([^}]*)[}]", "$1"); // Replace {@code foo} blocks with foo.
		javadocText = javadocText.replaceAll("&nbsp;", " ").replaceAll("&lt;", "<").replaceAll("&gt;", ">")
				.replaceAll("&quot;", "\""); // Replace html formatting.

//		javadocText = Flags.toString(member.getFlags()) + " " + JavaElement.getElementLabel(member, JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.F_PRE_TYPE_SIGNATURE) + "\n" + javadocText;
		return javadocText;
	}

	public List<String> FindRuntimeExceptions(IMethodBinding imb) throws JavaModelException {
		List<String> exceptionList = new ArrayList<>();
		IMethod imethod = (IMethod) imb.getJavaElement();
		if (imethod == null) {
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

	public List<String> FindExceptionsInJavadoc(String javadocText) {
		List<String> exceptionList = new ArrayList<>();
		String[] array = javadocText.split("\n");
		for (int i = 0; i < array.length; i++) {
			if (array[i].contains("@throws") || array[i].contains("@exception")) {
				String[] temp = array[i].split(" ");
				for (String ss : temp) {
					if (ss.contains("Exception")) {
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
