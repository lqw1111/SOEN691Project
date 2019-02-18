package SOEN691.visitors;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor{
	Set<MethodDeclaration> suspectDeclarations = new HashSet<MethodDeclaration>();
	HashSet<MethodInvocation> suspectInvocations = new HashSet<MethodInvocation>();
	
	public MethodInvocationVisitor(Set<MethodDeclaration> suspectDeclarations) {
		this.suspectDeclarations = suspectDeclarations;
	}
	public  MethodInvocationVisitor() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		ITypeBinding type = node.resolveMethodBinding().getDeclaringClass();
		IMethodBinding linkedDeclaration = node.resolveMethodBinding().getMethodDeclaration();
		
		for(MethodDeclaration suspectDeclaration: suspectDeclarations) {
			if(suspectDeclaration.resolveBinding().getMethodDeclaration().isEqualTo(linkedDeclaration)) {
				suspectInvocations.add(node);
			}
		}
		return super.visit(node);
	}
	
	public HashSet<MethodInvocation> getSuspectInvocations() {
		return suspectInvocations;
	}
	
}
