package com.example.safeboxv20;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String WRITE_UUID = "0000a001-0000-1000-8000-00805f9b34fb";

    private TextView mConnectionState;
    private TextView mDataField;

    private EditText mLoginPs;
    private Button mSendBtn, mOneBtn, mTwoBtn, mThreeBtn, mFourBtn, mFiveBtn, mSixBtn, mSevenBtn,
    mEightBtn, mNineBtn;

    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLEService mBluetoothLEService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // manages service lifecycle
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLEService = ((BluetoothLEService.LocalBinder) service).getService();
            if (!mBluetoothLEService.initialize()) {
                Log.e(TAG, "unable to initialize bluetooth");
                finish();
            }
            // automatically connects to the device upon successful start-up initialization.
            mBluetoothLEService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLEService = null;
        }
    };

    /* Handles various events fired by the Service.
    ACTION_GATT_CONNECTED: connected to a GATT server.
    ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
                            or notification operations.*/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            }
            else if (BluetoothLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            }
            else if (BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // show all the supported devices and characteristics on the user interface
                displayGattServices(mBluetoothLEService.getSupportedGattServices());
            }
            else if (BluetoothLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLEService.EXTRA_DATA));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLEService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLEService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLEService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        //mLoginPs = findViewById(R.id.loginPassword);
        //mSendBtn = findViewById(R.id.sendBtn);
        mOneBtn = findViewById(R.id.oneBtn);
        mTwoBtn = findViewById(R.id.twoBtn);
        mThreeBtn = findViewById(R.id.threeBtn);
        mFourBtn = findViewById(R.id.fourBtn);
        mFiveBtn = findViewById(R.id.fiveBtn);
        mSixBtn = findViewById(R.id.sixBtn);
        mSevenBtn = findViewById(R.id.sevenBtn);
        mEightBtn = findViewById(R.id.eightBtn);
        mNineBtn = findViewById(R.id.nineBtn);

        // sets up UI references
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //--------------------------------------pin pad Buttons------------------------------------------------------------

        mOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------1");
                byte[] bytes = {1};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button one send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });

        mTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------2");
                byte[] bytes = {2};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button TWO send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });

        mThreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------3");
                byte[] bytes = {3};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button THREE send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });

        mFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------4");
                byte[] bytes = {4};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button FOUR send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });

        mFiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------5");
                byte[] bytes = {5};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button FIVE send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });

        mSixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------6");
                byte[] bytes = {6};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button SIX send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });

        mSevenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------7");
                byte[] bytes = {7};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button SEVEN send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });

        mEightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------8");
                byte[] bytes = {8};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button EIGHT send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });

        mNineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here, about to send message---------------------------------------------------------------------9");
                byte[] bytes = {9};
                final BluetoothGattCharacteristic characteristic = mBluetoothLEService.writeCharacteristic();
                if (characteristic == null){
                    Log.d(TAG, "failed to send data, either the service or the characteristic" +
                            "was not found");
                    return;
                }
                Log.d("in button NINE send: ", "value of bytes = " + bytes[0]);
                characteristic.setValue(bytes);
                mBluetoothLEService.writeCharacteristic(characteristic);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLEService != null){
            final boolean result = mBluetoothLEService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result =" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLEService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        }
        else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLEService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLEService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }


    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String,String>>> gattCharacteristicData =
                new ArrayList<ArrayList<HashMap<String, String>>>();

        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        //loops through available GATT services
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this, gattServiceData, android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] {android.R.id.text1, android.R.id.text2},
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
