package SOEN691.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaModelException;
import SOEN691.patterns.ExceptionFinder;
import SOEN691.visitors.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public class DetectException extends AbstractHandler {

	public static Map<String, Result> resultMap = new HashMap<String, Result>();
	int CountOfMethodInvocation = 0;
	int CountOfCatchBlock = 0;
	public int CountOfOverCatch = 0;
	public int CountOfWrap = 0;
	public int CountOfMultipleLine = 0;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject[] projects = root.getProjects();

		detectInProjects(projects);


		SampleHandler.printMessage("MethodInvocation detected: "+CountOfMethodInvocation);
		SampleHandler.printMessage("CatchClause detected: "+CountOfCatchBlock);
		SampleHandler.printMessage("Multipleline Log detected: "+CountOfMultipleLine);
		SampleHandler.printMessage("Destructive Wrapping detected: "+CountOfWrap);
		SampleHandler.printMessage("Over-catch detected: "+CountOfOverCatch);

		SampleHandler.printMessage("DONE DETECTING");
		StringBuilder sb = new StringBuilder();
		System.out.println(DetectException.resultMap.values().size());
		writeCSV(DetectException.resultMap.values());
		System.out.println("Finish CSV.");

		for(Result res:DetectException.resultMap.values()) {
			sb.append(res.toString());
			
		}
		


		System.out.println(ExceptionFinder.CountOfFiles);
		System.out.println(SOEN691.patterns.ExceptionFinder.exceptionExtends.size());

		return null;
	}
	public  void writeCSV(Collection<Result> list) {
	    try {
	    	
	    
	        File csv = new File("/Users/sandra/Desktop/se691/assignment/SOEN691Project/anti-pattern.csv");//CSV文件
	        BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
	        
	        bw.newLine();
	        String title = "file_name,overcatch,overcatch_abort,catch_donothing,catch_returnnull,catchgeneric,destructivewrapping,"
	        		+ "dummy_handler,ignoring_interrupted_exception,incomplete_implementation,log_returnnull,log_throw,multiple_line_log,"
	        		+ "nestedtry,rely_getclause,throw_in_finally,subsumption,specific,unrecover_exception,recover_exception,tryquantity,catchquantity,"
	        		+ "log_try,loc_catch,sloc_try,sloc_catch,try_in_declaration,try_in_condition,try_in_loop,try_in_eh,try_in_other,invoked_methods,actions_abort,"
	        		+ "actions_continue,actions_default,actions_empty,actions_log,actions_method,actions_nestedtry,actions_return,actions_throwcurrent,actions_thrownew,"
	        		+ "actions_throwwrapping,actions_todo,actions_abort_percentage,actions_continue_percentage,actions_default_percentage,"
	        		+ "actions_empty_percentage,actions_log_percentage,actions_method_percentage,actions_nestedtry_percentage,actions_return_percentage,actions_throwcurrent_percentage,"
	        		+ "actions_thrownew_percentage,actions_throwwrap_percentage,actions_todo_percentage,subsumption_percentage,specific_percentage,"
	        		+ "overcatch_percentage,overcatch_abort_percentage,"
	        		+ "catch_donothing_percentage,catch_returnnull_percentage,catchgeneric_percentage,destructivewrapping_percentage,dummy_handler_percentage,ignoring_interrupted_exception_percentage,"
	        		+ "incomplete_implementation_percentage,log_returnnull_percentage,log_throw_percentage,multiple_line_log_percentage,nestedtry_percentage,rely_getclause_percentage,throw_in_finally_percentage";
	     
//	        String title = "file_name,unhandledexceptions,unreachableexceptions,overcatch,overcatch_abort,catch_donothing,catch_returnnull,catchgeneric,destructivewrapping,"
//	        		+ "dummy_handler,ignoring_interrupted_exception,incomplete_implementation,log_returnnull,log_throw,multiple_line_log,"
//	        		+ "nestedtry,rely_getclause,throw_in_finally,subsumption,specific,unrecover_exception,recover_exception,tryquantity,catchquantity,"
//	        		+ "log_try,loc_catch,sloc_try,sloc_catch,try_in_declaration,try_in_condition,try_in_loop,try_in_eh,try_in_other,invoked_methods,actions_abort,"
//	        		+ "actions_continue,actions_default,actions_empty,actions_log,actions_method,actions_nestedtry,actions_return,actions_throwcurrent,actions_thrownew,"
//	        		+ "actions_throwwrapping,actions_todo,actions_abort_percentage,actions_continue_percentage,actions_default_percentage,"
//	        		+ "actions_empty_percentage,actions_log_percentage,actions_method_percentage,actions_nestedtry_percentage,actions_return_percentage,actions_throwcurrent_percentage,"
//	        		+ "actions_thrownew_percentage,actions_throwwrap_percentage,actions_todo_percentage,subsumption_percentage,specific_percentage,unhandledexceptions_percentage,unreachableexceptions_percentage,"
//	        		+ "overcatch_percentage,overcatch_abort_percentage,"
//	        		+ "catch_donothing_percentage,catch_returnnull_percentage,catchgeneric_percentage,destructivewrapping_percentage,dummy_handler_percentage,ignoring_interrupted_exception_percentage,"
//	        		+ "incomplete_implementation_percentage,log_returnnull_percentage,log_throw_percentage,multiple_line_log_percentage,nestedtry_percentage,rely_getclause_percentage,throw_in_finally_percentage";
	        bw.write(title);
	        //新增一行数据
	        for(Result result :list) {
	        	bw.newLine();
		        bw.write(result.toCSVline());
	        }
	        
	        bw.close();
	    }catch(Exception e) {
	    	
	    }
	    
	}


	private void detectInProjects(IProject[] projects) {
		for(IProject project : projects) {
			SampleHandler.printMessage("DETECTING IN: " + project.getName());



			ExceptionFinder exceptionFinder = new ExceptionFinder();

			try {
				// find the exceptions and their methods
				exceptionFinder.findExceptions(project);


			} catch (JavaModelException e) {
				e.printStackTrace();
			}

			CountOfMethodInvocation = CountOfMethodInvocation + exceptionFinder.CountOfMethodInvocation;
			CountOfCatchBlock = CountOfCatchBlock + exceptionFinder.CountOfCatchBlock;
			CountOfMultipleLine = CountOfMultipleLine + exceptionFinder.CountOfMultipleLine;
			CountOfOverCatch = CountOfOverCatch + exceptionFinder.CountOfOverCatch;
			CountOfWrap = CountOfWrap + exceptionFinder.CountOfWrap;
	}
	}
}
