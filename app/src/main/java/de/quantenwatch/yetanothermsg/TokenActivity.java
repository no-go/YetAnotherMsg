package de.quantenwatch.yetanothermsg;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class TokenActivity extends Activity {
    private ImageView qrCodeImageview;
    public static final int WIDTH = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token_activity);
        qrCodeImageview = (ImageView) findViewById(R.id.imageView);

        String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("preference_token", "");
        Bitmap bitmap = null;

        bitmap =  encodeAsBitmap(token);
        qrCodeImageview.setImageBitmap(bitmap);
    }

    Bitmap encodeAsBitmap(String str) {
        BitMatrix result;
        try {
        result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (Exception e) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }
}
