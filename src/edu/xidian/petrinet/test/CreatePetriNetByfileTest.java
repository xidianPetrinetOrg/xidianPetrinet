package edu.xidian.petrinet.test;

import edu.xidian.petrinet.S3PR.S3PR;
import edu.xidian.petrinet.createnet.CreatePetriNetByFile;

public class CreatePetriNetByfileTest {
	public static void main(String[] args) {
		CreatePetriNetByFile createPetriNetByFile = new CreatePetriNetByFile();
		
		//path = C:\Users\xd\Desktop\txt1.pnt
		createPetriNetByFile.CreatePetriNetByPnt("C:\\Users\\xd\\Desktop\\txt1.pnt");
		
		createPetriNetByFile.CreatePetriNetBytxt("C:\\Users\\xd\\Desktop\\ResourceRelationFile.txt");
		//createPetriNetByFile.CreatePetriNetBytxt("C:\\Users\\xd\\Desktop\\ResourceRelationFile1.txt");
		S3PR createS3PR = createPetriNetByFile.CreateS3PR();
		createPetriNetByFile.CreateResourceGraphByResourceRelation(createS3PR);	
	}
}
