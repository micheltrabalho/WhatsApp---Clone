package br.com.michel.android.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.activity.ChatActivity;
import br.com.michel.android.whatsapp.adapter.ConversasAdapter;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.RecyclerItemClickListener;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;
import br.com.michel.android.whatsapp.model.COnversa;
import br.com.michel.android.whatsapp.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private RecyclerView recyclerViewConversas;
    private List<COnversa> listCOnversas = new ArrayList<>();
    private ConversasAdapter adapter;
    private DatabaseReference databaseReference;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;


    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);
        recyclerViewConversas = view.findViewById(R.id.recyclerlistaconversas);

        adapter = new ConversasAdapter(listCOnversas, getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);

        //evento de click para abrir a conversa
        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewConversas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                COnversa cOnversaSelecionada = listCOnversas.get(position);

                if (cOnversaSelecionada.getIsgrupo().equals("true")){
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatGrupo", cOnversaSelecionada.getGrupo());
                    startActivity(i);
                }else {
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatContato", cOnversaSelecionada.getUsuario());
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

        String idUser = UsuarioFirebae.getIdentificadordeUsuario();
        databaseReference = ConfigFirebase.getFirebase();
        conversasRef = databaseReference.child("usuariosWhatsApp").child("conversas").child(idUser);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void pesquisarConversas(String texto){

        List<COnversa> listaCOnversa =new ArrayList<>();
        for (COnversa cOnversa : listCOnversas){
            String nome = cOnversa.getUsuario().getNome().toLowerCase();
            String ultimamsg = cOnversa.getUltimamsg().toLowerCase();
            if (nome.contains(texto) || ultimamsg.contains(texto)){
                listCOnversas.add(cOnversa);
            }

        }

        adapter = new ConversasAdapter(listaCOnversa, getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recarregarConversas(){
        adapter = new ConversasAdapter(listCOnversas, getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void recuperarConversas(){


        childEventListenerConversas =  conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                COnversa cOnversa = dataSnapshot.getValue(COnversa.class);
                listCOnversas.add(cOnversa);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
