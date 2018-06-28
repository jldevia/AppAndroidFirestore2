package unpsjb.pm.fi.ars;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPhraseActivity extends AppCompatActivity {

    public static final String PHRASE_KEY = "phrase";
    public static final String AUTHOR_KEY = "author";

    //Acciones
    public static final int NEW = 1;
    public static final int EDIT = 2;

    public static final String MSG_SUCCESS = "La frase fue guardada correctamente.";
    public static final String MSG_FAILURE = "Fallo al guardar la frase.";
    private static final String TAG = "App:RegisterPhrase";

    private int action = 0;
    private String id;

    private EditText txtPhrase;
    private EditText txtAuthor;

    CollectionReference collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phrase);

        action = getIntent().getIntExtra("action", 1);

        txtPhrase = findViewById(R.id.editPhrase);
        txtAuthor = findViewById(R.id.editAuthor);

        collection = FirebaseFirestore.getInstance().collection("inspirationalPhrases");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if ( action == EDIT ){
            id = getIntent().getStringExtra("id");
            String phrase = getIntent().getStringExtra("phrase");
            String author = getIntent().getStringExtra("author");

            txtPhrase.setText(phrase);
            txtAuthor.setText(author);
        }

    }


    public void save(View view) {
        String phrase = txtPhrase.getText().toString();
        String author = txtAuthor.getText().toString();

        switch (action) {
            case NEW:{
                String poster = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Float rating = (float )0.0;

                if (phrase.isEmpty() || author.isEmpty()){return;}

                Map<String, Object> dataToSave = new HashMap<String, Object>();
                dataToSave.put(PHRASE_KEY, phrase);
                dataToSave.put(AUTHOR_KEY, author);
                dataToSave.put("poster", poster);
                dataToSave.put("rating", rating);

                collection.add(dataToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, MSG_SUCCESS);
                        Toast toast = Toast.makeText(RegisterPhraseActivity.this, MSG_SUCCESS, Toast.LENGTH_SHORT);
                        toast.show();

                        txtPhrase.setText("");
                        txtAuthor.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.d(TAG, MSG_FAILURE);
                        Toast toast = Toast.makeText(RegisterPhraseActivity.this, MSG_FAILURE, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                break;
            }
            case EDIT:{

                Map<String, Object> dataToUpdate = new HashMap<String, Object>();
                dataToUpdate.put(PHRASE_KEY, phrase);
                dataToUpdate.put(AUTHOR_KEY, author);

                collection.document(id).update(dataToUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, MSG_SUCCESS);
                        Toast toast = Toast.makeText(RegisterPhraseActivity.this, MSG_SUCCESS, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.d(TAG, MSG_FAILURE);
                        Toast toast = Toast.makeText(RegisterPhraseActivity.this, MSG_FAILURE, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

            }
        }
    }

    public void cancel(View view) {
        txtAuthor.setText("");
        txtPhrase.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }

    }
}
