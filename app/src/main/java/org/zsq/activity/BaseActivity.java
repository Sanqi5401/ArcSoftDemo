/*
 * ************************************************************************
 *  *
 *  * MySnapCam CONFIDENTIAL
 *  * FILE: BaseActivity.java
 *  *
 *  *  [2009] - [2015] MySnapCam, LLC
 *  *  All Rights Reserved.
 *
 * NOTICE:
 *  * All information contained herein is, and remains the property of MySnapCam LLC.
 *  * The intellectual and technical concepts contained herein are proprietary to MySnapCam
 *  * and may be covered by U.S. and Foreign Patents,patents in process, and are protected by
 *  * trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material
 *  * is strictly forbidden unless prior written permission is obtained
 *  * MySnapCam, LLC.
 *  *
 *  *
 *  * Written: Nate Ridderman
 *  * Updated: 7/30/2015
 *
 */

package org.zsq.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import org.zsq.playcamera.R;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    protected Toolbar mscToolbar;
    protected NavigationView navigationView;
    protected DrawerLayout drawerLayout;
    protected String titleString;
    protected SweetAlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mscToolbar = (Toolbar) findViewById(R.id.msc_toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //TODO eventually remove if everything has the bar
        if (mscToolbar != null) {
            setSupportActionBar(mscToolbar);
            getSupportActionBar().setTitle(titleString);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu_white_24dp));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            drawerLayout.closeDrawers();
                            Intent intent = null;
                            switch (menuItem.getItemId()) {
                                case R.id.drawer_home:
                                    if (BaseActivity.this.getClass().equals(HomeActivity.class))
                                        return true;
                                    intent = new Intent(BaseActivity.this, HomeActivity.class);
                                    break;
                                case R.id.drawer_users:
                                    if (BaseActivity.this.getClass().equals(EnrollActivity.class))
                                        return true;
                                    intent = new Intent(BaseActivity.this, EnrollActivity.class);
                                    break;
                                case R.id.drawer_cameras:
                                    if (BaseActivity.this.getClass().equals(CameraActivity.class))
                                        return true;
                                    intent = new Intent(BaseActivity.this, CameraActivity.class);
                                    break;
                            }
                            if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                            }
                            startActivity(intent);
                            //TODO if you leave the activity and come back later, the item is stil selected.
                            //javadoc: Return true to display the item as the selected item
                            return true;
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO need to finish all activities if we want this to unbind
        // properly. currently login/home/video aren't


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        //now that we are showing and hiding things based on dynamic state (are there cameras?)
        //we must do this on every resume
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void showLoading(final String title, final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialog == null){
                    dialog = new SweetAlertDialog(BaseActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                }else {
                    dialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                }
                dialog.setCancelable(false);
                dialog.setTitleText(title).setContentText(msg).show();
            }
        });
    }

    public void showError(final String title, final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialog == null){
                    dialog = new SweetAlertDialog(BaseActivity.this,SweetAlertDialog.ERROR_TYPE);
                }else {
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
                dialog.setCancelable(false);
                dialog.setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                         sweetAlertDialog.dismiss();
                    }
                });
                dialog.setTitleText(title).setContentText(msg).show();
            }
        });
    }
    public void showSuccess(final String title, final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialog == null){
                    dialog = new SweetAlertDialog(BaseActivity.this,SweetAlertDialog.SUCCESS_TYPE);
                }else {
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                }
                dialog.setCancelable(false);
                dialog.setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
                dialog.setTitleText(title).setContentText(msg).show();
            }
        });
    }


}