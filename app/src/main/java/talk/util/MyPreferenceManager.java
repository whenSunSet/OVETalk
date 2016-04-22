package talk.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyPreferenceManager {

    public static final String MESSAGE_NOTIFY_KEY = "message_notify";
    public static final String MESSAGE_SOUND_KEY = "message_sound";
    public static final String SHOW_HEAD_KEY = "show_head";
    public static final String PULLREFRESH_SOUND_KEY = "pullrefresh_sound";
    private static SharedPreferences mSharedPreferences = null;
    private static Editor mEditor = null;

    public static void init(Context context){
        if (null == mSharedPreferences) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) ;
        }
    }

    protected static void removeKey(String key){
        mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.apply();
    }

    protected static void removeAll(){
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.apply();
    }

    public static void commitString(String key, String value){
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public static String getString(String key, String faillValue){
        return mSharedPreferences.getString(key, faillValue);
    }

    public static void commitInt(String key, int value){
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    public static int getInt(String key, int failValue){
        return mSharedPreferences.getInt(key, failValue);
    }

    public static void commitLong(String key, long value){
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, value);
        mEditor.apply();
    }

    public static long getLong(String key, long failValue) {
        return mSharedPreferences.getLong(key, failValue);
    }



    //----------------------添加的

    public static void commitBoolean(String key, boolean value){
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    public static Boolean getBoolean(String key, boolean failValue){
        return mSharedPreferences.getBoolean(key, failValue);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void commitStringList(String key,Set<String> value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putStringSet(key, value);
        mEditor.apply();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getSet(String key,Set<String> failValue){
        return mSharedPreferences.getStringSet(key,failValue);
    }

    public boolean getIsCreatSystemGroup(){
        return mSharedPreferences.getBoolean("isCreateSystemGroup",false) ;
    }

    public void setIsCreatSystemGroup(boolean isCreatSystemGroup){
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("isCreateSystemGroup", isCreatSystemGroup);
        mEditor.apply();
    }

    public String getUserId() {
        return mSharedPreferences.getString("userId","");
    }

    public void setUserId(String userId) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString("userId", userId);
        mEditor.apply();
    }

    public String getUserAll(){
        StringBuilder userAll=new StringBuilder();
        userAll.append("userIcon:").append(this.getUserIcon());
        userAll.append(",userId:").append(this.getUserId());
        userAll.append(",userNickName:").append(this.getUsreNickName());

        List<String> tagAll=getTagAll();

        for (String tag:tagAll){
            int i=1;
            userAll.append(",tag").append(i).append(":").append(tag);
            i += 1;
        }

        return userAll.toString();
    }

    public void setUserNickName(String userNickName) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString("userNickName", userNickName);
        mEditor.apply();
    }

    public String getUsreNickName (){
        return mSharedPreferences.getString("userNickName", "");
    }

    public String getUserIcon() {
        return mSharedPreferences.getString("userIcon", "");
    }

    public void setUserIcon(String userIcon) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString("userIcon", userIcon);
        mEditor.apply();
    }

    public void setTagNum(){
        mEditor = mSharedPreferences.edit();
        mEditor.putInt("tagNum",mSharedPreferences.getInt("tagNum",0)+1);
        mEditor.apply();
    }

    public int getTagNum(){
        return mSharedPreferences.getInt("tagNum",0);
    }

    public void setTag(String tag) {
        mEditor = mSharedPreferences.edit();
        setTagNum();
        mEditor.putString("tag"+getTagNum(),tag);
        mEditor.apply();
    }

    public List<String> getTagAll() {
        List<String > tagAll=new ArrayList<>();

        for (int i=1;i<=getTagNum();i++){
            tagAll.add(mSharedPreferences.getString("tag"+i,""));
        }

        return tagAll;
    }








    public String getAppId()
    {
        return mSharedPreferences.getString("appid", "");
    }

    public void setAppId(String appid)
    {
        mEditor.putString("appid", appid);
        mEditor.commit();
    }

    public String getChannelId()
    {
        return mSharedPreferences.getString("ChannelId", "");
    }

    // channel_id
    public void setChannelId(String ChannelId)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putString("ChannelId", ChannelId);
        mEditor.apply();
    }

    // 是否通知
    public boolean getMsgNotify()
    {
        return mSharedPreferences.getBoolean(MESSAGE_NOTIFY_KEY, true);
    }

    public void setMsgNotify(boolean isChecked)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(MESSAGE_NOTIFY_KEY, isChecked);
        mEditor.apply();
    }

    // 新消息是否有声音
    public boolean getMsgSound()
    {
        return mSharedPreferences.getBoolean(MESSAGE_SOUND_KEY, true);
    }

    public void setMsgSound(boolean isChecked)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(MESSAGE_SOUND_KEY, isChecked);
        mEditor.apply();
    }

    // 刷新是否有声音
    public boolean getPullRefreshSound()
    {
        return mSharedPreferences.getBoolean(PULLREFRESH_SOUND_KEY, true);
    }

    public void setPullRefreshSound(boolean isChecked)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(PULLREFRESH_SOUND_KEY, isChecked);
        mEditor.apply();
    }

    // 是否显示自己头像
    public boolean getShowHead()
    {
        return mSharedPreferences.getBoolean(SHOW_HEAD_KEY, true);
    }

    public void setShowHead(boolean isChecked)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(SHOW_HEAD_KEY, isChecked);
        mEditor.apply();
    }

    // 表情翻页效果
    public int getFaceEffect()
    {
        return mSharedPreferences.getInt("face_effects", 3);
    }

    public void setFaceEffect(int effect)
    {
        if (effect < 0 || effect > 11)
            effect = 3;
        mEditor = mSharedPreferences.edit();
        mEditor.putInt("face_effects", effect);
        mEditor.apply();
    }

}
