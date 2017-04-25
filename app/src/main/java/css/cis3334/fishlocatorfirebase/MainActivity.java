package css.cis3334.fishlocatorfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button buttonAdd, buttonDetails, buttonDelete;          // two button widgets
    ListView listViewFish;                                  // listview to display all the fish in the database
    ArrayAdapter<Fish> fishAdapter;
    List<Fish> fishList;
    int positionSelected;
    FishFirebaseData fishDataSource;
    DatabaseReference fishDbRef;
    Fish fishSelected;


    //FirebaseAuth and AuthStateListener declarations.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize mAuth.
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() { //initialized mAuthListener
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //track the user when they sign in or out using the firebaseAuth
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    Log.d("CSS3334","onAuthStateChanged - User NOT is signed in");
                    Intent signInIntent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(signInIntent);
                }
            }
        };
        setupFirebaseDataChange();
        setupListView();
        setupAddButton();
        setupDetailButton();
        setupDeleteButton();
    }

    /**
     * Attach FirebaseAuth instance at start
     * Makes call to onStart() method of super class inside overridden method.
     * AuthStateListener mAuthListener is added to mAuth to allow for the listener to observe
     * changes in authentication state.
     */
    @Override
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Remove FirebaseAuth at stop.
     * onStop() method of super class is called in overriden method.
     * When stopped if the AuthListener is not null it is then removed.
     */
    @Override
    public void onStop()
    {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }








    private void setupFirebaseDataChange() {
        // ToDo - Add code here to update the listview with data from Firebase
        fishDataSource = new FishFirebaseData();

        //Retrieves DatabaseRef from open method of fishDataSource
        fishDbRef = fishDataSource.open();
        fishDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fishList = fishDataSource.getAllFish(dataSnapshot);

                //pass list to FishAdapter
                fishAdapter = new FishAdapter(MainActivity.this, android.R.layout.simple_list_item_single_choice,
                        fishList);

                //set adapter
                listViewFish.setAdapter(fishAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupListView() {
        listViewFish = (ListView) findViewById(R.id.ListViewFish);
        listViewFish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View parent,
                                    int position, long id) {
                positionSelected = position;
                Log.d("MAIN", "Fish selected at position " + positionSelected);
            }
        });
    }

    private void setupAddButton() {
        // Set up the button to add a new fish using a seperate activity
        buttonAdd = (Button) findViewById(R.id.buttonAddFish);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Start up the add fish activity with an intent
                Intent detailActIntent = new Intent(view.getContext(), AddFishActivity.class);
                finish();
                startActivity(detailActIntent);
            }
        });
    }

    private void setupDetailButton() {
        // Set up the button to display details on one fish using a seperate activity
        buttonDetails = (Button) findViewById(R.id.buttonDetails);
        buttonDetails.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("MAIN", "onClick for Details");
                Intent detailActIntent = new Intent(view.getContext(), DetailActivity.class);
                detailActIntent.putExtra("Fish", fishList.get(positionSelected));
                finish();
                startActivity(detailActIntent);
            }
        });
    }

    private void setupDeleteButton() {
        // Set up the button to display details on one fish using a seperate activity
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("MAIN", "onClick for Delete");
                Log.d("MAIN", "Delete at position " + positionSelected);
                fishDataSource.deleteFish(fishList.get(positionSelected));
                fishAdapter.remove( fishList.get(positionSelected) );
                fishAdapter.notifyDataSetChanged();
            }
        });
    }
}
