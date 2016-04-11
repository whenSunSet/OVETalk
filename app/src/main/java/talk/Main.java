package talk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.heshixiyang.ovetalk.R;

import cn.jpush.android.api.JPushInterface;
import talk.activity.fragment.Groups;

/**
 * Created by heshixiyang on 2016/1/29.
 */
public class Main extends Activity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        button=(Button)findViewById(R.id.in);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this, Groups.class);
                startActivity(intent);
            }
        });
    }
}
