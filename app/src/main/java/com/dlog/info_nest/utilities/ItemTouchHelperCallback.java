package com.dlog.info_nest.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dlog.info_nest.R;

enum ButtonState {
    GONE,
    RIGHT_VISIBLE,
    LEFT_VISIBLE
}

// 리사이클러뷰 오른쪽 왼쪽 스와이프 기능 콜백 함수
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperListener listener;
    private boolean swipeBack = false;
    private ButtonState buttonShowedState = ButtonState.GONE;
    private RecyclerView.ViewHolder currentViewHolder = null;
    private static final float buttonWidth = 300;
    private RectF buttonInstance = null;
    private Context context;


    public ItemTouchHelperCallback(ItemTouchHelperListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag_flags = ItemTouchHelper.UP| ItemTouchHelper.DOWN;
        int swipe_flags = ItemTouchHelper.START| ItemTouchHelper.END;
        return makeMovementFlags(0,swipe_flags);
        //return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //  listener.onItemSwipe(viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if(swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if(buttonShowedState != ButtonState.GONE) {
                if(buttonShowedState == ButtonState.RIGHT_VISIBLE)
                    dX = Math.min(dX, -buttonWidth);
                if(buttonShowedState == ButtonState.LEFT_VISIBLE)
                    dX = Math.max(dX, buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if(buttonShowedState == ButtonState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        currentViewHolder = viewHolder;
        drawButtons(c, currentViewHolder);
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder){
        float buttonWidthWithOutPadding = buttonWidth - 10;
        float corners = 5;
        View itemView = viewHolder.itemView;
        Paint p = new Paint();
        buttonInstance = null;

        //오른쪽으로 스와이프 했을때 (왼쪽에 버튼이 보여지게 될 경우)
        if(buttonShowedState == ButtonState.LEFT_VISIBLE){
            RectF leftButton = new RectF(itemView.getLeft() + 10, itemView.getTop() + 10, itemView.getLeft() + buttonWidthWithOutPadding, itemView.getBottom() - 10);
            p.setColor(Color.BLUE);
            c.drawRoundRect(leftButton, corners, corners, p);
            drawText("수정", c, leftButton, p);
            buttonInstance = leftButton;

        } else if(buttonShowedState == ButtonState.RIGHT_VISIBLE) { //왼쪽으로 스와이프 했을때 (오른쪽에 버튼이 보여지게 될 경우)z
            RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithOutPadding, itemView.getTop() + 10, itemView.getRight() -10, itemView.getBottom() - 10);
            p.setColor(Color.RED);
            c.drawRoundRect(rightButton, corners, corners, p);
            drawText("삭제", c, rightButton, p);
            buttonInstance = rightButton;
        }
    }

    //버튼의 텍스트 그려주기
    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 40;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);
        float textWidth = p.measureText(text);

        /*Bitmap bitmap = getBitmapByVectorAsset();
        if(bitmap == null) {
            Log.d("test", "null");
        }
        int cx = (int)(button.width() - bitmap.getWidth()) / 2;
        int cy = (int)(button.height() - bitmap.getHeight()) / 2;
        c.drawBitmap(bitmap, cx,cy, p);
         */
        c.drawText(text, button.centerX() - (textWidth/2), button.centerY() + (textSize/2), p);
    }

    public Bitmap getBitmapByVectorAsset() {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_star_border_black_24dp);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if(swipeBack) {
                    if(dX < -buttonWidth)
                        buttonShowedState = ButtonState.RIGHT_VISIBLE;
                    else if(dX > buttonWidth)
                        buttonShowedState = ButtonState.LEFT_VISIBLE;

                    if(buttonShowedState != ButtonState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ItemTouchHelperCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                setItemsClickable(recyclerView, true);
                swipeBack = false;

                if (listener != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
                    if (buttonShowedState == ButtonState.LEFT_VISIBLE) {
                        listener.onLeftClick(viewHolder.getAdapterPosition(), viewHolder);
                    }
                    if (buttonShowedState == ButtonState.RIGHT_VISIBLE) {
                        listener.onRightClick(viewHolder.getAdapterPosition(), viewHolder);
                    }
                }
                buttonShowedState = ButtonState.GONE;
                currentViewHolder = null;
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }
}
