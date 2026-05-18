package ui.tests;

import ui.base.BaseUITest;
import ui.pages.WebTablePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("ui")
@DisplayName("Web Table Tests")
class WebTableTest extends BaseUITest {

    private WebTablePage tablePage;

    @BeforeEach
    void setUpPage() {
        tablePage = new WebTablePage(page);
        tablePage.navigate();
    }

    @Test
    @DisplayName("Add a new record and verify it appears in the table")
    void addNewRecord() {
        int initialCount = tablePage.getVisibleRowCount();

        tablePage.addRecord("Alice", "Wonder", "alice@test.com", "30", "5000", "QA");

        assertThat(tablePage.getVisibleRowCount()).isEqualTo(initialCount + 1);

        tablePage.search("Alice");
        assertThat(tablePage.getCellText(0, 0)).isEqualTo("Alice");
        assertThat(tablePage.getCellText(0, 1)).isEqualTo("Wonder");
        assertThat(tablePage.getCellText(0, 2)).isEqualTo("30");
        assertThat(tablePage.getCellText(0, 3)).isEqualTo("alice@test.com");
        assertThat(tablePage.getCellText(0, 4)).isEqualTo("5000");
        assertThat(tablePage.getCellText(0, 5)).isEqualTo("QA");
    }

    @Test
    @DisplayName("Edit an existing record and verify changes")
    void editExistingRecord() {
        tablePage.editRecord(0);

        tablePage.fillFirstName("Updated");
        tablePage.fillSalary("99999");
        tablePage.submitForm();

        assertThat(tablePage.getCellText(0, 0)).isEqualTo("Updated");
        assertThat(tablePage.getCellText(0, 4)).isEqualTo("99999");
    }

    @Test
    @DisplayName("Delete a record and verify it is removed")
    void deleteRecord() {
        tablePage.addRecord("ToDelete", "User", "del@test.com", "25", "3000", "IT");

        tablePage.search("ToDelete");
        assertThat(tablePage.getVisibleRowCount()).isEqualTo(1);

        tablePage.deleteRecord(0);

        assertThat(tablePage.getVisibleRowCount()).isZero();
    }

    @Test
    @DisplayName("Search filters records correctly")
    void searchFunctionality() {
        tablePage.search("Cierra");
        assertThat(tablePage.getVisibleRowCount()).isEqualTo(1);
        assertThat(tablePage.getCellText(0, 0)).isEqualTo("Cierra");

        tablePage.clearSearch();
        tablePage.search("nonexistent_xyz_123");
        assertThat(tablePage.getVisibleRowCount()).isZero();

        tablePage.clearSearch();
        assertThat(tablePage.getVisibleRowCount()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Column headers are present")
    void columnHeadersPresent() {
        List<String> headers = tablePage.getColumnHeaders();
        assertThat(headers).contains("First Name", "Last Name", "Age", "Email", "Salary", "Department", "Action");
    }
}
