package s.android.ffmpeg.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import s.android.ffmpeg.R;
import s.android.ffmpeg.model.TMemberModel;
import s.android.ffmpeg.util.ConstantUtils;
import s.android.ffmpeg.util.FastJsonUtil;
import s.android.ffmpeg.util.MD5Util;
import s.android.ffmpeg.util.MediaUtils;

public class LoginActivity extends Activity {
    public static final String CLASS_TAG = "Login_Activity";

    private EditText loginName;
    private EditText loginPassword;
    private Button loginBtn;

    private String name;
    private String password;

    private OnClickListener logBtnOCL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
    }

    private void initComponent() {
        loginName = (EditText) this.findViewById(R.id.loginName);
        loginPassword = (EditText) this.findViewById(R.id.loginPassword);
        loginBtn = (Button) this.findViewById(R.id.loginBtn);
        loginName.setText("student");
        loginPassword.setText("123");
        logBtnOCL = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                name = loginName.getText().toString().trim();
                password = loginPassword.getText().toString().trim();
                loginReqeust(name, MD5Util.string2MD5(password));
            }
        };
        loginBtn.setOnClickListener(logBtnOCL);
    }

    private void loginReqeust(final String loginName, final String loginPassword) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    login(loginName, loginPassword);
                } catch (Exception e) {
                    getApplicationContext().getMainLooper().prepare();
                    Toast.makeText(getApplicationContext(), "请求发生错误！", Toast.LENGTH_SHORT).show();
                    Log.e(CLASS_TAG, e.getMessage(), e);
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    /**
     * @方法名：requestLlogingin
     * @方法描述： 向服务器发送数据请求登陆
     * @参数:@param name
     * @参数:@param password
     * @返回类型：void
     * @修改人:邓风森
     * @修改时间:2014-9-24 下午8:26:47
     */
    private void login(String loginName, String loginPassword) throws Exception {
        int responseStatus;
        HttpClient httpClient = new DefaultHttpClient();
        // 2.创建HttpGet对象，发起GET请求
        String url = ConstantUtils.getURL(ConstantUtils.SERVER_MEDIA_IP, ConstantUtils.SERVER_WEB_PORT, ConstantUtils.URL_LOGIN);
        StringBuffer buffer = new StringBuffer();
        buffer.append(url).append("?").append("loginName=").append(loginName)
                .append("&loginPassword=").append(loginPassword);
        HttpGet httpGet = new HttpGet(buffer.toString());
        // 3.调用execute()方法；返回值是HttpResponse对象
        HttpResponse httpResponse = httpClient.execute(httpGet);
        // 判断请求是否成功
        responseStatus = httpResponse.getStatusLine().getStatusCode();
        if (responseStatus == HttpStatus.SC_OK) {
            Log.i(CLASS_TAG, "请求服务器端成功");
            // 获得输入流
            BufferedReader inStrem = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String result;
            StringBuffer bf = new StringBuffer();
            while ((result = inStrem.readLine()) != null) {
                bf.append(result);
            }
            inStrem.close();
            Log.d(CLASS_TAG, "返回结果:\r\n \t" + bf.toString());
            MediaUtils.LOGIN_USER = JSON.parseObject(bf.toString(), TMemberModel.class);
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ContentActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
            // 关闭输入流
        } else {
            Toast.makeText(getApplicationContext(), "登陆请求失败：" + responseStatus, Toast.LENGTH_SHORT).show();
            Log.i(CLASS_TAG, "请求服务器端失败");
            System.out.println("登陆请求失败：" + responseStatus);
        }
    }
}
