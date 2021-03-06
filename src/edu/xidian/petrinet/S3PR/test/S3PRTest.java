/**
 * 
 */
package edu.xidian.petrinet.S3PR.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.invation.code.toval.time.TimeScale;
import de.invation.code.toval.time.TimeValue;
import de.uni.freiburg.iig.telematik.jagal.graph.Graph;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.EdgeNotFoundException;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.VertexNotFoundException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import edu.xidian.math.InvariantMatrix;
import edu.xidian.petrinet.S3PR.S2PR;
import edu.xidian.petrinet.S3PR.S3PR;
import edu.xidian.petrinet.S3PR.RGraph.RGraph;

/**
 * @author Administrator
 *
 */
public class S3PRTest {
	 long start; // 开始时间

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
		System.out.println("setUp()....");
		// 记录开始时间
		start = System.currentTimeMillis();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown()....");
		TimeValue runtime = new TimeValue(System.currentTimeMillis() - start, TimeScale.MILLISECONDS);
		runtime.adjustScale();
		
		System.out.println(runtime + " done.");
	}
	
	/*******************************************
	 * Li. 图3-1或图4-2
	 ******************************************/
	public S3PR Li_Figure3_1() {
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
		
		S3PR s3pr = new S3PR("Li_figure3-1_or_4-2","s2pr_in_s3pr_1",ptnet1,"p1",pa,pr);
		
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
		
		pa.clear();
		pa.add("p4"); pa.add("p5");
		pr.clear();
		pr.add("p7"); pr.add("p8");
		s3pr.add("s2pr_in_s3pr_2",ptnet2,"p6",pa,pr);
		System.out.println("s3pr: " + s3pr);
		
		return s3pr;
	}
	
	/**
	 * Li. p68, 图4-3
	 */
	public S3PR Li_Figure4_3() {
		///////////////////// ptnet1
		PTNet ptnet1 = new PTNet();
		// p0
		ptnet1.addPlace("p1");
		// pa
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t3");
		ptnet1.addPlace("p4");
		ptnet1.addTransition("t4");
		ptnet1.addPlace("p5");
		ptnet1.addTransition("t5");
		ptnet1.addPlace("p6");
		ptnet1.addTransition("t6");
		ptnet1.addTransition("t7");
		
		ptnet1.addPlace("p22");
		ptnet1.addTransition("t18");
		// pr
		ptnet1.addPlace("p16");
		ptnet1.addPlace("p19");
		ptnet1.addPlace("p20");
		ptnet1.addPlace("p21");
			
		// p0,pa Flows,回路1
		ptnet1.addFlowRelationPT("p1", "t1");
		ptnet1.addFlowRelationTP("t1", "p2");
		ptnet1.addFlowRelationPT("p2", "t2");
		ptnet1.addFlowRelationTP("t2", "p3");
		ptnet1.addFlowRelationPT("p3", "t3");
		ptnet1.addFlowRelationTP("t3", "p4");
		ptnet1.addFlowRelationPT("p4", "t4");
		ptnet1.addFlowRelationTP("t4", "p1");
		// p0,pa Flows,回路2
		ptnet1.addFlowRelationPT("p1", "t18");
		ptnet1.addFlowRelationTP("t18", "p22");
		ptnet1.addFlowRelationPT("p22", "t5");
		ptnet1.addFlowRelationTP("t5", "p5");
		ptnet1.addFlowRelationPT("p5", "t6");
		ptnet1.addFlowRelationTP("t6", "p6");
		ptnet1.addFlowRelationPT("p6", "t7");
		ptnet1.addFlowRelationTP("t7", "p1");
		
		// pr Flows
		ptnet1.addFlowRelationPT("p19", "t18");
		ptnet1.addFlowRelationTP("t5", "p19");
		ptnet1.addFlowRelationPT("p16", "t5");
		ptnet1.addFlowRelationTP("t6", "p16");
		ptnet1.addFlowRelationPT("p19", "t6");
		ptnet1.addFlowRelationTP("t7", "p19");
		
		ptnet1.addFlowRelationPT("p20", "t1");
		ptnet1.addFlowRelationTP("t2", "p20");
		ptnet1.addFlowRelationPT("p21", "t2");
		ptnet1.addFlowRelationTP("t3", "p21");
		ptnet1.addFlowRelationPT("p20", "t3");
		ptnet1.addFlowRelationTP("t4", "p20");
		
		Set<String> pa = new HashSet<String>();
		pa.add("p2"); pa.add("p3"); pa.add("p4"); pa.add("p5");
		pa.add("p6"); pa.add("p22");
		Set<String> pr = new HashSet<String>();
		pr.add("p16"); pr.add("p19"); pr.add("p20"); pr.add("p21");
		
		S3PR s3pr = new S3PR("Li_p68_figure4-3","s2pr_1",ptnet1,"p1",pa,pr);
		System.out.println("1: s3pr: " + s3pr);

		///////////////////// ptnet2
		PTNet ptnet2 = new PTNet();
		// p0
		ptnet2.addPlace("p7");
		// pa
		ptnet2.addTransition("t8");
		ptnet2.addPlace("p8");
		ptnet2.addTransition("t9");
		ptnet2.addPlace("p9");
		ptnet2.addTransition("t10");
		ptnet2.addPlace("p10");
		ptnet2.addTransition("t11");
		
		ptnet2.addTransition("t12");
		ptnet2.addPlace("p11");
		ptnet2.addTransition("t13");
	
		// pr
		ptnet2.addPlace("p16");
		ptnet2.addPlace("p17");
		ptnet2.addPlace("p18");
		ptnet2.addPlace("p19");

		// p0,pa Flows,回路1
		ptnet2.addFlowRelationPT("p7", "t8");
		ptnet2.addFlowRelationTP("t8", "p8");
		ptnet2.addFlowRelationPT("p8", "t9");
		ptnet2.addFlowRelationTP("t9", "p9");
		ptnet2.addFlowRelationPT("p9", "t10");
		ptnet2.addFlowRelationTP("t10", "p10");
		ptnet2.addFlowRelationPT("p10", "t11");
		ptnet2.addFlowRelationTP("t11", "p7");
		// p0,pa Flows,回路2
		ptnet2.addFlowRelationPT("p9", "t12");
		ptnet2.addFlowRelationTP("t12", "p11");
		ptnet2.addFlowRelationPT("p11", "t13");
		ptnet2.addFlowRelationTP("t13", "p7");

		// pr Flows
		ptnet2.addFlowRelationPT("p16", "t8");
		ptnet2.addFlowRelationTP("t9", "p16");
		ptnet2.addFlowRelationPT("p17", "t9");
		ptnet2.addFlowRelationTP("t10", "p17");
		
		ptnet2.addFlowRelationTP("t12", "p17");
		ptnet2.addFlowRelationPT("p18", "t12");
		ptnet2.addFlowRelationTP("t13", "p18");

		ptnet2.addFlowRelationPT("p19", "t10");
		ptnet2.addFlowRelationTP("t11", "p19");
		
		pa.clear();
		pa.add("p8"); pa.add("p9");	pa.add("p10"); pa.add("p11");
		
		pr.clear();
		pr.add("p16"); 	pr.add("p17"); pr.add("p18");pr.add("p19");

		s3pr.add("s2pr_2",ptnet2, "p7", pa, pr);
		System.out.println("2: s3pr: " + s3pr);
		
		///////////////////// ptnet3
		PTNet ptnet3 = new PTNet();
		// p0
		ptnet3.addPlace("p12");
		// pa
		ptnet3.addTransition("t14");
		ptnet3.addPlace("p13");
		ptnet3.addTransition("t15");
		ptnet3.addPlace("p14");
		ptnet3.addTransition("t16");
		ptnet3.addPlace("p15");
		ptnet3.addTransition("t17");
	
		// pr
		ptnet3.addPlace("p16");
		ptnet3.addPlace("p17");
		ptnet3.addPlace("p18");

		// p0,pa Flows,回路1
		ptnet3.addFlowRelationPT("p12", "t14");
		ptnet3.addFlowRelationTP("t14", "p13");
		ptnet3.addFlowRelationPT("p13", "t15");
		ptnet3.addFlowRelationTP("t15", "p14");
		ptnet3.addFlowRelationPT("p14", "t16");
		ptnet3.addFlowRelationTP("t16", "p15");
		ptnet3.addFlowRelationPT("p15", "t17");
		ptnet3.addFlowRelationTP("t17", "p12");
		
		// pr Flows
		ptnet3.addFlowRelationPT("p16", "t16");
		ptnet3.addFlowRelationTP("t17", "p16");
		ptnet3.addFlowRelationPT("p17", "t15");
		ptnet3.addFlowRelationTP("t16", "p17");
		ptnet3.addFlowRelationPT("p18", "t14");
		ptnet3.addFlowRelationTP("t15", "p18");
		
		pa.clear();
		pa.add("p13"); pa.add("p14");	pa.add("p15"); 
		
		pr.clear();
		pr.add("p16"); 	pr.add("p17"); pr.add("p18");

		s3pr.add("s2pr_3",ptnet3, "p12", pa, pr);
		System.out.println("3: s3pr: " + s3pr);
		return s3pr;
	 }
	
	/***********************************************
	 * Wang, p86,图4-1,对应的S3PR对象
	 **********************************************/
	public S3PR WangFigure4_1() {
		///////////////////// ptnet1
		PTNet ptnet1 = new PTNet();
		// p0
		ptnet1.addPlace("p21");
		// pa
		ptnet1.addTransition("t1");
		ptnet1.addPlace("p1");
		ptnet1.addTransition("t2");
		ptnet1.addPlace("p2");
		ptnet1.addTransition("t3");
		ptnet1.addPlace("p3");
		ptnet1.addTransition("t4");
		ptnet1.addPlace("p4");
		ptnet1.addTransition("t5");
		// pr
		ptnet1.addPlace("p26");
		ptnet1.addPlace("p27");
		ptnet1.addPlace("p28");
		// relations
		ptnet1.addFlowRelationTP("t1", "p1");
		ptnet1.addFlowRelationPT("p1", "t2");
		ptnet1.addFlowRelationTP("t2", "p2");
		ptnet1.addFlowRelationPT("p2", "t3");
		ptnet1.addFlowRelationTP("t3", "p3");
		ptnet1.addFlowRelationPT("p3", "t4");
		ptnet1.addFlowRelationTP("t4", "p4");
		ptnet1.addFlowRelationPT("p4", "t5");
		ptnet1.addFlowRelationTP("t5", "p21");
		ptnet1.addFlowRelationPT("p21", "t1");
		
		ptnet1.addFlowRelationPT("p26", "t3");
		ptnet1.addFlowRelationTP("t4", "p26");	
		ptnet1.addFlowRelationPT("p27", "t4");
		ptnet1.addFlowRelationTP("t5", "p27");
		ptnet1.addFlowRelationPT("p27", "t1");
		ptnet1.addFlowRelationTP("t2", "p27");
		ptnet1.addFlowRelationPT("p28", "t2");
		ptnet1.addFlowRelationTP("t3", "p28");
		
		Collection<String> pa1 = new HashSet<String>();
		pa1.add("p1"); pa1.add("p2"); pa1.add("p3"); pa1.add("p4");
		Collection<String> pr1 = new HashSet<String>();
		pr1.add("p26");
		pr1.add("p27");
		pr1.add("p28");
		
		///////////////////// ptnet2
		PTNet ptnet2 = new PTNet();
		// p0
		ptnet2.addPlace("p22");
		// pa
		ptnet2.addTransition("t11");
		ptnet2.addPlace("p9");
		ptnet2.addTransition("t10");
		ptnet2.addPlace("p8");
		ptnet2.addTransition("t9");
		ptnet2.addPlace("p7");
		ptnet2.addTransition("t8");
		ptnet2.addPlace("p6");
		ptnet2.addTransition("t7");
		ptnet2.addPlace("p5");
		ptnet2.addTransition("t6");
		// pr
		ptnet2.addPlace("p26");
		ptnet2.addPlace("p27");
		ptnet2.addPlace("p28");
		ptnet2.addPlace("p29");
		
		// relations
		ptnet2.addFlowRelationTP("t6", "p5");
		ptnet2.addFlowRelationPT("p5", "t7");
		ptnet2.addFlowRelationTP("t7", "p6");
		ptnet2.addFlowRelationPT("p6", "t8");
		ptnet2.addFlowRelationTP("t8", "p7");
		ptnet2.addFlowRelationPT("p7", "t9");
		ptnet2.addFlowRelationTP("t9", "p8");
		ptnet2.addFlowRelationPT("p8", "t10");
		ptnet2.addFlowRelationTP("t10", "p9");
		ptnet2.addFlowRelationPT("p9", "t11");

		ptnet2.addFlowRelationPT("p26", "t8");
		ptnet2.addFlowRelationTP("t8", "p27");
		ptnet2.addFlowRelationPT("p27", "t7");
		ptnet2.addFlowRelationTP("t7", "p28");
		ptnet2.addFlowRelationPT("p28", "t6");
		ptnet2.addFlowRelationTP("t10", "p28");
		ptnet2.addFlowRelationPT("p28", "t9");
		ptnet2.addFlowRelationTP("t9", "p26");
		ptnet2.addFlowRelationTP("t11", "p29");
		ptnet2.addFlowRelationPT("p29", "t10");
		

		Collection<String> pa2 = new HashSet<String>();
		pa2.add("p5"); pa2.add("p6"); pa2.add("p7"); pa2.add("p8");
		pa2.add("p9");
		Collection<String> pr2 = new HashSet<String>();
		pr2.add("p26"); pr2.add("p27"); pr2.add("p28"); pr2.add("p29");
		
		
		///////////////////// ptnet3
		PTNet ptnet3 = new PTNet();
		// p0
		ptnet3.addPlace("p23");
		// pa
		ptnet3.addTransition("t12");
		ptnet3.addPlace("p10");
		ptnet3.addTransition("t13");
		ptnet3.addPlace("p11");
		ptnet3.addTransition("t14");
		ptnet3.addPlace("p12");
		ptnet3.addTransition("t15");
		ptnet3.addPlace("p13");
		ptnet3.addTransition("t16");
		// pr
		ptnet3.addPlace("p29");
		ptnet3.addPlace("p30");
		ptnet3.addPlace("p31");
		ptnet3.addPlace("p32");
		
		// relations
		ptnet3.addFlowRelationTP("t12", "p10");
		ptnet3.addFlowRelationPT("p10", "t13");
		ptnet3.addFlowRelationTP("t13", "p11");
		ptnet3.addFlowRelationPT("p11", "t14");
		ptnet3.addFlowRelationTP("t14", "p12");
		ptnet3.addFlowRelationPT("p12", "t15");
		ptnet3.addFlowRelationTP("t15", "p13");
		ptnet3.addFlowRelationPT("p13", "t16");

		ptnet3.addFlowRelationPT("p29", "t12");
		ptnet3.addFlowRelationTP("t13", "p29");
		ptnet3.addFlowRelationPT("p30", "t13");
		ptnet3.addFlowRelationTP("t14", "p30");
		ptnet3.addFlowRelationPT("p31", "t15");
		ptnet3.addFlowRelationTP("t16", "p31");
		ptnet3.addFlowRelationPT("p32", "t14");
		ptnet3.addFlowRelationTP("t15", "p32");
		

		Collection<String> pa3 = new HashSet<String>();
		pa3.add("p10"); pa3.add("p11"); pa3.add("p12"); pa3.add("p13");
		Collection<String> pr3 = new HashSet<String>();
		pr3.add("p29"); pr3.add("p30"); pr3.add("p31"); pr3.add("p32");
		
		///////////////////// ptnet4
		PTNet ptnet4 = new PTNet();
		// p0
		ptnet4.addPlace("p24");
		// pa
		ptnet4.addTransition("t17");
		ptnet4.addPlace("p14");
		ptnet4.addTransition("t18");
		ptnet4.addPlace("p15");
		ptnet4.addTransition("t19");
		ptnet4.addPlace("p16");
		ptnet4.addTransition("t20");
		// pr
		ptnet4.addPlace("p29");
		ptnet4.addPlace("p30");
		ptnet4.addPlace("p32");

		// relations
		ptnet4.addFlowRelationTP("t17", "p14");
		ptnet4.addFlowRelationPT("p14", "t18");
		ptnet4.addFlowRelationTP("t18", "p15");
		ptnet4.addFlowRelationPT("p15", "t19");
		ptnet4.addFlowRelationTP("t19", "p16");
		ptnet4.addFlowRelationPT("p16", "t20");
		ptnet4.addFlowRelationTP("t20", "p24");
		ptnet4.addFlowRelationPT("p24", "t17");

		ptnet4.addFlowRelationPT("p29", "t19");
		ptnet4.addFlowRelationTP("t20", "p29");
		ptnet4.addFlowRelationPT("p32", "t18");
		ptnet4.addFlowRelationTP("t19", "p32");
		ptnet4.addFlowRelationPT("p30", "t17");
		ptnet4.addFlowRelationTP("t18", "p30");

		Collection<String> pa4 = new HashSet<String>();
		pa4.add("p14");	pa4.add("p15");	pa4.add("p16");
		Collection<String> pr4 = new HashSet<String>();
		pr4.add("p29");	pr4.add("p30");	pr4.add("p32");			
		
		///////////////////// ptnet5
		PTNet ptnet5 = new PTNet();
		// p0
		ptnet5.addPlace("p25");
		// pa
		ptnet5.addTransition("t21");
		ptnet5.addPlace("p17");
		ptnet5.addTransition("t22");
		ptnet5.addPlace("p18");
		ptnet5.addTransition("t23");
		ptnet5.addPlace("p19");
		ptnet5.addTransition("t24");
		ptnet5.addPlace("p20");
		ptnet5.addTransition("t25");
		// pr
		ptnet5.addPlace("p29");
		ptnet5.addPlace("p30");
		ptnet5.addPlace("p31");
		ptnet5.addPlace("p32");

		// relations
		ptnet5.addFlowRelationTP("t21", "p17");
		ptnet5.addFlowRelationPT("p17", "t22");
		ptnet5.addFlowRelationTP("t22", "p18");
		ptnet5.addFlowRelationPT("p18", "t23");
		ptnet5.addFlowRelationTP("t23", "p19");
		ptnet5.addFlowRelationPT("p19", "t24");
		ptnet5.addFlowRelationTP("t24", "p20");
		ptnet5.addFlowRelationPT("p20", "t25");
		ptnet5.addFlowRelationTP("t25", "p25");
		ptnet5.addFlowRelationPT("p25", "t21");

		ptnet5.addFlowRelationPT("p29", "t24");
		ptnet5.addFlowRelationTP("t25", "p29");
		ptnet5.addFlowRelationPT("p31", "t23");
		ptnet5.addFlowRelationTP("t24", "p31");
		ptnet5.addFlowRelationPT("p30", "t22");
		ptnet5.addFlowRelationTP("t23", "p30");
		ptnet5.addFlowRelationPT("p32", "t21");
		ptnet5.addFlowRelationTP("t22", "p32");

		Collection<String> pa5 = new HashSet<String>();
		pa5.add("p17");	pa5.add("p18");	pa5.add("p19"); pa5.add("p20");
		Collection<String> pr5 = new HashSet<String>();
		pr5.add("p29");	pr5.add("p30");	pr5.add("p31"); pr5.add("p32");			
		
		//////////////////////// S3PR对象
		S3PR s3pr = new S3PR("wang_p86_figure4-3","s2pr_1",ptnet1,"p21",pa1,pr1);
		s3pr.add("s2pr_2", ptnet2,"p22",pa2,pr2);
		s3pr.add("s2pr_3", ptnet3,"p23",pa3,pr3);
		s3pr.add("s2pr_4", ptnet4,"p24",pa4,pr4);
		s3pr.add("s2pr_5", ptnet5,"p25",pa5,pr5);
		
		s3pr.getPlace("p26").setLabel("r1");
		s3pr.getPlace("p27").setLabel("r2");
		s3pr.getPlace("p28").setLabel("r3");
		s3pr.getPlace("p29").setLabel("r4");
		s3pr.getPlace("p30").setLabel("r5");
		s3pr.getPlace("p31").setLabel("r6");
		s3pr.getPlace("p32").setLabel("r7");
		
		System.out.println("1: s3pr: " + s3pr);
		
		//S2PR s2pr2 = new S2PR("s2pr_2", ptnet2, "", pa2, pr2);
		
		return s3pr;
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#S3PR(S2PR)}.
	 * 2个资源， 自动生成网， Wang algorithm4_1(),algorithm4_2(),algorithm4_1()
	 */
	//@Test
	public void testS3PR() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR("test_s2pr",2,1,4);
		System.out.println("s2pr:" + s2pr);
		
		S3PR s3pr = new S3PR("test_s3pr","s2pr_1",s2pr);
		System.out.println("s3pr: " + s3pr);
		
		// 2个强分图，每个是1个节点（资源），
		// 本s3pr实际上是一个s2pr，因此，Cmatrix rank,alpha,delta = 0,0,0
		//s3pr.algorithm4_1(true);  // Cmatrix rank,alpha,delta = 0,0,0
		//s3pr.algorithm4_2(true);  // Cmatrix rank,alpha,delta = 0,0,0
		s3pr.algorithm4_3(true);  // Cmatrix rank,alpha,delta = 0,0,0
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#S3PR(S2PR)}.
	 * 3个资源，自动生成网， Wang algorithm4_1(),algorithm4_2(),algorithm4_1()
	 */
	//@Test
	public void testS3PR1() {
		//fail("Not yet implemented");
		S2PR s2pr = new S2PR("test_s2pr",3,1,4);
		System.out.println("s2pr:" + s2pr);
		
		S3PR s3pr = new S3PR("test_s3pr","s2pr_1",s2pr);
		System.out.println("s3pr: " + s3pr);
		
		// 3个强分图，每个是1个节点（资源），
		// 本s3pr实际上是一个s2pr，因此，Cmatrix rank,alpha,delta = 0,0,0
		//s3pr.algorithm4_1(true);  // Cmatrix rank,alpha,delta = 0,0,0
		//s3pr.algorithm4_2(true);  // Cmatrix rank,alpha,delta = 0,0,0
		s3pr.algorithm4_3(true);  // Cmatrix rank,alpha,delta = 0,0,0
	}
	
	/**
	 * Li. 图3-1或图4-2, Wang algorithm4_1(),algorithm4_2(),algorithm4_1()
	 */
	//@Test
	public void testS3PR2() {
	
		S3PR s3pr = Li_Figure3_1();
		
		// s3pr.algorithm4_1(true);  // Cmatrix rank,alpha,delta  = 1,2,1
		// s3pr.algorithm4_2(true);  // Cmatrix rank,alpha,delta  = 1,2,1
		s3pr.algorithm4_3(true);  // Cmatrix rank,alpha,delta  = 1,2,1
		
		/***
		Siphons[1]    = [p3[p3], p5[p5], p7[p7], p8[p8]]
		SiphonComs[1] = [p2[p2], p4[p4]]
		***/
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#getHr(Set)}.
	 * Li. 图3-1或图4-2
	 */
	//@Test
	public void getHrs() {
		S3PR s3pr = Li_Figure3_1();
		
		//////////////////////////////////////////////////
		// SR: 信标S中的资源库所集合
		Set<PTPlace> SR = new HashSet<>();
		SR.add(s3pr.getPlace("p7"));
		SR.add(s3pr.getPlace("p8"));
		Collection<PTPlace> Hrs = s3pr.getHr(SR);
		s3pr.printPNNodes("Hrs: ", Hrs); // Hrs: [p2[p2], p3[p3], p4[p4], p5[p5]]
		assertEquals(4, Hrs.size());
		
		// Hrs = H(p7) ∪ H(p8)
		s3pr.printPNNodes("H(p7) = ", s3pr.getHr(s3pr.getPlace("p7"))); // H(p7) = [p2[p2], p5[p5]]
		s3pr.printPNNodes("H(p8) = ", s3pr.getHr(s3pr.getPlace("p8"))); // H(p8) = [p3[p3], p4[p4]]]
		
		// 信标S
		Collection<PTPlace> S = new HashSet<>();
		S.add(s3pr.getPlace("p3"));  S.add(s3pr.getPlace("p5"));
		S.add(s3pr.getPlace("p7"));  S.add(s3pr.getPlace("p8"));
		// 信标补集[S] = Hrs \ S
		Collection<PTPlace> SS = new HashSet<>();
		SS.addAll(Hrs);
		SS.removeAll(S);  // S与SS的类型说明一致，因此removeAll()返回正确的值
		s3pr.printPNNodes("信标补集：", SS); // 信标补集：[p2[p2], p4[p4]]
		
		// 直接调用函数，求信标S的补集
		s3pr.printPNNodes("信标补集：" , s3pr.getSiphonCom(S)); // 信标补集：[p2[p2], p4[p4]]
		
	}
	
	/**
	 * test set, ==, equals
	 * Set中集合运算的基础：equals判断是否两个元素相同，不是通过"=="判断的
	 */
	@SuppressWarnings("rawtypes")
	//@Test
	public void testSet() {
		S2PR s2pr = new S2PR();
		s2pr.addPlace("p1");
		s2pr.addPlace("p2");
		s2pr.addPlace("p3");
		S3PR s3pr = new S3PR();
		s3pr.addPlace("p3");
		s3pr.addPlace("p4");
		
		Collection<PTPlace> places = new HashSet<>();
		// places.add(s3pr.getPlace("p3")))失败，
		// 因为在此之前,有places.add(s2pr.getPlace("p3")),
		// places通过equals判断，已经有"p3"对应的place，因此不用增加了。
		System.out.println("add ok = " + 
		        places.add(s2pr.getPlace("p1")) + "," +
				places.add(s2pr.getPlace("p3")) + "," + 
		        places.add(s3pr.getPlace("p3"))); // add ok = true,true,false
		System.out.println("places: " + places);  // places: [p1[p1], p3[p3]]
		boolean b1 = (s2pr.getPlace("p3") == s3pr.getPlace("p3")); // flase
		boolean b2 = (s2pr.getPlace("p3").equals(s3pr.getPlace("p3"))); // true
		System.out.println("两个p3  ！==,但是equals： " + b1 + "," + b2);
		
		Collection<PTPlace> ps1 = new HashSet<>();
		Collection<PTPlace> ps2 = new HashSet<>();
		s2pr.setName("net1");
		s3pr.setName("net2");
		ps1.add(s2pr.getPlace("p3"));
		ps2.add(s3pr.getPlace("p3"));
		ps1.retainAll(ps2); 
		System.out.println("虽然不是同一个place,但是他们的交集相同，因为equals相等，ps1=" + ps1);
		
		//Collection<AbstractPNNode> ps11 = new HashSet<>();
		//Collection<PTPlace> ps22 = new HashSet<>();
		Set<AbstractPNNode> ps11 = new HashSet<>();
		Set<PTPlace> ps22 = new HashSet<>();
		ps11.add(s2pr.getPlace("p3"));
		ps22.add(s3pr.getPlace("p3"));
		ps11.retainAll(ps22); 
		System.out.println("虽然不是同一个place,但是他们的交集相同，因为equals相等，ps11=" + ps11);
	}
	

	 
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#s3pr4_1()}.
	 * Li. p68, 图4-3，例4.4完整测试
	 */
	//@Test
	public void s3pr4_1() {	
		System.out.println("s3pr4_1()===============");
		S3PR s3pr = Li_Figure4_3();
		s3pr.s3pr4_1();
		
		// siphons
		Collection<PTPlace> S1 = new HashSet<>();
		S1.add(s3pr.getPlace("p6")); S1.add(s3pr.getPlace("p10")); S1.add(s3pr.getPlace("p11"));
		S1.add(s3pr.getPlace("p15")); S1.add(s3pr.getPlace("p16")); S1.add(s3pr.getPlace("p17"));
		S1.add(s3pr.getPlace("p18")); S1.add(s3pr.getPlace("p19"));
		
		Collection<PTPlace> S2 = new HashSet<>();
		S2.add(s3pr.getPlace("p4")); S2.add(s3pr.getPlace("p20")); S2.add(s3pr.getPlace("p21"));
		
		Collection<PTPlace> S3 = new HashSet<>();
		S3.add(s3pr.getPlace("p6")); S3.add(s3pr.getPlace("p9")); S3.add(s3pr.getPlace("p10"));
		S3.add(s3pr.getPlace("p15")); S3.add(s3pr.getPlace("p16")); S3.add(s3pr.getPlace("p17"));
		S3.add(s3pr.getPlace("p19"));
		
		Collection<PTPlace> S4 = new HashSet<>();
		S4.add(s3pr.getPlace("p5")); S4.add(s3pr.getPlace("p9")); S4.add(s3pr.getPlace("p15"));
		S4.add(s3pr.getPlace("p16")); S4.add(s3pr.getPlace("p17")); 
		
		Collection<PTPlace> S5 = new HashSet<>();
		S5.add(s3pr.getPlace("p6")); S5.add(s3pr.getPlace("p8")); S5.add(s3pr.getPlace("p10"));
		S5.add(s3pr.getPlace("p15")); S5.add(s3pr.getPlace("p16")); S5.add(s3pr.getPlace("p19"));
		
		// 信标的补集, 信标补集[S] = Hr(SR) \ S
		// SR: 信标S中的资源库所集合
		Collection<PTPlace> SR = new HashSet<>();
		SR.addAll(s3pr.getPR());
		SR.retainAll(S1);
		s3pr.printPNNodes("SR1 = ", SR);
		Collection<PTPlace> Scom1 = new HashSet<>();
		Scom1.addAll(s3pr.getHr(SR));
		Scom1.removeAll(S1);
		s3pr.printPNNodes("[S1] = ", Scom1);
		
		Collection<PTPlace> Scom2 = new HashSet<>();
		SR.clear();
		SR.addAll(s3pr.getPR());
		SR.retainAll(S2);
		s3pr.printPNNodes("SR2 = ", SR);
		Scom2.addAll(s3pr.getHr(SR));
		Scom2.removeAll(S2);
		s3pr.printPNNodes("[S2] = ", Scom2);
		
		Collection<PTPlace> Scom3 = new HashSet<>();
		SR.clear();
		SR.addAll(s3pr.getPR());
		SR.retainAll(S3);
		s3pr.printPNNodes("SR3 = ", SR);
		Scom3.addAll(s3pr.getHr(SR));
		Scom3.removeAll(S3);
		s3pr.printPNNodes("[S3] = ", Scom3);
		
		Collection<PTPlace> Scom4 = new HashSet<>();
		SR.clear();
		SR.addAll(s3pr.getPR());
		SR.retainAll(S4);
		s3pr.printPNNodes("SR4 = ", SR);
		Scom4.addAll(s3pr.getHr(SR));
		Scom4.removeAll(S4);
		s3pr.printPNNodes("[S4] = ", Scom4);
		
		Collection<PTPlace> Scom5 = new HashSet<>();
		SR.clear();
		SR.addAll(s3pr.getPR());
		SR.retainAll(S5);
		s3pr.printPNNodes("SR5 = ", SR);
		Scom5.addAll(s3pr.getHr(SR));
		Scom5.removeAll(S5);
		s3pr.printPNNodes("[S5] = ", Scom5);
		
		Collection<PTPlace> Scom31 = new HashSet<>();
		Scom31.addAll(Scom3);
		Scom31.retainAll(s3pr.getS2pr("s2pr_1").getPA());
		s3pr.printPNNodes("S31 = ", Scom31);
		
		Collection<PTPlace> Scom32 = new HashSet<>();
		Scom32.addAll(Scom3);
		Scom32.retainAll(s3pr.getS2pr("s2pr_2").getPA());
		s3pr.printPNNodes("S32 = ", Scom32);
		
		Collection<PTPlace> Scom33 = new HashSet<>();
		Scom33.addAll(Scom3);
		Scom33.retainAll(s3pr.getS2pr("s2pr_3").getPA());
		s3pr.printPNNodes("S33 = ", Scom33);
		
		Collection<PTPlace> Scom3U = new HashSet<>();
		Scom3U.addAll(Scom31); Scom3U.addAll(Scom32); Scom3U.addAll(Scom33);
		// 5. [S] = ∪ <sub>i=1</sub><sup style="margin-left:-8px">n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。
		assertTrue(Scom3.equals(Scom3U));
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#s3pr4_1()}.
	 * Li. p68, 图4-3，例4.4的分步测试
	 */
	//@Test
	public void s3pr4_1Parts() {
		System.out.println("s3pr4_1Parts()===============");
		S3PR s3pr = Li_Figure4_3();
				
		System.out.println("性质4.1 ===============");
		System.out.println("\n1. 任何 p∈PAi (一个S2PR对应一个p0，因此确切的说，应该是一个p0或一个S2PR对应一个Ip)都对应着一个极小的P-半流Ip, 使得‖Ip‖ = PAi ∪ {p0}\n");
		System.out.println("---getIp(s2prName)");
		Collection<PTPlace> Ip1 = s3pr.getIp("s2pr_1");
		Collection<PTPlace> Ip2 = s3pr.getIp("s2pr_2");
		Collection<PTPlace> Ip3 = s3pr.getIp("s2pr_3");
		s3pr.printPNNodes("Ip1 = ", Ip1);
		s3pr.printPNNodes("Ip2 = ", Ip2);
		s3pr.printPNNodes("Ip3 = ", Ip3);
		
		System.out.println("---getIp(p0)");
		for (S2PR s2pr: s3pr.getS2pr().values()) {
			s3pr.printPNNodes(s2pr.get_p0().getName() + "对应的Ip = ", s3pr.getIp(s2pr.get_p0()));
		}
		
		System.out.println("---getIp()");
		for (Map.Entry<String,Collection<PTPlace>> e: s3pr.getIp().entrySet()) {
			s3pr.printPNNodes(e.getKey()+ "对应的Ip = ", e.getValue());
		}
		
		/////////////////////////////////////////////
		System.out.println("\n2. 任何资源r∈PR都对应着一个极小的P-半流Ir, 使得‖Ir‖ = {r} ∪  H(r)\n");
		System.out.println("---getIr(pr)");
		for (PTPlace p: s3pr.getPR()) {
			s3pr.printPNNodes("资源" + p + "对应的Ir = ", s3pr.getIr(p));
		}
		System.out.println("---getIr()");
		Map<String,Collection<PTPlace>> Irs = s3pr.getIr();
		for (String p: Irs.keySet()) {
			s3pr.printPNNodes("资源" + p + "对应的Ir = ", Irs.get(p));
		}
		
		///////////////////////////////////////////////
		System.out.println("\n3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR\\{r}, p ∉ H(r1)\n");
		// 信标siphons
		System.out.println("信标: "); 
		List<Collection<PTPlace>> S = new ArrayList<>();
		Collection<PTPlace> S1 = new HashSet<>();
		S1.add(s3pr.getPlace("p6")); S1.add(s3pr.getPlace("p10")); S1.add(s3pr.getPlace("p11"));
		S1.add(s3pr.getPlace("p15")); S1.add(s3pr.getPlace("p16")); S1.add(s3pr.getPlace("p17"));
		S1.add(s3pr.getPlace("p18")); S1.add(s3pr.getPlace("p19"));
		s3pr.printPNNodes("S1 = ", S1);
		S.add(S1);
		
		Collection<PTPlace> S2 = new HashSet<>();
		S2.add(s3pr.getPlace("p4")); S2.add(s3pr.getPlace("p20")); S2.add(s3pr.getPlace("p21"));
		s3pr.printPNNodes("S2 = ", S2);
		S.add(S2);
		
		Collection<PTPlace> S3 = new HashSet<>();
		S3.add(s3pr.getPlace("p6")); S3.add(s3pr.getPlace("p9")); S3.add(s3pr.getPlace("p10"));
		S3.add(s3pr.getPlace("p15")); S3.add(s3pr.getPlace("p16")); S3.add(s3pr.getPlace("p17"));
		S3.add(s3pr.getPlace("p19"));
		s3pr.printPNNodes("S3 = ", S3);
		S.add(S3);
		
		Collection<PTPlace> S4 = new HashSet<>();
		S4.add(s3pr.getPlace("p5")); S4.add(s3pr.getPlace("p9")); S4.add(s3pr.getPlace("p15"));
		S4.add(s3pr.getPlace("p16")); S4.add(s3pr.getPlace("p17")); 
		s3pr.printPNNodes("S4 = ", S4);
		S.add(S4);
		
		Collection<PTPlace> S5 = new HashSet<>();
		S5.add(s3pr.getPlace("p6")); S5.add(s3pr.getPlace("p8")); S5.add(s3pr.getPlace("p10"));
		S5.add(s3pr.getPlace("p15")); S5.add(s3pr.getPlace("p16")); S5.add(s3pr.getPlace("p19"));
		s3pr.printPNNodes("S5 = ", S5);
		S.add(S5);
		
		// 信标S中资源库所集合
		System.out.println("信标S中资源库所集合");
		Collection<PTPlace> SR1 = s3pr.getSR(S1);
		Collection<PTPlace> SR2 = s3pr.getSR(S2);
		Collection<PTPlace> SR3 = s3pr.getSR(S3);
		Collection<PTPlace> SR4 = s3pr.getSR(S4);
		Collection<PTPlace> SR5 = s3pr.getSR(S5);
		s3pr.printPNNodes("SR1 = ", SR1);
		s3pr.printPNNodes("SR2 = ", SR2);
		s3pr.printPNNodes("SR3 = ", SR3);
		s3pr.printPNNodes("SR4 = ", SR4);
		s3pr.printPNNodes("SR5 = ", SR5);
		List<Collection<PTPlace>> SR = new ArrayList<>();
		SR.add(SR1);  SR.add(SR2);  SR.add(SR3);  SR.add(SR4);  SR.add(SR5);
		
		// 信标的补集
		System.out.println("信标的补集: ");
		Collection<PTPlace> SCom1 = s3pr.getSiphonCom(S1);
		s3pr.printPNNodes("[S1] = ", SCom1);
		Collection<PTPlace> SCom2 = s3pr.getSiphonCom(S2);
		s3pr.printPNNodes("[S2] = ", SCom2);
		Collection<PTPlace> SCom3 = s3pr.getSiphonCom(S3);
		s3pr.printPNNodes("[S3] = ", SCom3);
		Collection<PTPlace> SCom4 = s3pr.getSiphonCom(S4);
		s3pr.printPNNodes("[S4] = ", SCom4);
		Collection<PTPlace> SCom5 = s3pr.getSiphonCom(S5);
		s3pr.printPNNodes("[S5] = ", SCom5);
		List<Collection<PTPlace>> SCom = new ArrayList<>();
		SCom.add(SCom1); SCom.add(SCom2); SCom.add(SCom3); SCom.add(SCom4); SCom.add(SCom5); 
		
		// 3. 任意p∈[S], 存在r∈SR, p∈H(r), 任意r1∈PR \{r}, p ∉ H(r1)
		Collection<PTPlace> tmp1 = new HashSet<>();
		for (int i = 0; i < S.size(); i++) {
			Collection<PTPlace> Scom = SCom.get(i);
			for (PTPlace r: SR.get(i)) {
				tmp1.clear();
				tmp1.addAll(Scom);
				tmp1.retainAll(s3pr.getHr(r));
				for (PTPlace p: tmp1) {  // p∈[S], p∈H(r)
					for (PTPlace r1: s3pr.getPR()) {
						if (r1.equals(r)) continue;
						Collection<PTPlace> tmp2 = s3pr.getHr(r1);
						if (tmp2.contains(p)) System.out.println("【不】符合性质4.1，第3项，p = " + p);
						else System.out.println("符合性质4.1，第3项，p = " + p);
					}
				}
			}
			
		}
		
		/////////////////////////////////////////////////////////////////
		System.out.println("\n4. [S] ∪ S是N的P-半流的支撑;"); 
	    System.out.println("小王，引理4.1, 如果S满足条件：SR ≠ ∅,并且任意p∈SA,存在r∈SR,使得p∈H(r)成立，则");
	    System.out.println("[S] ∪ S = ‖Is‖, 其中Is = ∑<sub>r∈SR</sub>Ir. SA,SR分别表示S中工序、资源库所集合。\n");
	    // [S] ∪  S
	    for (int i = 0; i < S.size(); i++) {
	    	tmp1.clear();
	    	tmp1.addAll(SCom.get(i));
	    	tmp1.addAll(S.get(i));
	    	s3pr.printPNNodes(i+1 + ": [S] ∪  S  = ", tmp1);
	    }
		// ‖Is‖
	    Collection<PTPlace> Is = null;
	    for (int i = 0; i < S.size(); i++) {
	    	for (int j = 0; j < SR.get(i).size(); j++) {
	    		Is = s3pr.getIs(SR.get(i));
	    	}
	    	s3pr.printPNNodes(i+1 + ": ‖Is‖   = ", Is);
	    }
	    
	    ///////////////////////////////
		System.out.println("\n5. [S] = ∪ <sub>i=1</sub><sup>n</sup>[S]<sup>i</sup>, 其中[S]<sup>i</sup> = [S] ∩ PAi。\n");
		Collection<PTPlace> ScomU = new HashSet<>();
		Collection<PTPlace> Scomi = new HashSet<>(); // [S]<sup>i</sup> = [S] ∩ PAi 
		for (int i = 0; i < SCom.size(); i++) { 
			ScomU.clear(); 
			for (S2PR s2pr: s3pr.getS2pr().values())  { // 3个进程
				Scomi.clear();
				Scomi.addAll(SCom.get(i));
				Scomi.retainAll(s2pr.getPA());
				ScomU.addAll(Scomi);
			}
			s3pr.printPNNodes(i+1 + "[S]  = ", SCom.get(i));
			s3pr.printPNNodes(i+1 + "[S]u = ", ScomU);
			assertTrue(SCom.get(i).equals(ScomU));
		}
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#getRgraph()}.
	 * Wang, p86,图4-1,对应的S3PR
	 */
	//@Test
	public void RGraphWangFigure4_1() {
		S3PR s3pr = WangFigure4_1();
		// 资源有向图
		RGraph rGraph = s3pr.getRgraph(true);
		
		System.out.println("==========================");
		Collection<RGraph> Components1 = rGraph.getStronglyConnectedComponentGraphs(true);
		//Collection<RGraph> Components1 = rGraph.getStronglyConnectedComponentGraphs(false);
		System.out.println(Components1);
	}

	@Test
	public void RGraphWangFigure4_1_RemoveEdge() {
		S3PR s3pr = WangFigure4_1();
		// 资源有向图
		RGraph rGraph = s3pr.getRgraph(true);
		
		System.out.println("==========================");
		try {
			// 删除了内部边：t14(p32[r7] -> p30[r5])
			// 没有删除平行边：t18(p32[r7] -> p30[r5])
			// rGraph.removeEdge("p32", "p30"); 
			
			// 以上两种符合条件的边均可删除
			//rGraph.removeEdges("p32", "p30");
			
			//rGraph.removeEdge("t14"); // ok
			rGraph.removeEdge("t18"); // ok
			
		} catch (VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EdgeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println(rGraph);
		
		System.out.println("t14: " + rGraph.getEdge("t14"));
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#algorithm4_1(boolean)}.
	 * Wang, p86,图4-1,对应的S3PR
	 */
	//@Test
	public void algorithm4_1() {
		S3PR s3pr = WangFigure4_1();
		//s3pr.algorithm4_1(true);
		s3pr.algorithm4_1(false);
		int i = 1;
		for (Collection<PTPlace> s: s3pr.getSiphons()) {
			s3pr.printPNNodes("s[" + i + "] = ", s);
			i++;
		}
		i = 1;
		for (Collection<PTPlace> s: s3pr.getSiphonComs()) {
			s3pr.printPNNodes("SCom[" + i + "] = ", s);
			i++;
		}
		// 信标补集矩阵
		InvariantMatrix Cmatrix = s3pr.CMatrix();
		int rank_alpha_delta[] = s3pr.rank_alpha_delta(Cmatrix);
		System.out.println("C-Matrix:");
		Cmatrix.print(2, 0);
		System.out.println("Cmatrix rank,alpha,delta = " + 
					   rank_alpha_delta[0] + "," + rank_alpha_delta[1] + "," + rank_alpha_delta[2]);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#algorithm4_2(boolean)}.
	 * Wang, p86,图4-1,对应的S3PR
	 */
	//@Test
	public void algorithm4_2() {
		S3PR s3pr = WangFigure4_1();
		s3pr.algorithm4_2(true);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#algorithm4_3(boolean)}.
	 * Wang, p86,图4-1,对应的S3PR
	 */
	//@Test
	public void algorithm4_3_verbose() {
		S3PR s3pr = WangFigure4_1();
		// verbose模式，显示计算过程
		s3pr.algorithm4_3(true);
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#algorithm4_3(boolean)}.
	 * Wang, p86,图4-1,对应的S3PR
	 */
	//@Test
	public void algorithm4_3() {
		S3PR s3pr = WangFigure4_1();
		// 非verbose模式，不显示计算过程
		InvariantMatrix cmatrix = s3pr.algorithm4_3(false);
		System.out.println("=======================");
		System.out.println("\nC-Matrix:");
		cmatrix.print(2, 0);
		int rank_alpha_delta[] = s3pr.rank_alpha_delta(cmatrix); // rank([C])<=δ<=α
		System.out.println("Cmatrix rank,alpha,delta = " + 
				rank_alpha_delta[0] + "," + rank_alpha_delta[1] + "," + rank_alpha_delta[2]);
		int i = 1;
		for (Collection<PTPlace> siphon: s3pr.getSiphons()) {
			s3pr.printPNNodes("Siphons[" + i + "]    = ", siphon);
			i++;
		}
		i = 1;
		for (Collection<PTPlace> siphonCom: s3pr.getSiphonComs()) {
			s3pr.printPNNodes("SiphonComs[" + i + "] = ", siphonCom);
			i++;
		}
	}
	
	/**
	 * Test method for {@link edu.xidian.petrinet.S3PR.S3PR#deleteN(boolean)}.
	 * Wang, p86,图4-1,对应的S3PR
	 */
	//@Test
	public void deleteN_verbose() {
		S3PR s3pr = WangFigure4_1();
		// verbose模式，显示计算过程
		s3pr.deleteN(true);
	}
	

	
	/**
	 * 组合测试，从strs中取出2个元素的所有不同组合，参数返回结果
	 */
	public void combine1(List<String> strs, List<List<String>> results) {
		int n = strs.size();
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				List<String> comb = new ArrayList<>();
				comb.add(strs.get(i));
				comb.add(strs.get(j));
				results.add(comb);
			}
		}
	}
	
	//@Test
	public void combineTest1() {
		List<String> strs = new ArrayList<>();
		strs.add("1"); strs.add("2"); strs.add("3"); strs.add("4");
		List<List<String>> results = new ArrayList<>();
		combine1(strs,results);
		System.out.println(results.size()); // 6
		System.out.println(results);  // [[1, 2], [1, 3], [1, 4], [2, 3], [2, 4], [3, 4]]
	}
	
	/**
	 * 组合测试，从strs中取出2个元素的所有不同组合，返回结果
	 */
	public List<List<String>> combine2(List<String> strs) {
		List<List<String>> results = new ArrayList<>();
		int n = strs.size();
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				List<String> comb = new ArrayList<>();
				comb.add(strs.get(i));
				comb.add(strs.get(j));
				results.add(comb);
			}
		}
		return results;
	}
	
	//@Test
	public void combineTest2() {
		List<String> strs = new ArrayList<>();
		strs.add("1"); strs.add("2"); strs.add("3"); strs.add("4");
		List<List<String>> results = combine2(strs);
		System.out.println(results.size()); // 6
		System.out.println(results);  // [[1, 2], [1, 3], [1, 4], [2, 3], [2, 4], [3, 4]]
	}
	
	/**
	 * 从n个元素的集合中，取出m个元素的所有不同组合（与顺序无关，即顺序不同，包含元素相同即可）。
	 * 我们扫描每一个元素，针对该元素，我们可以将其放到组合集中，然后在剩下的n-1个元素中再选择m-1个元素；
	 * 我们也可以不放该元素进集合，而直接从剩下的n-1个元素中选择m个元素。
	 * C(n,m) = C(n-1,m-1) + C(n-1,m)    (0<m<n时)
	 * C(n,m) = 1                        (n=0或m=0或n=m时)
	 * C(n,m) = n!/m!*(n-m)!
	 */
	public int combine3(int n,int m) {
		if (n == 0 || m == 0 || n == m ){
			return 1;
		}
		else // 0 < m < n 
		{
			return combine3(n-1,m-1) + combine3(n-1,m);   
		}
	}
	
	//@Test
	public void combineTest3() {
		// 组合数
		System.out.println("combine3(4,3)= " + combine3(4,3));  // 4
		// 组合数
		System.out.println("combine3(4,2)= " + combine3(4,2));  // 6
	}
	
	/**
	 * 从n个元素的集合中，取出m个元素的所有不同组合（与顺序无关，即顺序不同，包含元素相同即可）。
	 * 我们扫描每一个元素，针对该元素，我们可以将其放到组合集中，然后在剩下的n-1个元素中再选择m-1个元素；
	 * 我们也可以不放该元素进集合，而直接从剩下的n-1个元素中选择m个元素。
	 * C(n,m) = C(n-1,m-1) + C(n-1,m)    (0<m<n时)
	 * C(n,m) = 1                        (n=0或m=0或n=m时)
	 * C(n,m) = n!/m!*(n-m)!
	 * @param n 集合总数，初值为strs的长度，strs.size()
	 * @param m 取出元素数，即从n个元素中取出m个元素
	 * @param strs 元素集合
	 * @param results 返回组合结果，从strs中取出m个元素的所有组合的集合，组合结果降序排列
	 * @param tmp 临时变量，存取每次取得的组合结果
	 */
	public void combine4(int n,int m,List<String> strs, List<List<String>> results,List<String> tmp) {
		if (m == 0 ) { // 取得一次组合结果
			//System.out.println("tmp = " + tmp);
			List<String> tmp1 = new ArrayList<>(tmp);
			results.add(tmp1);
			return;
		}
		if (n > 0) {
			String s = strs.get(n-1);  // 最后一个元素（索引为n-1）
			tmp.add(s);                // s添加到组合结果中
			combine4(n-1,m-1,strs,results,tmp);   // 因为s在组合结果中，因此执行：在n-1中选每个m-1个元素
			tmp.remove(s);
			combine4(n-1,m,strs,results,tmp);     // 因为s不在组合结果中，因此执行：在n-1中选m个元素
		}
	}
	
	//@Test
	public void combineTest4() {
		List<String> strs = new ArrayList<>();
		strs.add("1"); strs.add("2"); strs.add("3"); strs.add("4");
		List<List<String>> results = new ArrayList<>();
		List<String> tmp = new ArrayList<>();
		combine4(strs.size(),2,strs,results,tmp);
		
		System.out.println(results.size()); // 6
		// combine4()结果
		System.out.println(results);  // [[4, 3], [4, 2], [4, 1], [3, 2], [3, 1], [2, 1]] 
				
		// combine5()结果
		//System.out.println(results);  // [[1, 2], [1, 3], [1, 4], [2, 3], [2, 4], [3, 4]]
	}
	
	/**
	 * 从n个元素的集合中，取出m个元素的所有不同组合（与顺序无关，即顺序不同，包含元素相同即可）。
	 * 我们扫描每一个元素，针对该元素，我们可以将其放到组合集中，然后在剩下的n-1个元素中再选择m-1个元素；
	 * 我们也可以不放该元素进集合，而直接从剩下的n-1个元素中选择m个元素。
	 * C(n,m) = C(n-1,m-1) + C(n-1,m)    (0<m<n时)
	 * C(n,m) = 1                        (n=0或m=0或n=m时)
	 * C(n,m) = n!/m!*(n-m)!
	 * @param selected 模拟选取的元素索引，初值设为0，组合结果升序排列
	 * @param n 集合总数，，初值为strs的长度，strs.size()
	 * @param m 取出元素数，即从n个元素中取出m个元素
	 * @param strs 元素集合
	 * @param results 返回组合结果，从strs中取出m个元素的所有组合的集合，selected初值设为0，组合结果升序排列。
	 * @param tmp 临时变量，存取每次取得的组合结果
	 */
	public void combine5(int selected, int n,int m,List<String> strs, List<List<String>> results,List<String> tmp) {
		if (m == 0 ) { // 取得一次组合结果
			//System.out.println("tmp = " + tmp);
			List<String> tmp1 = new ArrayList<>(tmp);
			results.add(tmp1);
			return;
		}
		if (n > 0) {
			String s = strs.get(selected);  // 模拟选取的元素（索引为selected）
			tmp.add(s);                     // s添加到组合结果中
			combine5(selected+1,n-1,m-1,strs,results,tmp);   // 因为s在组合结果中，因此执行：在n-1中选每个m-1个元素
			tmp.remove(s);
			combine5(selected+1,n-1,m,strs,results,tmp);     // 因为s不在组合结果中，因此执行：在n-1中选m个元素
		}
	}
	
	//@Test
	public void combineTest5() {
		List<String> strs = new ArrayList<>();
		strs.add("1"); strs.add("2"); strs.add("3"); strs.add("4");
		List<List<String>> results = new ArrayList<>();
		List<String> tmp = new ArrayList<>();
		
		// selected 模拟选取的元素索引，初值设为0，组合结果升序排列
		combine5(0,strs.size(),2,strs,results,tmp);
		System.out.println(results.size()); // 6
		
		// combine4()结果
		//System.out.println(results);  // [[4, 3], [4, 2], [4, 1], [3, 2], [3, 1], [2, 1]] 
		
		// combine5()结果
		System.out.println(results);  // [[1, 2], [1, 3], [1, 4], [2, 3], [2, 4], [3, 4]]
	}
	
	/**
	 * 从n个元素的集合中，取出m个元素的所有不同组合（与顺序无关，即顺序不同，包含元素相同即可）。
	 * 我们扫描每一个元素，针对该元素，我们可以将其放到组合集中，然后在剩下的n-1个元素中再选择m-1个元素；
	 * 我们也可以不放该元素进集合，而直接从剩下的n-1个元素中选择m个元素。
	 * C(n,m) = C(n-1,m-1) + C(n-1,m)    (0<m<n时)
	 * C(n,m) = 1                        (n=0或m=0或n=m时)
	 * C(n,m) = n!/m!*(n-m)!
	 * @param selected 模拟选取的元素索引，初值设为0，组合结果升序排列
	 * @param n 集合总数，初值为strs的长度，strs.size()
	 * @param m 取出元素数，即从n个元素中取出m个元素
	 * @param strs 元素集合
	 * @param results 返回组合结果，从strs中取出m个元素的所有组合的集合，组合结果降序排列
	 * @param tmp 临时变量，存取每次取得的组合结果
	 */
	List<String> tmp = new ArrayList<>();
	public void combine6(int selected,int n,int m,List<String> strs, List<List<String>> results) {
		if (m == 0 ) { // 取得一次组合结果
			//System.out.println("tmp = " + tmp);
			List<String> tmp1 = new ArrayList<>(tmp);
			results.add(tmp1);
			return;
		}
		if (n > 0) {
			for (int i = selected; i < strs.size(); i++) {
				String s = strs.get(i);  // 最后一个元素（索引为n-1）
				tmp.add(s);                // s添加到组合结果中
				combine6(i+1,n-1,m-1,strs,results);   // 因为s在组合结果中，因此执行：在n-1中选每个m-1个元素
				tmp.remove(s);
				//combine6(i+1,n-1,m,strs,results);     // 因为s不在组合结果中，因此执行：在n-1中选m个元素
			}
		}
	}
	
	//@Test
	public void combineTest6() {
		List<String> strs = new ArrayList<>();
		strs.add("1"); strs.add("2"); strs.add("3"); strs.add("4");
		List<List<String>> results = new ArrayList<>();
		
		// selected 模拟选取的元素索引，初值设为0，组合结果升序排列
		combine6(0,strs.size(),2,strs,results);
		
		System.out.println(results.size()); // 6
		// combine4()结果
		// System.out.println(results);  // [[4, 3], [4, 2], [4, 1], [3, 2], [3, 1], [2, 1]] 
				
		// combine5()结果
		//System.out.println(results);  // [[1, 2], [1, 3], [1, 4], [2, 3], [2, 4], [3, 4]]
		
		// combine6()结果与combine5()相同,但是combine4()、combine5()更简洁
		System.out.println(results);  // [[1, 2], [1, 3], [1, 4], [2, 3], [2, 4], [3, 4]]
	}
	
	/*************************************************************
	 * ***********************************************************
	 * 从n个元素的集合中，取出m个元素的所有不同组合
	 * 结论：combine6()结果与combine5()相同,但是combine4()、combine5()更简洁
	 * 优先采用combine4(),参数tmp可以作为成员变量。
	 * ***********************************************************
	**************************************************************/
	
	/*******************************************************
	 * 获取min到max之间的随机整数，包含min和max
	 */
	//@Test
	public void testRandom() {
		int max=20;
	    int min=10;
	    Random random = new Random();
	    for (int i = 0; i < 20; i++) {
	    	int s = random.nextInt(max)%(max-min+1) + min;
	    	System.out.print(s + ",");  // 包含max和min,及其之间的值， 即10 ~ 20之间的整数，包含10和20
	    }
	    System.out.println();
	    for (int i = 0; i < 20; i++) {
	    	int s = random.nextInt(max)%(max-min+1) + min;
	    	System.out.print(s + ",");
	    }
	}
	
	/*******************************************************
	 * random.nextInt(10)： 获取0到9之间的随机整数，包含0和9，不包含10
	 */
	//@Test
	public void testRandom1() {
		Random random = new Random();
	    for (int i = 0; i < 10; i++) {
	    	System.out.print(random.nextInt(10) + ",");  // 0 ~ 9，包含0，不包含10
	    }
	    System.out.println();
	    
	    for (int i = 0; i < 10; i++) {
	    	System.out.print(random.nextInt(10) + 1 + ",");  // 1 ~ 10，包含1和10
	    }
	    System.out.println();
	}
	
	// 生成完全有向图，从而生成S3PR，并计算s3pr.algorithm4_3(true)
	//@Test
	public void complteGraph() {
		int vertexNum = 3; // 顶点数
		Graph<String> rGraph = S3PR.completeGraph(vertexNum);
		// edgeNum = vertexNumvertexNum*(vertexNum-1)
		assertEquals(vertexNum*(vertexNum-1), rGraph.getEdgeCount());
		System.out.println(rGraph);
		
		S3PR s3pr = S3PR.RGraphToS3PR(rGraph);
		System.out.println("s3pr:" + s3pr);
		s3pr.algorithm4_3(true);  // Cmatrix rank,alpha,delta = 3,6,3
	}
	
	/***
	 * 生成资源有向图，从而生成S3PR，并计算s3pr.algorithm4_3(true)
	 * <------- r2 <------..<----- r1 <------. 
	 * |                  ||                 |
	 * t1 -----> p2 ----> t2 -----> p3 ---> t3
	 * |                                     |
	 * <----------------- p1 <---------------.
	 */
	//@Test
	public void testRGraphToS3PR1() {
		Graph<String> rGraph = new Graph<>();
		rGraph.addVertex("r1"); rGraph.addVertex("r2");
		try {
			rGraph.addEdge("r1", "r2");
		} catch (VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("rGraph:" + rGraph);
		
		S3PR s3pr = S3PR.RGraphToS3PR(rGraph);
		System.out.println("s3pr:" + s3pr);
		
		s3pr.algorithm4_3(true);  // Cmatrix rank,alpha,delta = 0,0,0
	}
	
	/***
	 * 随机生成资源有向图，从而生成S3PR，并计算s3pr.algorithm4_3(true)
	 * <------- r2 <------..<----- r1 <------. 
	 * |                  ||                 |
	 * t1 -----> p2 ----> t2 -----> p3 ---> t3
	 * |                                     |
	 * <----------------- p1 <---------------.
	 */
	//@Test
	public void testRGraphToS3PR1_auto() {
		Graph<String> rGraph = S3PR.createRGraph(2, 0.5f);
		System.out.println("rGraph:" + rGraph);
		S3PR s3pr = S3PR.RGraphToS3PR(rGraph);
		System.out.println("s3pr:" + s3pr);
		
		s3pr.algorithm4_3(true);  // Cmatrix rank,alpha,delta = 0,0,0
	}
	
	/***
	 * 生成资源有向图，从而生成S3PR，并计算s3pr.algorithm4_3(true)
	 * t6 <---- p6 <----- t5 <---- p5 <-----t4
	 * |                  ||                 |
	 * -------> r2 ------>..-----> r1 ------>.
	 * <------- r2 <------..<----- r1 <------. 
	 * |                  ||                 |
	 * t1 -----> p2 ----> t2 -----> p3 ---> t3
	 * |                                     |
	 * <----------------- p1 <---------------.
	 */
	//@Test
	public void testRGraphToS3PR2() {
		Graph<String> rGraph = new Graph<>();
		rGraph.addVertex("r1"); rGraph.addVertex("r2");
		try {
			rGraph.addEdge("r1", "r2");
			rGraph.addEdge("r2", "r1");
		} catch (VertexNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("rGraph:" + rGraph);
		
		S3PR s3pr = S3PR.RGraphToS3PR(rGraph);
		System.out.println("s3pr:" + s3pr);
		
		s3pr.algorithm4_3(true);
		/***
		 * Siphon:      [r1[r1], r2[r2], p3[p3], p6[p6]]
           SiphonCom:   [p2[p2], p5[p5]]
           C-Matrix:
           [0]    0   1   0   0   1   0   0   0
           Cmatrix rank,alpha,delta = 1,2,1
		 */
	}
	
	/***
	 * 随机生成资源有向图，从而生成S3PR，并计算s3pr.algorithm4_3(true)
	 * t6 <---- p6 <----- t5 <---- p5 <-----t4
	 * |                  ||                 |
	 * -------> r2 ------>..-----> r1 ------>.
	 * <------- r2 <------..<----- r1 <------. 
	 * |                  ||                 |
	 * t1 -----> p2 ----> t2 -----> p3 ---> t3
	 * |                                     |
	 * <----------------- p1 <---------------.
	 */
	//@Test
	public void testRGraphToS3PR2_auto() {
		//Graph<String> rGraph = S3PR.createRGraph(3, 1.0f);
		Graph<String> rGraph = S3PR.createRGraph(30, 1.0f);
		System.out.println("rGraph:" + rGraph);
		
		S3PR s3pr = S3PR.RGraphToS3PR(rGraph);
		System.out.println("s3pr:" + s3pr);
		
		System.out.println("=======================");
		s3pr.algorithm4_3(true);
		//s3pr.algorithm4_3(false);
		// 非verbose模式，不显示计算过程
		InvariantMatrix cmatrix = s3pr.algorithm4_3(false);
		//System.out.println("\nC-Matrix:");
		//cmatrix.print(2, 0);
		int rank_alpha_delta[] = s3pr.rank_alpha_delta(cmatrix); // rank([C])<=δ<=α
		System.out.println("Cmatrix rank,alpha,delta = " + 
						rank_alpha_delta[0] + "," + rank_alpha_delta[1] + "," + rank_alpha_delta[2]);
	}
	
}
