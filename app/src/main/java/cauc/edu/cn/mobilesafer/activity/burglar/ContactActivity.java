package cauc.edu.cn.mobilesafer.activity.burglar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cauc.edu.cn.mobilesafer.util.ToastUtil;
import cauc.edu.cn.mobilesafer.R;

/**
* FileName: ContactActivity <br>
* Description: 从系统通讯录读取联系人列表并设置安全联系人的活动 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/18 9:48
*/
public class ContactActivity extends AppCompatActivity {
    private static final String tag = "ContactActivity";
    // 显示电话联系人的列表
    private ListView mListView;
    // 装载联系人姓名和电话号码所组成的hashmap的list
    private List<HashMap<String, String>> mContactList = new ArrayList<>();
    private ContactAdapter mContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        // 初始化布局文件中View控件
        initView();
        // 初始化ListView中用到的数据
        initData();
    }

    /**
     * 读取系统通讯录过程采用了子线程，此处采用异步消息处理机制，将UI更新放回主线程处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mContactAdapter = new ContactAdapter();
            mListView.setAdapter(mContactAdapter);
        }
    };

    /**
     * 初始化ListView中用到的数据
     */
    private void initData() {
        // 查询数据是一个费时操作，开启子线程进行查询
        new Thread() {
            @Override
            public void run() {
                //读取系统通讯录属于危险权限，需要进行动态权限申请
                if (ContextCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    //如果用户尚未授权此权限，则用requestPermissions函数向用户提交授权申请
                    ActivityCompat.requestPermissions(ContactActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                } else {
                    //用户已授权过，直接获取系统联系人
                    readContacts();
                }
            }
        }.start();
    }

    /**
     * 向用户提交动态授权申请后，自动对用户做出的授权决定做出应答
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //若用户同意将READ_CONTACTS权限授权给用户，则读取本机系统联系人
                    readContacts();
                }else{
                    ToastUtil.show(this,"你拒绝了授权！");
                }
                break;
            default:
        }
    }

    /**
     * 读取手机系统联系人列表
     */
    private  void readContacts(){
        // 获取内容解析器，并进行查询
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                new String[]{"contact_id"}, null, null, null);
        mContactList.clear();
        // 循环遍历查询结果，直至结束
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            Log.i(tag, "id = " + id);
            Cursor contactCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
                    new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{id}, null);
            // 每循环遍历一次，就把相应的数据添加到新的HashMap对象中
            HashMap<String, String> hashMap = new HashMap<String, String>();
            while (contactCursor.moveToNext()) {
                String data = contactCursor.getString(0);
                String type = contactCursor.getString(1);
                if (type.equals("vnd.android.cursor.item/phone_v2")) {
                    if (!TextUtils.isEmpty(data)) {
                        hashMap.put("phone", data);
                    }
                } else if (type.equals("vnd.android.cursor.item/name")) {
                    if (!TextUtils.isEmpty(data)) {
                        hashMap.put("name", data);
                    }
                }
                Log.i(tag, "data = " + data);
                Log.i(tag, "type = " + type);
            }
            contactCursor.close();
            mContactList.add(hashMap);
        }
        cursor.close();
        // 发送默认的消息
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 初始化布局文件中View控件
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.contacts_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mContactAdapter != null) {
                    // 获取被点击项Item的电话号码值，并返回给打开此Activity的活动
                    HashMap<String, String> hashMap = mContactAdapter.getItem(position);
                    String phone = hashMap.get("phone");
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);
                    // 关闭此活动
                    finish();
                }
            }
        });
    }

    class ContactAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mContactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mContactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.contact_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.contactListName = (TextView) view.findViewById(R.id.contact_list_name);
                viewHolder.contactListPhone = (TextView) view.findViewById(R.id.contact_list_phone);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.contactListName.setText(getItem(position).get("name"));
            viewHolder.contactListPhone.setText(getItem(position).get("phone"));
            return view;
        }

        class ViewHolder {
            TextView contactListName;
            TextView contactListPhone;
        }
    }
}
