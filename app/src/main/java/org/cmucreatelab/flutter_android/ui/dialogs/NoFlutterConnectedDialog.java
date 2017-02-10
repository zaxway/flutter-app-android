package org.cmucreatelab.flutter_android.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.cmucreatelab.flutter_android.R;
import org.cmucreatelab.flutter_android.activities.AppLandingActivity;
import org.cmucreatelab.flutter_android.activities.abstract_activities.BaseNavigationActivity;
import org.cmucreatelab.flutter_android.helpers.static_classes.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Steve on 10/4/2016.
 *
 * NoFlutterConnectedDialog
 *
 * A Dialog that shows the user that they cannot navigate the app until they connect to a Flutter.
 */
public class NoFlutterConnectedDialog extends BaseResizableDialog {

    private static final String noFlutterKey = "NO_FLUTTER_KEY";


    private static NoFlutterConnectedDialog newInstance(int description) {
        NoFlutterConnectedDialog noFlutterConnectedDialog = new NoFlutterConnectedDialog();

        Bundle args = new Bundle();
        args.putInt(noFlutterKey, description);
        noFlutterConnectedDialog.setArguments(args);

        return noFlutterConnectedDialog;
    }


    public static void displayDialog(BaseNavigationActivity activity, int description) {
        NoFlutterConnectedDialog noFlutterConnectedDialog = NoFlutterConnectedDialog.newInstance(description);
        noFlutterConnectedDialog.setCancelable(false);
        noFlutterConnectedDialog.show(activity.getSupportFragmentManager(), "tag");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        int resourceId = getArguments().getInt(noFlutterKey);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_no_flutter, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        builder.setView(view);
        ButterKnife.bind(this, view);

        TextView text = (TextView) view.findViewById(R.id.text_no_flutter);
        text.setText(resourceId);

        return builder.create();
    }


    @OnClick(R.id.button_connect_flutter)
    public void onClickConnectFlutter() {
        Log.d(Constants.LOG_TAG, "NoFlutterConnectedDialog.onClickConnectFlutter");
        Intent intent = new Intent(getActivity(), AppLandingActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
