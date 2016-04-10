package talk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.heshixiyang.ovetalk.R;

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
        Log.d("Main", "tes");
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
