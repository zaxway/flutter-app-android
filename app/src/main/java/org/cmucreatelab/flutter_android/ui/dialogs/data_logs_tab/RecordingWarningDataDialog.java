package org.cmucreatelab.flutter_android.ui.dialogs.data_logs_tab;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.cmucreatelab.flutter_android.R;
import org.cmucreatelab.flutter_android.helpers.static_classes.Constants;
import org.cmucreatelab.flutter_android.ui.dialogs.DismissDialogListener;
import org.cmucreatelab.flutter_android.ui.dialogs.RecordingWarningDialog;

import java.io.Serializable;

/**
 * Created by Steve on 2/27/2017.
 */

public class RecordingWarningDataDialog extends RecordingWarningDialog {


    public static RecordingWarningDataDialog newInstance(Serializable serializable, String name, int times, String time, int forTimes, String forTime) {
        RecordingWarningDataDialog recordingWarningDataDialog = new RecordingWarningDataDialog();

        Bundle args = new Bundle();
        args.putSerializable(DismissDialogListener.DISMISS_KEY, serializable);
        args.putString(NAME_KEY, name);
        args.putInt(TIMES_KEY, times);
        args.putString(TIME_KEY, time);
        args.putInt(FOR_TIMES_KEY, forTimes);
        args.putString(FOR_TIME_KEY, forTime);
        recordingWarningDataDialog.setArguments(args);

        return recordingWarningDataDialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "RecordingWarningSensorDialog.onCreateDialog");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        buttonOk.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.round_orange_button_bottom_right));
        return dialog;
    }

}
