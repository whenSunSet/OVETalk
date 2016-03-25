package talk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.heshixiyang.ovetalk.R;

import java.util.List;

import talk.model.User;


/**
 * Created by asus on 2015/12/20.
 */
public class MemberAdapter extends ArrayAdapter<User> {
    private int mResource;

    public MemberAdapter(Context context, int mResource, List<User> objects) {
        super(context, mResource, objects);
        this.mResource = mResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User member=getItem(position);
        View view;
        ViewHolder holder;
        if (convertView==null){
            view= LayoutInflater.from(getContext()).inflate(mResource,null);
            holder=new ViewHolder();
            holder.name=(TextView)view.findViewById(R.id.name);
            holder.icon=(ImageView)view.findViewById(R.id.icon);
            holder.nickName=(TextView)view.findViewById(R.id.nickname);
            view.setTag(holder);
        }else {
            view=convertView;
            holder=(ViewHolder)convertView.getTag();
        }

        setResource(member,holder);

        return view;
    }
    public void setResource(User user,ViewHolder viewHolder){
        viewHolder.nickName.setText(user.getUserNickName());
        viewHolder.name.setText(user.getUserID());

    }

    class ViewHolder{
        public TextView name;
        public TextView nickName;
        public ImageView icon;

    }
}
