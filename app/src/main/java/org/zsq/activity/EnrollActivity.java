package org.zsq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.arcsoft.library.FaceService;
import com.arcsoft.library.database.module.Face;
import com.arcsoft.library.module.FaceResponse;
import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zsq.app.DemoApplication;
import org.zsq.playcamera.R;

import java.io.File;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static org.zsq.playcamera.R.id.img_face;

/**
 * create by zsq
 */
public class EnrollActivity extends BaseActivity {
    private ImageView imageView;
    private EditText editText;
    private CardView btnChange, btnEnroll,btnDelete;
    private Face face;
    private String path;
    private FaceService faceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        faceService = ((DemoApplication) getApplication()).getFaceService();
        titleString = "人脸注册";
        imageView = (ImageView) findViewById(img_face);
        editText = (EditText) findViewById(R.id.accountEdittext);
        btnChange = (CardView) findViewById(R.id.change_button);
        btnEnroll = (CardView) findViewById(R.id.enroll_button);
        btnDelete = (CardView) findViewById(R.id.delete_button);
        imageView.setOnClickListener(imageSelectClickListener);
        Intent intent = getIntent();
        if (intent != null) {
            int offset = intent.getIntExtra("offset", -1);
            if (offset >= 0) {
                face = new Face(this, offset);
                btnEnroll.setVisibility(View.GONE);
                btnChange.setOnClickListener(changeClickListener);
                btnDelete.setOnClickListener(deleteClickListener);
                path = face.getPath();
                Picasso.with(this).load(new File(face.getPath())).into(imageView);
                editText.setText(face.getName());
            } else {
                btnChange.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                btnEnroll.setOnClickListener(enrollClickListener);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private View.OnClickListener imageSelectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectImage();
        }
    };

    private View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            face.delete();
            showSuccess("成功", "");
        }
    };
    private View.OnClickListener changeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = editText.getText().toString();
            if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(name)) {
                face.setPath(path, true);
                face.setName(name, true);
                showSuccess("成功", "");
            } else {
                showError("错误", "未添加人脸或者姓名");
            }
        }
    };

    private View.OnClickListener enrollClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = editText.getText().toString();
            if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(name)) {
                showLoading("请等待", "正在人脸检测中。。。");
                if(faceService != null)
                    faceService.enroll(path,name);
                else {
                    showError("错误","服务未初始化");
                }
            } else {
                showError("错误", "未添加人脸或者姓名");
            }
        }
    };



    @Override
    public void showSuccess(final String title, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog == null) {
                    dialog = new SweetAlertDialog(EnrollActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                }
                dialog.setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("isUpdate", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                dialog.setTitleText(title).setContentText(msg).show();
            }
        });

    }


    private void selectImage() {
        BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG); // Mode：Mode.SINGLE_IMG, Mode.MULTI_IMG, Mode.VIDEO
        config.needCamera().needGif().withMaxCount(1); // 支持gif,相机，设置最大选图数
        Boxing.of(config).withIntent(EnrollActivity.this, BoxingActivity.class).start(EnrollActivity.this, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                List<BaseMedia> list = Boxing.getResult(data);
                path = list.get(0).getPath();
                Picasso.with(EnrollActivity.this).load(new File(path)).placeholder(R.drawable.head).error(R.drawable.head).into(imageView);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceResponse event) {
        if(event.getCode() != 0){
            showError("错误","出错步骤：" + event.getType() + "  错误码：" + event.getCode());
            Log.e("Tag","错误码：" + event.getCode());
        }else {
            Log.e("Tag","成功：" + event.getType());
            if(event.getType() == FaceResponse.FaceType.RECOGNITION){
                showSuccess("成功","");
            }
        }
    };
}

