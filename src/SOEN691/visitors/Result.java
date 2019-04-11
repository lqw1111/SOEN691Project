package SOEN691.visitors;

import org.eclipse.jdt.internal.core.manipulation.util.Strings;

public class Result {
	public String FileName ="";
	//15 patterns
	public int OvercatchCount =0;
	public int DestructiveWrappingCount =0;
	public int MultipleLineLogCount = 0;
	public int DoNothingCount = 0;
	public int ReturnNullCount = 0;
	public int CatchGenericCount = 0;
	public int IgnoreInterruptedExceptionCount = 0;
	public int GetClauseCount = 0;
	public int OvercatchAbortCount =0;
	public int LogReturnNullCount = 0;
	public int LogThrowCount = 0;
	public int ThrowInFinallyCount = 0;
	public int NestedTryCount = 0;
	public int IncompleteCount = 0;

	public int DummyCount =0;


	public float OvercatchPercentage =0;
	public float DestructiveWrappingPercentage =0;
	public float MultipleLineLogPercentage = 0;
	public float DoNothingPercentage = 0;
	public float ReturnNullPercentage = 0;
	public float CatchGenericPercentage = 0;
	public float IgnoreInterruptedExceptionPercentage = 0;
	public float GetClausePercentage = 0;
	public float OvercatchAbortPercentage =0;
	public float LogReturnNullPercentage = 0;
	public float LogThrowPercentage = 0;
	public float ThrowInFinallyPercentage = 0;
	public float NestedTryPercentage = 0;
	public float IncompletePercentage = 0;
	public float DummyPercentage =0;
	

	public float SubSumptionPercentage = 0;
	public float SpecificPercentage= 0;
	
	
	

	//another metrics
	public int SubSumptionCount = 0;
	public int SpecificCount= 0;
	public int RecoverExCount =0;
	public int UnRecoverExCount = 0;
	public int TryQuantity = 0;
	public int CatchQuantity = 0;
	public int LOC_Try = 0;
	public int LOC_Catch = 0;
	public int Try_In_Declaration = 0;
	public int Try_In_Condition = 0;
	public int Try_In_Loop = 0;
	public int Try_In_EH=0;
	public int Try_In_Other =0;
	public int InvokedMethods = 0;
	//actions
	 public int Actions_Abort = 0;
	 public int Actions_Continue = 0;
	 public int Actions_Default = 0;//?
	 public int Actions_Empty = 0;
	 public int Actions_Log = 0;
	 public int Actions_Method = 0;
	 public int Actions_Nestedtry = 0;
	 public int Actions_Return = 0;
	 public int Actions_Throwcurrent = 0;
	 public int Actions_Thrownew = 0;
	 public int Actions_Throwwrap = 0;
	 public int Actions_Todo = 0;//
	 
	 public float Actions_Abort_Percentage = 0;
	 public float Actions_Continue_Percentage = 0;
	 public float Actions_Default_Percentage = 0;//?
	 public float Actions_Empty_Percentage = 0;
	 public float Actions_Log_Percentage = 0;
	 public float Actions_Method_Percentage = 0;
	 public float Actions_Nestedtry_Percentage = 0;
	 public float Actions_Return_Percentage = 0;
	 public float Actions_Throwcurrent_Percentage = 0;
	 public float Actions_Thrownew_Percentage = 0;
	 public float Actions_Throwwrap_Percentage = 0;
	 public float Actions_Todo_Percentage = 0;//
	 
	 
	 
	 public int Comment_In_Try = 0;
	 public int Comment_In_Catch = 0;
	 
	 
	 
	
	
	


