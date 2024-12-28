package tests;

import helpers.HomeHelpers;
import helpers.OpinionHelpers;
import helpers.TranslationHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BrowserSequentialTests {
    WebDriver driver;
    WebDriverWait wait;
    String userName, accessKey, browserStackUrl, url, webLocale;
    int articleCount, waitTime;
    Properties properties = new Properties();
    HomeHelpers homeHelpers;
    OpinionHelpers opinionHelpers;

    @BeforeClass
    public void setup() throws IOException {
        FileInputStream fis = new FileInputStream("src/resources/settings.properties");
        properties.load(fis);

        userName = properties.getProperty("userName");
        accessKey = properties.getProperty("accessKey");
        browserStackUrl = "https://" + userName + ":" + accessKey + "@hub.browserstack.com/wd/hub";
        url = properties.getProperty("url");
        webLocale = properties.getProperty("locale");
        articleCount = Integer.parseInt( properties.getProperty("articleCount") );
        waitTime = Integer.parseInt( properties.getProperty("waitTime") );

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));

        homeHelpers = new HomeHelpers(driver);
        opinionHelpers = new OpinionHelpers(driver);

        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(homeHelpers.ACCEPT_COOKIE));
        homeHelpers.acceptCookie();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void verifyWebsiteInSpanish(){
//        Fetching Browser text from HTML Lang Attribute
        String locale = homeHelpers.getLocle();
        Assert.assertEquals(locale,webLocale,"Language should match");
    }

    @Test(priority = 2)
    public void fetchArticleFromOpinion(){
        homeHelpers.clickOpinion();
        wait.until(ExpectedConditions.visibilityOfElementLocated(opinionHelpers.OPINION_HEADER));
//        Fetch First n mentioned articles using article tag
        String[] arr = opinionHelpers.getFirstnArticles(articleCount);
        for(String heading : arr) System.out.println(heading);
    }

    @Test(priority = 3)
    public void translateArticleHeader() throws IOException{
        TranslationHelper translationHelper = new TranslationHelper();
        wait.until(ExpectedConditions.visibilityOfElementLocated(opinionHelpers.OPINION_HEADER));
//        Fetch First n mentioned articles header using article tag header tag
        String[] arr = opinionHelpers.getFirstnArticlesHeader(articleCount);
        for(String header : arr) {
            System.out.println(header);
//            Translating using Rapid Api
            System.out.println( translationHelper.translateText(header).join() );
        }
    }

    @Test(priority = 4)
    public void analyzeHeader() throws IOException {
        HashMap<String,Integer> map = new HashMap<>();
        TranslationHelper translationHelper = new TranslationHelper();
        wait.until(ExpectedConditions.visibilityOfElementLocated(opinionHelpers.OPINION_HEADER));
        String[] arr = opinionHelpers.getFirstnArticlesHeader(articleCount);
        String[] translatedTexts = new String[articleCount];
        int i=0;
        for(String s : arr){
//            Translating using Rapid Api
            translatedTexts[i++] = translationHelper.translateText(s).join();
        }
        for(String translatedText : translatedTexts){
//            Converting to lowercase to avoid case sensitivity for counting
            translatedText = translatedText.toLowerCase();
//            Removing Special characters regex pattern for counting
            String sanitized = translatedText.replaceAll("[^a-zA-Z0-9\\s]", "");
//            Splitting Header to words based on white space
            String[] words = sanitized.split("\\s+");
            for(String word : words){
                map.put(word, map.getOrDefault(word,0)+1);
            }
        }
//        print the repeated words
        for(Map.Entry<String,Integer> entry : map.entrySet()){
            if(entry.getValue()>1){
                System.out.println(entry.getKey()+"  "+entry.getValue());
            }
        }
    }
}
