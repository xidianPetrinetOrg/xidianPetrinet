package edu.xidian.petrinet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Sequence;

import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.attributes.Font;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.attributes.Font.Align;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.attributes.Position;
import de.invation.code.toval.misc.soabase.SOABase;
import de.invation.code.toval.thread.ExecutorListener;
import de.invation.code.toval.types.Multiset;
import de.invation.code.toval.validate.InconsistencyException;
import de.uni.freiburg.iig.telematik.sepia.exception.PNException;
import de.uni.freiburg.iig.telematik.sepia.exception.PNSoundnessException;
import de.uni.freiburg.iig.telematik.sepia.exception.PNValidationException;
import de.uni.freiburg.iig.telematik.sepia.graphic.GraphicalIFNet;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.AnnotationGraphics;
import de.uni.freiburg.iig.telematik.sepia.graphic.netgraphics.IFNetGraphics;
import de.uni.freiburg.iig.telematik.sepia.mg.pt.PTMarkingGraph;
import de.uni.freiburg.iig.telematik.sepia.petrinet.cpn.CPN;
import de.uni.freiburg.iig.telematik.sepia.petrinet.cpn.CPNMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.cpn.FiringRule;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.IFNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.IFNetFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.IFNetMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.RegularIFNetTransition;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.concepts.AccessMode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.concepts.AnalysisContext;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.concepts.Labeling;
import de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.concepts.SecurityLevel;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.boundedness.BoundednessCheck;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.boundedness.BoundednessCheckResult;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.boundedness.BoundednessException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.mg.MGConstruction;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.mg.MarkingGraphException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.sequences.MGTraversalResult;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.sequences.SequenceGeneration;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.sequences.SequenceGenerationCallableGenerator;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.sequences.SequenceGenerationException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTTransition;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.properties.soundness.PTNetSoundness;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.properties.validity.PTNetValidity;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.traverse.RandomPTTraverser;
import de.uni.freiburg.iig.telematik.sepia.petrinet.transform.PNTransformationFactory;
import de.uni.freiburg.iig.telematik.sepia.replay.Replay;
import de.uni.freiburg.iig.telematik.sepia.replay.ReplayCallable.TerminationCriteria;
import de.uni.freiburg.iig.telematik.sepia.replay.ReplayCallableGenerator;
import de.uni.freiburg.iig.telematik.sepia.replay.ReplayException;
import de.uni.freiburg.iig.telematik.sepia.replay.ReplayResult;
import de.uni.freiburg.iig.telematik.sepia.traversal.PNTraversalUtils;
import de.uni.freiburg.iig.telematik.sewol.accesscontrol.acl.ACLModel;
import de.uni.freiburg.iig.telematik.sewol.log.LogEntry;
import de.uni.freiburg.iig.telematik.sewol.log.LogTrace;
import de.uni.freiburg.iig.telematik.sewol.log.LogTraceUtils;

public class TestXidian {
	public static void main(String[] args) {
//		try {
//			test1();
//		} catch (PNException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// test2();
		// test3();
		test4();
		// test5();
		// test6();
		// test7();
		// test8();
	}
	
	// basic petri net
	// p1 -> t1 -> p2
	static void test1() throws PNException {
		PTNet ptnet = new PTNet();
		// add elements
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addTransition("t1");
		ptnet.addFlowRelationPT("p1", "t1"); // t1 -1-> p2
		ptnet.addFlowRelationTP("t1", "p2"); // p1 -1-> t1
		
		System.out.println(ptnet);
		
		System.out.println("============ Marking: tokens,InitialMaking");
		// Marking 
		PTMarking marking = new PTMarking();
		// Sets the state of the given place in the marking.
		marking.set("p1", 2); // tokens
		marking.set("p2", 1);
		ptnet.setInitialMarking(marking); // initial marking: p1[2] p2[1]
		
		System.out.println(ptnet); // initial marking: p1[2] p2[1]
		                           // actual marking: p1[2] p2[1]   
		
		System.out.println("============ t1 fire()");
		if(ptnet.getTransition("t1").isEnabled())
			ptnet.getTransition("t1").fire();
		
		System.out.println(ptnet); // actual marking: p1[1] p2[2]  
	}
	
