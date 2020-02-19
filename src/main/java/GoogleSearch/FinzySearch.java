package GoogleSearch;

import java.io.File;
import java.io.FileReader;
import java.rmi.activation.ActivateFailedException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.http.util.Asserts;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class FinzySearch {

	public static WebDriver driver;
	static Properties property;
	static boolean resultFoundFirst = false;
	static boolean resultFound = false;
	static String href = "";
	static int resultIndexOnPage = 0;
	
	public static void Loadpropertyconfig() 
	{
		try 
		{
			File f  = new File("./src/main/java/Configuration/Config.properties");
			FileReader fr = new FileReader(f);
			property = new Properties();
			property.load(fr);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}
	
	public static String getconfig(String key)
	{
		Loadpropertyconfig();
		  String x= property.getProperty(key);
		return x;
	}
	
	public void launch(String browse) throws Throwable
	{
		if(browse.equalsIgnoreCase("chrome"))
		{
			System.setProperty(getconfig("drivername"), getconfig("chromedriver"));
			driver=new ChromeDriver();
		}
		else if(browse.equalsIgnoreCase("firefox"))
			{
				System.setProperty(getconfig("drivernamefirefox"), getconfig("firefoxdriver"));
				driver=new FirefoxDriver();
			}
		driver.get("https://www.google.com");
		
		//checkSearch(getconfig("searchText_2"), getconfig("expResulthref_2"), 5);

		driver.close();
	}

	/*public static void main(String[] args) throws Throwable {
		
		
		System.setProperty("webdriver.chrome.driver", "/Users/babayega/Documents/Chrome_driver/chromedriver");

		driver = new ChromeDriver();

		driver.get("https://www.google.com");

		// driver.manage().window().maximize();

		String searchText = "finzy";
		String expResulthref = "https://finzy.com/";

		String searchText_1 = "finzy logic";
		String expResulthref_1 = "https://economictimes.indiatimes.com/topic/Finzy/news";
		
		String searchText_2 = "test abcd";
		String expResulthref_2 = "https://adespresso.com/guides/facebook-ads-optimization/ab-testing/abc";
		
		

		// checkSearch("test result 12334","https://finzy.com/");
		

	}*/

	public static void checkSearch(String keyword, String Refhref, int pageCounter) throws Throwable {
		try {

			Thread.sleep(5000);

			driver.findElement(By.name("q")).sendKeys(keyword);
			Thread.sleep(5000);

			WebElement button = driver.findElement(By.xpath("//form[@id='tsf']/div[2]/div[1]/div[3]/center/input[1]"));

			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			jsExecutor.executeScript("arguments[0].click();", button);

			try {
				WebElement firstResult = driver.findElement(By.xpath("//div[@id='rso']/div[1]/descendant::link"));
				href = firstResult.getAttribute("href");
				if (href.toLowerCase().contains(Refhref.toLowerCase())) {
					// Result found on index
					resultFoundFirst = true;
					System.out.println("Result is on the first index: " + resultFoundFirst);

				} else {
					// keep looking for result
				}

			} catch (NoSuchElementException nse) {
				// Continue for the search as first search is not required result
				System.out.println("Element not found on 1st Index");
			}

			
			for (int index = 0; index < pageCounter; index++) { 
				boolean resultFound =  checkResultIndex(Refhref);
				if(resultFound) {
					System.out.println("Your result is on page: "+(index+1)+" on the index: "+resultIndexOnPage); 
					break;
				}
				else
				{
					driver.findElement(By.xpath("//a/span[text()='Next']")).click();
					//continue looking on other pages
				}
				
			}
			if(!resultFound)
			{
				System.out.println("The search not found till page count: " +pageCounter);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public static boolean checkResultIndex(String Refhref) throws Throwable {
		try {

			if (!resultFoundFirst) {
				// Desired result not found continue looking for results
				List<WebElement> resultList = driver.findElements(By.xpath("//div[@id='rso']/div/div/div[@class='g']/div/div/div/a"));
				for (int index = 0; index < resultList.size(); index++) {
					href = resultList.get(index).getAttribute("href");
					if (href.toLowerCase().contains(Refhref.toLowerCase())) {
						// Result found on index
						System.out.println("Result index is " + (index + 2));
						resultIndexOnPage = (index + 2);
						resultFound = true;
						break;
					}
				}
				if (!resultFoundFirst & !resultFound) {
					// write the logic for next page
					System.out.println("Expected result not found on page");

				}
			}
			return resultFound;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
}
