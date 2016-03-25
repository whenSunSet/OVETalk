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

import talk.model.Work;

/**
 * Created by heshixiyang on 2016/2/22.
 */
public class WorkAdapter extends ArrayAdapter<Work> {
    private int mResource;
    private View mView;
    public WorkAdapter(Context context, int mResource, List objects) {
        super(context, mResource, objects);
        this.mResource = mResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Work work=getItem(position);
        ViewHolder holder;
        if (convertView==null){
            mView = LayoutInflater.from(getContext()).inflate(mResource,null);
            holder=new ViewHolder();
            holder.name=(TextView) mView.findViewById(R.id.name);
            holder.typeImage=(ImageView) mView.findViewById(R.id.typeImage);
            holder.date=(TextView) mView.findViewById(R.id.date);
            mView.setTag(holder);
        }else {
            mView =convertView;
            holder=(ViewHolder)convertView.getTag();
        }

        setResource(work,holder);

        return mView;
    }

    private void setResource(Work work,ViewHolder holder){
        holder.date.setText(work.getDate());
        holder.name.setText("master name:"+work.getMaster());

        switch (work.getType()){
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
