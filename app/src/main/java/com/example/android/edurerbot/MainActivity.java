package com.example.android.edurerbot;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

/*import com.example.android.edurerbot.ChatAdapter;
import com.example.android.edurerbot.ChatMessage;
import com.example.android.edurerbot.R;
*/
import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
//import org.alicebot.ab.Graphmaster;
//import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
//import org.alicebot.ab.Timer;

import java.io.File;
//import java.io.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {


    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    public Bot bot;
    public static Chat chat;
    private ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (Button) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                //bot
                String response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                if(response.endsWith("</search></oob>")){
                    int start=response.indexOf("<oob><search>")+13;
                    int end=response.indexOf("</search></oob>");
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    String term = response.substring(start,end);
                    // term which you want to search for
                    response=response.substring(0,start-13);
                    intent.putExtra(SearchManager.QUERY, term);
                    startActivity(intent);
                }
                mimicOtherMessage(response);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
        //checking SD card availablility
        //  boolean a = isSDCARDAvailable();
        //receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();

        File myDir = this.getFilesDir();
        // Documents Path
        String documents = "documents/data";
        File jayDir = new File(myDir.toString());
        // jayDir.mkdirs(); // this line creates data folder at documents directory
        //jayDir.mkdirs();
        //File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/hari/bots/Hari");
        boolean a = jayDir.mkdirs();
        Log.d("directory", jayDir.getPath());

        Log.d("bool", valueOf(a));

            //Reading the file
            try {
                for (String dir : assets.list("Hari")) {
                    File subdir = new File(jayDir.getPath() + "/bots/Hari/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    for (String file : assets.list("Hari/" + dir)) {
                        File f = new File(jayDir.getPath() + "/bots/Hari/" + dir + "/" + file);
                       // Log.d("directoryin", jayDir.getPath() + "/bots/Hari/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("Hari/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath().toString() + "/bots/Hari/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }

            //get the working directory
            MagicStrings.root_path = jayDir.getPath().toString();
          //  System.out.println("Working Directory = " + MagicStrings.root_path);
            AIMLProcessor.extension = new PCAIMLProcessorExtension();
            //Assign the AIML files to bot for processing
            bot = new Bot("Hari", MagicStrings.root_path, "chat");
            chat = new Chat(bot);
            String[] args = null;
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
         //   mainFunction(args);

        }



    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }
    //check SD card availability
    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true :false;
    }
    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    //Request and response of user and the bot
  /*   public static void mainFunction (String[] args) {
       MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: "+request);
        System.out.println("Robot: " + response);
    }*/

}