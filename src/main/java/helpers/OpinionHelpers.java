package helpers;

import locator.OpinionLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class OpinionHelpers extends OpinionLocators {
    WebDriver driver;

    public OpinionHelpers(WebDriver browser){
        driver = browser;
    }

    public String[] getFirstnArticles(int n){
        //Fetch list of Webelements having Article Tag
        List<WebElement> articles = driver.findElements(ARTICLES_LIST);
        int size = articles.size();
        String[] articleList = new String[n];
        for(int i=0;i<n && n<size;i++){
            articleList[i] = articles.get(i).getText();
        }
        return articleList;
    }

    public String[] getFirstnArticlesHeader(int n){
        List<WebElement> articles = driver.findElements(ARTICLES_HEADER_LIST);
        int size = articles.size();
        String[] articleList = new String[n];
        for(int i=0;i<n && n<size;i++){
            articleList[i] = articles.get(i).getText();
        }
        return articleList;
    }
}
