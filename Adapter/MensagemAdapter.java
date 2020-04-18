package br.com.michel.android.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;
import br.com.michel.android.whatsapp.model.Mensagem;

public class MensagemAdapte extends RecyclerView.Adapter<MensagemAdapte.MyViewHolder> {

    private List<Mensagem> mensagemList;
    private Context context;
    private static final  int TIPO_REMETENTE = 0;
    private static final  int TIPO_DESTINATARIO = 1;


    public MensagemAdapte(List<Mensagem> list, Context c) {
        this.mensagemList = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;

        if (viewType == TIPO_REMETENTE){

            item  = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_msg_remetente, parent, false);

        }else if (viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_msg_destinatario, parent, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Mensagem mensagem = mensagemList.get(position);

        String mSG = mensagem.getMsg();
        String iMG = mensagem.getImg();

        holder.msg.setVisibility(View.VISIBLE);
        holder.img.setVisibility(View.VISIBLE);

        if (iMG != null){
            Uri url = Uri.parse(iMG);
            Glide.with(context).load(url).into(holder.img);

            String nome = mensagem.getNome();
            if (!nome.isEmpty()){
                holder.nome.setText(nome);
            }

            holder.msg.setVisibility(View.GONE);
        }else {

            holder.msg.setText(mSG);

            String nome = mensagem.getNome();
            if (!nome.isEmpty()){
                holder.nome.setText(nome);
            }else {
                holder.nome.setVisibility(View.GONE);
            }

            holder.img.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mensagemList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem = mensagemList.get(position);

        String idUser = UsuarioFirebae.getIdentificadordeUsuario();

        if (idUser.equals(mensagem.getIdUsuario())){
            return TIPO_REMETENTE;
        }
        return TIPO_DESTINATARIO;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView msg;
        ImageView img;
        TextView nome;


        public MyViewHolder(View itemview){
            super(itemview);

            msg = itemview.findViewById(R.id.textViewMSG);
            img = itemview.findViewById(R.id.imageViewMSG);
            nome = itemview.findViewById(R.id.txtNomeexibi);
        }
    }
}
