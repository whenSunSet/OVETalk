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
    private int resourceId;
    private View view;
    public TaskAdapter(Context context, int resourceId, List objects) {
        super(context, resourceId, objects);
        this.resourceId=resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task=getItem(position);
        ViewHolder holder;
        if (convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            holder=new ViewHolder();
            holder.name=(TextView)view.findViewById(R.id.name);
            holder.typeImage=(ImageView)view.findViewById(R.id.typeImage);
            holder.content=(TextView)view.findViewById(R.id.content);
            holder.date=(TextView)view.findViewById(R.id.date);
            view.setTag(holder);
        }else {
            view=convertView;
            holder=(ViewHolder)convertView.getTag();
        }

        setResource(task,holder);

        return view;
    }

    private void setResource(Task task,ViewHolder holder){
        holder.date.setText(task.getDate());
        holder.name.setText("name:"+task.getGroupName());
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
