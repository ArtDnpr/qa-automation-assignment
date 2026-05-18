package integration;

import api.helpers.ApiHelper;
import api.models.Booking;
import ui.base.BaseUITest;
import ui.pages.FormPage;
import ui.pages.WebTablePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DisplayName("API + UI Integration Tests")
class ApiUiIntegrationTest extends BaseUITest {

    @Test
    @DisplayName("Create booking via API, fill DemoQA form with booking data, verify submission")
    void createBookingThenFillForm() {
        Booking booking = ApiHelper.buildDefaultBooking();
        Booking.BookingResponse apiResponse = ApiHelper.createBooking(booking);

        assertThat(apiResponse.getBookingId()).isPositive();
        assertThat(apiResponse.getBooking().getFirstname()).isEqualTo(booking.getFirstname());

        String firstName = apiResponse.getBooking().getFirstname();
        String lastName = apiResponse.getBooking().getLastname();
        String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@booking.com";
        String mobile = String.format("%010d", apiResponse.getBooking().getTotalprice());
        String address = "Booking #" + apiResponse.getBookingId()
                + ", " + apiResponse.getBooking().getAdditionalneeds();

        FormPage formPage = new FormPage(page);
        formPage.navigate()
                .fillFirstName(firstName)
                .fillLastName(lastName)
                .fillEmail(email)
                .selectGender("Male")
                .fillMobile(mobile)
                .addSubject("Maths")
                .fillAddress(address)
                .selectState("NCR")
                .selectCity("Delhi")
                .submit();

        assertThat(formPage.isSuccessModalDisplayed()).isTrue();
        assertThat(formPage.getModalTableValue("Student Name"))
                .isEqualTo(firstName + " " + lastName);
        assertThat(formPage.getModalTableValue("Student Email")).isEqualTo(email);
        assertThat(formPage.getModalTableValue("Mobile")).isEqualTo(mobile);
        assertThat(formPage.getModalTableValue("Address")).isEqualTo(address);
    }

    @Test
    @DisplayName("Create booking via API, add it to DemoQA web table, verify record")
    void createBookingThenAddToWebTable() {
        Booking booking = ApiHelper.buildDefaultBooking();
        Booking.BookingResponse apiResponse = ApiHelper.createBooking(booking);

        assertThat(apiResponse.getBookingId()).isPositive();

        String firstName = apiResponse.getBooking().getFirstname();
        String lastName = apiResponse.getBooking().getLastname();
        String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@booking.com";
        String age = "30";
        String salary = String.valueOf(apiResponse.getBooking().getTotalprice());
        String department = apiResponse.getBooking().getAdditionalneeds();

        WebTablePage tablePage = new WebTablePage(page);
        tablePage.navigate();
        int initialCount = tablePage.getVisibleRowCount();

        tablePage.addRecord(firstName, lastName, email, age, salary, department);

        assertThat(tablePage.getVisibleRowCount()).isEqualTo(initialCount + 1);

        tablePage.search(firstName);
        assertThat(tablePage.getCellText(0, 0)).isEqualTo(firstName);
        assertThat(tablePage.getCellText(0, 1)).isEqualTo(lastName);
        assertThat(tablePage.getCellText(0, 3)).isEqualTo(email);
        assertThat(tablePage.getCellText(0, 4)).isEqualTo(salary);
        assertThat(tablePage.getCellText(0, 5)).isEqualTo(department);

        String token = ApiHelper.getAuthToken();
        ApiHelper.deleteBooking(apiResponse.getBookingId(), token);
    }
}
