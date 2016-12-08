package org.cmucreatelab.flutter_android.ui.dialogs.parents;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.cmucreatelab.flutter_android.R;
import org.cmucreatelab.flutter_android.activities.RobotActivity;
import org.cmucreatelab.flutter_android.classes.settings.AdvancedSettings;
import org.cmucreatelab.flutter_android.classes.settings.Settings;
import org.cmucreatelab.flutter_android.classes.outputs.Servo;
import org.cmucreatelab.flutter_android.classes.relationships.Relationship;
import org.cmucreatelab.flutter_android.classes.sensors.Sensor;
import org.cmucreatelab.flutter_android.helpers.static_classes.Constants;
import org.cmucreatelab.flutter_android.helpers.static_classes.MessageConstructor;
import org.cmucreatelab.flutter_android.ui.dialogs.BaseResizableDialog;
import org.cmucreatelab.flutter_android.ui.dialogs.children.AdvancedSettingsDialog;
import org.cmucreatelab.flutter_android.ui.dialogs.children.MaxPositionDialog;
import org.cmucreatelab.flutter_android.ui.dialogs.children.MaxPositionDialog.DialogMaxPositionListener;
import org.cmucreatelab.flutter_android.ui.dialogs.children.MinPositionDialog;
import org.cmucreatelab.flutter_android.ui.dialogs.children.RelationshipOutputDialog;
import org.cmucreatelab.flutter_android.ui.dialogs.children.SensorOutputDialog;

import java.io.Serializable;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Steve on 10/17/2016.
 *
 * ServoDialog
 *
 * A Dialog that shows the options for creating a link between Servo and a Sensor
 */
