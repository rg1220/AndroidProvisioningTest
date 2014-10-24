package com.randygroff.nfcprovisioner;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends Activity implements CreateNdefMessageCallback {

	private Handler h = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcAdapter.setNdefPushMessageCallback(this, this);
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		try {
			Properties p = new Properties();

			p.setProperty(
					DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
					"com.randygroff.deviceownercheck");
			p.setProperty(
					DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION,
					"https://storage.googleapis.com/randy/DeviceOwnerCheck.apk");
			p.setProperty(
					DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM,
					"FRaAsqdPSjp9nC5hKIU/ElPv+e4");

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStream out = new ObjectOutputStream(bos);
			p.store(out, "");
			final byte[] bytes = bos.toByteArray();
			
			h.post(new Runnable() {
				@Override
				public void run() {
					TextView tv = (TextView) findViewById(R.id.nfc_message);
					tv.setText(new String(bytes));
				}
			});
			
			NdefMessage msg = new NdefMessage(NdefRecord.createMime(
					DevicePolicyManager.MIME_TYPE_PROVISIONING_NFC, bytes));
			return msg;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
