package br.com.michel.android.whatsapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.fragment.ContatosFragments;
import br.com.michel.android.whatsapp.fragment.ConversasFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class PrincipalActivity extends AppCompatActivity {

    private Button button;
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAutentication();
    private MaterialSearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //criando toobar

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        // esse adapter e para as abas
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Conversas", ConversasFragment.class)
                        .add("Contatos", ContatosFragments.class).create());

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout smartTabLayout = findViewById(R.id.viewpagertab);
        smartTabLayout.setViewPager(viewPager);

        //configurando a lupa, la no "onCreateOptionsMenu" tem a continuacao da configuracao da lupa

        searchView = findViewById(R.id.materialSerachPrincipal);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                fragment.recarregarConversas();
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.d("evento","onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("evento","onQueryTextChange");

                // a linha de baixo esta dizendo como chamar um metodo de entro de um fragment
                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                if (newText != null && !newText.isEmpty()){
                    fragment.pesquisarConversas(newText.toLowerCase());
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                deslogarUsuario();
                finish();
                 break;
            case R.id.menuConfug:
                abrirConfig();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void abrirConfig() {
        Intent intent = new Intent(PrincipalActivity.this, ConfigActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //configurando o botao de pesquisa, tambem faz parte da configuracao da lupa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    public void deslogarUsuario() {
        try {
            auth.signOut();


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
