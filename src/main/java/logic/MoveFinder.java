package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.Board;
import model.Field;
import model.Move;

public class MoveFinder {
	
	private int number_of_threads;
	private ExecutorService executor;

	public MoveFinder(int number_of_threads) {
		this.number_of_threads = number_of_threads;
		executor = Executors.newFixedThreadPool(number_of_threads);
	}
	
	public ArrayList<Move> getParallelMoves(Board board){
		ArrayList<Move> results = new ArrayList<Move>();
		
		int chunk = board.getHashedFields().length/number_of_threads;
		
		ArrayList<MoveSearcher> taskList = new ArrayList<MoveSearcher>();
		
		for(int i = 0; i < number_of_threads; i++) {
			MoveSearcher search = new MoveSearcher(board, i * chunk, (i + 1) * chunk);
			taskList.add(search);
		}
		long time = System.currentTimeMillis();
		try {
			List<Future<ArrayList<Move>>> futures = executor.invokeAll(taskList);
				for(Future<ArrayList<Move>> future : futures) {
					results.addAll(future.get());
				}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("\nParallel Comp: " + (System.currentTimeMillis() - time));
		System.out.println("Returned " + results.size());
		return results;
	}
	
	private class MoveSearcher implements Callable<ArrayList<Move>>{
		
		private int low_range;
		private int high_range;
		private Board board;
		
		public MoveSearcher(Board board, int low_range, int high_range) {
			this.board = board;
			this.low_range = low_range;
			this.high_range = high_range;
		}
		
		@Override
		public ArrayList<Move> call() throws Exception {
			ArrayList<Field> top = board.getFieldRange(low_range, high_range);
			
			ArrayList<Move> options = new ArrayList<Move>();
			
			for(int i = 0; i < top.size(); i++) {
				if(top.get(i).getValue() != 0)
					continue;
				for(Field field : board.getFields().values()) {
					if(field.getValue() != 0)
						continue;
					options.add(new Move(top.get(i), field));
				}
			}
			System.out.print("T:" + low_range + "-" + high_range + " C| ");
			return options;
		}
	}

}