	// P/T-Nets: Place/Trnsition Nets
	// p1 -> t1 -> p2 -> t2
	static void test2() {
		PTNet ptnet = new PTNet();
		// add elements
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addTransition("t1");
		ptnet.addTransition("t2");
		ptnet.addFlowRelationPT("p1", "t1",2); // p1 -2-> t1
		ptnet.addFlowRelationTP("t1", "p2",1); // t1 -1-> p2
		ptnet.addFlowRelationPT("p2", "t2",1); // p2 -1-> t2
		System.out.println(ptnet);
		
		PTMarking marking = new PTMarking();
		marking.set("p1", 2); // tokens
		ptnet.setInitialMarking(marking); // initial marking: p1[2] 
		                                  // actual marking: p1[2]
		
		System.out.println("before fire=============");
		System.out.println(ptnet);
		
		if (ptnet.getTransition("t1").isEnabled()) // Are there enough tokens in the input places?
		{
			try {
				ptnet.getTransition("t1").fire();
				System.out.println("t1 is eabled.");
			} catch (PNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("After fire=============");
		System.out.println(ptnet);  // actual marking: p2[1] 
		
		System.out.println("Check validity=============");
		// Check validity
		try {
			PTNetValidity.checkValidity(ptnet);
		} catch (PNValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("this P/T-Nets is invalid.");
		}
		
		System.out.println("Check soundness=============");
		// Check soundness
		try {
			PTNetSoundness.checkSoundness(ptnet,false);
		} catch (PNSoundnessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("this P/T-Nets is not sound.");
		}
		try {
			// 开启了新的线程
			BoundednessCheckResult<PTPlace, PTTransition, PTFlowRelation, PTMarking, Integer> boundedness = BoundednessCheck.getBoundedness(ptnet);
			// 或
			// BoundednessCheckResult boundedness = BoundednessCheck.getBoundedness(ptnet);
			System.out.println(boundedness);  // BOUNDED
		} catch (BoundednessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Number of outgoing relations from the place:");
		// Number of outgoing relations from the place
		System.out.println(ptnet.getPlace("p1").outDegree()); // 1
		System.out.println("Are there enough tokens in the input places?");
		// Are there enough tokens in the input places?
		System.out.println(ptnet.getTransition("t1").isEnabled()); // false
		
		// Check(Observer Pattern),开启新的线程计算，监听结果
		System.out.println("=== test2 Check(Observer Pattern) ====");
		try {
			BoundednessCheck.initiateBoundednessCheck(ptnet, 
					new ExecutorListener<BoundednessCheckResult<PTPlace,PTTransition,
					PTFlowRelation,PTMarking,Integer>>() {
				@Override
				public void executorStarted() {
					System.out.println("test 2 Observer started...");
				}
							
				@Override
				public void executorFinished(BoundednessCheckResult<
						PTPlace, PTTransition, PTFlowRelation, PTMarking, Integer> result) {
					System.out.println("test 2 Observer: " + result.getBoundedness()); // BOUNDED
				}

				@Override
				public void executorStopped() {
					// TODO Auto-generated method stub
					System.out.println("test 2 Observer stoped!");
				}

				@Override
				public void executorException(Exception exception) {
					// TODO Auto-generated method stub
					System.out.println("test 2 Observer exception!");
				}

				@Override
				public void progress(double progress) {
					// TODO Auto-generated method stub
					
				}
				
			});
		} catch (BoundednessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Petri Net Traversal
	static void test3() {
		PTNet ptnet = new PTNet();
		
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addTransition("t1");
		ptnet.addTransition("t2");
		ptnet.addTransition("t3");
		ptnet.addFlowRelationPT("p1", "t1",2);
		ptnet.addFlowRelationTP("t1", "p2",1);
		ptnet.addFlowRelationPT("p2", "t3",1);
		ptnet.addFlowRelationPT("p1", "t2",1);
		ptnet.addFlowRelationTP("t2", "p2",1);
		
		PTMarking marking = new PTMarking();
		marking.set("p1", 2);
		ptnet.setInitialMarking(marking);
		System.out.println(ptnet);
		
		System.out.println("===Petri Net Traversal====");
		RandomPTTraverser t = new RandomPTTraverser(ptnet);
		for (int i = 1; ptnet.hasEnabledTransitions(); i++) {
			System.out.println(i + ": " + ptnet.getEnabledTransitions().size()); // 1: 2
			                                                                     // 2: 1
			try {
				PTTransition tran = t.chooseNextTransition(ptnet.getEnabledTransitions());
				System.out.println("chooseNextTransition: " + tran);
				// t.chooseNextTransition(ptnet.getEnabledTransitions()).fire();
				tran.fire();
			} catch (InconsistencyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ptnet);
		}
		System.out.println("no more enabled transitions");
		
		// simulates the Petri net ptnet 10 times and prints out all distinct traces. Thereby it
		// limits the number of activities per sequence to 100.
		PNTraversalUtils.testTraces(ptnet, 10, 100, true,false,false);
	}
	
	// Reachability
	static void test4() {
		PTNet ptnet = new PTNet();
		
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addTransition("t1");
		ptnet.addTransition("t2");
		ptnet.addTransition("t3");
		ptnet.addFlowRelationPT("p1", "t1",2);
		ptnet.addFlowRelationTP("t1", "p2",1);
		ptnet.addFlowRelationPT("p2", "t3",1);
		ptnet.addFlowRelationPT("p1", "t2",1);
		ptnet.addFlowRelationTP("t2", "p2",1);
		
		PTMarking marking = new PTMarking();
		marking.set("p1", 2);
		ptnet.setInitialMarking(marking);
		System.out.println(ptnet);
        
		// 不知如何使用mg
		try {
			PTMarkingGraph mg = (PTMarkingGraph) MGConstruction.buildMarkingGraph(ptnet);
			System.out.println("PTMarkingGraph");
			System.out.println(mg);
		} catch (MarkingGraphException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		SequenceGenerationCallableGenerator generator =
				new SequenceGenerationCallableGenerator<>(ptnet);
		try {
			MGTraversalResult res = SequenceGeneration.getFiringSequences(generator);
			//res.getCompleteSequences();
			System.out.println("Complete: " + res.getCompleteSequences());
			System.out.println("Sequences: " + res.getSequences());
		} catch (SequenceGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// Replaying
	static void test5() {
        PTNet ptnet = new PTNet();
		
		ptnet.addPlace("p1");
		ptnet.addPlace("p2");
		ptnet.addTransition("t1");
		ptnet.addTransition("t2");
		ptnet.addTransition("t3");
		ptnet.addFlowRelationPT("p1", "t1",2);
		ptnet.addFlowRelationTP("t1", "p2",1);
		ptnet.addFlowRelationPT("p2", "t3",1);
		ptnet.addFlowRelationPT("p1", "t2",1);
		ptnet.addFlowRelationTP("t2", "p2",1);
		
		PTMarking marking = new PTMarking();
		marking.set("p1", 2);
		ptnet.setInitialMarking(marking);
		System.out.println(ptnet);
		/**
		 * The following code creates a log for the formerly defined P/T-Net. The traces 1, 2,
and 3 are complete sequences on the net, trace 6 is incomplete, and traces 4 and 5 are
non-fitting traces for the given Petri net.
		 */
		List<LogTrace<LogEntry>> log = new ArrayList<LogTrace<LogEntry>>();
		log.add(LogTraceUtils.createTraceFromActivities(1, "t1","t3"));
		log.add(LogTraceUtils.createTraceFromActivities(2, "t2","t3","t2","t3"));
		log.add(LogTraceUtils.createTraceFromActivities(3, "t2","t2","t3","t3"));
		log.add(LogTraceUtils.createTraceFromActivities(4, "t2","t1","t2","t3"));
		log.add(LogTraceUtils.createTraceFromActivities(5, "t1","t2","t3"));
		log.add(LogTraceUtils.createTraceFromActivities(6, "t2","t2","t3"));
		// replays the log on the P/T-Net with the termination criterion possible firing sequence:
		ReplayCallableGenerator gen = new ReplayCallableGenerator(ptnet);
		gen.setLogTraces(log);
		gen.setTerminationCriteria(TerminationCriteria.POSSIBLE_FIRING_SEQUENCE);
		try {
			ReplayResult<LogEntry> result = Replay.replayTraces(gen);
			System.out.println(result.getFittingTraces());
			System.out.println(result.getNonFittingSequences());
		} catch (ReplayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	// CPNs: Colored Petri Nets
	static void test6() {
		CPN cpn = new CPN();
		cpn.addPlace("p1");
		cpn.addPlace("p2");
		cpn.addTransition("t1");
		cpn.addTransition("t2");
		cpn.addFlowRelationPT("p1", "t1");
		cpn.addFlowRelationTP("t1", "p2");
		
		Multiset<String> c1 = new Multiset<String>();
		c1.add("blue");
		cpn.addFlowRelationPT("p2", "t2", c1);
		FiringRule frT1 = new FiringRule();
		frT1.addRequirement("p1", "black", 2);
		frT1.addProduction("p2", "blue", 1);
		cpn.addFiringRule("t1", frT1);
		
		CPNMarking cpnmarking = new CPNMarking();
		cpnmarking.set("p1", new Multiset<String>("black", "black"));
		cpn.setInitialMarking(cpnmarking);
		
		System.out.println(cpn);
	}
	
	// IF-Nets: Information Flow Nets
	static void test7() {
		IFNet ifnet = new IFNet();
		ifnet.addPlace("pIn");
		ifnet.addPlace("p1");
		ifnet.addPlace("p2");
		ifnet.addPlace("pOut");
		ifnet.addTransition("tIn");
		ifnet.addDeclassificationTransition("td");
		ifnet.addTransition("tOut");
		IFNetFlowRelation f1 = ifnet.addFlowRelationPT("pIn", "tIn");
		IFNetFlowRelation f2 = ifnet.addFlowRelationTP("tIn", "p1");
		IFNetFlowRelation f3 = ifnet.addFlowRelationPT("p1", "td");
		IFNetFlowRelation f4 = ifnet.addFlowRelationTP("td", "p2");
		IFNetFlowRelation f5 = ifnet.addFlowRelationPT("p2", "tOut");
		IFNetFlowRelation f6 = ifnet.addFlowRelationTP("tOut", "pOut");
		
		f2.addConstraint("red", 1);
		f3.addConstraint("red", 1);
		f4.addConstraint("green", 1);
		f5.addConstraint("green", 1);
		ifnet.getPlace("p2").setColorCapacity("green", 1);
		
		// initial marking
		IFNetMarking sm = new IFNetMarking();
		sm.set("pIn", new Multiset<String>("black"));
		ifnet.setInitialMarking(sm);
		
		System.out.println(ifnet);
		
		RegularIFNetTransition tIn = (RegularIFNetTransition) ifnet.getTransition("tIn");
		tIn.addAccessMode("red", AccessMode.CREATE);
		
		//The SOA base contains all names of subjects, objects, and activities:
		SOABase context = new SOABase("DataUsage");
		context.setSubjects(Arrays.asList("sh1", "sh2", "sl0"));
		context.setObjects(Arrays.asList("red", "green", "black"));
		context.setActivities(Arrays.asList("tIn", "td", "tOut"));
		
		// access control models
		ACLModel acl = new ACLModel("ACL", context);
		acl.addActivityPermission("sh1", "tIn");
		acl.addActivityPermission("sh2", "td");
		acl.addActivityPermission("sl0", "tOut");
		
		AnalysisContext ac = new AnalysisContext("ac", acl, true);
		// Assign subjects to transitions
		ac.setSubjectDescriptor("tIn", "sh1");
		ac.setSubjectDescriptor("td", "sh2");
		ac.setSubjectDescriptor("tOut", "sl0");
		
		Labeling l = ac.getLabeling();
		// Set subject clearance
		l.setSubjectClearance("sh1", SecurityLevel.HIGH);
		l.setSubjectClearance("sh2", SecurityLevel.HIGH);
		l.setSubjectClearance("sl0", SecurityLevel.LOW);
		// set transition classification
		l.setActivityClassification("tIn", SecurityLevel.HIGH);
		l.setActivityClassification("td", SecurityLevel.HIGH);
		l.setActivityClassification("tOut", SecurityLevel.LOW);
		// set token color classification
		l.setAttributeClassification("red", SecurityLevel.HIGH);
		l.setAttributeClassification("green", SecurityLevel.LOW);
		
		ifnet.setAnalysisContext(ac);
		
		//System.out.println(ifnet);
	}
	// Petri Net Graphics
	static void test8() {
		IFNet ifnet = new IFNet();
		ifnet.addPlace("pIn");
		ifnet.addPlace("p1");
		ifnet.addPlace("p2");
		ifnet.addPlace("pOut");
		ifnet.addTransition("tIn");
		ifnet.addDeclassificationTransition("td");
		ifnet.addTransition("tOut");
		IFNetFlowRelation f1 = ifnet.addFlowRelationPT("pIn", "tIn");
		IFNetFlowRelation f2 = ifnet.addFlowRelationTP("tIn", "p1");
		IFNetFlowRelation f3 = ifnet.addFlowRelationPT("p1", "td");
		IFNetFlowRelation f4 = ifnet.addFlowRelationTP("td", "p2");
		IFNetFlowRelation f5 = ifnet.addFlowRelationPT("p2", "tOut");
		IFNetFlowRelation f6 = ifnet.addFlowRelationTP("tOut", "pOut");
		
		f2.addConstraint("red", 1);
		f3.addConstraint("red", 1);
		f4.addConstraint("green", 1);
		f5.addConstraint("green", 1);
		ifnet.getPlace("p2").setColorCapacity("green", 1);
		
		// initial marking
		IFNetMarking sm = new IFNetMarking();
		sm.set("pIn", new Multiset<String>("black"));
		ifnet.setInitialMarking(sm);
		
		System.out.println(ifnet);
		
		IFNetGraphics ifNetG = new IFNetGraphics();
		GraphicalIFNet gIFNet = new GraphicalIFNet(ifnet, ifNetG);
		Map<String, Color> colors = new HashMap<String, Color>();
		colors.put("black", Color.BLACK);
		colors.put("red", Color.RED);
		colors.put("green", Color.GREEN);
		ifNetG.setColors(colors);
		ifNetG.setClearancesPosition(new Position(20, 30));
		ifNetG.setTokenLabelsPosition(new Position(40, 30));
		
		Font annotationFont = new Font();
		annotationFont.setAlign(Align.RIGHT);
		AnnotationGraphics arcAnnotation = new AnnotationGraphics();
		arcAnnotation.setFont(annotationFont);
		ifNetG.getArcAnnotationGraphics().put(f1.getName(), arcAnnotation);
		ifNetG.getArcAnnotationGraphics().put(f2.getName(), arcAnnotation);
		ifNetG.getArcAnnotationGraphics().put(f3.getName(), arcAnnotation);
		ifNetG.getArcAnnotationGraphics().put(f4.getName(), arcAnnotation);
		ifNetG.getArcAnnotationGraphics().put(f5.getName(), arcAnnotation);
		ifNetG.getArcAnnotationGraphics().put(f6.getName(), arcAnnotation);
		
		System.out.println(ifNetG);
	}
}
