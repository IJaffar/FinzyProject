package Execution;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestExecution extends Reprository{
	@Parameters({"browser","searchText","expResulthref","maxPageCount"} )
	@Test
	public void verify_launch_1(String browse, String S1, String R1, int pageCountLimit) throws Throwable
	{
		System.out.println("Session 1 "+Thread.currentThread().getId());
		launch(browse,S1,R1,pageCountLimit);
	}

}
