import javafx.scene.control.Alert;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ACompletedForm {

    // the field types are total chaos because I only realized half-way through that they could all just be Strings //
    private String securityID;
    private String securityIDType;
    private String tradeID;
    private String originalTradeID;
    private int transType;
    private String executionDate;
    private String executionTime;
    private String settlementDate;
    private String traderID;
    private String reportingDealerID;
    private String counterPartyType;
    private String counterPartyID;
    private String customerAccType;
    private String customerLEI;
    private String customerAccountID;
    private String introDCarry;
    private char electronicExecution;
    private String tradingVenueID;
    private String side;
    private int quantity;
    private float price;
    private String benchmarkSecurityID;
    private String benchmarkSecurityIDType;
    private float yield;
    private String commission;
    private String capacity;
    private char primaryMarket;
    private char relatedParty;
    private char nonResident;
    private char feeBasedAccount;

    // All (except transType) conversions to the appropriate format are done during construction //
    ACompletedForm(String securityID, int securityIDType, String tradeID, String originalTradeID, int transType,
                   String executionDate, String executionTime, String settlementDate, String traderID,
                   String reportingDealerID, int counterPartyType, String counterPartyID, int customerAccType,
                   String customerLEI, String customerAccountID, int introDCarry, String electronicExecution,
                   String tradingVenueID, int side, String quantity, String price, String benchmarkSecurityID,
                   int benchmarkSecurityIDType, String yield, String commission, int capacity, String primaryMarket,
                   String relatedParty, String nonResident, String feeBasedAccount) {
        this.securityID = securityID;
        this.securityIDType = String.valueOf(securityIDType);
        this.tradeID = formatTradeID(tradeID);
        this.originalTradeID = originalTradeID;
        this.transType = transType;
        this.executionDate = executionDate;
        this.executionTime = executionTime;
        this.settlementDate = settlementDate;
        this.traderID = traderID;
        this.reportingDealerID = reportingDealerID;
        this.counterPartyType = String.valueOf(counterPartyType);
        this.counterPartyID = getDealerCode(checkComboBoxNotSelectedOnNonRequired(counterPartyID));
        this.customerAccType = checkChoiceBoxNotSelectedOnNonRequired(String.valueOf(customerAccType));
        this.customerLEI = getDealerCode(checkComboBoxNotSelectedOnNonRequired(customerLEI));
        this.customerAccountID = customerAccountID;
        this.introDCarry = String.valueOf(introDCarry);
        this.electronicExecution = electronicExecution.charAt(0);
        this.tradingVenueID = getDealerCode(checkComboBoxNotSelectedOnNonRequired(tradingVenueID));
        this.side = String.valueOf(side);
        this.quantity = Integer.parseInt(quantity);
        this.price = Float.parseFloat(price);
        this.benchmarkSecurityID = benchmarkSecurityID;
        this.benchmarkSecurityIDType = checkChoiceBoxNotSelectedOnNonRequired(String.valueOf(benchmarkSecurityIDType));
        this.yield = Float.parseFloat(yield);
        this.commission = String.valueOf(commission);
        this.capacity = String.valueOf(capacity);
        this.primaryMarket = primaryMarket.charAt(0);
        this.relatedParty = relatedParty.charAt(0);
        this.nonResident = nonResident.charAt(0);
        this.feeBasedAccount = feeBasedAccount.charAt(0);
    }

    private String getDealerCode(String string) {
        AllTheDealers allTheDealers = new AllTheDealers();
        try {
            return allTheDealers.getADealerCode(string);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    String createCSVFormat() {
        String[] finalList = {getSecurityID(), String.valueOf(getSecurityIDType()), getTradeID(), getOriginalTradeID(),
                String.valueOf(getTransType()), getExecutionDate(), getExecutionTime(), getSettlementDate(), getTraderID(),
                getReportingDealerID(), String.valueOf(getCounterPartyType()), getCounterPartyID(), String.valueOf(getCustomerAccType()),
                getCustomerLEI(), getCustomerAccountID(), String.valueOf(getIntroDCarry()), String.valueOf(getElectronicExecution()), getTradingVenueID(),
                String.valueOf(getSide()), String.valueOf(getQuantity()), String.valueOf(getPrice()), getBenchmarkSecurityID(),
                String.valueOf(getBenchmarkSecurityIDType()), String.valueOf(getYield()), String.valueOf(getCommission()),
                String.valueOf(getCapacity()), String.valueOf(getPrimaryMarket()), String.valueOf(getRelatedParty()),
                String.valueOf(getNonResident()), String.valueOf(getFeeBasedAccount())};

        StringBuilder sb = new StringBuilder();
        for (String string : finalList) {
            sb.append(string);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    void writeToFile(String theData) {
        String userHomeFolder = System.getProperty("user.home");
        File file = new File(userHomeFolder + "\\IIROC_Reports");
        if (!file.exists()) {
            if (file.mkdir()) {
            }
        }
        File log = new File(userHomeFolder + "\\IIROC_Reports\\" +
                getTradeID().substring(0, 8) + "_" + getReportingDealerID() + "_KERN-MTRSK_DEBT.csv");

        try{
            FileWriter fileWriter = new FileWriter(log, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(log));

            if(bufferedReader.readLine() == null) {
                Field[] fields = getClass().getDeclaredFields();
                String[] fieldVariables = new String[30];
                for (int i = 0; i < fields.length; i++) {
                    fieldVariables[i] = fields[i].getName();
                }
                StringBuilder sb = new StringBuilder();
                for (String string : fieldVariables) {
                    sb.append(string);
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                bufferedWriter.write(String.valueOf(sb));
            }
            bufferedWriter.newLine();
            bufferedWriter.write(theData);
            bufferedReader.close();
            bufferedWriter.close();
        } catch(FileNotFoundException f){
            openErrorDialog();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    // overloaded method for when there's a correction to be made and the file will have 2 lines instead of one //
    void writeToFile(String theData, String theOtherData) {
        String userHomeFolder = System.getProperty("user.home");
        File file = new File(userHomeFolder + "\\IIROC_Reports");
        if (!file.exists()) {
            if (file.mkdir()) {
            }
        }
        File log = new File(userHomeFolder + "\\IIROC_Reports\\" +
                getTradeID().substring(0, 8) + "_" + getReportingDealerID() + "_KERN-MTRSK_DEBT.csv");
        try {
            FileWriter fileWriter = new FileWriter(log, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(log));

            if (bufferedReader.readLine() == null) {
                Field[] fields = getClass().getDeclaredFields();
                String[] fieldVariables = new String[30];
                for (int i = 0; i < fields.length; i++) {
                    fieldVariables[i] = fields[i].getName();
                }
                StringBuilder sb = new StringBuilder();
                for (String string : fieldVariables) {
                    sb.append(string);
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                bufferedWriter.write(String.valueOf(sb));
            }
            bufferedWriter.newLine();
            bufferedWriter.write(theData);
            bufferedWriter.newLine();
            bufferedWriter.write(theOtherData);
            bufferedReader.close();
            bufferedWriter.close();
        }catch(FileNotFoundException f){
            openErrorDialog();
        }catch(IOException e){
            e.printStackTrace();
        }
    }





    public void openErrorDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("FileNotFound Error");
        alert.setHeaderText("Error: ");
        alert.setContentText("File is currently open elsewhere, or was entirely not found.");
        alert.showAndWait();
    }

    public String checkComboBoxNotSelectedOnNonRequired(String string) {
        try {
            if (string.contains("--")) {
                return "";
            }
        } catch (NullPointerException e){
            return "";
        }
        return string;
    }

    public String checkChoiceBoxNotSelectedOnNonRequired(String string){
        if(string.equals("0") || string.equals("-1")){
            return "";
        } return string;
    }

    public String getSecurityID() {
        return securityID;
    }

    public String getSecurityIDType() {
        return securityIDType;
    }

    public String getTradeID() {
        return tradeID;
    }

    public void setTradeID(String tradeID){
        this.tradeID = formatTradeID(tradeID);
    }

    public String formatTradeID(String string) {
        if (string.length() == 5) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date currentdate = new Date();
            return formatter.format(currentdate) + string;
        } else {
            return string;
        }
    }

    public String getOriginalTradeID() {
        if(originalTradeID.isEmpty()){
            return "";
        }
        return originalTradeID;
    }

    public void setOriginalTradeID(String originalTradeID) {
        this.originalTradeID = originalTradeID;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType){
        this.transType = transType;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public String getTraderID() {
        return traderID;
    }

    public String getReportingDealerID() {
        return reportingDealerID;
    }

    public String getCounterPartyType() {
        return counterPartyType;
    }

    public String getCounterPartyID() {
        if(counterPartyID.isEmpty()){
            return "";
        }
        return counterPartyID;
    }

    public String getCustomerAccType() {
        return customerAccType;
    }

    public String getCustomerLEI() {
        if(customerLEI.isEmpty()){
            return "";
        }
        return customerLEI;
    }

    public String getCustomerAccountID() {
        if(customerAccountID.isEmpty()){
            return "";
        }
        return customerAccountID;
    }

    public String getIntroDCarry() {
        return introDCarry;
    }

    public char getElectronicExecution() {
        return electronicExecution;
    }

    public String getTradingVenueID() {
        if(tradingVenueID.isEmpty()){
            return "";
        }
        return tradingVenueID;
    }

    public String getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public String getBenchmarkSecurityID() {
        if(benchmarkSecurityID.isEmpty()){
            return "";
        }
        return benchmarkSecurityID;
    }

    public String getBenchmarkSecurityIDType() {
        return benchmarkSecurityIDType;
    }

    public float getYield() {
        return yield;
    }

    public String getCommission() {
        if(commission.isEmpty()){
            return "";
        }
        return commission;
    }

    public String getCapacity() {
        return capacity;
    }

    public char getPrimaryMarket() {
        return primaryMarket;
    }

    public char getRelatedParty() {
        return relatedParty;
    }

    public char getNonResident() {
        return nonResident;
    }

    public char getFeeBasedAccount() {
        return feeBasedAccount;
    }
}

