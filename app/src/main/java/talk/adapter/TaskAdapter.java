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

import talk.model.Task;

/**
 * Created by asus on 2015/12/21.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    private int mResource;
    private View mView;
    public TaskAdapter(Context context, int mResource, List objects) {
        super(context, mResource, objects);
        this.mResource = mResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task=getItem(position);
        ViewHolder holder;
        if (convertView==null){
            mView = LayoutInflater.from(getContext()).inflate(mResource,null);
            holder=new ViewHolder();
            holder.name=(TextView) mView.findViewById(R.id.name);
            holder.typeImage=(ImageView) mView.findViewById(R.id.typeImage);
            holder.content=(TextView) mView.findViewById(R.id.target);
            holder.date=(TextView) mView.findViewById(R.id.date);
            mView.setTag(holder);
        }else {
            mView =convertView;
            holder=(ViewHolder)convertView.getTag();
        }

        setResource(task,holder);

        return mView;
    }

    private void setResource(Task task,ViewHolder holder){
        holder.date.setText(task.getDate());
        holder.name.setText("master name:"+task.getGroupId());
        holder.content.setText("target:"+task.getTarget());

        switch (task.getType()){
            case 0:
                holder.typeImage.setImageResource(R.drawable.text);
                break;
            case 1:
                holder.typeImage.setImageResource(R.drawable.music);
                break;
            case 2:
                holder.typeImage.setImageResource(R.drawable.video);
                break;
        }

    }

    class ViewHolder{
        ImageView typeImage;
        TextView name;
        TextView content;
        TextView date;
    }


}
