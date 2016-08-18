package edu.xidian.petrinet;

import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.sequences.MGTraversalResult;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.sequences.SequenceGeneration;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.sequences.SequenceGenerationCallableGenerator;
import de.uni.freiburg.iig.telematik.sepia.petrinet.properties.sequences.SequenceGenerationException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;

public class SequenceTest {

	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();       // states: 6
		SequenceGenerationCallableGenerator generator =
				new SequenceGenerationCallableGenerator<>(ptnet);
		try {
			MGTraversalResult res = SequenceGeneration.getFiringSequences(generator);
			System.out.println("Complete: " + res.getCompleteSequences());
			System.out.println("Sequences: " + res.getSequences());
		} catch (SequenceGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
