package com.usjtlorenzo.projetoandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private List<Mensagem> mensagens;
    private Context context;
    ChatAdapter(List<Mensagem> mensagens, Context context){
        this.mensagens = mensagens;
        this.context = context;
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position)
    {
        Mensagem m = mensagens.get(position);
        // holder.dataNomeTextView.setText(context.getString(R.string.data_nome,
        //DateHelper.format(m.getData()), m.getUsuario()));
        holder.dataNomeTextView.setText(context.getString(R.string.data_nome, DateHelper.format(m.getData()), m.getUsuario()));
        holder.mensagemTextView.setText(m.getTexto());
        //mensagemEditText.setText("");
    }
    @Override
    public int getItemCount()
    {
        return mensagens.size();
    }
}