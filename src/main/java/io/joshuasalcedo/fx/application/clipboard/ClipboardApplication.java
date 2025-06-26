package io.joshuasalcedo.fx.application.clipboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

@Controller
public class ClipboardApplication {

    @FXML
    private Button clearSearchBtn;

    @FXML
    private VBox clipboardListContainer;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private Label createdLabel;

    @FXML
    private VBox detailsContainer;

    @FXML
    private VBox entryDetailsBox;

    @FXML
    private ToggleGroup filterGroup;

    @FXML
    private Label hashLabel;

    @FXML
    private Label lengthLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Button nextPageBtn;

    @FXML
    private Label pageInfoLabel;

    @FXML
    private Button pinDetailBtn;

    @FXML
    private Button prevPageBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox selectAllCheckbox;

    @FXML
    private Label selectionCountLabel;

    @FXML
    private ToggleButton showAllToggle;

    @FXML
    private ToggleButton showPinnedToggle;

    @FXML
    private ToggleButton showRecentToggle;

    @FXML
    private Label statsLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label typeLabel;

    @FXML
    void clearSearch(ActionEvent event) {

    }

    @FXML
    void copyContent(ActionEvent event) {

    }

    @FXML
    void deleteAll(ActionEvent event) {

    }

    @FXML
    void deleteAllUnpinned(ActionEvent event) {

    }

    @FXML
    void deleteEntry(ActionEvent event) {

    }

    @FXML
    void deleteOldEntries(ActionEvent event) {

    }

    @FXML
    void exportSelected(ActionEvent event) {

    }

    @FXML
    void nextPage(ActionEvent event) {

    }

    @FXML
    void previousPage(ActionEvent event) {

    }

    @FXML
    void refreshEntries(ActionEvent event) {

    }

    @FXML
    void togglePin(ActionEvent event) {

    }

    @FXML
    void toggleSelectAll(ActionEvent event) {

    }
    @FXML
    public void showSettings(ActionEvent actionEvent) {
    }
}
