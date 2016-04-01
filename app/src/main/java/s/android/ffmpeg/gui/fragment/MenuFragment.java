package s.android.ffmpeg.gui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import s.android.ffmpeg.R;
import s.android.ffmpeg.util.MediaUtils;

public class MenuFragment extends Fragment {
    private ImageView userIcon;
    private TextView userName;
    private TextView accountNo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    private void initComponent()
    {
        userIcon = (ImageView) getActivity().findViewById(R.id.menu_user_icon);
//        if (CommonUtils.LOGIN_USER.getPhoto().length != 0) {
//            userIcon.post(new Runnable() {
//                @Override
//                public void run() {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(CommonUtils.LOGIN_USER.getPhoto(), 0, CommonUtils.LOGIN_USER.getPhoto().length);
//                    bitmap = UIUtils.createCircleImage(bitmap, userIcon.getHeight());
//                    userIcon.setImageBitmap(bitmap);
//                }
//            });
//        }

        userName = (TextView) getActivity().findViewById(R.id.menu_user_name);
        userName.setText(MediaUtils.LOGIN_USER.getName());
        accountNo = (TextView) getActivity().findViewById(R.id.menu_account_no);
        accountNo.setText(MediaUtils.LOGIN_USER.getAccountNo());
    }

    public void onResume()
    {
        initComponent();
        super.onResume();
    }
}
