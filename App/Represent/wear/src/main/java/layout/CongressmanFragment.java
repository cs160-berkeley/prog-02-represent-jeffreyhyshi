package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.toolbox.ImageLoader;
import com.example.jeffster.represent.R;
import com.example.jeffster.represent.VolleySingleton;
import com.example.jeffster.represent.WatchToPhoneService;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import io.fabric.sdk.android.Fabric;
import retrofit.http.GET;
import retrofit.http.Query;


public class CongressmanFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONGRESSMAN_NAME = "name";
    private static final String CONGRESSMAN_AFFILIATION = "affiliation";
    private static final String CONGRESSMAN_OBJECT = "json_object";

    private String TWITTER_KEY;
    private String TWITTER_SECRET;

    private String name;
    private String affiliation;
    private JSONObject congressman;
    private Context mContext;
    private TextView nameView;

    private OnFragmentInteractionListener mListener;

    public CongressmanFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param c A JSONObject representing a congressman
     * @return A new instance of fragment CongressmanFragment.
     */
    public static CongressmanFragment newInstance(JSONObject c) {
        CongressmanFragment fragment = new CongressmanFragment();
        Bundle args = new Bundle();

        args.putString(CONGRESSMAN_OBJECT, c.toString());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                congressman = new JSONObject(getArguments().getString(CONGRESSMAN_OBJECT));
            } catch (JSONException e) {
                congressman = new JSONObject();
            }
            name = congressman.optString("first_name") + " " +
                    congressman.optString("last_name");

            affiliation = congressman.optString("party");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_congressman, container, false);

        nameView = (TextView) view.findViewById(R.id.name);
        TextView affiliationView = (TextView) view.findViewById(R.id.affiliation);

        nameView.setText(name);
        int color = affiliation.equalsIgnoreCase("R") ?
                R.drawable.circle_red :
                (affiliation.equalsIgnoreCase("D") ? R.drawable.circle_blue : R.drawable.circle_grey);
        affiliationView.setBackground(ResourcesCompat.getDrawable(getResources(), color, null));
        affiliationView.setText(affiliation.substring(0, 1));

        final Context c = getActivity();
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

    class MyTwitterApiClient extends TwitterApiClient {
        public MyTwitterApiClient(AppSession session) {
            super(session);
        }

        public UsersService getUsersService() {
            return getService(UsersService.class);
        }
    }

    interface UsersService {
        @GET("/1.1/users/show.json")
        void show(@Query("screen_name") String screenName,
                  Callback<User> cb);
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
