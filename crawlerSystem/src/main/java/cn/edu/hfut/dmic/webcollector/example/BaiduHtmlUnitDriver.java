package cn.edu.hfut.dmic.webcollector.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.concurrent.TimeUnit;

public class BaiduHtmlUnitDriver {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        WebDriver driver = new HtmlUnitDriver();
//        driver.get("http://gaokao.eol.cn/news/");
//        String str = (String)((JavascriptExecutor)driver).executeScript("var _PAGE_COUNT=25;var _PAGE_INDEX=0");
//        System.out.println( "=======================str : "+str );
//
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //打开百度首页
        driver.get("http://www.baidu.com/");
        //打印页面标题
        System.out.println("页面标题：" + driver.getTitle());
        //根据id获取页面元素输入框
        WebElement search = driver.findElement(By.name("wd"));
        //在id=“kw”的输入框输入“selenium”
        search.sendKeys("selenium");
        //根据id获取提交按钮
//        WebElement submit = driver.findElement(By.id("su"));
//        //点击按钮查询
//        submit.click();
//        //打印当前页面标题
//        System.out.println("页面标题：" + driver.getTitle());
//        //返回当前页面的url
//        System.out.println("页面url：" + driver.getCurrentUrl());
//        //返回当前的浏览器的窗口句柄
//        System.out.println("窗口句柄：" + driver.getWindowHandle());
//
//    }
    }
}
