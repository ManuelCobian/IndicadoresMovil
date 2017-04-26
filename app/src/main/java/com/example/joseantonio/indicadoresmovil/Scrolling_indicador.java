package com.example.joseantonio.indicadoresmovil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Scrolling_indicador extends AppCompatActivity {
    TextView Secretaria,Formula,val,Titulo,lista,actu,status;
    Toolbar toolbar;
    CardView cardView;
    RequestQueue requestQueue;
    CollapsingToolbarLayout  name;
    String json="http://plancolima.col.gob.mx/apis/get_indicador",names;
    GraphView graph;
    TableLayout table;
    TableRow row;
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    String secciones;
    ImageView image_paralax;

    private static final String URL = "http://plancolima.col.gob.mx/apis/get_indicador";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_indicador);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Secretaria = (TextView) findViewById(R.id.Fuente);
        Formula=(TextView)findViewById(R.id.formula) ;
        val=(TextView)findViewById(R.id.val);
        image_paralax=(ImageView)findViewById(R.id.image_paralax);
        Titulo=(TextView)findViewById(R.id.Titulo);
        graph = (GraphView) findViewById(R.id.grafica);
        actu=(TextView)findViewById(R.id.actu);
        status=(TextView)findViewById(R.id.status);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //RECIBO LOS PARAMETROS
        SharedPreferences prefe=getSharedPreferences("menus", Context.MODE_PRIVATE);
        secciones=prefe.getString("back","");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id_indica = String.valueOf(getIntent().getExtras().getInt("id"));//recibo el di lo convierto en int

                Conexion();
            }
        });


        final String id_indica = String.valueOf(getIntent().getExtras().getInt("id"));//recibo el di lo convierto en int

        datos(id_indica);
        tablas(id_indica);
        graficar(id_indica);
        menu();

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } // Habilitar up button back

    }


    public void menu(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secciones.equals("favoritos")){

                    Intent favoritos=new Intent(Scrolling_indicador.this,Favoritos.class);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(favoritos);
                    finish();

                }

                if (secciones.equals("lista")){
                    Intent favoritos=new Intent(Scrolling_indicador.this,Ver_Indicadores.class);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(favoritos);
                    finish();

                }
                if (secciones.equals("temas")){
                    Intent favoritos=new Intent(Scrolling_indicador.this,Indicadores.class);

                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(favoritos);
                    finish();

                }
                if (secciones.equals("buscar")){
                    Intent favoritos=new Intent(Scrolling_indicador.this,GridViewActivity.class);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(favoritos);
                    finish();
                    startActivity(favoritos);
                }


                onBackPressed();//para serrar
            }
        });

    }



    public  void graficar(final String id_indicar){
        val.setText("");
        RequestQueue requestQueue;
        String json = "http://plancolima.col.gob.mx/apis/get_indicador";

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request=new StringRequest(Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject o = new JSONObject(response);

                    JSONArray a = o.getJSONArray("indicador");
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject contacto = a.getJSONObject(i);

                        //Log.d("a",String.valueOf(a));

                        String nombre = contacto.getString("nombre");
                        String Fuente = contacto.getString("fuente");
                        String formula = contacto.getString("formula");
                        String valor = contacto.getString("valor");
                        String alerta = contacto.getString("alerta");

                        char signo='%';
                        String medida = contacto.getString("medida");
                        String val0 = contacto.getString("val0");
                        String val1 = contacto.getString("val1");
                        String val2 = contacto.getString("val2");
                        String val3 = contacto.getString("val3");
                        String val4 = contacto.getString("val4");
                        String val5 = contacto.getString("val5");
                        String val6 = contacto.getString("val6");
                        String val7 = contacto.getString("val7");
                        String val8 = contacto.getString("val8");
                        String val9 = contacto.getString("val9");

                        String fecha0 = contacto.getString("fecha0");
                        String fecha1 = contacto.getString("fecha1");
                        String fecha2 = contacto.getString("fecha2");
                        String fecha3 = contacto.getString("fecha3");
                        String fecha4 = contacto.getString("fecha4");
                        String fecha5 = contacto.getString("fecha5");
                        String fecha6 = contacto.getString("fecha6");
                        String fecha7 = contacto.getString("fecha7");
                        String fecha8 = contacto.getString("fecha8");
                        String fecha9 = contacto.getString("fecha9");



                        //Log.d("signo",medida);
                        if(medida.equals("PORCENTAJE")){
                            signo='%';
                        }

                        if(val0.equals("0")&& val9.equals("0")){
                            status.setText("Inactivo");
                        }
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {

                                new DataPoint(1, Double.parseDouble(val0)),
                                new DataPoint(2, Double.parseDouble(val1)),
                                new DataPoint(3, Double.parseDouble(val2)),
                                new DataPoint(4, Double.parseDouble(val3)),
                                new DataPoint(5, Double.parseDouble(val4)),
                                new DataPoint(6, Double.parseDouble(val5)),
                                new DataPoint(7, Double.parseDouble(val6)),
                                new DataPoint(8, Double.parseDouble(val7)),
                                new DataPoint(9, Double.parseDouble(val8)),
                                new DataPoint(10, Double.parseDouble(val9)),

                        });
                        // use static labels for horizontal and vertical labels
                        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);


                        if (fecha0.equals("0") || fecha9.equals("0")){
                            staticLabelsFormatter.setHorizontalLabels(new String[] {"sin dato inicial", fecha9});
                            if (fecha0.equals("0") && fecha9.equals("0")){
                                staticLabelsFormatter.setHorizontalLabels(new String[] {"sin dato inicial", fecha9});
                                if (fecha9.equals("0")){
                                    staticLabelsFormatter.setHorizontalLabels(new String[] {"sin dato inicial", "sin ultimo dato"});
                                }
                            }
                        }
                        else {

                            staticLabelsFormatter.setHorizontalLabels(new String[] {formatoDeFecha(fecha0), formatoDeFecha(fecha0)});
                        }

                        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                        series.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {

                                Toast.makeText(Scrolling_indicador.this,dataPoint+"%", Toast.LENGTH_SHORT).show();

                            }
                        });

                        //Color a la grafica
                        if (alerta.equals("danger")){
                            series.setColor(Color.RED);
                            graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
                        }
                        else
                        if (alerta.equals("success")) {
                            series.setColor(Color.parseColor("#66bb6a"));
                            graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.parseColor("#66bb6a"));

                        }
                        graph.addSeries(series);


                        if(Fuente=="null"){
                            Fuente="No Esta definida";
                        }

                        // Secretaria.append("Fuente :"+" "+Fuente);
                        // Formula.setText("Formula  :"+" "+formula);


                        name.setTitle("Valor Actual"+valor+medidas(medida));

                        val.setText(Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1,nombre.length()).toLowerCase());


                        name.setTitle("Valor Actual \n" +valor+medidas(medida));
                        Titulo.setTextSize(14);


                        Titulo.setText("Unidad :"+" "+

                                Character.toUpperCase(medida.charAt(0)) + medida.substring(1,medida.length()).toLowerCase()
                        );
                        if(fecha9.equals("0")){
                            actu.setText("Ninguna");

                        }
                        else{
                            actu.setText(formatoDeFecha(fecha9));

                        }
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
                parameters.put("id",id_indicar);


                return parameters;

            }
        };
        requestQueue.add(request);


    }

    public void datos(final String id_indicar ){


        String signo;
        String valor;
        StringRequest request=new StringRequest(Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject o = new JSONObject(response);

                    //Log.d("dates",String.valueOf(response));
                    JSONArray a = o.getJSONArray("indicador");
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject contacto=a.getJSONObject(i);

                        String nombre=contacto.getString("nombre");
                        String Fuente=contacto.getString("fuente");
                        String formula=contacto.getString("formula");

                        String id_tema =contacto.getString("id_tema");
                        String valor =contacto.getString("valor");
                        String medida = contacto.getString("medida");
                        String alerta = contacto.getString("alerta");



                        char signo='%';
                        nombre = Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1,nombre.length());


                        names=nombre;

                        ImageView image = (ImageView) findViewById(R.id.image_paralax);//La imagen de cada indicador



                        int id=0;

                        id=Integer.parseInt(id_tema);

                        if(id==1){

                            // Usando Glide para la carga asíncrona
                            Glide.with(Scrolling_indicador.this)
                                    .load(R.drawable.salud)
                                    .centerCrop()
                                    .into(image);
                            // NAME="Salud";

                        }

                        else
                        if (id==2){
                            // Usando Glide para la carga asíncrona
                            Glide.with(Scrolling_indicador.this)
                                    .load(R.drawable.educat)
                                    .centerCrop()
                                    .into(image);

                        }


                        else
                        if (id==3){


                        }

                        if(Fuente=="null"){
                            Fuente="No Esta definida";
                        }


                        if(medida.equals("PORCENTAJE")){
                            signo='%';

                        }
                        Secretaria.append(Character.toUpperCase(Fuente.charAt(0)) + Fuente.substring(1,Fuente.length()).toLowerCase());
                        Formula.setText(
                                Character.toUpperCase(formula.charAt(0)) + formula.substring(1,formula.length()).toLowerCase(

                                ));




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
                parameters.put("id",id_indicar);


                return parameters;

            }
        };
        requestQueue.add(request);

    }


    public  void limpia_grafica(){
        graph.removeAllSeries();
    }


    private void  Update_tabla(){
        int count = table.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = table.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
    }
    public void tablas(final String id_indicar){

        val.setText("");
        RequestQueue requestQueue;

        String json = "http://plancolima.col.gob.mx/apis/get_indicador";
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request=new StringRequest(Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String resultado="";
                    for (String retval: response.split("<br>")) {
                        resultado=retval;
                        System.out.println(resultado);
                    }
                    JSONObject o = new JSONObject(resultado);

                    JSONArray a = o.getJSONArray("valores");
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject contacto = a.getJSONObject(i);

                        String valor = contacto.getString("valor");
                        String dates = contacto.getString("fecha");
                        String medidas = contacto.getString("medida");

                        char signo='%';


                        Log.d("date",dates);


                        table = (TableLayout) findViewById(R.id.repaymentTable);



                        row = (TableRow) LayoutInflater.from(getBaseContext()).inflate(R.layout.row_repayment_plan, null);

                        if(i % 2 != 0) {

                            row.setBackgroundColor(Color.LTGRAY);
                        }


                        if (dates.equals("0")){
                            ((TextView) row.findViewById(R.id.amount)).setText("fecha no registrada");
                            ((TextView) row.findViewById(R.id.date)).setText(valor+medidas(medidas));
                        }

                        else {

                            ((TextView) row.findViewById(R.id.amount)).setText(formatoDeFecha(dates));
                            ((TextView) row.findViewById(R.id.date)).setText(valor+medidas(medidas));

                        }




                        ((TextView) row.findViewById(R.id.amount)).setGravity(Gravity.CENTER);
                        ((TextView) row.findViewById(R.id.date)).setGravity(Gravity.CENTER);

                        table.addView(row);




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
                parameters.put("id",id_indicar);


                return parameters;

            }
        };
        requestQueue.add(request);
    }

    //sirve para quitar la hora:minutos:segundos de la  fecha
    public String formatoDeFecha(String fecha) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = fmt.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
        return fmtOut.format(date);

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
                limpia_grafica();
                final String id_indica = String.valueOf(getIntent().getExtras().getInt("id"));//recibo el di lo convierto en int
                graficar(id_indica);
                Update_tabla();
                tablas(id_indica);


            }
        } else {
       /* No estas conectado a internet */
            mensaje();

        }
    }

    private void mensaje(){
        Context context=this;
        AlertDialog.Builder aler=new AlertDialog.Builder(context)
                .setTitle("Sin Conexión")
                .setMessage("Active sus datos o Wifi ").setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog alertDialog=aler.create();
        alertDialog.show();
    }

    private  String medidas(String medidas) {

        switch (medidas) {

            case "PORCENTAJE":
                medidas = "%";
                break;
            case "AÑOS":
                medidas = "AS";
                break;
            case "COMUNIDAD":
                medidas = "COM";
                break;
            case "VERIFICACIONES":
                medidas = "VER";
                break;
            case "NIÑOS":
                medidas = "NIN";
                break;
            case "ESCUELAS":
                medidas = "ESC";
                break;
            case "PORCENTAJE DE CAMPAÑAS":
                medidas = "PCÑ";
                break;
            case "DETECCIONES":
                medidas = "DET";
                break;
            case "PUNTO PORCENTUAL":
                medidas = "PPR";
                break;
            case "PROFESIONALES DE SALUD CAPACITADOS":
                medidas = "PSC";
                break;
            case "CONSULTAS POR EMBARAZADAS":
                medidas = "CPE";
                break;
            case "CONSULTAS":
                medidas = "CON";
                break;
            case "CASOS":
                medidas = "CA";
                break;
            case "PERSONAS":
                medidas = "PER";
                break;
            case "HISOPOS":
                medidas = "HI";
                break;
            case "LOCALIDADES":
                medidas = "LOC";
                break;
            case "DONADORES":
                medidas = "DON";
                break;
            case "MUESTRAS":
                medidas = "MU";
                break;

            case "TASA":
                medidas = "TA";
                break;

            case "PUNTO PORCENTUA":
                medidas = "PNP";
                break;

            case "REPORTE":
                medidas = "REP";
                break;

            case "DICTAMENES":
                medidas = "DIC  ";
                break;
            case "EMERGENCIAS":
                medidas = "EM";
                break;
            case "INDICE":
                medidas = "IND";
                break;
            case "ABSOLUTO":
                medidas = "ABS";
                break;
            default:
                medidas = "%";
                break;


        }
        return medidas;
    }

}
