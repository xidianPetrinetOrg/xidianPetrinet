package edu.xidian.petrinet;

import java.util.ArrayList;
import java.util.List;

import de.invation.code.toval.misc.CollectionUtils;
import de.invation.code.toval.time.TimeScale;
import de.invation.code.toval.time.TimeValue;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPetriNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.replay.Replay;
import de.uni.freiburg.iig.telematik.sepia.replay.ReplayCallable.TerminationCriteria;
import de.uni.freiburg.iig.telematik.sepia.replay.ReplayCallableGenerator;
import de.uni.freiburg.iig.telematik.sepia.replay.ReplayResult;
import de.uni.freiburg.iig.telematik.sewol.log.LogEntry;
import de.uni.freiburg.iig.telematik.sewol.log.LogTrace;
import de.uni.freiburg.iig.telematik.sewol.log.LogTraceUtils;

/**
 * 追踪变迁序列：
 * complete，incomplete，non-fitting
 * @author ZhiwuLi
 *
 */
public class ReplayingTest {
	public static ReplayResult<LogEntry> replay(AbstractPetriNet<?, ?, ?, ?, ?> net, List<LogTrace<LogEntry>> traces, TerminationCriteria terminationCriteria) throws Exception{
		final String doneReplayformat = "done [fitting=%s, not fitting=%s] [%s]";
		System.out.print("Replaying log on model \""+net.getName()+"\"... ");
		ReplayCallableGenerator<?,?,?,?,?,LogEntry> gen = new ReplayCallableGenerator<>(net);
		gen.setLogTraces(traces);
		gen.setTerminationCriteria(terminationCriteria);
		long start = System.currentTimeMillis();
		
		ReplayResult<LogEntry> result = Replay.replayTraces(gen);
		
		TimeValue runtime = new TimeValue(System.currentTimeMillis() - start, TimeScale.MILLISECONDS);
		runtime.adjustScale();
		
		System.out.println(String.format(doneReplayformat, result.portionFitting(), result.portionNonFitting(), runtime));
		return result;
	}
	
	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();       // states: 6
		System.out.println(ptnet);
		
		/**
		 * The following code creates a log for the formerly defined P/T-Net. The traces 1, 2,
and 3 are complete sequences on the net, trace 6 is incomplete, and traces 4 and 5 are
non-fitting traces for the given Petri net.
		 */
		List<LogTrace<LogEntry>> log = new ArrayList<LogTrace<LogEntry>>();
		log.add(LogTraceUtils.createTraceFromActivities(1, "t1","t3"));  // complete
		log.add(LogTraceUtils.createTraceFromActivities(2, "t2","t3","t2","t3")); // complete
		log.add(LogTraceUtils.createTraceFromActivities(3, "t2","t2","t3","t3")); // complete
		log.add(LogTraceUtils.createTraceFromActivities(4, "t2","t1","t2","t3")); // non-fitting
		log.add(LogTraceUtils.createTraceFromActivities(5, "t1","t2","t3"));      // non-fitting
		log.add(LogTraceUtils.createTraceFromActivities(6, "t2","t2","t3"));      // incomplete
		
		try {
			ReplayResult<LogEntry> result = replay(ptnet,log,TerminationCriteria.POSSIBLE_FIRING_SEQUENCE);
			System.out.println("result:\n" + result.getCount() + "," + result.getNumTraces() + "," + result.getNumSequences());
			System.out.println("portionFitting: " + result.portionFitting());
			System.out.println("portionNonFitting: " + result.portionNonFitting());
			System.out.println("FittingTraces:\n" + result.getFittingTraces());
			CollectionUtils.print(result.getFittingTraces());
			
			System.out.println("FittingSequence:\n" + result.getFittingSequences());
			CollectionUtils.print(result.getFittingSequences());
			
			System.out.println("NonFittingTraces:\n" + result.getNonFittingTraces());
			CollectionUtils.print(result.getNonFittingTraces());
			
			System.out.println("NonFittingSequence:\n" + result.getNonFittingSequences());
			CollectionUtils.print(result.getNonFittingSequences());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
