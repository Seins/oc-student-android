package s.android.ffmpeg.gui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import s.android.ffmpeg.R;
import s.android.ffmpeg.util.ConstantUtils;
import s.android.ffmpeg.gui.LoginActivity;
import s.android.ffmpeg.gui.util.UIUtils;
import s.android.ffmpeg.handler.LoadDataHandler;
import s.android.ffmpeg.handler.ResponseHandler;
import s.android.ffmpeg.http.RequestParams;
import s.android.ffmpeg.model.TMemberModel;
import s.android.ffmpeg.util.MediaUtils;
import s.android.ffmpeg.util.FastJsonUtil;
import s.android.ffmpeg.util.RequestUtils;

/**
 * 作者： CodingMates
 * 创建时间： 2015/2/5 15:37.
 */
public class UserInfoFragment extends Fragment {
    public static final String CLASS_TAG = UserInfoFragment.class.getSimpleName();

    private ImageView icon;
    private EditText name;
    private TextView englishName;
    private TextView birthday;
    private EditText school;
    private EditText hobby;
    private EditText specialSkill;
    private EditText accountNo;
    private EditText qq;
    private EditText address;
    private EditText email;
    private EditText phone;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        initComponent(view);
        return view;
    }

    public void initComponent(View view)
    {
        icon = (ImageView) view.findViewById(R.id.info_icon);
        name = (EditText) view.findViewById(R.id.info_name);
        englishName = (TextView) view.findViewById(R.id.info_english_name);
        birthday = (TextView) view.findViewById(R.id.info_birthday);
        school = (EditText) view.findViewById(R.id.info_school);
        hobby = (EditText) view.findViewById(R.id.info_hobby);
        specialSkill = (EditText) view.findViewById(R.id.info_special_skill);
        accountNo = (EditText) view.findViewById(R.id.info_account_no);
        qq = (EditText) view.findViewById(R.id.info_qq);
        address = (EditText) view.findViewById(R.id.info_address);
        email = (EditText) view.findViewById(R.id.info_email);
        phone = (EditText) view.findViewById(R.id.info_phone);
    }

    public void onResume()
    {
        requestData();
        super.onResume();
    }

    public void requestData()
    {
        RequestParams rp = new RequestParams();
        rp.put("memberId", MediaUtils.LOGIN_USER.getMemberId() + "");
        //md5加密
        RequestUtils.post(ConstantUtils.getURL(ConstantUtils.SERVER_WEB_IP, ConstantUtils.SERVER_WEB_PORT, ConstantUtils.URL_GET_INFO),
                rp, new ResponseHandler(getActivity().getApplicationContext(),
                        new LoadDataHandler() {
                            @Override
                            public void onSuccess(String data) {
                                if (data == null || "".equals(data) || "null".equals(data)) {
                                    Toast.makeText(getActivity().getApplicationContext(), "数据请求失败：用户信息已过期，请重新登陆！", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity().getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    final TMemberModel m = FastJsonUtil.getObject(data, TMemberModel.class);
                                    if (m.getPhoto() != null && m.getPhoto().length > 0) {
                                        icon.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(m.getPhoto(), 0, m.getPhoto().length);
                                                bitmap = UIUtils.createCircleImage(bitmap, icon.getWidth());
                                                icon.setImageBitmap(bitmap);
                                            }
                                        });

                                    }
                                    englishName.setText(m.getEnglishName());
                                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    birthday.setText(sdf.format(m.getBirthdate()));
                                    name.setText(m.getName());
                                    school.setText(m.getSchool());
                                    hobby.setText(m.getHobby());
                                    specialSkill.setText(m.getSepecilSkill());
                                    accountNo.setText(m.getAccountNo());
                                    address.setText(m.getAddress());
                                    qq.setText(m.getQqNumber());
                                    email.setText(m.getEmailAddress());
                                    phone.setText(m.getPhone());
                                }
                            }

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onLoadCaches(String data) {
                            }

                            @Override
                            public void onFinish() {
                            }

                            @Override
                            public void onFailure(String error, String message) {
                                if (null == UserInfoFragment.this || !UserInfoFragment.this.isAdded()) {
                                    return;
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "数据请求失败：" + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
        );
    }
}
