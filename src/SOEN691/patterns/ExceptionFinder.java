package SOEN691.patterns;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import SOEN691.handlers.DetectException;
import SOEN691.handlers.SampleHandler;
import SOEN691.visitors.CatchClauseVisitor;
import SOEN691.visitors.CommentVisitor;
import SOEN691.visitors.MethodInvocationVisitor;
import SOEN691.visitors.Node;
import SOEN691.visitors.Result;
import SOEN691.visitors.TryBlockVisitor;

public class ExceptionFinder {
	HashMap<MethodDeclaration, String> suspectMethods = new HashMap<>();
	public int CountOfMethodInvocation = 0;
	public int CountOfCatchBlock = 0;
	public int CountOfOverCatch = 0;
	public int CountOfWrap = 0;
	public int CountOfMultipleLine = 0;
	public static int CountOfFiles = 0;


	HashSet<MethodDeclaration> multiLineLogCatchMethod = new HashSet<>();
	HashSet<MethodDeclaration> destructiveWrappingMethod = new HashSet<>();
	HashSet<MethodDeclaration> overCatchMethod = new HashSet<>();

	public static HashMap<String, String> runtimeExceptionExtends = new HashMap<String, String>();

	public static HashMap<String, String> exceptionExtends = new HashMap<String, String>();

	public static HashMap<Node, Set<Node>> CallGraph = new HashMap<Node, Set<Node>>();

	public static HashMap<Node, Set<String>> ExceptionMap = new HashMap<>();

	HashMap<String, HashMap<String, Integer>> antiPatternsContainer = new HashMap<String, HashMap<String, Integer>>();

	public HashMap<MethodDeclaration, String> getSuspectMethods() {
		return suspectMethods;
	}

