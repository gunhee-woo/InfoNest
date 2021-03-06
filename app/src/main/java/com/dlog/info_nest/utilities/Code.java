package com.dlog.info_nest.utilities;

public class Code {
    //widget activity에서 도형 추가를 위해 popupactivity를 호출할때 보내는 request code
    public static int RQ_TOPOPUP_ADD = 100;
    //popup에서 정상적으로 도형이 추가되어서, 다시 widget activity로 돌아갈 때 보내는 result code
    public static int RS_TOWIDGET_ADD = 1;
    /**
     * widget activity에서 도형 수정을 위해 popup activity를 호출할때 보내는 rquest code
     */
    public static int RQ_TOPOPUP_UPDATE = 101;
    /**
     * popup에서 정상적으로 도형이 수정되어서, 다시 widget activity로 돌아갈 때 보내는 result code
     */
    public static int RS_TOWIDGET_UPDATE = 2;

    //취소 result code  do nothing
    public static int CANCEL_RESULT = 0;

    /**
     * 북마크 리스트에서 하나를 선택해서 popupactivity를 호출할때 보내는 requset code
     */
    public static int RQ_TOPOPUP_LIST_ADD = 102;

    /**
     * 파일 쓰기 rq 코드
     */
    public static int WRITE_REQUEST_CODE = 88;

    /**
     * 위젯 > 웹뷰 rq 코드
     */
    public static int RQ_TOWEBVIEW_WIDGET = 500;
}
