package runners;


import com.vimalselvam.cucumber.listener.Reporter;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import properties.BaseProperties;

import java.io.File;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepDefinitions"},
        plugin = {"com.vimalselvam.cucumber.listener.ExtentCucumberFormatter:target/cucumber-html-reports/ExtentReport.html",
                "pretty", "json:target/cucumber-json-reports/Cucumber.json",
                "junit:target/cucumber-xml-reports/Cucumber.xml"},
        monochrome = true,
        tags="@Pagination"
)
public class TestRunner {

    @AfterClass
    public static void writeExtentReport() {
        Reporter.loadXMLConfig(new File(BaseProperties.REPORT_CONFIG_PATH));
        Reporter.setSystemInfo("User Name", System.getProperty("user.name"));
        Reporter.setSystemInfo("Time Zone", System.getProperty("user.timezone"));
        Reporter.setSystemInfo("Machine", "Windows 10" + "64 Bit");
        Reporter.setSystemInfo("RestAssured", "4.3.3");
        Reporter.setSystemInfo("UniRest", "1.4.9");
        Reporter.setSystemInfo("Junit", "4.12");
        Reporter.setSystemInfo("Cucumber", "1.2.5");
        Reporter.setSystemInfo("Maven", "3.7.0");
        Reporter.setSystemInfo("Java Version", "11.0.10");
    }

}