	private void initExceptionExtends() {
		exceptionExtends.put("Exception", "Throwable");
		exceptionExtends.put("RuntimeException", "Exception");
		exceptionExtends.put("AnnotationTypeMismatchException", "RuntimeException");
		exceptionExtends.put("ArithmeticException", "RuntimeException");
		exceptionExtends.put("ArrayStoreException", "RuntimeException");
		exceptionExtends.put("BufferOverflowException", "RuntimeException");
		exceptionExtends.put("BufferUnderflowException", "RuntimeException");
		exceptionExtends.put("CannotRedoException", "RuntimeException");
		exceptionExtends.put("CannotUndoException", "RuntimeException");
		exceptionExtends.put("ClassCastException", "RuntimeException");
		exceptionExtends.put("ClassCastException", "RuntimeException");
		exceptionExtends.put("CMMException", "RuntimeException");
		exceptionExtends.put("CompletionException", "RuntimeException");
		exceptionExtends.put("ConcurrentModificationException", "RuntimeException");
		exceptionExtends.put("DirectoryIteratorException", "ConcurrentModificationException");
		exceptionExtends.put("DataBindingException", "RuntimeException");
		exceptionExtends.put("DateTimeException", "RuntimeException");
		exceptionExtends.put("DateTimeParseException", "DateTimeException");
		exceptionExtends.put("UnsupportedTemporalTypeException", "DateTimeException");
		exceptionExtends.put("ZoneRulesException", "DateTimeException");
		exceptionExtends.put("DOMException", "RuntimeException");
		exceptionExtends.put("EmptyStackException", "RuntimeException");
		exceptionExtends.put("EnumConstantNotPresentException", "RuntimeException");
		exceptionExtends.put("EventException", "RuntimeException");
		exceptionExtends.put("FileSystemAlreadyExistsException", "RuntimeException");
		exceptionExtends.put("FileSystemNotFoundException", "RuntimeException");
		exceptionExtends.put("IllegalArgumentException", "RuntimeException");
		exceptionExtends.put("IllegalChannelGroupException", "IllegalArgumentException");
		exceptionExtends.put("IllegalCharsetNameException", "IllegalArgumentException");
		exceptionExtends.put("IllegalFormatException", "IllegalArgumentException");

		exceptionExtends.put("DuplicateFormatFlagsException", "IllegalFormatException");
		exceptionExtends.put("FormatFlagsConversionMismatchException", "IllegalFormatException");
		exceptionExtends.put("IllegalFormatCodePointException", "IllegalFormatException");
		exceptionExtends.put("IllegalFormatConversionException", "IllegalFormatException");
		exceptionExtends.put("IllegalFormatFlagsException", "IllegalFormatException");
		exceptionExtends.put("IllegalFormatPrecisionException", "IllegalFormatException");
		exceptionExtends.put("IllegalFormatWidthException", "IllegalFormatException");
		exceptionExtends.put("MissingFormatArgumentException", "IllegalFormatException");
		exceptionExtends.put("MissingFormatWidthException", "IllegalFormatException");
		exceptionExtends.put("UnknownFormatConversionException", "IllegalFormatException");
		exceptionExtends.put("UnknownFormatFlagsException", "IllegalFormatException");

		exceptionExtends.put("IllegalSelectorException", "IllegalArgumentException");
		exceptionExtends.put("IllegalThreadStateException", "IllegalArgumentException");
		exceptionExtends.put("InvalidKeyException", "IllegalArgumentException");
		exceptionExtends.put("InvalidOpenTypeException", "IllegalArgumentException");
		exceptionExtends.put("InvalidParameterException", "IllegalArgumentException");
		exceptionExtends.put("InvalidPathException", "IllegalArgumentException");
		exceptionExtends.put("KeyAlreadyExistsException", "IllegalArgumentException");
		exceptionExtends.put("NumberFormatException", "IllegalArgumentException");
		exceptionExtends.put("PatternSyntaxException", "IllegalArgumentException");
		exceptionExtends.put("ProviderMismatchException", "IllegalArgumentException");
		exceptionExtends.put("UnresolvedAddressException", "IllegalArgumentException");
		exceptionExtends.put("UnsupportedAddressTypeException", "IllegalArgumentException");
		exceptionExtends.put("UnsupportedCharsetException", "IllegalArgumentException");

		exceptionExtends.put("IllegalMonitorStateException", "RuntimeException");
		exceptionExtends.put("IllegalPathStateException", "RuntimeException");
		//
		exceptionExtends.put("AcceptPendingException", "IllegalStateException");
		exceptionExtends.put("AlreadyBoundException", "IllegalStateException");
		exceptionExtends.put("AlreadyConnectedException", "IllegalStateException");
		exceptionExtends.put("CancellationException", "IllegalStateException");
		exceptionExtends.put("CancelledKeyException", "IllegalStateException");
		exceptionExtends.put("ClosedDirectoryStreamException", "IllegalStateException");
		exceptionExtends.put("ClosedFileSystemException", "IllegalStateException");
		exceptionExtends.put("ClosedSelectorException", "IllegalStateException");
		exceptionExtends.put("ClosedWatchServiceException", "IllegalStateException");
		exceptionExtends.put("ConnectionPendingException", "IllegalStateException");

		exceptionExtends.put("FormatterClosedException", "IllegalStateException");
		exceptionExtends.put("IllegalBlockingModeException", "IllegalStateException");
		exceptionExtends.put("IllegalComponentStateException", "IllegalStateException");
		exceptionExtends.put("InvalidDnDOperationException", "IllegalStateException");
		exceptionExtends.put("InvalidMarkException", "IllegalStateException");
		exceptionExtends.put("NoConnectionPendingException", "IllegalStateException");
		exceptionExtends.put("NonReadableChannelException", "IllegalStateException");
		exceptionExtends.put("NonWritableChannelException", "IllegalStateException");
		exceptionExtends.put("NotYetBoundException", "IllegalStateException");
		exceptionExtends.put("NotYetConnectedException", "IllegalStateException");
		exceptionExtends.put("OverlappingFileLockException", "IllegalStateException");
		exceptionExtends.put("ReadPendingException", "IllegalStateException");
		exceptionExtends.put("ShutdownChannelGroupException", "IllegalStateException");
		exceptionExtends.put("WritePendingException", "IllegalStateException");
		exceptionExtends.put("IllegalStateException", "RuntimeException");
		exceptionExtends.put("IllformedLocaleException", "RuntimeException");
		exceptionExtends.put("ImagingOpException", "RuntimeException");
		exceptionExtends.put("IncompleteAnnotationException", "RuntimeException");
		exceptionExtends.put("IndexOutOfBoundsException", "RuntimeException");
		exceptionExtends.put("ArrayIndexOutOfBoundsException", "IndexOutOfBoundsException");
		exceptionExtends.put("StringIndexOutOfBoundsException", "IndexOutOfBoundsException");
		exceptionExtends.put("JMRuntimeException", "RuntimeException");
		exceptionExtends.put("MonitorSettingException", "JMRuntimeException");
		exceptionExtends.put("RuntimeErrorException", "JMRuntimeException");
		exceptionExtends.put("RuntimeMBeanException", "JMRuntimeException");
		exceptionExtends.put("RuntimeOperationsException", "JMRuntimeException");

		exceptionExtends.put("LSException", "RuntimeException");
		exceptionExtends.put("MalformedParameterizedTypeException", "RuntimeException");
		exceptionExtends.put("MalformedParametersException", "RuntimeException");
		exceptionExtends.put("MirroredTypesException", "RuntimeException");
		exceptionExtends.put("MissingResourceException", "RuntimeException");
		exceptionExtends.put("NegativeArraySizeException", "RuntimeException");
		exceptionExtends.put("NoSuchElementException", "RuntimeException");
		exceptionExtends.put("InputMismatchException", "NoSuchElementException");
		exceptionExtends.put("NullPointerException", "RuntimeException");
		exceptionExtends.put("ProfileDataException", "RuntimeException");
		exceptionExtends.put("ProviderException", "RuntimeException");
		exceptionExtends.put("ProviderNotFoundException", "RuntimeException");
		exceptionExtends.put("RasterFormatException", "RuntimeException");
		exceptionExtends.put("RejectedExecutionException", "RuntimeException");
		exceptionExtends.put("SecurityException", "RuntimeException");
		exceptionExtends.put("AccessControlException", "SecurityException");
		exceptionExtends.put("RMISecurityException", "SecurityException");
		exceptionExtends.put("SystemException", "RuntimeException");
		//
		exceptionExtends.put("TypeConstraintException", "RuntimeException");
		exceptionExtends.put("TypeNotPresentException", "RuntimeException");
		exceptionExtends.put("UncheckedIOException", "RuntimeException");
		exceptionExtends.put("UndeclaredThrowableException", "RuntimeException");
		exceptionExtends.put("UnknownEntityException", "RuntimeException");
		exceptionExtends.put("UnknownAnnotationValueException", "UnknownEntityException");
		exceptionExtends.put("UnknownElementException", "UnknownEntityException");
		exceptionExtends.put("UnknownTypeException", "UnknownEntityException");
		exceptionExtends.put("UnmodifiableSetException", "RuntimeException");
		exceptionExtends.put("UnsupportedOperationException", "RuntimeException");
		exceptionExtends.put("HeadlessException", "UnsupportedOperationException");
		exceptionExtends.put("ReadOnlyBufferException", "UnsupportedOperationException");
		exceptionExtends.put("ReadOnlyFileSystemException", "UnsupportedOperationException");
		exceptionExtends.put("WebServiceException", "RuntimeException");
		exceptionExtends.put("ProtocolException", "WebServiceException");
		exceptionExtends.put("HTTPException", "ProtocolException");
		exceptionExtends.put("SOAPFaultException", "ProtocolException");
		exceptionExtends.put("WrongMethodTypeException", "RuntimeException");

	}

