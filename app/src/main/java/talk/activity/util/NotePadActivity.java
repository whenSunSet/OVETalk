/**
 * <pre>
 * Title: 		NotePadActivity.java
 * Project: 	GugleFile
 * Type:		org.leoly.guglefile.activities.NotePadActivity
 * Author:		255507
 * Create:	 	2012-2-23 上午10:33:58
 * Copyright: 	Copyright (c) 2012
 * Company:		
 * <pre>
 */
package talk.activity.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;


import com.example.heshixiyang.ovetalk.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import talk.util.GugleUtils;

/**
 * <pre>
 * 记事本
 * </pre>
 * @author 255507
 * @version 1.0, 2012-2-23
 */
public class NotePadActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notepad);
		EditText mctv = (EditText) findViewById(R.id.notepadView);
		mctv.setTextColor(Color.WHITE);
		mctv.setBackgroundColor(Color.GRAY);
		Intent intent = getIntent();
		String fileName = intent.getStringExtra("FILE_NAME");
		setTitle(fileName);
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			reader = new BufferedReader(isr);
			String readLine;
			while((readLine = reader.readLine()) != null)
			{
				mctv.append(readLine);
				mctv.append("\n");
			}
		}
		catch (IOException e) {
			GugleUtils.showMessage(NotePadActivity.this, e.getMessage());
		}
		finally {
			try {
				if(null != reader)
				reader.close();
			}
			catch (IOException e) {
				GugleUtils.showMessage(NotePadActivity.this, e.getMessage());
			}
		}
	}
}
