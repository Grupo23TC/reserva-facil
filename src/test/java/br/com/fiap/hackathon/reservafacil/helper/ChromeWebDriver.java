package br.com.fiap.hackathon.reservafacil.helper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.io.ResourceLoader;

import java.net.URL;
import java.nio.file.Paths;

public class ChromeWebDriver {
    public static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        return options;
    }

    public static WebDriver getChromeDriver() throws Exception {
        URL resource = ResourceLoader.class.getClassLoader().getResource("chromedriver.exe");

        String driverPath = Paths.get(resource.toURI()).toString();

        System.setProperty("webdriver.chrome.driver", driverPath);
        WebDriver driver = new ChromeDriver(getChromeOptions());

        return driver;
    }
}
