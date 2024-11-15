package com.example.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private int progressRabbit = 0;
    private int progressTurtle = 0;
    private Button btn_start;
    private SeekBar sb_rabbit, sb_turtle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化元件
        btn_start = findViewById(R.id.btnStart);
        sb_rabbit = findViewById(R.id.sbRabbit);
        sb_turtle = findViewById(R.id.sbTurtle);

        // 設定按鈕點擊事件
        btn_start.setOnClickListener(v -> {
            btn_start.setEnabled(false); // 按鈕點擊後，將按鈕設為不可點擊，並初始化進度
            progressRabbit = 0;
            progressTurtle = 0;
            sb_rabbit.setProgress(0);
            sb_turtle.setProgress(0);

            // 執行兔子和烏龜的賽跑方法
            runRabbit();
            runTurtle();
        });
    }

    private final Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            // 判斷編號
            if (msg.what == 1) {
                sb_rabbit.setProgress(progressRabbit);
            } else if (msg.what == 2) {
                sb_turtle.setProgress(progressTurtle);
            }

            // 判斷勝負
            if (progressRabbit >= 100 && progressTurtle < 100) {
                Toast.makeText(MainActivity.this, "兔子勝", Toast.LENGTH_SHORT).show();
                btn_start.setEnabled(true);
            } else if (progressTurtle >= 100 && progressRabbit < 100) {
                Toast.makeText(MainActivity.this, "烏龜勝", Toast.LENGTH_SHORT).show();
                btn_start.setEnabled(true);
            }

            return false;
        }
    });

    private void runRabbit() {
        new Thread(() -> {
            // 兔子是否偷懶的機率陣列 (2/3的機率會偷懶)
            Boolean[] sleepProbability = {true, true, false};

            while (progressRabbit < 100 && progressTurtle < 100) {
                try {
                    Thread.sleep(100); // 延遲0.1秒
                    // 隨機: 0~2的整數，用來判斷兔子是否偷懶 (0和1休息，2移動)
                    if (sleepProbability[(int) (Math.random() * 3)]) {
                        Thread.sleep(300); // 兔子偷懶0.3秒
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressRabbit += 3; // 兔子前進3步

                Message msg = new Message(); // 建立Message動作
                msg.what = 1; //加入編號
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void runTurtle() {
        new Thread(() -> {
            while (progressTurtle < 100 && progressRabbit < 100) {
                try {
                    Thread.sleep(100); // 延遲0.1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressTurtle++; // 烏龜前進一步
                Message msg = new Message(); // 建立Message動作
                msg.what = 2; //加入編號
                handler.sendMessage(msg);
            }
        }).start();
    }
}