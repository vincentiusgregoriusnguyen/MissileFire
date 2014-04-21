package com.example.missilefire;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.momentofgeekiness.missilelauncher.io.MLCommand;

public class MainActivity extends Activity {
	private int port = 20000;	
	private InetAddress addr;
	private Button fire;
	private static final int check = 111;
	private static ArrayList<String> hmm;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fire = (Button)findViewById(R.id.fire);

		
		try {
			addr = InetAddress.getByName("192.168.1.2");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
    	fire.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				startActivityForResult(i,check);
				return false;
			}
    	});
	}
	
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if(requestCode == check && resultCode == RESULT_OK){
				hmm = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if(hmm.contains("fire")){
					handleCommand(MLCommand.FIRE);
				}
				if(hmm.contains("attack")){
					handleCommand(MLCommand.FIRE);
				}
			} 
		}
	
	
	private void handleCommand(MLCommand command) {
		try {
			SocketManager sm = new SocketManager(addr, port);
			sm.execute(command.getCommand());
		} catch (SocketException e) {
			Toast.makeText(getApplicationContext(),"Port "+port+" in use on the Android device!", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class SocketManager extends AsyncTask<String, Boolean, Boolean> {

		private InetAddress addr;
		private DatagramSocket s;
		private int port;

		public SocketManager(InetAddress addr, int port) throws SocketException {
			this.s = new DatagramSocket();
			this.port = port;
			this.addr = addr;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			byte[] commands = params[0].getBytes();			
			try {
				for(byte command : commands) {
					DatagramPacket packet= new DatagramPacket(new byte[]{command}, 1,addr,port);					
					s.send(packet);
				}
			} catch (Exception e) {
				return Boolean.valueOf(false);
			} finally {
				close();
			}
			return Boolean.valueOf(true);
		}
		
		@Override
		protected void onPostExecute(Boolean bool) {
			if(!bool.booleanValue())
				Toast.makeText(getApplicationContext(),"Error occured when sending package!", Toast.LENGTH_SHORT).show();
		}
		
		public void close() {
			s.close();
		}
	}

}
