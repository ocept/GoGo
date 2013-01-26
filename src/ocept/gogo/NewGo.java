package ocept.gogo;

import java.util.Date;

import ocept.gogo.db.*;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class NewGo extends Activity {
	goDB dba;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_go);
		
        //init db
        dba = new goDB(this);

        //connect up view
        Button okButton = (Button) findViewById(R.id.newGoOkButton);
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try{
					saveGo();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
        
        ((Button) findViewById(R.id.newGoCancelButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeActivity();
			}
		});
	}
	private void saveGo(){
		//get go data from View
		Go newGo = new Go();
		EditText name = (EditText) findViewById(R.id.newGoName);
		newGo.Name = name.getText().toString();
		newGo.Desc = ((EditText) findViewById(R.id.newGoDesc)).getText().toString();
		newGo.LastChecked = (long) 0;
		
		//open and enter into db
        dba.open();
		dba.insertGo(newGo);
		dba.close();
		
		//close activity
		//GoList.this.myAdapter.notifyDataSetChanged();
		GoList.myAdapter.notifyDataSetInvalidated();
		this.finish();
	}
	private void closeActivity()
	{
		this.finish();
	}
}

