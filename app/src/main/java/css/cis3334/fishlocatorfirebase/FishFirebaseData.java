package css.cis3334.fishlocatorfirebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cssuser on 4/20/2017.
 */

public class FishFirebaseData {

    public static final String FishDataTag = "Fish Data";
    DatabaseReference fishDbRef;

    public DatabaseReference open()  {

        // Get an instance of the database and a reference to the fish data in it
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        fishDbRef = database.getReference("FishDataTag");

        return fishDbRef;
    }

    public void close() {

    }

    public Fish createFish( String species, String weightInOz, String dateCaught) {           //Added String rating as a parameter

        // ---- Get a new database key for the vote
        String key = fishDbRef.child(FishDataTag).push().getKey();

        // ---- set up the fish object
        Fish newFish = new Fish(key, species, weightInOz, dateCaught);

        // ---- write the vote to Firebase
        fishDbRef.child(key).setValue(newFish);

        return newFish;
    }

    public Fish createFish( String species, String weightInOz, String dateCaught, String locationLatitude, String locationLongitude) {           //Added String rating as a parameter

        // ---- Get a new database key for the vote
        String key = fishDbRef.child(FishDataTag).push().getKey();

        // ---- set up the fish object
        Fish newFish = new Fish(key, species, weightInOz, dateCaught, locationLatitude,locationLongitude);

        // ---- write the vote to Firebase
        fishDbRef.child(key).setValue(newFish);

        return newFish;
    }

    public void deleteFish(Fish fish) {
        //Retrieve key from Fish param
        String key = fish.getKey();
        fishDbRef.child(key).removeValue();
    }

    public List<Fish> getAllFish(DataSnapshot dataSnapshot) {

            List <Fish> fishList = new ArrayList<Fish>();
            for(DataSnapshot data : dataSnapshot.getChildren())
            {
                Fish fish = data.getValue(Fish.class);
                fishList.add(fish);
            }

            return fishList;
    }

}