	public Result(String fileName) {
		this.FileName = fileName;

	}
	public int getOvercatchCount() {
		return OvercatchCount;
	}
	public void addOvercatchCount() {
		OvercatchCount = OvercatchCount +1;
	}
	public void addDestructiveWrappingCount() {
		DestructiveWrappingCount = DestructiveWrappingCount+1;
	}
	public void addDummyCount(int dummy) {
		DummyCount = DummyCount+dummy;
	}
	public void addMultipleLineLogCount() {
		MultipleLineLogCount = MultipleLineLogCount+1;
	}
	public int getDestructiveWrappingCount() {
		return DestructiveWrappingCount;
	}
	public void addDestructiveWrappingCount(int destructiveWrappingCount) {
		DestructiveWrappingCount = DestructiveWrappingCount +destructiveWrappingCount;
	}
	public int getMultipleLineLogCount() {
		return MultipleLineLogCount;
	}
	public void addMultipleLineLogCount(int multipleLineLogCount) {
		MultipleLineLogCount = MultipleLineLogCount +multipleLineLogCount;
	}
	public void addOvercatchCount(int overcatchCount) {
		OvercatchCount = OvercatchCount +overcatchCount;
	}
	public void addDoNothingCount(int doNothingCount) {
		DoNothingCount = DoNothingCount +doNothingCount;
	}
	public void addReturnNullCount(int returnNullCount) {
		ReturnNullCount = ReturnNullCount +returnNullCount;
	}
	public void addCatchGenericCount(int catchGenericCount) {
		CatchGenericCount = CatchGenericCount +catchGenericCount;
	}
	public void addIgnoreInterruptedExceptionCountCount(int ignoreInterruptedExceptionCount) {
		IgnoreInterruptedExceptionCount = IgnoreInterruptedExceptionCount +ignoreInterruptedExceptionCount;
	}
	public void addGetClauseCountCount(int getClauseCount) {
		GetClauseCount = GetClauseCount +getClauseCount;
	}
	public void addOvercatchAbortCount(int overcatchAbortCount) {
		OvercatchAbortCount = OvercatchAbortCount +overcatchAbortCount;
	}
	public void addLogReturnNullCount(int logReturnNullCount) {
		LogReturnNullCount = LogReturnNullCount +logReturnNullCount;
	}
	public void addLogThrowCount(int logThrowCount) {
		LogThrowCount = LogThrowCount +logThrowCount;
	}
	public void addThrowInFinally(int throwInFinally) {
		ThrowInFinallyCount = ThrowInFinallyCount +throwInFinally;
	}
	public void addNestedTry(int nestedTry) {
		NestedTryCount = NestedTryCount +nestedTry;
	}
	public void addSubSumption(int subSumption) {
		SubSumptionCount =SubSumptionCount+subSumption;
		
	}
	public void addSpecific(int specificCount) {
		SpecificCount =SpecificCount+specificCount;
		
	}
	public void addRecoverExCount(int recoverExCount) {
		RecoverExCount =RecoverExCount+recoverExCount;
		
	}
	public void addUnRecoverExCount(int unRecoverExCount) {
		UnRecoverExCount =UnRecoverExCount+unRecoverExCount;
		
	}
	public void addTryQuantity(int tryQuantity) {
		TryQuantity =TryQuantity+tryQuantity;
		
	}
	public void addCatchQuantity(int catchQuantity) {
		CatchQuantity =CatchQuantity+catchQuantity;
		
	}
	public void addLOC_Try(int lOC_Try) {
		LOC_Try =LOC_Try+lOC_Try;
		
	}
	public void addLOC_Catch(int lOC_Catch) {
		LOC_Catch =LOC_Catch+lOC_Catch;
		
	}
	public void addTry_In_Declaration(int try_In_Declaration) {
		Try_In_Declaration += try_In_Declaration;
	}

	public void addTry_In_Condition(int try_In_Consition) {
		Try_In_Condition += try_In_Consition;
	}

	public void addTry_In_Loop(int try_In_Loop) {
		Try_In_Loop += try_In_Loop;
	}
	public void addTry_In_EH(int try_In_EH) {
		Try_In_EH += try_In_EH;
	}

	public void addTry_In_Other(int try_In_Other) {
		Try_In_Other += try_In_Other;
	}
	public void addInvokedMethodsCount(int invokedMethods) {
		InvokedMethods += invokedMethods;
	}
	
	public void addActions_Abort(int actions_Abort) {
		Actions_Abort += actions_Abort;
	}

	public void addActions_Continue(int actions_Continue) {
		Actions_Continue += actions_Continue;
	}

	public void addActions_Default(int actions_Default) {
		Actions_Default += actions_Default;
	}

	public void addActions_Empty(int actions_Empty) {
		Actions_Empty += actions_Empty;
	}

	public void addActions_Log(int actions_Log) {
		Actions_Log += actions_Log;
	}

	public void addActions_Method(int actions_Method) {
		Actions_Method += actions_Method;
	}

	public void addActions_Nestedtry(int actions_Nestedtry) {
		Actions_Nestedtry += actions_Nestedtry;
	}

	public void addActions_Return(int actions_Return) {
		Actions_Return += actions_Return;
	}

	public void addActions_Throwcurrent(int actions_Throwcurrent) {
		Actions_Throwcurrent += actions_Throwcurrent;
	}

	public void addActions_Thrownew(int actions_Thrownew) {
		Actions_Thrownew += actions_Thrownew;
	}

	public void addActions_Throwwrap(int actions_Throwwrap) {
		Actions_Throwwrap += actions_Throwwrap;
	}

