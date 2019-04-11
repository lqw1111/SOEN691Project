package SOEN691.visitors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.TryStatement;

public class CommentVisitor extends ASTVisitor{
	public int single_comment_lines_in_catch_count = 0;
	public int single_comment_lines_in_try_count = 0;
	HashSet<CatchClause> catchContainsTODOorFIXMEComment = new HashSet<>();
	public HashSet<CatchClause> getCatchContainsTODOorFIXMEComment() {
		return catchContainsTODOorFIXMEComment;
	}

	CompilationUnit compilationUnit;
	private String source;


	public CommentVisitor(CompilationUnit compilationUnit, String source) {
		super();
		this.compilationUnit = compilationUnit;
		this.source= getOriginalFileAsString(source);
	
	}

	@Override
	public boolean visit(LineComment node) {

		int start = node.getStartPosition();
		int end = start + node.getLength();
		String comment = "";
		try{
			 comment = source.substring(start,end);
		}catch(Exception e) {
			return super.visit(node);
		}
		HashSet<CatchClause> temp = new HashSet<CatchClause>();
		temp = getParentCatchClause(start, end);
		if(!temp.isEmpty()) {
			CatchClause clause = temp.iterator().next();
			single_comment_lines_in_catch_count=1;
			
			
			if(fIXMEorTODOinString(comment)) {
				catchContainsTODOorFIXMEComment.addAll(temp);
			}	
			return super.visit(node);
			
		}
		HashSet<TryStatement> temp1 = new HashSet<TryStatement>();
		temp1 = getParentTryStatement(start, end);
		if(!temp1.isEmpty()) {
			TryStatement tryStatement = temp1.iterator().next();
			single_comment_lines_in_try_count=1;
			return super.visit(node);
			
		}
		

		return super.visit(node);
	}

	@Override
	public boolean visit(BlockComment node) {

		int start = node.getStartPosition();

		int end = start + node.getLength();
		String comment = "";
		try {
			comment = source.substring(start,end);
		}
		catch(Exception e) {
			return super.visit(node);
		
		}
		int lineCount = comment.split("\n").length;
		HashSet<CatchClause> temp = new HashSet<CatchClause>();
		temp = getParentCatchClause(start, end);
		if(!temp.isEmpty()) {
			CatchClause clause = temp.iterator().next();
			single_comment_lines_in_catch_count = lineCount;
			if(fIXMEorTODOinString(comment)) {
				catchContainsTODOorFIXMEComment.addAll(temp);
			}

			return super.visit(node);
		}
		HashSet<TryStatement> temp1 = new HashSet<TryStatement>();
		temp1 = getParentTryStatement(start, end);
		if(!temp1.isEmpty()) {
			TryStatement tryStatement = temp1.iterator().next();
			single_comment_lines_in_try_count=lineCount;
			return super.visit(node);
			
		}
		
		
		
		

		return super.visit(node);
	}

	public void preVisit(ASTNode node) {

	}

	private HashSet<CatchClause> getParentCatchClause(int commentStart, int commentEnd ) {
		CatchClauseVisitor1 catchClauseVisitor = new CatchClauseVisitor1(commentStart, commentEnd);
		compilationUnit.accept(catchClauseVisitor);
		return catchClauseVisitor.getIndebtCatches();
	}
	
	private HashSet<TryStatement> getParentTryStatement(int commentStart, int commentEnd ) {
		TryBlockVisitor1 tryBlockVisitor = new TryBlockVisitor1(commentStart, commentEnd);
		compilationUnit.accept(tryBlockVisitor);
		return tryBlockVisitor.getIndebtCatches();
	}
	
	

