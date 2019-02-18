package SOEN691.visitors;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;


public class MethodDeclarationVisitor extends ASTVisitor{
	HashSet<MethodDeclaration> callSuspectMethods = new HashSet<>();
	HashSet<MethodDeclaration> suspectDeclarations = new HashSet<MethodDeclaration>();
	HashSet<TypeDeclaration> typeList = new HashSet<TypeDeclaration>();
	Set<String> classNameList = new HashSet<String>();

	public MethodDeclarationVisitor(Set<MethodDeclaration> suspectDeclarations) {
		this.suspectDeclarations.addAll(suspectDeclarations);
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor(suspectDeclarations);
		node.accept(methodInvocationVisitor);
		
		if(!methodInvocationVisitor.getSuspectInvocations().isEmpty()) {
			callSuspectMethods.add(node);
			suspectDeclarations.add(node);
		}
		
		return super.visit(node);
	}
	
	
	
	@Override
	public boolean visit(TypeDeclaration node) {
		typeList.add(node);
		classNameList.add(node.getName().toString());
		System.out.print(node.getName().toString());
		// TODO Auto-generated method stub
		
		return super.visit(node);
	}


	@Override
	public boolean visit(VariableDeclarationStatement node) {
		// TODO Auto-generated method stub
		
		return super.visit(node);
	}

	public HashSet<MethodDeclaration> getCallSuspectMethods() {
		return callSuspectMethods;
	}
	
}
