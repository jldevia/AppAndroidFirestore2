package unpsjb.pm.fi.ars;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "App: MainActivity";
    private RecyclerView listView;
    private RecyclerView.LayoutManager layoutManager;
    private FirestoreRecyclerAdapter<Phrase, ViewHolderPhrase> adapter;
    private int positionSelected;

    CollectionReference collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (RecyclerView) findViewById(R.id.listFrases);

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(layoutManager);

        collection = FirebaseFirestore.getInstance().collection("inspirationalPhrases");

        getPhrasesList();
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();

        positionSelected = -1;
    }

    private void getPhrasesList(){
        Query qry = collection.orderBy("rating", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Phrase> options = new FirestoreRecyclerOptions.Builder<Phrase>()
                .setQuery(qry, Phrase.class).build();

        adapter = new FirestoreRecyclerAdapter<Phrase, ViewHolderPhrase>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolderPhrase holder, final int position, @NonNull final Phrase model) {
                holder.tvPhrase.setText('"' + model.getPhrase() + '"');
                holder.tvAuthor.setText(model.getAuthor());
                holder.rtngBar.setRating(model.getRating());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positionSelected = position;
                        notifyDataSetChanged();
                    }
                });

                if (positionSelected == position) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#FF33B5E5"));
                }else {
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                }
            }

            @NonNull
            @Override
            public ViewHolderPhrase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list, parent, false);

                return new ViewHolderPhrase(view);
            }

            @Override
            public void onError(FirebaseFirestoreException exc){
                Log.e(TAG, exc.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

    }

    public void newPhrase(View view){
        Intent intent = new Intent(MainActivity.this, RegisterPhraseActivity.class);

        intent.putExtra("action", RegisterPhraseActivity.NEW);

        startActivity(intent);
    }

    private void editPhrase(){
        if (positionSelected == -1){
            Toast.makeText(this, "No hay ninguna frase seleccionada", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String mailUserCurrent = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentSnapshot docSelected = adapter.getSnapshots().getSnapshot(positionSelected);

        if ( docSelected.getString("poster").equals(mailUserCurrent) ) {
            Intent intent = new Intent(this, RegisterPhraseActivity.class);

            intent.putExtra("action", RegisterPhraseActivity.EDIT);
            intent.putExtra("id", docSelected.getId());
            intent.putExtra("phrase", docSelected.getString("phrase"));
            intent.putExtra("author", docSelected.getString("author"));

            startActivity(intent);
        }else {
            Toast.makeText(this, "La frase no fue posteada por usted!!!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onStop () {
        super.onStop();

        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int option = item.getItemId();

        switch (option){
            case R.id.itMnExit:{
                salir();
                break;
            }
            case R.id.itmMnAbout:{
                break;
            }
            case R.id.itmMnDelete:{
                DeleteDialog dialog = new DeleteDialog();
                dialog.show(getFragmentManager(), "DeleteDialog");
                break;
            }
            case R.id.itmMnRating:{
                RatingDialog dialog = new RatingDialog();
                dialog.show(getFragmentManager(), "RatingDialog");
                break;
            }
            case R.id.itmMnEdit:{
                editPhrase();
                break;
            }
            default:{

            }
        }

        return super.onOptionsItemSelected(item);

    }

    private void salir(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LoginActivity.RC_SIGN_OUT);
        finish();
    }

    public void deletePhraseSelected(){
        if ( positionSelected != -1 ){
            DocumentSnapshot docSelected = adapter.getSnapshots().getSnapshot(positionSelected);
            Phrase frase = adapter.getItem(positionSelected);
            String mailUserCurrent = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            if ( frase.getPoster().equals(mailUserCurrent) ) {

                collection.document(docSelected.getId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Frase eliminada!!!", Toast.LENGTH_SHORT)
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error inesperado: No se pudo completar la tarea.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }else {
                Toast.makeText(this, "La frase no fue posteada por usted!!!", Toast.LENGTH_SHORT)
                        .show();
            }

        }else {
            Toast.makeText(this, "No hay ninguna frase seleccionada", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void ratingPhrase(final float rating){
        if ( positionSelected != -1 ) {
            final DocumentReference refPhrase = adapter.getSnapshots().getSnapshot(positionSelected).getReference();
            final String mailUserCurrent = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            final RatingPhrase newRating = new RatingPhrase( new Date(), mailUserCurrent, rating);

            refPhrase.collection("ratings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if ( task.isSuccessful() ) {
                        DocumentReference aux = null;
                        final int cantRatings;
                        float ratingAux = 0;

                        List<DocumentSnapshot> resultQry = task.getResult().getDocuments();

                        if (resultQry.isEmpty()) {
                            aux = refPhrase.collection("ratings").document();
                            cantRatings = 1;
                        }else{
                            boolean flag = false;
                            for (DocumentSnapshot doc: resultQry){
                                if (doc.getString("user").equals(mailUserCurrent)){
                                    aux = doc.getReference();
                                    flag = true;
                                    ratingAux = doc.getDouble("value").floatValue();
                                    break;
                                }
                            }
                            if (flag == false){
                                aux = refPhrase.collection("ratings").document();
                                cantRatings = resultQry.size() + 1;
                            }else{
                                cantRatings = resultQry.size();
                            }

                        }

                        final DocumentReference refRating = aux;
                        final float ratingBeforeUser = ratingAux;

                        collection.getFirestore().runTransaction(new Transaction.Function<Void>() {

                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                Phrase phrase = transaction.get(refPhrase).toObject(Phrase.class);

                                //Se calcula el nuevo promedio de rating
                                float oldRatingTotal = phrase.getRating() - ratingBeforeUser;
                                float newAvgRating = (oldRatingTotal + newRating.getValue()) / cantRatings;

                                //Se guarda la nueva info en la frase
                                phrase.setRating(newAvgRating);

                                //Se persisten los cambios en la base de datos
                                transaction.set(refPhrase, phrase);
                                transaction.set(refRating, newRating);

                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Voto Ok!!!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error inesperado: No se pudo completar la tarea.", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });


                    }else{
                        Toast.makeText(MainActivity.this, "Error inesperado: No se pudo completar la tarea.", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });


        }else {
            Toast.makeText(this, "No hay ninguna frase seleccionada", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
