package elec0.slugstop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MetroActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metro);

        // Enable the up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        Button btn10, btn15, btn16, btn19, btn20, btn22;

        btn10 = (Button) findViewById(R.id.btn10);
        btn15 = (Button) findViewById(R.id.btn15);
        btn16 = (Button) findViewById(R.id.btn16);
        btn19 = (Button) findViewById(R.id.btn19);
        btn20 = (Button) findViewById(R.id.btn20);
        btn22 = (Button) findViewById(R.id.btn22);


        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFTools.showPDFUrl(MetroActivity.this, "https://www.scmtd.com/media/bkg/20182/sched/rte_10.pdf");
            }
        });

        btn15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFTools.showPDFUrl(MetroActivity.this, "https://www.scmtd.com/media/bkg/20182/sched/rte_15.pdf");
            }
        });

        btn16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFTools.showPDFUrl(MetroActivity.this, "https://www.scmtd.com/media/bkg/20182/sched/rte_16.pdf");
            }
        });

        btn19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFTools.showPDFUrl(MetroActivity.this, "https://www.scmtd.com/media/bkg/20182/sched/rte_19.pdf");
            }
        });

        btn20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFTools.showPDFUrl(MetroActivity.this, "https://www.scmtd.com/media/bkg/20182/sched/rte_20.pdf");
            }
        });

        btn22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFTools.showPDFUrl(MetroActivity.this, "https://www.scmtd.com/media/bkg/20182/sched/rte_22.pdf");
            }
        });


    }
}
