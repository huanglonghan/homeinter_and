package com.longhan.huang.homeinter.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.longhan.huang.homeinter.R;
import com.longhan.huang.homeinter.component.ExpandableListAdapter;
import com.longhan.huang.homeinter.config.Config;
import com.longhan.huang.homeinter.service.LocationService;
import com.longhan.huang.homeinter.ui.acticity.LoginActivity;
import com.longhan.huang.homeinter.utls.ErrorCode;
import com.longhan.huang.homeinter.utls.connect.HTTPConnect;
import com.longhan.huang.homeinter.utls.connect.HTTPInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SidebarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SidebarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UID = "uid";
    private static final String ARG_NICKNAME = "nickname";
    @BindView(R.id.login_btn)
    Button mLoginBtn;


    // TODO: Rename and change types of parameters
    private String mUid;

    public void setNickName(String uid, String nickname) {
        this.mNickName = nickname;
        this.mUid = uid;
        if (nickName != null) {
            nickName.setText(nickname);
        }
    }

    public static final int UPDATE_NICKNAME = 0x123f;

    private String mNickName;


    EditText nickName;
    Button nickNameConfirm;
    Button nickNameCancel;
    ProgressBar wait;


    public SidebarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid      Parameter 1.
     * @param nickname Parameter 2.
     * @return A new instance of fragment SidebarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SidebarFragment newInstance(String uid, String nickname) {
        SidebarFragment fragment = new SidebarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        args.putString(ARG_NICKNAME, nickname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUid = getArguments().getString(ARG_UID);
            mNickName = getArguments().getString(ARG_NICKNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sideview, container, false);
        nickName = (EditText) view.findViewById(R.id.nick_name);
        nickNameConfirm = (Button) view.findViewById(R.id.nick_name_confirm);
        nickNameCancel = (Button) view.findViewById(R.id.nick_name_cancel);
        wait = (ProgressBar) view.findViewById(R.id.wait);
        ExpandableListView mOnlineUserList = (ExpandableListView) view.findViewById(R.id.user_list);
        mOnlineUserList.setAdapter(new ExpandableListAdapter(getContext()));

        nickName.setOnClickListener(v -> {
            nickName.setFocusable(true);
            nickName.setFocusableInTouchMode(true);
        });

        nickName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mNickName = nickName.getText().toString();
                    nickName.setText(mNickName);
                    nickNameConfirm.setVisibility(View.VISIBLE);
                    nickNameCancel.setVisibility(View.VISIBLE);
                }

            }
        });

        nickName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case 66:
                        nickNameConfirm.performClick();
                        return true;
                    case 62:
                        return true;
                }

                return false;
            }
        });
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern pattern = Pattern.compile("[0-9a-zA-Z\u4e00-\u9fa5]+");
                Matcher matcher = pattern.matcher(source);
                if (matcher.matches()) {
                    return source;
                }
                return "";
            }
        };
        nickName.setFilters(new InputFilter[]{inputFilter});
        nickNameConfirm.setOnClickListener(v -> {
            String nickname = nickName.getText().toString();
            if (nickname.length() != 0 && !nickname.equals(mNickName)) {
                AsyncTask<String, Void, Boolean> asyncTask = new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... params) {
                        boolean isSuccess = setUserNickName(params[0], params[1]);
                        if (isSuccess) {
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LocationService.PREFERENCE_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(LocationService.PRE_NICKNAME, params[1]);
                            editor.apply();
                        }
                        return isSuccess;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        wait.setIndeterminate(true);
                        wait.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        wait.setVisibility(View.GONE);
                        String result;
                        if (aBoolean) {
                            result = "更改成功";
                            mNickName = nickName.getText().toString();
                            Fragment parentfragment = SidebarFragment.this.getParentFragment();
                            if (parentfragment != null) {
                                parentfragment.onActivityResult(UPDATE_NICKNAME, 0, new Intent().putExtra(LocationService.PRE_NICKNAME, mNickName));
                            }
                        } else {
                            result = "更改失败";
                        }
                        Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                        super.onPostExecute(aBoolean);
                    }
                };
                asyncTask.execute(mUid, nickName.getText().toString());
            }
            nickName.setFocusable(false);
            nickName.setFocusableInTouchMode(false);
            nickNameConfirm.setVisibility(View.GONE);
            nickNameCancel.setVisibility(View.GONE);
        });
        nickNameCancel.setOnClickListener(v -> {
            nickName.setFocusable(false);
            nickName.setFocusableInTouchMode(false);
            nickName.setText(mNickName);
            nickNameConfirm.setVisibility(View.GONE);
            nickNameCancel.setVisibility(View.GONE);
        });
        ButterKnife.bind(this, view);
        return view;
    }

    private boolean setUserNickName(String uid, String nickname) {
        HTTPConnect HTTPConnect = com.longhan.huang.homeinter.utls.connect.HTTPConnect.createConnect(Config.httpServiceUrl);
        if (HTTPConnect == null) {
            return false;
        }
        String msg = HTTPConnect.sendMsg(HTTPInterface.setUserNickNameMsg(uid, nickname));
        if (msg == null) {
            return false;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            return false;
        }
        int errorCode = jsonObject.optInt("errorCode", -1);
        if (errorCode == ErrorCode.SUCCESS) {
            return true;
        }
        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    final int LOGIN_REQUEST_CODE = 0x01;

    @OnClick(R.id.login_btn)
    public void onClick() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
