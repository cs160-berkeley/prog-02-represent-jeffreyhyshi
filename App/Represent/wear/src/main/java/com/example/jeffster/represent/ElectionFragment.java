package com.example.jeffster.represent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ElectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ElectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ElectionFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATE = "param1";
    private static final String COUNTY = "param2";
    private static final String RED_PERCENT = "param3";

    private String state;
    private String county;
    private int redPercent;

    private OnFragmentInteractionListener mListener;

    public ElectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param state state the person reps.
     * @param county county the person reps.
     * @param redPercent the percent republican vote.
     * @return A new instance of fragment ElectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ElectionFragment newInstance(String state, String county, int redPercent) {
        ElectionFragment fragment = new ElectionFragment();
        Bundle args = new Bundle();
        args.putString(STATE, state);
        args.putString(COUNTY, county);
        args.putInt(RED_PERCENT, redPercent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            state = getArguments().getString(STATE);
            county = getArguments().getString(COUNTY);
            redPercent = getArguments().getInt(RED_PERCENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_election, container, false);
        TextView countyView = (TextView) view.findViewById(R.id.county);
        TextView stateView = (TextView) view.findViewById(R.id.state);
        TextView redPercentView = (TextView) view.findViewById(R.id.red_percent);
        TextView bluePercentView = (TextView) view.findViewById(R.id.blue_percent);

        countyView.setText(county);
        stateView.setText(state);
        redPercentView.setText(Integer.toString(redPercent) + "%");
        bluePercentView.setText(Integer.toString(100 - redPercent) + "%");

        //Get the display width
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;

        View redView = view.findViewById(R.id.red);
        View blueView = view.findViewById(R.id.blue);

        ViewGroup.LayoutParams redParams = redView.getLayoutParams();
        ViewGroup.LayoutParams blueParams = blueView.getLayoutParams();
        redParams.width = Math.round( (((float) redPercent) / 100) * width );
        blueParams.width = Math.round( (((float) 100 - redPercent) / 100) * width );
        redView.setLayoutParams(redParams);
        blueView.setLayoutParams(blueParams);

        return view;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

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
