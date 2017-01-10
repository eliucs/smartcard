package com.wes.keyring;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddCardActivity extends AppCompatActivity {

    ArrayAdapter<CharSequence> adapter;

    private final String BARCODE_FORMAT_1 = "CODE-128";
    private final String BARCODE_FORMAT_2 = "PDF417";
    private final String BARCODE_FORMAT_3 = "EAN8";
    private final String BARCODE_FORMAT_4 = "UPC-A";

    private final int BARCODE_FORMAT_LENGTH_1 = 17;
    private final int BARCODE_FORMAT_LENGTH_2 = 17;
    private final int BARCODE_FORMAT_LENGTH_3 = 8;
    private final int BARCODE_FORMAT_LENGTH_4 = 11;

    private final String ERROR_EMPTY_FIELD = "Cannot be empty.";
    private final String ERROR_BARCODE_1 = "Must be less than 17 digits long.";
    private final String ERROR_BARCODE_2 = "Must be 17 digits long.";
    private final String ERROR_BARCODE_3 = "Must be 8 digits long.";
    private final String ERROR_BARCODE_4 = "Must be 11 digits long.";

    EditText cardName = null;
    EditText cardHolder = null;
    Spinner barcodeFormat = null;
    EditText serialNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        cardName = (EditText)findViewById(R.id.field_card_name);
        cardHolder = (EditText)findViewById(R.id.field_card_holder);
        barcodeFormat = (Spinner)findViewById(R.id.field_barcode_format);
        serialNumber = (EditText)findViewById(R.id.field_serial_number);

        Button submit = (Button)findViewById(R.id.submit);

        adapter = ArrayAdapter.createFromResource(this, R.array.barcode_format_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barcodeFormat.setAdapter(adapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean success = true;

                if (cardName.getText().length() == 0) {
                    cardName.setError(ERROR_EMPTY_FIELD);
                    success = false;
                }

                if (cardHolder.getText().length() == 0) {
                    cardHolder.setError(ERROR_EMPTY_FIELD);
                    success = false;
                }

                if (serialNumber.getText().length() == 0) {
                    serialNumber.setError(ERROR_EMPTY_FIELD);
                    success = false;
                }
                else {
                    String selectedBarcodeFormat = barcodeFormat.getSelectedItem().toString();

                    if (selectedBarcodeFormat.equals(BARCODE_FORMAT_1) &&
                            serialNumber.getText().length() > BARCODE_FORMAT_LENGTH_1) {
                        serialNumber.setError(ERROR_BARCODE_1);
                        success = false;
                    }
                    else if (selectedBarcodeFormat.equals(BARCODE_FORMAT_2) &&
                            serialNumber.getText().length() != BARCODE_FORMAT_LENGTH_2) {
                        serialNumber.setError(ERROR_BARCODE_2);
                        success = false;
                    }
                    else if (selectedBarcodeFormat.equals(BARCODE_FORMAT_3) &&
                            serialNumber.getText().length() != BARCODE_FORMAT_LENGTH_3) {
                        serialNumber.setError(ERROR_BARCODE_3);
                        success = false;
                    }
                    else if (selectedBarcodeFormat.equals(BARCODE_FORMAT_4) &&
                            serialNumber.getText().length() != BARCODE_FORMAT_LENGTH_4) {
                        serialNumber.setError(ERROR_BARCODE_4);
                        success = false;
                    }
                }

                if (success) {
                    Toast.makeText(getBaseContext(),"Your card has been added!", Toast.LENGTH_SHORT).show();

                    DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext(), null, null, 1);

                    String cn = cardName.getText().toString();
                    String ch = cardHolder.getText().toString();
                    String bf = barcodeFormat.getSelectedItem().toString();
                    String sn = serialNumber.getText().toString();

                    Card card = new Card(cn, ch, bf, sn);

                    dbHandler.addCard(card);

                    finish();
                }
            }
        });
    }
}
