package ru.justd.redmadrobottest;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * В программе при запуске есть одно поле для ввода и кнопка «Давай коллаж». Вводим ник пользователя из инстаграма, 
 * приложение скачивает лучшие фотографии пользователя, 
 * склеивает их в коллаж, показывает превью с кнопкой «Печать», которая отправляет коллаж на принтер.
 * Более простой вариант — печать одной лучшей фотографии.
 * Более сложный вариант — сделать фото-пикер, позволяющий выбрать из лучших фотографий более лучшие и сделать коллаж из них.
 * Язык — Java.
 * Приложение должно работать на Android 2.2 и выше.
 */


public class MainActivity extends Activity {
	private EditText nicknameEditText;
	private GridView gridView;
	private ImageView testIV;
	private List<MyItem> myItemsList = new ArrayList<MyItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nicknameEditText = (EditText) findViewById(R.id.et_nickname);

		gridView = (GridView) findViewById(R.id.gridview);

		testIV = (ImageView) findViewById(R.id.test_iv);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_reload: {
			reload();
		}
			break;
		case R.id.action_print: {
			List<MyItem> selectedItemsList = findSelectedPhotos();
			createPrinterDialod(selectedItemsList);

		}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}



	private List<MyItem> findSelectedPhotos() {
		List<MyItem> selectedItemsList = new ArrayList<MyItem>();
//		System.out.println("www");
		int i=0;
		for(MyItem item: myItemsList){
			if (item.getImageView()!= null && !item.getImageView().isEnabled()){
				selectedItemsList.add(item);
			}
			System.out.println(i++);
		}
		System.out.println("fi "+(selectedItemsList== null?0:selectedItemsList.size()));
		return selectedItemsList;
		
	}

	private void createPrinterDialod(final List<MyItem> selectedItemsList) {
		int photosCount = selectedItemsList == null ? 0 : selectedItemsList.size();
		if (photosCount != 0){
		Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View view = inflater.inflate(R.layout.print_dialog, null);

		final ImageView imageView = (ImageView) view.findViewById(R.id.iv_printing);
		final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_printing);
		final TextView textView = (TextView) view.findViewById(R.id.tv_printing);
		textView.setText("Print "+photosCount+" photos?");
		
		builder.setTitle("Print photos");
		builder.setView(view)
				// Add action buttons
				.setPositiveButton(R.string.action_print, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
							}
						})
				.setNegativeButton(R.string.str_cancel,	new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});

		final AlertDialog alert = builder.create();
		alert.setOnShowListener(new DialogInterface.OnShowListener() {

		    @Override
		    public void onShow(DialogInterface dialog) {

		        Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
		        b.setOnClickListener(new View.OnClickListener() {

		            @Override
		            public void onClick(View view) {
		            	print(imageView, textView, progressBar, selectedItemsList, alert);
		            }
		        });
		    }
		});
		alert.show();
		} else toast("No photos selected ");
	}

	
	private void print(final ImageView imageView, final TextView textView, final ProgressBar progressBar, final List<MyItem> selectedItemsList, final AlertDialog dialog) {
		final int count = selectedItemsList.size();
		progressBar.setMax(count);
		synchronized (imageView) {

			new Thread(new Runnable() {
				
				@Override
				public void run() {
					for (int i = 0; i<count; i++){
						final int j = i+1;
						
						try {
							 Thread.sleep(1000);
							 
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								textView.setText("Printing "+j+" of "+ count);
								for (String url: ImageLoader.cache.keySet())
									if(url == selectedItemsList.get(j-1).getImageUrl()){
										imageView.setImageDrawable(ImageLoader.cache.get(url));
									}
								progressBar.setProgress(j);
								if (j == count) {
									finishPrintJob();
								}
								
							}

							private void finishPrintJob() {
								dialog.dismiss();
								toast("Print work sucsessfull");
								for (MyItem item:selectedItemsList){
									item.getImageView().setEnabled(false);
								}
								
							}
						});

					}
				}
			}).start();
		}
	}
	
	private void reload() {
		myItemsList.clear();
		ImageLoader.cache.clear();
		loadData();

	}

	public void onStartCollageBtnClick(View v) {
		hideKeyboard();
		loadData();
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(nicknameEditText.getWindowToken(), 0);

	}

	public void loadData() {
		if (Utils.isNetworkAvailable(MainActivity.this)) {
			new MyTask(nicknameEditText.getText().toString(), this, gridView,
					testIV, myItemsList).execute();
		} else {
			String msg = "No Network Connection!!!";
			toast(msg);
			this.finish();
		}
	}

	public void toast(String msg) {
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
	}

}
