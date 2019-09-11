package sn.alc.travelmantic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class InsertDealActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 42;
    EditText title,description,price;
    ImageView imageView;
    String titleDeal,descriptionDeal,priceDeal;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    TravelDeal deal;
    Button btn_image;
    private StorageReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title=findViewById(R.id.edit_title);
        price=findViewById(R.id.edit_price);
        imageView=findViewById(R.id.image);
        btn_image =findViewById(R.id.btnImage);
        description=findViewById(R.id.edit_description);
        FirebaseUtil.openFbreference("traveldeal",this);
        firebaseDatabase=FirebaseUtil.firebaseDatabase;
        databaseReference=FirebaseUtil.databaseReference;
        Intent intent=getIntent();
        deal= (TravelDeal) intent.getSerializableExtra("Deal");
        if(deal==null)
        {
            deal=new TravelDeal();
        }
        this.deal=deal;
        title.setText(deal.getTitle());
        price.setText(deal.getPrice());
        description.setText(deal.getDescription());
        showImage(deal.getImageUrl());
        btn_image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/jpec");
                startActivityForResult(Intent.createChooser(intent1,"Insert an image"),REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE&&resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            ref=FirebaseUtil.storageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    String pictureName = taskSnapshot.getStorage().getPath();
                    deal.setImageUrl(url);
                    deal.setImageName(pictureName);
                    Log.d("Url: ", url);
                    Log.d("Name", pictureName);
                    showImage(url);
                }
            });
        }
    }

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.save).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.save).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.save :
                saveDeals();
                Toast.makeText(this,"deal saved",Toast.LENGTH_LONG).show();
                clean();
                backtolist();
                return  true;
            case R.id.delete:
                deleteDeals();
                Toast.makeText(this,"deal deleted",Toast.LENGTH_LONG).show();
                backtolist();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void clean() {
        title.setText("");
        description.setText("");
        price.setText("");

    }

    private void saveDeals() {
     deal.setTitle(title.getText().toString());
     deal.setPrice(price.getText().toString());
     deal.setDescription(description.getText().toString());
     if (deal==null)
     {
         databaseReference.push().setValue(deal);
     }
     else
         {
             databaseReference.child(deal.getId()).setValue(deal);
         }
    }
    private void deleteDeals()
    {
        if (deal==null)
        {
            Toast.makeText(this,"Add a deal before deleting!",Toast.LENGTH_LONG).show();
        }

        databaseReference.child(deal.getId()).removeValue();
        Log.d("image name", deal.getImageName());
        if(deal.getImageName() != null && !deal.getImageName().isEmpty()) {
            StorageReference picRef = FirebaseUtil.firebaseStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image Successfully Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
        }

    }
    private  void backtolist(){
        Intent intent=new Intent(this,ListDealsActivity.class);
        startActivity(intent);
        }
    private void enableEditTexts(boolean isEnabled) {
        title.setEnabled(isEnabled);
        description.setEnabled(isEnabled);
        price.setEnabled(isEnabled);
    }
    }

