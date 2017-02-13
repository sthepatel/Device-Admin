package info.smitpatel.deviceadmin;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String PREFIX = MainActivity.class.getSimpleName() + ": ";

    private CheckBox checkBoxAdmin;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private static final int REQUEST_ENABLE = 1;
    private static final int SET_PASSWORD = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE:
                    checkBoxAdmin.setChecked(true);
                    AdminApp.logger(PREFIX, "Enabling Policies Now", Log.DEBUG);

                    devicePolicyManager.setMaximumTimeToLock(componentName, 3000L);
                    devicePolicyManager.setMaximumFailedPasswordsForWipe(componentName, 5);
                    devicePolicyManager.setPasswordQuality(componentName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                    devicePolicyManager.setCameraDisabled(componentName, false);

                    boolean isSufficient = devicePolicyManager.isActivePasswordSufficient();

                    if (!isSufficient) {
                        Intent setPasswordIntent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        startActivityForResult(setPasswordIntent, SET_PASSWORD);
                        devicePolicyManager.setPasswordExpirationTimeout(componentName, 10000L);
                    }
                    break;
            }
        } else {
            checkBoxAdmin.setChecked(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, MyPolicyReceiver.class);

        checkBoxAdmin = (CheckBox) findViewById(R.id.checkBoxAdmin);
        checkBoxAdmin.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.request_explanation));
            startActivityForResult(intent, REQUEST_ENABLE);
        } else {
            devicePolicyManager.removeActiveAdmin(componentName);
        }
    }

//    private void something() {
//        if(devicePolicyManager == null) {
//            devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//        }
//        List<ComponentName> activeAdmins = devicePolicyManager.getActiveAdmins();
//
//        if(activeAdmins != null && !activeAdmins.isEmpty()){
//            for(int index = 0; index < activeAdmins.size(); index++) {
//                AdminApp.logger(PREFIX, "getPackageName: " + activeAdmins.get(index).getPackageName(), Log.INFO);
//                AdminApp.logger(PREFIX, "getShortClassName: " + activeAdmins.get(index).getShortClassName(), Log.INFO);
//            }
//        } else {
//            AdminApp.logger(PREFIX, "No Active Device Policy Manager", Log.DEBUG);
//        }
//    }
}
