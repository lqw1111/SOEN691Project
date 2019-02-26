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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MethodInvocationVisitor extends ASTVisitor{
	Set<MethodDeclaration> suspectDeclarations = new HashSet<MethodDeclaration>();
	HashSet<MethodInvocation> suspectInvocations = new HashSet<MethodInvocation>();
	
	
	public static HashSet<Node> MethodDeclarationSet = new HashSet<Node>();
	
	
	public static HashMap<MethodDeclaration,List<String>> exceptionMap = new HashMap<>();
	
	
	public MethodInvocationVisitor(Set<MethodDeclaration> suspectDeclarations) {
		this.suspectDeclarations = suspectDeclarations;
	}
	public  MethodInvocationVisitor() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		
		List<String> exceptionList = new ArrayList<String>();
		
		ITypeBinding type = node.resolveMethodBinding().getDeclaringClass();
		IMethodBinding imb = node.resolveMethodBinding().getMethodDeclaration();
		
		// find call method ->
		ASTNode astNode = node;
		MethodDeclaration callMD ;
		while(true) {
			
			if(astNode instanceof MethodDeclaration) {
				callMD = (MethodDeclaration)astNode;
				break;
				
			}
			else if(astNode instanceof TypeDeclaration) {
				return super.visit(node);//to check
			}
			astNode = astNode.getParent();

		}
		//   ->  called method.
		MethodDeclaration calledMD = FindMethodDeclaration(imb);
		if (calledMD == null) {
			try {
				exceptionList.addAll(FindNonRuntimeExceptions(imb));
				exceptionList.addAll(FindRuntimeExceptions(imb));
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				
			}

		}
		else {
			//Add edge from call ->called
			boolean newNode = true;
			for(Node node1 : MethodDeclarationSet) {
				if(node1.Method.equals(callMD)) {
					node1.AddAdjNode(calledMD);
					newNode = false;
					break;
				}
			}
			if(newNode) {
				Node node1 = new Node(callMD);
				Node node2 = new Node(calledMD);
				node1.AddAdjNode(calledMD);
				MethodDeclarationSet.add(node1);
				MethodDeclarationSet.add(node2);
			}

		
		}
		if(exceptionMap.containsKey(callMD)) {
			List<String> list = new ArrayList<String>();
			list = exceptionMap.get(callMD);
			list.addAll(exceptionList);
			exceptionMap.put(callMD, list);
			
		}
		else {
			exceptionMap.put(callMD, exceptionList);
		}


		return super.visit(node);
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
