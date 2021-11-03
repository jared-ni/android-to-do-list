package com.milkteadj.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.listView);
        final TextAdapter adapter = new TextAdapter();

        //read file info of saved list
        readInfo();

        //every time set data of the new list to the list
        adapter.setData(list);
        listView.setAdapter(adapter);

        //when clicking on a task and a menu pop up to delete or undo
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //when clicked
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                //dialog menu, delete or no?
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete this task?")
                        //if "yes, delete" with positive button, remove the item from the list and update data
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.remove(position);
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        //if "no", then exit
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
            }
        });

        //new task button
        final Button newTaskButtom = findViewById(R.id.newTaskButtom);

        //when clicking on "New Task" button
        newTaskButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //editText box for user input
                final EditText taskInput = new EditText(MainActivity.this);
                taskInput.setSingleLine();

                //dialog pops up with title, message question, positive and negative buttons, and displayed editText box
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add a New Task")
                        .setMessage("What is your new task?")
                        .setView(taskInput)
                        .setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.add(taskInput.getText().toString());
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        //Clear all button
        final Button deleteAllTasksButton = findViewById(R.id.DeleteAllTasksButton);

        //Delete all tasks and pop up window for confirmation
        deleteAllTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Clear all tasks?")
                        .setMessage("All tasks will be deleted.")
                        .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.clear();
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });
    }


    //save list into file
    private void saveInfo() {
        try {
            File file = new File(this.getFilesDir(), "saved");

            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            //writes in every line on the list to saved file
            for(int i = 0; i < list.size(); i++) {
                bw.write(list.get(i));
                bw.newLine();
            }

            bw.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //read the file saved
    private void readInfo() {
        File file = new File(this.getFilesDir(), "saved");
        //if no saved file, return
        if(!file.exists()) {
            return;
        }

        try {
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            //reader reads the file lines and add them to list until there's no line left to read.
            while(line!=null) {
                list.add(line);
                line = reader.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    class TextAdapter extends BaseAdapter {

        List<String> list = new ArrayList<>();

        //clear the previous data and adds the new data and tells the adapter
        void setData(List<String> mList) {
            list.clear();
            list.addAll(mList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        //convertView is 10x more efficient than rawView because it is only creating 10% of the time
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater)
                        MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item, parent, false);
            }

            final TextView textView = convertView.findViewById(R.id.task);
            textView.setText(list.get(position));
            return convertView;
        }
    }
}