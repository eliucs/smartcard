package com.wes.test;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.os.Bundle;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //ImageView image = (ImageView) findViewById(R.id.thumbnail);

                Bitmap bitmap = null;
                String barcode = "13513";
                try {
                   bitmap = encodeAsBitmap(barcode,BarcodeFormat.PDF_417,800, 600);
                    ImageView image = new ImageView(MainActivity.this);
                    image.setImageBitmap(bitmap);
                    createNotification("Starbucks", bitmap);


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Scan Here")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
                    builder.create().show();

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void createNotification (String cardName, Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 220, 290, true);
        Notification page2 = new NotificationCompat.Builder(this).extend(new NotificationCompat.WearableExtender().setBackground(scaledBitmap)
                .setHintShowBackgroundOnly(true)).build();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(cardName).setContentText(cardName + " is close by.")
                .setSmallIcon(R.drawable.ic_small).extend(new NotificationCompat.WearableExtender().addPage(page2));

        int notificationID = 4914;
        NotificationManagerCompat.from(this).notify(notificationID, builder.build());
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

        private static String guessAppropriateEncoding(CharSequence contents) {
            // Very crude at the moment
            for (int i = 0; i < contents.length(); i++) {
                if (contents.charAt(i) > 0xFF) {
                    return "UTF-8";
                }
            }
            return null;
        }
}
