import javafx.scene.control.Alert;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ACompletedForm {

    // the field types are total chaos because I only realized half-way through that they could all just be Strings //
    private String SECURITY_ID;
    private String SECURITY_ID_TYPE;
    private String TRADE_ID;
    private String ORIG_TRADE_ID;
    private int TRANS_TYPE;
    private String EXECUTION_DATE;
    private String EXECUTION_TIME;
    private String SETTLEMENT_DATE;
    private String TRADER_ID;
    private String REPORTING_DEALER_ID;
    private String COUNTERPARTY_TYPE;
    private String COUNTERPARTY_ID;
    private String CUSTOMER_ACC_TYPE;
    private String CUSTOMER_LEI;
    private String CUSTOMER_ACCOUNT_ID;
    private String INTROD_CARRY;
    private char ELECTRONIC_EXECUTION;
    private String TRADING_VENUE_ID;
    private String SIDE;
    private int QUANTITY;
    private float PRICE;
    private String BENCHMARK_SEC_ID;
    private String BENCHMARK_SEC_ID_TYPE;
    private float YIELD;
    private String COMMISSION;
    private String CAPACITY;
    private char PRIMARY_MARKET;
    private char RELATED_PTY;
    private char NON_RESIDENT;
    private char FEE_BASED_ACCOUNT;

    // All (except transType) conversions to the appropriate format are done during construction //
    ACompletedForm(String SECURITY_ID, int SECURITY_ID_TYPE, String TRADE_ID, String ORIG_TRADE_ID, int TRANS_TYPE,
                   String EXECUTION_DATE, String EXECUTION_TIME, String SETTLEMENT_DATE, String TRADER_ID,
                   String REPORTING_DEALER_ID, int COUNTERPARTY_TYPE, String COUNTERPARTY_ID, int customerAccType,
                   String CUSTOMER_LEI, String CUSTOMER_ACCOUNT_ID, int INTROD_CARRY, String ELECTRONIC_EXECUTION,
                   String TRADING_VENUE_ID, int SIDE, String QUANTITY, String PRICE, String BENCHMARK_SEC_ID,
                   int BENCHMARK_SEC_ID_TYPE, String YIELD, String commission, int CAPACITY, String PRIMARY_MARKET,
                   String RELATED_PTY, String NON_RESIDENT, String FEE_BASED_ACCOUNT) {
        this.SECURITY_ID = SECURITY_ID;
        this.SECURITY_ID_TYPE = String.valueOf(SECURITY_ID_TYPE);
        this.TRADE_ID = formatTradeID(TRADE_ID);
        this.ORIG_TRADE_ID = ORIG_TRADE_ID;
        this.TRANS_TYPE = TRANS_TYPE;
        this.EXECUTION_DATE = EXECUTION_DATE;
        this.EXECUTION_TIME = EXECUTION_TIME;
        this.SETTLEMENT_DATE = SETTLEMENT_DATE;
        this.TRADER_ID = TRADER_ID;
        this.REPORTING_DEALER_ID = REPORTING_DEALER_ID;
        this.COUNTERPARTY_TYPE = String.valueOf(COUNTERPARTY_TYPE);
        this.COUNTERPARTY_ID = getDealerCode(checkComboBoxNotSelectedOnNonRequired(COUNTERPARTY_ID));
        this.CUSTOMER_ACC_TYPE = checkChoiceBoxNotSelectedOnNonRequired(String.valueOf(customerAccType));
        this.CUSTOMER_LEI = getDealerCode(checkComboBoxNotSelectedOnNonRequired(CUSTOMER_LEI));
        this.CUSTOMER_ACCOUNT_ID = CUSTOMER_ACCOUNT_ID;
        this.INTROD_CARRY = String.valueOf(INTROD_CARRY);
        this.ELECTRONIC_EXECUTION = ELECTRONIC_EXECUTION.charAt(0);
        this.TRADING_VENUE_ID = getDealerCode(checkComboBoxNotSelectedOnNonRequired(TRADING_VENUE_ID));
        this.SIDE = String.valueOf(SIDE);
        this.QUANTITY = Integer.parseInt(QUANTITY);
        this.PRICE = Float.parseFloat(PRICE);
        this.BENCHMARK_SEC_ID = BENCHMARK_SEC_ID;
        this.BENCHMARK_SEC_ID_TYPE = checkChoiceBoxNotSelectedOnNonRequired(String.valueOf(BENCHMARK_SEC_ID_TYPE));
        this.YIELD = Float.parseFloat(YIELD);
        this.COMMISSION = String.valueOf(commission);
        this.CAPACITY = String.valueOf(CAPACITY);
        this.PRIMARY_MARKET = PRIMARY_MARKET.charAt(0);
        this.RELATED_PTY = RELATED_PTY.charAt(0);
        this.NON_RESIDENT = NON_RESIDENT.charAt(0);
        this.FEE_BASED_ACCOUNT = FEE_BASED_ACCOUNT.charAt(0);
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
        String[] finalList = {getSECURITY_ID(), String.valueOf(getSECURITY_ID_TYPE()), getTRADE_ID(), getORIG_TRADE_ID(),
                String.valueOf(getTRANS_TYPE()), getEXECUTION_DATE(), getEXECUTION_TIME(), getSETTLEMENT_DATE(), getTRADER_ID(),
                getREPORTING_DEALER_ID(), String.valueOf(getCOUNTERPARTY_TYPE()), getCOUNTERPARTY_ID(), String.valueOf(getCUSTOMER_ACC_TYPE()),
                getCUSTOMER_LEI(), getCUSTOMER_ACCOUNT_ID(), String.valueOf(getINTROD_CARRY()), String.valueOf(getELECTRONIC_EXECUTION()), getTRADING_VENUE_ID(),
                String.valueOf(getSIDE()), String.valueOf(getQUANTITY()), String.valueOf(getPRICE()), getBENCHMARK_SEC_ID(),
                String.valueOf(getBENCHMARK_SEC_ID_TYPE()), String.valueOf(getYIELD()), String.valueOf(getCOMMISSION()),
                String.valueOf(getCAPACITY()), String.valueOf(getPRIMARY_MARKET()), String.valueOf(getRELATED_PTY()),
                String.valueOf(getNON_RESIDENT()), String.valueOf(getFEE_BASED_ACCOUNT())};

        StringBuilder sb = new StringBuilder();
        for (String string : finalList) {
            sb.append(string);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    void writeToFile(String theData) {
        String path = FXMLController.PREF_LOCATION;

        File file = new File(path + "\\IIROC_Reports");
        if (!file.exists()) {
            if (file.mkdir()) {
            }
        }
        File log = new File(path + "\\IIROC_Reports\\" +
                getTRADE_ID().substring(0, 8) + "_549300GNS5HNF05WRY38_KERN-MTRSK_DEBT.csv");

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
        String path = FXMLController.PREF_LOCATION;
        File file = new File(path + "\\IIROC_Reports");
        if (!file.exists()) {
            if (file.mkdir()) {
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String theDate = simpleDateFormat.format(date);

        File log = new File(path + "\\IIROC_Reports\\" +
                theDate + "_549300GNS5HNF05WRY38_KERN-MTRSK_DEBT.csv");
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

    public String getSECURITY_ID() {
        return SECURITY_ID;
    }

    public String getSECURITY_ID_TYPE() {
        return SECURITY_ID_TYPE;
    }

    public String getTRADE_ID() {
        return TRADE_ID;
    }

    public void setTRADE_ID(String TRADE_ID){
        this.TRADE_ID = formatTradeID(TRADE_ID);
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

    public String getORIG_TRADE_ID() {
        if(ORIG_TRADE_ID.isEmpty()){
            return "";
        }
        return ORIG_TRADE_ID;
    }

    public void setORIG_TRADE_ID(String ORIG_TRADE_ID) {
        this.ORIG_TRADE_ID = ORIG_TRADE_ID;
    }

    public int getTRANS_TYPE() {
        return TRANS_TYPE;
    }

    public void setTRANS_TYPE(int TRANS_TYPE){
        this.TRANS_TYPE = TRANS_TYPE;
    }

    public String getEXECUTION_DATE() {
        return EXECUTION_DATE;
    }

    public String getEXECUTION_TIME() {
        return EXECUTION_TIME;
    }

    public String getSETTLEMENT_DATE() {
        return SETTLEMENT_DATE;
    }

    public String getTRADER_ID() {
        return TRADER_ID;
    }

    public String getREPORTING_DEALER_ID() {
        return REPORTING_DEALER_ID;
    }

    public String getCOUNTERPARTY_TYPE() {
        return COUNTERPARTY_TYPE;
    }

    public String getCOUNTERPARTY_ID() {
        if(COUNTERPARTY_ID.isEmpty()){
            return "";
        }
        return COUNTERPARTY_ID;
    }

    public String getCUSTOMER_ACC_TYPE() {
        return CUSTOMER_ACC_TYPE;
    }

    public String getCUSTOMER_LEI() {
        if(CUSTOMER_LEI.isEmpty()){
            return "";
        }
        return CUSTOMER_LEI;
    }

    public String getCUSTOMER_ACCOUNT_ID() {
        if(CUSTOMER_ACCOUNT_ID.isEmpty()){
            return "";
        }
        return CUSTOMER_ACCOUNT_ID;
    }

    public String getINTROD_CARRY() {
        return INTROD_CARRY;
    }

    public char getELECTRONIC_EXECUTION() {
        return ELECTRONIC_EXECUTION;
    }

    public String getTRADING_VENUE_ID() {
        if(TRADING_VENUE_ID.isEmpty()){
            return "";
        }
        return TRADING_VENUE_ID;
    }

    public String getSIDE() {
        return SIDE;
    }

    public int getQUANTITY() {
        return QUANTITY;
    }

    public float getPRICE() {
        return PRICE;
    }

    public String getBENCHMARK_SEC_ID() {
        if(BENCHMARK_SEC_ID.isEmpty()){
            return "";
        }
        return BENCHMARK_SEC_ID;
    }

    public String getBENCHMARK_SEC_ID_TYPE() {
        return BENCHMARK_SEC_ID_TYPE;
    }

    public float getYIELD() {
        return YIELD;
    }

    public String getCOMMISSION() {
        if(COMMISSION.isEmpty()){
            return "";
        }
        return COMMISSION;
    }

    public String getCAPACITY() {
        return CAPACITY;
    }

    public char getPRIMARY_MARKET() {
        return PRIMARY_MARKET;
    }

    public char getRELATED_PTY() {
        return RELATED_PTY;
    }

    public char getNON_RESIDENT() {
        return NON_RESIDENT;
    }

    public char getFEE_BASED_ACCOUNT() {
        return FEE_BASED_ACCOUNT;
    }
}

