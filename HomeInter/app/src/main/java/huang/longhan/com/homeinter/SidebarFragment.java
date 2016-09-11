package huang.longhan.com.homeinter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import huang.longhan.com.homeinter.component.HomeInterExpandableListAdapter;
import huang.longhan.com.homeinter.config.Config;
import huang.longhan.com.homeinter.service.BaseHTTPCommunication;
import huang.longhan.com.homeinter.service.BaseHTTPProtocol;
import huang.longhan.com.homeinter.service.HomeInterService;
import huang.longhan.com.homeinter.utls.ErrorCode;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SidebarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SidebarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SidebarFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UID = "uid";
    private static final String ARG_NICKNAME = "nickname";

    // TODO: Rename and change types of parameters
    private String mUid;

    public void setNickName(String uid, String nickname) {
        this.mNickName = nickname;
        this.mUid = uid;
        if (nickName != null) {
            nickName.setText(nickname);
        }
    }

    public static final int UPDATE_NICKNAME= 0x123f;

    private String mNickName;


    EditText nickName;
    Button nickNameConfirm;
    Button nickNameCancel;
    ProgressBar wait;

    private OnFragmentInteractionListener mListener;

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
        View view = inflater.inflate(R.layout.fragment_sidebar, container, false);
        nickName = (EditText) view.findViewById(R.id.nick_name);
        nickNameConfirm = (Button) view.findViewById(R.id.nick_name_confirm);
        nickNameCancel = (Button) view.findViewById(R.id.nick_name_cancel);
        wait = (ProgressBar) view.findViewById(R.id.wait);
        ExpandableListView mOnlineUserList = (ExpandableListView)view.findViewById(R.id.online_user_list);
        mOnlineUserList.setAdapter(new HomeInterExpandableListAdapter(getContext()));

        nickName.setOnClickListener(this);

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
                        nickNameConfirm.callOnClick();
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
        nickNameConfirm.setOnClickListener(this);
        nickNameCancel.setOnClickListener(this);
        return view;
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nick_name:
                nickName.setFocusable(true);
                nickName.setFocusableInTouchMode(true);
                break;
            case R.id.nick_name_confirm:
                String nickname = nickName.getText().toString();
                if (nickname.length() != 0 && !nickname.equals(mNickName)) {
                    AsyncTask<String, Void, Boolean> asyncTask = new AsyncTask<String, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(String... params) {
                            boolean isSuccess = setUserNickName(params[0], params[1]);
                            if (isSuccess) {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HomeInterService.PREFERENCE_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(HomeInterService.PRE_NICKNAME, params[1]);
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
                                if (parentfragment!=null){
                                    parentfragment.onActivityResult(UPDATE_NICKNAME, 0, new Intent().putExtra(HomeInterService.PRE_NICKNAME,mNickName));
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
                break;
            case R.id.nick_name_cancel:
                nickName.setFocusable(false);
                nickName.setFocusableInTouchMode(false);
                nickName.setText(mNickName);
                nickNameConfirm.setVisibility(View.GONE);
                nickNameCancel.setVisibility(View.GONE);
                break;
        }
    }

    private boolean setUserNickName(String uid, String nickname) {
        BaseHTTPCommunication baseHTTPCommunication = BaseHTTPCommunication.createConnect(Config.httpServiceUrl);
        if (baseHTTPCommunication == null) {
            return false;
        }
        String msg = baseHTTPCommunication.sendMsg(BaseHTTPProtocol.setUserNickNameMsg(uid, nickname));
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
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
