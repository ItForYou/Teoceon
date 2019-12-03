package kr.foryou.teoceon;

import java.io.File;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Paresh Mayani (@pareshmayani)
 */
public class MultiPhotoSelectActivity extends BaseActivity {
	private ImageLoader imageLoader;
	private ArrayList<ItemData> DataArr;
	private ImageAdapter imageAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private int Number = 0;
	private Uri outputFileUri;
	private String filePath;
	private ArrayList<String> FolderArr;
	private boolean isChange = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
		FolderArr = new ArrayList<String>();
		FolderArr.add("전체");


		getImage("");

		LinearLayout layout_folder = (LinearLayout) findViewById(R.id.layout_folder);
		layout_folder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MultiPhotoSelectActivity.this);
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MultiPhotoSelectActivity.this,android.R.layout.select_dialog_singlechoice);
				adapter.addAll(FolderArr);
				alertBuilder.setTitle("폴더를 선택하세요.");
				alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				alertBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String strName = adapter.getItem(id);
						getImage(strName);
					}
				});

				alertBuilder.show();
			}
		});

		LinearLayout layout_camera = (LinearLayout) findViewById(R.id.layout_camera);
		layout_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

				String folderName = "zero";
				String fileName = System.currentTimeMillis() + "";
				String path = Environment.getExternalStorageDirectory().getAbsolutePath();
				String folderPath = path + File.separator + folderName;

				filePath = path + File.separator + folderName + File.separator +  fileName + ".jpg";


				// 저장 폴더 지정 및 폴더 생성
				File fileFolderPath = new File(folderPath);
				fileFolderPath.mkdir();

				// 파일 이름 지정
				File file = new File(filePath);
				outputFileUri = FileProvider.getUriForFile(getApplicationContext(), "com.test.android.test.fileprovider", file);
				// 카메라 작동시키는 Action으로 인텐트 설정, OutputFileURI 추가
				intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
				// requestCode지정해서 인텐트 실행
				startActivityForResult(intent, 1);
			}
		});

		LinearLayout layout_back = (LinearLayout) findViewById(R.id.layout_back);
		layout_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		LinearLayout layout_done = (LinearLayout)findViewById(R.id.layout_done);
		layout_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<String> uri = new ArrayList<String>();
				ArrayList<ItemData> tempArr = imageAdapter.getItem();
				for (ItemData data : tempArr) {
					uri.add(data.imageUrl);
				}

				Intent intent = new Intent();
				intent.putStringArrayListExtra("uri", uri);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

	public void getImage(String where){
		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
		final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
		Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,orderBy + " DESC");

		Number = getIntent().getIntExtra("number", 0);
		TextView tv = (TextView) findViewById(R.id.tv_count);
		tv.setText(Number + "/8");

		this.DataArr = new ArrayList<MultiPhotoSelectActivity.ItemData>();

		for (int i = 0; i < imagecursor.getCount(); i++) {
			imagecursor.moveToPosition(i);
			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
			int dataBucket = imagecursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

			String folder = imagecursor.getString(dataBucket);

			if(!where.equals("") && !where.equals("전체")){
				if(!folder.equals(where)){
					continue;
				}
			}


			ItemData item = new ItemData(imagecursor.getString(dataColumnIndex), false, imagecursor.getString(dataBucket));


			boolean isIn = false;
			for(String str : FolderArr){
				if(str.equals(folder)){
					isIn = true;
					break;
				}
			}

			if(!isIn) FolderArr.add(folder);

			DataArr.add(item);
		}

		mLayoutManager = new GridLayoutManager(this, 3);

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(mLayoutManager);

		imageAdapter = new ImageAdapter(DataArr);
		recyclerView.setAdapter(imageAdapter);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(outputFileUri+"")));
				try {
					//Thread.sleep(100);
				} catch (Exception e) {
					// TODO: handle exception
				}

				ArrayList<String> uri = new ArrayList<String>();
				uri.add(""+filePath);

				Intent intent = new Intent();
				intent.putStringArrayListExtra("uri", uri);
				setResult(RESULT_OK, intent);
				finish();

			}
		}

	}

	@Override
	protected void onStop() {
		imageLoader.stop();
		super.onStop();
	}

	public void btnChoosePhotosClick(View v) {

		ArrayList<String> uri = new ArrayList<String>();
		ArrayList<ItemData> tempArr = imageAdapter.getItem();
		for (ItemData data : tempArr) {
			uri.add(data.imageUrl);
		}

		Intent intent = new Intent();
		intent.putStringArrayListExtra("uri", uri);
		setResult(RESULT_OK, intent);
		finish();

	}

	public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
		private ArrayList<ItemData> itemsData;
		private ArrayList<ItemData> selectData;

		public ImageAdapter(ArrayList<ItemData> itemsData) {
			this.itemsData = itemsData;
			this.selectData = new ArrayList<MultiPhotoSelectActivity.ItemData>();
		}

		// Create new views (invoked by the layout manager)
		@Override
		public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			// create a new view
			View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_multiphoto_item, null);
			ViewHolder viewHolder = new ViewHolder(itemLayoutView);
			return viewHolder;
		}

		// Replace the contents of a view (invoked by the layout manager)
		@Override
		public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

			// - get data from your itemsData at this position
			// - replace the contents of the view with that itemsData

			// viewHolder.txtViewTitle.setText(itemsData.get(position).getActive());
			// viewHolder.imgViewIcon.setImageResource(itemsData.get(position).getImageUrl());

			DisplayImageOptions options = null;

			if(isChange == true){
				options = new DisplayImageOptions.Builder()
				        .resetViewBeforeLoading(false)  // default
				        .cacheInMemory(true) // default
				        .cacheOnDisk(true) // default
				        .considerExifParams(true) // default
				        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
				        .displayer(new SimpleBitmapDisplayer()) // default
				        .handler(new Handler()) // default
				        .build();
			} else {

				options = new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.ic_launcher)
						.showImageOnFail(R.drawable.ic_travel)
				        .resetViewBeforeLoading(false)  // default
				        .cacheInMemory(true) // default
				        .cacheOnDisk(true) // default
				        .considerExifParams(true) // default
				        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
				        .displayer(new SimpleBitmapDisplayer()) // default
				        .handler(new Handler()) // default
				        .build();
			}


			imageLoader.displayImage("file://" + itemsData.get(position).getImageUrl(), viewHolder.imgViewIcon, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {

				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					view.setLayoutParams(new LinearLayout.LayoutParams(view.getWidth(), view.getWidth()));
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub

				}
			});

			viewHolder.layout.setBackgroundColor(Color.WHITE);
			if (itemsData.get(position).isActive == true) {
				viewHolder.layout.setBackgroundColor(Color.RED);
			}
			viewHolder.imgViewIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (itemsData.get(position).isActive == true) {
						int i = 0;
						for (ItemData data : itemsData) {
							if (data.isActive == true && data.count > itemsData.get(position).count) {
								data.count = data.count - 1;
								isChange = false;
								notifyItemChanged(i);
							}
							i++;
						}

						itemsData.get(position).isActive = false;
						itemsData.get(position).count = 0;
					} else {
						if (selectData.size() + Number >= 8)
							return;
						itemsData.get(position).isActive = true;
						itemsData.get(position).count = selectData.size() + 1 + Number;
					}
					itemChange();
					isChange = false;
					notifyItemChanged(position);

				}
			});



			viewHolder.tv.setVisibility(View.GONE);
			if (itemsData.get(position).count != 0) {
				viewHolder.tv.setVisibility(View.VISIBLE);
				viewHolder.tv.setText(itemsData.get(position).count + "");
			}

			isChange = true;
		}

		// inner class to hold a reference to each item of RecyclerView
		public class ViewHolder extends RecyclerView.ViewHolder {

			public LinearLayout layout;
			public ImageView imgViewIcon;
			public TextView tv;

			public ViewHolder(View itemLayoutView) {
				super(itemLayoutView);

				tv = (TextView) itemLayoutView.findViewById(R.id.item_count);
				layout = (LinearLayout) itemLayoutView.findViewById(R.id.item_layout);
				imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_iv);

			}
		}

		// Return the size of your itemsData (invoked by the layout manager)
		@Override
		public int getItemCount() {
			return itemsData.size();
		}

		private void itemChange() {
			selectData = new ArrayList<MultiPhotoSelectActivity.ItemData>();

			for (ItemData data : itemsData) {
				if (data.isActive == true) {
					selectData.add(data);
				}
			}

			if (selectData.size() > 1) {
				for (int i = 0; i < selectData.size() - 1; i++) {
					for (int j = i + 1; j < selectData.size(); j++) {
						if (selectData.get(i).count > selectData.get(j).count) {
							ItemData data = selectData.get(i);
							selectData.set(i, selectData.get(j));
							selectData.set(j, data);
						}
					}
				}
			}

			TextView tv = (TextView) findViewById(R.id.tv_count);
			tv.setText(selectData.size() + Number + "/8");
		}

		public ArrayList<ItemData> getItem() {
			return selectData;
		}

	}

	public class ItemData {

		private boolean isActive;
		private String imageUrl;
		private int count;
		private String Folder;

		public ItemData(String imageUrl, boolean isActive, String folder) {

			this.isActive = isActive;
			this.imageUrl = imageUrl;
			this.count = 0;
			this.Folder = folder;
		}

		public boolean getActive() {
			return isActive;
		}

		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

	}

}
