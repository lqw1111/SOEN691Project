package SOEN691.patterns;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TryStatement;

import SOEN691.handlers.SampleHandler;
import SOEN691.visitors.CatchClauseVisitor;
import SOEN691.visitors.MethodInvocationVisitor;
import SOEN691.visitors.Node;
import SOEN691.visitors.TryBlockVisitor;

public class ExceptionFinder {
	HashMap<MethodDeclaration, String> suspectMethods = new HashMap<>();
	public int CountOfMethodInvocation = 0;
	public int CountOfCatchBlock = 0;
	public int CountOfOverCatch = 0;
	public int CountOfWrap = 0;
	public int CountOfMultipleLine = 0;

	HashSet<MethodDeclaration> multiLineLogCatchMethod = new HashSet<>();
	HashSet<MethodDeclaration> destructiveWrappingMethod = new HashSet<>();
	HashSet<MethodDeclaration> overCatchMethod = new HashSet<>();

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

	public void findExceptions(IProject project) throws JavaModelException {
		initExceptionExtends();
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
		System.out.println("finish.");
		downLoadTXTFile(project);

	}

	private void findTargetCatchClauses(IPackageFragment packageFragment) throws JavaModelException {

		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
			CompilationUnit parsedCompilationUnit = parse(unit);
			CatchClauseVisitor exceptionVisitor = new CatchClauseVisitor();
			TryBlockVisitor tryBlockVisitor = new TryBlockVisitor();
			parsedCompilationUnit.accept(exceptionVisitor);
			parsedCompilationUnit.accept(tryBlockVisitor);
//			printExceptions(exceptionVisitor);
			getMethodsWithTargetCatchClauses(exceptionVisitor);
			updateAntiPatternsContainer(exceptionVisitor, tryBlockVisitor, parsedCompilationUnit);
			CountOfCatchBlock = CountOfCatchBlock + exceptionVisitor.countOfCatchBlock;

		}
	}

	private void updateAntiPatternsContainer(CatchClauseVisitor catchClauseVisitor, TryBlockVisitor tryBlockVisitor,
			CompilationUnit parsedCompilationUnit) {

		int numOfMultipleLine = catchClauseVisitor.getMultipleLineLogCatches().size();
		int numOfWrap = catchClauseVisitor.getDestructiveWrappingCatches().size();
		int numOfOverCatch = catchClauseVisitor.getOverCatches().size();
		int numOfSLOCInCatchBlock = getNumOfCatchStatement(catchClauseVisitor);
		int numOfSLOCInTryBlock = getNumOfTryStatement(tryBlockVisitor);

		HashMap<String, Integer> antiPatterns = new HashMap<String, Integer>();
		antiPatterns.put("CountOfMultipleLine", numOfMultipleLine);
		antiPatterns.put("CountOfWrap", numOfWrap);
		antiPatterns.put("CountOfOverCatch", numOfOverCatch);
		antiPatterns.put("SLOCInTryBlock", numOfSLOCInTryBlock);
		antiPatterns.put("SLOCInCatchBlock", numOfSLOCInCatchBlock);
		antiPatternsContainer.put(parsedCompilationUnit.getJavaElement().getPath().toString(), antiPatterns);
	}

	private static final String NEW_LINE_SEPARATOR = "\n";

	private void downLoadTXTFile(IProject project) {
		String format = "%15s %15s %15s %15s %15s %15s";

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(project.getName() + ".csv");
//			fileWriter = new FileWriter("/Users/jingyang/Desktop/" + project.getName() + ".csv");
			fileWriter.append(String.format(format, "File path\t", "#Wrap\t", "#OverCatch\t", "#MultiLine\t",
					"SLOC in Try\t", "SLOC in Catch\t").toString());

			fileWriter.append(NEW_LINE_SEPARATOR);

			for (String fileName : antiPatternsContainer.keySet()) {
				HashMap<String, Integer> antiPatterns = antiPatternsContainer.get(fileName);
				fileWriter.append(String.format(format, fileName.toString() + "\t",
						antiPatterns.get("CountOfWrap").toString() + "\t",
						antiPatterns.get("CountOfOverCatch").toString() + "\t",
						antiPatterns.get("CountOfMultipleLine").toString() + "\t",
						antiPatterns.get("SLOCInTryBlock").toString() + "\t",
						antiPatterns.get("SLOCInCatchBlock").toString() + "\t"));

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

	private int getNumOfCatchStatement(CatchClauseVisitor catchClauseVisitor) {
		int count = 0;
		for (Map.Entry<CatchClause, Integer> catchBlocks : catchClauseVisitor.catchBlocksList.entrySet()) {
			count += catchBlocks.getValue();
		}
		return count;

	}

	private int getNumOfTryStatement(TryBlockVisitor tryBlockVisitor) {
		int count = 0;
		for (Map.Entry<TryStatement, Integer> tryBlocks : tryBlockVisitor.tryBlocksList.entrySet()) {
			count += tryBlocks.getValue();
		}
		return count;

	}
}
