package com.hc.uicomponent.base;


public class PlaceholderHelper {

    private PlaceholderHelper() {

    }

    public static PlaceholderHelper getInstance() {
        return PlaceholderHelperInstance.instance;
    }

    private static class PlaceholderHelperInstance {
        static PlaceholderHelper instance = new PlaceholderHelper();
    }

    public void setStatus(PlaceholderLayout layout, @PlaceholderLayout.PageState int status) {
        this.mLayout = layout;
        layout.setStatus(status,null);
    }

    private PlaceholderLayout mLayout;

    public PlaceholderLayout getmLayout() {
        return mLayout;
    }

    public void setmLayout(PlaceholderLayout mLayout) {
        this.mLayout = mLayout;
    }

}
