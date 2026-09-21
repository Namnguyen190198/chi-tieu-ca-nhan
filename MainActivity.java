package com.example.offlineexpense;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list;
    TextView balance, income, expense;
    SharedPreferences sp;
    int blue=Color.rgb(21,101,192), green=Color.rgb(46,125,50), red=Color.rgb(198,40,40);

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        sp=getSharedPreferences("data",MODE_PRIVATE);
        build();
    }

    TextView tv(String s,int size){
        TextView t=new TextView(this);
        t.setText(s); t.setTextSize(size); t.setPadding(24,18,24,18);
        return t;
    }

    void build(){
        ScrollView sv=new ScrollView(this);
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(18,18,18,18);

        TextView title=tv("QUẢN LÝ CHI TIÊU OFFLINE",24);
        title.setTextColor(blue);
        box.addView(title);

        balance=tv("",22); box.addView(balance);

        LinearLayout stats=new LinearLayout(this);
        income=tv("",16); expense=tv("",16);
        stats.addView(income,new LinearLayout.LayoutParams(0,-2,1));
        stats.addView(expense,new LinearLayout.LayoutParams(0,-2,1));
        box.addView(stats);

        Button add=new Button(this);
        add.setText("+ Thêm giao dịch");
        add.setOnClickListener(v->dialog());
        box.addView(add);

        TextView h=tv("Lịch sử giao dịch",19);
        box.addView(h);

        list=new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        box.addView(list);

        Button clear=new Button(this);
        clear.setText("Xóa toàn bộ dữ liệu");
        clear.setOnClickListener(v->
            new AlertDialog.Builder(this)
                .setTitle("Xóa dữ liệu?")
                .setMessage("Thao tác này không thể hoàn tác.")
                .setNegativeButton("Hủy",null)
                .setPositiveButton("Xóa",(d,w)->{sp.edit().clear().apply(); refresh();})
                .show()
        );
        box.addView(clear);

        sv.addView(box);
        setContentView(sv);
        refresh();
    }

    void dialog(){
        LinearLayout x=new LinearLayout(this);
        x.setOrientation(LinearLayout.VERTICAL);
        x.setPadding(30,10,30,10);

        Spinner type=new Spinner(this);
        type.setAdapter(new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item,
            new String[]{"Chi","Thu"}
        ));
        x.addView(type);

        EditText amount=new EditText(this);
        amount.setHint("Số tiền");
        amount.setInputType(2);
        x.addView(amount);

        EditText cat=new EditText(this);
        cat.setHint("Danh mục");
        x.addView(cat);

        EditText note=new EditText(this);
        note.setHint("Ghi chú");
        x.addView(note);

        new AlertDialog.Builder(this)
            .setTitle("Thêm giao dịch")
            .setView(x)
            .setNegativeButton("Hủy",null)
            .setPositiveButton("Lưu",(d,w)->{
                try {
                    long n=Long.parseLong(amount.getText().toString().trim());
                    String key=System.currentTimeMillis()+"";
                    String val=type.getSelectedItem()+"|"+n+"|"+cat.getText()+"|"+note.getText()+"|"+
                        new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(new Date());
                    sp.edit().putString(key,val).apply();
                    refresh();
                } catch(Exception e){
                    Toast.makeText(this,"Số tiền không hợp lệ",Toast.LENGTH_SHORT).show();
                }
            }).show();
    }

    void refresh(){
        if(list==null)return;
        list.removeAllViews();
        long inc=0,exp=0;
        ArrayList<String> keys=new ArrayList<>();

        for(String k:sp.getAll().keySet())
            if(k.matches("\\d+")) keys.add(k);

        Collections.sort(keys,Collections.reverseOrder());

        for(String k:keys){
            String[] a=sp.getString(k,"").split("\\|",-1);
            if(a.length<5)continue;

            long n=Long.parseLong(a[1]);
            boolean isInc=a[0].equals("Thu");
            if(isInc) inc+=n; else exp+=n;

            TextView row=tv(
                (isInc?"THU":"CHI")+"  "+
                String.format(Locale.getDefault(),"%,d đ",n)+"\n"+
                a[2]+"  "+a[3]+"\n"+a[4],15
            );
            row.setTextColor(isInc?green:red);

            row.setOnLongClickListener(v->{
                sp.edit().remove(k).apply();
                refresh();
                return true;
            });
            list.addView(row);
        }

        income.setText("Thu: "+String.format(Locale.getDefault(),"%,d đ",inc));
        expense.setText("Chi: "+String.format(Locale.getDefault(),"%,d đ",exp));
        balance.setText("Số dư: "+String.format(Locale.getDefault(),"%,d đ",inc-exp));
    }
}
