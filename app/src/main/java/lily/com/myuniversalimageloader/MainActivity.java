package lily.com.myuniversalimageloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView uilListViewText = null;
    private TextView uilViewPagerText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uilListViewText = (TextView)findViewById(R.id.activity_main_uil_listview);
        uilViewPagerText = (TextView)findViewById(R.id.activity_main_uil_viewpager);

        uilViewPagerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UILViewPagerActivity.class);
                startActivity(intent);
            }
        });

        uilListViewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UILListViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
