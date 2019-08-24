package sn.alc.travelmantic;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class FirebaseUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private static FirebaseUtil firebaseUtil;
    public static ArrayList<TravelDeal> mdeals;

    public static void openFbreference(String ref)
    {
        if(firebaseUtil==null)
        {
            firebaseUtil=new FirebaseUtil();
            firebaseDatabase=FirebaseDatabase.getInstance();
            mdeals=new ArrayList<>();
        }
        databaseReference=firebaseDatabase.getReference().child(ref);
    }

}
