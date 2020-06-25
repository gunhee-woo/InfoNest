package com.dlog.info_nest.ui.palette;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dlog.info_nest.R;
import com.dlog.info_nest.db.entity.WidgetItem2;
import com.dlog.info_nest.utilities.Code;

public class PopupActivity2 extends AppCompatActivity {


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
        setContentView(R.layout.pop_up_widget_add2);

        btn_no = findViewById(R.id.btn_widget_add_x);
        btn_ok = findViewById(R.id.btn_widget_add_o);
        edt_title = findViewById(R.id.edt_dialog_title);
        edt_url = findViewById(R.id.edt_dialog_url);

        Intent intent = getIntent();
        final int requestCode = intent.getIntExtra("RequestCode" , Code.RQ_TOPOPUP_ADD);



        btn_no.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //모양, 색상, 제목, url이 다 설정되었는지 체크
                String title = edt_title.getText().toString();
                String url = edt_url.getText().toString();

                if(isAllSetting(title, url)){
                    //설정되었으면 widgetitem 만들어서 setresult
                    WidgetItem2 widgetItem2 = new WidgetItem2(title, url, 0f, 0f);
                    Intent result = new Intent();
                    result.putExtra("WidgetItem", widgetItem2);
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
            String title = intent.getStringExtra("Title");
            String url = intent.getStringExtra("Url");
            idxOfFigureList = intent.getIntExtra("IndexOfFigure", 0);

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

