package com.dlog.info_nest.ui.palette;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.PaletteFragmentBinding;
import com.dlog.info_nest.db.WidgetDB;
import com.dlog.info_nest.db.entity.WidgetItem;
import com.dlog.info_nest.ui.palette.views.FigureView;
import com.dlog.info_nest.utilities.Code;
import com.dlog.info_nest.utilities.ColorSaver;

import java.util.ArrayList;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

public class PaletteFragment extends Fragment {
    private PaletteFragmentBinding mPaletteFragment;

    //스티커 첫 생성할때 bottom margin 값. (쓰레기통의 중앙에 와야함)
    final int STICKER_MARGIN_BOTTOM = 10;//dp값으로 쓰일 것임. 적절한 값은 10
    //쓰레기통 이미지뷰
    ImageView trash_view;
    //this context를 저장
    Context context;
    //진동효과
    Vibrator vibrator;
    ImageView[] gridImgViews;
    GridLayout gridLayout;
    //도형을 그릴 view
    RelativeLayout r_view;

    long downTime ;
    float downX ;
    float downY ;

    int choosedColor ;

    // 도형을 add할때마다 list에 넣어주고 도형을 삭제하면 list에서도 삭제.. 아마 list에서도 자동삭제가 되겠지 ? 포인터니까??
    ArrayList<FigureView> figureList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPaletteFragment = DataBindingUtil.inflate(inflater, R.layout.palette_fragment, container, false);
        return mPaletteFragment.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        figureList = new ArrayList<>();
        Thread getWidgetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                WidgetDB widgetDB = Room.databaseBuilder(context, WidgetDB.class, "widget").build();
                List<WidgetItem> widgetItemList = widgetDB.widgetDao().getAllWidget();
                Log.d("TTT", "widgetItemList size : " + widgetItemList.size());
                setSavedWidgets((ArrayList<WidgetItem>) widgetItemList);
            }
        });
        getWidgetThread.start();


        context = this.getContext();
        r_view = view.findViewById(R.id.canvas_parent);
        if(BasicApp.prefsManager.getWidgetBackgroundColorPrefs().length() != 0){
            choosedColor = Integer.parseInt(BasicApp.prefsManager.getWidgetBackgroundColorPrefs());
            r_view.setBackgroundColor(choosedColor);
        }
        vibrator = (Vibrator)this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        gridImgViews = new ImageView[]{
                view.findViewById(R.id.img_grid_1), view.findViewById(R.id.img_grid_2), view.findViewById(R.id.img_grid_3), view.findViewById(R.id.img_grid_4),
                view.findViewById(R.id.img_grid_5), view.findViewById(R.id.img_grid_6), view.findViewById(R.id.img_grid_7), view.findViewById(R.id.img_grid_8), view.findViewById(R.id.img_grid_9)
        };
        gridLayout = view.findViewById(R.id.canvas_grid_view);
        //db 에 저장된 url 도형들을 가져와서 미리 grid layout에 뿌린다.
        //url 도형 추가를 선택하면  해당 도형을 this에 추가 .
        //grid layout의 위치를 사용자가 지정하게 한다.  그 위치에 이미 다른 도형이 있으면  "겹치는 도형이 있습니다!" 라는 인터페이스
        //위치에 도형을 놓고 확인 버튼을 누르면 저장.  DB에도 저장.  >  위젯 업데이트 ?


        trash_view = view.findViewById(R.id.img_trash);



        //도형 추가 버튼 (임시)
        Button btn_add = view.findViewById(R.id.btn_sticker_add);
        btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(context, PopupActivity.class);
                intent2.putExtra("RequestCode" , Code.RQ_TOPOPUP_ADD);
                startActivityForResult(intent2, Code.RQ_TOPOPUP_ADD);
            }
        });
        //배경색 변경(임시)
        Button btn_back_color_change = view.findViewById(R.id.btn_back_color_change);
        btn_back_color_change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        //저장버튼 (임시)
        Button btn_save = view.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int size = figureList.size();
                final WidgetItem[] widgetItems = new WidgetItem[size];
                for (int i = 0; i < size; i++) {
                    FigureView view = figureList.get(i);
                    Log.d("TTT", i + "번 도형 : " + figureList.get(i).getPosition());
                    //db에 저장.
                    if(view.getPosition() != -1) {
                        widgetItems[i] = new WidgetItem(view.getLayoutResId(), view.getColorId(), view.getTitle(),
                                view.getUrl(), view.getPosition(), view.getX() - (r_view.getWidth()/2f - view.getWidth()/2f), view.getY() - (r_view.getHeight() - view.getHeight()));
                    }else{
                        Toast.makeText(context, "위젯의 위치를 지정해주세요!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WidgetDB widgetDB = Room.databaseBuilder(context, WidgetDB.class, "widget").build();
                        widgetDB.widgetDao().deleteAll();
                        widgetDB.widgetDao().insertWidgetItem(widgetItems);
                        BasicApp.prefsManager.saveWidgetBackgroundPrefs(String.valueOf(choosedColor));
                    }
                });
                t.start();
                try {
                    t.join();
                    Intent intent = new Intent(context, My_Widget_Provider.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    context.sendBroadcast(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });


        //lp.setMargins(0,-95,9,0);
        //imageView2.setLayoutParams(lp);
        /*
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lp.setMargins(0,-95,9,0);
                        imageView2.setLayoutParams(lp);
                    }
                });
            }
        });
        t.start();*/

        try {
            getWidgetThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void setSavedWidgets(ArrayList<WidgetItem> widgetItemList){
        for(int i = 0 ; i < widgetItemList.size() ; i++){
            final WidgetItem widgetItem = widgetItemList.get(i);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
            FigureView figureView = new FigureView(context, widgetItem.getFigure_layout_id(), widgetItem.getColor(), widgetItem.getTitle(), widgetItem.getUrl(), widgetItem.getPosition());
            figureView.setOnTouchListener(new StickerOnTouch());
            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, R.id.canvas_parent);
            p.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.canvas_parent);
            figureView.setLayoutParams(p);
            figureView.setX(widgetItem.getX());
            figureView.setY(widgetItem.getY());
            r_view.addView(figureView);
            figureList.add(figureView);
        }
    }
    //도형 객체의 중심이 gridlayout 영역안에 들어왔는지 체크하는 함수
    private boolean isInGridLayout(float y, float height){
        if( y + height/2f < (gridLayout.getY() + gridLayout.getHeight()) && y + height/2f > gridLayout.getY()) {
            return true;
        }
        else return false;
    }

    /***
     * 도형객체의 x , y 좌표를 받아서 도형객체의 중심이 gridlayout의 어떤 이미지뷰 영역안에 들어와있는지 확인하고
     * 해당 이미지뷰를 return
     * @param v 도형 객체
     * @return 도형객체가 있는 이미지뷰
     */
    private ImageView getRangedView(FigureView v){
        float x = v.getX() + ((float)v.getWidth())/2f;
        float y = v.getY() + ((float)v.getHeight())/2f;
        int pos = -1;
        if(y > gridLayout.getY() + (float)gridLayout.getHeight() * (2f/3f)){
            if(x > (gridLayout.getX() + ((float)gridLayout.getWidth()))*(2f/3f)) pos = 8;

            else if(x > (float)gridLayout.getWidth() * (1f/3f)) pos = 7;

            else pos = 6;

        }
        else if( y > gridLayout.getY() + (float)gridLayout.getHeight() * (1f/3f)){
            if(x > (gridLayout.getX() + ((float)gridLayout.getWidth()))*(2f/3f)) pos = 5;
            else if(x > (float)gridLayout.getWidth() * (1f/3f)) pos = 4;
            else pos = 3;
        }else{
            if(x > (gridLayout.getX() + ((float)gridLayout.getWidth()))*(2f/3f)) pos = 2;
            else if(x > (float)gridLayout.getWidth() * (1f/3f)) pos = 1;
            else pos = 0;
        }
        v.setPosition(pos);
        return gridImgViews[pos];
    }



    class StickerOnTouch implements View.OnTouchListener{
        float fromX = 0f;
        float fromY = 0f;
        //지금 현재 trash에 들어와있는가?
        boolean isInTrash = false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.bringToFront();
            Log.d("TTT","onTouch!!");
            if(v != null && event != null){
                float parentWidth = ((ViewGroup)v.getParent()).getWidth();// 부모 view의 width
                float parentHeight = ((ViewGroup)v.getParent()).getHeight(); // 부모 view의 height
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        Log.d("TTT","onTouch down !!");
                        downTime = System.currentTimeMillis();
                        downX = v.getX();
                        downY = v.getY();
                        fromX = event.getX();
                        fromY = event.getY();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trash_view.setVisibility(View.VISIBLE);
                                gridLayout.setVisibility(View.VISIBLE);
                            }
                        });
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if(!isInTrash){
                            if(isRanged((float)(v.getX() + v.getWidth()*0.5),
                                    (float)(v.getY() + v.getHeight()*0.5),
                                    parentWidth)
                            ){
                                vibrator.vibrate(100);

                                Animation view_ani = AnimationUtils.loadAnimation(context, R.anim.trash_anim);
                                Animation trash_ani = AnimationUtils.loadAnimation(context, R.anim.trash_big_anim);
                                trash_view.startAnimation(trash_ani);
                                v.startAnimation(view_ani);
                                isInTrash = true;
                            }

                        }else{//쓰레기통 안에 있는 상태에서  바깥으로 빠져나가는 경우
                            if(!isRanged((float)(v.getX()+v.getWidth()*0.5),(float)(v.getY()+v.getHeight()*0.5), parentWidth)){
                                Animation view_ani = AnimationUtils.loadAnimation(context, R.anim.trash_out);
                                Animation trash_ani = AnimationUtils.loadAnimation(context, R.anim.trash_reset_anim);
                                trash_view.startAnimation(trash_ani);
                                v.startAnimation(view_ani);
                                isInTrash = false;
                            }
                        }
                        v.setX(v.getX() + event.getX() - v.getWidth()/2);
                        v.setY(v.getY() + event.getY() - v.getHeight()/2);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {

                        if(isInTrash){
                            //v를 삭제
                            r_view.removeView(v);
                            figureList.remove(v);
                            //reset trash view
                            Animation trash_ani = AnimationUtils.loadAnimation(context, R.anim.trash_reset_anim);
                            trash_view.startAnimation(trash_ani);
                            gridLayout.setVisibility(View.INVISIBLE);
                            trash_view.setVisibility(View.INVISIBLE);

                        }
                        if(isInGridLayout(v.getY(), (float)v.getHeight())){
                            //어떤 사각형 이미지 영역에 들어왔는지 알아낸 후
                            //해당 이미지 뷰와 도형 뷰에 이벤트를 준다.
                            ImageView imageView = getRangedView((FigureView)v);//현재 도형 객체가 속한 사각 이미지 뷰
                            //도형 객체가 속한 사각이미지 뷰 중심으로 이동.
                            v.setX((imageView.getX() + (float)imageView.getWidth()/2f) - (float)v.getWidth()/2f);
                            v.setY(gridLayout.getY() + (imageView.getY() + (float)imageView.getHeight()/2f) - (float)v.getHeight()/2f);

                            gridLayout.setVisibility(View.INVISIBLE);
                            trash_view.setVisibility(View.INVISIBLE);
                        }

                        //아래코드는 반드시 실행되어야함. 건드리지 말아야함.
                        if(v.getX() < 0){
                            v.setX((float)0);
                        }else if((v.getX() + v.getWidth()) > parentWidth){
                            v.setX(parentWidth - v.getWidth());
                        }
                        if(v.getY() < 0){
                            v.setY((float)0);
                        }else if((v.getY()+v.getHeight() > parentHeight)) {
                            v.setY(parentHeight - v.getHeight());
                        }
                        //시간차가 210 이하 이면서  도형의 움직임이 80이하  클릭으로 간주.
                        Log.d("TTT", "down - up 시간 차 : " + (downTime - System.currentTimeMillis()));
                        Log.d("TTT", "down - up 거리 차 : " + (Math.sqrt(Math.pow(v.getX() - downX, 2) + Math.pow(v.getY() - downY , 2))));
                        if(Math.abs(downTime - System.currentTimeMillis()) < 210  && Math.sqrt(Math.pow(v.getX() - downX, 2) + Math.pow(v.getY() - downY , 2)) < 80){
                            Log.d("TTT", "onClick!!");
                            Intent updateIntent = new Intent(context, PopupActivity.class);
                            updateIntent.putExtra("RequestCode", Code.RQ_TOPOPUP_UPDATE);
                            updateIntent.putExtra("IndexOfFigure", figureList.indexOf(v));
                            updateIntent.putExtra("Title", ((FigureView) v).getTitle());
                            updateIntent.putExtra("Url", ((FigureView) v).getUrl());
                            updateIntent.putExtra("Color", ((FigureView) v).getColorId());
                            updateIntent.putExtra("Shape", ((FigureView) v).getLayoutResId());
                            startActivityForResult(updateIntent, Code.RQ_TOPOPUP_UPDATE);
                        }
                        break;
                    }
                }
            }
            return true;
        }
        public boolean isRanged(float x , float y, float parentW){
            if((x > (parentW*0.5 - trash_view.getWidth()*0.5)) && (x < (parentW*0.5 + trash_view.getWidth()*0.5))
                    &&(y > trash_view.getY() && (y < trash_view.getY() + trash_view.getHeight()))) return true;
            else return false;
        }
    }
    public void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(this.getActivity());  // ColorPicker 객체 생성


        colorPicker.setColors(ColorSaver.colorList)  // 만들어둔 list 적용
                .setColumns(5)  // 5열로 설정
                .setRoundColorButton(true)  // 원형 버튼으로 설정
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        r_view.setBackgroundColor(color);  // OK 버튼 클릭 시 이벤트
                        choosedColor = color;

                    }

                    @Override
                    public void onCancel() {
                        // Cancel 버튼 클릭 시 이벤트
                    }
                }).show();  // dialog 생성
    }
    private void addFigure(WidgetItem widgetItem){
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, R.id.canvas_parent);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.canvas_parent);
        p.setMargins(0,0,0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STICKER_MARGIN_BOTTOM,getResources().getDisplayMetrics()));
        FigureView figureView = new FigureView(context, widgetItem.getFigure_layout_id(), widgetItem.getColor(), widgetItem.getTitle(), widgetItem.getUrl(), widgetItem.getPosition());
        figureView.setOnTouchListener(new StickerOnTouch());
        figureView.setLayoutParams(p);
        r_view.addView(figureView);
        figureList.add(figureView);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Code.RS_TOWIDGET_ADD){
            if(data != null){
                WidgetItem widgetItem = (WidgetItem)data.getSerializableExtra("WidgetItem");
                try {
                    addFigure(widgetItem);
                }catch (IllegalStateException e){
                    Log.d("TTT","잘못된 도형의 모양이 설정되었습니다.");
                }
            }
        }else if(resultCode == Code.RS_TOWIDGET_UPDATE){
            if(data != null) {
                Log.d("TTT", "result a");
                int idx = data.getIntExtra("IdxOfFigureList", 0);
                WidgetItem widgetItem = (WidgetItem) data.getSerializableExtra("WidgetItem");
                FigureView figureView = figureList.get(idx);
                figureView.setTitle(widgetItem.getTitle());
                figureView.setUrl(widgetItem.getUrl());
                figureView.setColorId(widgetItem.getColor());
                figureView.setLayoutResId(widgetItem.getFigure_layout_id());
                figureView.postInvalidate();
            }
        }
    }
}
