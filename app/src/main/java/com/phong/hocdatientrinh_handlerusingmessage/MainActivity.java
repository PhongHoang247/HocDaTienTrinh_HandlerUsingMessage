package com.phong.hocdatientrinh_handlerusingmessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText edtButton;
    Button btnDraw;
    TextView txtPercent;
    LinearLayout llButton;
    LinearLayout.LayoutParams layoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
    int n = 0;
    Random random = new Random();

    //Tiến trình CHÍNH xử lý ở đây: Main Thread
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int percent = msg.arg1;
            String value = msg.obj.toString();
            if (value.equals("END")){
                Toast.makeText(MainActivity.this,"Đã xong tiến trình",Toast.LENGTH_LONG).show();
            }
            else {
                //Show lên giao diện:
                final Button btn = new Button(MainActivity.this);
                btn.setText(value);
                btn.setLayoutParams(layoutParams);
                llButton.addView(btn);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn.setTextColor(Color.RED);
                    }
                });
                btn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        btn.setVisibility(View.INVISIBLE);
                        return false;
                    }
                });
            }
            txtPercent.setText(percent + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLyVeButton();
            }
        });
    }

    private void xuLyVeButton() {
        llButton.removeAllViews();

        n = Integer.parseInt(edtButton.getText().toString());
        //Tiến trình CON xử lý ở đây: Background Thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Trong này ko đc phép truy suất đến các biến control trên giao diện
                for (int i = 0; i < n; i++){
                    //Lấy message từ Main Background:
                    Message message = handler.obtainMessage();
                    //Từ message này sẽ thay đổi thông tin rồi gửi ngược lại Main Background:
                    int percent = i * 100/n;
                    int value = random.nextInt(100);
                    //Gán giá trị trên vào message sau đó gửi lại Main Background:
                    message.arg1 = percent;
                    message.obj = value;
                    //Gửi message cho Main Background:
                    handler.sendMessage(message);
                    SystemClock.sleep(100);
                }
                //Gửi tín hiệu kết thúc tiến trình:
                Message message = handler.obtainMessage();
                message.arg1 = 100;
                message.obj = "END";
                handler.sendMessage(message);
            }
        });
        thread.start();//kích hoạt Background Thread
    }

    private void addControls() {
        edtButton = findViewById(R.id.edtButton);
        btnDraw = findViewById(R.id.btnDraw);
        txtPercent = findViewById(R.id.txtPercent);
        llButton = findViewById(R.id.llButton);
    }
}