	private boolean fIXMEorTODOinString(String comment) {
		if(comment.contains("FIXME")|| comment.contains("TODO")) {
			return true;
		}
		return false;
	}
	private String updatePath(String path) {
		
		if(path.contains("/hadoop-annotations/")) {
			path = path.replace("/hadoop-annotations/", "/hadoop-common-project/hadoop-annotations/");		
		}
		else if(path.contains("/hadoop-auth/")) {
			path = path.replace("/hadoop-auth/", "/hadoop-common-project/hadoop-auth/");			
		}
		else if(path.contains("/hadoop-auth-examples/")) {
			path = path.replace("/hadoop-auth-examples/", "/hadoop-common-project/hadoop-auth-examples/");			
		}
		else if(path.contains("/hadoop-common/")) {
			path = path.replace("/hadoop-common/", "/hadoop-common-project/hadoop-common/");			
		}
		else if(path.contains("/hadoop-kms/")) {
			path = path.replace("/hadoop-kms/", "/hadoop-common-project/hadoop-kms/");			
		}
		else if(path.contains("/hadoop-minikdc/")) {
			path = path.replace("/hadoop-minikdc/", "/hadoop-common-project/hadoop-minikdc/");			
		}
		else if(path.contains("/hadoop-nfs/")) {
			path = path.replace("/hadoop-nfs/", "/hadoop-common-project/hadoop-nfs/");		
		}
		else if(path.contains("/hadoop-hdfs/")) {
			path = path.replace("/hadoop-hdfs/", "/hadoop-hdfs-project/hadoop-hdfs/");		
		}
		else if(path.contains("/hadoop-hdfs-client/")) {
			path = path.replace("/hadoop-hdfs-client/", "/hadoop-hdfs-project/hadoop-hdfs-client/");		
		}
		else if(path.contains("/hadoop-hdfs-httpfs/")) {
			path = path.replace("/hadoop-hdfs-httpfs/", "/hadoop-hdfs-project/hadoop-hdfs-httpfs/");		
		}
		else if(path.contains("/hadoop-hdfs-native-client/")) {
			path = path.replace("/hadoop-hdfs-native-client/", "/hadoop-hdfs-project/hadoop-hdfs-native-client/");		
		}
		else if(path.contains("/hadoop-hdfs-nfs/")) {
			path = path.replace("/hadoop-hdfs-nfs/", "/hadoop-hdfs-project/hadoop-hdfs-nfs/");		
		}
		else if(path.contains("/hadoop-mapreduce-client-app/")) {
			path = path.replace("/hadoop-mapreduce-client-app/", "/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-app/");		
		}
		else if(path.contains("/hadoop-mapreduce-client-common/")) {
			path = path.replace("/hadoop-mapreduce-client-common/", "/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-common/");		
		}
		else if(path.contains("/hadoop-mapreduce-client-core/")) {
			path = path.replace("/hadoop-mapreduce-client-core/", "/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-core/");		
		}
		else if(path.contains("/hadoop-mapreduce-client-hs/")) {
			path = path.replace("/hadoop-mapreduce-client-hs/", "/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-hs/");		
		}
		else if(path.contains("/hadoop-mapreduce-client-hs-plugins/")) {
			path = path.replace("/hadoop-mapreduce-client-hs-plugins/", "/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-hs-plugins/");		
		}
		else if(path.contains("/hadoop-mapreduce-client-jobclient/")) {
			path = path.replace("/hadoop-mapreduce-client-jobclient/", "/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-jobclient/");		
		}
		else if(path.contains("/hadoop-mapreduce-client-shuffle/")) {
			path = path.replace("/hadoop-mapreduce-client-shuffle/", "/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-shuffle/");		
		}
		
		
		
		
		
		
		else if(path.contains("/hadoop-mapreduce-examples/")) {
			path = path.replace("/hadoop-mapreduce-examples/", "/hadoop-mapreduce-project/hadoop-mapreduce-examples/");		
		}
		else if(path.contains("/hadoop-yarn-api/")) {
			path = path.replace("/hadoop-yarn-api/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-api/");		
		}
		else if(path.contains("/hadoop-yarn-applications-distributedshell/")) {
			path = path.replace("/hadoop-yarn-applications-distributedshell/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-applications/hadoop-yarn-applications-distributedshell/");		
		}
		else if(path.contains("/hadoop-yarn-applications-unmanaged-am-launcher/")) {
			path = path.replace("/hadoop-yarn-applications-unmanaged-am-launcher/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-applications/hadoop-yarn-applications-unmanaged-am-launcher/");		
		}
		else if(path.contains("/hadoop-yarn-client/")) {
			path = path.replace("/hadoop-yarn-client/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-client/");		
		}
		else if(path.contains("/hadoop-yarn-common/")) {
			path = path.replace("/hadoop-yarn-common/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-common/");		
		}
		else if(path.contains("/hadoop-yarn-registry/")) {
			path = path.replace("/hadoop-yarn-registry/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-registry/");		
		}
		else if(path.contains("/hadoop-yarn-server-applicationhistoryservice/")) {
			path = path.replace("/hadoop-yarn-server-applicationhistoryservice/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-applicationhistoryservice/");		
		}
		else if(path.contains("/hadoop-yarn-server-common/")) {
			path = path.replace("/hadoop-yarn-server-common/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-common/");		
		}
		else if(path.contains("/hadoop-yarn-server-nodemanager/")) {
			path = path.replace("/hadoop-yarn-server-nodemanager/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager/");		
		}
		else if(path.contains("/hadoop-yarn-server-resourcemanager/")) {
			path = path.replace("/hadoop-yarn-server-resourcemanager/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/");		
		}
		else if(path.contains("/hadoop-yarn-server-router/")) {
			path = path.replace("/hadoop-yarn-server-router/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-router/");		
		}
		else if(path.contains("/hadoop-yarn-server-sharedcachemanager/")) {
			path = path.replace("/hadoop-yarn-server-sharedcachemanager/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-sharedcachemanager/");		
		}
		else if(path.contains("/hadoop-yarn-server-tests/")) {
			path = path.replace("/hadoop-yarn-server-tests/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-tests/");		
		}
		else if(path.contains("/hadoop-yarn-server-timeline-pluginstorage/")) {
			path = path.replace("/hadoop-yarn-server-timeline-pluginstorage/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-timeline-pluginstorage/");		
		}
		else if(path.contains("/hadoop-yarn-server-timelineservice/")) {
			path = path.replace("/hadoop-yarn-server-timelineservice/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-timelineservice/");		
		}
		else if(path.contains("/hadoop-yarn-server-timelineservice-hbase/")) {
			path = path.replace("/hadoop-yarn-server-timelineservice-hbase/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-timelineservice-hbase/");		
		}
		else if(path.contains("/hadoop-yarn-server-timelineservice-hbase-tests/")) {
			path = path.replace("/hadoop-yarn-server-timelineservice-hbase-tests/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-timelineservice-hbase-tests/");		
		}
		else if(path.contains("/hadoop-yarn-server-web-proxy/")) {
			path = path.replace("/hadoop-yarn-server-web-proxy/", "/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-web-proxy/");		
		}
		
		
		else {
        	System.out.println("");

		}
		
		return path;
		
	}
	private String getOriginalFileAsString(String path) {
//		path = updatePath(path);
	    String content = "";
	    try
	    {
	        content = new String ( Files.readAllBytes( Paths.get(path) ) );
	        if(content == "") {
	        	System.out.println("");
	        }
	    }
	    catch (IOException e)
	    {
	       
	    }
	    return content;
	}

}
