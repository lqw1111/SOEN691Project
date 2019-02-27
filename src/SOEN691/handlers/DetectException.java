package SOEN691.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaModelException;
import SOEN691.patterns.ExceptionFinder;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public class DetectException extends AbstractHandler {

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

		return null;
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
