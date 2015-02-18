package gengyixiong.me.pinballgamedemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private int tableWidth;
    private int tableHeight;
    private int racketY;

    private static final int RACKET_HEIGHT = 20;
    private static final int RACKET_WIDTH = 140;
    private static final int BALL_SIZE = 14;

    private int ySpeed = 10;
    private double xyRate = new Random().nextDouble()-0.5;
    private int xSpeed = (int) (ySpeed * xyRate * 2);

    private int ballX = new Random().nextInt(200) + 20;
    private int ballY = new Random().nextInt(10) + 20;

    private int racketX = new Random().nextInt(200);
    private boolean isLose = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      //No title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final GameView gameView = new GameView(this);
        setContentView(gameView);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        tableWidth = displayMetrics.widthPixels;
        tableHeight = displayMetrics.heightPixels;
        racketY = tableHeight - 80;
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123){
                    gameView.invalidate();
                }
            }
        };
        gameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (event.getKeyCode()){
                    case KeyEvent.KEYCODE_A:
                        if (racketX > 0){
                            racketX -= 10;
                        }
                    break;
                    case KeyEvent.KEYCODE_D:
                        if (racketX < tableWidth - RACKET_WIDTH){
                            racketX += 10;
                        }
                    break;
                }
                gameView.invalidate();
                return true;
            }
        });

        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (ballX <= 0 || ballX >= tableWidth - BALL_SIZE){
                    xSpeed = -xSpeed;
                }
                if (ballY >= racketY - BALL_SIZE && (ballX < racketX || ballX > racketX + RACKET_WIDTH)){
                    timer.cancel();
                    isLose = true;
                }
                else if(ballY <= 0 || (ballY > racketY - BALL_SIZE && ballX > racketX && ballX <= racketX + RACKET_WIDTH)){
                    ySpeed = -ySpeed;
                }
                ballX += xSpeed;
                ballY += ySpeed;
                handler.sendEmptyMessage(0x123);
            }
        };
        timer.schedule(timerTask, 0, 25);
    }
    class GameView extends View {
        Paint paint = new Paint();
        public GameView(Context context) {
            super(context);
            setFocusable(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);

            if(isLose){
                paint.setColor(Color.RED);
                paint.setTextSize(40);
                canvas.drawText("Game Over", 50, 200, paint);
            }else{
                paint.setColor(Color.BLACK);
                canvas.drawRect(racketX, racketY, racketX+RACKET_WIDTH, racketY+RACKET_HEIGHT, paint);
                canvas.drawCircle(ballX, ballY, BALL_SIZE, paint);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
