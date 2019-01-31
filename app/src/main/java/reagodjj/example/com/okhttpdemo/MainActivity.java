package reagodjj.example.com.okhttpdemo;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "RealgodJJ";
    private TextView tvGetInformation;
    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvGetInformation = findViewById(R.id.tv_get_information);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_get:
                get();
                break;

            case R.id.m_response:
                response();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //采用线程池的异步处理
    private void response() {
        Request.Builder builder = new Request.Builder();
        builder.url("https://raw.githubusercontent.com/square/okhttp/master/README.md");
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        //此时采用异步处理
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i(TAG, "Fail to get information!");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    int code = response.code();
                    Headers headers = response.headers();
                    String content = response.body() != null ? response.body().string() : null;
                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("code: ").append(String.valueOf(code));
                    stringBuilder.append("\nheaders: ").append(String.valueOf(headers));
                    stringBuilder.append("\ncontent: ").append(content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvGetInformation.setText(stringBuilder.toString());
                        }
                    });
                }
            }
        });
    }

    //采用线程池的同步处理
    private void get() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Request.Builder builder = new Request.Builder();
                builder.url("https://raw.githubusercontent.com/square/okhttp/master/README.md");
                Request request = builder.build();
                Log.d(TAG, "run: " + request);
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        final String bodyString = response.body() != null ? response.body().string() : null;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvGetInformation.setText(bodyString);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        executor.shutdown();
    }
}
