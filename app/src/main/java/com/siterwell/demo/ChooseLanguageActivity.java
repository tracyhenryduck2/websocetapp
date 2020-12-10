package com.siterwell.demo;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.common.UnitTools;


/**
 * Created by vsuriyal on 8/8/18.
 */

public class ChooseLanguageActivity  extends TopbarSuperActivity implements View.OnClickListener {
	private ImageView chineseTickView;
	private ImageView englishTickView;
	private ImageView dutchTickView;
	private ImageView franceTickView;
	private LinearLayout chineseContainer;
	private LinearLayout englishCountainer;
	private LinearLayout dutchContainer;
	private LinearLayout franceContainer;
	private ImageView spanishTickView;
	private LinearLayout spanishContainer;

	@Override
	protected void onCreateInit() {
		initializeView();
		initData();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_change_language;
	}

	private void initializeView() {
		chineseTickView = (ImageView) findViewById(R.id.chinese_tick);
		englishTickView = (ImageView) findViewById(R.id.english_tick);
		dutchTickView = (ImageView) findViewById(R.id.dutch_tick);
		franceTickView = (ImageView) findViewById(R.id.france_tick);
		chineseContainer = (LinearLayout) findViewById(R.id.chinese_container);
		chineseContainer.setOnClickListener(this);
		englishCountainer = (LinearLayout) findViewById(R.id.english_container);
		englishCountainer.setOnClickListener(this);
		dutchContainer = (LinearLayout) findViewById(R.id.dutch_container);
		dutchContainer.setOnClickListener(this);
		franceContainer = (LinearLayout) findViewById(R.id.france_container);
		franceContainer.setOnClickListener(this);
		spanishContainer = (LinearLayout) findViewById(R.id.spanish_container);
		spanishContainer.setOnClickListener(this);
		spanishTickView = (ImageView) findViewById(R.id.spanish_tick);

		getTopBarView().setTopBarStatus(R.drawable.back, null, getResources().getString(R.string.language), 1, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(ChooseLanguageActivity.this, LoginActivity.class));
			}
		}, null, R.color.bar_bg);
	}



	private void initData() {
		UnitTools tools = new UnitTools(this);
		String lan = tools.readLanguage();

		if("zh".equals(lan)){
			makeChineseTick();
		}
		else if("en".equals(lan)) {
			makeEnglishTick();
		}
		else if("de".equals(lan)) {
			makeDutchTick();
		}
		else if("fr".equals(lan)) {
			makeFranceTick();
		}
		else if("es".equals(lan)) {
			makeSpanishTick();
		}

	}

	@Override
	public void onClick(View view) {
		UnitTools ut = new UnitTools(this);
		switch (view.getId()){
		case R.id.chinese_container:
			makeChineseTick();
			ut.shiftLanguage(this,"zh");
			break;
		case R.id.english_container:
			makeEnglishTick();
			ut.shiftLanguage(this,"en");
			break;
		case R.id.dutch_container:
			makeDutchTick();
			ut.shiftLanguage(this,"de");
			break;
		case R.id.france_container:
			makeFranceTick();
			ut.shiftLanguage(this,"fr");
			break;
		case R.id.spanish_container:
			makeSpanishTick();
			ut.shiftLanguage(this,"es");
			break;
		default:
			break;
		}
	}

	private void makeChineseTick() {
		chineseTickView.setVisibility(View.VISIBLE);
		englishTickView.setVisibility(View.INVISIBLE);
		dutchTickView.setVisibility(View.INVISIBLE);
		franceTickView.setVisibility(View.INVISIBLE);
		spanishTickView.setVisibility(View.INVISIBLE);
	}

	private void makeEnglishTick() {
		chineseTickView.setVisibility(View.INVISIBLE);
		englishTickView.setVisibility(View.VISIBLE);
		dutchTickView.setVisibility(View.INVISIBLE);
		franceTickView.setVisibility(View.INVISIBLE);
		spanishTickView.setVisibility(View.INVISIBLE);
	}
	private void makeDutchTick() {
		chineseTickView.setVisibility(View.INVISIBLE);
		englishTickView.setVisibility(View.INVISIBLE);
		dutchTickView.setVisibility(View.VISIBLE);
		franceTickView.setVisibility(View.INVISIBLE);
		spanishTickView.setVisibility(View.INVISIBLE);
	}
	private void makeFranceTick() {
		chineseTickView.setVisibility(View.INVISIBLE);
		englishTickView.setVisibility(View.INVISIBLE);
		dutchTickView.setVisibility(View.INVISIBLE);
		franceTickView.setVisibility(View.VISIBLE);
		spanishTickView.setVisibility(View.INVISIBLE);
	}
	private void makeSpanishTick() {
		chineseTickView.setVisibility(View.INVISIBLE);
		englishTickView.setVisibility(View.INVISIBLE);
		dutchTickView.setVisibility(View.INVISIBLE);
		franceTickView.setVisibility(View.INVISIBLE);
		spanishTickView.setVisibility(View.VISIBLE);
	}
}
