package com.hc.uicomponent.placeView;


public class PlaceholderHelper {
    private PlaceholderHelper() {
    }

    public static PlaceholderHelper getInstance() {
        return PlaceholderHelperInstance.instance;
    }

    private static class PlaceholderHelperInstance {
        static PlaceholderHelper instance = new PlaceholderHelper();
    }

    public void setStatus(PlaceholderLayout layout, int status) {
        this.mLayout = layout;
        switch (status) {
            case PlaceholderLayout.SUCCESS:
            case PlaceholderLayout.ERROR:
            case PlaceholderLayout.NO_NETWORK:
            case PlaceholderLayout.LOADING:
            case PlaceholderLayout.EMPTY:
            case PlaceholderLayout.NO_NOTICE:
            case PlaceholderLayout.NO_ANNO:
                layout.setStatus(status);
                break;
        }
    }

    private PlaceholderLayout mLayout;

    public PlaceholderLayout getmLayout() {
        return mLayout;
    }

    public void setmLayout(PlaceholderLayout mLayout) {
        this.mLayout = mLayout;
    }
}
