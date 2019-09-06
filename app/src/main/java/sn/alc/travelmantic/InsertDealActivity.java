package sn.alc.travelmantic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertDealActivity extends AppCompatActivity {
    EditText title,description,price;
    String titleDeal,descriptionDeal,priceDeal;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title=findViewById(R.id.edit_title);
        price=findViewById(R.id.edit_price);
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
        else
        {
            databaseReference.child(deal.getId()).removeValue();
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

