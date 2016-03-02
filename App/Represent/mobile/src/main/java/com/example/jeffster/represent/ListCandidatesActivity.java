package com.example.jeffster.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListCandidatesActivity extends AppCompatActivity {

    private final String[] NAMES = {"Hillary Clinton", "Bloopy Blurpingon", "Dom Perignon",
                                    "Barbara Boxer", "Saul", "Phillippe"};
    private final String[] AFFILIATIONS = {"D - New York", "I - New York", "R - Ithaca",
                                            "D - California", "D - California", "R - Solano"};
    private final String[] TWEETS = {"my #brraaannnd", "burpo morgus has two tongues, you know",
            "I love america too much", "samuel johnson has hives", "the lord will love us",
            "give me all the politics, yo"};
    private final String[] EMAILS = {"hillary@thaclintons.com", "bloop@blurp.com", "derp@derp.io",
            "barbara@barabxoe.org", "saul@yo.com", "web@email.org"};
    private final String[] WEBSITES = {"hill.thaclintons.com", "blurp.geocities.com",
            "facebook.com/dom484", "barbaraboxer.org", "websites.com", "coolpolitican.org"};
    private final int[] DRAWABLE_IDS = {R.drawable.hilary, R.drawable.perot, R.drawable.schumer,
                                        R.drawable.boxer, R.drawable.saul, R.drawable.philippe};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_candidates);

        Bundle extras = getIntent().getExtras();
        int zip = extras.getInt("location");

        LinearLayout senators = (LinearLayout) findViewById(R.id.senators);
        LinearLayout representatives = (LinearLayout) findViewById(R.id.representatives);

        LayoutInflater inflater = getLayoutInflater();

        //will be not an if statement when actual APIs happen
        if (zip == 11111) {
            for (int i = 0; i < 2; i++) {
                addCongressmanCard(senators, NAMES[i], AFFILIATIONS[i], TWEETS[i], EMAILS[i],
                        WEBSITES[i], DRAWABLE_IDS[i]);
            }
            addCongressmanCard(representatives, NAMES[2], AFFILIATIONS[2], TWEETS[2], EMAILS[2],
                    WEBSITES[2], DRAWABLE_IDS[2]);
        } else {
            for (int i = 3; i < 5; i++) {
                addCongressmanCard(senators, NAMES[i], AFFILIATIONS[i], TWEETS[i], EMAILS[i],
                        WEBSITES[i], DRAWABLE_IDS[i]);
            }
            addCongressmanCard(representatives, NAMES[5], AFFILIATIONS[5], TWEETS[5], EMAILS[5],
                    WEBSITES[5], DRAWABLE_IDS[5]);
        }
    }

    private void startDetailActivity(String congressmanName) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("congressman_name", congressmanName);
        startActivity(intent);
    }

    private void addCongressmanCard(LinearLayout layoutToUse, final String name, String affiliation,
                                    String tweet, String email, String website, int drawableId) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.congressman_card, layoutToUse, false);

        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView affiliationView = (TextView) view.findViewById(R.id.affiliation);
        TextView tweetView = (TextView) view.findViewById(R.id.tweet);
        TextView emailView = (TextView) view.findViewById(R.id.email);
        TextView websiteView = (TextView) view.findViewById(R.id.website);
        ImageView pic = (ImageView) view.findViewById(R.id.pic);
        Button infoButton = (Button) view.findViewById(R.id.button);

        nameView.setText(name);
        affiliationView.setText(affiliation);
        tweetView.setText(tweet);
        emailView.setText(email);
        websiteView.setText(website);
        pic.setImageResource(drawableId);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailActivity(name);
            }
        });
        layoutToUse.addView(view);
    }

}
