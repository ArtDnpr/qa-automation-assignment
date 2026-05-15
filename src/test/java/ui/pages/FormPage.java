package ui.pages;

import com.microsoft.playwright.Page;

public class FormPage {

    private final Page page;
    private static final String URL = "https://demoqa.com/automation-practice-form";

    // TODO: Iteration 4 — add locators and interaction methods
    // Locators will target: name fields, gender, hobbies, picture upload,
    // date picker, subject, address, state/city dropdowns, submit button, modal

    public FormPage(Page page) {
        this.page = page;
    }

    public FormPage navigate() {
        page.navigate(URL);
        return this;
    }
}
