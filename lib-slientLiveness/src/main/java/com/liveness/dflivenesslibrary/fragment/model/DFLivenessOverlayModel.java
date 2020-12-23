package com.liveness.dflivenesslibrary.fragment.model;


public class DFLivenessOverlayModel {
    private String showHint;
    private int borderColor;

    public DFLivenessOverlayModel(String showHint, int borderColor) {
        this.showHint = showHint;
        this.borderColor = borderColor;
    }

    public String getShowHint() {
        return showHint;
    }

    public void setShowHint(String showHint) {
        this.showHint = showHint;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }
}
