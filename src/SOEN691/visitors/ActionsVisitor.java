package SOEN691.visitors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.UnionType;

public class ActionsVisitor extends ASTVisitor {
	
	//12 actions
	
	public boolean actions_abort = false;
	public boolean actions_continue = false;
	public boolean actions_default = false;//?
	public boolean actions_empty = false;
	public boolean actions_log = false;
	public boolean actions_method = false;
	public boolean actions_nestedtry = false;
	public boolean actions_return = false;
	public boolean actions_throwcurrent = false;
	public boolean actions_thrownew = false;
	public boolean actions_throwwrap = false;
//	public boolean actions_todo = false; // in comment visitor
	

	
	@Override
	public boolean visit(TryStatement node) {
		this.actions_nestedtry = true;
		// TODO Auto-generated method stub
		return super.visit(node);
	}



	@Override
	public boolean visit(CatchClause node) {

		
		if(node.getBody().statements().size()==0) {
			this.actions_empty = true;
		}
		// TODO Auto-generated method stub
		return super.visit(node);
	}



	@Override
	public boolean visit(MethodInvocation node) {

		String methodFullName = node.toString();
		String methodName = node.getName().toString();
		
		
		if(methodFullName.contains("System.exit("))
			this.actions_abort= true;
		else if(CheckLogLevel(methodName)) {
			this.actions_log = true;
		}
		else {
			this.actions_method = true;
		}
		

		
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	
	
	@Override
	public boolean visit(ContinueStatement node) {
		// TODO Auto-generated method stub
		this.actions_continue = true;
		return super.visit(node);
	}



	@Override
	public boolean visit(ReturnStatement node) {
		this.actions_return = true;
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ThrowStatement node) {
		Expression ex = node.getExpression();
		if(ex instanceof ClassInstanceCreation ) {
			ClassInstanceCreation create = (ClassInstanceCreation)ex;
			if(create.arguments().size()==0) {
				this.actions_thrownew = true;
			}
			else {
				this.actions_throwwrap = true;
			}
		}
		else {
			this.actions_throwcurrent=true;
		}

		
		
		// TODO Auto-generated method stub
		return super.visit(node);
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
	
	
	

}
