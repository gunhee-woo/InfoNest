package com.dlog.info_nest.ui.palette;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dlog.info_nest.R;
import com.dlog.info_nest.db.entity.WidgetItem;
import com.dlog.info_nest.ui.palette.adapters.ColorRcylAdapter;
import com.dlog.info_nest.ui.palette.rycldecos.ColorRyclDeco;
import com.dlog.info_nest.ui.palette.views.HideEffectLeftView;
import com.dlog.info_nest.ui.palette.views.HideEffectRightView;
import com.dlog.info_nest.utilities.Code;
import com.dlog.info_nest.utilities.ColorSaver;

import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.OnScrollChangeListener;
import static android.view.View.VISIBLE;


public class PopupActivity extends AppCompatActivity {

    //color recycler view
    RecyclerView mColorRyclView;
    HideEffectRightView hideEffectRightView;
    HideEffectLeftView hideEffectLeftView;

    ImageView circleView;
    ImageView rectangleView;
    ImageView circleCheck;
    ImageView rectangleCheck;

    Button btn_ok;
    Button btn_no;

    EditText edt_title;
    EditText edt_url;

    int idxOfFigureList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pop_up_widget_add);
        mColorRyclView = findViewById(R.id.rycl_color);
        circleView = findViewById(R.id.img_dialog_circle);
        rectangleView = findViewById(R.id.img_dialog_rectangle);
        circleCheck = findViewById(R.id.img_circle_check);
        rectangleCheck = findViewById(R.id.img_rectangle_check);
        btn_no = findViewById(R.id.btn_widget_add_x);
        btn_ok = findViewById(R.id.btn_widget_add_o);
        edt_title = findViewById(R.id.edt_dialog_title);
        edt_url = findViewById(R.id.edt_dialog_url);

        Intent intent = getIntent();
        final int requestCode = intent.getIntExtra("RequestCode" , Code.RQ_TOPOPUP_ADD);


        //color recycler view
        mColorRyclView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        final ColorRcylAdapter colorRcylAdapter = new ColorRcylAdapter(ColorSaver.colorList, new ImageView[]{circleView, rectangleView});
        mColorRyclView.setAdapter(colorRcylAdapter);
        mColorRyclView.addItemDecoration(new ColorRyclDeco());
        //hide eff
        hideEffectLeftView = findViewById(R.id.hide_eff_left);
        hideEffectRightView = findViewById(R.id.hide_eff_right);
        mColorRyclView.setOnScrollChangeListener(new OnScrollChangeListener(){
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!v.canScrollHorizontally(-1)) {
                    hideEffectLeftView.setVisibility(INVISIBLE);

                } else if (!v.canScrollHorizontally(1)) {
                    hideEffectRightView.setVisibility(INVISIBLE);
                } else {
                    hideEffectRightView.setVisibility(VISIBLE);
                    hideEffectLeftView.setVisibility(VISIBLE);
                }
            }
        });

        //도형 선택
        circleView.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                circleCheck.setVisibility(VISIBLE);
                rectangleCheck.setVisibility(INVISIBLE);
            }
        });
        rectangleView.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                circleCheck.setVisibility(INVISIBLE);
                rectangleCheck.setVisibility(VISIBLE);
            }
        });

        btn_no.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_ok.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                //모양, 색상, 제목, url이 다 설정되었는지 체크
                String color = colorRcylAdapter.getSelectedColor(); //
                int figure_layout_id = 0;
                if(circleCheck.getVisibility() == VISIBLE) figure_layout_id = R.layout.circle;
                else if(rectangleCheck.getVisibility() == VISIBLE) figure_layout_id = R.layout.rectangle;
                String title = edt_title.getText().toString();
                String url = edt_url.getText().toString();

                if(isAllSetting(title, url)){
                    //설정되었으면 widgetitem 만들어서 setresult
                    WidgetItem widgetItem = new WidgetItem(figure_layout_id, color, title, url, -1, 0f, 0f);
                    Intent result = new Intent();
                    result.putExtra("WidgetItem", widgetItem);
                    if(requestCode == Code.RQ_TOPOPUP_ADD) {
                        setResult(Code.RS_TOWIDGET_ADD, result);
                    }else if (requestCode == Code.RQ_TOPOPUP_UPDATE){
                        result.putExtra("IdxOfFigureList", idxOfFigureList);
                        setResult(Code.RS_TOWIDGET_UPDATE, result);
                    }
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "제목과 url은 필수 입력사항입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(requestCode == Code.RQ_TOPOPUP_UPDATE) {
            String colorID = intent.getStringExtra("Color");
            String title = intent.getStringExtra("Title");
            String url = intent.getStringExtra("Url");
            int shape = intent.getIntExtra("Shape", R.layout.circle);
            idxOfFigureList = intent.getIntExtra("IndexOfFigure", 0);

            colorRcylAdapter.setColor(colorID);
            switch (shape){
                case R.layout.circle : {
                    circleCheck.setVisibility(VISIBLE);
                    rectangleCheck.setVisibility(INVISIBLE);
                    break;
                }
                case R.layout.rectangle : {
                    circleCheck.setVisibility(INVISIBLE);
                    rectangleCheck.setVisibility(VISIBLE);
                    break;
                }
            }
            edt_title.setText(title);
            edt_url.setText(url);
        }

    }
    private boolean isAllSetting(String title, String url){
        if(title != null && title.length() != 0 && url != null && url.length() != 0 ){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 화면 터치 이벤트 바깥레이어 클릭시 팝업이 닫히지 않게 함
     * @param event event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}
