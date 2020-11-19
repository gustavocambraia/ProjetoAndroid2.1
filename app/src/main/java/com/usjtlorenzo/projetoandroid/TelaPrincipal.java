package com.usjtlorenzo.projetoandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.usjtlorenzo.projetoandroid.Adapter.MeuAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TelaPrincipal extends AppCompatActivity {

    private RecyclerView rv_lembretes;

    private DrawerLayout dl;
    private NavigationView navigationView;
    private String l1[], l2[], l3[];
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private FloatingActionButton fb_addLembrete;
    private EditText et_dataRealizacao;

    private RecyclerView mensagensRecyclerView;
    private ChatAdapter adapter;
    private List<Mensagem> mensagens;
    private TextView mensagemEditText;
    private FirebaseUser fireUser;
    private CollectionReference mMsgsReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ALTERAR O SETCONTENT PARA .activity_tela_principal
        setContentView(R.layout.activity_tela_principal);

        getWindow().setBackgroundDrawableResource(R.drawable.telaprincipal);
        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        adapter = new ChatAdapter(mensagens, this);
        mensagensRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        mensagensRecyclerView.setLayoutManager(linearLayoutManager);
        mensagemEditText = findViewById(R.id.lembreteEditText);

        dl = findViewById(R.id.drawerLayout);

        // Metodo para abrir a navi view
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dl.openDrawer(GravityCompat.START);
            }
        });

        // Metodo de logout
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TelaPrincipal.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        // Trabalhando com o navi view
        navigationView = findViewById(R.id.nav_principal);
        navigationView.setItemIconTintList(null);
        Menu menu = navigationView.getMenu();
        menu.clear();


        // Colocar a função de adicionar categoria
        navigationView.getHeaderView(0).findViewById(R.id.btn_AdcCategoria).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder = new AlertDialog.Builder(TelaPrincipal.this);
                View popout = getLayoutInflater().inflate(R.layout.adc_categoria, null);
                Spinner spinnerCor = popout.findViewById(R.id.escolhaIcone);
                popout.findViewById(R.id.fecharAdcCategoria).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                popout.findViewById(R.id.btn_criarCategoria).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText et_nome = popout.findViewById(R.id.et_nomeCategoria);
                        String nomeCategoria = et_nome.getText().toString();
                        EditText et_icone = popout.findViewById(R.id.escolhaIcone);
                        String icone = et_icone.getText().toString();
                        if(icone.equalsIgnoreCase("bookmark")){
                            menu.add(nomeCategoria).setIcon(R.drawable.ic_document_delivery_64);
                        }
                    }
                });
                dialogBuilder.setView(popout);
                dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        // Adiocionar lembrete
        fb_addLembrete = findViewById(R.id.fb_addLembrete);
        fb_addLembrete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateSetListener;
                Calendar calendar = Calendar.getInstance();
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                int ano = calendar.get(Calendar.YEAR);
                dialogBuilder = new AlertDialog.Builder(TelaPrincipal.this);
                View popout = getLayoutInflater().inflate(R.layout.adc_lembrete, null);
                et_dataRealizacao = popout.findViewById(R.id.et_dataRealizacao);
                popout.findViewById(R.id.fecharAdcLembrete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                popout.findViewById(R.id.et_dataRealizacao).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(TelaPrincipal.this, R.style.Theme_meuDateDialog, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                                mes = mes+1;
                                et_dataRealizacao.setText(dia + "/" + mes + "/" + ano);
                            }
                        }, ano, mes, dia
                        );
                        datePickerDialog.show();
                    }
                });
                dialogBuilder.setView(popout);
                dialog = dialogBuilder.create();
                dialog.show();

            }
        });

        // Trabalhando com Recycler View, instanciando e preenchendo.
        rv_lembretes = findViewById(R.id.mensagensRecyclerView);

        l1 = getResources().getStringArray(R.array.tarefas);
        l2 = getResources().getStringArray(R.array.datas_cadastro);
        l3 = getResources().getStringArray(R.array.datas_previsao);

        MeuAdapter meuAdapter = new MeuAdapter(this, l1, l2, l3);
        rv_lembretes.setAdapter(meuAdapter);
        rv_lembretes.setLayoutManager( new LinearLayoutManager(this));
        rv_lembretes.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL){
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });
    }

    private void setupFirebase() {
        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        mMsgsReference = FirebaseFirestore.getInstance().collection("mensagens");
        //getRemoteMsgs();
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

    public void salvarMensagem(View view) {
        String mensagem = mensagemEditText.getText().toString();
        Mensagem m = new Mensagem(fireUser.getEmail(), new Date(), mensagem);
        esconderTeclado(view);
        mMsgsReference.add(m);
    }

    private void esconderTeclado(View view){
        InputMethodManager ims = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        ims.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}


//public class TelaPrincipal extends AppCompatActivity {
//
//    private RecyclerView rv_lembretes;
//
//    private DrawerLayout dl;
//    private NavigationView navigationView;
//    private String l1[], l2[], l3[];
//    private AlertDialog.Builder dialogBuilder;
//    private AlertDialog dialog;
//    private FloatingActionButton fb_addLembrete;
//    private EditText et_dataRealizacao;
//
//    private RecyclerView mensagensRecyclerView;
//    private ChatAdapter adapter;
//    private List<Mensagem> mensagens;
//    private TextView lembreteText;
//    private FirebaseUser fireUser;
//    private CollectionReference mMsgsReference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tela_principal);
//        getWindow().setBackgroundDrawableResource(R.drawable.telaprincipal);
//
//        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
//        mensagens = new ArrayList<>();
//        adapter = new ChatAdapter(mensagens, this);
//        mensagensRecyclerView.setAdapter(adapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        mensagensRecyclerView.setLayoutManager(linearLayoutManager);
//        lembreteText = findViewById(R.id.lembreteEditText);
//
//        dl = findViewById(R.id.drawerLayout);
//
//        // Metodo para abrir a navi view
//        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dl.openDrawer(GravityCompat.START);
//            }
//        });
//
//        // Metodo de logout
//        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(TelaPrincipal.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            }
//        });
//
//        // Trabalhando com o navi view
//        navigationView = findViewById(R.id.nav_principal);
//        navigationView.setItemIconTintList(null);
//        Menu menu = navigationView.getMenu();
//        menu.clear();
//
//
//        // Colocar a função de adicionar categoria
//        navigationView.getHeaderView(0).findViewById(R.id.btn_AdcCategoria).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogBuilder = new AlertDialog.Builder(TelaPrincipal.this);
//                View popout = getLayoutInflater().inflate(R.layout.adc_categoria, null);
//                Spinner spinnerCor = popout.findViewById(R.id.escolhaIcone);
//                popout.findViewById(R.id.fecharAdcCategoria).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//                popout.findViewById(R.id.btn_criarCategoria).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        EditText et_nome = popout.findViewById(R.id.et_nomeCategoria);
//                        String nomeCategoria = et_nome.getText().toString();
//                        EditText et_icone = popout.findViewById(R.id.escolhaIcone);
//                        String icone = et_icone.getText().toString();
//                        if(icone.equalsIgnoreCase("bookmark")){
//                            menu.add(nomeCategoria).setIcon(R.drawable.ic_document_delivery_64);
//                        }
//                    }
//                });
//                dialogBuilder.setView(popout);
//                dialog = dialogBuilder.create();
//                dialog.show();
//            }
//        });
//
//        // Adiocionar lembrete
//        fb_addLembrete = findViewById(R.id.fb_addLembrete);
//        fb_addLembrete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog.OnDateSetListener dateSetListener;
//                Calendar calendar = Calendar.getInstance();
//                int dia = calendar.get(Calendar.DAY_OF_MONTH);
//                int mes = calendar.get(Calendar.MONTH);
//                int ano = calendar.get(Calendar.YEAR);
//                dialogBuilder = new AlertDialog.Builder(TelaPrincipal.this);
//                View popout = getLayoutInflater().inflate(R.layout.adc_lembrete, null);
//                et_dataRealizacao = popout.findViewById(R.id.et_dataRealizacao);
//
//                popout.findViewById(R.id.fecharAdcLembrete).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//
//                popout.findViewById(R.id.et_dataRealizacao).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        DatePickerDialog datePickerDialog = new DatePickerDialog(TelaPrincipal.this, R.style.Theme_meuDateDialog, new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
//                                mes = mes+1;
//                                et_dataRealizacao.setText(dia + "/" + mes + "/" + ano);
//                            }
//                        }, ano, mes, dia
//                        );
//                        datePickerDialog.show();
//                    }
//                });
//                dialogBuilder.setView(popout);
//                dialog = dialogBuilder.create();
//                dialog.show();
//            }
//        });
//
//        // Trabalhando com Recycler View, instanciando e preenchendo.
////        rv_lembretes = findViewById(R.id.mensagensRecyclerView);
////
////        l1 = getResources().getStringArray(R.array.tarefas);
////        l2 = getResources().getStringArray(R.array.datas_cadastro);
////        l3 = getResources().getStringArray(R.array.datas_previsao);
////
////        MeuAdapter meuAdapter = new MeuAdapter(this, l1, l2, l3);
////        rv_lembretes.setAdapter(meuAdapter);
////        rv_lembretes.setLayoutManager( new LinearLayoutManager(this));
////        rv_lembretes.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL){
////            @Override
////            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
////                super.onDraw(c, parent, state);
////            }
////        });
////    }
//
//        //Recycler View - versão Gustavo
////        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
////        mensagens = new ArrayList<>();
////        adapter = new ChatAdapter(mensagens, this);
////        mensagensRecyclerView.setAdapter(adapter);
////        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
////        //linearLayoutManager.setReverseLayout(true);
////        mensagensRecyclerView.setLayoutManager(linearLayoutManager);
////        mensagemEditText = findViewById(R.id.lembreteEditText);
//    }
//
//    private void setupFirebase() {
//        fireUser = FirebaseAuth.getInstance().getCurrentUser();
//        mMsgsReference = FirebaseFirestore.getInstance().collection("mensagens");
//        getRemoteMsgs();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        setupFirebase();
//    }
//
//    private void getRemoteMsgs() {
//        mMsgsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
//                mensagens.clear();
//                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
//                    Mensagem incomingMsg = doc.toObject(Mensagem.class);
//                    mensagens.add(incomingMsg);
//                }
//                Collections.sort(mensagens);
//                adapter.notifyDataSetChanged();
//            }
//        });
//    }
//
//    public void enviarMensagem(View view){
//        String mensagem = lembreteText.getText().toString();
//        Mensagem m = new Mensagem(fireUser.getEmail(), new Date(), mensagem);
//        esconderTeclado(view);
//        mMsgsReference.add(m);
//    }
//
//    private void esconderTeclado(View view){
//        InputMethodManager ims = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//        ims.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        if (dl.isDrawerOpen(GravityCompat.START)) {
//            dl.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//}