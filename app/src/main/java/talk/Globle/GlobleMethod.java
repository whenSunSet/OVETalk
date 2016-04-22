package talk.Globle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import talk.activity.fragment.Groups;
import talk.datebase.ClickTaskDB;
import talk.datebase.ClickWorkDB;
import talk.datebase.GroupDB;
import talk.datebase.JoinGroupDB;
import talk.datebase.TaskDB;
import talk.datebase.UserDB;
import talk.datebase.WorkDB;
import talk.model.ChangeBean;
import talk.model.JoinGroup;
import talk.model.Message;
import talk.model.User;
import talk.util.DialogUtil;

/**
 * Created by heshixiyang on 2016/1/22.
 */
public class GlobleMethod {
    public static void openPhotoAlbum(Activity activity,int type){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, type);
    }
    public static void addUserToGroup(TalkApplication talkApplication,Message message,int statu){
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
            user.setUserIcon(saveImage(talkApplication, message));
            user.setUserNick(message.getUserNick());
            userDB.add(user);
        }
    }

    public static void addMeToGroup(TalkApplication talkApplication,HashMap<String,Object> result){
        GroupDB groupDB=talkApplication.getGroupDB();
        TaskDB taskDB=talkApplication.getTaskDB();
        WorkDB workDB=talkApplication.getWorkDB();
        UserDB userDB=talkApplication.getUserDB();
        ClickTaskDB clickTaskDB=talkApplication.getClickTaskDB();
        ClickWorkDB clickWorkDB=talkApplication.getClickWorkDB();
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        ChangeBean changeBean=((ChangeBean)(result.get("changeBean")));

        for (User user:changeBean.getUsers()){
            user.setUserIcon("http");
        }
        groupDB.addGroup(changeBean.getGroup());
        taskDB.adds(changeBean.getTasks());
        workDB.adds(changeBean.getWorks());
        userDB.adds(changeBean.getUsers());
        clickTaskDB.adds(changeBean.getClickTasks());
        clickWorkDB.adds(changeBean.getClickWorks());
        joinGroupDB.adds(changeBean.getJoinGroups());
        GlobleMethod.saveIcon(talkApplication,GlobleData.base+changeBean.getGroup().getGroupIcon(),String.valueOf(changeBean.getGroup().getGroupId()),GlobleData.SAVE_GROUP_ICON);
        Groups.mIsFlash=true;
//        taskDB.adds((ArrayList<TaskBean>) result.get("tasks"));
//        workDB.adds((ArrayList<WorkBean>)result.get("works"));
//        userDB.adds((ArrayList<User>) result.get("users"));
//        clickTaskDB.adds((ArrayList<ClickTask>) result.get("clickTasks"));
//        clickWorkDB.adds((ArrayList<ClickWork>) result.get("clickWorks"));
//        joinGroupDB.adds((ArrayList<JoinGroup>) result.get("joinGroups"));
    }

    public static ArrayList<User> findUserFromGroup(JoinGroupDB joinGroupDB,UserDB userDB,int groupId){
        ArrayList<User> list=new ArrayList<>();
        ArrayList<String> nameList;
        nameList=joinGroupDB.getMembersName(groupId);
        for (String name:nameList) {
            list.add(userDB.getMember(name));
        }
        return list;
    }

    public static void userOutGroup(TalkApplication talkApplication, Message message){
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        ClickWorkDB clickWorkDB=talkApplication.getClickWorkDB();

        joinGroupDB.deleteMember(message.getGroupId(), message.getUserId());
        clickWorkDB.deleteClick(message.getGroupId(), message.getUserId());
    }

    public static void deleteGroup(TalkApplication talkApplication,int groupId,String masterId){
        GroupDB groupDB=talkApplication.getGroupDB();
        JoinGroupDB joinGroupDB=talkApplication.getJoinGroupDB();
        ClickTaskDB clickTaskDB=talkApplication.getClickTaskDB();
        ClickWorkDB clickWorkDB=talkApplication.getClickWorkDB();

        groupDB.delGroup(groupId);
        joinGroupDB.deleteMember(groupId,masterId);
        clickTaskDB.deleteClick(groupId, masterId);
        clickWorkDB.deleteClick(groupId, masterId);

    }

    public static void setTag(final TalkApplication application){
        List<String > groupIds=application.getGroupDB().getGroupIds();
        Set<String> set= new HashSet<String>();
        set.addAll(groupIds);
        set= JPushInterface.filterValidTags(set);


        //这个每次改变都会覆盖前面的
        JPushInterface.setTags(application, set, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i==0){
                    DialogUtil.showToast(application,"设置tag成功");
                }else {
                    DialogUtil.showToast(application,"设置tag失败");
                }
            }
        });
    }

    public static void setAlias(final TalkApplication application, String alias){
        JPushInterface.setAlias(application, alias, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i==0){
                    DialogUtil.showToast(application,"设置alias成功");
                }else {
                    DialogUtil.showToast(application,"设置alias失败");
                }
            }
        });
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
    public static String saveImage(TalkApplication talkApplication, final Message message){
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

    public static Bitmap getImageFromUri(Uri uri, Activity activity){
        try {
            // 读取uri所在的图片
            return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    public static File saveImage(Bitmap bitmap, String fileName){
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

    public static void saveIcon(final TalkApplication talkApplication, String url, String iconId, int type){
        final StringBuilder imageName=new StringBuilder(getCacheDir(talkApplication));
        if (type==GlobleData.SAVE_GROUP_ICON){
            imageName.append("/groupIcon"+iconId+".jpg");
        }else if (type==GlobleData.SAVE_USER_ICON){
            imageName.append("/userIcon"+iconId+".jpg");
        }

        RequestQueue requestQueue= Volley.newRequestQueue(talkApplication);
        ImageRequest imageRequest=new ImageRequest(url,new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap bitmap) {
                saveImage(bitmap,imageName.toString());
            }
        },60,60, Bitmap.Config.ARGB_8888,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                DialogUtil.showToast(talkApplication,"图片下载失败");
            }
        });
        requestQueue.add(imageRequest);
    }

    public static String getIconFromId(TalkApplication talkApplication,String id,int type){
        if (type==GlobleData.SAVE_GROUP_ICON){
            return getCacheDir(talkApplication)+"/groupIcon"+id+".jpg";
        }
        return getCacheDir(talkApplication)+"/userIcon"+id+".jpg";
    }
    public static String changeFileName(TalkApplication mApplication,String newName){
        String oldName= GlobleMethod.getFileDir(mApplication)+"/"+"0.jpg";
        newName=GlobleMethod.getFileDir(mApplication)+"/"+newName+".jpg";
        File file=new File(oldName);   //指定文件名及路径
        file.renameTo(new File(newName));   //改名
        return newName;
    }

    public static String saveFile(TalkApplication talkApplication,File file, String fileName){
        String oldFileName=file.getAbsolutePath();
        String prefix =oldFileName.substring(oldFileName.lastIndexOf(".")+1);
        String newFileName=getFileDir(talkApplication)+"/"+fileName+"."+prefix;
        BufferedReader bufferedReader = null;
        FileWriter fileWriter = null;
        try {
            String s;
            bufferedReader=new BufferedReader(new FileReader(file));
            fileWriter=new FileWriter(new File(newFileName));
            while ((s=bufferedReader.readLine())!=null){
                fileWriter.write(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileWriter!=null){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return newFileName;
    }

    public static void getFile(byte[] bfile, String filePath,String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+"\\"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
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
