package br.com.fiap.hackathon.reservafacil.helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class OAuthHelper {
    private WebDriver driver;
    private WebDriverWait wait;

    private By loginCns = By.name("username");
    private By loginPassword = By.name("password");
    private By loginButton = By.xpath("//button[@type='submit']");

    public OAuthHelper() throws Exception {
        this.driver = ChromeWebDriver.getChromeDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private String getBaseUrl(String port, String client_id, String redirect_uri) {
        return String.format(
                "http://localhost:%s/oauth2/authorize?response_type=code&client_id=%s&scope=read write&redirect_uri=%s",
                port,
                client_id,
                URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8)
        );
    }

    private void login(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        driver.findElement(loginCns).sendKeys(username);
        driver.findElement(loginPassword).sendKeys(password);
        driver.findElement(loginButton).click();

        wait.until(ExpectedConditions.urlContains("?code="));
    }

    private void setup() {
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.manage().window().maximize();

    }

    public String getOAuthToken(
            String username,
            String password,
            String client_id,
            String redirect_uri,
            String port
    ) {
        String currentUrl = "";
        try {
            setup();
            String baseUrl = getBaseUrl(port, client_id, redirect_uri);
            System.out.println(baseUrl);
            driver.navigate().to(baseUrl);
            login(username, password);
            currentUrl = driver.getCurrentUrl();

            System.out.println(currentUrl);

        } finally {
            if(driver != null) {
                driver.quit();
            }
        }

        return parseToken(currentUrl);
    }

    private String parseToken(String code) {
        String authCode = "";

        if(code.contains("=")) {
            authCode = code.split("=")[1];
        }

        return authCode;
    }
}
