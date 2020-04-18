package br.com.michel.android.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.model.COnversa;
import br.com.michel.android.whatsapp.model.Grupo;
import br.com.michel.android.whatsapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<COnversa> cOnversas;
    private Context context;

    public ConversasAdapter(List<COnversa> list, Context c) {
        this.cOnversas = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        COnversa cOnversa = cOnversas.get(position);
        holder.ultimaMensagem.setText(cOnversa.getUltimamsg());

        if(cOnversa.getIsgrupo().equals("true")){
            Grupo grupo = cOnversa.getGrupo();
            holder.nome.setText(grupo.getNome());

            if (grupo.getFoto() != null){
                Uri url = Uri.parse(grupo.getFoto());
                Glide.with(context).load(url).into(holder.foto);
            }else {
                holder.foto.setImageResource(R.drawable.padrao);
            }
        }else {
            Usuario usuario = cOnversa.getUsuario();
            if(usuario != null){
                holder.nome.setText(usuario.getNome());

                if (usuario.getFoto() != null){

                    Uri url = Uri.parse(usuario.getFoto());
                    Glide.with(context).load(url).into(holder.foto);
                }else {
                    holder.foto.setImageResource(R.drawable.padrao);
                }
            }



        }
    }

    @Override
    public int getItemCount() {
        return  cOnversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView foto;
        TextView nome, ultimaMensagem;

        public MyViewHolder(View itemview) {
            super(itemview);

            foto = itemview.findViewById(R.id.imageCirculecontato);
            nome = itemview.findViewById(R.id.textViewNomeContato);
            ultimaMensagem = itemview.findViewById(R.id.textViewEmailCOntato);
        }
    }
}
