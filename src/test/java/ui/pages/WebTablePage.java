package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class WebTablePage {

    private final Page page;
    private static final String URL = "https://demoqa.com/webtables";

    public WebTablePage(Page page) {
        this.page = page;
    }

    public WebTablePage navigate() {
        page.navigate(URL);
        removeAdsAndOverlays();
        return this;
    }

    public WebTablePage clickAdd() {
        page.locator("#addNewRecordButton").click();
        return this;
    }

    public WebTablePage fillFirstName(String name) {
        page.locator("#firstName").fill(name);
        return this;
    }

    public WebTablePage fillLastName(String name) {
        page.locator("#lastName").fill(name);
        return this;
    }

    public WebTablePage fillEmail(String email) {
        page.locator("#userEmail").fill(email);
        return this;
    }

    public WebTablePage fillAge(String age) {
        page.locator("#age").fill(age);
        return this;
    }

    public WebTablePage fillSalary(String salary) {
        page.locator("#salary").fill(salary);
        return this;
    }

    public WebTablePage fillDepartment(String department) {
        page.locator("#department").fill(department);
        return this;
    }

    public WebTablePage submitForm() {
        page.locator("#submit").click();
        return this;
    }

    public WebTablePage addRecord(String firstName, String lastName, String email,
                                  String age, String salary, String department) {
        clickAdd();
        fillFirstName(firstName);
        fillLastName(lastName);
        fillEmail(email);
        fillAge(age);
        fillSalary(salary);
        fillDepartment(department);
        submitForm();
        return this;
    }

    public WebTablePage search(String text) {
        page.locator("#searchBox").fill(text);
        return this;
    }

    public WebTablePage clearSearch() {
        page.locator("#searchBox").clear();
        return this;
    }

    public int getVisibleRowCount() {
        return page.locator("table tbody tr").count();
    }

    public String getCellText(int row, int col) {
        return page.locator("table tbody tr").nth(row)
                .locator("td").nth(col).textContent().trim();
    }

    public List<String> getRowData(int row) {
        return page.locator("table tbody tr").nth(row)
                .locator("td").allTextContents()
                .stream()
                .map(String::trim)
                .toList();
    }

    public WebTablePage editRecord(int row) {
        page.locator("table tbody tr").nth(row)
                .locator("[title='Edit']").click();
        return this;
    }

    public WebTablePage deleteRecord(int row) {
        page.locator("table tbody tr").nth(row)
                .locator("[title='Delete']").click();
        return this;
    }

    public List<String> getColumnHeaders() {
        return page.locator("table thead th").allTextContents()
                .stream()
                .map(String::trim)
                .filter(h -> !h.isEmpty())
                .toList();
    }

    public WebTablePage clickColumnHeader(String headerText) {
        page.locator("table thead th", new Page.LocatorOptions()
                .setHasText(headerText)).click();
        return this;
    }

    private void removeAdsAndOverlays() {
        page.evaluate("document.querySelectorAll('#adplus-anchor, #fixedban, footer, " +
                "#close-fixedban, .ad, iframe[id*=\"google\"]').forEach(e => e.remove())");
    }
}