public class ServoDialog extends BaseResizableDialog implements Serializable,
        AdvancedSettingsDialog.DialogAdvancedSettingsListener,
        SensorOutputDialog.DialogSensorListener,
        RelationshipOutputDialog.DialogRelationshipListener,
        DialogMaxPositionListener,
        MinPositionDialog.DialogMinPositionListener {


    private DialogServoListener dialogServoListener;

    private Serializable serializable;
    private DialogFragment dialogFragment;

    private ImageView currentImageView;
    private TextView currentTextViewDescrp;
    private TextView currentTextViewItem;
    private Button saveButton;

    private Settings servoSettings;
    private Servo servo;


    private void updateViews(View view) {
        if (servo.getSettings() != null) {
            super.updateViews(view, servo);
            servoSettings = servo.getSettings();

            // max
            ImageView maxPosImg = (ImageView) view.findViewById(R.id.image_max_pos);
            RotateAnimation rotateAnimation = new RotateAnimation(0, servoSettings.getOutputMax(), Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setFillEnabled(true);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setDuration(0);
            maxPosImg.startAnimation(rotateAnimation);
            TextView maxPosTxt = (TextView) view.findViewById(R.id.text_max_pos);
            TextView maxPosValue = (TextView) view.findViewById(R.id.text_max_pos_value);
            maxPosTxt.setText(servoSettings.getSensor().getHighTextId());
            maxPosValue.setText(String.valueOf(servoSettings.getOutputMax()));

            // min
            ImageView minPosImg = (ImageView) view.findViewById(R.id.image_min_pos);
            rotateAnimation = new RotateAnimation(0, servoSettings.getOutputMin(), Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setFillEnabled(true);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setDuration(0);
            minPosImg.startAnimation(rotateAnimation);
            TextView minPosTxt = (TextView) view.findViewById(R.id.text_min_pos);
            TextView minPosValue = (TextView) view.findViewById(R.id.text_min_pos_value);
            minPosTxt.setText(servoSettings.getSensor().getLowTextId());
            minPosValue.setText(String.valueOf(servoSettings.getOutputMin()));
        }
    }


    public static ServoDialog newInstance(Servo servo, Serializable activity) {
        ServoDialog servoDialog = new ServoDialog();

        Bundle args = new Bundle();
        args.putSerializable(Servo.SERVO_KEY, servo);
        args.putSerializable(RobotActivity.SERIALIZABLE_KEY, activity);
        servoDialog.setArguments(args);

        return servoDialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "onCreateDialog");
        super.onCreateDialog(savedInstanceState);

        serializable = this;
        dialogFragment = this;

        servo = (Servo) getArguments().getSerializable(Servo.SERVO_KEY);
        dialogServoListener = (DialogServoListener) getArguments().getSerializable(RobotActivity.SERIALIZABLE_KEY);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_servos, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        builder.setView(view);
        ((TextView) view.findViewById(R.id.text_output_title)).setText(getString(R.string.set_up_servo) + " " +  String.valueOf(servo.getPortNumber()));
        ButterKnife.bind(this, view);

        servoSettings = servo.getServoSettings();

        updateViews(view);
        saveButton = (Button) view.findViewById(R.id.button_save_settings);

        return builder.create();
    }


    // OnClickListeners


    @OnClick(R.id.button_save_settings)
    public void onClickSaveSettings() {
        Log.d(Constants.LOG_TAG, "onClickSaveSettings");
        servo.setSettings(servoSettings);
        String msg = MessageConstructor.getLinkedMessage(servo);
        Log.d(Constants.LOG_TAG, msg);
        servo.setIsLinked(true, servo);
        dialogServoListener.onServoLinkListener(msg);
        this.dismiss();
    }


    @OnClick(R.id.button_remove_link)
    public void onClickRemoveLink() {
        if (servo.getSettings() != null) {
            Log.d(Constants.LOG_TAG, "onClickRemoveLink");
            String msg = MessageConstructor.getRemoveLinkMessage(servo);
            Log.d(Constants.LOG_TAG, msg);
            servo.setIsLinked(false, servo);
            servoSettings.setOutputMax(servo.getMax());
            servoSettings.setOutputMin(servo.getMin());
            dialogServoListener.onServoLinkListener(msg);
        }
        this.dismiss();
    }


    @OnClick(R.id.image_advanced_settings)
    public void onClickAdvancedSettings() {
        Log.d(Constants.LOG_TAG, "onClickAdvancedSettings");
        DialogFragment dialog = AdvancedSettingsDialog.newInstance(this, servo);
        dialog.show(dialogFragment.getFragmentManager(), "tag");
    }


    @OnClick(R.id.linear_set_linked_sensor)
    public void onClickSetLinkedSensor(View view) {
        Log.d(Constants.LOG_TAG, "onClickSetLinkedSensor");
        currentImageView = (ImageView) ((ViewGroup) view).getChildAt(0);
        View layout = ((ViewGroup) view).getChildAt(1);
        currentTextViewDescrp = (TextView) ((ViewGroup) layout).getChildAt(0);
        currentTextViewItem = (TextView) ((ViewGroup) layout).getChildAt(1);
        DialogFragment dialog = SensorOutputDialog.newInstance(serializable);
        dialog.show(dialogFragment.getFragmentManager(), "tag");
    }


    @OnClick(R.id.linear_set_relationship)
    public void onClickSetRelationship(View view) {
        Log.d(Constants.LOG_TAG, "onClickSetRelationship");
        currentImageView = (ImageView) ((ViewGroup) view).getChildAt(0);
        View layout = ((ViewGroup) view).getChildAt(1);
        currentTextViewDescrp = (TextView) ((ViewGroup) layout).getChildAt(0);
        currentTextViewItem = (TextView) ((ViewGroup) layout).getChildAt(1);
        DialogFragment dialog = RelationshipOutputDialog.newInstance(serializable);
        dialog.show(dialogFragment.getFragmentManager(), "tag");
    }


    @OnClick(R.id.linear_set_max_pos)
    public void onClickSetMaximumPosition(View view) {
        Log.d(Constants.LOG_TAG, "onClickSetMaximumPosition");
        View layout = ((ViewGroup) view).getChildAt(0);
        currentImageView = (ImageView) ((ViewGroup) layout).getChildAt(0);
        layout = ((ViewGroup) view).getChildAt(1);
        currentTextViewDescrp = (TextView) ((ViewGroup) layout).getChildAt(0);
        currentTextViewItem = (TextView) ((ViewGroup) layout).getChildAt(1);
        DialogFragment dialog = MaxPositionDialog.newInstance(serializable);
        dialog.show(dialogFragment.getFragmentManager(), "tag");
    }


    @OnClick(R.id.linear_set_min_pos)
    public void onclickSetMinimumPosition(View view) {
        Log.d(Constants.LOG_TAG, "onClickSetMinimumPosition");
        View layout = ((ViewGroup) view).getChildAt(0);
        currentImageView = (ImageView) ((ViewGroup) layout).getChildAt(0);
        layout = ((ViewGroup) view).getChildAt(1);
        currentTextViewDescrp = (TextView) ((ViewGroup) layout).getChildAt(0);
        currentTextViewItem = (TextView) ((ViewGroup) layout).getChildAt(1);
        DialogFragment dialog = MinPositionDialog.newInstance(serializable);
        dialog.show(dialogFragment.getFragmentManager(), "tag");
    }


    // Option listeners


    @Override
    public void onAdvancedSettingsSet(AdvancedSettings advancedSettings) {
        Log.d(Constants.LOG_TAG, "onAdvancedSettingsSet");
        servoSettings.setAdvancedSettings(advancedSettings);
    }


    @Override
    public void onSensorChosen(Sensor sensor) {
        if (sensor.getSensorType() != Sensor.Type.NO_SENSOR) {
            Log.d(Constants.LOG_TAG, "onSensorChosen");
            saveButton.setEnabled(true);
            currentImageView.setImageResource(sensor.getGreenImageId());
            currentTextViewDescrp.setText(R.string.linked_sensor);
            currentTextViewItem.setText(sensor.getSensorType().toString());
            servoSettings.setSensor(sensor);
        }
    }

    @Override
    public void onRelationshipChosen(Relationship relationship) {
        Log.d(Constants.LOG_TAG, "onRelationshipChosen");
        currentImageView.setImageResource(relationship.getGreenImageIdMd());
        currentTextViewDescrp.setText(R.string.relationship);
        currentTextViewItem.setText(relationship.getRelationshipType().toString());
        servoSettings.setRelationship(relationship);
    }


    @Override
    public void onMaxPosChosen(int max) {
        Log.d(Constants.LOG_TAG, "onMaxPosChosen");
        RotateAnimation rotateAnimation = new RotateAnimation(0, max, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(0);
        currentImageView.startAnimation(rotateAnimation);
        currentTextViewDescrp.setText(servoSettings.getSensor().getHighTextId());
        currentTextViewItem.setText(String.valueOf(max) + (char) 0x00B0);
        servoSettings.setOutputMax(max);
    }


    @Override
    public void onMinPosChosen(int min) {
        Log.d(Constants.LOG_TAG, "onMinPosChosen");
        RotateAnimation rotateAnimation = new RotateAnimation(0, min, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(0);
        currentImageView.startAnimation(rotateAnimation);
        currentTextViewDescrp.setText(servoSettings.getSensor().getLowTextId());
        currentTextViewItem.setText(String.valueOf(min) + (char) 0x00B0);
        servoSettings.setOutputMin(min);

    }


    public interface DialogServoListener {
        public void onServoLinkListener(String message);
    }

}
