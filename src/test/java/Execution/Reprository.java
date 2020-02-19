package Execution;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Reprository {
	
	public WebDriver driver;
	Properties property;
	boolean resultFoundFirst = false;
	boolean resultFound = false;
	String href = "";
	int resultIndexOnPage = 0;
	
	public void Loadpropertyconfig() 
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
	
	public String getconfig(String key)
	{
		Loadpropertyconfig();
		  String x= property.getProperty(key);
		return x;
	}
	
	public void launch(String browse, String SText, String SResult, int pageCountLimit) throws Throwable
	{
		
		try {
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
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.get(getconfig("url"));
		
		checkSearch(getconfig(SText), getconfig(SResult), pageCountLimit);

		driver.close();
	}
	
	public void checkSearch(String keyword, String Refhref, int pageCounter) throws Throwable {
		try {

			waitTillElementVisible("//input[@name='q']",60);
			driver.findElement(By.xpath("//input[@name='q']")).sendKeys(keyword);
			
			waitTillElementVisible("//form[@id='tsf']/div[2]/div[1]/div[3]/center/input[1]",60);
			WebElement button = driver.findElement(By.xpath("//form[@id='tsf']/div[2]/div[1]/div[3]/center/input[1]"));

			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			jsExecutor.executeScript("arguments[0].click();", button);

			try {
				
			
				waitTillElementVisible("//div[@id='rso']/div[1]/descendant::a[@href='"+Refhref+"']",5);
				WebElement firstResult = driver.findElement(By.xpath("//div[@id='rso']/div[1]/descendant::a[@href='"+Refhref+"']"));
				//WebElement firstResult = driver.findElement(By.xpath("//div[@id='rso']/div[1]/descendant::link"));
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

			if(!resultFoundFirst) {
				Thread.sleep(5000);
				for (int index = 0; index < pageCounter; index++) { 
					boolean resultFound =  checkResultIndex(Refhref);
					if(resultFound) {
						System.out.println("Your result is on page: "+(index+1)+" on the index: "+resultIndexOnPage); 
						break;
					}
					else
					{
						driver.findElement(By.xpath(".//a/span[text()='Next']")).click();
						//continue looking on other pages
					}
					
				}
				if(!resultFound)
				{
					System.out.println("The search not found till page count: " +pageCounter);
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public boolean checkResultIndex(String Refhref) throws Throwable {
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
					System.out.println("Expected result not found on page: ");
				}
			}
			return resultFound;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	public boolean waitTillElementVisible(String xpath, int time) throws Exception{
		try {
			WebDriverWait wait = new WebDriverWait(driver,time);
		      WebElement locator = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		      return true;
		}catch(Exception e) {
			System.out.println("Element not found after "+time+" seconds of wait");
			return false;
		}
		
	}
}
