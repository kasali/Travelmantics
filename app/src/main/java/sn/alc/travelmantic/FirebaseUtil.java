package sn.alc.travelmantic;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FirebaseUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static FirebaseUtil firebaseUtil;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static ArrayList<TravelDeal> mdeals;
    private static final int RC_SIGN_IN = 123;
    public static Activity caller;
    public static boolean isAdmin;

    public static void openFbreference(String ref, final Activity calledActivity)
    {
        if(firebaseUtil==null)
        {
            firebaseUtil=new FirebaseUtil();
            firebaseDatabase=FirebaseDatabase.getInstance();
            firebaseAuth=FirebaseAuth.getInstance();
            caller=calledActivity;
            authStateListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser()==null)
                    {
                        FirebaseUtil.signin();
                    }
                    else
                        {
                            String useruid=firebaseAuth.getUid();
                            checkAdmin(useruid);
                        }

                    Toast.makeText(calledActivity.getBaseContext(), "Welcome back", Toast.LENGTH_SHORT).show();

                }
            };
            connectStorage();

        }
        mdeals=new ArrayList<>();
        databaseReference=firebaseDatabase.getReference().child(ref);

    }

    private static void checkAdmin(String useruid) {
        FirebaseUtil.isAdmin=false;
        DatabaseReference reference=firebaseDatabase.getReference().child("administrator")
                .child(useruid);
        ChildEventListener childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin=true;
                Log.d("admin", "you are the administrator");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addChildEventListener(childEventListener);
    }

    private static void signin() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());



        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);



    }

    public static void attachListener()
    {
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    public static void detachListener()
    {
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    public static void connectStorage()
    {
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("deals_pictures");
    }

}