	public void addActions_Todo(int actions_Todo) {
		Actions_Todo += actions_Todo;
	}
	public void add(int actions_Todo) {
		Actions_Todo += actions_Todo;
	}
	public void addIncompleteCount(int incompleteCount) {
		IncompleteCount += incompleteCount;
	}
	public void addComment_In_Catch(int comment_In_Catch) {
		Comment_In_Catch += comment_In_Catch;
	}
	public void addComment_In_Try(int comment_In_Try) {
		Comment_In_Try += comment_In_Try;
	}
	
	
	public String toCSVline() {
		if(this.FileName.equals("/hadoop-yarn-api/src/main/java/org/apache/hadoop/yarn/api/protocolrecords/package-info.java")) {
			System.out.println("found");
		}
		String str = "";
		str = this.FileName+","+
				String.valueOf(this.OvercatchCount)+","+
				String.valueOf(this.OvercatchAbortCount)+","+
				String.valueOf(this.DoNothingCount)+","+
				String.valueOf(this.ReturnNullCount)+","+
				String.valueOf(this.CatchGenericCount)+","+
				String.valueOf(this.DestructiveWrappingCount)+","+
				String.valueOf(this.DummyCount)+","+
				String.valueOf(this.IgnoreInterruptedExceptionCount)+","+
				String.valueOf(this.IncompleteCount)+","+
				String.valueOf(this.LogReturnNullCount)+","+
				String.valueOf(this.LogThrowCount)+","+
				String.valueOf(this.MultipleLineLogCount)+","+
				String.valueOf(this.NestedTryCount)+","+
				String.valueOf(this.GetClauseCount)+","+
				String.valueOf(this.ThrowInFinallyCount)+","+
				String.valueOf(this.SubSumptionCount)+","+
				String.valueOf(this.SpecificCount)+","+
				String.valueOf(this.UnRecoverExCount)+","+
				String.valueOf(this.RecoverExCount)+","+
				String.valueOf(this.TryQuantity)+","+
				String.valueOf(this.CatchQuantity)+","+
				
				//LOC
				String.valueOf(this.LOC_Try+this.Comment_In_Try)+","+
				String.valueOf(this.LOC_Catch+this.Comment_In_Catch)+","+
				//SLOC
				String.valueOf(this.LOC_Try)+","+
				String.valueOf(this.LOC_Catch)+","+
				String.valueOf(this.Try_In_Declaration)+","+
				String.valueOf(this.Try_In_Condition)+","+
				String.valueOf(this.Try_In_Loop)+","+
				String.valueOf(this.Try_In_EH)+","+
				String.valueOf(this.Try_In_Other)+","+
				String.valueOf(this.InvokedMethods)+","+
				String.valueOf(this.Actions_Abort)+","+
				String.valueOf(this.Actions_Continue)+","+
				String.valueOf(this.Actions_Default)+","+
				String.valueOf(this.Actions_Empty)+","+
				String.valueOf(this.Actions_Log)+","+
				String.valueOf(this.Actions_Method)+","+
				String.valueOf(this.Actions_Nestedtry)+","+
				String.valueOf(this.Actions_Return)+","+
				String.valueOf(this.Actions_Throwcurrent)+","+
				String.valueOf(this.Actions_Thrownew)+","+
				String.valueOf(this.Actions_Throwwrap)+","+
				String.valueOf(this.Actions_Todo)+","+
				//percentage  12 actions
				String.valueOf(this.Actions_Abort_Percentage)+","+
				String.valueOf(this.Actions_Continue_Percentage)+","+
				String.valueOf(this.Actions_Default_Percentage)+","+
				String.valueOf(this.Actions_Empty_Percentage)+","+
				String.valueOf(this.Actions_Log_Percentage)+","+
				String.valueOf(this.Actions_Method_Percentage)+","+
				String.valueOf(this.Actions_Nestedtry_Percentage)+","+
				String.valueOf(this.Actions_Return_Percentage)+","+
				String.valueOf(this.Actions_Throwcurrent_Percentage)+","+
				String.valueOf(this.Actions_Thrownew_Percentage)+","+
				String.valueOf(this.Actions_Throwwrap_Percentage)+","+
				String.valueOf(this.Actions_Todo_Percentage)+","+
				//15 antipatterns and subsumption specific
				String.valueOf(this.SubSumptionPercentage)+","+
				String.valueOf(this.SpecificPercentage)+","+
				String.valueOf(this.OvercatchPercentage)+","+
				String.valueOf(this.OvercatchAbortPercentage)+","+
				String.valueOf(this.DoNothingPercentage)+","+
				String.valueOf(this.ReturnNullPercentage)+","+
				String.valueOf(this.CatchGenericPercentage)+","+
				String.valueOf(this.DestructiveWrappingPercentage)+","+
				String.valueOf(this.DummyPercentage)+","+
				String.valueOf(this.IgnoreInterruptedExceptionPercentage)+","+
				String.valueOf(this.IncompletePercentage)+","+
				String.valueOf(this.LogReturnNullPercentage)+","+
				String.valueOf(this.LogThrowPercentage)+","+
				String.valueOf(this.MultipleLineLogPercentage)+","+
				String.valueOf(this.NestedTryPercentage)+","+
				String.valueOf(this.GetClausePercentage)+","+
				String.valueOf(this.ThrowInFinallyPercentage);
				
				

		return str;
	}
	
	
	

}
