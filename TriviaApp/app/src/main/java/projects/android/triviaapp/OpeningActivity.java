package projects.android.triviaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Class: OpeningActivity
 * Opening class to display a welcome message
 */
public class OpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
    }

    public void startTrivia(View view){
        startActivity(new Intent(this, MainActivity.class));
    }
}
