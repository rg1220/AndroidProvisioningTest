package com.randygroff.deviceownercheck;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

	private Handler mHandler;

	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminRcvr;

	private TextView mStatus;

	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mHandler.postDelayed(rUpdateStatus, 250);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler();

		mStatus = (TextView) findViewById(R.id.status);
		Button activate = (Button) findViewById(R.id.activate);
		Button deactivate = (Button) findViewById(R.id.deactivate);
		Button enableProfile = (Button) findViewById(R.id.enable_profile);
		activate.setOnClickListener(this);
		deactivate.setOnClickListener(this);
		enableProfile.setOnClickListener(this);

		mDPM = (DevicePolicyManager) this
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminRcvr = new ComponentName(this, DeviceAdminRcvr.class);
		mHandler.postDelayed(rUpdateStatus, 250);
	}

	public void activate() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminRcvr);
		startActivityForResult(intent, 243299);
		mHandler.postDelayed(rUpdateStatus, 250);
	}

	public void deactivate() {
		mDPM.removeActiveAdmin(mDeviceAdminRcvr);
		mHandler.postDelayed(rUpdateStatus, 250);
	}

	public void enableProfile() {
		mDPM.setProfileEnabled(mDeviceAdminRcvr);
		mHandler.postDelayed(rUpdateStatus, 250);
	}

	private Runnable rUpdateStatus = new Runnable() {
		@Override
		public void run() {
			boolean active = mDPM.isAdminActive(mDeviceAdminRcvr);
			boolean owner = mDPM.isDeviceOwnerApp(getPackageName());

			String msg = "Device Admin: " + (active ? "Active" : "Not Active");
			msg = msg + "\nDevice Owner: " + (owner ? "Owner" : "Not Owner");

			mStatus.setText(msg);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activate:
			activate();
			break;
		case R.id.deactivate:
			deactivate();
			break;
		case R.id.enable_profile:
			enableProfile();
			break;
		}
	}
}
