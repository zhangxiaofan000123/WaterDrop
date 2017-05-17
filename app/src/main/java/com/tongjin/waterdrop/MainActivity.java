package com.tongjin.waterdrop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.view.drop.CoverManager;
import com.view.drop.DropCover;
import com.view.drop.WaterDrop;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CoverManager.getInstance().init(this);
        CoverManager.getInstance().setMaxDragDistance(150);
        CoverManager.getInstance().setExplosionTime(150);
        WaterDrop waterDrop = (WaterDrop) findViewById(R.id.wd);
        waterDrop.setCanMove(true);
        waterDrop.setOnDragCompeteListener(new DropCover.OnDragCompeteListener() {
            @Override
            public void onDrag() {
                Log.i(TAG, "onDrag: !!!");
            }
        });
    }
}
