package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.model.UserPractisingCertificateInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;


public class PractisingCertificateEditActivity extends BaseActivity {

    @Bind(R.id.rl_birthday)
    RelativeLayout mBirthdayLayout;

    @Bind(R.id.rl_first_sign_date)
    RelativeLayout mFirstSignDateLayout;

    @Bind(R.id.rl_last_sign_date)
    RelativeLayout mLastSignDateLayout;

    @Bind(R.id.rl_last_sign_date_valid)
    RelativeLayout mLastSignDateValidLayout;

    @Bind(R.id.rl_certificate_date)
    RelativeLayout mCertificateDateLayout;

    @Bind(R.id.rl_first_work_date)
    RelativeLayout mFirstWordDateLayout;

    @Bind(R.id.et_name)
    EditText mNameEditText;

    @Bind(R.id.tv_birthday)
    TextView mBirthdayTextView;

    @Bind(R.id.et_country_nation)
    EditText mCountryNationEditText;

    @Bind(R.id.et_practising_location)
    EditText mPractisingLocationEditText;

    @Bind(R.id.et_certificate_number)
    EditText mCertificateNumberEditText;

    @Bind(R.id.et_id_card_number)
    EditText mIdCardNumberEditText;

    @Bind(R.id.tv_first_sign_date)
    TextView mFirstSignDateTextView;

    @Bind(R.id.tv_last_sign_date)
    TextView mLastSignDateTextView;

    @Bind(R.id.tv_last_sign_date_valid)
    TextView mLastSignDateValidTextView;

    @Bind(R.id.et_sign_department)
    EditText mSignDepartmentTextView;

    @Bind(R.id.et_certificate_organization)
    EditText mCertificateOrganizationTextView;

    @Bind(R.id.tv_certificate_date)
    TextView mCertificateDateTextView;

    @Bind(R.id.tv_first_work_date)
    TextView mFirstWorkDateTextView;

    private UserPractisingCertificateInfo mInfo;

    private String mErrorMessage;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PractisingCertificateEditActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_practising_certificate_edit;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setDateSelectListener(mBirthdayLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mBirthdayTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mFirstSignDateLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mFirstSignDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mLastSignDateLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mLastSignDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mLastSignDateValidLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mLastSignDateValidTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mCertificateDateLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mCertificateDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mFirstWordDateLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mFirstWorkDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });

        mInfo = DataSupport.findFirst(UserPractisingCertificateInfo.class);

        if (mInfo != null) {
            mNameEditText.setText(mInfo.getName());
            mBirthdayTextView.setText(mInfo.getBirthDate());
            mCountryNationEditText.setText(mInfo.getCountry());
            mPractisingLocationEditText.setText(mInfo.getPracticeAddress());
            mCertificateNumberEditText.setText(mInfo.getCertificateId());
            mIdCardNumberEditText.setText(mInfo.getIDCard());
            mFirstSignDateTextView.setText(mInfo.getFirstRegisterDate());
            mLastSignDateTextView.setText(mInfo.getLastRegisterDate());
            mLastSignDateValidTextView.setText(mInfo.getRegisterToDate());
            mSignDepartmentTextView.setText(mInfo.getRegisterAuthority());
            mCertificateOrganizationTextView.setText(mInfo.getCertificateAuthority());
            mCertificateDateTextView.setText(mInfo.getCertificateDate());
            mFirstWorkDateTextView.setText(mInfo.getFirstJobTime());
        }
    }

    @OnClick(R.id.acb_submit)
    void onSubmit(View view) {
        if (!validate()) {
            if (!TextUtils.isEmpty(mErrorMessage)) {
                T.showShort(this, mErrorMessage);
            }
            return;
        }

        // 组装数据
        mInfo.setBirthDate(mNameEditText.getText().toString());
        mInfo.setCountry(mBirthdayTextView.getText().toString());
        mInfo.setPracticeAddress(mCountryNationEditText.getText().toString());
        mInfo.setCertificateId(mPractisingLocationEditText.getText().toString());
        mInfo.setIDCard(mCertificateNumberEditText.getText().toString());
        mInfo.setFirstRegisterDate(mIdCardNumberEditText.getText().toString());
        mInfo.setLastRegisterDate(mFirstSignDateTextView.getText().toString());
        mInfo.setRegisterToDate(mLastSignDateTextView.getText().toString());
        mInfo.setRegisterAuthority(mLastSignDateValidTextView.getText().toString());
        mInfo.setCertificateAuthority(mSignDepartmentTextView.getText().toString());
        mInfo.setCertificateDate(mCertificateOrganizationTextView.getText().toString());
        mInfo.setBirthDate(mCertificateDateTextView.getText().toString());
        mInfo.setBirthDate(mFirstWorkDateTextView.getText().toString());

        postProfessionalCertificate();
    }

    private boolean validate() {
        if (TextUtils.isEmpty(mNameEditText.getText().toString())) {
            mErrorMessage = "姓名为空！";
            return false;
        }
        if (TextUtils.isEmpty(mBirthdayTextView.getText().toString())) {
            mErrorMessage = "生日为空！";
            return false;
        }
        if (TextUtils.isEmpty(mCountryNationEditText.getText().toString())) {
            mErrorMessage = "国家为空！";
            return false;
        }
//        if (TextUtils.isEmpty(mSex.getText().toString())) {
//            mErrorMessage = "编号为空！";
//        return false;
//        }
        if (TextUtils.isEmpty(mPractisingLocationEditText.getText().toString())) {
            mErrorMessage = "执业地点为空！";
            return false;
        }
        if (TextUtils.isEmpty(mCertificateNumberEditText.getText().toString())) {
            mErrorMessage = "执业编号为空！";
            return false;
        }
        if (TextUtils.isEmpty(mIdCardNumberEditText.getText().toString())) {
            mErrorMessage = "身份证为空！";
            return false;
        }
        if (TextUtils.isEmpty(mFirstSignDateTextView.getText().toString())) {
            mErrorMessage = "首次注册日期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mLastSignDateTextView.getText().toString())) {
            mErrorMessage = "最近注册日期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mLastSignDateValidTextView.getText().toString())) {
            mErrorMessage = "最近一次注册有效期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mSignDepartmentTextView.getText().toString())) {
            mErrorMessage = "注册机关为空！";
            return false;
        }
        if (TextUtils.isEmpty(mCertificateOrganizationTextView.getText().toString())) {
            mErrorMessage = "发证机关为空！";
            return false;
        }

        if (TextUtils.isEmpty(mCertificateDateTextView.getText().toString())) {
            mErrorMessage = "发证日期为空！";
            return false;
        }

        if (TextUtils.isEmpty(mFirstWorkDateTextView.getText().toString())) {
            mErrorMessage = "首次工作日期为空！";
            return false;
        }

        return true;
    }

    private void postProfessionalCertificate() {
        mProgressDialog.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_PRACTISING_CERTIFICATE_INFO)
                .content(StringUtil.addModelWithJson(mInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.equals(REQUEST_SUCCESS)) {
                            L.i("设置执业证成功");
                            // 设置成功后更新数据库
                            UserUtil.savePractisingCertificateInfo(mInfo);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            L.i("设置执业证失败");
                        }
                    }
                });
    }
}
