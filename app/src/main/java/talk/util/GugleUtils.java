/**
 * <pre>
 * Title: 		RssUtils.java
 * Project: 	GugleRss
 * Type:		org.leoly.utils.RssUtils
 * Author:		255507
 * Create:	 	2011-11-3 下午3:04:55
 * Copyright: 	Copyright (c) 2011
 * Company:		
 * <pre>
 */
package talk.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.format.Time;
import android.widget.Toast;


import com.example.heshixiyang.ovetalk.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 工具类
 * </pre>
 * @author 255507
 * @version 1.0, 2011-11-3
 */
public class GugleUtils
{
	private static HashMap<String, Integer> FILE_TYPE = new HashMap<String, Integer>();

	/**
	 * @param source
	 * @return
	 */
	public static boolean isEmpty(String source)
	{
		return (null == source) || (source.trim().length() == 0);
	}

	/**
	 * @param source
	 * @return
	 */
	public static boolean isEmpty(Collection source)
	{
		return (null == source) || (source.size() == 0);
	}

	/**
	 * @param source
	 * @return
	 */
	public static boolean isEmpty(Map source)
	{
		return (null == source) || (source.size() == 0);
	}

	/**
	 * <pre>
	 * 判断手机网络是否联通
	 * </pre>
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	/**
	 * <pre>
	 * 获取当前是星期几
	 * </pre>
	 * @return
	 */
	public static int getNowWeek()
	{
		Time time = new Time();
		time.setToNow();
		return time.weekDay - 1;
	}

	/**
	 * <pre>
	 * 去掉前后空格
	 * </pre>
	 * @param source
	 * @return
	 */
	public static String trim(String source)
	{
		if (null == source)
		{
			source = "";
		}

		return source.trim();
	}

	/**
	 * <pre>
	 * 显示提示窗口
	 * </pre>
	 * @param message
	 */
	public static void showAlert(Context context, String title, String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.btn_star_big_on).setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton(R.string.sure_menu, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		builder.create();
		builder.show();
	}

	/**
	 * <pre>
	 * 显示短暂的提示信息
	 * </pre>
	 * @param message
	 */
	public static void showMessage(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * <pre>
	 * 把时间的毫秒数转换成格式化的日期格式 
	 * </pre>
	 * @param timeMills
	 * @return
	 */
	public static String formatDate(long timeMills)
	{
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(GugleConstants.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMills);
		return sdf.format(cal.getTime());
	}

	/**
	 * <pre>
	 * 取得表示文件类型的图片
	 * </pre>
	 * @param isFolder
	 * @param fileName
	 * @return
	 */
	public static int getFileType(boolean isFolder, String fileName) {
		if (isFolder) {
			return R.drawable.folder;
		}



		if (GugleUtils.isEmpty(FILE_TYPE)) {

			// 文本文件
			FILE_TYPE.put("TXT", R.drawable.text);
			FILE_TYPE.put("INI", R.drawable.text);
			FILE_TYPE.put("LOG", R.drawable.text);
			FILE_TYPE.put("TXT", R.drawable.text);
			FILE_TYPE.put("PROPERTIES", R.drawable.text);
			FILE_TYPE.put("CFG", R.drawable.text);
			FILE_TYPE.put("XML", R.drawable.text);


			// 音频文件
			FILE_TYPE.put("MP3", R.drawable.music);
			FILE_TYPE.put("OGG", R.drawable.music);
			FILE_TYPE.put("WAV", R.drawable.music);
			FILE_TYPE.put("WMA", R.drawable.music);
			FILE_TYPE.put("WMV", R.drawable.music);



			// 视频文件
			FILE_TYPE.put("MP4", R.drawable.video);
			FILE_TYPE.put("3GP", R.drawable.video);
			FILE_TYPE.put("AVI", R.drawable.video);
			FILE_TYPE.put("MPE", R.drawable.video);
			FILE_TYPE.put("MPEG", R.drawable.video);
			FILE_TYPE.put("MPG", R.drawable.video);
			FILE_TYPE.put("MPG4", R.drawable.video);
			FILE_TYPE.put("RM", R.drawable.video);
			FILE_TYPE.put("RMVB", R.drawable.video);

		}

		int index = fileName.lastIndexOf(".");
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
			fileType = fileType.toUpperCase();
			if (FILE_TYPE.containsKey(fileType)) {
				return FILE_TYPE.get(fileType);
			}

		return 0;
	}

	/**
	 * <pre>
	 * 得到按照字母顺序排序后的文件列表
	 * </pre>
	 * @param sdFile
	 * @return
	 */
	public static List<File> getSortedFiles(File sdFile, final int sortType, final boolean isAsc)
	{
		File[] files = sdFile.listFiles();
		if (null == files || files.length < 1)
		{
			return new ArrayList<File>(0);
		}
		List<File> fileList = Arrays.asList(files);
		// 根据字母顺序排序，不区分大小写
		Collections.sort(fileList, new Comparator<File>()
		{
			public int compare(File file1, File file2)
			{
				int result = 0;
				if (GugleConstants.SORT_BY_TIME == sortType)
				{
					Long temp = file1.lastModified() - file2.lastModified();
					result = temp.intValue();
				}
				else
				{
					result = file1.getName().compareToIgnoreCase(file2.getName());
				}

				return isAsc ? result : -result;
			}
		});
		return fileList;
	}

	/**
	 * <pre>
	 * 获取手机SD卡目录
	 * </pre>
	 * @return
	 */
	public static File getSDFile()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir;
	}

	/**
	 * <pre>
	 * 格式化文件大小 
	 * </pre>
	 * @param fileSizeByte
	 * @return
	 */
	public static String getFileSize(long fileSizeByte)
	{
		String byteStr = String.valueOf(fileSizeByte);
		int length = byteStr.length();
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
		df.applyPattern("0.00");
		if (length > 3 && length <= 6)
		{
			return df.format(fileSizeByte / 1024D) + "KB";
		}
		else if (length > 6 && length <= 9)
		{
			return df.format(fileSizeByte / (1024 * 1024D)) + "MB";
		}
		else if (length > 9)
		{
			return df.format(fileSizeByte / (1024 * 1024 * 1024D)) + "GB";
		}
		else
		{
			return String.valueOf(fileSizeByte) + "Byte";
		}
	}

	/**
	 * <pre>
	 * 得到一个全是true的布尔数组
	 * </pre>
	 * @param count
	 * @return
	 */
	public static boolean[] getAllChecked(int count)
	{
		boolean[] result = new boolean[count];
		for (int i = 0; i < count; i++)
		{
			result[i] = true;
		}

		return result;
	}

	/**
	 * <pre>
	 * 处理命令行中文件的空格问题
	 * </pre>
	 * @param source
	 * @return
	 */
	public static String formatBlank(String source)
	{
		if (isEmpty(source))
		{
			return source;
		}

		return source.replaceAll(" ", "\\\\ ");
	}
}
