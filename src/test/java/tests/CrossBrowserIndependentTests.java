package tests;

import helpers.HomeHelpers;
import helpers.OpinionHelpers;
import helpers.TranslationHelper;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CrossBrowserIndependentTests {
    WebDriver driver;
    WebDriverWait wait;
    MutableCapabilities capabilities;
    String userName, accessKey, browserStackUrl;
    String url, webLocale;
    int articleCount, waitTime;
    Properties properties = new Properties();
    HomeHelpers homeHelpers;
    OpinionHelpers opinionHelpers;

    @BeforeClass
    @Parameters({"browser", "os", "os_version", "device"})
    public void setup(String browser, String os, String os_version, String device) throws IOException {
        FileInputStream fis = new FileInputStream("src/resources/settings.properties");
        properties.load(fis);

        userName = properties.getProperty("userName");
        accessKey = properties.getProperty("accessKey");
        browserStackUrl = "https://" + userName + ":" + accessKey + "@hub.browserstack.com/wd/hub";

        url = properties.getProperty("url");
        webLocale = properties.getProperty("locale");
        articleCount = Integer.parseInt( properties.getProperty("articleCount") );
        waitTime = Integer.parseInt( properties.getProperty("waitTime") );

        // W3C-compliant capabilities
        MutableCapabilities browserStackOptions = new MutableCapabilities();
        browserStackOptions.setCapability("os", os);
        browserStackOptions.setCapability("osVersion", os_version);
        browserStackOptions.setCapability("projectName", "BrowserStack Selenium Test");
        browserStackOptions.setCapability("buildName", "Build 1");
        browserStackOptions.setCapability("sessionName", "Test Session");
        browserStackOptions.setCapability("debug", true);
        // Include `deviceName` only for mobile testing
        if ( (os.equals("android") || os.equals("ios")) && device != null && !device.isEmpty()) {
            browserStackOptions.setCapability("deviceName", device); // Mobile-specific
        }

        capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", "chrome");
        capabilities.setCapability("bstack:options", browserStackOptions);
    }

    @BeforeMethod
    public void loadWebpage() throws MalformedURLException {
//         Initialize RemoteWebDriver
        driver = new RemoteWebDriver(new URL(browserStackUrl), capabilities);

        wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));

        homeHelpers = new HomeHelpers(driver);
        opinionHelpers = new OpinionHelpers(driver);

        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(homeHelpers.ACCEPT_COOKIE));
        homeHelpers.acceptCookie();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void verifyWebsiteInSpanish(){
        //Fetching Browser text from HTML Lang Attribute
        String locale = homeHelpers.getLocle();
        Assert.assertEquals(locale,webLocale,"Language should match");
    }

    @Test
    public void fetchArticleFromOpinion(){
        //Click Opinion Link
        homeHelpers.clickOpinion();
        wait.until(ExpectedConditions.visibilityOfElementLocated(opinionHelpers.OPINION_HEADER));
        //Fetch First n mentioned articles
        String[] arr = opinionHelpers.getFirstnArticles(articleCount);
        for(String heading : arr) System.out.println(heading);
    }

    @Test
    public void translateArticleHeader() throws IOException{
        TranslationHelper translationHelper = new TranslationHelper();
//        Click Opinion Link
        homeHelpers.clickOpinion();
        wait.until(ExpectedConditions.visibilityOfElementLocated(opinionHelpers.OPINION_HEADER));
        //Fetch First n mentioned articles
        String[] arr = opinionHelpers.getFirstnArticlesHeader(articleCount);
        for(String header : arr) {
            System.out.println(header);
            System.out.println( translationHelper.translateText(header).join() );
        }
    }

    @Test
    public void analyzeHeader() throws IOException {
        HashMap<String,Integer> map = new HashMap<>();
        TranslationHelper translationHelper = new TranslationHelper();
//        Click Opinion Link
        homeHelpers.clickOpinion();
        wait.until(ExpectedConditions.visibilityOfElementLocated(opinionHelpers.OPINION_HEADER));
        String[] arr = opinionHelpers.getFirstnArticlesHeader(articleCount);
        String[] translatedTexts = new String[articleCount];
        int i=0;
        for(String s : arr){
            translatedTexts[i++] = translationHelper.translateText(s).join();
        }
        for(String translatedText : translatedTexts){
            translatedText = translatedText.toLowerCase();
            String sanitized = translatedText.replaceAll("[^a-zA-Z0-9\\s]", "");
            String[] words = sanitized.split("\\s+");
            for(String word : words){
                map.put(word, map.getOrDefault(word,0)+1);
            }
        }
        //print the repeated words
        for(Map.Entry<String,Integer> entry : map.entrySet()){
            if(entry.getValue()>1){
                System.out.println(entry.getKey()+"  "+entry.getValue());
            }
        }
    }
}
