package s.android.ffmpeg.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import s.android.ffmpeg.R;
import s.android.ffmpeg.gui.MainActivity;
import s.android.ffmpeg.util.MediaUtils;

public class OnlineRoomFragment extends Fragment {
    private ImageButton startBtn;
    private EditText inviteCodeET;
    private String inviteCode;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_online_class, container, false);
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    public void onStart()
    {
        startBtn = (ImageButton) this.getActivity().findViewById(R.id.startBtn);
        inviteCodeET = (EditText) this.getActivity().findViewById(R.id.inviteCode);

        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                inviteCode = inviteCodeET.getText().toString();
                Intent intent = new Intent();
                intent.setClass(OnlineRoomFragment.this.getActivity().getApplicationContext(), MainActivity.class);
                String clientId = inviteCodeET.getText().toString();
                String roomId = inviteCodeET.getText().toString();
                String realm = MediaUtils.USERT_REAML[0];
                intent.putExtra("clientId", clientId);
                intent.putExtra("roomId", roomId);
                intent.putExtra("realm", realm);
                startActivity(intent);
            }
        });
        super.onStart();
    }


}
