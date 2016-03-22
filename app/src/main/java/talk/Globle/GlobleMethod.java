package talk.Globle;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import talk.TalkApplication;
import talk.datebase.ClickTaskDB;
import talk.datebase.ClickWorkDB;
import talk.datebase.JoinGroupDB;
import talk.datebase.UserDB;
import talk.model.JoinGroup;
import talk.model.Message;
import talk.model.Task;
import talk.model.User;
import talk.model.Work;
import talk.util.AsyncHttpClientUtil;
import talk.util.MyAsyncHttpResponseHandler;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class GlobleMethod {

    public static final void addUserToGroup(TalkApplication talkApplication,Message message){
        UserDB userDB=talkApplication.getUserDB();
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        JoinGroup joinGroup=new JoinGroup();
        User user=new User();

        joinGroup.setGroupName(message.getGroupName());
        joinGroup.setMemberId(message.getUserName());
        joinGroup.setDate(message.getDate());

        user.setUserID(message.getUserName());
        user.setUserIcon(savaImage(talkApplication,message));
        user.setUserNickName(message.getUserNickName());

        userDB.add(user);
        joinGroupDB.add(joinGroup);
    }

    public static final ArrayList<User> findUserFromGroup(JoinGroupDB joinGroupDB,UserDB userDB,String groupName){
        ArrayList<User> list=new ArrayList<>();
        ArrayList<String> nameList;
        nameList=joinGroupDB.getMembersName(groupName);
        for (String name:nameList) {
            list.add(userDB.getMember(name));
        }
        return list;
    }

    public static final void quitFromGroup(TalkApplication talkApplication,Message message){
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        ClickTaskDB clickTaskDB=talkApplication.getClickTaskDB();
        ClickWorkDB clickWorkDB=talkApplication.getClickWorkDB();

        joinGroupDB.deleteMember(message.getGroupName(), message.getUserName());
        clickTaskDB.deleteClick(message.getGroupName(), message.getUserName());
        clickWorkDB.deleteClick(message.getGroupName(), message.getUserName());

    }


    public static boolean setTag(TalkApplication application){
        List<String > groupNames=application.getGroupDB().getGroupNames();
        Set<String> set= new HashSet<String>();
        final int[] j = new int[1];
        set.addAll(groupNames);
        set= JPushInterface.filterValidTags(set);

        //这个每次改变都会覆盖前面的
        JPushInterface.setTags(application, set, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

                j[0] = i;
            }
        });

        if (j[0]==0){
            return true;
        }else {
            return false;
        }
    }

    public static ArrayList<User> findClickWorkMembers(String groupName,
                                                         int taskId,
                                                         int workId,
                                                         ClickWorkDB clickWorkDB,
                                                         UserDB userDB){
        ArrayList<User> list=new ArrayList<>();
        for (String userId:clickWorkDB.getClickMemberName(groupName,taskId,workId)){
            list.add(userDB.getMember(userId));
        }
        return list;
    }

    public static ArrayList<User> findClickTaskMembers(String groupName,
                                                       int taskId,
                                                       ClickTaskDB clickTaskDB,
                                                       UserDB userDB){
        ArrayList<User> list=new ArrayList<>();
        for (String userId:clickTaskDB.getClickMemberName(groupName, taskId)){
            list.add(userDB.getMember(userId));
        }
        return list;
    }
    public static String savaImage(TalkApplication talkApplication, final Message message){
        final String[] userIcon = new String[1];
        final File file = new File(GlobleData.SD_CACHE+"/"+message.getUserName()+".jpg");
        if (file.exists()){
            file.delete();
        }
        ImageRequest imageRequest=new ImageRequest(message.getUserIcon(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                userIcon[0] =file.getName();
            }
        }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                userIcon[0]=message.getUserIcon();
            }
        });
        talkApplication.getRequestQueue().add(imageRequest);
        return userIcon[0];
    }

    //一些重要性不高的cache或者大文件放到这里，比如图片缓存
    public static String getCacheDir(Context context){
        String cachePath ;
        //Environment.getExtemalStorageState() 获取SDcard的状态
        //Environment.MEDIA_MOUNTED 手机装有SDCard,并且可以进行读写
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ||!Environment.isExternalStorageRemovable()){
            cachePath=context.getExternalCacheDir().getPath();
        }else{
            cachePath=context.getCacheDir().getPath();
        }
        return cachePath;
    }

    //一些重要性不高的file cache或者大文件放到这里
    public static String getFileDir(Context context){
        String filePath;
        //Environment.getExtemalStorageState() 获取SDcard的状态
        //Environment.MEDIA_MOUNTED 手机装有SDCard,并且可以进行读写
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ||!Environment.isExternalStorageRemovable()){
            filePath=context.getExternalCacheDir().getPath();
        }else{
            filePath=context.getCacheDir().getPath();
        }
        return filePath;
    }

    //判断文件是否已经被下载过
    public static boolean isDownLoad(String path){
        boolean isDownLoad=false;
        if (path.matches("http")){
            isDownLoad=false;
        }else {
            isDownLoad=true;
        }
        return isDownLoad;
    }

    public static String downLoadFile(){
        String path=null;

        return path;
    }
    public static String  getNowDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date=df.format(new java.util.Date());// new Date()为获取当前系统时间
        return date;
    }
    public static boolean upLoadFile(Object object,String name,String url, final Context context) throws FileNotFoundException {
        final boolean[] b=new boolean[1];
        AsyncHttpClientUtil asyncHttpClientUtil=new AsyncHttpClientUtil();
        RequestParams requestParams=new RequestParams();
        MyAsyncHttpResponseHandler myAsyncHttpResponseHandler=new MyAsyncHttpResponseHandler(context);
        if (name.equals("task")){
            Task mTask=(Task)object;
            File file=new File(mTask.getPath());
            requestParams.put(GlobleData.ID_IN_GROUP, mTask.getIdInGroup());
            requestParams.put(GlobleData.GROUP_NAME,mTask.getGroupName());
            requestParams.put(GlobleData.TYPE, mTask.getType());
            requestParams.put(GlobleData.TARGET, mTask.getTarget());
            requestParams.put(GlobleData.CLICK_NUMBER, mTask.getClickNumber());
            requestParams.put(GlobleData.DATE, mTask.getDate());
            requestParams.put(GlobleData.FILE, file);

            asyncHttpClientUtil.post(url, requestParams,myAsyncHttpResponseHandler);
        }else if (name.equals("work")){
            Work mWork=(Work)object;
            File file=new File(mWork.getPath());

            requestParams.put(GlobleData.GROUP_NAME, mWork.getGroupName());
            requestParams.put(GlobleData.MASTER, mWork.getMaster());
            requestParams.put(GlobleData.TYPE, mWork.getType());
            requestParams.put(GlobleData.ID_IN_TASK, mWork.getIdInTask());
            requestParams.put(GlobleData.TASK_ID,mWork.getTaskId());
            requestParams.put(GlobleData.CLICK_NUMBER, mWork.getClickNumber());
            requestParams.put(GlobleData.DATE, mWork.getDate());
            requestParams.put(GlobleData.FILE, file);

            asyncHttpClientUtil.post(url, requestParams,myAsyncHttpResponseHandler);
        }
        return myAsyncHttpResponseHandler.isSuccess();
    }

//    public static String GetResult(String url, List<org.apache.http.NameValuePair> formparams) {
//
//        HttpResponse response2;// 创建一个可关闭的response对象
//        HttpClient client = new DefaultHttpClient();// 创建一个http客户端，用于发送http请求
//        HttpPost post = new HttpPost(url);// 创建一个post请求对象
//        String result = "";
//        try {
//            // 将参数放入post
//            post.setEntity(new UrlEncodedFormEntity(formparams,"utf-8"));
//            response2 = client.execute(post);// 执行请求
//
//            if (response2.getStatusLine().getStatusCode() != 200) {
//                result = Const.methodString;
//                return result;
//            }
//            HttpEntity entity2 = response2.getEntity();
//            result = EntityUtils.toString(entity2);// 打印出entity2的内容
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
