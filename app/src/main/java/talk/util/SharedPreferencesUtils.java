package talk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

/**
 * Created by asus on 2015/12/17.
 */
public class SharedPreferencesUtils {

    private Context context;
    private String name;
    public static final String MEMBER = "member";
    public static final String TASK = "task";
    public static final String DATA_URL = "/data/data/";

    public SharedPreferencesUtils(Context context, String name) {
        this.context = context;
        this.name = name;
    }


    public void del() {
        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
        sp.edit().clear().commit();

        File file = new File(DATA_URL + context.getPackageName().toString()
                + "/shared_prefs", name + ".xml");
        if (file.exists()) {
            file.delete();
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setObject(String key, Object object) {
        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {

            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMember(String type, int id) {
        ArrayList<Integer> arrayList = getObject(type, ArrayList.class);
        arrayList.add(new Integer(id));
        setObject(type, arrayList);
    }

    public void setMembers(String type, ArrayList<Integer> id) {
        ArrayList<Integer> arrayList = getObject(type, ArrayList.class);
        for (Integer integer : id) {
            arrayList.add(integer);
        }
        setObject(type, arrayList);
    }

    public void delMember(int id) {
        ArrayList<Integer> arrayList = getObject(MEMBER, ArrayList.class);
        baseDelMember(arrayList, id);
        setObject(MEMBER, arrayList);
    }

    public void delMembers(ArrayList<Integer> id) {
        ArrayList<Integer> arrayList = getObject(MEMBER, ArrayList.class);
        for (Integer integer : id) {
            baseDelMember(arrayList, integer);
        }
        setObject(MEMBER, arrayList);
    }

    public void baseDelMember(ArrayList<Integer> arrayList, int id) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i) == id) {
                arrayList.remove(i);
                return;
            }
        }
    }
}
//
//    /**
//     * 对于外部不可见的过渡方法
//     *
//     * @param key
//     * @param clazz
//     * @param sp
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    private <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
//        T t;
//        try {
//
//            t = clazz.newInstance();
//
//            if (t instanceof Integer) {
//                return (T) Integer.valueOf(sp.getInt(key, 0));
//            } else if (t instanceof String) {
//                return (T) sp.getString(key, "");
//            } else if (t instanceof Boolean) {
//                return (T) Boolean.valueOf(sp.getBoolean(key, false));
//            } else if (t instanceof Long) {
//                return (T) Long.valueOf(sp.getLong(key, 0L));
//            } else if (t instanceof Float) {
//                return (T) Float.valueOf(sp.getFloat(key, 0L));
//            }
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//            Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//            Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
//        }
//        Log.e("system", "无法找到" + key + "对应的值");
//        return null;
//    }
//    /**
//     * 根据key和预期的value类型获取value的值
//     *
//     * @param key
//
//     * @param clazz
//     * @return
//     */
//    public <T> T getValue(String key, Class<T> clazz) {
//        if (context == null) {
//            throw new RuntimeException("请先调用带有context，name参数的构造！");
//        }
//        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
//        return getValue(key, clazz, sp);
//    }

