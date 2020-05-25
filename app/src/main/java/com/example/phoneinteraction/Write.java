package com.example.phoneinteraction;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Write {
    private static Context context;

    public Write(Context context) {
        this.context = context;
    }

    public static void WriteFileExample(String message) {
        FileOutputStream fop = null;
        File file;
        String content = message;

        try {
            File sdcard = Environment.getExternalStorageDirectory();
            file = new File(sdcard, context.getResources().getString(R.string.app_name)+"Log.txt"); //輸出檔案位置
            Log.i("Write File:", file + "");
            fop = new FileOutputStream(file);

            if (!file.exists()) { // 如果檔案不存在，建立檔案
                file.createNewFile();
            }

            byte[] contentInBytes = content.getBytes();// 取的字串內容bytes

            fop.write(contentInBytes); //輸出
            fop.flush();
            fop.close();

            Toast.makeText(context, "輸出完成", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.i("Write E:", e + "");
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                Log.i("Write IOException", e + "");
                e.printStackTrace();
            }
        }
    }
}
