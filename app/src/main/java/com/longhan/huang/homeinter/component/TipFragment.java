package com.longhan.huang.homeinter.component;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

;


/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TipFragment} interface
 * to handle interaction events.
 * Use the {@link TipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TipFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MOBILE = "mobile";
    private static final String ARG_PASSWD = "passwd";
    public static final String RESULT_JSON = "result_json";

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAILED = 0;


    // TODO: Rename and change types of parameters
    private String mMobile;
    private String mPasswd;
//    private OnFragmentTipListener mStopServiceListeners;

    public TipFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mobile Parameter 1.
     * @param passwd Parameter 2.
     * @return A new instance of fragment TipFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TipFragment newInstance(String mobile, String passwd) {
        TipFragment fragment = new TipFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOBILE, mobile);
        args.putString(ARG_PASSWD, passwd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mMobile = getArguments().getString(ARG_MOBILE);
            mPasswd = getArguments().getString(ARG_PASSWD);
        }
        Log.e("tipfragment","tipfragment");
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return null;
            }

            @Override
            protected void onPostExecute(String str) {
                getTargetFragment()
                        .onActivityResult(getTargetRequestCode(),
                                str != null ? RESULT_SUCCESS : RESULT_FAILED,
                                new Intent().putExtra(RESULT_JSON, str));
            }
        }.execute(mMobile, mPasswd);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentTipListener) {
//            mStopServiceListeners = (OnFragmentTipListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mStopServiceListeners = null;
//    }

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
//    public interface OnFragmentTipListener {
//        // TODO: Update argument type and name
//        void onFragmentTip(String str);
//    }
}
