import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public class FXMLController implements Initializable {

    private static final int TEXT_FIELDTYPE = 1;
    private static final int CHOICE_FIELDTYPE = 2;
    private static final int COMBO_FIELDTYPE = 3;

    public static String PREF_LOCATION;
    private Stage stage;
    private ACompletedForm selectedForm;

    @FXML
    private TextField cancelTradeID;
    @FXML
    private TextField originalID;
    @FXML
    private TextField securityID;
    @FXML
    private TextField tradeID;
    @FXML
    private TextField reportingDealerID;
    @FXML
    private TextField executionDate;
    @FXML
    private TextField executionTime;
    @FXML
    private TextField settlementDate;
    @FXML
    private TextField quantity;
    @FXML
    private TextField price;
    @FXML
    private TextField customerAccountID;
    @FXML
    private TextField benchmarkSecurityID;
    @FXML
    private TextField yield;
    @FXML
    private TextField commission;

    private ToggleGroup toggleGroup;
    @FXML
    private RadioButton transNew;
    @FXML
    private RadioButton transCancel;
    @FXML
    private RadioButton transCorrection;

    @FXML
    private ChoiceBox<String> customerAccountType;
    @FXML
    private ChoiceBox<String> side;
    @FXML
    private ChoiceBox<String> capacity;
    @FXML
    private ChoiceBox<String> primaryMarket;
    @FXML
    private ChoiceBox<String> relatedPTY;
    @FXML
    private ChoiceBox<String> nonResident;
    @FXML
    private ChoiceBox<String> feeBasedAccount;

    @FXML
    private ComboBox<String> electronicExecution;
    @FXML
    private ComboBox<String> securityIDType;
    @FXML
    private ComboBox<String> counterPartyType;
    @FXML
    private ComboBox<String> counterPartyID;
    @FXML
    private ComboBox<String> benchmarkSecurityIDType;
    @FXML
    private ComboBox<String> customerAccountLEI;
    @FXML
    private ComboBox<String> traderID;
    @FXML
    private ComboBox<String> introDCarry;
    @FXML
    private ComboBox<String> tradingVenueID;

    @FXML
    private Text errorText;
    @FXML
    private Text correctionNumbers;
    @FXML
    private TextField selectedFileText;

    @FXML
    private TableView<ACompletedForm> tableView;
    private Field[] fields = ACompletedForm.class.getDeclaredFields();
    private String[] fieldVariables = new String[30];

    private TextField[] allTheTextFields = new TextField[12];
    private ChoiceBox[] allTheChoiceBoxes = new ChoiceBox[7];
    private ComboBox[] allTheComboBoxes = new ComboBox[6];

    private AllTheDealers allTheDealers = new AllTheDealers();
    private TestingInputs testingInputs = new TestingInputs();

    private ObservableList<String> secIDTypeOptions = FXCollections.observableArrayList("--Security ID Type-- *","CUSIP", "ISIN");
    private ObservableList<String> benchSecIDTypeOptions = FXCollections.observableArrayList("--Bench Security ID Type--","CUSIP", "ISIN");
    private ObservableList<String> traderIDOptions = FXCollections.observableArrayList("--Trader ID-- *","AARONLAIDLAW");
    private ObservableList<String> custAccountTypeOptions = FXCollections.observableArrayList("Retail", "Institutional");
    private ObservableList<String> counterPartyTypeOptions = FXCollections.observableArrayList("--CounterParty Type-- *","Client", "Non-client", "Dealer", "IDBB", "ATS", "Bank", "Issuer");
    private ObservableList<String> introDCarryOptions = FXCollections.observableArrayList("--IntroD Carry-- *", "Introducing", "Carrying", "N/A");
    private ObservableList<String> sideOptions = FXCollections.observableArrayList("Buy", "Sell");
    private ObservableList<String> capacityOptions = FXCollections.observableArrayList("Agency", "Principal");
    private ObservableList<String> YNOptions = FXCollections.observableArrayList("Y", "N");
    private ObservableList<String> electronicOptions = FXCollections.observableArrayList("--Electronic Execution-- *","Y", "N");

    public void createLogForm(ActionEvent actionEvent) throws FileNotFoundException {
        gatherDataFromFieldsAndCreateArraysForRequiredChecks();
        errorText.setText("");
        boolean errorsPresent = false;

        // Before creating the form, all fields are error-checked here //
        for (ChoiceBox choiceBox : allTheChoiceBoxes) {
            choiceBox.setStyle(null);
            if (!testingInputs.isChoiceBoxSelected(choiceBox)) {
                errorsPresent = true;
                createErrorResponse(choiceBox, CHOICE_FIELDTYPE);
                break;
            }
        }
        for (TextField aTextField : allTheTextFields) {
            aTextField.setStyle("-fx-text-inner-color: black;");
            if (!isCorrectInputTypeTextFields(aTextField)) {
                errorsPresent = true;
                createErrorResponse(aTextField, TEXT_FIELDTYPE);
                break;
            }
        }
        for (ComboBox aComboBox : allTheComboBoxes) {
            aComboBox.setStyle(null);
            if (!testingInputs.isComboBoxSelected(aComboBox)) {
                errorsPresent = true;
                createErrorResponse(aComboBox, COMBO_FIELDTYPE);
                break;
        }
    }
        if(!errorsPresent) {
            ACompletedForm aCompletedForm = new ACompletedForm
                    (securityID.getText(), securityIDType.getSelectionModel().getSelectedIndex(), setAppropriateTradeID(whichRadioButton()), originalID.getText(),
                            whichRadioButton(), executionDate.getText(), executionTime.getText(), settlementDate.getText(),
                            traderID.getValue(), reportingDealerID.getText(), counterPartyType.getSelectionModel().getSelectedIndex(),
                            counterPartyID.getValue(), customerAccountType.getSelectionModel().getSelectedIndex()+1, customerAccountLEI.getValue(),
                            customerAccountID.getText(), introDCarry.getSelectionModel().getSelectedIndex(), electronicExecution.getValue(), tradingVenueID
                            .getValue(), side.getSelectionModel().getSelectedIndex()+1, quantity.getText().replaceAll(",", ""), price.getText(), benchmarkSecurityID.getText(),
                            benchmarkSecurityIDType.getSelectionModel().getSelectedIndex(), yield.getText(), commission.getText(), capacity.getSelectionModel().getSelectedIndex()+1,
                            primaryMarket.getValue(), relatedPTY.getValue(), nonResident.getValue(), feeBasedAccount.getValue());

            if(whichRadioButton() == 0) {
                aCompletedForm.setORIG_TRADE_ID("");
                String aFile = aCompletedForm.createCSVFormat();
                aCompletedForm.writeToFile(aFile);
            } else if (whichRadioButton() == 1){
                String aFile = aCompletedForm.createCSVFormat();
                aCompletedForm.writeToFile(aFile);
            } else if (whichRadioButton() == 2){
                // instead of having corrections, instead it will produce a cancelled trade line and then a new trade line afterwards //
                selectedForm.setTRANS_TYPE(1);
                selectedForm.setORIG_TRADE_ID(originalID.getText());
                selectedForm.setTRADE_ID(cancelTradeID.getText()); // the cancelID field becomes the new tradeID //
                String firstLineOfFile = selectedForm.createCSVFormat(); // a cancel trade //
                aCompletedForm.setTRANS_TYPE(0);
                aCompletedForm.setORIG_TRADE_ID("");
                /* the TradeID here has to start with the date of the trade it is cancelling. It also has a random number <80000 added so it doesn't overlap with anything made
                 on that previous day (they all start with 80000) */
                aCompletedForm.setTRADE_ID(selectedForm.getEXECUTION_DATE().substring(0,8) + String.valueOf(new Random().nextInt(80000)));
                String secondLineOfFile = aCompletedForm.createCSVFormat(); // a new trade //
                aCompletedForm.writeToFile(firstLineOfFile, secondLineOfFile);
            }
            refreshAllFields();
            createSuccessResponse();
        }
    }

    // Methods during initialization. Setting up fields and appropriate data //
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Searching for save location preferences. Otherwise sets user.home //
        Preferences preferences = Preferences.userRoot();
        PREF_LOCATION = preferences.get("PREF_LOCATION", System.getProperty("user.home"));

        // Setting all the fields //
        securityIDType.setItems(secIDTypeOptions);
        traderID.setItems(traderIDOptions);
        counterPartyID.setItems(FXCollections.observableArrayList(allTheDealers.getAllDealerNames("--CounterParty ID--")));
        counterPartyType.setItems(counterPartyTypeOptions);
        customerAccountLEI.setItems(FXCollections.observableArrayList(allTheDealers.getAllDealerNames("--Customer Account LEI--")));
        customerAccountType.setItems(custAccountTypeOptions);
        introDCarry.setItems(introDCarryOptions);
        electronicExecution.setItems(electronicOptions);
        tradingVenueID.setItems(FXCollections.observableArrayList(allTheDealers.getAllDealerNames("--Trading Venue ID--")));
        side.setItems(sideOptions);
        benchmarkSecurityIDType.setItems(benchSecIDTypeOptions);
        capacity.setItems(capacityOptions);
        primaryMarket.setItems(YNOptions);
        relatedPTY.setItems(YNOptions);
        nonResident.setItems(YNOptions);
        feeBasedAccount.setItems(YNOptions);

        toggleGroup = new ToggleGroup();
        transNew.setToggleGroup(toggleGroup);
        transCancel.setToggleGroup(toggleGroup);
        transCorrection.setToggleGroup(toggleGroup);
        transNew.setSelected(true);

        for(int i = 0; i < fields.length; i++){
            fieldVariables[i] = fields[i].getName();
        }

        setCompGeneratedFields();






        // don't talk to me about this part. I just copy pasted someone's code so I could have commas while typing numbers //
        // I can't even figure out how to move it into its own extended TextFormatter class -_- //
        final char seperatorChar = ',';
        final Pattern p = Pattern.compile("[0-9" + seperatorChar + "]*");
        quantity.setTextFormatter(new TextFormatter<Object>(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                if (!change.isContentChange()) {
                    return change; // no need for modification, if only the selection changes
                }
                String newText = change.getControlNewText();
                if (newText.isEmpty()) {
                    return change;
                }
                if (!p.matcher(newText).matches()) {
                    return null; // invalid change
                }

                // invert everything before the range
                int suffixCount = change.getControlText().length() - change.getRangeEnd();
                int digits = suffixCount - suffixCount / 4;
                StringBuilder sb = new StringBuilder();

                // insert seperator just before caret, if necessary
                if (digits % 3 == 0 && digits > 0 && suffixCount % 4 != 0) {
                    sb.append(seperatorChar);
                }

                // add the rest of the digits in reversed order
                for (int i = change.getRangeStart() + change.getText().length() - 1; i >= 0; i--) {
                    char letter = newText.charAt(i);
                    if (Character.isDigit(letter)) {
                        sb.append(letter);
                        digits++;
                        if (digits % 3 == 0) {
                            sb.append(seperatorChar);
                        }
                    }
                }

                // remove seperator char, if added as last char
                if (digits % 3 == 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.reverse();
                int length = sb.length();

                // replace with modified text
                change.setRange(0, change.getRangeEnd());
                change.setText(sb.toString());
                change.setCaretPosition(length);
                change.setAnchor(length);

                return change;
            }}));


    }

    private void setCompGeneratedFields(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        executionDate.setText(formatter.format(date));
        reportingDealerID.setText("549300GNS5HNF05WRY38");
        introDCarry.getSelectionModel().select(1);
        traderID.getSelectionModel().select(1);
        electronicExecution.getSelectionModel().select(2);
        setFunkyTradeIDsParty(whichRadioButton());
    }

    public void setFunkyTradeIDsParty(int radioButton){
        switch (radioButton){
            case 0:
                tradeID.setText(testingInputs.getSystemsMostRecentlyUsedTradeIDAndAdd1(reportingDealerID.getText()));
                break;
            case 1:
                cancelTradeID.setText(testingInputs.getSystemsMostRecentlyUsedTradeIDAndAdd1(reportingDealerID.getText()));
                break;
            case 2:
                tradeID.setText(String.valueOf(Integer.parseInt(testingInputs.getSystemsMostRecentlyUsedTradeIDAndAdd1(reportingDealerID.getText()))+1));
                cancelTradeID.setText(testingInputs.getSystemsMostRecentlyUsedTradeIDAndAdd1(reportingDealerID.getText()));
                break;
        }
    }

    private void gatherDataFromFieldsAndCreateArraysForRequiredChecks(){
        // Initialization had to be done later on for some reason so it's all moved into this function //
        allTheTextFields[0] = securityID;
        allTheTextFields[1] = tradeID;
        allTheTextFields[2] = cancelTradeID;
        allTheTextFields[3] = executionDate;
        allTheTextFields[4] = executionTime;
        allTheTextFields[5] = settlementDate;
        allTheTextFields[6] = customerAccountID;
        allTheTextFields[7] = quantity;
        allTheTextFields[8] = price;
        allTheTextFields[9] = benchmarkSecurityID;
        allTheTextFields[10] = yield;
        allTheTextFields[11] = commission;


        allTheChoiceBoxes[0] = side;
        allTheChoiceBoxes[1] = capacity;
        allTheChoiceBoxes[2] = primaryMarket;
        allTheChoiceBoxes[3] = relatedPTY;
        allTheChoiceBoxes[4] = nonResident;
        allTheChoiceBoxes[5] = feeBasedAccount;
        allTheChoiceBoxes[6] = customerAccountType;

        allTheComboBoxes[0] = securityIDType;
        allTheComboBoxes[1] = electronicExecution;
        allTheComboBoxes[2] = counterPartyType;
        allTheComboBoxes[3] = traderID;
        allTheComboBoxes[4] = introDCarry;
        allTheComboBoxes[5] = benchmarkSecurityIDType;
    }

    // Methods for checking and responding to inputs //

    private boolean isCorrectInputTypeTextFields(TextField textField){
        String theField = textField.getId();
        switch (theField){
            case "securityIDFXML":
                if(securityID.getText().length() < 9 || !testingInputs.isAlphanumeric(securityID.getText())){
                    errorText.setText("SecurityID must be at least 9 alphanumeric characters.");
                    return false;
                } break;
            case "tradeIDFXML":
                if(whichRadioButton() == 0 || whichRadioButton() == 2) {
                    if (tradeID.getText().length() != 5) {
                        errorText.setText("TradeID must be 5 digits.");
                        return false;
                    }
                }break;
            case "cancelTradeIDFXML":
                if(whichRadioButton() == 1 || whichRadioButton() == 2){
                if(cancelTradeID.getText().length() != 5) {
                    errorText.setText("CancelTradeID must be 5 digits.");
                    return false;
                }
                } break;
            case "executionDateFXML":
                if(executionDate.getText().length() != 8){
                    errorText.setText("Execution");
                    return false;
                } break;
            case "executionTimeFXML":
                if(!testingInputs.isValidTime(executionTime.getText())){
                    errorText.setText("ExecutionTime must be hh:mm:ss.");
                    return false;
                } break;
            case "settlementDateFXML":
                if(settlementDate.getText().length() != 8){
                    errorText.setText("SettlementDate must be YYYYMMDD");
                    return false;
                } break;
            case "customerAccountIDFXML":
                if(customerAccountID.getText().isEmpty()){
                    return true;
                } else if (!testingInputs.isAlphanumeric(customerAccountID.getText())){
                    errorText.setText("CustomerAccountID must be alphanumeric.");
                    return false;
                } break;
            case "quantityFXML":
                String quantityFormatted = quantity.getText().replaceAll(",", "");
                if(!testingInputs.isNumerical(quantityFormatted) || quantity.getText().isEmpty()){
                    errorText.setText("Quantity must be numerical.");
                    return false;
                } break;
            case "priceFXML":
                if(!testingInputs.isFloat(price.getText()) || price.getText().isEmpty()){
                    errorText.setText("Price must be numerical (decimal optional).");
                    return false;
                } break;
            case "benchmarkSecurityIDFXML":
                if(benchmarkSecurityID.getText().isEmpty()){
                    return true;
                }else if(benchmarkSecurityID.getText().length() != 12 || !testingInputs.isAlphanumeric(benchmarkSecurityID.getText())){
                    errorText.setText("BenchmarkSecurityID must be 12 alphanumeric characters.");
                    return false;
                } break;
            case "yieldFXML":
                if(!testingInputs.isFloat(yield.getText()) || yield.getText().isEmpty()){
                    errorText.setText("Yield must be numerical (decimal optional).");
                    return false;
                } break;
            case "commissionFXML":
                if(commission.getText().isEmpty()){
                    return true;
                }else if(!testingInputs.isFloat(commission.getText())){
                    errorText.setText("Commission must be numerical (decimal optional).");
                    return false;
                } break;
        }
        return true;
    }

    private void createErrorResponse(Control theField, int fieldType){
        errorText.setVisible(true);
        switch (fieldType){
            case 1:
                TextField theText = (TextField) theField;
                theText.setStyle("-fx-text-fill: #f54a32;");
                errorText.setFill(Color.valueOf("#f54a32"));
                break;
            case 2:
                ChoiceBox theChoiceBox = (ChoiceBox) theField;
                theChoiceBox.setStyle("-fx-base: #f54a32");
                break;
            case 3:
                ComboBox theComboBox = (ComboBox) theField;
                theComboBox.setStyle("-fx-base: #f54a32");
                break;
        }
    }

    private void createSuccessResponse(){
        // Green text at the top that says "Success!" for 3 seconds when a file a successfully been written to //
        errorText.setVisible(true);
        errorText.setText("Success!");
        errorText.setFill(Color.GREEN);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                errorText.setVisible(false);
            }
        };
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(runnable, 3, TimeUnit.SECONDS);
    }

    // Methods for dealing with general file I/O and selection //

    @FXML
    private void openFileExplorer(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                fillTableView(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void setSaveLocation(MouseEvent mouseEvent) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Set Save Location");
        jFileChooser.setApproveButtonText("Select Folder");
        if(PREF_LOCATION.equals(".")){
            jFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        } else {
            jFileChooser.setCurrentDirectory(new File(PREF_LOCATION));
        }
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setAcceptAllFileFilterUsed(false);
        if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            Preferences preferences = Preferences.userRoot();
            preferences.put("PREF_LOCATION", jFileChooser.getSelectedFile().toString());
            PREF_LOCATION = jFileChooser.getSelectedFile().toString();
        }
    }

    private void fillTableView(File file) throws IOException {
        tableView.getItems().clear();
        selectedFileText.setText(file.getAbsolutePath());
        BufferedReader buffer = new BufferedReader(new FileReader(file));
        buffer.readLine();
        String lines = buffer.readLine();
        tableView.getColumns().clear();

            for (String fieldVariable : fieldVariables) {
                TableColumn<ACompletedForm, String> tableColumn = new TableColumn<>(fieldVariable);
                tableColumn.setCellValueFactory(new PropertyValueFactory<>(fieldVariable));
                tableView.getColumns().add(tableColumn);
            }

        ObservableList<ACompletedForm> theDataList = FXCollections.observableArrayList();
        while (lines != null) {
            ACompletedForm aCompletedForm;
            String[] anotherLine = lines.split(",");
            // The constructor automatically does the dealerCode conversion. Since this is reversed - some magic here is here to put the dealerCode in,
            // change it to a dealerName, and then the constructor will switch it back again to the dealerCode //
            aCompletedForm = new ACompletedForm(anotherLine[0], attemptToParseInt(anotherLine[1]), anotherLine[2], anotherLine[3],
                    attemptToParseInt(anotherLine[4]), anotherLine[5], anotherLine[6], anotherLine[7], anotherLine[8], anotherLine[9], attemptToParseInt(anotherLine[10]),
                    allTheDealers.getADealerName(anotherLine[11]), attemptToParseInt(anotherLine[12]), allTheDealers.getADealerName(anotherLine[13]),
                    anotherLine[14], attemptToParseInt(anotherLine[15]), anotherLine[16], allTheDealers.getADealerName(anotherLine[17]), attemptToParseInt(anotherLine[18]),
                    anotherLine[19], anotherLine[20], anotherLine[21], attemptToParseInt(anotherLine[22]), anotherLine[23], anotherLine[24],
                    attemptToParseInt(anotherLine[25]), anotherLine[26], anotherLine[27], anotherLine[28], anotherLine[29]);

            lines = buffer.readLine();
            theDataList.add(aCompletedForm);
            aCompletedForm = null;
        }
        tableView.setItems(theDataList);
    }

    private int attemptToParseInt(String string){
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public void moveData(ActionEvent actionEvent) {
        refreshAllFields();
        try {
            selectedForm = tableView.getSelectionModel().getSelectedItem();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        securityID.setText(selectedForm.getSECURITY_ID());
        securityIDType.getSelectionModel().select(Integer.parseInt(selectedForm.getSECURITY_ID_TYPE()));
        originalID.setText(selectedForm.getTRADE_ID());
        executionDate.setText(selectedForm.getEXECUTION_DATE());
        executionTime.setText(formatExecutionTime(selectedForm.getEXECUTION_TIME()));
        settlementDate.setText(selectedForm.getSETTLEMENT_DATE());
        traderID.getSelectionModel().select(selectedForm.getTRADER_ID());
        reportingDealerID.setText(selectedForm.getREPORTING_DEALER_ID());
        counterPartyType.getSelectionModel().select(Integer.parseInt(selectedForm.getCOUNTERPARTY_TYPE()));
        counterPartyID.getSelectionModel().select(allTheDealers.getADealerName(selectedForm.getCOUNTERPARTY_ID()));
        try {
            customerAccountType.getSelectionModel().select(Integer.parseInt(selectedForm.getCUSTOMER_ACC_TYPE())-1);
        } catch (NumberFormatException e){
            customerAccountType.getSelectionModel().select(null);
        }
        customerAccountLEI.getSelectionModel().select(allTheDealers.getADealerName(selectedForm.getCUSTOMER_LEI()));
        customerAccountID.setText(selectedForm.getCUSTOMER_ACCOUNT_ID());
        introDCarry.getSelectionModel().select(Integer.parseInt(selectedForm.getINTROD_CARRY()));
        electronicExecution.getSelectionModel().select(selectedForm.getELECTRONIC_EXECUTION());
        tradingVenueID.getSelectionModel().select(allTheDealers.getADealerName(selectedForm.getTRADING_VENUE_ID()));
        side.getSelectionModel().select(Integer.parseInt(selectedForm.getSIDE())-1);
        quantity.setText(String.valueOf(selectedForm.getQUANTITY()));
        price.setText(String.valueOf(selectedForm.getPRICE()));
        benchmarkSecurityID.setText(selectedForm.getBENCHMARK_SEC_ID());
        try {
            benchmarkSecurityIDType.getSelectionModel().select(Integer.parseInt(selectedForm.getBENCHMARK_SEC_ID_TYPE()));
        } catch (NumberFormatException f){
            benchmarkSecurityIDType.getSelectionModel().select(0);
        }
        yield.setText(String.valueOf(selectedForm.getYIELD()));
        commission.setText(selectedForm.getCOMMISSION());
        capacity.getSelectionModel().select(Integer.parseInt(selectedForm.getCAPACITY())-1);
        primaryMarket.getSelectionModel().select(stupidWtvrPants(String.valueOf(selectedForm.getPRIMARY_MARKET())));
        relatedPTY.getSelectionModel().select(stupidWtvrPants(String.valueOf(selectedForm.getRELATED_PTY())));
        nonResident.getSelectionModel().select(stupidWtvrPants(String.valueOf(selectedForm.getNON_RESIDENT())));
        feeBasedAccount.getSelectionModel().select(stupidWtvrPants(String.valueOf(selectedForm.getFEE_BASED_ACCOUNT())));
        setFunkyTradeIDsParty(whichRadioButton());
    }

    public int stupidWtvrPants(String string){
        if(string.equals("Y")){
            return 0;
        } else return 1;
    }

    // Extra utility methods //

    @FXML
    private void refreshAllFields(){
        gatherDataFromFieldsAndCreateArraysForRequiredChecks();
        errorText.setVisible(false);
        for(ComboBox comboBox : allTheComboBoxes){
            comboBox.getSelectionModel().select(0);
            comboBox.setStyle(null);
        }
        for(TextField textField : allTheTextFields){
            textField.setStyle("-fx-text-fill: black");
            textField.setText("");
        }
        for(ChoiceBox choiceBox : allTheChoiceBoxes){
            choiceBox.getSelectionModel().clearSelection();
            choiceBox.setStyle(null);
        }
        counterPartyID.getSelectionModel().select(0);
        customerAccountLEI.getSelectionModel().select(0);
        tradingVenueID.getSelectionModel().select(0);
        setCompGeneratedFields();
    }

    private int whichRadioButton(){
        if (toggleGroup.getSelectedToggle().toString().contains("transNew")){
            return 0;
        } else if (toggleGroup.getSelectedToggle().toString().contains("transCancel")){
            return 1;
        } else {
            return 2;
        }
    }

    private String setAppropriateTradeID(int transType) {
        // This is just setting the appropriate tradeID for each construction  //
        if (transType == 1) {
            return cancelTradeID.getText();
        }
        return tradeID.getText();
    }

    private String formatExecutionTime(String string){
        if(string.matches("^(\\d:\\d\\d:\\d\\d)")){
            string = "0" + string;
        } return string;
    }

    public void setVisibility(ActionEvent actionEvent) {
        // Setting visibility of various nodes that change depending on the radio button (Transaction Type) selected //
        // Also sets the appropriate TradeIDs for most scenarios (except for when "moving". It's called on its own there)
        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        if(actionEvent.getSource().toString().contains("transNew")){
            originalID.setVisible(false);
            tradeID.setVisible(true);
            cancelTradeID.setVisible(false);
            correctionNumbers.setVisible(false);
            tradeID.setText(testingInputs.getSystemsMostRecentlyUsedTradeIDAndAdd1(reportingDealerID.getText()));
            stageTheEventSourceNodeBelongs.setWidth(670);
        } else if (actionEvent.getSource().toString().contains("transCancel")) {
            originalID.setVisible(true);
            tradeID.setVisible(false);
            cancelTradeID.setVisible(true);
            correctionNumbers.setVisible(true);
            cancelTradeID.setText(testingInputs.getSystemsMostRecentlyUsedTradeIDAndAdd1(reportingDealerID.getText()));
            stageTheEventSourceNodeBelongs.setWidth(1198);
        } else if (actionEvent.getSource().toString().contains("transCorrection")){
            originalID.setVisible(true);
            tradeID.setVisible(true);
            cancelTradeID.setVisible(true);
            correctionNumbers.setVisible(true);
            cancelTradeID.setText(testingInputs.getSystemsMostRecentlyUsedTradeIDAndAdd1(reportingDealerID.getText()));
            tradeID.setText(String.valueOf(Integer.parseInt(testingInputs.getSystemsMostRecentlyUsedTradeIDAndAdd1(reportingDealerID.getText()))+1));
            stageTheEventSourceNodeBelongs.setWidth(1198);
        }
    }

    @FXML
    public void openHelpDialog(MouseEvent mouseEvent) {
        // Just the dialog for the question mark button //
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("How each Transaction Type works: ");
        alert.setContentText("New: Fill in the required fields below and click the \"Create Report\" button. \n\n" +
                            "Cancel: Follow the directions in the added frame and select the desired trade to be cancelled. " +
                            "Fill in the Cancellation Trade ID field (will become the new Trade ID) and click \"Create Report\". \n" +
                            "DO NOT change any of the other fields.\n \n" +
                            "Cancel and Correct: Follow the directions in the added frame and select the desired trade to be cancelled. " +
                            "This will automatically set it up to become a cancelled trade entry. Now, change the desired field(s). " +
                            "TradeID will be the ID of the new trade and Cancellation Trade ID will be the TradeID of the trade to be cancelled. \n\n" +
                            "Unless save location changed, all files will be created in the User/IIROC_Reports directory.");
        alert.showAndWait();
    }

    public String getPREF_LOCATION(){
        return PREF_LOCATION;
    }

}