	private void initRunTimeExceptionExtends() {
		runtimeExceptionExtends.put("Exception", "Throwable");
		runtimeExceptionExtends.put("RuntimeException", "Exception");
		runtimeExceptionExtends.put("AnnotationTypeMismatchException", "RuntimeException");
		runtimeExceptionExtends.put("ArithmeticException", "RuntimeException");
		runtimeExceptionExtends.put("ArrayStoreException", "RuntimeException");
		runtimeExceptionExtends.put("BufferOverflowException", "RuntimeException");
		runtimeExceptionExtends.put("BufferUnderflowException", "RuntimeException");
		runtimeExceptionExtends.put("CannotRedoException", "RuntimeException");
		runtimeExceptionExtends.put("CannotUndoException", "RuntimeException");
		runtimeExceptionExtends.put("ClassCastException", "RuntimeException");
		runtimeExceptionExtends.put("ClassCastException", "RuntimeException");
		runtimeExceptionExtends.put("CMMException", "RuntimeException");
		runtimeExceptionExtends.put("CompletionException", "RuntimeException");
		runtimeExceptionExtends.put("ConcurrentModificationException", "RuntimeException");
		runtimeExceptionExtends.put("DirectoryIteratorException", "ConcurrentModificationException");
		runtimeExceptionExtends.put("DataBindingException", "RuntimeException");
		runtimeExceptionExtends.put("DateTimeException", "RuntimeException");
		runtimeExceptionExtends.put("DateTimeParseException", "DateTimeException");
		runtimeExceptionExtends.put("UnsupportedTemporalTypeException", "DateTimeException");
		runtimeExceptionExtends.put("ZoneRulesException", "DateTimeException");
		runtimeExceptionExtends.put("DOMException", "RuntimeException");
		runtimeExceptionExtends.put("EmptyStackException", "RuntimeException");
		runtimeExceptionExtends.put("EnumConstantNotPresentException", "RuntimeException");
		runtimeExceptionExtends.put("EventException", "RuntimeException");
		runtimeExceptionExtends.put("FileSystemAlreadyExistsException", "RuntimeException");
		runtimeExceptionExtends.put("FileSystemNotFoundException", "RuntimeException");
		runtimeExceptionExtends.put("IllegalArgumentException", "RuntimeException");
		runtimeExceptionExtends.put("IllegalChannelGroupException", "IllegalArgumentException");
		runtimeExceptionExtends.put("IllegalCharsetNameException", "IllegalArgumentException");
		runtimeExceptionExtends.put("IllegalFormatException", "IllegalArgumentException");

		runtimeExceptionExtends.put("DuplicateFormatFlagsException", "IllegalFormatException");
		runtimeExceptionExtends.put("FormatFlagsConversionMismatchException", "IllegalFormatException");
		runtimeExceptionExtends.put("IllegalFormatCodePointException", "IllegalFormatException");
		runtimeExceptionExtends.put("IllegalFormatConversionException", "IllegalFormatException");
		runtimeExceptionExtends.put("IllegalFormatFlagsException", "IllegalFormatException");
		runtimeExceptionExtends.put("IllegalFormatPrecisionException", "IllegalFormatException");
		runtimeExceptionExtends.put("IllegalFormatWidthException", "IllegalFormatException");
		runtimeExceptionExtends.put("MissingFormatArgumentException", "IllegalFormatException");
		runtimeExceptionExtends.put("MissingFormatWidthException", "IllegalFormatException");
		runtimeExceptionExtends.put("UnknownFormatConversionException", "IllegalFormatException");
		runtimeExceptionExtends.put("UnknownFormatFlagsException", "IllegalFormatException");

		runtimeExceptionExtends.put("IllegalSelectorException", "IllegalArgumentException");
		runtimeExceptionExtends.put("IllegalThreadStateException", "IllegalArgumentException");
		runtimeExceptionExtends.put("InvalidKeyException", "IllegalArgumentException");
		runtimeExceptionExtends.put("InvalidOpenTypeException", "IllegalArgumentException");
		runtimeExceptionExtends.put("InvalidParameterException", "IllegalArgumentException");
		runtimeExceptionExtends.put("InvalidPathException", "IllegalArgumentException");
		runtimeExceptionExtends.put("KeyAlreadyExistsException", "IllegalArgumentException");
		runtimeExceptionExtends.put("NumberFormatException", "IllegalArgumentException");
		runtimeExceptionExtends.put("PatternSyntaxException", "IllegalArgumentException");
		runtimeExceptionExtends.put("ProviderMismatchException", "IllegalArgumentException");
		runtimeExceptionExtends.put("UnresolvedAddressException", "IllegalArgumentException");
		runtimeExceptionExtends.put("UnsupportedAddressTypeException", "IllegalArgumentException");
		runtimeExceptionExtends.put("UnsupportedCharsetException", "IllegalArgumentException");

		runtimeExceptionExtends.put("IllegalMonitorStateException", "RuntimeException");
		runtimeExceptionExtends.put("IllegalPathStateException", "RuntimeException");
		//
		runtimeExceptionExtends.put("AcceptPendingException", "IllegalStateException");
		runtimeExceptionExtends.put("AlreadyBoundException", "IllegalStateException");
		runtimeExceptionExtends.put("AlreadyConnectedException", "IllegalStateException");
		runtimeExceptionExtends.put("CancellationException", "IllegalStateException");
		runtimeExceptionExtends.put("CancelledKeyException", "IllegalStateException");
		runtimeExceptionExtends.put("ClosedDirectoryStreamException", "IllegalStateException");
		runtimeExceptionExtends.put("ClosedFileSystemException", "IllegalStateException");
		runtimeExceptionExtends.put("ClosedSelectorException", "IllegalStateException");
		runtimeExceptionExtends.put("ClosedWatchServiceException", "IllegalStateException");
		runtimeExceptionExtends.put("ConnectionPendingException", "IllegalStateException");

		runtimeExceptionExtends.put("FormatterClosedException", "IllegalStateException");
		runtimeExceptionExtends.put("IllegalBlockingModeException", "IllegalStateException");
		runtimeExceptionExtends.put("IllegalComponentStateException", "IllegalStateException");
		runtimeExceptionExtends.put("InvalidDnDOperationException", "IllegalStateException");
		runtimeExceptionExtends.put("InvalidMarkException", "IllegalStateException");
		runtimeExceptionExtends.put("NoConnectionPendingException", "IllegalStateException");
		runtimeExceptionExtends.put("NonReadableChannelException", "IllegalStateException");
		runtimeExceptionExtends.put("NonWritableChannelException", "IllegalStateException");
		runtimeExceptionExtends.put("NotYetBoundException", "IllegalStateException");
		runtimeExceptionExtends.put("NotYetConnectedException", "IllegalStateException");
		runtimeExceptionExtends.put("OverlappingFileLockException", "IllegalStateException");
		runtimeExceptionExtends.put("ReadPendingException", "IllegalStateException");
		runtimeExceptionExtends.put("ShutdownChannelGroupException", "IllegalStateException");
		runtimeExceptionExtends.put("WritePendingException", "IllegalStateException");
		runtimeExceptionExtends.put("IllegalStateException", "RuntimeException");
		runtimeExceptionExtends.put("IllformedLocaleException", "RuntimeException");
		runtimeExceptionExtends.put("ImagingOpException", "RuntimeException");
		runtimeExceptionExtends.put("IncompleteAnnotationException", "RuntimeException");
		runtimeExceptionExtends.put("IndexOutOfBoundsException", "RuntimeException");
		runtimeExceptionExtends.put("ArrayIndexOutOfBoundsException", "IndexOutOfBoundsException");
		runtimeExceptionExtends.put("StringIndexOutOfBoundsException", "IndexOutOfBoundsException");
		runtimeExceptionExtends.put("JMRuntimeException", "RuntimeException");
		runtimeExceptionExtends.put("MonitorSettingException", "JMRuntimeException");
		runtimeExceptionExtends.put("RuntimeErrorException", "JMRuntimeException");
		runtimeExceptionExtends.put("RuntimeMBeanException", "JMRuntimeException");
		runtimeExceptionExtends.put("RuntimeOperationsException", "JMRuntimeException");

		runtimeExceptionExtends.put("LSException", "RuntimeException");
		runtimeExceptionExtends.put("MalformedParameterizedTypeException", "RuntimeException");
		runtimeExceptionExtends.put("MalformedParametersException", "RuntimeException");
		runtimeExceptionExtends.put("MirroredTypesException", "RuntimeException");
		runtimeExceptionExtends.put("MissingResourceException", "RuntimeException");
		runtimeExceptionExtends.put("NegativeArraySizeException", "RuntimeException");
		runtimeExceptionExtends.put("NoSuchElementException", "RuntimeException");
		runtimeExceptionExtends.put("InputMismatchException", "NoSuchElementException");
		runtimeExceptionExtends.put("NullPointerException", "RuntimeException");
		runtimeExceptionExtends.put("ProfileDataException", "RuntimeException");
		runtimeExceptionExtends.put("ProviderException", "RuntimeException");
		runtimeExceptionExtends.put("ProviderNotFoundException", "RuntimeException");
		runtimeExceptionExtends.put("RasterFormatException", "RuntimeException");
		runtimeExceptionExtends.put("RejectedExecutionException", "RuntimeException");
		runtimeExceptionExtends.put("SecurityException", "RuntimeException");
		runtimeExceptionExtends.put("AccessControlException", "SecurityException");
		runtimeExceptionExtends.put("RMISecurityException", "SecurityException");
		runtimeExceptionExtends.put("SystemException", "RuntimeException");
		//
		runtimeExceptionExtends.put("TypeConstraintException", "RuntimeException");
		runtimeExceptionExtends.put("TypeNotPresentException", "RuntimeException");
		runtimeExceptionExtends.put("UncheckedIOException", "RuntimeException");
		runtimeExceptionExtends.put("UndeclaredThrowableException", "RuntimeException");
		runtimeExceptionExtends.put("UnknownEntityException", "RuntimeException");
		runtimeExceptionExtends.put("UnknownAnnotationValueException", "UnknownEntityException");
		runtimeExceptionExtends.put("UnknownElementException", "UnknownEntityException");
		runtimeExceptionExtends.put("UnknownTypeException", "UnknownEntityException");
		runtimeExceptionExtends.put("UnmodifiableSetException", "RuntimeException");
		runtimeExceptionExtends.put("UnsupportedOperationException", "RuntimeException");
		runtimeExceptionExtends.put("HeadlessException", "UnsupportedOperationException");
		runtimeExceptionExtends.put("ReadOnlyBufferException", "UnsupportedOperationException");
		runtimeExceptionExtends.put("ReadOnlyFileSystemException", "UnsupportedOperationException");
		runtimeExceptionExtends.put("WebServiceException", "RuntimeException");
		runtimeExceptionExtends.put("ProtocolException", "WebServiceException");
		runtimeExceptionExtends.put("HTTPException", "ProtocolException");
		runtimeExceptionExtends.put("SOAPFaultException", "ProtocolException");
		runtimeExceptionExtends.put("WrongMethodTypeException", "RuntimeException");

	}

