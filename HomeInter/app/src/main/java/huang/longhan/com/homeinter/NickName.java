package huang.longhan.com.homeinter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * TODO: document your custom view class.
 */
public class NickName extends LinearLayout implements View.OnClickListener{

    EditText nickName;
    Button nickNameConfirm;
    Button nickNameCancel;

    public NickName(Context context) {
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.nick_name, this, true);
        nickName = (EditText) view.findViewById(R.id.nick_name);
        nickNameConfirm = (Button) view.findViewById(R.id.nick_name_confirm);
        nickNameCancel = (Button) view.findViewById(R.id.nick_name_cancel);

        nickName.setOnClickListener(this);
        nickNameConfirm.setOnClickListener(this);
        nickNameCancel.setOnClickListener(this);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nick_name:
                nickName.setFocusable(true);
                nickName.setFocusableInTouchMode(true);
                nickNameConfirm.setVisibility(VISIBLE);
                nickNameCancel.setVisibility(VISIBLE);
                break;
            case R.id.nick_name_confirm:
                nickName.setFocusable(false);
                nickName.setFocusableInTouchMode(false);
                nickNameConfirm.setVisibility(GONE);
                nickNameCancel.setVisibility(GONE);
                break;
            case R.id.nick_name_cancel:
                nickName.setFocusable(false);
                nickName.setFocusableInTouchMode(false);
                nickNameConfirm.setVisibility(GONE);
                nickNameCancel.setVisibility(GONE);
                break;
        }
    }
}
