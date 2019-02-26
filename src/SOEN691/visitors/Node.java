package SOEN691.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThisExpression;

public class Node {
//	public MethodDeclaration Method;

	
	public String Method;
	public String Class;
	public String Package;
	public Node(String method, String class1, String package1) {
		super();
		Method = method;
		Class = class1;
		Package = package1;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Method == null) ? 0 : Method.hashCode());
		result = prime * result + ((Class == null) ? 0 : Class.hashCode());
		result = prime * result + ((Package == null) ? 0 : Package.hashCode());
		return result;

	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node)obj;
		if(other.Class.equals(this.Class)&&other.Package.equals(this.Package)&&other.Method.equals(this.Method)) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	

	


	
	

}