	public void findExceptions(IProject project) throws JavaModelException {
//		resultMap.clear();
		initExceptionExtends();
		initRunTimeExceptionExtends();
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		// Find all methodinvocation and create call graph
		for (IPackageFragment mypackage : packages) {

//			findTargetCatchClauses(mypackage);

			findAllMehodInvocation(mypackage);
		}

		// analyze 3 anti-patterns.
		for (IPackageFragment mypackage : packages) {

			findTargetCatchClauses(mypackage);

//			findAllMehodInvocation(mypackage);
		}
		System.out.println(DetectException.resultMap.size());
		System.out.println("finish.");
		downLoadTXTFile(project);

	}

	private String getAbsoluteFilePath(ICompilationUnit icu) {
		String filePath = "";
		String workSpacePath = "";
		String test ="";
		try {
			test = icu.getCorrespondingResource().getLocation().toString();
//			filePath = icu.getCorrespondingResource().getFullPath().toOSString();
//			
//			workSpacePath = icu.getCorrespondingResource().getWorkspace().getRoot().getLocation().toFile()
//					.getAbsolutePath();
//			workSpacePath = workSpacePath+"/hadoop-rel-release-2.9.0";
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return test;
//		return workSpacePath + filePath;
	}

	private void checkTODOFIXMEComments(CompilationUnit unit, ICompilationUnit original) {
		HashSet<CatchClause> set = new HashSet<CatchClause>();
		String fileName = original.getPath().toString();
		int comment_lines_in_catch = 0;
		int comment_lines_in_try = 0;
		for (Comment comment : (List<Comment>) unit.getCommentList()) {
			CommentVisitor commentVisitor = new CommentVisitor(unit, getAbsoluteFilePath(original));
			comment.accept(commentVisitor);

			comment_lines_in_catch += commentVisitor.single_comment_lines_in_catch_count;

			comment_lines_in_try += commentVisitor.single_comment_lines_in_try_count;

			set.addAll(commentVisitor.getCatchContainsTODOorFIXMEComment());

		}
		// update result here
		int todo_fixme_count = set.size();

		updateCommentsResult(fileName, todo_fixme_count, todo_fixme_count, comment_lines_in_catch,
				comment_lines_in_try);

	}

	private void findTargetCatchClauses(IPackageFragment packageFragment) throws JavaModelException {

		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {

			CompilationUnit parsedCompilationUnit = parse(unit);
			String s = unit.getPath().toString();
			if(s.contains("/test/")) {
				continue;
			}


			Result res = new Result(s);
			if (!DetectException.resultMap.containsKey(s)) {
				DetectException.resultMap.put(s, res);
				CountOfFiles++;
			}else {
				continue;
			}
			if(s.contains("/hadoop-yarn-api/src/main/java/org/apache/hadoop/yarn/api/protocolrecords/package-info.java")) {
				System.out.println("test found");
			}
			checkTODOFIXMEComments(parsedCompilationUnit, unit);

			TryBlockVisitor tryBlockVisitor = new TryBlockVisitor(s);
			parsedCompilationUnit.accept(tryBlockVisitor);
			CatchClauseVisitor exceptionVisitor = new CatchClauseVisitor(s);
			parsedCompilationUnit.accept(exceptionVisitor);
			int tryQuantity = tryBlockVisitor.tryQuantity;
			int catchQuantity = exceptionVisitor.countOfCatchBlock;
			int loc_try = tryBlockVisitor.lOC;
			int loc_catch = exceptionVisitor.lOC;
			//
			int try_in_Declaration = tryBlockVisitor.try_in_Declaration;
			int try_in_Condition = tryBlockVisitor.try_in_Condition;
			int try_in_Loop = tryBlockVisitor.try_in_Loop;
			int try_int_EH = tryBlockVisitor.try_int_EH;
			int try_in_Other = tryBlockVisitor.try_in_Other;
			int invoked_methods = tryBlockVisitor.invoked_methods;

			updateResult(s, tryQuantity, catchQuantity, loc_try, loc_catch, try_in_Declaration, try_in_Condition,
					try_in_Loop, try_int_EH, try_in_Other, invoked_methods);

			int actions_Abort = tryBlockVisitor.actions_abort;
			int actions_Continue = tryBlockVisitor.actions_continue;
			int actions_Default = tryBlockVisitor.actions_default;
			int actions_Empty = tryBlockVisitor.actions_empty;
			int actions_Log = tryBlockVisitor.actions_log;
			int actions_Method = tryBlockVisitor.actions_method;
			int actions_Nestedtry = tryBlockVisitor.actions_nestedtry;
			int actions_Return = tryBlockVisitor.actions_return;
			int actions_Throwcurrent = tryBlockVisitor.actions_throwcurrent;
			int actions_Thrownew = tryBlockVisitor.actions_thrownew;
			int actions_Throwwrap = tryBlockVisitor.actions_throwwrap;

			updateActionsResult(s, actions_Abort, actions_Continue, actions_Default, actions_Empty, actions_Log,
					actions_Method, actions_Nestedtry, actions_Return, actions_Throwcurrent, actions_Thrownew,
					actions_Throwwrap);
//			printExceptions(exceptionVisitor);

			updatePercentageResult(s);

			getMethodsWithTargetCatchClauses(exceptionVisitor);
			getOuterClass(exceptionVisitor, parsedCompilationUnit);
			CountOfCatchBlock = CountOfCatchBlock + exceptionVisitor.countOfCatchBlock;

		}
	}

	private void updatePercentageResult(String fileName) {
		Result res = DetectException.resultMap.get(fileName);
		if (res.TryQuantity != 0 && res.CatchQuantity!=0) {

			// 12 actions and subsumption specific
			res.Actions_Abort_Percentage = Math.min((float) 1.0, (float) res.Actions_Abort / (float) res.TryQuantity);
			res.Actions_Continue_Percentage = Math.min((float) 1.0,
					(float) res.Actions_Continue / (float) res.TryQuantity);
			res.Actions_Default_Percentage = Math.min((float) 1.0,
					(float) res.Actions_Default / (float) res.TryQuantity);
			res.Actions_Empty_Percentage = Math.min((float) 1.0, (float) res.Actions_Empty / (float) res.TryQuantity);
			res.Actions_Log_Percentage = Math.min((float) 1.0, (float) res.Actions_Log / (float) res.TryQuantity);
			res.Actions_Method_Percentage = Math.min((float) 1.0, (float) res.Actions_Method / (float) res.TryQuantity);
			res.Actions_Nestedtry_Percentage = Math.min((float) 1.0,
					(float) res.Actions_Nestedtry / (float) res.TryQuantity);
			res.Actions_Return_Percentage = Math.min((float) 1.0, (float) res.Actions_Return / (float) res.TryQuantity);
			res.Actions_Throwcurrent_Percentage = Math.min((float) 1.0,
					(float) res.Actions_Throwcurrent / (float) res.TryQuantity);
			res.Actions_Thrownew_Percentage = Math.min((float) 1.0,
					(float) res.Actions_Thrownew / (float) res.TryQuantity);
			res.Actions_Throwwrap_Percentage = Math.min((float) 1.0,
					(float) res.Actions_Throwwrap / (float) res.TryQuantity);
			res.Actions_Todo_Percentage = Math.min((float) 1.0, (float) res.Actions_Todo / (float) res.TryQuantity);
			res.SubSumptionPercentage = Math.min((float) 1.0, (float) res.SubSumptionCount / (float) res.TryQuantity);
			res.SpecificPercentage = Math.min((float) 1.0, (float) res.SpecificCount / (float) res.TryQuantity);
			// 15 anti-patterns
			res.OvercatchPercentage = Math.min((float) 1.0, (float) res.OvercatchCount / (float) res.CatchQuantity);
			res.OvercatchAbortPercentage = Math.min((float) 1.0,
					(float) res.OvercatchAbortCount / (float) res.CatchQuantity);
			res.DoNothingPercentage = Math.min((float) 1.0, (float) res.DoNothingCount / (float) res.CatchQuantity);
			res.LogReturnNullPercentage = Math.min((float) 1.0,
					(float) res.ReturnNullCount / (float) res.CatchQuantity);
			res.CatchGenericPercentage = Math.min((float) 1.0,
					(float) res.CatchGenericCount / (float) res.CatchQuantity);
			res.DestructiveWrappingPercentage = Math.min((float) 1.0,
					(float) res.DestructiveWrappingCount / (float) res.CatchQuantity);
			res.DummyPercentage = Math.min((float) 1.0, (float) res.DummyCount / (float) res.CatchQuantity);
			res.IgnoreInterruptedExceptionPercentage = Math.min((float) 1.0,
					(float) res.IgnoreInterruptedExceptionCount / (float) res.CatchQuantity);
			res.IncompletePercentage = Math.min((float) 1.0, (float) res.IncompleteCount / (float) res.CatchQuantity);
			res.LogReturnNullPercentage = Math.min((float) 1.0,
					(float) res.LogReturnNullCount / (float) res.CatchQuantity);
			res.LogThrowPercentage = Math.min((float) 1.0, (float) res.LogThrowCount / (float) res.CatchQuantity);
			res.MultipleLineLogPercentage = Math.min((float) 1.0,
					(float) res.MultipleLineLogCount / (float) res.CatchQuantity);
			res.Actions_Nestedtry_Percentage = Math.min((float) 1.0,
					(float) res.NestedTryCount / (float) res.CatchQuantity);
			res.GetClausePercentage = Math.min((float) 1.0, (float) res.GetClauseCount / (float) res.CatchQuantity);
			res.ThrowInFinallyPercentage = Math.min((float) 1.0,
					(float) res.ThrowInFinallyCount / (float) res.CatchQuantity);
			res.UnhandledExceptionsPercentage = Math.min((float) 1.0,
					(float) res.UnhandledExceptionsCount / (float) res.CatchQuantity);
			res.UnreachableExceptionsPercentage = Math.min((float) 1.0,
					(float) res.UnreachableExceptionsCount / (float) res.CatchQuantity);
		}

	}

	private void updateResult(String file, int tryQuantity, int catchQuantity, int loc_try, int loc_catch,
			int try_in_declaration, int try_in_condition, int try_in_loop, int try_in_EH, int try_in_other,
			int invoked_methods) {
		Result res = DetectException.resultMap.get(file);
		res.addTryQuantity(tryQuantity);
		res.addCatchQuantity(catchQuantity);
		res.addLOC_Try(loc_try);
		res.addLOC_Catch(loc_catch);
		res.addTry_In_Declaration(try_in_declaration);
		res.addTry_In_Condition(try_in_condition);
		res.addTry_In_Loop(try_in_loop);
		res.addTry_In_EH(try_in_EH);
		res.addTry_In_Other(try_in_other);
		res.addInvokedMethodsCount(invoked_methods);

	}

	private void updateActionsResult(String file, int ac_abort, int ac_continue, int ac_default, int ac_empty,
			int ac_log, int ac_method, int ac_nestedtry, int ac_return, int ac_thrwocurrent, int ac_thrownew,
			int ac_throwwrap) {

		Result res = DetectException.resultMap.get(file);
		res.addActions_Abort(ac_abort);
		res.addActions_Continue(ac_continue);
		res.addActions_Default(ac_default);
		res.addActions_Empty(ac_empty);
		res.addActions_Log(ac_log);
		res.addActions_Method(ac_method);
		res.addActions_Nestedtry(ac_nestedtry);
		res.addActions_Return(ac_return);
		res.addActions_Throwcurrent(ac_thrwocurrent);
		res.addActions_Thrownew(ac_thrownew);
		res.addActions_Throwwrap(ac_throwwrap);

	}

	private void updateCommentsResult(String file, int actions_todo, int incomplete, int comment_in_try,
			int comment_in_catch) {

		Result res = DetectException.resultMap.get(file);

		res.addActions_Todo(actions_todo);
		res.addIncompleteCount(incomplete);
		res.addComment_In_Catch(comment_in_catch);
		res.addComment_In_Try(comment_in_try);

	}

	private void getOuterClass(CatchClauseVisitor catchClauseVisitor, CompilationUnit parsedCompilationUnit) {

		int NumOfMultipleLine = catchClauseVisitor.getMultipleLineLogCatches().size();
		int NumOfWrap = catchClauseVisitor.getDestructiveWrappingCatches().size();
		int NumOfOverCatch = catchClauseVisitor.getOverCatches().size();

		HashMap<String, Integer> antiPatterns = new HashMap<String, Integer>();
		antiPatterns.put("CountOfMultipleLine", NumOfMultipleLine);
		antiPatterns.put("CountOfWrap", NumOfWrap);
		antiPatterns.put("CountOfOverCatch", NumOfOverCatch);
		antiPatternsContainer.put(parsedCompilationUnit.getJavaElement().getPath().toString(), antiPatterns);
	}

	private static final String NEW_LINE_SEPARATOR = "\n";

	private void downLoadTXTFile(IProject project) {
		String format = "%40s %20s %15s %15s";

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(project.getName() + ".txt");
			fileWriter
					.append(String.format(format, "File path\t", "#Wrap\t", "#OverCatch\t", "#MultiLine\t").toString());
			fileWriter.append(NEW_LINE_SEPARATOR);

			for (String fileName : antiPatternsContainer.keySet()) {
				String[] subFileNameString = fileName.toString().split("/");
				HashMap<String, Integer> antiPatterns = antiPatternsContainer.get(fileName);
				fileWriter.append(String.format(format,
						subFileNameString[1] + "/.../" + subFileNameString[subFileNameString.length - 1] + "\t",
						antiPatterns.get("CountOfWrap").toString() + "\t",
						antiPatterns.get("CountOfOverCatch").toString() + "\t",
						antiPatterns.get("CountOfMultipleLine").toString() + "\t"));

				fileWriter.append(NEW_LINE_SEPARATOR);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
	}

	private void findAllMehodInvocation(IPackageFragment packageFragment) throws JavaModelException {

		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
			CompilationUnit parsedCompilationUnit = parse(unit);

			MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();
//			ConstructorInvocationVisitor conInvocationVisitor = new ConstructorInvocationVisitor();
			parsedCompilationUnit.accept(methodInvocationVisitor);
//			parsedCompilationUnit.accept(conInvocationVisitor);
			CountOfMethodInvocation = CountOfMethodInvocation + methodInvocationVisitor.countOfMethodInvocation;

		}

	}

	private void getMethodsWithTargetCatchClauses(CatchClauseVisitor catchClauseVisitor) {

//		for(CatchClause multiLineCatch: catchClauseVisitor.getMultipleLineLogCatches()) {
//			multiLineLogCatchMethod.add(findMethodForCatch(multiLineCatch));
//			suspectMethods.put(findMethodForCatch(multiLineCatch), "MultiLineLogCatch");
//		}
//
//		for(CatchClause destructiveWrappingCatch: catchClauseVisitor.getDestructiveWrappingCatches()) {
//			destructiveWrappingMethod.add(findMethodForCatch(destructiveWrappingCatch));
//			suspectMethods.put(findMethodForCatch(destructiveWrappingCatch), "destructiveWrappingCatch");
//		}
//
//		for(CatchClause overCatch: catchClauseVisitor.getOverCatches()) {
//			overCatchMethod.add(findMethodForCatch(overCatch));
//			suspectMethods.put(findMethodForCatch(overCatch), "overCatch");
//		}
//
//		printInvocations();
		printInvocations2(catchClauseVisitor);
	}

	private MethodDeclaration findMethodForCatch(CatchClause catchClause) {
		return (MethodDeclaration) findParentMethodDeclaration(catchClause);
	}

	private ASTNode findParentMethodDeclaration(ASTNode node) {
		if (node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
			return node.getParent();
		} else {
			return findParentMethodDeclaration(node.getParent());
		}
	}

	private ASTNode findParentTryStatment(ASTNode node) {

		if (node.getParent().getNodeType() == ASTNode.TRY_STATEMENT) {
			return node.getParent();
		} else {
			return findParentMethodDeclaration(node.getParent());
		}
	}

	private void printExceptions(CatchClauseVisitor visitor) {
		SampleHandler.printMessage("__________________MULTIPLE LINE CATCHES___________________");
		for (CatchClause statement : visitor.getMultipleLineLogCatches()) {
			SampleHandler.printMessage(statement.toString());
		}
		SampleHandler.printMessage("__________________DESTRUCTIVE WRAPPING CATCHES___________________");
		for (CatchClause statement : visitor.getDestructiveWrappingCatches()) {
			SampleHandler.printMessage(statement.toString());
		}

		SampleHandler.printMessage("__________________OVER CATCHES___________________");
		for (CatchClause statement : visitor.getOverCatches()) {
			SampleHandler.printMessage(statement.toString());
		}

	}

	public static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);

