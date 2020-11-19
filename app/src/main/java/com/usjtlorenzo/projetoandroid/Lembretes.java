package com.usjtlorenzo.projetoandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Lembretes extends AppCompatActivity {

    private RecyclerView mensagensRecyclerView;
    private ChatAdapter adapter;
    private List<Mensagem> mensagens;
    private EditText mensagemEditText;
    private FirebaseUser fireUser;
    private CollectionReference mMsgsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.adc_lembrete);
        setContentView(R.layout.lista_lembretes);
        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        adapter = new ChatAdapter(mensagens, this);
        mensagensRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        mensagensRecyclerView.setLayoutManager(linearLayoutManager);
        mensagemEditText = findViewById(R.id.mensagemEditText);

    }
    private void setupFirebase() {
        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        mMsgsReference = FirebaseFirestore.getInstance().collection("mensagens");
        getRemoteMsgs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirebase();
    }

    private void getRemoteMsgs() {
        mMsgsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                mensagens.clear();
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    Mensagem incomingMsg = doc.toObject(Mensagem.class);
                    mensagens.add(incomingMsg);
                }
                Collections.sort(mensagens);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void enviarMensagem(View view){
        String mensagem = mensagemEditText.getText().toString();
        Mensagem m = new Mensagem(fireUser.getEmail(), new Date(), mensagem);
        esconderTeclado(view);
        mMsgsReference.add(m);
    }

    private void esconderTeclado(View view){
        InputMethodManager ims = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        ims.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}