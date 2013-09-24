package de.fub.agg2graph.input;
import java.util.ArrayList;
import java.util.List;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.GPSSegment;


public class SerializeAgg {

	public static GPSSegment getSegmentFromLastNode(AggNode last) {

		GPSSegment result = new GPSSegment();
		result.add(last);
		while(!last.getIn().isEmpty()) {
			last = last.getIn().iterator().next().getFrom();
			result.add(0, last);
		}
		return result;
	}
	
	public static List<GPSSegment> getSerialize(AggNode root) {
		List<GPSSegment> result = new ArrayList<GPSSegment>();
		if(root != null) {
			GPSSegment trace = new GPSSegment();
			result.add(trace);
			trace.add(root);
			
			if(root.getIn().size() == 1)
				previousBranch(result, trace, root.getIn().iterator().next().getFrom(), root);
			else if(root.getIn().size() > 1) {
				for(AggConnection conn : root.getIn()) {
					GPSSegment branchTrace = new GPSSegment();
					result.add(branchTrace);
					branchTrace.add(conn.getTo());
					previousBranch(result, branchTrace, conn.getFrom(), root);
				}

			}
			
			if(root.getOut().size() == 1)
				continueBranch(result, trace, root.getOut().iterator().next().getTo(), root);
			else if(root.getOut().size() > 1) {
				for(AggConnection conn : root.getOut()) {
					GPSSegment branchTrace = new GPSSegment();
					result.add(branchTrace);
					branchTrace.add(conn.getFrom());
					continueBranch(result, branchTrace, conn.getTo(), root);
				}

			}
		}
		
		return result;
	}
	
	public static void previousBranch(List<GPSSegment> tree, GPSSegment trace, AggNode node, AggNode source) {
		trace.add(0, node);
//		ArrayList<AggNode> out = null;
		if(node.getIn().size() == 1) {
			previousBranch(tree, trace, node.getIn().iterator().next().getFrom(), node);
			for(AggConnection connTo : node.getOut()) {
				if(!connTo.getTo().equals(source)) {
					GPSSegment branchTrace = new GPSSegment();
					tree.add(branchTrace);
					branchTrace.add(connTo.getFrom());
					continueBranch(tree, branchTrace, connTo.getTo(), connTo.getFrom());
				}
			}
//			out = new ArrayList<AggNode>(node.getOut().iterator().next().getTo());
		}
			
		else if(node.getIn().size() > 1) {
			for(AggConnection conn : node.getIn()) {
				GPSSegment branchTrace = new GPSSegment();
				tree.add(branchTrace);
				branchTrace.add(conn.getTo());
				previousBranch(tree, branchTrace, conn.getFrom(), node);
				for(AggConnection connTo : node.getOut()) {
					if(!connTo.getTo().equals(source)) {
						GPSSegment branchTraceTo = new GPSSegment();
						tree.add(branchTraceTo);
						branchTraceTo.add(connTo.getFrom());
						continueBranch(tree, branchTrace, connTo.getTo(), connTo.getFrom());
					}
				}
			}
		}
	}
	
	public static void continueBranch(List<GPSSegment> tree, GPSSegment trace, AggNode node, AggNode source) {
		trace.add(node);
//		ArrayList<AggNode> out = null;
		if(node.getOut().size() == 1) {
			continueBranch(tree, trace, node.getOut().iterator().next().getTo(), node);
			for(AggConnection connFrom : node.getIn()) {
				if(!connFrom.getFrom().equals(source)) {
					GPSSegment branchTraceFrom = new GPSSegment();
					tree.add(branchTraceFrom);
					branchTraceFrom.add(connFrom.getTo());
					previousBranch(tree, branchTraceFrom, connFrom.getFrom(), connFrom.getTo());
				}
			}
//			out = new ArrayList<AggNode>(node.getOut().iterator().next().getTo());
		}
			
		else if(node.getOut().size() > 1) {
			for(AggConnection conn : node.getOut()) {
				GPSSegment branchTrace = new GPSSegment();
				tree.add(branchTrace);
				branchTrace.add(conn.getFrom());
				continueBranch(tree, branchTrace, conn.getTo(), node);
				for(AggConnection connFrom : node.getIn()) {
					if(!connFrom.getFrom().equals(source)) {
						GPSSegment branchTraceFrom = new GPSSegment();
						tree.add(branchTraceFrom);
						branchTraceFrom.add(connFrom.getTo());
						previousBranch(tree, branchTraceFrom, connFrom.getFrom(), connFrom.getTo());
					}
				}
			}
		}
	}
	
//	public static void main(String[] args) {
//		Node a1 = new Node(1);
//		Node a2 = new Node(2);
//		Node a3 = new Node(3);
//		Node a4 = new Node(4);
//		Node a5 = new Node(5);
//		Node a6 = new Node(6);
//		Node a7 = new Node(7);
//		Node a8 = new Node(8);
//		Node a9 = new Node(9);
//		Node a10 = new Node(10);
//		Node a11 = new Node(11);
//		Node a12 = new Node(12);
//		Node a13 = new Node(13);
//		Node a14 = new Node(14);
//		Node a15 = new Node(15);
//		Node a16 = new Node(16);
//		Node a17 = new Node(17);
//		Node a18 = new Node(18);
//		Node a19 = new Node(19);
//		Node a20 = new Node(20);
//		
//		a1.children.add(a2);
//		a1.children.add(a8);
//		a1.children.add(a11);
//		a1.children.add(a12);
//		a2.children.add(a3);
//		a2.children.add(a5);
//		a2.children.add(a7);
//		a3.children.add(a4);
//		a5.children.add(a6);
//		a8.children.add(a9);
//		a8.children.add(a10);
//		a11.children.add(a13);
//		a11.children.add(a14);
//		a11.children.add(a15);
//		a13.children.add(a16);
//		a16.children.add(a17);
//		a17.children.add(a18);
//		a17.children.add(a19);
//		a14.children.add(a20);
//		
//		myResult = getSerialize(a1);
//		System.out.println(myResult);
//	}

}
