package locator;

import org.openqa.selenium.By;
public class HomeLocators {
    public static final By HTML_LANG = By.tagName("html");
    public static final By ACCEPT_COOKIE = By.id("didomi-notice-agree-button");
    public static final By EDITION_HEAD = By.xpath("//li[@id='edition_head']//span");
    public static final By OPINION_LINK = By.xpath("//a[text()='Opinión']");
}
