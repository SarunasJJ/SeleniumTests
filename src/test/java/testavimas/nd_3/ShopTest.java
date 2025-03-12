package testavimas.nd_3;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.*;
import java.time.Duration;

public class ShopTest{

    private WebDriver driver;
    private static WebDriverWait wait;
    private static String email;
    private static String password;
    
    @BeforeEach
    public void setUp() {
    	WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    public void tearDown() {
    	if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void registerUser() {
        driver.get("https://demowebshop.tricentis.com/");
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Log in"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[value='Register']"))).click();

        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"gender-male\"]"))).click();
        driver.findElement(By.xpath("//*[@id=\"FirstName\"]")).sendKeys("Test");
        driver.findElement(By.xpath("//*[@id=\"LastName\"]")).sendKeys("User");
        
        email = "testuser" + System.currentTimeMillis() + "@example.com";
        password = "Password123";
        
        driver.findElement(By.xpath("//*[@id=\"Email\"]")).sendKeys(email);
        driver.findElement(By.xpath("//*[@id=\"Password\"]")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"ConfirmPassword\"]")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"register-button\"]")).click();

        driver.findElement(By.cssSelector("input[value='Continue']")).click();
        
        System.out.println(email);
        System.out.println(password);
    }

    
    @Test
    @Order(2)
    public void test1() throws IOException {
        runTest("data1.txt");
    }

    @Test
    @Order(3)
    public void test2() throws IOException {
        runTest("data2.txt");
    }

    private void runTest(String dataFile) throws IOException {
        driver.get("https://demowebshop.tricentis.com/");
        
        //Login
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Log in"))).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"Email\"]"))).sendKeys(email);
        driver.findElement(By.xpath("//*[@id=\"Password\"]")).sendKeys(password);
        System.out.println(email);
        System.out.println(password);
        driver.findElement(By.cssSelector("input[value='Log in']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Digital downloads"))).click();
        
        //Add products

        try (BufferedReader br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource(dataFile).getFile()))) {
            String productName;
            while ((productName = br.readLine()) != null) {
                System.out.println("Adding product to cart: " + productName);

                WebElement addToCartButton = driver.findElement(By.xpath(
                    "//div[@class='product-item' and contains(., '" + productName + "')]//input[@value='Add to cart']"
                ));
                addToCartButton.click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loading-overlay"))); // Adjust the locator as needed
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Shopping cart"))).click();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"termsofservice\"]"))).click();
        driver.findElement(By.xpath("//*[@id=\"checkout\"]")).click();

        if(driver.findElements(By.xpath("//*[@id=\"billing-address-select\"]")).size() > 0){
        	wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'new-address-next-step-button')]")))
            .click();
        } else if(driver.findElements(By.xpath("//*[@id=\"BillingNewAddress_CountryId\"]")).size() > 0) {
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"BillingNewAddress_CountryId\"]")));
	        WebElement countryDropdown = driver.findElement(By.xpath("//*[@id=\"BillingNewAddress_CountryId\"]"));
	        Select countrySelect = new Select(countryDropdown);
	        countrySelect.selectByVisibleText("United States");
	        driver.findElement(By.xpath("//*[@id=\"BillingNewAddress_City\"]")).sendKeys("New York");
	        driver.findElement(By.xpath("//*[@id=\"BillingNewAddress_Address1\"]")).sendKeys("Abc 123");
	        driver.findElement(By.xpath("//*[@id=\"BillingNewAddress_ZipPostalCode\"]")).sendKeys("12345");
	        driver.findElement(By.xpath("//*[@id=\"BillingNewAddress_PhoneNumber\"]")).sendKeys("123456789");
	        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'new-address-next-step-button')]"))).click();
        }
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'payment-method-next-step-button')]"))).click();
        System.out.println("Payment method pressed");
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'payment-info-next-step-button')]"))).click();
	    System.out.println("Payment info pressed");
	    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"checkout-confirm-order-load\"]")));
	    
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'confirm-order-next-step-button')]")));
	    WebElement confirmButton = driver.findElement(By.xpath("//input[contains(@class, 'confirm-order-next-step-button')]"));
	    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmButton);
	    confirmButton.click();
	    System.out.println("Confirm pressed");
	    
	    WebElement orderConfirmation = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'section order-completed')]")));
        Assertions.assertTrue(orderConfirmation.isDisplayed());
        
    }
}