package talk.activity.util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.example.heshixiyang.ovetalk.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import talk.util.GugleConstants;
import talk.util.GugleUtils;

public class GugleFileActivity extends Activity {
	public static final int TEXT=0;
	public static final int MUSIC=1;
	// 共享属性的名称
	private final String GUGLE_FILE_SET = "GUGLE_FILE";
	private final String GF_SET_LIST_TYPE = "LIST_TYPE";
	// 列表数据集合 27
	private ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
	// 列表界面视图
	private ListView listView;
	// 列表数据与视图的适配器
	private SimpleAdapter adapter;
	// 目录层级的堆栈
	private Stack<String> folderStack = new Stack<>();
	// 当前的目录
	private String nowFolder;
	// 返回次数，当为2时，退出软件
	private int backIndex = 1;
	// 是否是升序
	private boolean isAsc = true;
	private SharedPreferences setting = null;
	private String fileManagePath;
	private String TEMP_BASE = null;
	private int fileType;
	private ImageButton mGoBackBtn;
	private ImageButton goBackBtn;
	private File sdFile;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		init();
		if (null == sdFile) {
			GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.no_sdcard));
			return;
		}

		// 初始化配置對象
		initSetting(TEMP_BASE);
		pushData(TEMP_BASE);
		setNowFolder(TEMP_BASE);

		// 刷新列表
		freshList(setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
		setTitle(nowFolder);
	}

	private void init(){
		folderStack.setSize(25);
		folderStack.clear();
		Intent intent = getIntent();
		fileType=intent.getIntExtra("fileType",0);
		sdFile = GugleUtils.getSDFile();
		TEMP_BASE = sdFile.getAbsolutePath();
		listView = (ListView) findViewById(R.id.fileList);

		listView.setCacheColorHint(0);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> thisAdapter, View view, int index, long arg3) {
				fileClick(thisAdapter, index);
			}
		});
		// 视图与列表数据的适配器
		//listItem 是所有的item的集合，第四个参数是每个item里面要显示的参数，第五个参数是与第四个参数对应的资源
		adapter = new SimpleAdapter(this, listItem, R.layout.file_item, new String[] { "fileTypeImg", "fileName",
				"lastModify" }, new int[] { R.id.fileTypeImage, R.id.fileName, R.id.fileLastModify, 101010, 202020 });
		listView.setAdapter(adapter);

		// 主目录按鈕
		mGoBackBtn= (ImageButton) findViewById(R.id.gohome);
		mGoBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goHome();
			}
		});

		// 上一层按钮
		goBackBtn= (ImageButton) findViewById(R.id.goback);
		goBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onKeyDown(KeyEvent.KEYCODE_BACK, null);
			}
		});
	}

	/**
	 * 转到主目录
	 */
	private void goHome() {
		if (nowFolder.equalsIgnoreCase(GugleUtils.getSDFile().getAbsolutePath())) {
			return;
		}
		// 清空堆疊，再壓入要目錄元素
		folderStack.clear();
		String path = GugleUtils.getSDFile().getAbsolutePath();
		pushData(path);
		setNowFolder(path);
		// 刷新列表
		freshList(setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
	}

	/**
	 * 压入元素，如果元素个数超过了规定的数量，则把最底层的元素弹出，再压入
	 */
	private void pushData(String data)
	{
		folderStack.push(data);
	}

	/**
	 * 取得堆栈元素，当取到最后一层时，直接返回根目录
	 */
	private String popData() {
		if (folderStack.isEmpty()) {
			return null;
		}

		return folderStack.pop();
	}


	private void freshList(int sortType) {
		List<File> fileList = GugleUtils.getSortedFiles(new File(nowFolder), sortType, isAsc);
		ArrayList<HashMap<String, Object>> result = new ArrayList<>();
		HashMap<String, Object> map ;
		Log.d("GugleFileActivity", "fileType:" + fileType);
		for (File file : fileList) {
			map = new HashMap<>();
			if (fileType==TEXT){
				if (GugleUtils.getFileType(file.isDirectory(),file.getName())==R.drawable.text
						||GugleUtils.getFileType(file.isDirectory(), file.getName())==R.drawable.folder){
					map.put("fileTypeImg", GugleUtils.getFileType(file.isDirectory(), file.getName()));
					map.put("fileName", file.getName());
					map.put("lastModify", GugleUtils.formatDate(file.lastModified()));
					map.put("absolutePath", file.getAbsolutePath());
					map.put("isFolder", file.isDirectory());
					result.add(map);
				}
			}else if (fileType==MUSIC){
				if (GugleUtils.getFileType(file.isDirectory(), file.getName())==R.drawable.music
						||GugleUtils.getFileType(file.isDirectory(), file.getName())==R.drawable.folder){
					map.put("fileTypeImg", GugleUtils.getFileType(file.isDirectory(), file.getName()));
					map.put("fileName", file.getName());
					map.put("lastModify", GugleUtils.formatDate(file.lastModified()));
					map.put("absolutePath", file.getAbsolutePath());
					map.put("isFolder", file.isDirectory());
					result.add(map);
				}
			}

		}

		if (GugleUtils.isEmpty(result)) {
			GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.nofile));
		}

		listItem.clear();
		listItem.addAll(result);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		// 返回键按下时，返回刚才操作的目录，如果已經到了最後一層，則按再次退出
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (backIndex == 2 || GugleUtils.isEmpty(nowFolder))
			{
				finish();
				return true;
			}

			// 已經退到了根節點了
			if (nowFolder.equalsIgnoreCase(TEMP_BASE))
			{
				GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.finishapp));
				freshList(setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
				pushData(nowFolder);
				backIndex++;
				setTitle(nowFolder);
				return true;
			}

			String popStr = popData();
			if (GugleUtils.isEmpty(popStr))
			{
				finish();
				return true;
			}
			setNowFolder(popStr);
			freshList(setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
			setTitle(nowFolder);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * <pre>
	 * 初始化程序配置
	 * </pre>
	 */
	private void initSetting(String basePath)
	{
		if (null == setting) {
			setting = getSharedPreferences(GUGLE_FILE_SET, MODE_PRIVATE);
		}

		if (-1 == setting.getInt(GF_SET_LIST_TYPE, -1)) {
			SharedPreferences.Editor editor = setting.edit();
			editor.putInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD);
			editor.apply();
		}
	}


	/**
	 * 点击文件时触发的事件处理
	 */

	private void fileClick(AdapterView<?> thisAdapter, int index) {
		HashMap<String, Object> map = (HashMap<String, Object>) (thisAdapter.getAdapter().getItem(index));
		boolean isFolder = Boolean.parseBoolean(String.valueOf(map.get("isFolder")));
		String absolutePath = String.valueOf(map.get("absolutePath"));
		File file = new File(absolutePath);
		if (isFolder) {
			//如果当前的item的type是文件夹
			if (file.exists()) {
				pushData(getNowFolder());
				rollbackIndex();
				setNowFolder(file.getAbsolutePath());
				freshList(setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
				setTitle(nowFolder);
			}
		}else {
			Intent intent = new Intent();
			intent.putExtra("filePath",absolutePath);
			setResult(2, intent);
			finish();

//			String fileName = file.getName();
//			if (fileName.lastIndexOf(".") > 0) {
//				int fileType = GugleUtils.getFileType(false, fileName);
//				//如果是text的形式
//				if (R.drawable.text == fileType) {
//					Intent intent = new Intent();
//					intent.setClass(GugleFileActivity.this, NotePadActivity.class);
//					intent.putExtra("filePath",absolutePath);
//					startActivity(intent);
//					finish();
//				} else {
//					String subfix = fileName.substring(fileName.lastIndexOf(".") + 1);
//					String mineType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(subfix);
//					Intent intent = new Intent(Intent.ACTION_VIEW);
//					intent.setDataAndType(Uri.fromFile(file), mineType);
//					startActivity(Intent.createChooser(intent,getString(R.string.selectChooser)));
//				}
//			}else {
//				Intent intent = new Intent();
//				intent.setDataAndType(Uri.fromFile(file), null);
//				startActivity(Intent.createChooser(intent, getString(R.string.selectChooser)));
//			}
		}
	}


	@Override
	protected void onDestroy() {
		listItem.clear();
		listItem = null;
		folderStack.clear();
		folderStack = null;
		setting = null;
		super.onDestroy();
	}

	public String getNowFolder() {
		return nowFolder;
	}

	public void setNowFolder(String nowFolder) {
		this.nowFolder = nowFolder;
	}

	private void rollbackIndex()
	{
		backIndex = 1;
	}
}