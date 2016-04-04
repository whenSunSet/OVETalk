package talk.Globle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.heshixiyang.ovetalk.BuildConfig;
import com.loopj.android.http.RequestParams;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import talk.TalkApplication;
import talk.datebase.ClickTaskDB;
import talk.datebase.ClickWorkDB;
import talk.datebase.GroupDB;
import talk.datebase.JoinGroupDB;
import talk.datebase.TaskDB;
import talk.datebase.UserDB;
import talk.datebase.WorkDB;
import talk.model.ClickTask;
import talk.model.ClickWork;
import talk.model.Group;
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

    public static final void addUserToGroup(TalkApplication talkApplication,Message message,int statu){
        UserDB userDB=talkApplication.getUserDB();
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        JoinGroup joinGroup=new JoinGroup();

        joinGroup.setGroupId(message.getGroupId());
        joinGroup.setUserId(message.getUserId());
        joinGroup.setDate(message.getDate());

        joinGroupDB.add(joinGroup);
        if (statu==GlobleData.ADD_MEMBER){
            User user=new User();
            user.setUserId(message.getUserId());
            user.setUserIcon(savaImage(talkApplication, message));
            user.setUserNick(message.getUserNickName());
            userDB.add(user);
        }
    }

    public static final void addMeToGroup(TalkApplication talkApplication,HashMap<String,Object> result){
        GroupDB groupDB=talkApplication.getGroupDB();
        TaskDB taskDB=talkApplication.getTaskDB();
        WorkDB workDB=talkApplication.getWorkDB();
        UserDB userDB=talkApplication.getUserDB();
        ClickTaskDB clickTaskDB=talkApplication.getClickTaskDB();
        ClickWorkDB clickWorkDB=talkApplication.getClickWorkDB();
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();

        groupDB.addGroup((Group)result.get("group"));
        taskDB.adds((ArrayList<Task>) result.get("tasks"));
        workDB.adds((ArrayList<Work>)result.get("works"));
        userDB.adds((ArrayList<User>)result.get("users"));
        clickTaskDB.adds((ArrayList<ClickTask>)result.get("clickTasks"));
        clickWorkDB.adds((ArrayList<ClickWork>)result.get("clickWorks"));
        joinGroupDB.adds((ArrayList<JoinGroup>)result.get("joinGroup"));
    }

    public static final ArrayList<User> findUserFromGroup(JoinGroupDB joinGroupDB,UserDB userDB,int groupId){
        ArrayList<User> list=new ArrayList<>();
        ArrayList<String> nameList;
        nameList=joinGroupDB.getMembersName(groupId);
        for (String name:nameList) {
            list.add(userDB.getMember(name));
        }
        return list;
    }

    public static final void userOutGroup(TalkApplication talkApplication, Message message){
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        ClickWorkDB clickWorkDB=talkApplication.getClickWorkDB();

        joinGroupDB.deleteMember(message.getGroupId(), message.getUserId());
        clickWorkDB.deleteClick(message.getGroupId(), message.getUserId());
    }

    public static final void deleteGroup(TalkApplication talkApplication,int groupId,String masterId){
        GroupDB groupDB=talkApplication.getGroupDB();
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        ClickTaskDB clickTaskDB=talkApplication.getClickTaskDB();
        ClickWorkDB clickWorkDB=talkApplication.getClickWorkDB();

        groupDB.delGroup(groupId);
        joinGroupDB.deleteMember(groupId,masterId);
        clickTaskDB.deleteClick(groupId,masterId);
        clickWorkDB.deleteClick(groupId, masterId);

    }

    public static boolean setTag(TalkApplication application){
        List<String > groupIds=application.getGroupDB().getGroupIds();
        Set<String> set= new HashSet<String>();
        final int[] j = new int[1];
        set.addAll(groupIds);
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

    public static ArrayList<User> findClickWorkMembers(int groupId,
                                                         int taskId,
                                                         int workId,
                                                         ClickWorkDB clickWorkDB,
                                                         UserDB userDB){
        ArrayList<User> list=new ArrayList<>();
        for (String userId:clickWorkDB.getClickMemberName(groupId,taskId,workId)){
            list.add(userDB.getMember(userId));
        }
        return list;
    }

    public static ArrayList<User> findClickTaskMembers(int groupId,
                                                       int taskId,
                                                       ClickTaskDB clickTaskDB,
                                                       UserDB userDB){
        ArrayList<User> list=new ArrayList<>();
        for (String userId:clickTaskDB.getClickMemberName(groupId, taskId)){
            list.add(userDB.getMember(userId));
        }
        return list;
    }
    public static String savaImage(TalkApplication talkApplication, final Message message){
        final String[] userIcon = new String[1];
        final File file = new File(getCacheDir(talkApplication)+"/"+message.getUserId()+".jpg");
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

    public static Bitmap getImage(Uri uri,Activity activity){
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    public static File saveIamge(Bitmap bitmap,String fileName){
        File image=new File(fileName);
        FileOutputStream fileOutputStream=null;
        if (image.exists()){
            image.delete();
        }
        try {
            fileOutputStream=new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,fileOutputStream);
            fileOutputStream.flush();;
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
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
        AsyncHttpClientUtil asyncHttpClientUtil=new AsyncHttpClientUtil();
        RequestParams requestParams=new RequestParams();
        MyAsyncHttpResponseHandler myAsyncHttpResponseHandler=new MyAsyncHttpResponseHandler(context,GlobleData.SEND_FILE);
        if (name.equals("task")){
            Task mTask=(Task)object;
            File file=new File(mTask.getPath());
            requestParams.put(GlobleData.ID_IN_GROUP, mTask.getIdInGroup());
            requestParams.put(GlobleData.GROUP_ID,mTask.getGroupId());
            requestParams.put(GlobleData.TYPE, mTask.getType());
            requestParams.put(GlobleData.TARGET, mTask.getTarget());
            requestParams.put(GlobleData.CLICK_NUMBER, mTask.getClickNum());
            requestParams.put(GlobleData.DATE, mTask.getDate());
            requestParams.put(GlobleData.FILE, file);

            asyncHttpClientUtil.post(url, requestParams,myAsyncHttpResponseHandler);
        }else if (name.equals("work")){
            Work mWork=(Work)object;
            File file=new File(mWork.getPath());

            requestParams.put(GlobleData.GROUP_ID, mWork.getGroupId());
            requestParams.put(GlobleData.MASTER, mWork.getMaster());
            requestParams.put(GlobleData.TYPE, mWork.getType());
            requestParams.put(GlobleData.ID_IN_TASK, mWork.getIdInTask());
            requestParams.put(GlobleData.TASK_ID,mWork.getTaskId());
            requestParams.put(GlobleData.CLICK_NUMBER, mWork.getClickNum());
            requestParams.put(GlobleData.DATE, mWork.getDate());
            requestParams.put(GlobleData.FILE, file);

            asyncHttpClientUtil.post(url, requestParams,myAsyncHttpResponseHandler);
        }
        return myAsyncHttpResponseHandler.isSuccess();
    }
    public static void updateFile(Part[] parts,String url){

        PostMethod filePost = new PostMethod(url);
        if (BuildConfig.DEBUG) Log.d("GlobleMethod", url);
        //  filePost.setRequestHeader("Content-type", "multipart/form-data");
        try {
            filePost.setRequestEntity(new MultipartRequestEntity(parts,filePost.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(filePost);
            if (BuildConfig.DEBUG) Log.d("GlobleMethod", "status:" + status);
            if (status == HttpStatus.SC_OK) {
                System.out.println("上传成功");
                // 上传成功
                System.out.println(filePost.getResponseBodyAsString());
            }
            else {
                System.out.println("上传失败");
                if (BuildConfig.DEBUG)
                    Log.d("GlobleMethod", "filePost.getResponseHeaders():" + filePost.getResponseHeaders());
                if (BuildConfig.DEBUG) Log.d("GlobleMethod", filePost.getResponseBodyAsString());
                // 上传失败
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            filePost.releaseConnection();
        }
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
