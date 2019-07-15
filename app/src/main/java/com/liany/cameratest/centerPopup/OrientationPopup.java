package com.liany.cameratest.centerPopup;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.liany.cameratest.R;
import com.lxj.xpopup.core.CenterPopupView;

/**
 * @创建者 ly
 * @创建时间 2019/7/12
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class OrientationPopup extends CenterPopupView implements View.OnClickListener {

    private Button btnConfirm;

    private RadioButton[] rbs;
    private RadioButton sn;
    private RadioButton ns;
    private RadioButton ew;
    private RadioButton we;
    private RadioButton eswn;
    private RadioButton wnes;
    private RadioButton wsen;
    private RadioButton enws;

    private AppCompatEditText etDes;

    private String orientationValue = "";

    public OrientationPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.item_pop_orientation;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);

        sn = findViewById(R.id.rb_sn);
        ns = findViewById(R.id.rb_ns);
        ew = findViewById(R.id.rb_ew);
        we = findViewById(R.id.rb_we);
        eswn = findViewById(R.id.rb_eswn);
        wnes = findViewById(R.id.rb_wnes);
        wsen = findViewById(R.id.rb_wsen);
        enws = findViewById(R.id.rb_enws);

        sn.setOnClickListener(this);
        ns.setOnClickListener(this);
        ew.setOnClickListener(this);
        we.setOnClickListener(this);
        eswn.setOnClickListener(this);
        wnes.setOnClickListener(this);
        wsen.setOnClickListener(this);
        enws.setOnClickListener(this);

        etDes = findViewById(R.id.et_des);

        rbs = new RadioButton[]{sn,ns,ew,we,eswn,wnes,wsen,enws};
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
//                hideInput();
                Log.e("123",orientationValue + "/" + etDes.getText().toString());
                dismiss();
                break;
            case R.id.rb_sn:
                changeClick(sn);
                orientationValue = sn.getText().toString();
                break;
            case R.id.rb_ns:
                changeClick(ns);
                orientationValue = ns.getText().toString();
                break;
            case R.id.rb_ew:
                changeClick(ew);
                orientationValue = ew.getText().toString();
                break;
            case R.id.rb_we:
                changeClick(we);
                orientationValue = we.getText().toString();
                break;
            case R.id.rb_eswn:
                changeClick(eswn);
                orientationValue = eswn.getText().toString();
                break;
            case R.id.rb_wnes:
                changeClick(wnes);
                orientationValue = wnes.getText().toString();
                break;
            case R.id.rb_wsen:
                changeClick(wsen);
                orientationValue = wsen.getText().toString();
                break;
            case R.id.rb_enws:
                changeClick(enws);
                orientationValue = enws.getText().toString();
                break;
        }
    }

    private void changeClick(RadioButton rb) {
        for(int i = 0; i < rbs.length; i++) {
            if(rb.getId() == rbs[i].getId()) {
                rbs[i].setChecked(true);
            }else {
                rbs[i].setChecked(false);
            }
        }
    }

//    /**
//     * 隐藏键盘
//     */
//    protected void hideInput() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        View v = getWindow().peekDecorView();
//        if (null != v) {
//            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        }
//    }
}
