import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class KiwiduckElementsTest {
    private final String mainPageUrl = "https://kiwiduck.github.io/";
    private WebDriver webDriver;

    @BeforeClass
    public void setupBeforeClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setupBeforeMethod() {
        webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        webDriver.quit();
    }

    @Test
    public void testPages() {
        webDriver.get(mainPageUrl);
        // тест страницы Prompt, Alert and Confirm
        testPromptAlertConfirmPage();
        // тест страницы Table
        testTablePage();
    }

    private void testPromptAlertConfirmPage() {
        testWhenEnteredPasswordIsCorrect();
        testWhenEnteredPasswordIsNotCorrect();
    }

    private void testWhenEnteredPasswordIsCorrect() {
        // переходим на страницу Prompt, Alert and Confirm
        webDriver.get(mainPageUrl + "alerts");

        // находим кнопку с текстом "Get password" и нажимаем ее
        findAndClickButton("Get password");
        // переключаемся во всплывающее окно Alert с паролем
        Alert alert = webDriver.switchTo().alert();
        // получаем пароль из текста во всплывающем окне, убирая фразу "Your password: "
        String password = alert.getText().replace("Your password: ", "");
        alert.accept();
        // находим кнопку с текстом "Enter password" и нажимаем ее
        findAndClickButton("Enter password");
        // переключаемся во всплывающее окно Prompt для ввода полученного пароля
        alert = webDriver.switchTo().alert();
        // вводим полученный пароль
        alert.sendKeys(password);
        alert.accept();
        // проверяем, что появился текст "Great!"
        testLabelIsDisplayed("Great!");
        // проверяем, что появилась кнопка "Return to menu" и нажимаем на нее
        findAndClickButton("Return to menu");
        // подтвеждаем действие нажатием ОК во всплывающем окне Confirm
        alert = webDriver.switchTo().alert();
        alert.accept();
    }

    private void testWhenEnteredPasswordIsNotCorrect() {
        // переходим на страницу Prompt, Alert and Confirm
        webDriver.get(mainPageUrl + "alerts");

        // находим кнопку с текстом "Get password" и нажимаем ее
        findAndClickButton("Get password");
        // переключаемся во всплывающее окно Alert с паролем
        Alert alert = webDriver.switchTo().alert();
        // получаем весь текст из всплывающего окна вместе с фразой "Your password: "
        String password = alert.getText();
        alert.accept();
        // находим кнопку с текстом "Enter password" и нажимаем ее
        findAndClickButton("Enter password");
        // переключаемся во всплывающее окно Prompt для ввода полученного пароля
        alert = webDriver.switchTo().alert();
        // вводим полученный текст в поле ввода пароля
        alert.sendKeys(password);
        alert.accept();
        // проверяем, что текст "Great!" не появился
        assertTrue(webDriver.findElements(By.xpath("//label[text()='Great!']")).isEmpty());
        // проверяем, что кнопка "Return to menu" тоже не появилась
        assertTrue(webDriver.findElements(By.xpath("//button[text()='Return to menu']")).isEmpty());
    }

    private void testTablePage() {
        // переходим на страницу Table
        webDriver.get(mainPageUrl + "table");

        // находим чек-боксы с индексами 3 и 4 и отмечаем их
        findAndClickCheckBox(3);
        findAndClickCheckBox(4);
        // находим кнопку с текстом "Delete" и нажимаем её
        findAndClickInputOfTypeButton("Delete");
        // добавляем записи в таблицу
        addRecordToTable("Ernst Handel", "Roland Mendel", "Austria");
        addRecordToTable("Island Trading", "Helen Bennett", "UK");
        // проверяем, что появилась ссылка "Great! Return to menu" и нажимаем на неё
        findAndClickLink("Great! Return to menu");
    }

    private void checkWebElement(WebElement webElement) {
        assertTrue(webElement.isDisplayed());
        assertTrue(webElement.isEnabled());
    }

    private void addRecordToTable(String companyName, String contactName, String countryName) {
        enterTextIntoInputField("Company", companyName);
        enterTextIntoInputField("Contact", contactName);
        enterTextIntoInputField("Country", countryName);
        // находим кнопку с текстом "Add" и нажимаем её
        findAndClickInputOfTypeButton("Add");
    }

    private void enterTextIntoInputField(String fieldName, String fieldValue) {
        // находим поле ввода
        WebElement webElement = findFollowingElement(fieldName, "input[@type='text']");
        checkWebElement(webElement);
        // вводим в поле ввода текст fieldValue
        webElement.sendKeys(fieldValue);
    }

    private WebElement findFollowingElement(String precedingElementName, String followingElementXPath) {
        return webDriver.findElement(By.xpath(String.format("//label[text()='%s']/following-sibling::%s",
                precedingElementName, followingElementXPath)));
    }

    private void testLabelIsDisplayed(String expectedText) {
        WebElement label = webDriver.findElement(By.xpath(String.format("//label[text()='%s']", expectedText)));
        assertTrue(label.isDisplayed());
    }

    private void findAndClickButton(String buttonText) {
        WebElement button = webDriver.findElement(By.xpath(String.format("//button[text()='%s']", buttonText)));
        checkWebElement(button);
        button.click();
    }

    private void findAndClickInputOfTypeButton(String buttonText) {
        WebElement button = webDriver.findElement(By.xpath(String.format("//input[@type='button' and @value='%s']", buttonText)));
        checkWebElement(button);
        button.click();
    }

    private void findAndClickCheckBox(Integer checkBoxOrderNum) {
        List<WebElement> checkBoxes = webDriver.findElements(By.xpath("//input[@type='checkbox']"));
        assertTrue(checkBoxes.size() >= checkBoxOrderNum);
        WebElement checkBox = checkBoxes.get(checkBoxOrderNum - 1);
        checkWebElement(checkBox);
        checkBox.click();
    }

    private void findAndClickLink(String linkText) {
        WebElement link = webDriver.findElement(By.linkText(linkText));
        assertTrue(link.isDisplayed());
        link.click();
    }
}