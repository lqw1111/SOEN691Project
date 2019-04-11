package SOEN691.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class TryBlockVisitor extends ASTVisitor{
	public int tryQuantity = 0;
	public String visitFile = "";
	public int lOC = 0;
	public int try_in_Declaration = 0;
	public int try_in_Condition = 0;
	public int try_in_Loop = 0;
	public int try_int_EH = 0;
	public int try_in_Other = 0;
	public int invoked_methods = 0;
	//12 actions
	
	public int actions_abort = 0;
	public int actions_continue = 0;
	public int actions_default = 0;
	public int actions_empty = 0;
	public int actions_log = 0;
	public int actions_method = 0;
	public int actions_nestedtry = 0;
	public int actions_return = 0;
	public int actions_throwcurrent = 0;
	public int actions_thrownew = 0;
	public int actions_throwwrap = 0;
	public int actions_todo = 0;
	

	
	public TryBlockVisitor(String visitFile) {
		super();
		this.visitFile = visitFile;
	}
	
	
	
	public void checkScope(ASTNode scope) {
		if(scope instanceof MethodDeclaration) {
			this.try_in_Declaration++;
		}
		else if( scope instanceof IfStatement||scope instanceof SwitchStatement) {
			this.try_in_Condition++;
		}
		else if( scope instanceof ForStatement||scope instanceof WhileStatement) {
			this.try_in_Loop++;
		}
		else if(scope instanceof CatchClause) {
			this.try_int_EH++;
		}else {
			this.try_in_Other++;
		}
		
	}

	@Override
	public boolean visit(TryStatement node) {
		
		this.tryQuantity++;
		InvokedMethodsVisitor invoked = new InvokedMethodsVisitor();
		node.getBody().accept(invoked);
		ActionsVisitor actionVi = new ActionsVisitor();
		List<CatchClause> list = node.catchClauses();
		for(CatchClause cc : list) {
			
			cc.accept(actionVi);
	
		}
		if(actionVi.actions_abort)
			this.actions_abort+=1;
		if(actionVi.actions_continue)
			this.actions_continue+=1;
		if(actionVi.actions_default)
			this.actions_default+=1;
		if(actionVi.actions_empty)
			this.actions_empty+=1;
		if(actionVi.actions_log)
			this.actions_log+=1;
		if(actionVi.actions_method)
			this.actions_method+=1;
		if(actionVi.actions_nestedtry)
			this.actions_nestedtry+=1;
		if(actionVi.actions_return)
			this.actions_return+=1;
		if(actionVi.actions_throwcurrent)
			this.actions_throwcurrent+=1;
		if(actionVi.actions_thrownew)
			this.actions_thrownew+=1;
		if(actionVi.actions_throwwrap)
			this.actions_throwwrap+=1;

		
		
		this.invoked_methods += invoked.invoked_method_count;
		ASTNode scope = node.getParent().getParent();
		checkScope(scope);

		Block block = node.getBody();
		String ss = block.toString();
		int loc = ss.split("\n").length;
		this.lOC = this.lOC+loc;

		int start = block.getStartPosition();		
		int end = block.getLength();
		
		
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	

}