		return (CompilationUnit) parser.createAST(null); // parse
	}

	private void printInvocations() {

		for (MethodDeclaration declaration : multiLineLogCatchMethod) {
			SampleHandler.printMessage(String.format("Following method suffers from the %s pattern", "multiLine-Log"));
			SampleHandler.printMessage(declaration.toString());
		}

		for (MethodDeclaration declaration : destructiveWrappingMethod) {
			SampleHandler.printMessage(
					String.format("Following method suffers from the %s pattern", "destructive Wrapping"));
			SampleHandler.printMessage(declaration.toString());
		}

		for (MethodDeclaration declaration : overCatchMethod) {
			SampleHandler.printMessage(String.format("Following method suffers from the %s pattern", "over Catch"));
			SampleHandler.printMessage(declaration.toString());
		}
	}

	private void printInvocations2(CatchClauseVisitor catchClauseVisitor) {
//		CountOfOverCatch = catchClauseVisitor.getOverCatches().size();
//		CountOfMultipleLine = catchClauseVisitor.getMultipleLineLogCatches().size();
//		CountOfWrap = catchClauseVisitor.getDestructiveWrappingCatches().size();

		for (CatchClause multiLineCatch : catchClauseVisitor.getMultipleLineLogCatches()) {
			CountOfMultipleLine++;
			SampleHandler.printMessage(
					String.format("====Following method suffers from the %s pattern====", "Multiple-line Log"));
			SampleHandler.printMessage(findParentTryStatment(multiLineCatch).toString());
		}

		for (CatchClause destructiveWrappingCatch : catchClauseVisitor.getDestructiveWrappingCatches()) {
			CountOfWrap++;
			SampleHandler.printMessage(
					String.format("====Following method suffers from the %s pattern====", "destructive Wrapping"));
			SampleHandler.printMessage(findParentTryStatment(destructiveWrappingCatch).toString());
		}

		for (CatchClause overCatch : catchClauseVisitor.getOverCatches()) {
			CountOfOverCatch++;
			SampleHandler
					.printMessage(String.format("====Following method suffers from the %s pattern====", "over Catch"));
			SampleHandler.printMessage(findParentTryStatment(overCatch).toString());
			SampleHandler.printMessage(catchClauseVisitor.overCatchesDetails.get(overCatch));

		}
	}
}
