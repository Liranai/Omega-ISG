package logic.ai;

import java.util.concurrent.Callable;
import logic.OmegaLogic;
import model.Board;
import model.Move;
import model.Node;

public class AlphaBetaMiniMax extends ArtificialIntelligence implements Callable<Move> {

	private int nodes_explored;
	private static double MAX_DEPTH = 0.2;
	
	public AlphaBetaMiniMax(int number) {
		super(number);
		nodes_explored = 0;
	}

	@Override
	public String getName() {
		return "AlphaBetaMiniMax";
	}

	@Override
	public Move getMove(Board board) {
		long intime = System.currentTimeMillis();
		System.out.println("Getting ABMM move");
		
		Node root = new Node(board.clone(), null, null);
		Node best = alphaBeta(root, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, true);
		
		while(!best.getParent().equals(root)) {
			best = best.getParent();
		}
		
		MAX_DEPTH += 0.15;
		
		System.out.println("Time taken: " + (System.currentTimeMillis() - intime) + "ms");
		return best.getMove();
	}

	@Override
	public Move call() throws Exception {
		
		return null;
	}

	public Node alphaBeta(Node node, int depth, int alpha, int beta, boolean min_max) {
//		if (node.isTerminal()) {
//			if(min_max) 
//				return -1;
//			return 1;
//		}
		node.calculateValue(min_max ? super.playerNumber : super.playerNumber+1);
		if (depth >= MAX_DEPTH) {
			return node;
		} else {
			Node maxNode = null;
			long time = System.currentTimeMillis();
			for (Move move : OmegaLogic.getLegalMoves(node.getBoard())) {
				Node newNode = alphaBeta(new Node(node.getBoard().clone(), move, node), depth + 1, alpha, beta, !min_max);
				if (maxNode == null || newNode.getValue() > maxNode.getValue()) {
					maxNode = newNode;
				}
			}
			System.out.println("DEPTH: " + depth + " took " + (System.currentTimeMillis() - time) + "ms");
			return maxNode;
		}
		
		
		
//		function alphabeta(node, depth, α, β, maximizingPlayer) is
//	    if depth = 0 or node is a terminal node then
//	        return the heuristic value of node
//	    if maximizingPlayer then
//	        value := −∞
//	        for each child of node do
//	            value := max(value, alphabeta(child, depth − 1, α, β, FALSE))
//	            α := max(α, value)
//	            if α ≥ β then
//	                break (* β cut-off *)
//	        return value
//	    else
//	        value := +∞
//	        for each child of node do
//	            value := min(value, alphabeta(child, depth − 1, α, β, TRUE))
//	            β := min(β, value)
//	            if α ≥ β then
//	                break (* α cut-off *)
//	        return value
		
	}

}
//	public Integer alphaBeta(Node2 node, int depth, int alpha, int beta, boolean min_max) {
//		nodes_explored++;
//		// TODO: Optional, output
//
//		if (node.isTerminal()) {
//			if (min_max)
//				return -1;
//			return 1;
//		} else {
//			if (previousNodes.containsKey(depth)) {
//				for (Plane2 plane : node.getState().getPlanes().values()) {
//					plane.setClusters(Cluster2.clusterPlane(plane, node.getState()));
//				}
//				for (Node2 previous : previousNodes.get(depth)) {
//					if (previous.getState().isEqualTo(node.getState()))
//						return (min_max ? Integer.MAX_VALUE : Integer.MIN_VALUE);
//				}
//			} else {
//				previousNodes.put(depth, new ArrayList<Node2>());
//			}
//
//			previousNodes.get(depth).add(node);
//
//			if (min_max) {
//				int value = Integer.MIN_VALUE;
//				Node2 child = null;
//
//				searchloop: for (Plane2 plane : node.getState().getPlanes().values()) {
//					plane.setClusters(Cluster2.clusterPlane(plane, node.getState()));
//					for (Cluster2 cluster : plane.getClusters()) {
//						cluster.analyseCluster(plane, node.getState());
//						cluster.setClusterComplexForm(cluster.searchCluster(plane, node.getState()));
//					}
//					ArrayList<String> clusterTypes = plane.getClusterTypes();
//					for (int i = 0; i < clusterTypes.size(); i++) {
//
//						Cluster2 c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
//						for (int j = i; j < clusterTypes.size(); j++) {
//							Cluster2 c2 = null;
//							if (clusterTypes.get(i).equals(clusterTypes.get(j))) {
//								ArrayList<Cluster2> secundairClusters = plane
//										.getClustersOfStringExcluding(clusterTypes.get(j), c1);
//								if (!secundairClusters.isEmpty()) {
//									c2 = secundairClusters.get(0);
//								}
//							} else {
//								ArrayList<Cluster2> secundairClusters = plane.getClustersOfString(clusterTypes.get(j));
//								if (!secundairClusters.isEmpty()) {
//									c2 = secundairClusters.get(0);
//								}
//							}
//							if (c2 != null) {
//								for (Vertex2 v1 : c1.getVertices().values()) {
//									if (v1.getDegree() > 2)
//										continue;
//									for (Vertex2 v2 : c2.getVertices().values()) {
//										if (v2.getDegree() > 2)
//											continue;
//										State2 childState = SproutsGameSolver2.modifyStateInPlane(node.getState(),
//												plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID());
//										child = new Node2(childState, node);
//										value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//										alpha = Math.max(alpha, value);
//										if (beta <= alpha)
//											break searchloop;
//										else
//											node.addChild(child);
//									}
//								}
//							}
//						}
//
//						c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
//						for (Vertex2 v1 : c1.getVertices().values()) {
//							if (v1.getDegree() > 1) {
//								continue;
//							}
//							ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
//							for (Cluster2 c : plane.getClusters()) {
//								if (c.equals(c1))
//									continue;
//								clusters.add(c);
//							}
//							if (clusters.isEmpty()) {
//								State2 childState = SproutsGameSolver2.nooseModification(node.getState(),
//										plane.getUniqueID(), v1.getUniqueID(), null);
//								child = new Node2(childState, node);
//								value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//								alpha = Math.max(alpha, value);
//								if (beta <= alpha)
//									break searchloop;
//								else
//									node.addChild(child);
//							}
//							for (int n = 0; n < clusters.size() * 2; n++) {
//								String binary = Integer.toBinaryString(n);
//								ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
//								for (int j = 0; j < binary.length(); j++) {
//									if (binary.charAt(j) == '1')
//										clusterSublist.add(clusters.get(j));
//								}
//								State2 childState = SproutsGameSolver2.nooseModification(node.getState(),
//										plane.getUniqueID(), v1.getUniqueID(), clusterSublist);
//								child = new Node2(childState, node);
//								value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//								alpha = Math.max(alpha, value);
//								if (beta <= alpha)
//									break searchloop;
//								else
//									node.addChild(child);
//							}
//						}
//
//						c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
//						for (Vertex2 v1 : c1.getVertices().values()) {
//							if (v1.getDegree() > 2) {
//								continue;
//							}
//							ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
//							for (Cluster2 c : plane.getClusters()) {
//								if (c.equals(c1))
//									continue;
//								clusters.add(c);
//							}
//							ArrayList<Vertex2> candidateVertices = new ArrayList<Vertex2>();
//							for (Vertex2 v : c1.getVertices().values()) {
//								if (v.equals(v1))
//									continue;
//								if (v.getDegree() > 2)
//									continue;
//								if (v.getUniqueID() <= v1.getUniqueID())
//									continue;
//								candidateVertices.add(v);
//							}
//							if (clusters.isEmpty()) {
//								for (Vertex2 v2 : candidateVertices) {
//									State2 childState = SproutsGameSolver2.loopModification(node.getState(),
//											plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), null,
//											c1.getVertices());
//									child = new Node2(childState, node);
//									value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//									alpha = Math.max(alpha, value);
//									if (beta <= alpha)
//										break searchloop;
//									else
//										node.addChild(child);
//								}
//							}
//							for (int n = 0; n < clusters.size() * 2; n++) {
//								String binary = Integer.toBinaryString(n);
//								ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
//								for (int j = 0; j < binary.length(); j++) {
//									if (binary.charAt(j) == '1')
//										clusterSublist.add(clusters.get(j));
//								}
//								for (Vertex2 v2 : candidateVertices) {
//									State2 childState = SproutsGameSolver2.loopModification(node.getState(),
//											plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), clusterSublist,
//											c1.getVertices());
//									child = new Node2(childState, node);
//									value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//									alpha = Math.max(alpha, value);
//									if (beta <= alpha)
//										break searchloop;
//									else
//										node.addChild(child);
//								}
//							}
//						}
//					}
//				}
//				return value;
//			} else {
//				int value = Integer.MAX_VALUE;
//				Node2 child = null;
//
//				searchloop: for (Plane2 plane : node.getState().getPlanes().values()) {
//					plane.setClusters(Cluster2.clusterPlane(plane, node.getState()));
//					for (Cluster2 cluster : plane.getClusters()) {
//						cluster.analyseCluster(plane, node.getState());
//						cluster.setClusterComplexForm(cluster.searchCluster(plane, node.getState()));
//					}
//					ArrayList<String> clusterTypes = plane.getClusterTypes();
//					for (int i = 0; i < clusterTypes.size(); i++) {
//
//						Cluster2 c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
//						for (int j = i; j < clusterTypes.size(); j++) {
//							Cluster2 c2 = null;
//							if (clusterTypes.get(i).equals(clusterTypes.get(j))) {
//								ArrayList<Cluster2> secundairClusters = plane
//										.getClustersOfStringExcluding(clusterTypes.get(j), c1);
//								if (!secundairClusters.isEmpty()) {
//									c2 = secundairClusters.get(0);
//								}
//							} else {
//								ArrayList<Cluster2> secundairClusters = plane.getClustersOfString(clusterTypes.get(j));
//								if (!secundairClusters.isEmpty()) {
//									c2 = secundairClusters.get(0);
//								}
//							}
//							if (c2 != null) {
//								for (Vertex2 v1 : c1.getVertices().values()) {
//									if (v1.getDegree() > 2)
//										continue;
//									for (Vertex2 v2 : c2.getVertices().values()) {
//										if (v2.getDegree() > 2)
//											continue;
//										State2 childState = SproutsGameSolver2.modifyStateInPlane(node.getState(),
//												plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID());
//										child = new Node2(childState, node);
//										value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//										beta = Math.min(beta, value);
//										if (beta <= alpha)
//											break searchloop;
//										else
//											node.addChild(child);
//									}
//								}
//							}
//						}
//
//						c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
//						for (Vertex2 v1 : c1.getVertices().values()) {
//							if (v1.getDegree() > 1) {
//								continue;
//							}
//							ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
//							for (Cluster2 c : plane.getClusters()) {
//								if (c.equals(c1))
//									continue;
//								clusters.add(c);
//							}
//							if (clusters.isEmpty()) {
//								State2 childState = SproutsGameSolver2.nooseModification(node.getState(),
//										plane.getUniqueID(), v1.getUniqueID(), null);
//								child = new Node2(childState, node);
//								value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//								beta = Math.min(beta, value);
//								if (beta <= alpha)
//									break searchloop;
//								else
//									node.addChild(child);
//							}
//							for (int n = 0; n < clusters.size() * 2; n++) {
//								String binary = Integer.toBinaryString(n);
//								ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
//								for (int j = 0; j < binary.length(); j++) {
//									if (binary.charAt(j) == '1')
//										clusterSublist.add(clusters.get(j));
//								}
//								State2 childState = SproutsGameSolver2.nooseModification(node.getState(),
//										plane.getUniqueID(), v1.getUniqueID(), clusterSublist);
//								child = new Node2(childState, node);
//								value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//								beta = Math.min(beta, value);
//								if (beta <= alpha)
//									break searchloop;
//								else
//									node.addChild(child);
//							}
//						}
//
//						c1 = plane.getClustersOfString(clusterTypes.get(i)).get(0);
//						for (Vertex2 v1 : c1.getVertices().values()) {
//							if (v1.getDegree() > 2) {
//								continue;
//							}
//							ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
//							for (Cluster2 c : plane.getClusters()) {
//								if (c.equals(c1))
//									continue;
//								clusters.add(c);
//							}
//							ArrayList<Vertex2> candidateVertices = new ArrayList<Vertex2>();
//							for (Vertex2 v : c1.getVertices().values()) {
//								if (v.equals(v1))
//									continue;
//								if (v.getDegree() > 2)
//									continue;
//								if (v.getUniqueID() <= v1.getUniqueID())
//									continue;
//								candidateVertices.add(v);
//							}
//							if (clusters.isEmpty()) {
//								for (Vertex2 v2 : candidateVertices) {
//									State2 childState = SproutsGameSolver2.loopModification(node.getState(),
//											plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), null,
//											c1.getVertices());
//									child = new Node2(childState, node);
//									value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//									beta = Math.min(beta, value);
//									if (beta <= alpha)
//										break searchloop;
//									else
//										node.addChild(child);
//								}
//							}
//							for (int n = 0; n < clusters.size() * 2; n++) {
//								String binary = Integer.toBinaryString(n);
//								ArrayList<Cluster2> clusterSublist = new ArrayList<Cluster2>();
//								for (int j = 0; j < binary.length(); j++) {
//									if (binary.charAt(j) == '1')
//										clusterSublist.add(clusters.get(j));
//								}
//								for (Vertex2 v2 : candidateVertices) {
//									State2 childState = SproutsGameSolver2.loopModification(node.getState(),
//											plane.getUniqueID(), v1.getUniqueID(), v2.getUniqueID(), clusterSublist,
//											c1.getVertices());
//									child = new Node2(childState, node);
//									value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//									beta = Math.min(beta, value);
//									if (beta <= alpha)
//										break searchloop;
//									else
//										node.addChild(child);
//								}
//							}
//						}
//					}
//				}
//				return value;
//			}
//		}
//	}
//}
