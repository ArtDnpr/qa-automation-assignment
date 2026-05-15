package ui.pages;

import com.microsoft.playwright.Page;

public class WebTablePage {

    private final Page page;
    private static final String URL = "https://demoqa.com/webtables";

    // TODO: Iteration 4 — add locators and interaction methods
    // Locators will target: add button, edit/delete buttons, search box,
    // registration form fields, table rows, column headers for sorting

    public WebTablePage(Page page) {
        this.page = page;
    }

    public WebTablePage navigate() {
        page.navigate(URL);
        return this;
    }
}
