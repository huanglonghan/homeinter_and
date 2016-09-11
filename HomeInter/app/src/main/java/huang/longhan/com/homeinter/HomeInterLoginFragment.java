package huang.longhan.com.homeinter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import huang.longhan.com.homeinter.component.TipFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link HomeInterLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeInterLoginFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    private static final String ARG_ACCOUNT = "account";
    private static final String ARG_PASSWD = "passwd";

    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_OTHER = 10037;
    public static final int RESULT_CODE_LOGIN_ERROR = 10038;
    public static final int RESULT_CODE_OPT = 10039;
    public static final int RESULT_CODE_TIMESTAMP = 10040;
    public static final int RESULT_CODE_NONE = 10041;
    public static final int RESULT_CODE_NON_UNIQUE = 10042;
    public static final int RESULT_CODE_ALREADY_REGISTRY = 10043;

    private String mAccount;
    private String mPasswd;
    private  final int LOGIN = 123;

    public HomeInterLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccount = getArguments().getString(ARG_ACCOUNT);
            mPasswd = getArguments().getString(ARG_PASSWD);
        }
    }


    public static HomeInterLoginFragment newInstance(String account, String passwd) {
        HomeInterLoginFragment fragment = new HomeInterLoginFragment();
        if (account != null && passwd != null) {
            Bundle args = new Bundle();
            args.putString(ARG_ACCOUNT, account);
            args.putString(ARG_PASSWD, passwd);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_homeinter_login, container, false);
        Button btLogin = (Button) view.findViewById(R.id.btLogin);

        final EditText etAccount = (EditText) view.findViewById(R.id.etAccount);
        final EditText etPasswd = (EditText) view.findViewById(R.id.etPassword);
        Button btRegist = (Button) view.findViewById(R.id.btRegist);

        btLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = etAccount.getText().toString();
                String passwd = etPasswd.getText().toString();
                if (!account.equals("") && !passwd.equals("")) {
                    TipFragment tipFragment =TipFragment.newInstance(account,passwd);
                    tipFragment.setTargetFragment(HomeInterLoginFragment.this,LOGIN);
                    tipFragment.show(getFragmentManager().beginTransaction(),"TipFragment");
                }

            }
        });
        btRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRegistButtonBehavior();
                }
            }
        });

        return view;

    }

    public interface OnFragmentInteractionListener {
        void onLoginButtonBehavior(String tCode);

        void onRegistButtonBehavior();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mStopServiceListeners = (OnFragmentInteractionListener) context;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==this.LOGIN){
            switch (resultCode){

            }
        }
    }
}
