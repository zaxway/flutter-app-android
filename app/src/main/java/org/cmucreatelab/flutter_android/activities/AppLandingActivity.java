package org.cmucreatelab.flutter_android.activities;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cmucreatelab.flutter_android.R;
import org.cmucreatelab.flutter_android.activities.abstract_activities.BaseNavigationActivity;
import org.cmucreatelab.flutter_android.adapters.LeDeviceListAdapter;
import org.cmucreatelab.flutter_android.classes.flutters.Flutter;
import org.cmucreatelab.flutter_android.classes.flutters.FlutterConnectListener;
import org.cmucreatelab.flutter_android.helpers.datalogging.DataLoggingHandler;
import org.cmucreatelab.flutter_android.helpers.GlobalHandler;
import org.cmucreatelab.flutter_android.helpers.static_classes.Constants;
import org.cmucreatelab.flutter_android.helpers.static_classes.NamingHandler;
import org.cmucreatelab.flutter_android.ui.ExtendedHorizontalScrollView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Steve on 5/26/2016.
 *
 * AppLandingActivity
 *
 * An activity that can scan for flutters nearby and connect to them.
 *
 */
public class AppLandingActivity extends BaseNavigationActivity implements FlutterConnectListener, DataLoggingHandler.DataSetPointsListener {

    private LeDeviceListAdapter mLeDeviceAdapter;
    private Timer mLeDeviceAdapterTimer;
    private boolean layoutLarge = true;
    private boolean dataPopulated = false;

