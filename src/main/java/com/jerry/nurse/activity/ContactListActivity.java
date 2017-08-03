package com.jerry.nurse.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.OnItemClickListener;
import com.jerry.nurse.util.ViewHolder;
import com.jerry.nurse.view.RecycleViewDivider;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

import static com.jerry.nurse.constant.ServiceConstant.AVATAR_ADDRESS;

public class ContactListActivity extends BaseActivity {

    @Bind(R.id.tb_contact_list)
    TitleBar mTitleBar;

    @Bind(R.id.rv_contact_list)
    RecyclerView mRecyclerView;

    private UserHospitalInfo mUserHospitalInfo;

    private ProgressDialog mProgressDialog;

    private List<Contact> mContacts;

    private ContactAdapter mAdapter;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ContactListActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_contact_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        // 初始化等待框
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("请稍后...");

        mUserHospitalInfo = DataSupport.findFirst(UserHospitalInfo.class);

        if (mUserHospitalInfo.getDepartmentName() != null) {
            mTitleBar.setTitle(mUserHospitalInfo.getDepartmentName());
        }
        String hospitalId = mUserHospitalInfo.getHospitalId();
        String officeId = mUserHospitalInfo.getDepartmentId();
        if (hospitalId != null && officeId != null) {
            getContactByOffice(hospitalId, officeId);
        }
    }

    /**
     * 获取科室内所有联系人
     */
    private void getContactByOffice(String hospitalId, String officeId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_CONTACT_LIST_BY_OFFICE_ID)
                .addParams("HospitalId", hospitalId)
                .addParams("DepartmentId", officeId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        try {
                            mContacts = new Gson().fromJson(response,
                                    new TypeToken<List<Contact>>() {
                                    }.getType());
                            setListData();
                        } catch (JsonSyntaxException e) {
                            L.i("获取科室内联系人信息失败");
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setListData() {
        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));

        mAdapter = new ContactAdapter(this, R.layout.item_contact, mContacts);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                Intent intent = ChatActivity.getIntent(ContactListActivity.this);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
    }

    class ContactAdapter extends CommonAdapter<Contact> {
        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final Contact contact) {
            holder.setText(R.id.tv_nickname, contact.getNickName());
            ImageView imageView = holder.getView(R.id.iv_avatar);
            if (contact.getAvatar().startsWith("http")) {
                Glide.with(ContactListActivity.this).load(contact.getAvatar()).into(imageView);
            } else {
                Glide.with(ContactListActivity.this).load(AVATAR_ADDRESS + contact.getAvatar()).into(imageView);
            }
        }
    }
}