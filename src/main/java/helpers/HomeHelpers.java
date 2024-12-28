package helpers;

import locator.HomeLocators;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomeHelpers extends HomeLocators {
    WebDriver driver;
    public HomeHelpers(WebDriver browser){
        driver = browser;
    }

    public void acceptCookie(){
        WebElement accept = driver.findElement(ACCEPT_COOKIE);
        accept.click();
    }


    public String getLocle(){
        WebElement locale = driver.findElement(HTML_LANG);
        return locale.getAttribute("lang");
    }
    public void clickOpinion(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement opinionElement = driver.findElement(OPINION_LINK);
        js.executeScript("arguments[0].click();", opinionElement);
    }
}