    // TODO @tasota we could move this to its own class and have MelodySamrtDeviceHandler contain the instance
    private final BluetoothAdapter.LeScanCallback mLeScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            final GlobalHandler globalHandler = GlobalHandler.getInstance(getApplicationContext());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < mLeDeviceAdapter.getCount(); i++) {
                        Flutter result = (Flutter) mLeDeviceAdapter.getItem(i);
                        if (result.getBluetoothDevice().equals(device)) {
                            return;
                        }
                    }
                    // Check if the device is a flutter or not
                    String macAddress = device.getAddress();
                    // All Flutters have the same first 8 characters
                    String address = macAddress.substring(0, 8);
                    if (address.equals(Constants.FLUTTER_MAC_ADDRESS) && !Constants.addressBlackList.contains(macAddress)) {
                        // Clear out part one of landing page connection content
                        findViewById(R.id.image_flutter).setVisibility(View.INVISIBLE);
                        findViewById(R.id.text_connect_s1).setVisibility(View.INVISIBLE);
                        findViewById(R.id.text_connect_s2).setVisibility(View.INVISIBLE);
                        // Set up part two of landing page connection content
                        final TextView landingPage = (TextView) findViewById(R.id.text_app_landing_title);
                        landingPage.setText(R.string.choose_flutter);
                        findViewById(R.id.image_flutter_name_tag).setVisibility(View.VISIBLE);
                        findViewById(R.id.text_connect_s3).setVisibility(View.VISIBLE);
                        findViewById(R.id.text_connect_s3_explanation).setVisibility(View.VISIBLE);
                        findViewById(R.id.text_flutter_tag_label).setVisibility(View.VISIBLE);
                        findViewById(R.id.image_content_scan_scroll_left).setVisibility(View.VISIBLE);
                        findViewById(R.id.image_content_scan_scroll_right).setVisibility(View.VISIBLE);
                        findViewById(R.id.frame_second_scan).setVisibility(View.VISIBLE);
                        // Get Flutter and add it scan list
                        String name = NamingHandler.generateName(getApplicationContext(), device.getAddress());
                        Flutter endResult = new Flutter(device, name);
                        mLeDeviceAdapter.addDevice(endResult);
                        // TODO: RecyclerView is really what should be used here, rather than manually appending TextViews to a LinearLayout inside a HorizontalScrollView.
                        final LinearLayout list = (LinearLayout) findViewById(R.id.scan_list);
                        final TextView nameEntry = (TextView) View.inflate(getApplicationContext(), R.layout.list_item_device, null);
                        nameEntry.setTag(mLeDeviceAdapter.getCount() - 1);
                        // Ensure names in the list are formatted like they are on the back of the Flutter (2 words and then 1 word)
                        Integer replaceIdx = name.indexOf(" ", name.indexOf(" ") + 1);
                        String formattedName = name.substring(0, replaceIdx) + "\n" + name.substring(replaceIdx + 1);
                        nameEntry.setText(formattedName);
                        // Height does not seem to carry over from the list_item_device.xml layout file...
                        nameEntry.setHeight(89);
                        // Margin does not seem to carry over from the content_scan.xml layout file...
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        llp.setMargins(0, 0, 8, 0); // left, top, right, bottom
                        nameEntry.setLayoutParams(llp);
                        nameEntry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Flutter flutter = (Flutter) mLeDeviceAdapter.getItem((Integer) (v.getTag()));
                                scanForDevice(false);
                                RelativeLayout rl = (RelativeLayout) findViewById(R.id.landing_page_content);
                                final int childCount = rl.getChildCount();
                                for (int i = 0; i < childCount; i++) {
                                    View view = rl.getChildAt(i);
                                    if (view.getId() != R.id.include_toolbar_scan) {
                                        view.setVisibility(View.INVISIBLE);
                                    }
                                }
                                globalHandler.sessionHandler.startSession(AppLandingActivity.this, flutter);
                            }
                        });
                        list.addView(nameEntry);
                        final ImageView bPrevious = (ImageView) findViewById(R.id.image_content_scan_scroll_left);
                        final ImageView bNext = (ImageView) findViewById(R.id.image_content_scan_scroll_right);
                        if (list.getChildCount() > 2) {
                            bNext.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_right));
                        } else {
                            bPrevious.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.grey_left));
                            bNext.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.grey_right));
                        }
                    }
                }
            });
        }
    };


    // Class methods


    private void scanForDevice(boolean isScanning) {
        Button scan = (Button) findViewById(R.id.button_scan);
        LinearLayout listContainer = (LinearLayout) findViewById(R.id.frame_second_scan);
        LinearLayout list = (LinearLayout) findViewById(R.id.scan_list);
        GlobalHandler globalHandler = GlobalHandler.getInstance(getApplicationContext());

        globalHandler.melodySmartDeviceHandler.setFlutterScanning(isScanning, mLeScanCallBack);
        if (isScanning) {
            scan.setBackground(ContextCompat.getDrawable(this, R.drawable.round_green_white));
            scan.setText(R.string.scanning);
            scan.setTextColor(Color.BLACK);
        } else {
            TextView landingPage = (TextView) findViewById(R.id.text_app_landing_title);
            landingPage.setText(R.string.connect_flutter);
            findViewById(R.id.layout_timed_prompt).setVisibility(View.INVISIBLE);
            scan.setBackground(ContextCompat.getDrawable(this, R.drawable.round_green_button));
            scan.setText(R.string.scan);
            scan.setTextColor(Color.WHITE);
            listContainer.setVisibility(View.GONE);
        }
        mLeDeviceAdapter.clearDevices();
        list.removeAllViewsInLayout();
    }


    private void showAlertBleUnsupported() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage(R.string.ble_unsupported);
        adb.setPositiveButton(R.string.positive_response, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog dialog = adb.create();
        dialog.show();
    }


    private void showAlertBluetoothDisabled(final BluetoothAdapter bluetoothAdapter) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage(R.string.enable_bluetooth_msg);
        adb.setTitle(R.string.enable_bluetooth);
        adb.setPositiveButton(R.string.positive_response, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // nothing because we are overriding it
            }
        });

        final AlertDialog dialog = adb.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.LOG_TAG, String.valueOf(bluetoothAdapter.isEnabled()));
                if (bluetoothAdapter.isEnabled()) {
                    dialog.dismiss();
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_landing);
        ButterKnife.bind(this);
        final GlobalHandler globalHandler = GlobalHandler.getInstance(getApplicationContext());

        // Just in case someone got a hold of the app without BLE support
        PackageManager pm = getApplicationContext().getPackageManager();
        boolean isSupported = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if (!isSupported) {
            showAlertBleUnsupported();
        } else {
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                // Automatically enable bluetooth for the user, but prompt that we are doing this.
                bluetoothAdapter.enable();
                showAlertBluetoothDisabled(bluetoothAdapter);
            }

            // construct toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
            // this is checking for if the layout being used is layout-large. if the view is null, we must be using non-large layout
            if (toolbar == null) {
                layoutLarge = false;
                return;
            }
            toolbar.setContentInsetsAbsolute(0, 0);
            toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.tab_b_g));
            setSupportActionBar(toolbar);

            TextView flutterStatusText = (TextView) findViewById(R.id.text_flutter_connection_status);
            ImageView flutterStatusIcon = (ImageView) findViewById(R.id.image_flutter_status_icon);
            flutterStatusText.setText(R.string.connection_disconnected);
            flutterStatusText.setTextColor(Color.GRAY);
            flutterStatusIcon.setImageResource(R.drawable.flutterdisconnectgraphic);

            // setup adapter for LeScan
            mLeDeviceAdapter = new LeDeviceListAdapter(getLayoutInflater());

            // Setup scroll buttons on scan list
            final ExtendedHorizontalScrollView list = (ExtendedHorizontalScrollView) findViewById(R.id.scan_list_container);
            final ImageView bPrevious = (ImageView) findViewById(R.id.image_content_scan_scroll_left);
            final ImageView bNext = (ImageView) findViewById(R.id.image_content_scan_scroll_right);
            final Integer scrollAmount = 200;

            list.setOnScrollChangedListener(new ExtendedHorizontalScrollView.OnScrollChangedListener() {
                @Override
                public void onScrollChanged(int l, int t, int oldl, int oldt) {
                    Integer maxScrollX = list.getChildAt(0).getMeasuredWidth() - list.getMeasuredWidth();
                    if (l == 0) {
                        bPrevious.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.grey_left));
                    }
                    if (l > 0) {
                        bPrevious.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_left));
                        bNext.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_right));
                    }
                    if (l == maxScrollX) {
                        bNext.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.grey_right));
                    }
                }
            });

            bPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.smoothScrollBy(-scrollAmount, 0);
                }
            });
            bNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.smoothScrollBy(scrollAmount, 0);
                }
            });

            // TODO debugging screen size (can remove later)
            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;
            String toastMsg;
            switch (screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    toastMsg = "Large screen";
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    toastMsg = "Normal screen";
                    break;
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    toastMsg = "Small screen";
                    break;
                default:
                    toastMsg = "Screen size is neither large, normal or small";
            }
            toastMsg += " (dim=" + String.valueOf(dpHeight) + "x" + String.valueOf(dpWidth) + ")";
            Log.v(Constants.LOG_TAG, toastMsg);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        final GlobalHandler globalHandler = GlobalHandler.getInstance(getApplicationContext());
        if (layoutLarge) {
            scanForDevice(false);
        }
        if (globalHandler.melodySmartDeviceHandler.isConnected() && !dataPopulated) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    globalHandler.sessionHandler.createProgressDialog(AppLandingActivity.this);
                    globalHandler.sessionHandler.updateProgressDialogMessage(getResources().getString(R.string.reading_data));
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (layoutLarge) {
            scanForDevice(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final GlobalHandler globalHandler = GlobalHandler.getInstance(getApplicationContext());
        globalHandler.sessionHandler.dismissProgressDialog();
    }

    @Optional @OnClick(R.id.button_scan)
    public void onClickScan() {
        Log.d(Constants.LOG_TAG, "onClickScan");
        scanForDevice(true);
        if (mLeDeviceAdapterTimer != null) {
            mLeDeviceAdapterTimer.cancel();
        }
        mLeDeviceAdapterTimer = new Timer();
        mLeDeviceAdapterTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Long systemTime = System.currentTimeMillis();
                        for (int i = 0; i < mLeDeviceAdapter.getCount(); i++) {
                            if (mLeDeviceAdapter.getDeviceAddedTime(i) < systemTime - Constants.FLUTTER_WAITING_TIMEOUT_IN_MILLISECONDS) {
                                mLeDeviceAdapter.removeDevice(i);
                                final LinearLayout list = (LinearLayout) findViewById(R.id.scan_list);
                                TextView txtView = (TextView) list.findViewWithTag(i);
                                if (txtView != null)
                                    list.removeView(txtView);
                            } else if (mLeDeviceAdapter.getDeviceAddedTime(i) < systemTime - Constants.FLUTTER_WAITING_PROMPT_TIMEOUT_IN_MILLISECONDS) {
                                findViewById(R.id.layout_timed_prompt).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }, 0, 1000);
    }


    @Override
    public void onDataSetPointsPopulated(boolean isSuccess) {
        Log.d(Constants.LOG_TAG, "AppLanding.onDataSetPointsPopulated - Success: " + isSuccess);
        dataPopulated = true;

        // Dismiss spinner
        GlobalHandler.getInstance(this).sessionHandler.dismissProgressDialog();

        // Start new activity
        Intent intent = new Intent(this, SensorsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        // Disable back button for this Activity.
    }


    // FlutterConnectListener implementation


    @Override
    public void onFlutterConnected() {
        Log.d(Constants.LOG_TAG, "AppLandingActivity.onFlutterConnected");
        final GlobalHandler globalHandler = GlobalHandler.getInstance(this);
        mLeDeviceAdapterTimer.cancel();
        dataPopulated = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                globalHandler.sessionHandler.updateProgressDialogMessage(getResources().getString(R.string.reading_data));
            }
        });
        globalHandler.dataLoggingHandler.populatePointsAvailable(this);
    }


    @Override
    public void onFlutterDisconnected() {
        Log.d(Constants.LOG_TAG, "AppLandingActivity.onFlutterDisconnected");
        Intent intent = new Intent(this, AppLandingActivity.class);
        startActivity(intent);
    }

}