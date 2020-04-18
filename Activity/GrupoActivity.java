package br.com.michel.android.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.adapter.ContatosAdapter;
import br.com.michel.android.whatsapp.adapter.GrupoSelecionadoAdapter;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.RecyclerItemClickListener;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;
import br.com.michel.android.whatsapp.model.Usuario;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMembrosSelecionados, recyclerViewMembros;
    private ContatosAdapter contatosAdapter;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private List<Usuario> listMembros = new ArrayList<>();
    private List<Usuario> listMembrosSlecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private DatabaseReference usuariosREF;
    private FirebaseUser firebaseUser;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;


    public  void atualizarMembrosToobar(){

        int totalSelecionado = listMembrosSlecionados.size();
        int total = listMembros.size() + totalSelecionado;

        toolbar.setSubtitle(totalSelecionado + " " + "de" +" " + total + " " + "selecionados");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewMembros = findViewById(R.id.recicyclerMembro);
        recyclerViewMembrosSelecionados = findViewById(R.id.recyclerViewMembrosSelecionados);
        floatingActionButton = findViewById(R.id.fabsalvargrupo);

        usuariosREF = ConfigFirebase.getFirebase().child("usuariosWhatsApp");
        firebaseUser = UsuarioFirebae.getUserAtual();

        //configurando o adapter
        contatosAdapter = new ContatosAdapter(listMembros, getApplication());

        //
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewMembros.setLayoutManager(layoutManager);
        recyclerViewMembros.setHasFixedSize(true);
        recyclerViewMembros.setAdapter(contatosAdapter);

        recyclerViewMembros.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerViewMembros, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listMembros.get(position);

                //removendo o usuario da lista
                listMembros.remove(usuarioSelecionado);
                contatosAdapter.notifyDataSetChanged();

                //adicionando usuario na lista selecionada
                listMembrosSlecionados.add(usuarioSelecionado);
                grupoSelecionadoAdapter.notifyDataSetChanged();

                atualizarMembrosToobar();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listMembrosSlecionados, getApplicationContext());

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMembrosSelecionados.setLayoutManager(layoutManager1);
        recyclerViewMembrosSelecionados.setHasFixedSize(true);
        recyclerViewMembrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        recyclerViewMembrosSelecionados.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerViewMembrosSelecionados, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecioanado = listMembrosSlecionados.get(position);

                listMembrosSlecionados.remove(usuarioSelecioanado);
                grupoSelecionadoAdapter.notifyDataSetChanged();

                listMembros.add(usuarioSelecioanado);
                contatosAdapter.notifyDataSetChanged();

                atualizarMembrosToobar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(GrupoActivity.this, CadastroGrupoActivity.class);
                i.putExtra("membros", (Serializable) listMembrosSlecionados);
                startActivity(i);
            }
        });


    }

    public void recuperarContatos(){

        valueEventListenerMembros = usuariosREF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dados: dataSnapshot.getChildren()){

                    Usuario usuario = dados.getValue(Usuario.class);

                    String userAtual = firebaseUser.getEmail();
                    if ( !userAtual.equals(usuario.getEmail()) ){
                        listMembros.add(usuario);
                    }
                }
                contatosAdapter.notifyDataSetChanged();
                atualizarMembrosToobar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosREF.removeEventListener(valueEventListenerMembros);
    }
}
