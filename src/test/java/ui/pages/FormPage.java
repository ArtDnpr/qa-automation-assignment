package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import config.Constants.PagePaths;
import config.TestConfig;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class FormPage {

    private final Page page;
    private static final String URL = TestConfig.getInstance().getDemoQaBaseUrl() + PagePaths.PRACTICE_FORM;

    public FormPage(Page page) {
        this.page = page;
    }

    public FormPage navigate() {
        page.navigate(URL);
        removeAdsAndOverlays();
        return this;
    }

    public FormPage fillFirstName(String name) {
        page.locator("#firstName").fill(name);
        return this;
    }

    public FormPage fillLastName(String name) {
        page.locator("#lastName").fill(name);
        return this;
    }

    public FormPage fillEmail(String email) {
        page.locator("#userEmail").fill(email);
        return this;
    }

    public FormPage selectGender(String gender) {
        page.locator("label", new Page.LocatorOptions()
                .setHasText(Pattern.compile("^" + gender + "$"))).click();
        return this;
    }

    public FormPage fillMobile(String mobile) {
        page.locator("#userNumber").fill(mobile);
        return this;
    }

    public FormPage setDateOfBirth(String day, String month, String year) {
        page.locator("#dateOfBirthInput").click();
        page.locator(".react-datepicker__month-select")
                .selectOption(new SelectOption().setLabel(month));
        page.locator(".react-datepicker__year-select")
                .selectOption(new SelectOption().setValue(year));
        String paddedDay = String.format("%03d", Integer.parseInt(day));
        page.locator(".react-datepicker__day--" + paddedDay
                + ":not(.react-datepicker__day--outside-month)").click();
        return this;
    }

    public FormPage addSubject(String subject) {
        page.locator("#subjectsInput").fill(subject);
        page.locator(".subjects-auto-complete__option",
                new Page.LocatorOptions().setHasText(subject)).first().click();
        return this;
    }

    public FormPage selectHobby(String hobby) {
        page.locator("label", new Page.LocatorOptions()
                .setHasText(hobby)).click();
        return this;
    }

    public FormPage uploadPicture(Path filePath) {
        page.locator("#uploadPicture").setInputFiles(filePath);
        return this;
    }

    public FormPage fillAddress(String address) {
        page.locator("#currentAddress").fill(address);
        return this;
    }

    public FormPage selectState(String state) {
        page.locator("#state").click();
        page.locator("#state").locator("[class*='option']",
                new Locator.LocatorOptions().setHasText(state)).click();
        return this;
    }

    public FormPage selectCity(String city) {
        page.locator("#city").click();
        page.locator("#city").locator("[class*='option']",
                new Locator.LocatorOptions().setHasText(city)).click();
        return this;
    }

    public FormPage submit() {
        page.locator("#submit").scrollIntoViewIfNeeded();
        page.locator("#submit").click();
        return this;
    }

    public boolean isSuccessModalDisplayed() {
        return page.locator("#example-modal-sizes-title-lg").isVisible();
    }

    public String getModalTableValue(String label) {
        return page.locator(".table-responsive tbody tr",
                        new Page.LocatorOptions().setHasText(label))
                .locator("td").last().textContent();
    }

    private void removeAdsAndOverlays() {
        page.evaluate("document.querySelectorAll('#adplus-anchor, #fixedban, footer, " +
                "#close-fixedban, .ad, iframe[id*=\"google\"]').forEach(e => e.remove())");
    }
}
