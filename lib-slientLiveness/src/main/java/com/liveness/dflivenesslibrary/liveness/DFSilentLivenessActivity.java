package com.liveness.dflivenesslibrary.liveness;

import com.liveness.dflivenesslibrary.R;
import com.liveness.dflivenesslibrary.fragment.DFProductFragmentBase;
import com.liveness.dflivenesslibrary.fragment.DFSilentLivenessFragment;


public class DFSilentLivenessActivity extends DFLivenessBaseActivity {

    @Override
    protected DFProductFragmentBase getFrament() {
        return new DFSilentLivenessFragment();
    }

    @Override
    protected int getTitleString() {
        return R.string.string_silent_liveness;
    }
}
