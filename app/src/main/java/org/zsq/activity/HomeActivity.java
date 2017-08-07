package org.zsq.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.library.database.module.Face;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;
import org.zsq.playcamera.R;

import java.io.File;

/**
 * Created by Administrator on 2017/8/2.
 */

public class HomeActivity extends BaseActivity {
    FlowLayout iconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        iconList = (FlowLayout) findViewById(R.id.thingsGridContainer);
        titleString = "主页";
        showItem();
    }

    private void showItem() {
        int i = 0;
        while (true) {
            Face face = new Face(this, i);
            if (!TextUtils.isEmpty(face.getName())) {
                LinearLayout thing_item = (LinearLayout) getLayoutInflater().inflate(R.layout.home_thing_container, iconList, false);
                TextView tv2 = (TextView) thing_item.findViewById(R.id.thingLabel);
                ImageView iv = (ImageView) thing_item.findViewById(R.id.thingTypeIcon);
                Picasso.with(this).load(new File(face.getPath())).placeholder(R.drawable.head)
                        .error(R.drawable.head)
                        .into(iv);
                tv2.setText(face.getName());
                thing_item.setId(i);
                thing_item.setOnClickListener(editClickListener);
                i++;
                iconList.addView(thing_item);
            } else
                break;
        }

        LinearLayout thing_item = (LinearLayout) getLayoutInflater().inflate(R.layout.home_add, iconList, false);
        thing_item.setOnClickListener(addClickListener);
        iconList.addView(thing_item);
    }

    private View.OnClickListener addClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, EnrollActivity.class);
            startActivityForResult(intent, 1000);
        }
    };

    private View.OnClickListener editClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, EnrollActivity.class);
            intent.putExtra("offset", v.getId());
            startActivityForResult(intent, 1000);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                boolean isUpdate = data.getBooleanExtra("isUpdate", false);
                if (isUpdate) {
                    iconList.removeAllViews();
                    showItem();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                onPermission();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1000);
        } else {
            startCameraActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity();
                } else {
                    Toast.makeText(HomeActivity.this,"由于相机权限未打开,无法进行识别。请去设置界面手动打开",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void startCameraActivity() {
        Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
        startActivity(intent);
    }
}
