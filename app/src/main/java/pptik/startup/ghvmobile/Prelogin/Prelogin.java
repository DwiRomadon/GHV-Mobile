package pptik.startup.ghvmobile.Prelogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.security.PrivateKey;
import java.util.Timer;
import java.util.TimerTask;

import pptik.startup.ghvmobile.Login;
import pptik.startup.ghvmobile.MainMenu;
import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
import pptik.startup.ghvmobile.User_Admin.Admin;

public class Prelogin extends FragmentActivity {

    private PreloginAdapter preAdapter;
    private ViewPager preloginPager;

    Integer currentPage;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    private Button goTologinpage;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pralogin_activity);
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString(ApplicationConstants.REG_ID, "");
        String theRole = prefs.getString(ApplicationConstants.LEVEL_ID, "");
        if (!TextUtils.isEmpty(registrationId)) {
            if(theRole.contains("1")){
                Intent i = new Intent(Prelogin.this, Admin.class);
                i.putExtra("regId", registrationId);
                startActivity(i);
                finish();
            }else {
                Intent i = new Intent(Prelogin.this, MainMenu.class);
                i.putExtra("regId", registrationId);
                startActivity(i);
                finish();
            }

        }
        overridePendingTransition(R.anim.slidein, R.anim.slideout);
        preloginPager = (ViewPager) findViewById(R.id.pagerprelogin);
        image1 = (ImageView)findViewById(R.id.imgprelogin1);
        image2 = (ImageView)findViewById(R.id.imgprelogin2);
        image3 = (ImageView)findViewById(R.id.imgprelogin3);
        preAdapter = new PreloginAdapter(getSupportFragmentManager());
        goTologinpage=(Button)findViewById(R.id.btnLinkToLogin) ;
        goTologinpage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Prelogin.this, Login.class);
                startActivity(i);
                finish();
            }
        });
        preloginPager.setAdapter(preAdapter);
        currentPage = preloginPager.getCurrentItem();
        preloginPager.setCurrentItem(currentPage, true);

        final Handler handler = new Handler();

        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == preAdapter.getCount()) {
                    currentPage = 0;
                }
                preloginPager.setCurrentItem(currentPage++, true);


            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 4000, 7000);
        preloginPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    image2.setVisibility(View.GONE);
                    image3.setVisibility(View.GONE);
                    image1.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    image1.setVisibility(View.GONE);
                    image3.setVisibility(View.GONE);
                    image2.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    image2.setVisibility(View.GONE);
                    image1.setVisibility(View.GONE);
                    image3.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

}
