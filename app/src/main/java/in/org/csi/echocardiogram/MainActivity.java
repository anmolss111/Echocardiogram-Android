package in.org.csi.echocardiogram;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;

    private Spinner weight;

    private TextView bsaValue;
    private TextView bsaUnit;

    private List<String>  siteTags;
    private TableLayout siteValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseInitialise(this);

        weightDropdown();
        weightSelection();

        valueInitialise();
        unitsInitialise();

        siteValuesInitialise(this);
    }

    public void databaseInitialise(Context context){

        db = new DatabaseHelper(context);
    }

    public void valueInitialise(){

        bsaValue = ((TextView)findViewById(R.id.bsaValue));

        bsaValue.setText("");
    }

    public void unitsInitialise(){

        bsaUnit = ((TextView)findViewById(R.id.bsaUnit));

        bsaUnit.setText(Html.fromHtml("M<sup><small>2</small></sup>"));
    }

    public void siteValuesInitialise(Context context){

        List<String> list = new ArrayList<String>();

        siteValues = (TableLayout) findViewById(R.id.siteValues);

        list.addAll(Arrays.asList("RVD", "IVSd", "IVSs" , "LVIDd", "LVIDs" , "LVPWd" , "LVPWs" , "Aortic\nAnnulus" , "Sinuses" , "ST\nJunction" , "Transverse\nArch" , "Isthmus" , "Distal\nArch" , "Ao at\nDiaphragm" , "Pulmonary\nAnnulus" , "MPA" , "RPA" , "LPA" , "Mitral\nAnnulus" , "Tricuspid\nAnnulus" , "Left\nAtrium" ));

        siteTags = list;

        for (int i = 0; i < list.size(); i++) {

            TableRow row = new TableRow(context);

            TextView sites = new TextView(context);
            TextView mean = new TextView(context);
            TextView range = new TextView(context);

            TableLayout.LayoutParams tableRowParams=new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int topMargin = 16;
            int bottomMargin = 16;
            int leftMargin = 8;

            tableRowParams.setMargins(leftMargin, topMargin, 0, bottomMargin);

            row.setLayoutParams(tableRowParams);

            sites.setGravity(Gravity.CENTER);
            sites.setTextSize(16);

            mean.setGravity(Gravity.CENTER);
            mean.setTextSize(16);

            range.setGravity(Gravity.CENTER);
            range.setTextSize(16);

            sites.setText(list.get(i));
            mean.setText("");
            range.setText("");

            row.addView(sites);
            row.addView(mean);
            row.addView(range);

            siteValues.addView(row);
        }


    }

    public void weightDropdown(){

        weight = (Spinner)findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();

        Cursor res = db.getWeights();

        list.add("Select a Value");
        while (res.moveToNext()){
            list.add(res.getString(0));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weight.setAdapter(dataAdapter);
    }

    public void weightSelection(){

        weight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){

                    float selectedweight = Float.parseFloat(parent.getItemAtPosition(position).toString());
                    Cursor res = db.getDataByWeight(selectedweight);

                    res.moveToFirst();

                    bsaValue.setText(res.getString(2));

                    for (int i = 1; i <= siteTags.size(); i++) {

                        for(int j = 1 ; j < 3 ; j++) {
                            ((TextView)((TableRow) siteValues.getChildAt(i)).getChildAt(j)).setText(res.getString(2*i + j));
                        }
                    }
                }
                else {

                    valueInitialise();

                    for (int i = 1; i <= siteTags.size(); i++) {

                        for(int j = 1 ; j < 3 ; j++) {
                            ((TextView)((TableRow) siteValues.getChildAt(i)).getChildAt(j)).setText("");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}