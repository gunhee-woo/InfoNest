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
import android.view.Gravity;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.room.Room;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.R;
import com.dlog.info_nest.db.WidgetDB;
import com.dlog.info_nest.db.entity.WidgetItem;
import com.dlog.info_nest.ui.WebViewActivity;
import com.dlog.info_nest.ui.palette.views.FigureView;

import java.util.List;


public class My_Widget_Provider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName testWidget = new ComponentName(context.getPackageName(), My_Widget_Provider.class.getName());

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
        }//업데이트인 경우
        else if(action.equals("Click1"))
        {
            Toast.makeText(context,"Hello",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        /*
        for(int i = 0; i < appWidgetIds.length ; i++) {
            Intent intent = new Intent(context, GridWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.simple_widget);
            rv.setRemoteAdapter(R.id.grid_view, intent);

            // The empty view is displayed when the collection has no items.
            // It should be in the same layout used to instantiate the RemoteViews
            // object above.

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }*/
        /*
        Intent serviceIntent = new Intent(context, UpdateService.class);
        serviceIntent.putExtra("WidgetIds", appWidgetIds);
        context.startService(serviceIntent);*/
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
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        PendingIntent pi = PendingIntent.getActivity(context,0,intent,0);*/
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(context, WebViewActivity.class));
        intent.putExtra("url", url);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        return pi;
    }

    //Click1 이라는 Action을 onReceive로 보낸다.
    private PendingIntent buildToastIntent(Context context)
    {
        Intent in = new Intent("Click1");
        PendingIntent pi = PendingIntent.getBroadcast(context,0,in,PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    //위젯에 멀티 버튼 추가하기
    private RemoteViews buildViews(final Context context) throws InterruptedException {
        Log.d("TTT","buildViews!  context : " + context.toString());


        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.simple_widget);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //clear image view
                if(BasicApp.prefsManager.getWidgetBackgroundColorPrefs().length() != 0) {
                    views.setInt(R.id.canvas_grid_view, "setBackgroundColor", Integer.parseInt(BasicApp.prefsManager.getWidgetBackgroundColorPrefs()));
                }
                views.setImageViewResource(R.id.img_grid_1, 0);
                views.setImageViewResource(R.id.img_grid_2, 0);
                views.setImageViewResource(R.id.img_grid_3, 0);
                views.setImageViewResource(R.id.img_grid_4, 0);
                views.setImageViewResource(R.id.img_grid_5, 0);
                views.setImageViewResource(R.id.img_grid_6, 0);
                views.setImageViewResource(R.id.img_grid_7, 0);
                views.setImageViewResource(R.id.img_grid_8, 0);
                views.setImageViewResource(R.id.img_grid_9, 0);
                List<WidgetItem> widgetItemList;
                WidgetDB db = Room.databaseBuilder(context, WidgetDB.class, "widget").build();
                widgetItemList = db.widgetDao().getAllWidget();
                for(int i = 0 ; i < widgetItemList.size() ; i++){
                    WidgetItem widgetItem = widgetItemList.get(i);
                    FigureView view = new FigureView(context, widgetItem.getFigure_layout_id(), widgetItem.getColor(),
                            widgetItem.getTitle(), widgetItem.getUrl(), widgetItem.getPosition());
                    view.measure(View.MeasureSpec.makeMeasureSpec(
                            convertDpToPixels(30), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(
                                    convertDpToPixels(30), View.MeasureSpec.EXACTLY));
                    view.getChildTextView().setTextSize(5f);
                    view.getChildTextView().setMaxLines(2);
                    view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
                    view.getChildTextView().setGravity(Gravity.CENTER);
                    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                    view.draw(new Canvas(bitmap));

                    int imgLayoutId ;
                    switch (widgetItem.getPosition()){
                        case 0 : {
                            imgLayoutId = R.id.img_grid_1;
                            break;
                        }
                        case 1 : {
                            imgLayoutId = R.id.img_grid_2;
                            break;
                        }
                        case 2 : {
                            imgLayoutId = R.id.img_grid_3;
                            break;
                        }
                        case 3 : {
                            imgLayoutId = R.id.img_grid_4;
                            break;
                        }
                        case 4 : {
                            imgLayoutId = R.id.img_grid_5;
                            break;
                        }
                        case 5 : {
                            imgLayoutId = R.id.img_grid_6;
                            break;
                        }
                        case 6 : {
                            imgLayoutId = R.id.img_grid_7;
                            break;
                        }
                        case 7 : {
                            imgLayoutId = R.id.img_grid_8;
                            break;
                        }
                        case 8 : {
                            imgLayoutId = R.id.img_grid_9;
                            break;
                        }
                        default:{
                            imgLayoutId = R.id.img_grid_1;
                        }
                    }
                    if(widgetItem.getFigure_layout_id() == R.layout.circle) {
                        views.setImageViewBitmap(imgLayoutId, getRoundedCornerBitmap(bitmap,2));
                    }else{
                        views.setImageViewBitmap(imgLayoutId, getRoundedCornerBitmap(bitmap,5));
                    }
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


