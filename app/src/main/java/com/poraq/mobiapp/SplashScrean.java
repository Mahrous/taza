package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;

import android.widget.TextView;

import org.json.JSONException;


public class SplashScrean extends AppCompatActivity {

    public static boolean activityIsVisable;
    static Context context;
    static TextView checkInternet;
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screan);
        checkInternet = findViewById(R.id.check_internet);
        context = SplashScrean.this;

        activityIsVisable = true;
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScrean.this, MainActivity.class);

                if (NetworkChangeReceive.isOnline(context) && NetworkChangeReceive.isOnline()) {
                    startActivity(mainIntent);
                    SplashScrean.this.finish();
                    checkInternet.setText("Welcom");
                } else {
                    checkInternet.setText("please Check the Internet");
                }
                try {
                    General.translator
                            .put("NE", "توظيف")
                            .put("توظيف", "NE")
                            .put("FE", "بحث فى الموظفين")
                            .put("بحث فى الموظفين", "FE")
                            .put("UE", "تحديث موظف")
                            .put("تحديث موظف", "UE")
                            .put("ER", "تعديل صلاحيات")
                            .put("تعديل صلاحيات", "ER")
                            .put("GOD", "بيانات المكتب")
                            .put("بيانات المكتب", "GOD")
                            .put("NC", "دولة جديدة")
                            .put("دولة جديدة", "NC")
                            .put("NG", "محافظة جديدة")
                            .put("محافظة جديدة", "NG")
                            .put("NT", "مدينة جديدة")
                            .put("مدينة جديدة", "NT")
                            .put("NN", "حى جديد")
                            .put("حى جديد", "NN")
                            .put("NI", "صنف جديد")
                            .put("صنف جديد", "NI")
                            .put("EI", "تعديل صنف")
                            .put("تعديل صنف", "EI")

                            .put("AII", "تعديل صورة صنف")
                            .put("تعديل صورة صنف", "aii")

                            .put("UIC", "تعديل سعر صنف")
                            .put("تعديل سعر صنف", "UIC")
                            .put("GAO", "كل الطلبات")
                            .put("كل الطلبات", "GAO")
                            .put("UO", "تحديث طلب")//
                            .put("تحديث طلب", "UO")//
                            .put("SNO", "حفظ طلب جديد")
                            .put("حفظ طلب جديد", "SNO")
                            .put("NTO", "تحويلات مخزنية")
                            .put("تحويلات مخزنية", "NTO")
                            .put("NPO", "شراء جديد")
                            .put("شراء جديد", "NPO")
                            .put("DO", "توزيع طلب")
                            .put("توزيع طلب", "DO")
                            .put("RSO", "مراجعة طلب")
                            .put("مراجعة طلب", "RSO")
                            .put("FP", "انهاء شراء")
                            .put("انهاء شراء", "FP")
                            .put("FT", "انهاء تحويلات مخزنية")
                            .put("انهاء تحويلات مخزنية", "FT")
                            .put("FSO", "انهاء بيع")
                            .put("انهاء بيع", "FSO")
                            .put("GM", "تحصيل اموال")
                            .put("تحصيل اموال", "GM")
                            .put("CMT", "الغاء تحويل أموال")
                            .put("الغاء تحويل أموال", "CMT")
                            .put("FO", "أوردرات المخزن")
                            .put("أوردرات المخزن", "FO")
                            .put("NS", "مخزن جديد")
                            .put("مخزن جديد", "NS")
                            .put("ES", "تعديل مخزن")
                            .put("تعديل مخزن", "ES")
                            .put("GCL", "قائمة الاتصال اليومية")
                            .put("قائمة الاتصال اليومية", "GCL")
                            .put("UEO", "تحديث عمليات موظف")
                            .put("تحديث عمليات موظف", "UFO")
                            .put("GUD", "عهدة موظف")
                            .put("عهدة موظف", "GUD")
                            .put("GUDS", "عهد موظفين")
                            .put("عهد موظفين", "GUDS");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        activityIsVisable = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityIsVisable = false;
    }
}

