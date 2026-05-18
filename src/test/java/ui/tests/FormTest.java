package ui.tests;

import ui.base.BaseUITest;
import ui.pages.FormPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("ui")
@DisplayName("Practice Form Tests")
class FormTest extends BaseUITest {

    private FormPage formPage;

    @BeforeEach
    void setUpPage() {
        formPage = new FormPage(page);
        formPage.navigate();
    }

    @Test
    @DisplayName("Fill and submit the full student registration form")
    void fillAndSubmitFullForm() throws IOException {
        Path tempFile = Files.createTempFile("test-upload", ".png");

        formPage
                .fillFirstName("John")
                .fillLastName("Doe")
                .fillEmail("john.doe@example.com")
                .selectGender("Male")
                .fillMobile("1234567890")
                .setDateOfBirth("15", "January", "1995")
                .addSubject("Maths")
                .selectHobby("Sports")
                .uploadPicture(tempFile)
                .fillAddress("123 Test Street, QA City")
                .selectState("NCR")
                .selectCity("Delhi")
                .submit();

        assertThat(formPage.isSuccessModalDisplayed()).isTrue();
        assertThat(formPage.getModalTableValue("Student Name")).isEqualTo("John Doe");
        assertThat(formPage.getModalTableValue("Student Email")).isEqualTo("john.doe@example.com");
        assertThat(formPage.getModalTableValue("Gender")).isEqualTo("Male");
        assertThat(formPage.getModalTableValue("Mobile")).isEqualTo("1234567890");
        assertThat(formPage.getModalTableValue("Subjects")).isEqualTo("Maths");
        assertThat(formPage.getModalTableValue("Hobbies")).isEqualTo("Sports");
        assertThat(formPage.getModalTableValue("Address")).isEqualTo("123 Test Street, QA City");
        assertThat(formPage.getModalTableValue("State and City")).isEqualTo("NCR Delhi");

        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("Upload a file and verify filename is displayed")
    void uploadFileVerifyFilename() throws IOException {
        Path tempFile = Files.createTempFile("upload-test", ".txt");
        Files.writeString(tempFile, "test content");

        formPage.uploadPicture(tempFile);

        String uploadedName = page.locator("#uploadPicture").inputValue();
        assertThat(uploadedName).contains("upload-test");

        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("Select date from the date picker")
    void selectDateFromDatePicker() {
        formPage.setDateOfBirth("20", "March", "2000");

        String dateValue = page.locator("#dateOfBirthInput").inputValue();
        assertThat(dateValue).contains("20");
        assertThat(dateValue).contains("2000");
    }

    @Test
    @DisplayName("Choose from state and city dropdowns")
    void chooseStateAndCityDropdowns() {
        formPage
                .selectState("Haryana")
                .selectCity("Karnal");

        assertThat(page.locator("#state").textContent()).contains("Haryana");
        assertThat(page.locator("#city").textContent()).contains("Karnal");
    }

    @Test
    @DisplayName("Submit minimal required fields and verify success modal")
    void submitMinimalForm() {
        formPage
                .fillFirstName("Jane")
                .fillLastName("Smith")
                .selectGender("Female")
                .fillMobile("9876543210")
                .submit();

        assertThat(formPage.isSuccessModalDisplayed()).isTrue();
        assertThat(formPage.getModalTableValue("Student Name")).isEqualTo("Jane Smith");
        assertThat(formPage.getModalTableValue("Gender")).isEqualTo("Female");
        assertThat(formPage.getModalTableValue("Mobile")).isEqualTo("9876543210");
    }
}
