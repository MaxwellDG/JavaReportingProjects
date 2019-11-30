import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// A util class for checking all the fields' validity //

class TestingInputs {

    TestingInputs() {
    }

    boolean isAlphanumeric(String theString){
        for (int i = 0; i < theString.length(); i++){
            char c = theString.charAt(i);
            if(!Character.isLetterOrDigit(c)){
                return false;
            }
        } return true;
    }

    boolean isNumerical(String theString){
        for (int i = 0; i <theString.length(); i++){
            char c = theString.charAt(i);
            if(!Character.isDigit(c)){
                return false;
            }
        } return true;
    }

    boolean isFloat(String theString) {
        for (int i = 0; i <theString.length(); i++){
            char c = theString.charAt(i);
            if(!Character.isDigit(c)){
                if(!String.valueOf(c).matches(".")){
                    return false;
                }
            }
        } return true;
    }

    boolean isValidTime(String string){
        if(string.length() != 8){
            return false;
        } else {
            if (!string.matches("^(\\d\\d:\\d\\d:\\d\\d)")) {
                return false;
            } else
                return Integer.parseInt(string.substring(0, 2)) <= 24 && Integer.parseInt(string.substring(3, 5)) <= 59 && Integer.parseInt(string.substring(6, 8)) <= 59;
        }
    }

    boolean isChoiceBoxSelected(ChoiceBox choiceBox){
        return choiceBox.getValue() != null;
    }

    boolean isComboBoxSelected(ComboBox comboBox){
        try {
            if (comboBox.getValue().toString().contains("-- *")) {
                return false;
            }
        } catch (NullPointerException e){
            return false;
        }
        return comboBox.getValue() != null;
    }

    public String getSystemsMostRecentlyUsedTradeIDAndAdd1(String reportingDealerID){
        // This will only work if the folder and file is in the LOCATION it's looking for specifically //
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = new Date();
        String date = formatter.format(currentDate);

        ArrayList<Long> allTradeIDsInFile = new ArrayList<>();
        String userHomeFolder = System.getProperty("user.home");
        boolean fileExists = new File (userHomeFolder + "\\IIROC_Reports\\" +
                date + "_" + reportingDealerID + "_KERN-MTRSK_DEBT.csv").isFile();
        if (!fileExists){
            return "10000";
        } else {
            File file = new File (userHomeFolder + "\\IIROC_Reports\\" +
                    date + "_" + reportingDealerID + "_KERN-MTRSK_DEBT.csv");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String aLine = bufferedReader.readLine();
                while (aLine != null){
                    String[] infoInALine = aLine.split(",");
                    aLine = bufferedReader.readLine();
                    if(!isNumerical(infoInALine[2])){
                        continue;
                    }
                    allTradeIDsInFile.add(Long.parseLong(infoInALine[2]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long biggestNumber = 0;
            for (long tradeID : allTradeIDsInFile){
                try{
                    if (tradeID > biggestNumber){
                        biggestNumber = tradeID;
                    }
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
            String returnNumber = String.valueOf(biggestNumber + 1);
            return returnNumber.substring(8);
        }
    }



   /* Didn't end up using this method yet it could have uses in the future. Super dope pun Max. //

   boolean isExistingDate(String theString){
        if(!isNumerical(theString)){
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String currentDate = simpleDateFormat.format(date);

        int currentYear = Integer.parseInt(currentDate.substring(0, 4));
        int currentMonth = Integer.parseInt(currentDate.substring(4, 6));
        int currentDay = Integer.parseInt(currentDate.substring(6, 8));

        int yearInput = Integer.parseInt(theString.substring(0, 4));
        int monthInput = Integer.parseInt(theString.substring(4, 6));
        int dayInput = Integer.parseInt(theString.substring(6, 8));

        if(currentYear == yearInput){
            if(monthInput > currentMonth){
                return false;
            } else if (monthInput == currentMonth) {
                if(dayInput > currentDay){
                    return false;
                }
            }
        }
        return currentYear >= yearInput && monthInput <= 12 && dayInput <= 31;
    }
    */
}
