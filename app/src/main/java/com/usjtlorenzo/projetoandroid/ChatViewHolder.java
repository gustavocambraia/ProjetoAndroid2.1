package com.usjtlorenzo.projetoandroid;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    TextView dataNomeTextView;
    TextView mensagemTextView;

    ChatViewHolder (View view){
        super(view);
        this.dataNomeTextView = view.findViewById(R.id.dataNomeTextView);
        this.mensagemTextView = view.findViewById(R.id.mensagemTextView);
    }
}
