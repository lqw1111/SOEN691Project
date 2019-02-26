package SOEN691.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class Node {
	MethodDeclaration Method;
	List<MethodDeclaration> Adj;
	public Node(MethodDeclaration method) {
		this.Method =method;
		Adj = new ArrayList<MethodDeclaration>();
	}
	public void AddAdjNode(MethodDeclaration method) {
		this.Adj.add(method);
	}
	public List<MethodDeclaration> GetAdj(){
		return this.Adj;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.Method.getName() +"[" + this.Adj.toString()+"]";
//		return super.toString();
	}
	
	
	
	

}
