package com.example.joseantonio.indicadoresmovil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.joseantonio.indicadoresmovil.Adapters.GridViewVer;
import com.example.joseantonio.indicadoresmovil.Modelos.GridItem;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luis  manuel cobian on 18/01/2017.
 */
public class Indicadores extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //MUestra todos los temas
    public Indicadores() {
        // Required empty public constructor

    }


    private GridView mGridView;
    private ProgressBar mProgressBar;
    String id_anun;
    private GridViewVer mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String FEED_URL = "http://plancolima.col.gob.mx/apis/get_temas";
    private String FEED_URLs = "http://plancolima.col.gob.mx/apis/get_temas";
    String  NombreCompleto,pass;
    TextView user, txtNombreCompleto;
    private Session session;
    EditText buscar;
    String busqueda="no";
    String indica,activar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_indicadores);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // buscar=(EditText)findViewById(R.id.buscar);

        action();

        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewVer(this, R.layout.gridviewtema, mGridData);
        mGridView.setAdapter(mGridAdapter);


        //Start download
        Conexion();
        httpHandler handler = new httpHandler();

        String txt = handler.post(FEED_URLs,busqueda,indica);

        mProgressBar.setVisibility(View.VISIBLE);

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                GridItem item = (GridItem) parent.getItemAtPosition(position);
                mensaje(v.getId());


                //Start details activity

                mGridData.get(position);

            }
        });



        //RECIBO LOS PARAMETROS
        SharedPreferences prefe=getSharedPreferences("usuario", Context.MODE_PRIVATE);
        NombreCompleto=prefe.getString("mail","");
        pass=prefe.getString("pass","");



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //inserto el nombre en el nav
        View header=navigationView.getHeaderView(0);
        txtNombreCompleto = (TextView) header.findViewById(R.id.txtNombreCompleto);
        //txtNombreCompleto.setText(NombreCompleto);
        final ImageView user=(ImageView)header.findViewById(R.id.User);


        RequestQueue requestQueues;
        String json = "http://plancolima.col.gob.mx/apis/get_login";


        requestQueues = Volley.newRequestQueue(getApplicationContext());

        StringRequest request=new StringRequest(com.android.volley.Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject o = new JSONObject(response);

                    JSONArray a = o.getJSONArray("clientes");
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject contacto=a.getJSONObject(i);
                        String nombre=contacto.getString("nombre");
                        String cargo=contacto.getString("email");
                        String email=contacto.getString("cargo");
                        String val=contacto.getString("imagen");
                        txtNombreCompleto.setText(nombre);

                        Picasso.with(Indicadores.this)
                                .load(val)
                                .into(user);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parameters=new HashMap<String, String>();
                Bundle bundle = getIntent().getExtras();
                String id="1";
                parameters.put("email",NombreCompleto);
                parameters.put("password",pass);
                return parameters;

            }
        };
        requestQueues.add(request);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.indicadores, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            back_inicio();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            back_inicio();
        } else if (id == R.id.nav_gallery) {
            back_inicio();
        } else if (id == R.id.nav_slideshow) {
            back_favoritos();
        }  else if (id == R.id.nav_share) {
            session = new Session(this);
            //aqui cierras sesion
            logout();
        }
        else if(id==R.id.nav_temas){
            back_temas();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(FEED_URLs);
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("email",busqueda));


                httppost.setEntity(new UrlEncodedFormEntity(parametros));

                HttpResponse httpResponse = httpclient.execute(httppost);

                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                // Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI

            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Retorno();
            }

            //Hide progressbar
            mProgressBar.setVisibility(View.GONE);
        }
    }
    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("temas");
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("tema");

                id_anun = post.optString("id_tema");
                String curso = post.optString("curso");
                ;
                item = new GridItem();
                item.setTitle(title);
                if (curso=="null"){

                    curso="No esta definida";
                }
                item.setCourse("Fuente : "+"  "+ curso);
                item.setId(id_anun);

                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void back_inicio(){
        Intent favoritos=new Intent(Indicadores.this,Ver_Indicadores.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(favoritos);

        finish();
    }

    public void back_login(){
        Intent favoritos=new Intent(Indicadores.this,MainActivity.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(favoritos);
        finish();
    }
    public void back_favoritos(){
        Intent favoritos=new Intent(Indicadores.this,Favoritos.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(favoritos);
        finish();
    }

    public  void back_temas(){

        Intent favoritos=new Intent(Indicadores.this,Indicadores.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(favoritos);
        finish();

    }
    private void logout(){
        session.setLoggedin(false);
        finish();
        back_login();
    }

    private  void  mensaje(final int id){

        final Intent intenta = new Intent(Indicadores.this,Temas.class);
        intenta.putExtra("id",id);//esta funcion jala los id
        startActivity(intenta);

        SharedPreferences preferencias=getSharedPreferences("indicadores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("ids",String.valueOf(id));
        editor.commit();

    }


    public void buscar(){

        buscar.setVisibility(View.VISIBLE);
        indica=buscar.getText().toString();

        SharedPreferences preferencias=getSharedPreferences("busca", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("mail", buscar.getText().toString());
        editor.commit();
        if (buscar.length()>0){



            Intent favoritos=new Intent(Indicadores.this,GridViewActivity.class);

            favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

            startActivity(favoritos);
            finish();


        }

    }

    public void action(){
        ActionBar actionBar=getSupportActionBar();
        getSupportActionBar().setCustomView(R.layout.miactionbar);

        buscar=(EditText) actionBar.getCustomView().findViewById(R.id.busqueda);
        buscar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Toast.makeText(GridViewActivity.this,"a buscar"+busca.getText().toString(),Toast.LENGTH_LONG).show();
                return false;
            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);
    }


    private void Conexion() {

        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean tipoConexion1 = false;
        boolean tipoConexion2 = false;

        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected()) {
                tipoConexion1 = true;
            }
            if (mMobile.isConnected()) {
                tipoConexion2 = true;
            }

            if (tipoConexion1 == true || tipoConexion2 == true) {
               /* Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos */

                new Indicadores.AsyncHttpTask().execute(FEED_URL);
            }
        } else {
       /* No estas conectado a internet */
            confirmacion();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void confirmacion(){
        Context context=this;
        AlertDialog.Builder aler=new AlertDialog.Builder(context)
                .setTitle("Sin Conexión")
                .setMessage("Revise Su Conexión a Internet ").setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        AlertDialog alertDialog=aler.create();
        alertDialog.show();
    }

    private void Retorno(){
        Toast.makeText(Indicadores.this, "No Se Encontraron Temas ", Toast.LENGTH_SHORT).show();
        back_temas();
    }


}
