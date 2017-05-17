package cn.edu.hfut.dmic.webcollector.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Created by song on 2015/12/16.
 */
public class FirstExample {
    public static void main(String[] args) {
        System.setProperty("webdriver.firefox.bin","F:\\software\\Firefox\\firefox.exe");
        WebDriver driver = new FirefoxDriver();
        ((JavascriptExecutor)driver).executeScript("alert(\"hello,this is a alert!\")", null);
//        WebDriver driver =new HtmlUnitDriver();
//        driver.get("httP://www.baidu.com");
//        WebElement search = driver.findElement(By.name("wd"));
//        search.sendKeys("");
//        search.submit();
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Page title is: " + driver.getTitle());
//        driver.quit();
    }
}
