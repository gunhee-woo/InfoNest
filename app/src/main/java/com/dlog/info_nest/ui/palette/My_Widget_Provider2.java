package com.dlog.info_nest.ui.palette;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.room.Room;

import com.dlog.info_nest.R;
import com.dlog.info_nest.db.WidgetDB2;
import com.dlog.info_nest.db.entity.WidgetItem2;
import com.dlog.info_nest.ui.WebViewActivity;
import com.dlog.info_nest.ui.palette.views.WidgetView;
import com.dlog.info_nest.utilities.Code;

import java.util.List;

public class My_Widget_Provider2 extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName testWidget = new ComponentName(context.getPackageName(), My_Widget_Provider2.class.getName());

        int[] widgetIds = appWidgetManager.getAppWidgetIds(testWidget);

        if(action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            if(widgetIds != null && widgetIds.length >0) {
                this.onUpdate(context, AppWidgetManager.getInstance(context), widgetIds);
            }
        }




        if(AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action))
        {
            Bundle extras = intent.getExtras();
            //Bundle 은 Key-Value 쌍으로 이루어진 일종의 해쉬맵 자료구조
            //한 Activity에서 Intent 에 putExtras로 Bundle 데이터를 넘겨주고,
            //다른 Activity에서 getExtras로 데이터를 참조하는 방식입니다.
            if(extras!=null)
            {
                int [] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if(appWidgetIds!=null && appWidgetIds.length>0)
                    this.onUpdate(context,AppWidgetManager.getInstance(context),appWidgetIds);
            }
        }

    }


    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        RemoteViews remoteViews = null;
        try {
            remoteViews = buildViews(context);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < appWidgetIds.length ; i++) {
            appWidgetManager.updateAppWidget(appWidgetIds[i],remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private PendingIntent buildURIIntent(Context context, String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(context, WebViewActivity.class));
        return PendingIntent.getActivity(context, 0, intent, 0);
    }



    //위젯에 멀티 버튼 추가하기
    private RemoteViews buildViews(final Context context) throws InterruptedException {
        Log.d("TTT","buildViews!  context : " + context.toString());


        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.simple_widget2);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //clear image view
                views.setImageViewResource(R.id.img_list_1, 0);
                views.setImageViewResource(R.id.img_list_2, 0);
                views.setImageViewResource(R.id.img_list_3, 0);
                views.setImageViewResource(R.id.img_list_4, 0);
                views.setImageViewResource(R.id.img_list_5, 0);
                views.setImageViewResource(R.id.img_list_6, 0);
                views.setImageViewResource(R.id.img_list_7, 0);
                views.setImageViewResource(R.id.img_list_8, 0);
                views.setImageViewResource(R.id.img_list_9, 0);
                List<WidgetItem2> widgetItemList;
                WidgetDB2 db = Room.databaseBuilder(context, WidgetDB2.class, "widget_list").build();
                widgetItemList = db.widgetDao2().getLatelyWidget();
                for(int i = 0 ; i < widgetItemList.size() ; i++){
                    WidgetItem2 widgetItem = widgetItemList.get(i);
                    WidgetView view = new WidgetView(context, widgetItem.getTitle(), widgetItem.getUrl(), i);
                    view.measure(View.MeasureSpec.makeMeasureSpec(
                            convertDpToPixels(300), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(
                                    convertDpToPixels(20), View.MeasureSpec.EXACTLY));
                    view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
                    Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    view.draw(new Canvas(bitmap));

                    int imgLayoutId ;
                    switch (i){
                        case 0 : {
                            imgLayoutId = R.id.img_list_1;
                            break;
                        }
                        case 1 : {
                            imgLayoutId = R.id.img_list_2;
                            break;
                        }
                        case 2 : {
                            imgLayoutId = R.id.img_list_3;
                            break;
                        }
                        case 3 : {
                            imgLayoutId = R.id.img_list_4;
                            break;
                        }
                        case 4 : {
                            imgLayoutId = R.id.img_list_5;
                            break;
                        }
                        case 5 : {
                            imgLayoutId = R.id.img_list_6;
                            break;
                        }
                        case 6 : {
                            imgLayoutId = R.id.img_list_7;
                            break;
                        }
                        case 7 : {
                            imgLayoutId = R.id.img_list_8;
                            break;
                        }
                        case 8 : {
                            imgLayoutId = R.id.img_list_9;
                            break;
                        }
                        default:{
                            imgLayoutId = R.id.img_list_1;
                        }
                    }


                    views.setImageViewBitmap(imgLayoutId, bitmap);

                    views.setOnClickPendingIntent(imgLayoutId, buildURIIntent(context, widgetItem.getUrl()));

                }
            }
        });
        t.start();
        t.join();



        return views;
    }
    /**
     * Converts DP into pixels.
     *
     * @param dp The value in DP to be converted into pixels.
     *
     * @return The converted value in pixels.
     */
    public static int convertDpToPixels(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, Resources.getSystem().getDisplayMetrics()));
    }
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int rounded) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth()/rounded;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }



}