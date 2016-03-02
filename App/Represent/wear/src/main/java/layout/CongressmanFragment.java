package layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;

import com.example.jeffster.represent.R;
import com.example.jeffster.represent.WatchToPhoneService;

import org.w3c.dom.Text;


public class CongressmanFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONGRESSMAN_NAME = "name";
    private static final String CONGRESSMAN_AFFILIATION = "affiliation";

    private String name;
    private String affiliation;
    private Context mContext;

    private OnFragmentInteractionListener mListener;

    public CongressmanFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Candidate name.
     * @param party Candidate affiliation.
     * @return A new instance of fragment CongressmanFragment.
     */
    public static CongressmanFragment newInstance(String name, String party) {
        CongressmanFragment fragment = new CongressmanFragment();
        Bundle args = new Bundle();
        args.putString(CONGRESSMAN_NAME, name);
        args.putString(CONGRESSMAN_AFFILIATION, party);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(CONGRESSMAN_NAME);
            affiliation = getArguments().getString(CONGRESSMAN_AFFILIATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_congressman, container, false);

        TextView nameView = (TextView) view.findViewById(R.id.name);
        LinearLayout circle = (LinearLayout) view.findViewById(R.id.affiliation_circle);
        TextView affiliationView = (TextView) view.findViewById(R.id.affiliation);

        nameView.setText(name);
        circle.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle, null));
        affiliationView.setText(affiliation.substring(0, 1));
        return view;
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
