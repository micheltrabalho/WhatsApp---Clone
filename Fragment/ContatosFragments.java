package br.com.michel.android.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.activity.ChatActivity;
import br.com.michel.android.whatsapp.activity.GrupoActivity;
import br.com.michel.android.whatsapp.adapter.ContatosAdapter;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.RecyclerItemClickListener;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;
import br.com.michel.android.whatsapp.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragments extends Fragment {

    private RecyclerView recyclerView;
    private ContatosAdapter contatosAdapter;
    private ArrayList<Usuario> listaContato = new ArrayList<>();
    private DatabaseReference usuariosREF;
    private ValueEventListener valueEventListener;
    private FirebaseUser firebaseUser;


    public ContatosFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos_fragments, container, false);

        usuariosREF = ConfigFirebase.getFirebase().child("usuariosWhatsApp");
        recyclerView = view.findViewById(R.id.recyclerView);
        firebaseUser = UsuarioFirebae.getUserAtual();


        contatosAdapter = new ContatosAdapter(listaContato, getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contatosAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Usuario usuarioSelecionado = listaContato.get(position);
                boolean cabecalho = usuarioSelecionado.getEmail().isEmpty();

                if (cabecalho){
                    Intent i = new Intent(getActivity(), GrupoActivity.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatContato", usuarioSelecionado);
                    startActivity(i);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        Usuario group = new Usuario();
        group.setNome("Novo Grupo");
        group.setEmail("");

        listaContato.add(group);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosREF.removeEventListener(valueEventListener);
    }

    public void recuperarContatos(){
        valueEventListener = usuariosREF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){


                    Usuario usuario = dados.getValue(Usuario.class);

                    String userAtual = firebaseUser.getEmail();
                    if ( !userAtual.equals(usuario.getEmail()) ){
                        listaContato.add(usuario);

                    }
                }

                contatosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
