package org.auchan129.soultalker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Menu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.GridLayout;

import android.graphics.Color;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
/* import org.apache.http.impl.client.HttpClients;*/
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.acl.Group;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends Activity
{
    private EditText edit;
    private TextView pendingText;
    private ScrollView mainLayout;
    private LinearLayout mainContent;
    private String result = "";
    private String pendingString = "";
    private Map<String, Integer> bgColorfields;
    private Map<String, Integer> bgPictruefields;
    private boolean firstStart = true;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bgColorfields = new HashMap<String, Integer>();
        bgPictruefields = new HashMap<String, Integer>();
        init();

        edit = (EditText) findViewById(R.id.txtEdit);
        //pendingText = (TextView) findViewById(R.id.txtInfo);
        Button btnSend = (Button) findViewById(R.id.btnSend);
        mainLayout = (ScrollView) findViewById(R.id.mainContainer);
        mainContent = (LinearLayout) findViewById(R.id.mainContent);
        btnSend.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {

                    String INFO = URLEncoder.encode(edit.getText().toString(), "utf-8");
                    String requesturl = "http://www.tuling123.com/openapi/api?key=a9ac1bf95aad7712a8fd29df40e00d9b&info="+INFO;
                    //text.setText(requesturl);
                    final HttpGet httpget = new HttpGet(requesturl);
                    final HttpClient httpClient = new DefaultHttpClient();
                    //Toast.makeText(getApplicationContext(),"线程开始",Toast.LENGTH_SHORT).show();

                    new Thread()
                    {
                        @Override
                        public void run()
                        {

                            try
                            {
                                mHandler.sendEmptyMessage(0);
                                //mHandler.sendEmptyMessage(1);
                                HttpResponse response = httpClient.execute(httpget);
                                //mHandler.sendEmptyMessage(2);
                                if(response.getStatusLine().getStatusCode() ==200 && response != null)
                                {
                                    //mHandler.sendEmptyMessage(3);
                                    HttpEntity entity = response.getEntity();
                                    InputStream is = entity.getContent();
                                    InputStreamReader isr = new InputStreamReader(is);
                                    BufferedReader reader = new BufferedReader(isr);
                                    //String result = EntityUtils.toString(response.getEntity());
                                    result = getJson(reader.readLine());
                                    mHandler.sendEmptyMessage(4);
                                }
                                //super.run();
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                                mHandler.sendEmptyMessage(5);
                            }
                        }
                    }.start();

                    //200即正确的返回码
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(6);
                }
            }
        });
    }
    private void init() {
        String[] colorArray = getResources().getStringArray(R.array.pref_background_colors);
        String[] pictrueArray = getResources().getStringArray(R.array.pref_background_pictures);

        bgColorfields.put(colorArray[0], R.drawable.textview_background_white);
        bgColorfields.put(colorArray[1], R.drawable.textview_background_yellow);
        bgColorfields.put(colorArray[2], R.drawable.textview_background_blue);
        bgColorfields.put(colorArray[3], R.drawable.textview_background_gray);
        bgColorfields.put(colorArray[4], R.drawable.textview_background_green);
        //Toast.makeText(this, String.valueOf(colorArray.length), Toast.LENGTH_SHORT).show();

        bgPictruefields.put(pictrueArray[0], R.drawable.background_1);
        bgPictruefields.put(pictrueArray[1], R.drawable.background_2);
        bgPictruefields.put(pictrueArray[2], R.drawable.background_3);
        bgPictruefields.put(pictrueArray[3], R.drawable.background_4);
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        if (firstStart) {
            showNormalMessage("欢迎回来", 0);
            firstStart = false;
        }
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = "默认背景4";
        String strTitle = getString(R.string.pref_title_main_background);
        name = mySharedPreferences.getString(strTitle, "默认背景2");
        mainLayout.setBackgroundResource(bgPictruefields.get(name));

        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ActionBarDrawerToggle will take care of this.
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            System.exit(0);
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 0:
                    pendingString = edit.getText().toString();
                    pendingText = showNormalMessage(getResources().getString(R.string.pending)
                            + pendingString, 1);
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(),"发起请求",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),"判断数据",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(),"获取数据",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    //Toast.makeText(getApplicationContext(),"返回结果："+result,Toast.LENGTH_SHORT).show();
                    pendingText.setText(pendingString);
                    showNormalMessage(result, 0);
                    edit.setText("");
                    break;
                case 5:
                    Toast.makeText(getApplicationContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                    pendingText.setText(getResources().getString(R.string.sendFailure) + pendingString);
                    break;
                case 6:
                    Toast.makeText(getApplicationContext(),"IO异常",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private String getJson(String json)
    {
        String jsonStr = "{'name':" + json + "}";
        String code;
        String jstr = "";
        try
        {
            JSONObject jsobj = new JSONObject(jsonStr);
            JSONObject jo = jsobj.getJSONObject("name");

            jstr = jo.getString("text");
            code = jo.getString("code");
            switch (Integer.valueOf(code)) {
                case 100000:
                    ///text
                    break;
                case 200000:
                    ///interlinkage
                    jstr += "\n" + jo.getString("url");
                    break;
                case 301000:
                    break;
                case 302000:
                    break;
                case 304000:
                    break;
                default:
                    break;
            }
        }
        catch(JSONException e)
        {

        }
        return jstr;
    }

    private TextView showNormalMessage(String info, int dir) {

        // use inflater
        LayoutInflater inflater = getLayoutInflater();
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = "灰色";
        if (dir == 0) {
            // turn robot
            inflater.inflate(R.layout.robot_message_box, mainContent);
            String strTitle = getString(R.string.pref_title_robot_background);
            name = mySharedPreferences.getString(strTitle, "蓝色");
        }
        else {
            // turn user
            inflater.inflate(R.layout.user_message_box, mainContent);
            String strTitle = getString(R.string.pref_title_user_background);
            name = mySharedPreferences.getString(strTitle, "黄色");
        }
        int childNum =  mainContent.getChildCount();
        GridLayout gridLayout = (GridLayout) mainContent.getChildAt(childNum - 1);
        TextView textView = (TextView) gridLayout.getChildAt(0);
        textView.setBackgroundResource(bgColorfields.get(name));
        textView.setText(info);
        // 调整高度
        mHandler.post(mScrollToBottom);
        return textView;
    }
    private Runnable mScrollToBottom = new Runnable()
    {
        @Override
        public void run()
        {
            // update MeasuredHeight
            mainLayout.measure(0, 0);
            int off = mainLayout.getMeasuredHeight() - mainLayout.getHeight();
            if (off > 0)
            {
                mainLayout.scrollBy(0, off);
            }
        }
    };
}
