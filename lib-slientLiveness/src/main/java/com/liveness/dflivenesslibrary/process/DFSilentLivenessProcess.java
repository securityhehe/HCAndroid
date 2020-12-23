package com.liveness.dflivenesslibrary.process;

import android.app.Activity;
import android.os.Bundle;

import com.dfsdk.liveness.DFLivenessSDK;
import com.liveness.dflivenesslibrary.liveness.util.Constants;

public class DFSilentLivenessProcess extends DFLivenessBaseProcess {

    public DFSilentLivenessProcess(Activity context) {
        super(context);
    }

    @Override
    protected DFLivenessSDK.DFLivenessOutputType getOutputType(Bundle bundle) {
        return DFLivenessSDK.DFLivenessOutputType.getOutputTypeByValue(Constants.MULTIIMG);
    }

    @Override
    protected boolean isSilent() {
        return true;
    }

    @Override
    protected DFLivenessSDK.DFLivenessMotion[] getMotionList() {
        DFLivenessSDK.DFLivenessMotion[] motions = new DFLivenessSDK.DFLivenessMotion[1];
        motions[0] = DFLivenessSDK.DFLivenessMotion.HOLD_STILL;
        return motions;
    }

    @Override
    protected void setDetectorParameters(DFLivenessSDK detector) {
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_SILENT_DETECT_NUMBER, 1.f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_SILENT_FACE_RET_MAX_RATE, 0.60f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_SILENT_TIME_INTERVAL, 30.f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_SILENT_FACE_OFFSET_RATE, 0.4f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_BLUR_THRESHOLD, 0.3f);

        setSilentDetectionRegion(detector);
    }
}
