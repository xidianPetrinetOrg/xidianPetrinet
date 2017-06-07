/**
 * 
 */
package edu.xidian.petrinet.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import edu.xidian.petrinet.S2PR;
import edu.xidian.petrinet.S3PR;

/**
 * @author Administrator
 *
 */
public class S3PRTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR#S3PR(S2PR)}.
	 */
	//@Test
	public void testS3PR() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR(2,1,4);
		System.out.println("s2pr:" + s2pr);
		
		S3PR s3pr = new S3PR(s2pr);
		System.out.println("s3pr: " + s3pr);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR#S3PR(PTNet, String, Collection, Collection)}.
	 */
	//@Test
	public void testS3PR1() {
		///////////////////// ptnet1
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		ptnet1.addPlace("p7");
		ptnet1.addPlace("p8");
		
		// p0,pa
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");
		
		// pr
		ptnet1.addFlowRelationPT("p7", "t1");
		ptnet1.addFlowRelationTP("t2", "p7");
		ptnet1.addFlowRelationPT("p8", "t2");
		ptnet1.addFlowRelationTP("t3", "p8");
		
		Set<String> pa = new HashSet<String>();
		pa.add("p2"); pa.add("p3");
		Set<String> pr = new HashSet<String>();
		pr.add("p7"); pr.add("p8");
		
		///////////////////// ptnet2
		PTNet ptnet2 = new PTNet();
		ptnet2.addPlace("p4");
		ptnet2.addTransition("t4");
		ptnet2.addPlace("p5");
		ptnet2.addTransition("t5");
		ptnet2.addPlace("p6");
		ptnet2.addTransition("t6");
		ptnet2.addPlace("p7");
		ptnet2.addPlace("p8");
		
		// p0,pa
		ptnet2.addFlowRelationPT("p6", "t4");
		ptnet2.addFlowRelationTP("t4", "p4");
		ptnet2.addFlowRelationPT("p4", "t5");
		ptnet2.addFlowRelationTP("t5", "p5");
		ptnet2.addFlowRelationPT("p5", "t6");
		ptnet2.addFlowRelationTP("t6", "p6");
		
		// pr
		ptnet2.addFlowRelationPT("p7", "t5");
		ptnet2.addFlowRelationTP("t6", "p7");
		ptnet2.addFlowRelationPT("p8", "t4");
		ptnet2.addFlowRelationTP("t5", "p8");
		
		S3PR s3pr = new S3PR(ptnet1,"p1",pa,pr);
		System.out.println("s3pr: " + s3pr);
		
		pa.clear();
		pa.add("p4"); pa.add("p5");
		pr.clear();
		pr.add("p7"); pr.add("p8");
		s3pr.add(ptnet2,"p6",pa,pr);
		System.out.println("s3pr: " + s3pr);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR#getHr(Set)}.
	 */
	@Test
	public void getHrs() {
		///////////////////// ptnet1
		PTNet ptnet1 = new PTNet();
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		ptnet1.addPlace("p7");
		ptnet1.addPlace("p8");
		
		// p0,pa
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p1");
		
		// pr
		ptnet1.addFlowRelationPT("p7", "t1");
		ptnet1.addFlowRelationTP("t2", "p7");
		ptnet1.addFlowRelationPT("p8", "t2");
		ptnet1.addFlowRelationTP("t3", "p8");
		
		Set<String> pa = new HashSet<String>();
		pa.add("p2"); pa.add("p3");
		Set<String> pr = new HashSet<String>();
		pr.add("p7"); pr.add("p8");
		
		///////////////////// ptnet2
		PTNet ptnet2 = new PTNet();
		ptnet2.addPlace("p4");
		ptnet2.addTransition("t4");
		ptnet2.addPlace("p5");
		ptnet2.addTransition("t5");
		ptnet2.addPlace("p6");
		ptnet2.addTransition("t6");
		ptnet2.addPlace("p7");
		ptnet2.addPlace("p8");
		
		// p0,pa
		ptnet2.addFlowRelationPT("p6", "t4");
		ptnet2.addFlowRelationTP("t4", "p4");
		ptnet2.addFlowRelationPT("p4", "t5");
		ptnet2.addFlowRelationTP("t5", "p5");
		ptnet2.addFlowRelationPT("p5", "t6");
		ptnet2.addFlowRelationTP("t6", "p6");
		
		// pr
		ptnet2.addFlowRelationPT("p7", "t5");
		ptnet2.addFlowRelationTP("t6", "p7");
		ptnet2.addFlowRelationPT("p8", "t4");
		ptnet2.addFlowRelationTP("t5", "p8");
		
		S3PR s3pr = new S3PR(ptnet1,"p1",pa,pr);
		
		pa.clear();
		pa.add("p4"); pa.add("p5");
		pr.clear();
		pr.add("p7"); pr.add("p8");
		s3pr.add(ptnet2,"p6",pa,pr);
		System.out.println("s3pr: " + s3pr);
		
		//////////////////////////////////////////////////
		// SR: 信标S中的资源库所集合
		Set<PTPlace> SR = new HashSet<>();
		SR.add(s3pr.getPlace("p7"));
		SR.add(s3pr.getPlace("p8"));
		Collection<PTPlace> Hrs = s3pr.getHr(SR);
		System.out.println("Hrs: " + Hrs); // Hrs: [p4[p4], p5[p5], p3[p3], p2[p2]]
		assertEquals(4, Hrs.size());
		
		// Hrs = H(p7) ∪ H(p8)
		System.out.println("H(p7) = " + s3pr.getHr(s3pr.getPlace("p7"))); // H(p7) = [p5[p5], p2[p2]]
		System.out.println("H(p8) = " + s3pr.getHr(s3pr.getPlace("p8"))); // H(p8) = [p4[p4], p3[p3]]
		
		// 信标S
		Collection<PTPlace> S = new HashSet<>();
		S.add(s3pr.getPlace("p3"));  S.add(s3pr.getPlace("p5"));
		S.add(s3pr.getPlace("p7"));  S.add(s3pr.getPlace("p8"));
		// 信标补集[S] = Hrs \ S
		Collection<PTPlace> SS = new HashSet<>();
		SS.addAll(Hrs);
		SS.removeAll(S);
		System.out.println("信标补集：" + SS); // 信标补集：[p4[p4], p2[p2]]
		
		// 排序
		List<PTPlace> SS1 = new ArrayList<>(SS);
		Collections.sort(SS1, new Comparator<PTPlace>(){
			@Override
			public int compare(PTPlace o1, PTPlace o2) {
				String s1 = o1.getName();
				String s2 = o2.getName();
				return s1.compareTo(s2);
			}});
		System.out.println("排序后的信标补集：" + SS1); // 排序后的信标补集：[p2[p2], p4[p4]]
		
		// 直接调用函数，求信标S的补集
		System.out.println("信标补集：" + s3pr.getSiphonCom(SR, S)); // 信标补集：[p4[p4], p2[p2]]
	}	
}