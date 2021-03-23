package com.example.tictactoe;

public class SettingsDialogItem {

    private String itemName;
    private String[] spinnerItemNames;

    public SettingsDialogItem(String itemName) {
        this.itemName = itemName;
    }

    public SettingsDialogItem(String itemName, String[] spinnerItemNames) {
        this.itemName = itemName;
        this.spinnerItemNames = spinnerItemNames;
    }

    public String getItemName() {
        return this.itemName;
    };

    public String[] getSpinnerItemNames() {
        return this.spinnerItemNames;
    };

}
